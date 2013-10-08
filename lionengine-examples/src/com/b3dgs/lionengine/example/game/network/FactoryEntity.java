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
package com.b3dgs.lionengine.example.game.network;

import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.game.SetupSurfaceGame;
import com.b3dgs.lionengine.game.entity.FactoryEntityGame;
import com.b3dgs.lionengine.game.purview.model.ConfigurableModel;

/**
 * Factory entity implementation. Any entity instantiation has to be made using a factory instance.
 */
class FactoryEntity
        extends FactoryEntityGame<TypeEntity, SetupSurfaceGame, Entity>
{
    /** Main entity directory name. */
    private static final String ENTITY_DIR = "entity";
    /** Entity desired fps. */
    private final int desiredFps;
    /** Map reference. */
    private final Map map;

    /**
     * Standard constructor.
     * 
     * @param desiredFps The desired fps.
     * @param map The map reference.
     */
    public FactoryEntity(int desiredFps, Map map)
    {
        super(TypeEntity.class);
        this.desiredFps = desiredFps;
        this.map = map;
        loadAll(TypeEntity.values());
    }

    @Override
    protected SetupSurfaceGame createSetup(TypeEntity id)
    {
        return new SetupSurfaceGame(new ConfigurableModel(), Media.get(FactoryEntity.ENTITY_DIR, id + ".xml"), false);
    }

    @Override
    public Entity createEntity(TypeEntity type)
    {
        switch (type)
        {
            case mario:
                return createMario(true);
            case goomba:
                return createGoomba(true);
            default:
                return null;
        }
    }

    /**
     * Create a new mario.
     * 
     * @param server <code>true</code> if is server, <code>false</code> if client.
     * @return The instance of mario.
     */
    public Mario createMario(boolean server)
    {
        return new Mario(getSetup(TypeEntity.mario), map, desiredFps, server);
    }

    /**
     * Create a new goomba.
     * 
     * @param server <code>true</code> if is server, <code>false</code> if client.
     * @return The instance of goomba.
     */
    public Goomba createGoomba(boolean server)
    {
        return new Goomba(getSetup(TypeEntity.goomba), map, desiredFps, server);
    }
}
