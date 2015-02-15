/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.strategy;

/**
 * This class represents the player, with its data, using a unique ID. The ID will be assigned to any entity owned by
 * the player. Then it is possible to know which entity is owned by a player.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public abstract class PlayerStrategy
{
    /** Last player id. */
    private static int lastId = 0;

    /**
     * Get the next ID.
     * 
     * @return The next ID.
     */
    private static int getNextId()
    {
        PlayerStrategy.lastId++;
        return PlayerStrategy.lastId;
    }

    /** Player id. */
    public final int id;
    /** Player name. */
    private String name;

    /**
     * Constructor base.
     */
    public PlayerStrategy()
    {
        name = null;
        id = PlayerStrategy.getNextId();
    }

    /**
     * Set player name.
     * 
     * @param name The player name.
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Get player name.
     * 
     * @return The player name.
     */
    public String getName()
    {
        return name;
    }
}
