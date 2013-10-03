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
package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity;

import java.lang.reflect.InvocationTargetException;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.AppLionheart;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.Level;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.WorldType;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.item.FactoryEntityItem;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.monster.FactoryEntityMonster;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.player.Valdyn;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.scenery.FactoryEntityScenery;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.landscape.LandscapeType;
import com.b3dgs.lionengine.game.SetupSurfaceRasteredGame;
import com.b3dgs.lionengine.game.entity.FactoryEntityGame;

/**
 * Handle the entity creation by containing all necessary object for their instantiation.
 */
public final class FactoryEntity
        extends FactoryEntityGame<EntityType, SetupSurfaceRasteredGame, Entity>
{
    /** Unknown entity error message. */
    private static final String UNKNOWN_ENTITY_ERROR = "Unknown entity: ";

    /**
     * Create an item from its type.
     * 
     * @param level The level reference.
     * @param type The item type.
     * @param factory The factory class.
     * @return The item instance.
     */
    public static Entity createEntity(Level level, EntityType type, Class<?> factory)
    {
        try
        {
            final StringBuilder clazz = new StringBuilder(factory.getPackage().getName());
            clazz.append('.').append(type.asClassName());
            return (Entity) Class.forName(clazz.toString()).getConstructor(Level.class).newInstance(level);
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
            throw new LionEngineException(exception, FactoryEntity.UNKNOWN_ENTITY_ERROR + type.asClassName());
        }
    }

    /** Level used. */
    private Level level;
    /** World used. */
    private WorldType world;
    /** Landscape used. */
    private LandscapeType landscape;

    /**
     * Standard constructor.
     */
    public FactoryEntity()
    {
        super(EntityType.class);
    }

    /**
     * Set the context.
     * 
     * @param level The level reference.
     */
    public void setLevel(Level level)
    {
        this.level = level;
    }

    /**
     * Set the world type used.
     * 
     * @param world The world used.
     */
    public void setWorld(WorldType world)
    {
        this.world = world;
    }

    /**
     * Set the landscape type used.
     * 
     * @param landscape The landscape type used.
     */
    public void setLandscape(LandscapeType landscape)
    {
        this.landscape = landscape;
    }

    /**
     * Create a new valdyn.
     * 
     * @return The instance of valdyn.
     */
    public Valdyn createValdyn()
    {
        return new Valdyn(level);
    }

    /*
     * FactoryEntityGame
     */

    @Override
    public Entity createEntity(EntityType type)
    {
        switch (type.getCategory())
        {
            case ITEM:
                return FactoryEntityItem.createItem(level, type);
            case MONSTER:
                return FactoryEntityMonster.createMonster(level, type);
            case SCENERY:
                return FactoryEntityScenery.createScenery(level, type);
            default:
                throw new LionEngineException(FactoryEntity.UNKNOWN_ENTITY_ERROR + type);
        }
    }

    @Override
    protected SetupSurfaceRasteredGame createSetup(EntityType id)
    {
        final String pathBase = Media.getPath(AppLionheart.ENTITIES_DIR, id.getCategory().getFolder());
        final String configExtension = AppLionheart.CONFIG_FILE_EXTENSION;
        final String path;
        if (id == EntityType.VALDYN)
        {
            path = Media.getPath(pathBase, id.asPathName() + configExtension);
        }
        else
        {
            path = Media.getPath(pathBase, world.asPathName(), id.asPathName() + configExtension);
        }

        final Media raster;
        if (AppLionheart.RASTER_ENABLED && id != EntityType.VALDYN)
        {
            raster = Media.get(AppLionheart.RASTERS_DIR, landscape.getRaster());
        }
        else
        {
            raster = null;
        }
        return new SetupSurfaceRasteredGame(Media.get(path), raster, false);
    }
}
