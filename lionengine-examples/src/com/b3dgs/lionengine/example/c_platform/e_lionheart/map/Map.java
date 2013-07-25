package com.b3dgs.lionengine.example.c_platform.e_lionheart.map;

import java.io.IOException;

import com.b3dgs.lionengine.file.FileReading;
import com.b3dgs.lionengine.file.FileWriting;
import com.b3dgs.lionengine.game.maptile.MapTileGame;
import com.b3dgs.lionengine.game.platform.map.MapTilePlatformRastered;

/**
 * Map implementation
 */
public class Map
        extends MapTilePlatformRastered<TileCollision, Tile>
{
    /** Sheets directory. */
    public static final String TILES_DIR = "tiles";
    /** Tile width. */
    public static final int TILE_WIDTH = 16;
    /** Tile height. */
    public static final int TILE_HEIGHT = 16;

    /**
     * Standard constructor.
     */
    public Map()
    {
        super(Map.TILE_WIDTH, Map.TILE_HEIGHT);
    }

    @Override
    public Tile createTile(int width, int height)
    {
        return new Tile(width, height);
    }

    @Override
    protected void saveTile(FileWriting file, Tile tile) throws IOException
    {
        file.writeByte(toByte(tile.getPattern().intValue()));
        file.writeByte(toByte(tile.getNumber()));
        file.writeByte(toByte((tile.getX() / tileWidth) % MapTileGame.BLOC_SIZE));
        file.writeByte(toByte(tile.getY() / tileHeight));
    }

    @Override
    public Tile loadTile(FileReading file, int i) throws IOException
    {
        final int pattern = fromByte(file.readByte());
        final int number = fromByte(file.readByte());
        final int x = fromByte(file.readByte());
        final int y = fromByte(file.readByte());
        final Tile tile = createTile(tileWidth, tileHeight);

        tile.setCollision(tile.getCollisionFrom(null, null));
        tile.setPattern(Integer.valueOf(pattern));
        tile.setNumber(number);
        tile.setX((x + i * MapTileGame.BLOC_SIZE) * tileWidth);
        tile.setY(y * tileHeight);

        return tile;
    }

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
}
