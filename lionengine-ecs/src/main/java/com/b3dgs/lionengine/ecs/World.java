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
 * The container of the objects of a game or of a scene (REQ-WORLD-001, REQ-ECS-001, REQ-ECS-002, REQ-ECS-010,
 * REQ-ECS-011, ADR-0011, ADR-0021, ADR-0030, ADR-0034, ADR-0074).
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
 * <b>Deferred changes.</b> {@link #create()} returns the ID of a new object at once, so further commands can be
 * recorded with it, but the object is <em>pending</em>: {@link #isAlive(long)} returns {@code false} until the next
 * {@link #flush()}, and accessors fail fast. {@link #remove(long)} records the removal of a pending or alive object,
 * with all its traits: the object stays alive until the flush. The flush applies the recorded commands in recorded
 * order: a creation and a removal recorded in the same step both apply. A removal of an object already removed by an
 * earlier command of the same flush changes nothing. In the game loop there is one flush per step, at the end, after
 * every system and before rendering, so changes are visible from the next step; an explicit flush is allowed outside
 * the loop (setup, tests) and between two world updates (snapshot application, ADR-0038).
 * </p>
 *
 * <p>
 * <b>Capacity.</b> The world holds {@link WorldConfig#objectCapacity()} objects without growing. Creating more is
 * allowed: the table doubles at the next flush until every created object fits, a documented warm-up cost, and never
 * shrinks; {@link #reserve(int)} grows it ahead. The log of the recorded commands starts with the same capacity and
 * doubles when a step records more commands. Once the capacities fit the busiest step, {@link #create()},
 * {@link #remove(long)} and {@link #flush()} allocate nothing. The world never holds more than
 * {@link WorldConfig#MAX_OBJECT_CAPACITY} indices at once.
 * </p>
 *
 * <p>
 * No singleton: several worlds coexist, each independent of the others; an ID is meaningful in the world that created
 * it only. A world has no dependency on rendering, resources, audio, camera or tile maps. Traits, queries, events,
 * systems and the random source are added to the world by the next batches of the feature page.
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

    /** The objects. */
    private final ObjectTable objects;
    /** The commands recorded since the last flush. */
    private final StructuralCommands commands;

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
        if (!objects.isAlive(objectId) && !objects.isPending(objectId))
        {
            throw new IllegalArgumentException(ERROR_INVALID + objectId);
        }
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
     * First the object table grows, doubling, if more objects were created than it holds. Then each creation makes its
     * object alive and each removal removes its object, if still alive; its index takes the next generation and becomes
     * reusable by the next {@link #create()} calls. In the game loop the world calls it once per step, at the end;
     * outside the loop it may be called at any time (setup, tests). A flush without command changes nothing. No
     * allocation unless the table grows.
     * </p>
     */
    public void flush()
    {
        objects.growToIssued();
        final int count = commands.size();
        for (int i = 0; i < count; i++)
        {
            final long id = commands.getId(i);
            if (commands.getKind(i) == StructuralCommands.CREATE)
            {
                objects.activate(id);
            }
            else
            {
                objects.release(id);
            }
        }
        commands.clear();
    }

    /**
     * Grows the object table ahead, so that the next flushes do not have to.
     *
     * <p>
     * Allocates now, outside the steps; never shrinks: a value below the current capacity changes nothing.
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
     * Checks that an ID names an alive object, for the accessors of the world (the trait accessors of the next batch).
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
     * Returns the capacity of the command log, for the tests of its growth.
     *
     * @return The number of commands held without growing.
     */
    int getCommandCapacity()
    {
        return commands.getCapacity();
    }
}
