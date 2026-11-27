/*
 * Copyright (C) 2013-2026 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.b3dgs.lionengine.ecs;

import com.b3dgs.lionengine.RandomSource;

/**
 * The container of the objects of a game or of a scene, of their traits and of the systems that run them
 * (REQ-WORLD-001, REQ-ECS-001 to REQ-ECS-008, REQ-ECS-010 to REQ-ECS-013, REQ-DET-001, REQ-DET-003, ADR-0011,
 * ADR-0018, ADR-0021, ADR-0024, ADR-0030, ADR-0031, ADR-0034, ADR-0074 to ADR-0078).
 *
 * <p>
 * <b>Object IDs.</b> An object is an opaque primitive {@code long}: a generation in the high 32 bits, an index in the
 * low 32 bits. Generations start at 1 and stay positive, so every ID is positive and {@link #NO_OBJECT} ({@code 0})
 * never names an object. An index is reused only after its object is removed, with the next generation, so an ID kept
 * after the removal of its object is <em>stale</em> and is detected. The oldest free index is reused first; an index
 * that reached the highest generation ({@code 2^31 - 1}) is never reused. The same {@code long} is the camera follow
 * key (ADR-0029). Do not decode an ID: its layout is for the engine.
 * </p>
 *
 * <p>
 * <b>Traits.</b> A trait is data attached to an object, at most one per trait type. Each trait type is registered
 * explicitly, once per world, with {@link #register(TraitType)}, which gives it an {@code int} type ID (0, then
 * consecutive, in registration order) and returns its {@link TraitStorage}; registering the same type twice fails.
 * Adding and removing traits goes through the storage and is deferred like the object changes below; removing an
 * object removes all its traits. Trait instances are pooled and reset on removal.
 * </p>
 *
 * <p>
 * <b>Queries.</b> {@link #createQuery(TraitStorage[], TraitStorage[])} gives a {@link Query}: the cached list of the
 * objects having all of an include set of trait types and none of an exclude set, updated by each flush without
 * allocating, iterated without allocating.
 * </p>
 *
 * <p>
 * <b>Deferred changes.</b> {@link #create()} returns the ID of a new object at once, so further commands can be
 * recorded with it, but the object is <em>pending</em>: {@link #isAlive(long)} returns {@code false} until the next
 * {@link #flush()}, and accessors fail fast. {@link #remove(long)} records the removal of a pending or alive object,
 * with all its traits: the object stays alive until the flush. The flush applies the recorded commands in recorded
 * order, object and trait commands together: a creation and a removal recorded in the same step both apply, as do a
 * trait addition and its removal. A removal of an object already removed by an earlier command of the same flush
 * changes nothing. In the game loop there is one flush per step, at the end, after
 * every system and before rendering, so changes are visible from the next step; an explicit flush is allowed outside
 * the loop (setup, tests) and between two world updates (snapshot application, ADR-0038).
 * </p>
 *
 * <p>
 * <b>Systems and steps.</b> {@link #addSystem(GameSystem, int)} registers a {@link GameSystem} with an {@code int}
 * slot. {@link #update(double)} is one step: it runs the enabled systems from the lowest slot to the highest,
 * systems of equal slots in registration order, with the constant step, then flushes once, so the changes the systems
 * record are visible from the next step. Rendering only reads the world between two updates; commands recorded
 * outside an update, from the inbox for example, join the log and apply at the flush that ends the next update.
 * </p>
 *
 * <p>
 * <b>Events.</b> {@link ObjectListener}s and {@link TraitListener}s receive, synchronously during the flush and in its
 * order, every effective change: object created and destroyed, trait added and removed. A command that changes nothing
 * fires nothing. A listener must not change the structure of the world: during the flush, creating or removing an
 * object, adding or removing a trait, flushing, updating, registering a type, creating a query, reserving capacity and
 * adding or removing a system throw {@link IllegalStateException}.
 * </p>
 *
 * <p>
 * <b>Random source.</b> Each world owns one {@link RandomSource}, created at construction from
 * {@link WorldConfig#randomSeed()} and never shared with another world: {@link #getRandomSource()} gives it to the
 * systems, which draw the numbers of the simulation from it, never from a global source (ADR-0024, ADR-0031). Two
 * worlds with the same seed and the same sequence of operations and draws give the same numbers on every platform.
 * Listeners may draw too: they run in flush order, so their draws are part of the same deterministic sequence.
 * Rendering and presentation effects do not draw from it, since their frame count is not the step count; they own
 * their generators (camera shake, audio variants). {@link #reseed(long)} restarts the sequence between two updates.
 * Drawing allocates nothing.
 * </p>
 *
 * <p>
 * <b>Failure.</b> An exception thrown by a system or a listener propagates out of {@link #update(double)} or
 * {@link #flush()}; the world is undefined afterwards (ADR-0030): the rest of the step is not run, and every later
 * structural change, flush and update throws {@link IllegalStateException}. The loop fails (REQ-LOOP-010); the game
 * discards the world.
 * </p>
 *
 * <p>
 * <b>Capacity.</b> The world holds {@link WorldConfig#objectCapacity()} objects without growing. Creating more is
 * allowed: the table doubles at the next flush until every created object fits, a documented warm-up cost, and never
 * shrinks; {@link #reserve(int)} grows it ahead. The log of the recorded commands starts with the same capacity and
 * doubles when a step records more commands. Each trait storage holds {@link WorldConfig#traitCapacity()} traits
 * the same way (see {@link TraitStorage}). Once the capacities fit the busiest step, {@link #create()},
 * {@link #remove(long)}, the trait additions and removals and {@link #flush()} allocate nothing. The world never
 * holds more than {@link WorldConfig#MAX_OBJECT_CAPACITY} indices at once.
 * </p>
 *
 * <p>
 * No singleton: several worlds coexist, each independent of the others; an ID is meaningful in the world that created
 * it only. A world has no dependency on rendering, resources, audio, camera or tile maps. The object definitions are
 * added to the world by a later batch of the feature page.
 * </p>
 *
 * <p>
 * Not thread-safe: created and used on the runtime thread only (ADR-0018). Events of other threads reach it through the
 * inbox, whose delivery runs on that thread.
 * </p>
 *
 * @since 11.0.0
 */
public final class World
{
    /** The ID that never names an object: {@code 0}, "no object". */
    public static final long NO_OBJECT = 0L;

    /** The message of a stale or never-valid ID. */
    private static final String ERROR_INVALID = "Stale or invalid object ID: ";
    /** Initial length of the array of trait storages. */
    private static final int INITIAL_TRAIT_TYPES = 16;
    /** Initial length of the array of queries. */
    private static final int INITIAL_QUERIES = 8;
    /** The empty exclude set. */
    private static final TraitStorage<?>[] NO_TYPES = new TraitStorage<?>[0];
    /** The phase outside an update and a flush. */
    private static final int IDLE = 0;
    /** The phase of the systems of an update. */
    private static final int UPDATING = 1;
    /** The phase of a flush, the listener calls included. */
    private static final int FLUSHING = 2;
    /** The phase after an exception of a system or a listener. */
    private static final int FAILED = 3;

    /** The objects. */
    private final ObjectTable objects;
    /** The commands recorded since the last flush. */
    private final StructuralCommands commands;
    /** The trait masks of the objects. */
    private final TraitMasks masks;
    /** The trait capacity of a new storage. */
    private final int traitCapacity;
    /** The trait storages, by type ID. */
    private TraitStorage<?>[] storages;
    /** The number of registered trait types. */
    private int typeCount;
    /** The queries, in creation order. */
    private Query[] queries;
    /** The number of queries. */
    private int queryCount;
    /** The listeners of the effective changes of the flush. */
    private final WorldListeners listeners = new WorldListeners();
    /** The systems. */
    private final SystemSchedule systems = new SystemSchedule();
    /** The current phase: {@link #IDLE}, {@link #UPDATING}, {@link #FLUSHING} or {@link #FAILED}. */
    private int phase = IDLE;
    /** The random source of the simulation, owned by this world only. */
    private final RandomSource random;

    /**
     * Creates a world with the default configuration, {@link WorldConfig#DEFAULT}.
     */
    public World()
    {
        this(WorldConfig.DEFAULT);
    }

    /**
     * Creates a world.
     *
     * @param config The configuration.
     * @throws IllegalArgumentException If the configuration is {@code null}.
     */
    public World(WorldConfig config)
    {
        this(config, ObjectTable.MAX_GENERATION);
    }

    /**
     * Creates a world with a lower highest generation, for the tests of index retirement.
     *
     * @param config The configuration.
     * @param maxGeneration The highest generation, at least {@link ObjectTable#FIRST_GENERATION}.
     * @throws IllegalArgumentException If the configuration is {@code null}.
     */
    World(WorldConfig config, int maxGeneration)
    {
        if (config == null)
        {
            throw new IllegalArgumentException("World configuration is null");
        }
        objects = new ObjectTable(config.objectCapacity(), maxGeneration);
        commands = new StructuralCommands(config.objectCapacity());
        masks = new TraitMasks(config.objectCapacity());
        traitCapacity = config.traitCapacity();
        random = config.createRandomSource();
        storages = new TraitStorage<?>[INITIAL_TRAIT_TYPES];
        queries = new Query[INITIAL_QUERIES];
    }

    /**
     * Registers a trait type and gives it the next type ID.
     *
     * <p>
     * Registration is explicit and happens once per type and world, usually at initialization: it allocates the
     * storage of the type and fills its pool with {@link WorldConfig#traitCapacity()} instances created by
     * {@link TraitType#create()}. It is allowed at any time outside a flush, also after objects exist; existing
     * objects have no trait of the new type. The same {@link TraitType} may be registered in several worlds, each
     * with its own storage and type ID.
     * </p>
     *
     * @param <T> The trait class.
     * @param type The trait type, not registered in this world yet.
     * @return The storage of the type, whose {@link TraitStorage#getTypeId()} is the number of types registered before
     *         it.
     * @throws IllegalArgumentException If the type is {@code null} or already registered in this world.
     * @throws IllegalStateException If the type creates a {@code null} instance, if called during a flush, or if the
     *             world failed.
     */
    public <T> TraitStorage<T> register(TraitType<T> type)
    {
        checkChangeable();
        if (type == null)
        {
            throw new IllegalArgumentException("Trait type is null");
        }
        for (int i = 0; i < typeCount; i++)
        {
            if (storages[i].getType() == type)
            {
                throw new IllegalArgumentException("Trait type already registered with the type ID " + i);
            }
        }
        final TraitStorage<T> storage = new TraitStorage<>(this,
                                                           type,
                                                           typeCount,
                                                           traitCapacity,
                                                           objects.getCapacity());
        if (typeCount == storages.length)
        {
            final TraitStorage<?>[] grown = new TraitStorage<?>[typeCount * 2];
            System.arraycopy(storages, 0, grown, 0, typeCount);
            storages = grown;
        }
        masks.addType(typeCount);
        storages[typeCount] = storage;
        typeCount++;
        return storage;
    }

    /**
     * Creates a query of the objects having all the given trait types, without exclude set.
     *
     * <p>
     * Same as {@link #createQuery(TraitStorage[], TraitStorage[])} with an empty exclude set.
     * </p>
     *
     * @param include The storages of the included trait types, registered in this world, at least one.
     * @return The query, filled from the state of the last flush.
     * @throws IllegalArgumentException If the include set is {@code null}, empty, contains {@code null} or a storage
     *             of another world.
     * @throws IllegalStateException If called during a flush, or if the world failed.
     */
    public Query createQuery(TraitStorage<?>... include)
    {
        return createQuery(include, NO_TYPES);
    }

    /**
     * Creates a query of the objects having all the trait types of an include set and none of an exclude set.
     *
     * <p>
     * The trait types are named by the storages {@link #register(TraitType)} returned; a type given twice in a set
     * counts once. The query is filled at once with the matching alive objects, as of the last flush, in the dense
     * order of the include storage with the fewest traits (pending changes are applied by the next flush, which also
     * updates the query); then every flush keeps it up to date, without allocating. Allowed at any time outside a
     * flush and an iteration of the same query, usually at initialization: it allocates the query, sized to the
     * object capacity. Each call creates a new query, even for the same sets; a query lives as long as the world, and
     * each one adds a constant cost to every trait change of its types at the flush, so create them once, not per step.
     * </p>
     *
     * @param include The storages of the included trait types, registered in this world, at least one.
     * @param exclude The storages of the excluded trait types, registered in this world, possibly none.
     * @return The query, filled from the state of the last flush.
     * @throws IllegalArgumentException If a set is {@code null} or contains {@code null} or a storage of another world,
     *             if the include set is empty, or if a type is both included and excluded.
     * @throws IllegalStateException If called during a flush, or if the world failed.
     */
    public Query createQuery(TraitStorage<?>[] include, TraitStorage<?>[] exclude)
    {
        checkChangeable();
        if (include == null || include.length == 0)
        {
            throw new IllegalArgumentException("Query include set is null or empty");
        }
        if (exclude == null)
        {
            throw new IllegalArgumentException("Query exclude set is null");
        }
        final int words = Math.max(maxTypeId(include), maxTypeId(exclude)) / TraitMasks.TYPES_PER_WORD + 1;
        final long[] includeMask = toMask(include, words);
        final long[] excludeMask = toMask(exclude, words);
        checkDisjoint(includeMask, excludeMask);
        final Query query = new Query(includeMask, excludeMask, objects.getCapacity());
        fill(query, include);
        if (queryCount == queries.length)
        {
            final Query[] grown = new Query[queryCount * 2];
            System.arraycopy(queries, 0, grown, 0, queryCount);
            queries = grown;
        }
        queries[queryCount] = query;
        queryCount++;
        return query;
    }

    /**
     * Returns the number of queries.
     *
     * @return The queries created in this world.
     */
    public int getQueryCount()
    {
        return queryCount;
    }

    /**
     * Returns the number of registered trait types.
     *
     * @return The trait types registered in this world; the next one gets this number as type ID.
     */
    public int getTraitTypeCount()
    {
        return typeCount;
    }

    /**
     * Creates an object, pending until the next {@link #flush()}.
     *
     * <p>
     * The ID is valid at once for recording commands, such as {@link #remove(long)}; {@link #isAlive(long)} returns
     * {@code false} until the flush. No allocation while the capacities fit.
     * </p>
     *
     * @return The ID of the new object, positive.
     * @throws IllegalStateException If {@link WorldConfig#MAX_OBJECT_CAPACITY} objects are already created and not
     *             removed, or as many commands are recorded since the last flush; if called during a flush, by a
     *             listener; or if the world failed.
     */
    public long create()
    {
        checkChangeable();
        final long id = objects.allocate();
        commands.add(StructuralCommands.CREATE, id);
        return id;
    }

    /**
     * Removes an object and all its traits, at the next {@link #flush()}.
     *
     * <p>
     * The object may be alive or pending: a pending object removed before its first flush is created and removed by
     * that flush, and is never alive. It stays alive until the flush. Removing it again before the flush is allowed and
     * changes nothing more. After the flush its ID is stale. No allocation while the capacities fit.
     * </p>
     *
     * @param objectId The ID of an alive or pending object of this world.
     * @throws IllegalArgumentException If the ID is stale (its object was removed by a flush) or never named an object
     *             of this world, {@link #NO_OBJECT} included.
     * @throws IllegalStateException If {@link WorldConfig#MAX_OBJECT_CAPACITY} commands are already recorded since the
     *             last flush; if called during a flush, by a listener; or if the world failed.
     */
    public void remove(long objectId)
    {
        checkRecordable(objectId);
        commands.add(StructuralCommands.REMOVE, objectId);
    }

    /**
     * Tells whether an ID names an alive object. Never throws, whatever the value.
     *
     * <p>
     * This is the safe check of a consumer that may hold an old ID, such as a camera following an object.
     * </p>
     *
     * @param objectId The ID, any value.
     * @return {@code true} if the object was created by a flush and not removed by a later one; {@code false} if it is
     *         pending, if the ID is stale, or if it never named an object of this world ({@link #NO_OBJECT}, a
     *         negative value).
     */
    public boolean isAlive(long objectId)
    {
        return objects.isAlive(objectId);
    }

    /**
     * Applies the recorded structural changes, in recorded order, then forgets them.
     *
     * <p>
     * First the object table, the trait masks and the trait storages grow, doubling, if more objects were created or
     * more traits added than they hold. Then each command applies in recorded order: a creation makes its object
     * alive; a removal removes the traits of its object, if still alive, then the object, whose index takes the next
     * generation and becomes reusable by the next {@link #create()} calls; a trait addition attaches its instance to
     * its object, if alive, replacing the trait of the same type it may have, otherwise the instance goes back to the
     * pool; a trait removal detaches the trait of its object, if it has one. Removed and replaced instances are reset
     * and pooled. Each trait attached or detached updates at once the queries that include or exclude its type; a
     * replacement changes no query. Each effective change is delivered at once to the listeners (see
     * {@link ObjectListener} and {@link TraitListener}). {@link #update(double)} calls it once per step, at the end;
     * outside the updates it may be called at any time (setup, tests, between two updates), but not inside the
     * iteration of a query. A flush without command changes nothing. No allocation unless a capacity grows.
     * </p>
     *
     * @throws IllegalStateException If called during an update (by a system) or a flush (by a listener), or if the
     *             world failed.
     */
    public void flush()
    {
        checkIdle();
        phase = FLUSHING;
        boolean done = false;
        try
        {
            applyCommands();
            done = true;
        }
        finally
        {
            phase = done ? IDLE : FAILED;
        }
    }

    /**
     * Runs one step: the enabled systems in slot order with the constant step, then one {@link #flush()}.
     *
     * <p>
     * First the enabled flags requested since the last step apply; then each enabled system runs, from the lowest slot
     * to the highest, systems of equal slots in registration order; then the commands recorded by the systems, and
     * before them by the game since the last flush (inbox events), apply in recorded order, firing the events. The
     * changes are thus visible from the next step. The scene calls it from the runtime update with the constant step of
     * the loop (ADR-0017); a variable step is not supported. A world without systems only flushes. No allocation once
     * the capacities fit the busiest step.
     * </p>
     *
     * <p>
     * An exception thrown by a system or a listener propagates; the remaining systems and the flush, or the rest of the
     * flush, do not run; the world is undefined afterwards and every later structural change, flush and update throws
     * {@link IllegalStateException}.
     * </p>
     *
     * @param stepSeconds The constant step, in seconds, positive and finite.
     * @throws IllegalArgumentException If the step is zero, negative, infinite or NaN; nothing runs.
     * @throws IllegalStateException If called during an update (by a system) or a flush (by a listener), or if the
     *             world failed.
     */
    public void update(double stepSeconds)
    {
        if (Double.isNaN(stepSeconds) || Double.isInfinite(stepSeconds) || stepSeconds <= 0.0)
        {
            throw new IllegalArgumentException("Step not positive and finite: " + stepSeconds);
        }
        checkIdle();
        phase = UPDATING;
        boolean done = false;
        try
        {
            systems.run(stepSeconds);
            phase = FLUSHING;
            applyCommands();
            done = true;
        }
        finally
        {
            phase = done ? IDLE : FAILED;
        }
    }

    /**
     * Registers a system, enabled, run by each {@link #update(double)} in slot order.
     *
     * <p>
     * The system runs after the systems of lower slots and of equal slots registered before it, and before the
     * others; any {@code int} is a slot. Standard slots and recommended game ranges: see {@link GameSystem}. Allowed
     * outside an update and a flush, usually at initialization; the first update after it runs the system. The same
     * system may be registered in several worlds.
     * </p>
     *
     * @param system The system, not registered in this world yet.
     * @param slot The ordering slot, lower first.
     * @throws IllegalArgumentException If the system is {@code null} or already registered in this world.
     * @throws IllegalStateException If called during an update or a flush, or if the world failed.
     */
    public void addSystem(GameSystem system, int slot)
    {
        checkIdle();
        systems.add(system, slot);
    }

    /**
     * Unregisters a system; the others keep their order.
     *
     * @param system A system registered in this world.
     * @throws IllegalArgumentException If the system is not registered in this world, {@code null} included.
     * @throws IllegalStateException If called during an update or a flush, or if the world failed.
     */
    public void removeSystem(GameSystem system)
    {
        checkIdle();
        systems.remove(system);
    }

    /**
     * Enables or disables a system from the next step.
     *
     * <p>
     * The flag applies at the start of the next {@link #update(double)}: during an update the current step keeps the
     * flags it started with, so a system disabled by an earlier system still runs in this step. Allowed at any time,
     * also by a system or a listener.
     * </p>
     *
     * @param system A system registered in this world.
     * @param enabled {@code true} to run it, {@code false} to skip it.
     * @throws IllegalArgumentException If the system is not registered in this world, {@code null} included.
     */
    public void setSystemEnabled(GameSystem system, boolean enabled)
    {
        systems.setEnabled(system, enabled);
    }

    /**
     * Tells whether a system is enabled.
     *
     * @param system A system registered in this world.
     * @return The flag the next {@link #update(double)} applies: the last value given to
     *         {@link #setSystemEnabled(GameSystem, boolean)}, {@code true} after registration.
     * @throws IllegalArgumentException If the system is not registered in this world, {@code null} included.
     */
    public boolean isSystemEnabled(GameSystem system)
    {
        return systems.isEnabled(system);
    }

    /**
     * Returns the number of systems.
     *
     * @return The systems registered in this world.
     */
    public int getSystemCount()
    {
        return systems.size();
    }

    /**
     * Registers an object listener, called after the listeners registered before it.
     *
     * <p>
     * Allowed at any time, also from a listener during the flush: it then receives the events from the next one.
     * </p>
     *
     * @param listener The listener, not registered in this world yet.
     * @throws IllegalArgumentException If the listener is {@code null} or already registered in this world.
     */
    public void addObjectListener(ObjectListener listener)
    {
        listeners.addObjectListener(listener);
    }

    /**
     * Unregisters an object listener.
     *
     * <p>
     * Allowed at any time, also from a listener during the flush: it then receives no event after the current one.
     * </p>
     *
     * @param listener A listener registered in this world.
     * @throws IllegalArgumentException If the listener is not registered in this world, {@code null} included.
     */
    public void removeObjectListener(ObjectListener listener)
    {
        listeners.removeObjectListener(listener);
    }

    /**
     * Registers a trait listener, called after the listeners registered before it.
     *
     * <p>
     * Allowed at any time, also from a listener during the flush: it then receives the events from the next one.
     * </p>
     *
     * @param listener The listener, not registered in this world yet.
     * @throws IllegalArgumentException If the listener is {@code null} or already registered in this world.
     */
    public void addTraitListener(TraitListener listener)
    {
        listeners.addTraitListener(listener);
    }

    /**
     * Unregisters a trait listener.
     *
     * <p>
     * Allowed at any time, also from a listener during the flush: it then receives no event after the current one.
     * </p>
     *
     * @param listener A listener registered in this world.
     * @throws IllegalArgumentException If the listener is not registered in this world, {@code null} included.
     */
    public void removeTraitListener(TraitListener listener)
    {
        listeners.removeTraitListener(listener);
    }

    /**
     * Applies the recorded commands, then forgets them: the body of {@link #flush()}.
     */
    private void applyCommands()
    {
        objects.growToIssued();
        growTraitIndex(objects.getCapacity());
        for (int t = 0; t < typeCount; t++)
        {
            storages[t].prepareFlush();
        }
        final int count = commands.size();
        for (int i = 0; i < count; i++)
        {
            apply(i);
        }
        for (int t = 0; t < typeCount; t++)
        {
            storages[t].endFlush();
        }
        commands.clear();
    }

    /**
     * Returns the random source of this world, the generator of the simulation (REQ-DET-001, REQ-DET-003).
     *
     * <p>
     * The same instance for the life of the world, so a system may keep it from its construction. It is created from
     * {@link WorldConfig#randomSeed()}, owned by this world and never given to another one. Draw from systems,
     * listeners, setup code and inbox events, all on the runtime thread; not from rendering. Reseed it through
     * {@link #reseed(long)} only, which checks the phase of the world; {@link RandomSource#reseed(long)} called
     * directly bypasses that check and is a misuse. Allowed in every phase, after a failure too.
     * </p>
     *
     * @return The random source, not {@code null}.
     */
    public RandomSource getRandomSource()
    {
        return random;
    }

    /**
     * Restarts the random source from a seed: the next draws are those of a new world configured with this seed.
     *
     * <p>
     * Allowed at setup and between two updates, for example to start a new round or to apply a seed received from a
     * server; it takes effect at once, so the next update draws the new sequence. Refused while an update or a flush
     * runs, so that a step draws from one sequence, and after a failure. Allocates nothing; the instance returned by
     * {@link #getRandomSource()} stays the same.
     * </p>
     *
     * @param seed The seed, any value.
     * @throws IllegalStateException If called during an update (by a system) or a flush (by a listener), or if the
     *             world failed.
     */
    public void reseed(long seed)
    {
        checkIdle();
        random.reseed(seed);
    }

    /**
     * Grows the object table ahead, so that the next flushes do not have to.
     *
     * <p>
     * Allocates now, outside the steps, for the object table, the trait masks, the index of every trait storage and
     * the queries; never shrinks: a value below the current capacity changes nothing. The trait capacities grow with
     * {@link TraitStorage#reserve(int)}.
     * </p>
     *
     * @param objectCapacity The number of objects to hold without growing, in [1,
     *            {@link WorldConfig#MAX_OBJECT_CAPACITY}].
     * @throws IllegalArgumentException If the capacity is outside its range.
     * @throws IllegalStateException If called during a flush, or if the world failed.
     */
    public void reserve(int objectCapacity)
    {
        checkChangeable();
        if (objectCapacity < 1 || objectCapacity > WorldConfig.MAX_OBJECT_CAPACITY)
        {
            throw new IllegalArgumentException("Object capacity outside [1, 2^30]: " + objectCapacity);
        }
        objects.grow(objectCapacity);
        growTraitIndex(objects.getCapacity());
    }

    /**
     * Returns the number of alive objects.
     *
     * @return The objects created and not removed, as of the last flush; pending objects are not counted.
     */
    public int getObjectCount()
    {
        return objects.getAlive();
    }

    /**
     * Returns the object capacity.
     *
     * @return The number of objects held without growing: the configured capacity, or more after a growth.
     */
    public int getObjectCapacity()
    {
        return objects.getCapacity();
    }

    /**
     * Checks that an ID names an alive object, for the accessors of the world and of its trait storages.
     *
     * @param objectId The ID.
     * @return The index of the object.
     * @throws IllegalStateException If the object is pending, waiting for the flush.
     * @throws IllegalArgumentException If the ID is stale or never named an object of this world.
     */
    int checkAlive(long objectId)
    {
        if (!objects.isAlive(objectId))
        {
            if (objects.isPending(objectId))
            {
                throw new IllegalStateException("Object pending until the flush: " + objectId);
            }
            throw new IllegalArgumentException(ERROR_INVALID + objectId);
        }
        return ObjectTable.index(objectId);
    }

    /**
     * Checks that a command may be recorded for an ID: the object is alive or pending, and the log has room.
     *
     * @param objectId The ID.
     * @throws IllegalArgumentException If the ID is stale or never named an object of this world.
     * @throws IllegalStateException If {@link WorldConfig#MAX_OBJECT_CAPACITY} commands are already recorded, if called
     *             during a flush, or if the world failed.
     */
    void checkRecordable(long objectId)
    {
        checkChangeable();
        if (!objects.isAlive(objectId) && !objects.isPending(objectId))
        {
            throw new IllegalArgumentException(ERROR_INVALID + objectId);
        }
        commands.ensureRoom();
    }

    /**
     * Records a trait command, after {@link #checkRecordable(long)}.
     *
     * @param kind The kind, {@link StructuralCommands#ADD_TRAIT} or {@link StructuralCommands#REMOVE_TRAIT}.
     * @param objectId The object ID.
     * @param typeId The trait type ID.
     * @param slot The staged slot of an addition, otherwise {@code 0}.
     */
    void recordTrait(byte kind, long objectId, int typeId, int slot)
    {
        commands.add(kind, objectId, typeId, slot);
    }

    /**
     * Returns the trait masks, the seam of the queries.
     *
     * @return The masks of the objects.
     */
    TraitMasks getMasks()
    {
        return masks;
    }

    /**
     * Checks that the structure of the world may change: no flush is running and the world did not fail.
     *
     * @throws IllegalStateException If a flush is running (a listener is called), or if the world failed.
     */
    void checkChangeable()
    {
        if (phase == FLUSHING)
        {
            throw new IllegalStateException("World flushing: a listener must not change the world");
        }
        if (phase == FAILED)
        {
            throw new IllegalStateException("World failed: an exception of a system or a listener left it undefined");
        }
    }

    /**
     * Checks that no update and no flush is running and that the world did not fail.
     *
     * @throws IllegalStateException If an update or a flush is running, or if the world failed.
     */
    private void checkIdle()
    {
        checkChangeable();
        if (phase == UPDATING)
        {
            throw new IllegalStateException("World updating: no flush, update, reseed or system change from a system");
        }
    }

    /**
     * Returns the capacity of the command log, for the tests of its growth.
     *
     * @return The number of commands held without growing.
     */
    int getCommandCapacity()
    {
        return commands.getCapacity();
    }

    /**
     * Applies one command, at the flush.
     *
     * @param command The command, in recorded order.
     */
    private void apply(int command)
    {
        final long id = commands.getId(command);
        switch (commands.getKind(command))
        {
            case StructuralCommands.CREATE -> applyCreate(id);
            case StructuralCommands.REMOVE -> applyRemove(id);
            case StructuralCommands.ADD_TRAIT -> applyAddTrait(id, commands.getType(command), commands.getArg(command));
            default -> applyRemoveTrait(id, commands.getType(command));
        }
    }

    /**
     * Makes a pending object alive.
     *
     * @param id The object ID.
     */
    private void applyCreate(long id)
    {
        objects.activate(id);
        listeners.created(id);
    }

    /**
     * Removes an object and its traits, if it is alive.
     *
     * @param id The object ID.
     */
    private void applyRemove(long id)
    {
        if (objects.isAlive(id))
        {
            final int index = ObjectTable.index(id);
            final int words = masks.getWordCount();
            for (int w = 0; w < words; w++)
            {
                long bits = masks.getWord(index, w);
                while (bits != 0L)
                {
                    final int typeId = w * TraitMasks.TYPES_PER_WORD + Long.numberOfTrailingZeros(bits);
                    bits &= bits - 1L;
                    detachTrait(id, index, typeId);
                }
            }
            objects.release(id);
            listeners.destroyed(id);
        }
    }

    /**
     * Attaches a staged trait to its object if it is alive, replacing the trait of the same type; otherwise pools it.
     *
     * @param id The object ID.
     * @param typeId The trait type ID.
     * @param slot The staged slot.
     */
    private void applyAddTrait(long id, int typeId, int slot)
    {
        final TraitStorage<?> storage = storages[typeId];
        if (objects.isAlive(id))
        {
            final int index = ObjectTable.index(id);
            if (storage.contains(index))
            {
                listeners.traitRemoved(id, typeId);
                storage.replace(index, slot);
            }
            else
            {
                storage.attach(id, index, slot);
                masks.set(index, typeId);
                updateQueries(id, index, typeId);
            }
            listeners.traitAdded(id, typeId);
        }
        else
        {
            storage.discard(slot);
        }
    }

    /**
     * Detaches a trait from its object, if the object is alive and has it.
     *
     * @param id The object ID.
     * @param typeId The trait type ID.
     */
    private void applyRemoveTrait(long id, int typeId)
    {
        if (objects.isAlive(id))
        {
            final int index = ObjectTable.index(id);
            if (storages[typeId].contains(index))
            {
                detachTrait(id, index, typeId);
            }
        }
    }

    /**
     * Detaches a trait the object has: reports it, clears its mask bit, then resets and pools the instance.
     *
     * @param id The object ID.
     * @param index The object index.
     * @param typeId The trait type ID.
     */
    private void detachTrait(long id, int index, int typeId)
    {
        listeners.traitRemoved(id, typeId);
        masks.clear(index, typeId);
        updateQueries(id, index, typeId);
        storages[typeId].detach(index);
    }

    /**
     * Updates the queries that include or exclude a trait type, after a change of its mask bit.
     *
     * @param id The object ID.
     * @param index The object index.
     * @param typeId The trait type ID.
     */
    private void updateQueries(long id, int index, int typeId)
    {
        for (int q = 0; q < queryCount; q++)
        {
            final Query query = queries[q];
            if (query.involves(typeId))
            {
                query.update(masks, id, index);
            }
        }
    }

    /**
     * Returns the highest type ID of a set of storages, checking them.
     *
     * @param set The storages.
     * @return The highest type ID, {@code 0} for an empty set.
     * @throws IllegalArgumentException If the set contains {@code null} or a storage of another world.
     */
    private int maxTypeId(TraitStorage<?>[] set)
    {
        int max = 0;
        for (final TraitStorage<?> storage : set)
        {
            if (storage == null || !storage.belongsTo(this))
            {
                throw new IllegalArgumentException("Query trait storage is null or of another world");
            }
            max = Math.max(max, storage.getTypeId());
        }
        return max;
    }

    /**
     * Checks that no type is both included and excluded.
     *
     * @param include The include mask words.
     * @param exclude The exclude mask words, as many.
     * @throws IllegalArgumentException If a type is in both.
     */
    private static void checkDisjoint(long[] include, long[] exclude)
    {
        for (int w = 0; w < include.length; w++)
        {
            final long both = include[w] & exclude[w];
            if (both != 0L)
            {
                final int typeId = w * TraitMasks.TYPES_PER_WORD + Long.numberOfTrailingZeros(both);
                throw new IllegalArgumentException("Query type both included and excluded: " + typeId);
            }
        }
    }

    /**
     * Fills a new query with the matching alive objects, in the dense order of its smallest include storage.
     *
     * @param query The query, empty.
     * @param include The include storages, at least one.
     */
    private void fill(Query query, TraitStorage<?>[] include)
    {
        TraitStorage<?> smallest = include[0];
        for (final TraitStorage<?> storage : include)
        {
            if (storage.getCount() < smallest.getCount())
            {
                smallest = storage;
            }
        }
        final int count = smallest.getCount();
        for (int p = 0; p < count; p++)
        {
            final long id = smallest.getId(p);
            query.update(masks, id, ObjectTable.index(id));
        }
    }

    /**
     * Builds the mask words of a set of storages.
     *
     * @param set The storages, checked.
     * @param words The number of words.
     * @return The mask words.
     */
    private static long[] toMask(TraitStorage<?>[] set, int words)
    {
        final long[] mask = new long[words];
        for (final TraitStorage<?> storage : set)
        {
            final int typeId = storage.getTypeId();
            mask[typeId / TraitMasks.TYPES_PER_WORD] |= 1L << typeId;
        }
        return mask;
    }

    /**
     * Grows the trait masks, the index of every trait storage and the queries to the object capacity.
     *
     * @param objectCapacity The object capacity.
     */
    private void growTraitIndex(int objectCapacity)
    {
        masks.grow(objectCapacity);
        for (int t = 0; t < typeCount; t++)
        {
            storages[t].growObjects(objectCapacity);
        }
        for (int q = 0; q < queryCount; q++)
        {
            queries[q].growObjects(objectCapacity);
        }
    }
}
