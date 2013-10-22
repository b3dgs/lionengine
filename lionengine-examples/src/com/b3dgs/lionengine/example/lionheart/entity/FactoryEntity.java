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
package com.b3dgs.lionengine.example.lionheart.entity;

import java.io.IOException;

import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.example.lionheart.AppLionheart;
import com.b3dgs.lionengine.example.lionheart.Level;
import com.b3dgs.lionengine.example.lionheart.WorldType;
import com.b3dgs.lionengine.example.lionheart.entity.player.EntityPlayerType;
import com.b3dgs.lionengine.example.lionheart.landscape.LandscapeType;
import com.b3dgs.lionengine.file.FileReading;
import com.b3dgs.lionengine.game.FactoryObjectGame;
import com.b3dgs.lionengine.game.SetupSurfaceRasteredGame;

/**
 * Handle the entity creation by containing all necessary object for their instantiation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @param <T> The entity type used.
 */
public abstract class FactoryEntity<T extends Enum<T> & EntityType<T>>
        extends FactoryObjectGame<T, SetupSurfaceRasteredGame, Entity>
{
    /** Unknown entity error message. */
    public static final String UNKNOWN_ENTITY_ERROR = "Unknown entity: ";

    /** Level used. */
    protected Level level;
    /** Landscape used. */
    protected LandscapeType landscape;

    /**
     * Constructor.
     * 
     * @param keyType The key type.
     * @param types The types.
     * @param world The world type.
     */
    protected FactoryEntity(Class<T> keyType, T[] types, WorldType world)
    {
        super(keyType, types, Media.getPath(AppLionheart.ENTITIES_DIR, world.asPathName()));
    }

    /**
     * Constructor.
     * 
     * @param keyType The key type.
     * @param types The types.
     * @param folder The folder name.
     */
    protected FactoryEntity(Class<T> keyType, T[] types, String folder)
    {
        super(keyType, types, Media.getPath(AppLionheart.ENTITIES_DIR, folder));
    }

    /**
     * Create an entity from a file loaded.
     * 
     * @param file The file loaded.
     * @return The created entity.
     * @throws IOException If error.
     */
    public abstract Entity createEntity(FileReading file) throws IOException;

    /**
     * Create an entity from a type.
     * 
     * @param type The type.
     * @return The created entity.
     */
    public abstract Entity createEntityFromType(String type);

    /**
     * Set the level used.
     * 
     * @param level The level used.
     */
    public void setLevel(Level level)
    {
        this.level = level;
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

    /*
     * FactoryEntityGame
     */

    @Override
    protected SetupSurfaceRasteredGame createSetup(T type, Media media)
    {
        final Media raster;
        if (AppLionheart.RASTER_ENABLED && type != EntityPlayerType.VALDYN)
        {
            raster = Media.get(AppLionheart.RASTERS_DIR, landscape.getRaster());
        }
        else
        {
            raster = null;
        }
        return new SetupSurfaceRasteredGame(media, raster, false);
    }
}
