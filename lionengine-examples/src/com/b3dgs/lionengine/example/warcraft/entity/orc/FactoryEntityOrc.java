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
package com.b3dgs.lionengine.example.warcraft.entity.orc;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.example.warcraft.Context;
import com.b3dgs.lionengine.example.warcraft.entity.Entity;
import com.b3dgs.lionengine.example.warcraft.entity.EntityType;

/**
 * Factory entity implementation.
 */
public final class FactoryEntityOrc
{
    /**
     * Create an entity from its type.
     * 
     * @param type The entity type.
     * @param context The context reference.
     * @return The entity instance.
     */
    public static Entity createEntity(EntityType type, Context context)
    {
        switch (type)
        {
            case PEON:
                return new Peon(context);
            case GRUNT:
                return new Grunt(context);
            case SPEARMAN:
                return new Spearman(context);
            case TOWNHALL_ORC:
                return new TownhallOrc(context);
            case FARM_ORC:
                return new FarmOrc(context);
            case BARRACKS_ORC:
                return new BarracksOrc(context);
            case LUMBERMILL_ORC:
                return new LumbermillOrc(context);
            default:
                throw new LionEngineException("Entity not found: ", type.name());
        }
    }

    /**
     * Constructor.
     */
    private FactoryEntityOrc()
    {
        throw new RuntimeException();
    }
}
