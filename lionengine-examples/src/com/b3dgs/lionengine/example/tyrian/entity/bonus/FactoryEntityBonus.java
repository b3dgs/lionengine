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
package com.b3dgs.lionengine.example.tyrian.entity.bonus;

import java.lang.reflect.InvocationTargetException;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.example.tyrian.effect.FactoryEffect;
import com.b3dgs.lionengine.example.tyrian.effect.HandlerEffect;
import com.b3dgs.lionengine.example.tyrian.entity.Entity;
import com.b3dgs.lionengine.game.SetupSurfaceGame;
import com.b3dgs.lionengine.game.entity.FactoryEntityGame;

/**
 * Factory entity bonus.
 */
public final class FactoryEntityBonus
        extends FactoryEntityGame<EntityBonusType, SetupSurfaceGame, Entity>
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
            HandlerEffect handlerEffect, EntityBonusType type, Class<?> factory)
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
            throw new LionEngineException(exception, FactoryEntityBonus.UNKNOWN_ENTITY_ERROR + type.asClassName());
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
    public FactoryEntityBonus(FactoryEffect factoryEffect, HandlerEffect handlerEffect)
    {
        super(EntityBonusType.class);
        this.factoryEffect = factoryEffect;
        this.handlerEffect = handlerEffect;
        loadAll(EntityBonusType.values());
    }

    /*
     * FactoryEntityGame
     */

    @Override
    public Entity createEntity(EntityBonusType type)
    {
        return FactoryEntityBonus.createEntity(getSetup(type), factoryEffect, handlerEffect, type, getClass());
    }

    @Override
    protected SetupSurfaceGame createSetup(EntityBonusType type)
    {
        return new SetupSurfaceGame(Media.get("entities", "bonus", type.asPathName() + ".xml"));
    }
}
