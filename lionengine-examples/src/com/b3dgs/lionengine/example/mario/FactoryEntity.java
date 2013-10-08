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
package com.b3dgs.lionengine.example.mario;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.game.SetupSurfaceGame;
import com.b3dgs.lionengine.game.entity.FactoryEntityGame;

/**
 * Factory entity implementation. Any entity instantiation has to be made using a factory instance.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
final class FactoryEntity
        extends FactoryEntityGame<EntityType, SetupSurfaceGame, Entity>
{
    /** Main entity directory name. */
    private static final String ENTITY_DIR = "entities";
    /** Map reference. */
    private final Map map;
    /** Entity desired fps. */
    private final int desiredFps;

    /**
     * Constructor.
     * 
     * @param desiredFps The desired fps.
     * @param map The map reference.
     */
    FactoryEntity(Map map, int desiredFps)
    {
        super(EntityType.class);
        this.map = map;
        this.desiredFps = desiredFps;
        loadAll(EntityType.values());
    }

    /**
     * Create a new mario.
     * 
     * @return The instance of mario.
     */
    Mario createMario()
    {
        return new Mario(getSetup(EntityType.MARIO), map, desiredFps);
    }

    /**
     * Create a new goomba.
     * 
     * @return The instance of goomba.
     */
    Goomba createGoomba()
    {
        return new Goomba(getSetup(EntityType.GOOMBA), map, desiredFps);
    }

    /*
     * FactoryEntityGame
     */

    @Override
    protected SetupSurfaceGame createSetup(EntityType id)
    {
        return new SetupSurfaceGame(Media.get(FactoryEntity.ENTITY_DIR, id + ".xml"));
    }

    @Override
    public Entity createEntity(EntityType type)
    {
        switch (type)
        {
            case MARIO:
                return createMario();
            case GOOMBA:
                return createGoomba();
            default:
                throw new LionEngineException("Unknown entity type: " + type);
        }
    }
}
