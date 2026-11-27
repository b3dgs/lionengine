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
 * The type of {@link Health}, counting its creations and resets.
 */
final class HealthType implements TraitType<Health>
{
    /** Instances created. */
    private int created;
    /** Resets. */
    private int resets;

    /**
     * Creates the type.
     */
    HealthType()
    {
        super();
    }

    @Override
    public Health create()
    {
        created++;
        return new Health();
    }

    @Override
    public void reset(Health trait)
    {
        resets++;
        trait.points = 0;
    }

    /**
     * Returns the instances created.
     *
     * @return The number of {@link #create()} calls.
     */
    int getCreated()
    {
        return created;
    }

    /**
     * Returns the resets.
     *
     * @return The number of {@link #reset(Health)} calls.
     */
    int getResets()
    {
        return resets;
    }
}
