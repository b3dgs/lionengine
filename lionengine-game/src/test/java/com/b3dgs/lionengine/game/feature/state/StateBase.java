/*
 * Copyright (C) 2013-2022 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
 * State base test implementation.
 */
public class StateBase extends StateAbstract
{
    /** Entered flag. */
    static boolean entered;
    /** Updated flag. */
    static boolean updated;
    /** Exited flag. */
    static boolean exited;
    /** Check flag. */
    static boolean check;

    /**
     * Reset flags.
     */
    static void reset()
    {
        entered = false;
        updated = false;
        exited = false;
    }

    /**
     * Create state.
     */
    public StateBase()
    {
        super();

        addTransition(StateNext.class, () -> check);
    }

    @Override
    public void enter()
    {
        entered = true;
    }

    @Override
    public void update(double extrp)
    {
        updated = true;
    }

    @Override
    public void exit()
    {
        super.exit();
        exited = true;
    }
}
