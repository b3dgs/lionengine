/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.example.game.entity;

import com.b3dgs.lionengine.core.Core;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.game.CameraGame;
import com.b3dgs.lionengine.game.ContextGame;
import com.b3dgs.lionengine.game.EntityGame;
import com.b3dgs.lionengine.game.FactoryObjectGame;
import com.b3dgs.lionengine.game.SetupSurfaceGame;
import com.b3dgs.lionengine.game.configurer.Configurer;

/**
 * Entity implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
abstract class Entity
        extends EntityGame
{
    /**
     * Get an entity configuration file.
     * 
     * @param type The config associated class.
     * @return The media config.
     */
    protected static Media getConfig(Class<? extends Entity> type)
    {
        return Core.MEDIA
                .create(Factory.ENTITY_DIR, type.getSimpleName() + "." + FactoryObjectGame.FILE_DATA_EXTENSION);
    }

    /**
     * Constructor.
     * 
     * @param setup The setup reference.
     */
    protected Entity(SetupSurfaceGame setup)
    {
        super(setup);
        final Configurer configurer = setup.getConfigurer();
        setCollision(configurer.getCollision("default"));
    }

    /*
     * EntityGame
     */

    @Override
    public void prepare(ContextGame context)
    {
        // Nothing to do
    }

    @Override
    public void update(double extrp)
    {
        System.out.println("I am updating: " + this);
    }

    @Override
    public void render(Graphic g, CameraGame camera)
    {
        System.out.println("I am rendering: " + this);
    }
}
