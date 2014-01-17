/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.map;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Transparency;
import com.b3dgs.lionengine.core.ImageBuffer;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.UtilityImage;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.SpriteTiled;
import com.b3dgs.lionengine.file.File;
import com.b3dgs.lionengine.file.FileReading;
import com.b3dgs.lionengine.file.FileWriting;
import com.b3dgs.lionengine.file.XmlNode;
import com.b3dgs.lionengine.file.XmlParser;
import com.b3dgs.lionengine.game.CameraGame;

/**
 * Abstract representation of a standard tile based map. This class uses a List of List to store tiles, a TreeMap to
 * store patterns references (SpriteTiled), and collisions.
 * <p>
 * The way to prepare a map is the following:
 * </p>
 * 
 * <pre>
 * {@link #create(int, int)} // prepare memory to store tiles
 * {@link #loadPatterns(Media)} // load tilesheet
 * </pre>
 * 
 * A simple call to {@link #load(FileReading)} will automatically perform theses operations.
 * 
 * @param <C> The collision type used.
 * @param <T> The tile type used.
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see TileGame
 */
public abstract class MapTileGame<C extends Enum<C>, T extends TileGame<C>>
        implements MapTile<C, T>
{
    /** Number of horizontal tiles to make a bloc. */
    public static final int BLOC_SIZE = 256;

    /** Patterns list. */
    private final Map<Integer, SpriteTiled> patterns;
    /** Tiles directory. */
    protected Media patternsDirectory;
    /** Tile width. */
    protected int tileWidth;
    /** Tile height. */
    protected int tileHeight;
    /** Number of horizontal tiles. */
    protected int widthInTile;
    /** Number of vertical tiles. */
    protected int heightInTile;
    /** Minimap reference. */
    protected ImageBuffer minimap;
    /** Loaded flag. */
    private boolean surfacesLoaded;
    /** Tiles map. */
    private List<List<T>> tiles;

    /**
     * Constructor.
     * 
     * @param tileWidth The tile width.
     * @param tileHeight The tile height.
     */
    public MapTileGame(int tileWidth, int tileHeight)
    {
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        patterns = new HashMap<>();
        patternsDirectory = null;
        surfacesLoaded = false;
    }

    /**
     * Create a tile.
     * 
     * @param width The tile width.
     * @param height The tile height.
     * @param pattern The tile pattern.
     * @param number The tile number.
     * @param collision The tile collision.
     * @return The created tile.
     */
    public abstract T createTile(int width, int height, Integer pattern, int number, C collision);

    /**
     * Get collision type from its name as string. The parameter value is read from the file describing the map
     * collisions. The best way to store map collisions name is to use an enum with the same names.
     * 
     * @param collision The collision name.
     * @return The collision type.
     */
    public abstract C getCollisionFrom(String collision);

    /**
     * Generate the minimap from the current map.
     */
    public void createMiniMap()
    {
        if (minimap == null)
        {
            minimap = UtilityImage.createImageBuffer(getWidthInTile(), getHeightInTile(), Transparency.OPAQUE);
        }
        final Graphic g = minimap.createGraphic();
        final int vert = getHeightInTile();
        final int hori = getWidthInTile();

        for (int v = 0; v < vert; v++)
        {
            for (int h = 0; h < hori; h++)
            {
                final T tile = getTile(h, v);
                if (tile != null)
                {
                    g.setColor(getTilePixelColor(tile));
                }
                else
                {
                    g.setColor(ColorRgba.BLACK);
                }
                g.drawRect(h, vert - v - 1, 1, 1, true);
            }
        }
        g.dispose();
    }

    /**
     * Save map to specified file as binary data. Data are saved this way (using specific types to save space):
     * 
     * <pre>
     * <code>(String)</code> theme
     * <code>(short)</code> width in tiles
     * <code>(short)</code> height in tiles
     * <code>(byte)</code> tile width (use of byte because tile width &lt; 255)
     * <code>(byte)</code> tile height (use of byte because tile height &lt; 255)
     * <code>(short)</code> number of 256 horizontal blocs (widthInTile / 256)
     * for each blocs tile
     *   <code>(short)</code> number of tiles in this bloc
     *   for each tile in this bloc
     *     call tile.save(file)
     * </pre>
     * 
     * Collisions are not saved, because it is possible to retrieve them from collisions.txt
     * 
     * @param file The output file.
     * @throws IOException If error on writing.
     */
    public void save(FileWriting file) throws IOException
    {
        // Header
        file.writeString(patternsDirectory.getPath());
        file.writeShort((short) widthInTile);
        file.writeShort((short) heightInTile);
        file.writeByte((byte) tileWidth);
        file.writeByte((byte) tileHeight);

        final int step = MapTileGame.BLOC_SIZE;
        final int x = Math.min(step, widthInTile);
        final int t = (int) Math.ceil(widthInTile / (double) step);

        file.writeShort((short) t);
        for (int s = 0; s < t; s++)
        {
            int count = 0;
            for (int h = 0; h < x; h++)
            {
                for (int v = 0; v < heightInTile; v++)
                {
                    if (getTile(h + s * step, v) != null)
                    {
                        count++;
                    }
                }
            }
            file.writeShort((short) count);
            for (int h = 0; h < x; h++)
            {
                for (int v = 0; v < heightInTile; v++)
                {
                    final T tile = getTile(h + s * step, v);
                    if (tile != null)
                    {
                        saveTile(file, tile);
                    }
                }
            }
        }
    }

    /**
     * Load a map from a specified file as binary data.
     * <p>
     * Data are loaded this way (see save(file) order):
     * </p>
     * 
     * <pre>
     * <code>(String)</code> theme
     * <code>(short)</code> width in tiles
     * <code>(short)</code> height in tiles
     * <code>(byte)</code> tile width
     * <code>(byte)</code> tile height
     * <code>(short)</code> number of 256 horizontal blocs (widthInTile / 256)
     * for each blocs tile
     *   <code>(short)</code> number of tiles in this bloc
     *   for each tile in this bloc
     *     create blank tile
     *     call tile.load(file)
     *     call this.setTile(...) to update map with this new tile
     * </pre>
     * 
     * @param file The input file.
     * @throws IOException If error on reading.
     */
    public void load(FileReading file) throws IOException
    {
        patternsDirectory = Media.create(file.readString());
        final int width = file.readShort();
        final int height = file.readShort();
        tileWidth = file.readByte();
        tileHeight = file.readByte();

        create(width, height);
        loadPatterns(patternsDirectory);

        final Media media = Media.create(Media.getPath(patternsDirectory.getPath(), "collisions.xml"));
        final XmlParser xml = File.createXmlParser();
        final XmlNode root = xml.load(media);
        final List<XmlNode> nodes = root.getChildren();

        final int t = file.readShort();
        for (int i = 0; i < t; i++)
        {
            final int n = file.readShort();
            for (int j = 0; j < n; j++)
            {
                final T tile = loadTile(nodes, file, i);
                final int v = tile.getY() / getTileHeight();
                final int h = tile.getX() / getTileWidth();
                final List<T> list = tiles.get(v);
                list.set(h, tile);
            }
        }
        loadCollisions(media);
    }

    /**
     * Load map collision from an external file.
     * 
     * @param media The collision container.
     */
    public void loadCollisions(Media media)
    {
        final XmlParser xml = File.createXmlParser();
        final XmlNode root = xml.load(media);
        final List<XmlNode> nodes = root.getChildren();
        for (int i = 0; i < heightInTile; i++)
        {
            final List<T> list = tiles.get(i);
            for (int j = 0; j < widthInTile; j++)
            {
                final T tile = list.get(j);
                if (tile != null)
                {
                    tile.setCollision(getCollisionFrom(getCollision(nodes, tile.getPattern().intValue(),
                            tile.getNumber())));
                }
            }
        }
    }

    /**
     * Load tile. Data are loaded this way:
     * 
     * <pre>
     * (integer) pattern number
     * (integer) index number inside pattern
     * (integer) tile location x
     * (integer tile location y
     * </pre>
     * 
     * @param nodes The collision nodes.
     * @param file The file reader reference.
     * @param i The last loaded tile number.
     * @return The loaded tile.
     * @throws IOException If error on reading.
     */
    public T loadTile(List<XmlNode> nodes, FileReading file, int i) throws IOException
    {
        final int pattern = file.readInteger();
        final int number = file.readInteger();
        final int x = file.readInteger() * tileWidth + i * MapTileGame.BLOC_SIZE * getTileWidth();
        final int y = file.readInteger() * tileHeight;
        final C collision = getCollisionFrom(getCollision(nodes, pattern, number));
        final T tile = createTile(tileWidth, tileHeight, Integer.valueOf(pattern), number, collision);

        tile.setX(x);
        tile.setY(y);

        return tile;
    }

    /**
     * Render minimap on graphic output at specified location.
     * 
     * @param g The graphic output.
     * @param x The location x.
     * @param y The location y.
     */
    public void renderMiniMap(Graphic g, int x, int y)
    {
        g.drawImage(minimap, x, y);
    }

    /**
     * Set a tile at specified map indexes.
     * 
     * @param v The vertical index.
     * @param h The horizontal index.
     * @param tile The tile reference.
     */
    public void setTile(int v, int h, T tile)
    {
        tile.setX(h * tileWidth);
        tile.setY(v * tileHeight);
        tiles.get(v).set(h, tile);
    }

    /**
     * Get pattern (tilesheet) from its id.
     * 
     * @param pattern The pattern id.
     * @return The pattern found.
     */
    public SpriteTiled getPattern(Integer pattern)
    {
        return patterns.get(pattern);
    }

    /**
     * Get list of patterns id.
     * 
     * @return The set of patterns id.
     */
    public Set<Integer> getPatterns()
    {
        return patterns.keySet();
    }

    /**
     * Get map theme.
     * 
     * @return The map tiles directory.
     */
    public Media getPatternsDirectory()
    {
        return patternsDirectory;
    }

    /**
     * Get minimap surface reference.
     * 
     * @return The minimap surface reference.
     */
    public ImageBuffer getMiniMap()
    {
        return minimap;
    }

    /**
     * Read collisions from external file.
     * 
     * @param nodes The collision nodes.
     * @param tilePattern The tile pattern number.
     * @param tileNumber The tile number.
     * @return The collision found.
     */
    protected String getCollision(List<XmlNode> nodes, int tilePattern, int tileNumber)
    {
        for (final XmlNode node : nodes)
        {
            final String name = node.readString("name");
            final int pattern = node.readInteger("pattern");
            final int start = node.readInteger("start");
            final int end = node.readInteger("end");

            if (tilePattern == pattern)
            {
                if (tileNumber + 1 >= start && tileNumber + 1 <= end)
                {
                    return name;
                }
            }
        }
        return null;
    }

    /**
     * Render map from starting position, showing a specified area, including a specific offset.
     * 
     * @param g The graphic output.
     * @param screenHeight The view height (rendering start from bottom).
     * @param sx The starting x (view real location x).
     * @param sy The starting y (view real location y).
     * @param inTileWidth The number of rendered tiles in width.
     * @param inTileHeight The number of rendered tiles in height.
     * @param offsetX The horizontal map offset.
     * @param offsetY The vertical map offset.
     */
    protected void render(Graphic g, int screenHeight, int sx, int sy, int inTileWidth, int inTileHeight, int offsetX,
            int offsetY)
    {
        // Each vertical tiles
        for (int v = 0; v <= inTileHeight; v++)
        {
            final int ty = v + (sy - offsetY) / tileHeight;
            if (!(ty < 0 || ty >= heightInTile))
            {
                // Each horizontal tiles
                for (int h = 0; h <= inTileWidth; h++)
                {
                    final int tx = h + (sx - offsetX) / tileWidth;
                    if (!(tx < 0 || tx >= widthInTile))
                    {
                        // Get the tile and render it
                        final T tile = getTile(tx, ty);
                        if (tile != null)
                        {
                            renderTile(g, screenHeight, sx, sy, tile);
                        }
                    }
                }
            }
        }
    }

    /**
     * Render tile on its designed location (automatically called by
     * {@link #render(Graphic, int, int, int, int, int, int, int)}).
     * 
     * @param g The graphic output.
     * @param screenHeight The view height (rendering start from bottom).
     * @param sx The starting x (view real location x).
     * @param sy The starting y (view real location y).
     * @param tile The tile to render.
     */
    protected void renderTile(Graphic g, int screenHeight, int sx, int sy, T tile)
    {
        final int x = tile.getX() - sx;
        final int y = -tile.getY() - tile.getHeight() + sy;
        renderingTile(g, tile, x, y, screenHeight);
    }

    /**
     * Render a specific tile from specified location.
     * 
     * @param g The graphic output.
     * @param tile The tile to render.
     * @param x The location x.
     * @param y The location y.
     * @param screenHeight The view height (rendering start from bottom).
     */
    protected void renderingTile(Graphic g, T tile, int x, int y, int screenHeight)
    {
        getPattern(tile.getPattern()).render(g, tile.getNumber(), x, y + screenHeight);
    }

    /**
     * Save tile. Data are saved this way:
     * 
     * <pre>
     * (integer) pattern number
     * (integer) index number inside pattern
     * (integer) tile location x % AbstractMapTile.BLOC_SIZE
     * (integer tile location y
     * </pre>
     * 
     * @param file The file writer reference.
     * @param tile The tile to save.
     * @throws IOException If error on writing.
     */
    protected void saveTile(FileWriting file, T tile) throws IOException
    {
        file.writeInteger(tile.getPattern().intValue());
        file.writeInteger(tile.getNumber());
        file.writeInteger(tile.getX() / tileWidth % MapTileGame.BLOC_SIZE);
        file.writeInteger(tile.getY() / tileHeight);
    }

    /**
     * Get color corresponding to the specified tile. Override it to return a specific color for each type of tile. This
     * function is used when generating the minimap.
     * 
     * @param tile The input tile.
     * @return The color representing the tile on minimap.
     */
    protected ColorRgba getTilePixelColor(T tile)
    {
        return ColorRgba.WHITE;
    }

    /*
     * MapTile
     */

    @Override
    public void create(int widthInTile, int heightInTile)
    {
        this.widthInTile = widthInTile;
        this.heightInTile = heightInTile;
        tiles = new ArrayList<>(heightInTile);

        for (int v = 0; v < heightInTile; v++)
        {
            tiles.add(v, new ArrayList<T>(widthInTile));
            for (int h = 0; h < widthInTile; h++)
            {
                tiles.get(v).add(h, null);
            }
        }
    }

    @Override
    public void append(MapTile<C, T> map, int offsetX, int offsetY)
    {
        final int newWidth = widthInTile - (widthInTile - offsetX) + map.getWidthInTile();
        final int newHeight = heightInTile - (heightInTile - offsetY) + map.getHeightInTile();

        // Adjust height
        final int sizeV = tiles.size();
        for (int i = 0; i < newHeight - sizeV; i++)
        {
            tiles.add(new ArrayList<T>(newWidth));
        }

        for (int v = 0; v < map.getHeightInTile(); v++)
        {
            final int y = offsetY + v;

            // Adjust width
            final int sizeH = tiles.get(y).size();
            for (int i = 0; i < newWidth - sizeH; i++)
            {
                tiles.get(y).add(null);
            }

            for (int h = 0; h < map.getWidthInTile(); h++)
            {
                final int x = offsetX + h;
                final T tile = map.getTile(h, v);
                if (tile != null)
                {
                    setTile(y, x, tile);
                }
            }
        }

        widthInTile = newWidth;
        heightInTile = newHeight;
    }

    @Override
    public void clear()
    {
        for (int v = 0; v < tiles.size(); v++)
        {
            tiles.get(v).clear();
        }
        tiles.clear();
    }

    @Override
    public void loadPatterns(Media directory)
    {
        if (!surfacesLoaded)
        {
            surfacesLoaded = true;
            patternsDirectory = directory;
            patterns.clear();
            String[] files;

            // Retrieve patterns list
            final XmlParser xml = File.createXmlParser();
            final Media mediaPatterns = Media.create(Media.getPath(patternsDirectory.getPath(), "patterns.xml"));
            final XmlNode root = xml.load(mediaPatterns);
            final List<XmlNode> children = root.getChildren();
            files = new String[children.size()];
            int i = 0;
            for (final XmlNode child : children)
            {
                files[i] = child.getText();
                i++;
            }

            // Load patterns from list
            for (final String file : files)
            {
                final Media media = Media.create(Media.getPath(patternsDirectory.getPath(), file));
                try
                {
                    final Integer pattern = Integer.valueOf(file.substring(0, file.length() - 4));
                    final SpriteTiled sprite = Drawable.loadSpriteTiled(media, tileWidth, tileHeight);

                    sprite.load(false);
                    patterns.put(pattern, sprite);
                }
                catch (final NumberFormatException exception)
                {
                    throw new LionEngineException(exception, media,
                            "Error on getting pattern number (should be a name with a number only) !");
                }
            }
        }
    }

    @Override
    public void render(Graphic g, CameraGame camera)
    {
        render(g, camera.getViewHeight(), camera.getLocationIntX(), camera.getLocationIntY(),
                (int) Math.ceil(camera.getViewWidth() / (double) tileWidth),
                (int) Math.ceil(camera.getViewHeight() / (double) tileHeight), -camera.getViewX(), camera.getViewY());
    }

    @Override
    public T getTile(int tx, int ty)
    {
        try
        {
            return tiles.get(ty).get(tx);
        }
        catch (final IndexOutOfBoundsException exception)
        {
            return null;
        }
    }

    @Override
    public int getNumberPatterns()
    {
        return patterns.size();
    }

    @Override
    public int getNumberTiles()
    {
        int n = 0;
        for (int v = 0; v < heightInTile; v++)
        {
            for (int h = 0; h < widthInTile; h++)
            {
                final T tile = getTile(h, v);
                if (tile != null)
                {
                    n++;
                }
            }
        }
        return n;
    }

    @Override
    public int getTileWidth()
    {
        return tileWidth;
    }

    @Override
    public int getTileHeight()
    {
        return tileHeight;
    }

    @Override
    public int getWidthInTile()
    {
        return widthInTile;
    }

    @Override
    public int getHeightInTile()
    {
        return heightInTile;
    }
}
