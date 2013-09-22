package com.b3dgs.lionengine.example.c_platform.e_lionheart.map;

import java.io.IOException;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.AppLionheart;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.landscape.LandscapeType;
import com.b3dgs.lionengine.file.FileReading;
import com.b3dgs.lionengine.file.FileWriting;
import com.b3dgs.lionengine.game.maptile.MapTileGame;
import com.b3dgs.lionengine.game.platform.map.MapTilePlatformRastered;

/**
 * Map implementation.
 */
public class Map
        extends MapTilePlatformRastered<TileCollision, Tile>
{
    /** Tile width. */
    public static final int TILE_WIDTH = 16;
    /** Tile height. */
    public static final int TILE_HEIGHT = 16;
    /** Raster enabled. */
    private static final boolean RASTER_ENABLED = false;

    /**
     * Convert int to byte value (working on if int is less than 256).
     * 
     * @param value The int value.
     * @return The byte value.
     */
    private static byte toByte(int value)
    {
        return (byte) (value + Byte.MIN_VALUE);
    }

    /**
     * Convert byte to int value.
     * 
     * @param value The byte value.
     * @return The int value.
     */
    private static int fromByte(byte value)
    {
        return value - Byte.MIN_VALUE;
    }

    /**
     * Constructor.
     */
    public Map()
    {
        super(Map.TILE_WIDTH, Map.TILE_HEIGHT);
    }

    /**
     * Set the map landscape (must be called before loading).
     * 
     * @param landscape The landscape type.
     */
    public void setLandscape(LandscapeType landscape)
    {
        if (Map.RASTER_ENABLED)
        {
            setRaster(Media.get(AppLionheart.RASTERS_DIR, landscape.getRaster()), false, false);
        }
    }

    /*
     * MapTilePlatformRastered
     */

    @Override
    public Tile createTile(int width, int height)
    {
        return new Tile(width, height);
    }

    @Override
    protected void saveTile(FileWriting file, Tile tile) throws IOException
    {
        file.writeByte(Map.toByte(tile.getPattern().intValue()));
        file.writeByte(Map.toByte(tile.getNumber()));
        file.writeByte(Map.toByte(tile.getX() / tileWidth % MapTileGame.BLOC_SIZE));
        file.writeByte(Map.toByte(tile.getY() / tileHeight));
    }

    @Override
    public Tile loadTile(FileReading file, int i) throws IOException
    {
        final int pattern = Map.fromByte(file.readByte());
        final int number = Map.fromByte(file.readByte());
        final int x = Map.fromByte(file.readByte());
        final int y = Map.fromByte(file.readByte());
        final Tile tile = createTile(tileWidth, tileHeight);

        tile.setCollision(tile.getCollisionFrom(null));
        tile.setPattern(Integer.valueOf(pattern));
        tile.setNumber(number);
        tile.setX((x + i * MapTileGame.BLOC_SIZE) * tileWidth);
        tile.setY(y * tileHeight);

        return tile;
    }
}
