/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.state;

/**
 * Last state marker. Must be used to return the last state instead of a specific one.
 */
public final class StateLast extends StateAbstract
{
    /**
     * Private constructor.
     */
    public StateLast()
    {
        super();
    }

    @Override
    public void enter()
    {
        // Nothing to do
    }

    @Override
    public void update(double extrp)
    {
        // Nothing to do
    }
}
