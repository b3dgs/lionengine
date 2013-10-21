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
package com.b3dgs.lionengine.example.warcraft.entity.human;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.example.warcraft.Context;
import com.b3dgs.lionengine.example.warcraft.entity.Entity;
import com.b3dgs.lionengine.example.warcraft.entity.EntityType;

/**
 * Factory entity implementation.
 */
public final class FactoryEntityHuman
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
            case PEASANT:
                return new Peasant(context);
            case FOOTMAN:
                return new Footman(context);
            case ARCHER:
                return new Archer(context);
            case TOWNHALL_HUMAN:
                return new TownhallHuman(context);
            case FARM_HUMAN:
                return new FarmHuman(context);
            case BARRACKS_HUMAN:
                return new BarracksHuman(context);
            case LUMBERMILL_HUMAN:
                return new LumbermillHuman(context);
            default:
                throw new LionEngineException("Entity not found: ", type.name());
        }
    }

    /**
     * Constructor.
     */
    private FactoryEntityHuman()
    {
        throw new RuntimeException();
    }
}
