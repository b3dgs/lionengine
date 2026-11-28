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
package com.b3dgs.lionengine.traits;

import com.b3dgs.lionengine.RandomSource;
import com.b3dgs.lionengine.ecs.Query;
import com.b3dgs.lionengine.ecs.TraitStorage;
import com.b3dgs.lionengine.ecs.World;

/**
 * The tile push-out scene of the benchmark (ADR-0083): squares of 0.5 m walking and hopping in a tile arena of 4 m² per
 * object, with the capture, a hop system, {@code Gravity}, {@code Movement} and {@code Collision} with the tiles.
 *
 * <p>
 * The arena is a {@link TileGrid} of 1 m tiles: a floor, two side walls and full-width platforms every four rows, so
 * the objects stay spread over the levels. Each object walks at {@value #SPEED} m/s, turns back when a wall stops it
 * and hops at {@value #HOP} m/s when grounded (about 1.3 m high, under the next platform), under a gravity of
 * {@value #GRAVITY} m/s² limited to {@value #TERMINAL} m/s (a displacement of at most 0.17 m per step of 1/60 s, under
 * half of the 0.5 m box). Each step pushes every object out of the floor it lands on, of the wall it walks into, or
 * neither while in the air. Seeded placement: the scene is the same at each run.
 * </p>
 */
final class TileBenchmarkWorld
{
    /** The step, in seconds. */
    static final double STEP = 1.0 / 60.0;
    /** The slot of the hop system, before {@code Gravity}. */
    private static final int HOP_SLOT = 1500;
    /** Arena area per object, in square meters. */
    private static final double AREA_PER_OBJECT = 4.0;
    /** Box size, in meters. */
    private static final double SIZE = 0.5;
    /** Walking speed, in meters per second. */
    private static final double SPEED = 2.0;
    /** Hop velocity, in meters per second. */
    private static final double HOP = 5.0;
    /** Gravity, in meters per second squared. */
    private static final double GRAVITY = -9.81;
    /** Terminal velocity, in meters per second. */
    private static final double TERMINAL = 10.0;
    /** Rows between two platform rows. */
    private static final int PLATFORM_SPACING = 4;
    /** The placement seed. */
    private static final long SEED = 0x4C49_4F4EL;

    /** The world. */
    private final World world;
    /** The transformables. */
    private final TraitStorage<Transformable> transformables;
    /** The movables. */
    private final TraitStorage<Movable> movables;
    /** The gravities. */
    private final TraitStorage<Gravity> gravities;
    /** The collidables. */
    private final TraitStorage<Collidable> collidables;
    /** The jumpables. */
    private final TraitStorage<Jumpable> jumpables;
    /** The collision system. */
    private final CollisionSystem collision;
    /** The hopping objects. */
    private final Query hopping;
    /** The arena. */
    private final TileGrid grid;
    /** The arena side, in tiles. */
    private final int side;
    /** The population. */
    private final int population;
    /** The grounded objects counted over the steps. */
    private long grounded;

    /**
     * Creates the scene, without objects.
     *
     * @param population The population.
     */
    TileBenchmarkWorld(int population)
    {
        this.population = population;
        side = (int) Math.ceil(Math.sqrt(population * AREA_PER_OBJECT));
        grid = new TileGrid(side, side).fill(0, 0, side, 1).fill(0, 0, 1, side).fill(side - 1, 0, 1, side);
        for (int row = PLATFORM_SPACING; row < side - 1; row += PLATFORM_SPACING)
        {
            grid.fill(1, row, side - 2, 1);
        }
        world = new World();
        transformables = world.register(Transformable.TYPE);
        movables = world.register(Movable.TYPE);
        gravities = world.register(Gravity.TYPE);
        collidables = world.register(Collidable.TYPE);
        jumpables = world.register(Jumpable.TYPE);
        collision = new CollisionSystem(world, transformables, collidables, movables, jumpables, grid);
        hopping = collision.getTileQuery();
        world.addSystem(new TransformCaptureSystem(world, transformables), TransformCaptureSystem.SLOT);
        world.addSystem(s -> hop(), HOP_SLOT);
        world.addSystem(new GravitySystem(world, movables, gravities), GravitySystem.SLOT);
        world.addSystem(new MovementSystem(world, transformables, movables), MovementSystem.SLOT);
        world.addSystem(collision, CollisionSystem.SLOT);
    }

    /**
     * Creates the objects in empty cells, from the seed, and flushes.
     */
    void populate()
    {
        final RandomSource random = new RandomSource(SEED);
        for (int i = 0; i < population; i++)
        {
            int column;
            int row;
            do
            {
                column = 1 + random.nextInt(side - 2);
                row = 1 + random.nextInt(side - 2);
            }
            while (grid.isSolid(column, row));
            final long id = world.create();
            final Transformable transformable = transformables.add(id);
            transformable.setPosition(column + 0.5, row + 0.5);
            transformable.setPreviousPosition(column + 0.5, row + 0.5);
            transformable.setSize(SIZE, SIZE);
            movables.add(id).setVelocity(random.nextInt(2) == 0 ? -SPEED : SPEED, 0.0);
            final Gravity gravity = gravities.add(id);
            gravity.setAcceleration(GRAVITY);
            gravity.setTerminalVelocity(TERMINAL);
            collidables.add(id).setSize(SIZE, SIZE);
            jumpables.add(id).setImpulse(HOP);
        }
        world.flush();
    }

    /**
     * Returns the world.
     *
     * @return The world.
     */
    World getWorld()
    {
        return world;
    }

    /**
     * Returns the collision system.
     *
     * @return The system.
     */
    CollisionSystem getCollision()
    {
        return collision;
    }

    /**
     * Returns the grounded objects counted over the steps.
     *
     * @return The count, positive once objects landed.
     */
    long getGrounded()
    {
        return grounded;
    }

    /**
     * Turns back the objects a wall stopped and makes the grounded ones hop.
     */
    private void hop()
    {
        final int count = hopping.getCount();
        for (int i = 0; i < count; i++)
        {
            final long id = hopping.getId(i);
            final Movable movable = movables.get(id);
            final Jumpable jumpable = jumpables.get(id);
            double vx = movable.getVelocityX();
            if (Math.abs(vx) < SPEED)
            {
                vx = transformables.get(id).getX() < side * 0.5 ? SPEED : -SPEED;
            }
            double vy = movable.getVelocityY();
            if (jumpable.isGrounded())
            {
                vy = jumpable.getImpulse();
                grounded++;
            }
            movable.setVelocity(vx, vy);
        }
    }
}
