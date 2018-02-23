/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.b3dgs.lionengine.game.state;

/**
 * State base test implementation.
 */
public class StateBase extends StateAbstract
{
    /** Entered flag. */
    private boolean entered;
    /** Updated flag. */
    private boolean updated;
    /** Exited flag. */
    private boolean exited;

    /**
     * Create the state.
     * 
     * @param type The state type.
     */
    public StateBase(Enum<?> type)
    {
        super(type);
    }

    /**
     * Check entered flag.
     * 
     * @return <code>true</code> if entered, <code>false</code> else.
     */
    public boolean isEntered()
    {
        return entered;
    }

    /**
     * Check updated flag.
     * 
     * @return <code>true</code> if updated, <code>false</code> else.
     */
    public boolean isUpdated()
    {
        return updated;
    }

    /**
     * Check exited flag.
     * 
     * @return <code>true</code> if exited, <code>false</code> else.
     */
    public boolean isExited()
    {
        return exited;
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
