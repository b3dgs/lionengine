package com.b3dgs.lionengine.example.c_platform.e_lionheart.map;

import java.io.IOException;
import java.util.List;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.AppLionheart;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.landscape.LandscapeType;
import com.b3dgs.lionengine.file.FileReading;
import com.b3dgs.lionengine.file.FileWriting;
import com.b3dgs.lionengine.file.XmlNode;
import com.b3dgs.lionengine.game.map.MapTileGame;
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
    private static final boolean RASTER_ENABLED = true;

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
    public Tile createTile(int width, int height, Integer pattern, int number, TileCollision collision)
    {
        final TileCollisionGroup group = collision != null ? collision.getGroup() : TileCollisionGroup.NONE;
        switch (group)
        {
            case FLAT:
                return new TileGround(width, height, pattern, number, collision);
            case LIANA_HORIZONTAL:
            case LIANA_STEEP:
            case LIANA_LEANING:
                return new TileLiana(width, height, pattern, number, collision);
            case SLOPE:
                return new TileSlope(width, height, pattern, number, collision);
            case SLIDE:
                return new TileSlide(width, height, pattern, number, collision);
            default:
                return new Tile(width, height, pattern, number, collision);
        }
    }

    @Override
    public TileCollision getCollisionFrom(String collision)
    {
        try
        {
            return TileCollision.valueOf(collision);
        }
        catch (final IllegalArgumentException
                     | NullPointerException exception)
        {
            return TileCollision.NONE;
        }
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
    public Tile loadTile(List<XmlNode> nodes, FileReading file, int i) throws IOException
    {
        final int pattern = Map.fromByte(file.readByte());
        final int number = Map.fromByte(file.readByte());
        final int x = Map.fromByte(file.readByte());
        final int y = Map.fromByte(file.readByte());
        final TileCollision collision = getCollisionFrom(getCollision(nodes, pattern, number));
        final Tile tile = createTile(tileWidth, tileHeight, Integer.valueOf(pattern), number, collision);

        tile.setX((x + i * MapTileGame.BLOC_SIZE) * tileWidth);
        tile.setY(y * tileHeight);

        return tile;
    }
}
