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
package com.b3dgs.lionengine.example.tyrian.entity.scenery;

import java.lang.reflect.InvocationTargetException;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.example.tyrian.effect.FactoryEffect;
import com.b3dgs.lionengine.example.tyrian.effect.HandlerEffect;
import com.b3dgs.lionengine.example.tyrian.entity.Entity;
import com.b3dgs.lionengine.game.SetupSurfaceGame;
import com.b3dgs.lionengine.game.entity.FactoryEntityGame;

/**
 * Factory entity scenery.
 */
public final class FactoryEntityScenery
        extends FactoryEntityGame<EntitySceneryType, SetupSurfaceGame, Entity>
{
    /** Unknown entity error message. */
    private static final String UNKNOWN_ENTITY_ERROR = "Unknown entity: ";

    /**
     * Create an entity from its type.
     * 
     * @param setup The setup reference.
     * @param factoryEffect The effect factory reference.
     * @param handlerEffect The handler effect reference.
     * @param type The item type.
     * @param factory The factory class.
     * @return The entity instance.
     */
    private static Entity createEntity(SetupSurfaceGame setup, FactoryEffect factoryEffect,
            HandlerEffect handlerEffect, EntitySceneryType type, Class<?> factory)
    {
        try
        {
            final StringBuilder clazz = new StringBuilder(factory.getPackage().getName());
            clazz.append('.').append(type.asClassName());
            return (Entity) Class.forName(clazz.toString())
                    .getConstructor(SetupSurfaceGame.class, FactoryEffect.class, HandlerEffect.class)
                    .newInstance(setup, factoryEffect, handlerEffect);
        }
        catch (InstantiationException
               | IllegalAccessException
               | IllegalArgumentException
               | InvocationTargetException
               | NoSuchMethodException
               | SecurityException
               | ClassCastException
               | ClassNotFoundException exception)
        {
            throw new LionEngineException(exception, FactoryEntityScenery.UNKNOWN_ENTITY_ERROR + type.asClassName());
        }
    }

    /** Factory effect. */
    private final FactoryEffect factoryEffect;
    /** Handler effect. */
    private final HandlerEffect handlerEffect;

    /**
     * Constructor.
     * 
     * @param factoryEffect The effect factory reference.
     * @param handlerEffect The handler effect reference.
     */
    public FactoryEntityScenery(FactoryEffect factoryEffect, HandlerEffect handlerEffect)
    {
        super(EntitySceneryType.class, EntitySceneryType.values(), Media.getPath("entities", "scenery"));
        this.factoryEffect = factoryEffect;
        this.handlerEffect = handlerEffect;
        load();
    }

    /*
     * FactoryEntityGame
     */

    @Override
    public Entity createEntity(EntitySceneryType type)
    {
        return FactoryEntityScenery.createEntity(getSetup(type), factoryEffect, handlerEffect, type, getClass());
    }

    @Override
    protected SetupSurfaceGame createSetup(EntitySceneryType key, Media config)
    {
        return new SetupSurfaceGame(config);
    }
}
