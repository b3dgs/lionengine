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

/**
 * The container of the objects of a game or of a scene and of their traits (REQ-WORLD-001, REQ-ECS-001 to REQ-ECS-004,
 * REQ-ECS-006, REQ-ECS-010 to REQ-ECS-013, ADR-0011, ADR-0021, ADR-0030, ADR-0034, ADR-0074, ADR-0075).
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
 * it only. A world has no dependency on rendering, resources, audio, camera or tile maps. Queries, events, systems and
 * the random source are added to the world by the next batches of the feature page.
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
    /** The hook of a world without listeners. */
    private static final ChangeHook NO_HOOK = new ChangeHook()
    {
        // Every change is ignored.
    };

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
    /** The effective changes of the flush. */
    private ChangeHook hook = NO_HOOK;

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
        storages = new TraitStorage<?>[INITIAL_TRAIT_TYPES];
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
     * @throws IllegalStateException If the type creates a {@code null} instance.
     */
    public <T> TraitStorage<T> register(TraitType<T> type)
    {
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
     *             removed, or as many commands are recorded since the last flush.
     */
    public long create()
    {
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
     *             last flush.
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
     * and pooled. In the game loop the world calls it once per step, at the end; outside the loop it may be called at
     * any time (setup, tests). A flush without command changes nothing. No allocation unless a capacity grows.
     * </p>
     */
    public void flush()
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
     * Grows the object table ahead, so that the next flushes do not have to.
     *
     * <p>
     * Allocates now, outside the steps, for the object table, the trait masks and the index of every trait storage;
     * never shrinks: a value below the current capacity changes nothing. The trait capacities grow with
     * {@link TraitStorage#reserve(int)}.
     * </p>
     *
     * @param objectCapacity The number of objects to hold without growing, in [1,
     *            {@link WorldConfig#MAX_OBJECT_CAPACITY}].
     * @throws IllegalArgumentException If the capacity is outside its range.
     */
    public void reserve(int objectCapacity)
    {
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
     * @throws IllegalStateException If {@link WorldConfig#MAX_OBJECT_CAPACITY} commands are already recorded.
     */
    void checkRecordable(long objectId)
    {
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
     * Sets the hook of the effective changes, the seam of the listeners.
     *
     * @param hook The hook, not {@code null}.
     */
    void setHook(ChangeHook hook)
    {
        this.hook = hook;
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
        hook.onCreated(id);
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
            hook.onDestroyed(id);
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
                hook.onTraitRemoved(id, typeId);
                storage.replace(index, slot);
            }
            else
            {
                storage.attach(id, index, slot);
                masks.set(index, typeId);
            }
            hook.onTraitAdded(id, typeId);
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
        hook.onTraitRemoved(id, typeId);
        masks.clear(index, typeId);
        storages[typeId].detach(index);
    }

    /**
     * Grows the trait masks and the index of every trait storage to the object capacity.
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
    }
}
