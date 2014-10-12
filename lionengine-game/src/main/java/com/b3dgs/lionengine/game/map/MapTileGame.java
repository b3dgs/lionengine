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
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Transparency;
import com.b3dgs.lionengine.core.Core;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.ImageBuffer;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.SpriteTiled;
import com.b3dgs.lionengine.game.CameraGame;
import com.b3dgs.lionengine.game.purview.Localizable;
import com.b3dgs.lionengine.game.utility.LevelRipConverter;
import com.b3dgs.lionengine.game.utility.UtilMapTile;
import com.b3dgs.lionengine.stream.FileReading;
import com.b3dgs.lionengine.stream.FileWriting;
import com.b3dgs.lionengine.stream.Stream;
import com.b3dgs.lionengine.stream.XmlNode;

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
 * @param <T> The tile type used.
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see TileGame
 */
public abstract class MapTileGame<T extends TileGame>
        implements MapTile<T>
{
    /** Error pattern number message. */
    private static final String ERROR_PATTERN_NUMBER = "Error on getting pattern number (should be a name with a number only) !";

    /** Collisions. */
    private final CollisionTile[] collisions;
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
    /** Tiles map. */
    private List<List<T>> tiles;
    /** Collision draw cache. */
    private HashMap<CollisionTile, ImageBuffer> collisionCache;

    /**
     * Constructor.
     * 
     * @param tileWidth The tile width.
     * @param tileHeight The tile height.
     * @param collisions The collisions list.
     */
    public MapTileGame(int tileWidth, int tileHeight, CollisionTile[] collisions)
    {
        this.collisions = collisions;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        patterns = new HashMap<>();
        patternsDirectory = null;
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
                            final int x = tile.getX() - sx;
                            final int y = -tile.getY() - tile.getHeight() + sy + screenHeight;
                            renderTile(g, tile, x, y);
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
     * @param x The location x.
     * @param y The location y.
     * @param tile The tile to render.
     */
    protected void renderTile(Graphic g, T tile, int x, int y)
    {
        renderingTile(g, tile, tile.getPattern(), tile.getNumber(), x, y);
        if (collisionCache != null)
        {
            renderCollision(g, tile, x, y);
        }
    }

    /**
     * Render a specific tile to specified location.
     * 
     * @param g The graphic output.
     * @param tile The tile to render.
     * @param pattern The tile pattern.
     * @param number The tile number.
     * @param x The location x.
     * @param y The location y.
     */
    protected void renderingTile(Graphic g, T tile, Integer pattern, int number, int x, int y)
    {
        getPattern(pattern).render(g, number, x, y);
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
        file.writeInteger(tile.getX() / tileWidth % MapTile.BLOC_SIZE);
        file.writeInteger(tile.getY() / tileHeight);
    }

    /**
     * Get color corresponding to the specified tile. Override it to return a specific color for each type of tile.
     * This function is used when generating the minimap.
     * 
     * @param tile The input tile.
     * @return The color representing the tile on minimap.
     */
    protected ColorRgba getTilePixelColor(T tile)
    {
        return ColorRgba.WHITE;
    }

    /**
     * Check the collision at the specified location.
     * 
     * @param localizable The localizable reference.
     * @param collisions Collisions list to search for.
     * @param h The horizontal index.
     * @param ty The vertical tile.
     * @param applyRayCast <code>true</code> to apply collision to each tile crossed, <code>false</code> to ignore and
     *            just return the first tile hit.
     * @return The first tile hit, <code>null</code> if none found.
     */
    private T checkCollision(Localizable localizable, Set<CollisionTile> collisions, double h, int ty,
            boolean applyRayCast)
    {
        final T tile = getTile((int) Math.floor(h / getTileWidth()), ty);
        if (tile != null && collisions.contains(tile.getCollision()))
        {
            final Double coll = tile.getCollisionY(localizable);
            if (applyRayCast && coll != null)
            {
                localizable.teleportY(coll.doubleValue());
            }
            return tile;
        }
        return null;
    }

    /**
     * Render the collision function.
     * 
     * @param g The graphic output.
     * @param tile The tile reference.
     * @param x The horizontal render location.
     * @param y The vertical render location.
     */
    private void renderCollision(Graphic g, T tile, int x, int y)
    {
        final ImageBuffer buffer = collisionCache.get(tile.getCollision());
        if (buffer != null)
        {
            g.drawImage(buffer, x, y);
        }
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
    public void createCollisionDraw()
    {
        clearCollisionDraw();
        collisionCache = new HashMap<>(collisions.length);

        for (final CollisionTile collision : collisions)
        {
            final Set<CollisionFunction> functions = collision.getCollisionFunctions();
            if (functions != null)
            {
                final ImageBuffer buffer = UtilMapTile.createFunctionDraw(functions, this);
                collisionCache.put(collision, buffer);
            }
        }
    }

    @Override
    public void createMiniMap()
    {
        if (minimap == null)
        {
            minimap = Core.GRAPHIC.createImageBuffer(getWidthInTile(), getHeightInTile(), Transparency.OPAQUE);
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

    @Override
    public void load(FileReading file) throws IOException, LionEngineException
    {
        patternsDirectory = Core.MEDIA.create(file.readString());
        final int width = file.readShort();
        final int height = file.readShort();
        tileWidth = file.readByte();
        tileHeight = file.readByte();

        create(width, height);
        loadPatterns(patternsDirectory);

        final Media media = Core.MEDIA.create(patternsDirectory.getPath(), MapTile.COLLISIONS_FILE_NAME);
        final XmlNode root = Stream.loadXml(media);
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

    @Override
    public void load(Media levelrip, Media patternsDirectory) throws LionEngineException
    {
        clear();
        final LevelRipConverter<T> rip = new LevelRipConverter<>();
        rip.start(levelrip, patternsDirectory, this);
        this.patternsDirectory = patternsDirectory;
        loadCollisions(Core.MEDIA.create(patternsDirectory.getPath(), MapTile.COLLISIONS_FILE_NAME));
    }

    @Override
    public void loadPatterns(Media directory) throws LionEngineException
    {
        patternsDirectory = directory;
        patterns.clear();
        String[] files;

        // Retrieve patterns list
        final Media mediaPatterns = Core.MEDIA.create(patternsDirectory.getPath(), MapTile.TILE_SHEETS_FILE_NAME);
        final XmlNode root = Stream.loadXml(mediaPatterns);
        final List<XmlNode> children = root.getChildren(MapTile.NODE_TILE_SHEET);
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
            final Media media = Core.MEDIA.create(patternsDirectory.getPath(), file);
            try
            {
                final Integer pattern = Integer.valueOf(file.substring(0, file.length() - 4));
                final SpriteTiled sprite = Drawable.loadSpriteTiled(media, tileWidth, tileHeight);

                sprite.load(false);
                patterns.put(pattern, sprite);
            }
            catch (final NumberFormatException exception)
            {
                throw new LionEngineException(exception, media, MapTileGame.ERROR_PATTERN_NUMBER);
            }
        }
    }

    @Override
    public void loadCollisions(Media media) throws LionEngineException
    {
        final XmlNode root = Stream.loadXml(media);
        final List<XmlNode> collisions = root.getChildren();
        for (int i = 0; i < heightInTile; i++)
        {
            final List<T> list = tiles.get(i);
            for (int j = 0; j < widthInTile; j++)
            {
                final T tile = list.get(j);
                if (tile != null)
                {
                    tile.setCollision(getCollisionFrom(UtilMapTile.getCollision(collisions, tile.getPattern()
                            .intValue(), tile.getNumber())));
                }
            }
        }
        for (final XmlNode node : collisions)
        {
            final CollisionTile collision = getCollisionFrom(node.readString(UtilMapTile.ATT_TILE_COLLISION_NAME));
            for (final XmlNode functionNode : node.getChildren(UtilMapTile.TAG_FUNCTION))
            {
                final CollisionFunction function = UtilMapTile.getCollisionFunction(collision, functionNode);
                assignCollisionFunction(collision, function);
            }
        }
    }

    @Override
    public T loadTile(List<XmlNode> nodes, FileReading file, int i) throws IOException
    {
        final int pattern = file.readInteger();
        final int number = file.readInteger();
        final int x = file.readInteger() * tileWidth + i * MapTile.BLOC_SIZE * getTileWidth();
        final int y = file.readInteger() * tileHeight;
        final CollisionTile collision = getCollisionFrom(UtilMapTile.getCollision(nodes, pattern, number));
        final T tile = createTile(tileWidth, tileHeight, Integer.valueOf(pattern), number, collision);

        tile.setX(x);
        tile.setY(y);

        return tile;
    }

    @Override
    public void append(MapTile<T> map, int offsetX, int offsetY)
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
        if (tiles != null)
        {
            for (int v = 0; v < tiles.size(); v++)
            {
                final List<T> list = tiles.get(v);
                if (list != null)
                {
                    list.clear();
                }
            }
            tiles.clear();
        }
    }

    @Override
    public void clearCollisionDraw()
    {
        if (collisionCache != null)
        {
            for (final ImageBuffer buffer : collisionCache.values())
            {
                buffer.dispose();
            }
            collisionCache.clear();
            collisionCache = null;
        }
    }

    @Override
    public void assignCollisionFunction(CollisionTile collision, CollisionFunction function)
    {
        collision.addCollisionFunction(function);
    }

    @Override
    public void removeCollisionFunction(CollisionFunction function)
    {
        for (final CollisionTile collision : collisions)
        {
            collision.removeCollisionFunction(function);
        }
    }

    @Override
    public void saveCollisions()
    {
        final Media media = Core.MEDIA.create(patternsDirectory.getPath(), MapTile.COLLISIONS_FILE_NAME);
        UtilMapTile.saveCollisions(this, media);
    }

    @Override
    public void save(FileWriting file) throws IOException
    {
        // Header
        file.writeString(patternsDirectory.getPath());
        file.writeShort((short) widthInTile);
        file.writeShort((short) heightInTile);
        file.writeByte((byte) tileWidth);
        file.writeByte((byte) tileHeight);

        final int step = MapTile.BLOC_SIZE;
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

    @Override
    public void render(Graphic g, CameraGame camera)
    {
        render(g, camera.getViewHeight(), camera.getLocationIntX(), camera.getLocationIntY(),
                (int) Math.ceil(camera.getViewWidth() / (double) tileWidth),
                (int) Math.ceil(camera.getViewHeight() / (double) tileHeight), -camera.getViewX(), camera.getViewY());
    }

    @Override
    public void renderMiniMap(Graphic g, int x, int y)
    {
        g.drawImage(minimap, x, y);
    }

    @Override
    public void setTile(int v, int h, T tile)
    {
        tile.setX(h * tileWidth);
        tile.setY(v * tileHeight);
        tiles.get(v).set(h, tile);
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
    public T getTile(Localizable entity, int offsetX, int offsetY)
    {
        final int tx = (entity.getLocationIntX() + offsetX) / getTileWidth();
        final int ty = (entity.getLocationIntY() + offsetY) / getTileHeight();
        return getTile(tx, ty);
    }

    @Override
    public T getFirstTileHit(Localizable localizable, Set<CollisionTile> collisions, boolean applyRayCast)
    {
        // Starting location
        final int sv = (int) Math.floor(localizable.getLocationOldY());
        final int sh = (int) Math.floor(localizable.getLocationOldX());

        // Ending location
        final int ev = (int) Math.floor(localizable.getLocationY());
        final int eh = (int) Math.floor(localizable.getLocationX());

        // Distance calculation
        final int dv = ev - sv;
        final int dh = eh - sh;

        // Search vector and number of search steps
        final double sx, sy;
        final int stepMax;
        if (Math.abs(dv) >= Math.abs(dh))
        {
            sy = UtilMapTile.getTileSearchSpeed(dv);
            sx = UtilMapTile.getTileSearchSpeed(Math.abs(dv), dh);
            stepMax = Math.abs(dv);
        }
        else
        {
            sx = UtilMapTile.getTileSearchSpeed(dh);
            sy = UtilMapTile.getTileSearchSpeed(Math.abs(dh), dv);
            stepMax = Math.abs(dh);
        }

        T t = null;
        int step = 0;
        for (double v = sv, h = sh; step <= stepMax;)
        {
            T tile = checkCollision(localizable, collisions, h, (int) Math.floor(v / getTileHeight()), applyRayCast);
            if (t == null)
            {
                t = tile;
            }
            h += sx;

            tile = checkCollision(localizable, collisions, h, (int) Math.ceil(v / getTileHeight()), applyRayCast);
            if (t == null)
            {
                t = tile;
            }
            v += sy;

            step++;
            if (!applyRayCast && t != null)
            {
                return t;
            }
        }
        return t;
    }

    @Override
    public int getInTileX(Localizable localizable)
    {
        return (int) Math.floor(localizable.getLocationX() / getTileWidth());
    }

    @Override
    public int getInTileY(Localizable localizable)
    {
        return (int) Math.floor(localizable.getLocationY() / getTileHeight());
    }

    @Override
    public Media getPatternsDirectory()
    {
        return patternsDirectory;
    }

    @Override
    public Set<Integer> getPatterns()
    {
        return patterns.keySet();
    }

    @Override
    public SpriteTiled getPattern(Integer pattern)
    {
        return patterns.get(pattern);
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

    @Override
    public CollisionTile[] getCollisions()
    {
        return collisions;
    }

    @Override
    public ImageBuffer getMiniMap()
    {
        return minimap;
    }

    @Override
    public boolean isCreated()
    {
        return tiles != null;
    }
}
