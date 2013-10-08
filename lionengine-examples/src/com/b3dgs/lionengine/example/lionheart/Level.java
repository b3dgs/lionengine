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
package com.b3dgs.lionengine.example.lionheart;

import java.io.IOException;

import com.b3dgs.lionengine.example.lionheart.effect.EffectType;
import com.b3dgs.lionengine.example.lionheart.effect.FactoryEffect;
import com.b3dgs.lionengine.example.lionheart.effect.HandlerEffect;
import com.b3dgs.lionengine.example.lionheart.entity.EntityType;
import com.b3dgs.lionengine.example.lionheart.entity.FactoryEntity;
import com.b3dgs.lionengine.example.lionheart.entity.HandlerEntity;
import com.b3dgs.lionengine.example.lionheart.landscape.LandscapeType;
import com.b3dgs.lionengine.example.lionheart.map.Map;
import com.b3dgs.lionengine.file.FileReading;
import com.b3dgs.lionengine.file.FileWriting;
import com.b3dgs.lionengine.game.platform.CameraPlatform;

/**
 * Represents a level and its data (world data, map, entities).
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class Level
{
    /** Level file format. */
    public static final String FILE_FORMAT = "lrm";
    /** Camera reference. */
    public final CameraPlatform camera;
    /** Map reference. */
    public final Map map;
    /** World data reference. */
    public final WorldData worldData;
    /** Entity factory reference. */
    public final FactoryEntity factoryEntity;
    /** Factory effect reference. */
    public final FactoryEffect factoryEffect;
    /** Entity handler reference. */
    public final HandlerEntity handlerEntity;
    /** Handler effect reference. */
    public final HandlerEffect handlerEffect;
    /** Desired fps value. */
    public final int desiredFps;
    /** World type. */
    private WorldType world;
    /** Landscape type. */
    private LandscapeType landscape;

    /**
     * Constructor.
     * 
     * @param camera The camera reference.
     * @param factoryEntity The entity factory reference.
     * @param handlerEntity The entity handler reference.
     * @param desiredFps The desired fps value.
     */
    public Level(CameraPlatform camera, FactoryEntity factoryEntity, HandlerEntity handlerEntity, int desiredFps)
    {
        this.camera = camera;
        this.factoryEntity = factoryEntity;
        this.handlerEntity = handlerEntity;
        handlerEffect = new HandlerEffect(camera);
        factoryEffect = new FactoryEffect(handlerEffect);
        map = new Map();
        worldData = new WorldData(map);
        this.desiredFps = desiredFps;
    }

    /**
     * Save a level to a file.
     * 
     * @param file The file to save level to.
     * @throws IOException If error.
     */
    public void save(FileWriting file) throws IOException
    {
        file.writeString(Level.FILE_FORMAT);
        world.save(file);
        landscape.save(file);
        map.save(file);
        worldData.save(file);
        handlerEntity.save(file);
    }

    /**
     * Load a level from a file.
     * 
     * @param file The level file.
     * @throws IOException If error.
     */
    public void load(FileReading file) throws IOException
    {
        final String format = file.readString();
        if (!Level.FILE_FORMAT.equals(format))
        {
            throw new IOException("Invalid level format !");
        }
        setWorld(WorldType.load(file));
        setLandscape(LandscapeType.load(file));
        factoryEntity.loadAll(EntityType.values());
        factoryEffect.loadAll(EffectType.values());
        map.load(file);
        worldData.load(file);
        handlerEntity.load(file);
        camera.setLimits(map);
    }

    /**
     * Set the world type.
     * 
     * @param world The world type.
     */
    public void setWorld(WorldType world)
    {
        this.world = world;
        factoryEntity.setWorld(world);
    }

    /**
     * Set the landscape type.
     * 
     * @param landscape The landscape type.
     */
    public void setLandscape(LandscapeType landscape)
    {
        this.landscape = landscape;
        factoryEntity.setLandscape(landscape);
        factoryEffect.setLandscape(landscape);
        map.setLandscape(landscape);
    }

    /**
     * Get the current world used.
     * 
     * @return The world type used.
     */
    public WorldType getWorld()
    {
        return world;
    }

    /**
     * Get the current landscape type used.
     * 
     * @return The landscape type used.
     */
    public LandscapeType getLandscape()
    {
        return landscape;
    }
}
