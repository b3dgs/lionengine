/*
 * Copyright (C) 2013 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.example.d_rts.d_ability;

import java.util.Locale;

/**
 * List of entity types.
 */
public enum EntityType
{
    /** Peon unit. */
    PEON,
    /** Grunt unit. */
    GRUNT,
    /** Spearman unit. */
    SPEARMAN,
    /** TownHall building. */
    TOWNHALL_ORC,
    /** Farm building. */
    FARM_ORC,
    /** Barracks building. */
    BARRACKS_ORC,
    /** Gold mine building. */
    GOLD_MINE;

    @Override
    public String toString()
    {
        return name().toLowerCase(Locale.ENGLISH);
    }
}
