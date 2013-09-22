package com.b3dgs.lionengine.example.c_platform.e_lionheart;

import java.io.IOException;

import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.EntityType;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.FactoryEntity;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.HandlerEntity;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.landscape.LandscapeType;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.map.Map;
import com.b3dgs.lionengine.file.FileReading;
import com.b3dgs.lionengine.file.FileWriting;

/**
 * Represents a level and its data (world data, map, entities).
 */
public class Level
{
    /** Level file format. */
    public static final String FILE_FORMAT = "lrm";
    /** Map reference. */
    public final Map map;
    /** World data reference. */
    public final WorldData worldData;
    /** Entity factory reference. */
    public final FactoryEntity factoryEntity;
    /** Entity handler reference. */
    public final HandlerEntity handlerEntity;
    /** World type. */
    private WorldType world;
    /** Landscape type. */
    private LandscapeType landscape;

    /**
     * Constructor.
     * 
     * @param factoryEntity The entity factory reference.
     * @param handlerEntity The entity handler reference.
     */
    public Level(FactoryEntity factoryEntity, HandlerEntity handlerEntity)
    {
        this.factoryEntity = factoryEntity;
        this.handlerEntity = handlerEntity;
        map = new Map();
        worldData = new WorldData(map);
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
        file.writeByte((byte) world.getIndex());
        file.writeByte((byte) landscape.getIndex());
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
        setWorld(WorldType.get(file.readByte()));
        factoryEntity.loadAll(EntityType.values());
        setLandscape(LandscapeType.get(file.readByte()));
        map.setLandscape(getLandscape());
        map.load(file);
        worldData.load(file);
        handlerEntity.load(file);
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
