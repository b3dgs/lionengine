package com.b3dgs.lionengine.example.c_platform.e_lionheart;

import java.io.IOException;
import java.util.Collection;
import java.util.TreeMap;

import com.b3dgs.lionengine.example.c_platform.e_lionheart.map.Map;
import com.b3dgs.lionengine.file.FileReading;
import com.b3dgs.lionengine.file.FileWriting;
import com.b3dgs.lionengine.game.Coord;
import com.b3dgs.lionengine.game.CoordTile;

/**
 * Represents the data of a world (starting & ending location, checkpoints...)
 */
public class WorldData
{
    /** Map reference. */
    private final Map map;
    /** Player starting location. */
    private final Coord playerStart;
    /** Player ending location. */
    private final Coord playerEnd;
    /** Checkpoints list. */
    private final TreeMap<Integer, CoordTile> checkpoints;

    /**
     * Constructor.
     * 
     * @param map The map reference.
     */
    public WorldData(Map map)
    {
        this.map = map;
        playerStart = new Coord(-Map.TILE_WIDTH, -Map.TILE_HEIGHT);
        playerEnd = new Coord(-Map.TILE_WIDTH, -Map.TILE_HEIGHT);
        checkpoints = new TreeMap<>();
    }

    /**
     * Save to an existing file.
     * 
     * @param file The level file.
     * @throws IOException If error.
     */
    public void save(FileWriting file) throws IOException
    {
        file.writeShort((short) (playerStart.getX() / Map.TILE_WIDTH));
        file.writeShort((short) (playerStart.getY() / Map.TILE_HEIGHT));
        file.writeShort((short) (playerEnd.getX() / Map.TILE_WIDTH));
        file.writeShort((short) (playerEnd.getY() / Map.TILE_HEIGHT));
        file.writeShort((short) checkpoints.size());
        for (final CoordTile p : checkpoints.values())
        {
            file.writeShort((short) (p.getX() / Map.TILE_WIDTH));
            file.writeShort((short) (p.getY() / Map.TILE_HEIGHT));
        }
    }

    /**
     * Load from an existing file.
     * 
     * @param file The level file.
     * @throws IOException If error.
     */
    public void load(FileReading file) throws IOException
    {
        playerStart.set(file.readShort() * Map.TILE_WIDTH, file.readShort() * Map.TILE_HEIGHT);
        playerEnd.set(file.readShort() * Map.TILE_WIDTH, file.readShort() * Map.TILE_HEIGHT);
        final int size = file.readShort();
        for (int i = 0; i < size; i++)
        {
            addCheckpoint(file.readShort() * Map.TILE_WIDTH, file.readShort() * Map.TILE_HEIGHT);
        }
    }

    /**
     * Set the starting location.
     * 
     * @param tx The horizontal starting location.
     * @param ty The vertical starting location.
     */
    public void setStarting(int tx, int ty)
    {
        playerStart.set(tx, ty);
    }

    /**
     * Set the ending location.
     * 
     * @param tx The horizontal starting location.
     * @param ty The vertical starting location.
     */
    public void setEnding(int tx, int ty)
    {
        playerEnd.set(tx, ty);
    }

    /**
     * Add a checkpoint at the specified location.
     * 
     * @param x The horizontal location.
     * @param y The vertical location
     */
    public void addCheckpoint(int x, int y)
    {
        checkpoints.put(getHash(x, y), new CoordTile(x, y));
    }

    /**
     * Remove a checkpoint.
     * 
     * @param checkpoint The checkpoint to remove.
     */
    public void removeCheckpoint(CoordTile checkpoint)
    {
        if (checkpoint != null)
        {
            checkpoints.remove(getHash(checkpoint.getX(), checkpoint.getY()));
        }
    }

    /**
     * Get the starting horizontal location.
     * 
     * @return The starting horizontal location.
     */
    public int getStartX()
    {
        return (int) playerStart.getX();
    }

    /**
     * Get the starting vertical location.
     * 
     * @return The starting vertical location.
     */
    public int getStartY()
    {
        return (int) playerStart.getY();
    }

    /**
     * Get the ending horizontal location.
     * 
     * @return The ending horizontal location.
     */
    public int getEndX()
    {
        return (int) playerEnd.getX();
    }

    /**
     * Get the ending vertical location.
     * 
     * @return The ending vertical location.
     */
    public int getEndY()
    {
        return (int) playerEnd.getY();
    }

    /**
     * Get the checkpoints list.
     * 
     * @return The checkpoints list.
     */
    public Collection<CoordTile> getCheckpoints()
    {
        return checkpoints.values();
    }

    /**
     * Get the checkpoint at the specified location.
     * 
     * @param x The horizontal location.
     * @param y The vertical location
     * @return The checkpoint reference.
     */
    public CoordTile getCheckpointAt(int x, int y)
    {
        return checkpoints.get(getHash(x, y));
    }

    /**
     * Get the hash value of a location.
     * 
     * @param x The horizontal location.
     * @param y The vertical location
     * @return The hash value.
     */
    public Integer getHash(int x, int y)
    {
        return Integer.valueOf(x / Map.TILE_WIDTH + map.getWidthInTile() * (y / Map.TILE_HEIGHT));
    }
}
