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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Localizable;
import com.b3dgs.lionengine.Transparency;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.core.Core;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.ImageBuffer;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.Verbose;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.SpriteTiled;
import com.b3dgs.lionengine.game.Axis;
import com.b3dgs.lionengine.game.collision.CollisionCategory;
import com.b3dgs.lionengine.game.collision.CollisionConstraint;
import com.b3dgs.lionengine.game.collision.CollisionFormula;
import com.b3dgs.lionengine.game.collision.CollisionFunction;
import com.b3dgs.lionengine.game.collision.CollisionGroup;
import com.b3dgs.lionengine.game.collision.CollisionRange;
import com.b3dgs.lionengine.game.collision.CollisionResult;
import com.b3dgs.lionengine.game.configurer.ConfigCollisionFormula;
import com.b3dgs.lionengine.game.configurer.ConfigCollisionGroup;
import com.b3dgs.lionengine.game.trait.Transformable;
import com.b3dgs.lionengine.game.utility.LevelRipConverter;
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
    /** Info loading patterns. */
    private static final String INFO_LOAD_PATTERNS = "Loading patterns from: ";
    /** Info loading formulas. */
    private static final String INFO_LOAD_FORMULAS = "Loading collision formulas from: ";
    /** Info loading groups. */
    private static final String INFO_LOAD_GROUPS = "Loading collision groups from: ";
    /** Error pattern missing message. */
    private static final String ERROR_PATTERN_MISSING = "Pattern missing: ";
    /** Error create tile message. */
    private static final String ERROR_CREATE_TILE = "Invalid tile creation: ";
    /** Error formula not found. */
    private static final String ERROR_FORMULA = "Formula not found (may not have been loaded): ";

    /** Patterns list. */
    private final Map<Integer, SpriteTiled> patterns;
    /** Collision formulas list. */
    private final Map<String, CollisionFormula> formulas;
    /** Collisions groups list. */
    private final Map<String, CollisionGroup> groups;
    /** Tiles directory. */
    private Media patternsDirectory;
    /** Tile width. */
    private int tileWidth;
    /** Tile height. */
    private int tileHeight;
    /** Number of horizontal tiles. */
    private int widthInTile;
    /** Number of vertical tiles. */
    private int heightInTile;
    /** Minimap reference. */
    private ImageBuffer minimap;
    /** Tiles map. */
    private List<List<T>> tiles;
    /** Collision draw cache. */
    private HashMap<CollisionFormula, ImageBuffer> collisionCache;

    /**
     * Constructor base.
     * 
     * @param tileWidth The tile width (must be strictly positive).
     * @param tileHeight The tile height (must be strictly positive).
     */
    public MapTileGame(int tileWidth, int tileHeight)
    {
        Check.superiorStrict(tileWidth, 0);
        Check.superiorStrict(tileHeight, 0);

        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        patterns = new HashMap<>();
        formulas = new HashMap<>();
        groups = new HashMap<>();
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
        final SpriteTiled sprite = getPattern(pattern);
        sprite.setLocation(x, y);
        sprite.setTile(number);
        sprite.render(g);
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
     * @throws LionEngineException If error on creating tile.
     */
    protected T loadTile(Collection<XmlNode> nodes, FileReading file, int i) throws IOException, LionEngineException
    {
        Check.notNull(file);

        final int pattern = file.readInteger();
        final int number = file.readInteger();
        final int x = file.readInteger() * tileWidth + i * MapTile.BLOC_SIZE * getTileWidth();
        final int y = file.readInteger() * tileHeight;
        final T tile = createTile();

        if (tile == null)
        {
            throw new LionEngineException(ERROR_CREATE_TILE, "pattern=" + pattern, " | number=" + number);
        }
        tile.setPattern(Integer.valueOf(pattern));
        tile.setNumber(number);
        tile.setX(x);
        tile.setY(y);

        return tile;
    }

    /**
     * Get color corresponding to the specified tile. Override it to return a specific color for each type of tile.
     * This function is used when generating the minimap.
     * 
     * @param tile The input tile.
     * @return The color representing the tile on minimap.
     * @see #createMiniMap()
     */
    protected ColorRgba getTilePixelColor(T tile)
    {
        return ColorRgba.WHITE;
    }

    /**
     * Create the function draw to buffer.
     * 
     * @param collision The collision reference.
     * @return The created collision representation buffer.
     */
    private ImageBuffer createFunctionDraw(CollisionFormula collision)
    {
        final ImageBuffer buffer = Core.GRAPHIC.createImageBuffer(tileWidth + 2, tileHeight + 2,
                Transparency.TRANSLUCENT);
        final Graphic g = buffer.createGraphic();
        g.setColor(ColorRgba.PURPLE);

        createFunctionDraw(g, collision);

        g.dispose();
        return buffer;
    }

    /**
     * Create the function draw to buffer.
     * 
     * @param g The graphic buffer.
     * @param formula The collision formula to draw.
     */
    private void createFunctionDraw(Graphic g, CollisionFormula formula)
    {
        final CollisionFunction function = formula.getFunction();
        final CollisionRange range = formula.getRange();

        for (int x = 0; x < tileWidth; x++)
        {
            for (int y = 0; y < tileHeight; y++)
            {
                switch (range.getOutput())
                {
                    case X:
                        final double fx = function.compute(x);
                        if (UtilMath.isBetween(x, range.getMinX(), range.getMaxX()))
                        {
                            g.drawRect((int) fx + 1, tileHeight - y, 0, 0, false);
                        }
                        break;
                    case Y:
                        final double fy = function.compute(y);
                        if (UtilMath.isBetween(y, range.getMinY(), range.getMaxY()))
                        {
                            g.drawRect(x + 1, tileHeight - (int) fy, 0, 0, false);
                        }
                        break;
                    default:
                        throw new RuntimeException("Unknown type: " + range.getOutput());
                }
            }
        }
    }

    /**
     * Load the collision formula. All previous collisions will be cleared.
     * 
     * @param collisionFormulas The configuration collision formulas file.
     */
    private void loadCollisionFormulas(Media collisionFormulas)
    {
        Verbose.info(INFO_LOAD_FORMULAS, collisionFormulas.getFile().getPath());
        removeCollisionFormulas();
        final XmlNode nodeFormulas = Stream.loadXml(collisionFormulas);
        final ConfigCollisionFormula config = ConfigCollisionFormula.create(nodeFormulas);
        for (final CollisionFormula formula : config.getFormulas().values())
        {
            addCollisionFormula(formula);
        }
        config.clear();
    }

    /**
     * Load the collision groups. All previous groups will be cleared.
     * 
     * @param collisionGroups The configuration collision groups file.
     */
    private void loadCollisionGroups(Media collisionGroups)
    {
        Verbose.info(INFO_LOAD_GROUPS, collisionGroups.getFile().getPath());
        removeCollisionGroups();
        final XmlNode nodeGroups = Stream.loadXml(collisionGroups);
        final Collection<CollisionGroup> groups = ConfigCollisionGroup.create(nodeGroups, this);
        for (final CollisionGroup group : groups)
        {
            addCollisionGroup(group);
        }
        groups.clear();
    }

    /**
     * Load collisions for each tile. Previous collisions will be removed.
     */
    private void loadTilesCollisions()
    {
        for (int v = 0; v < heightInTile; v++)
        {
            final List<T> list = tiles.get(v);
            for (int h = 0; h < widthInTile; h++)
            {
                final T tile = list.get(h);
                if (tile != null)
                {
                    tile.removeCollisionFormulas();
                    addTileCollisions(tile);
                }
            }
        }
    }

    /**
     * Add the tile collisions from loaded configuration.
     * 
     * @param tile The tile reference.
     */
    private void addTileCollisions(T tile)
    {
        final int pattern = tile.getPattern().intValue();
        final int number = tile.getNumber();
        for (final CollisionGroup group : getCollisionGroups())
        {
            if (group.getPattern() == pattern && UtilMath.isBetween(number, group.getStart(), group.getEnd()))
            {
                tile.setGroup(group.getName());
                for (final CollisionFormula formula : group.getFormulas())
                {
                    tile.addCollisionFormula(formula);
                }
            }
        }
    }

    /**
     * Apply tile constraints depending of their adjacent collisions.
     */
    private void applyConstraints()
    {
        final Map<T, Collection<CollisionFormula>> toRemove = new HashMap<>();
        for (int v = 0; v < heightInTile; v++)
        {
            final List<T> list = tiles.get(v);
            for (int h = 0; h < widthInTile; h++)
            {
                final T tile = list.get(h);
                if (tile != null)
                {
                    toRemove.put(tile, checkConstraints(tile, h, v));
                }
            }
        }
        for (final Entry<T, Collection<CollisionFormula>> current : toRemove.entrySet())
        {
            final T tile = current.getKey();
            for (final CollisionFormula formula : current.getValue())
            {
                tile.removeCollisionFormula(formula);
            }
        }
    }

    /**
     * Check the tile constraints and get the removable formulas.
     * 
     * @param tile The current tile to check.
     * @param h The horizontal location.
     * @param v The vertical location.
     * @return The formula to remove.
     */
    private Collection<CollisionFormula> checkConstraints(T tile, int h, int v)
    {
        final T top = getTile(h, v + 1);
        final T bottom = getTile(h, v - 1);
        final T left = getTile(h - 1, v);
        final T right = getTile(h + 1, v);

        final Collection<CollisionFormula> toRemove = new ArrayList<>();
        for (final CollisionFormula formula : tile.getCollisionFormulas())
        {
            final CollisionConstraint constraint = formula.getConstraint();
            if (constraint.getTop() != null && top != null && !top.getCollisionFormulas().isEmpty()
                    || constraint.getBottom() != null && bottom != null && !bottom.getCollisionFormulas().isEmpty()
                    || constraint.getLeft() != null && left != null && !left.getCollisionFormulas().isEmpty()
                    || constraint.getRight() != null && right != null && !right.getCollisionFormulas().isEmpty())
            {
                toRemove.add(formula);
            }
        }
        return toRemove;
    }

    /**
     * Compute the collision from current location.
     * 
     * @param category The collision category.
     * @param ox The old horizontal location.
     * @param oy The old vertical location.
     * @param x The current horizontal location.
     * @param y The current vertical location.
     * @return The computed collision result.
     */
    private CollisionResult<T> computeCollision(CollisionCategory category, double ox, double oy, double x, double y)
    {
        final T tile = getTile((int) Math.floor(x / getTileWidth()), (int) Math.floor(y / getTileHeight()));
        if (tile != null && containsCollisionFormula(tile, category))
        {
            final Double cx = category.getAxis() == Axis.X ? tile.getCollisionX(category, ox, oy, x, y) : null;
            final Double cy = category.getAxis() == Axis.Y ? tile.getCollisionY(category, ox, oy, x, y) : null;

            return new CollisionResult<>(cx, cy, tile);
        }
        return null;
    }

    /**
     * Check if tile contains at least one collision from the category.
     * 
     * @param tile The tile reference.
     * @param category The category reference.
     * @return <code>true</code> if there is a formula in common between tile and category.
     */
    private boolean containsCollisionFormula(T tile, CollisionCategory category)
    {
        final Collection<CollisionFormula> formulas = tile.getCollisionFormulas();
        for (final CollisionFormula formula : category.getFormulas())
        {
            if (formulas.contains(formula))
            {
                return true;
            }
        }
        return false;
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
        for (final CollisionFormula collision : tile.getCollisionFormulas())
        {
            final ImageBuffer buffer = collisionCache.get(collision);
            if (buffer != null)
            {
                // x - 1 because collision result is outside tile area
                g.drawImage(buffer, x - 1, y);
            }
        }
    }

    /*
     * MapTile
     */

    @Override
    public void create(int widthInTile, int heightInTile)
    {
        Check.superiorStrict(widthInTile, 0);
        Check.superiorStrict(heightInTile, 0);

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
        collisionCache = new HashMap<>(formulas.size());

        for (final CollisionFormula collision : formulas.values())
        {
            final ImageBuffer buffer = createFunctionDraw(collision);
            collisionCache.put(collision, buffer);
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

        final Media tileCollisionformulas = Core.MEDIA.create(patternsDirectory.getPath(), MapTile.FORMULAS_FILE_NAME);
        final Media tileCollisions = Core.MEDIA.create(patternsDirectory.getPath(), MapTile.GROUPS_FILE_NAME);
        final XmlNode root = Stream.loadXml(tileCollisions);
        final Collection<XmlNode> nodes = root.getChildren();

        final int t = file.readShort();
        for (int v = 0; v < t; v++)
        {
            final int n = file.readShort();
            for (int h = 0; h < n; h++)
            {
                final T tile = loadTile(nodes, file, v);
                if (tile.getPattern().intValue() > getNumberPatterns())
                {
                    throw new LionEngineException(tileCollisions, ERROR_PATTERN_MISSING, tile.getPattern().toString());
                }
                final int th = tile.getX() / getTileWidth();
                final int tv = tile.getY() / getTileHeight();
                final List<T> list = tiles.get(tv);
                list.set(th, tile);
            }
        }
        loadCollisions(tileCollisionformulas, tileCollisions);
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
    public void load(Media levelrip, Media patternsDirectory) throws LionEngineException
    {
        clear();
        final LevelRipConverter<T> rip = new LevelRipConverter<>(levelrip, patternsDirectory, this);
        rip.start();
        this.patternsDirectory = patternsDirectory;

        final Media tileCollisionformulas = Core.MEDIA.create(patternsDirectory.getPath(), MapTile.FORMULAS_FILE_NAME);
        final Media tileCollisions = Core.MEDIA.create(patternsDirectory.getPath(), MapTile.GROUPS_FILE_NAME);
        loadCollisions(tileCollisionformulas, tileCollisions);
    }

    @Override
    public void loadPatterns(Media directory) throws LionEngineException
    {
        Verbose.info(INFO_LOAD_PATTERNS, directory.getFile().getPath());
        patternsDirectory = directory;
        patterns.clear();
        String[] files;

        // Retrieve patterns list
        final Media mediaPatterns = Core.MEDIA.create(patternsDirectory.getPath(), MapTile.SHEETS_FILE_NAME);
        final XmlNode root = Stream.loadXml(mediaPatterns);
        final Collection<XmlNode> children = root.getChildren(MapTile.NODE_TILE_SHEET);
        files = new String[children.size()];
        int i = 0;
        for (final XmlNode child : children)
        {
            files[i] = child.getText();
            i++;
        }

        // Load patterns from list
        for (int pattern = 0; pattern < files.length; pattern++)
        {
            final Media media = Core.MEDIA.create(patternsDirectory.getPath(), files[pattern]);
            final SpriteTiled sprite = Drawable.loadSpriteTiled(media, tileWidth, tileHeight);
            sprite.load(false);
            patterns.put(Integer.valueOf(pattern), sprite);
        }
    }

    @Override
    public void loadCollisions(Media collisionFormulas, Media collisionGroups) throws LionEngineException
    {
        if (collisionFormulas.exists())
        {
            loadCollisionFormulas(collisionFormulas);
        }
        if (collisionGroups.exists())
        {
            loadCollisionGroups(collisionGroups);
        }
        loadTilesCollisions();
        applyConstraints();
    }

    @Override
    public void saveCollisions() throws LionEngineException
    {
        final Media formulas = Core.MEDIA.create(patternsDirectory.getPath(), MapTile.FORMULAS_FILE_NAME);
        final XmlNode formulasRoot = Stream.createXmlNode(ConfigCollisionFormula.FORMULAS);
        for (final CollisionFormula formula : getCollisionFormulas())
        {
            formulasRoot.add(ConfigCollisionFormula.export(formula));
        }
        Stream.saveXml(formulasRoot, formulas);

        final Media groups = Core.MEDIA.create(patternsDirectory.getPath(), MapTile.GROUPS_FILE_NAME);
        final XmlNode groupsNode = Stream.createXmlNode(ConfigCollisionGroup.GROUPS);
        for (final CollisionGroup group : getCollisionGroups())
        {
            groupsNode.add(ConfigCollisionGroup.export(group));
        }
        Stream.saveXml(groupsNode, groups);
    }

    @Override
    public void append(MapTile<T> map, int offsetX, int offsetY)
    {
        Check.notNull(map);

        final int newWidth = widthInTile - (widthInTile - offsetX) + map.getWidthInTile();
        final int newHeight = heightInTile - (heightInTile - offsetY) + map.getHeightInTile();

        // Adjust height
        final int sizeV = tiles.size();
        for (int v = 0; v < newHeight - sizeV; v++)
        {
            tiles.add(new ArrayList<T>(newWidth));
        }

        for (int v = 0; v < map.getHeightInTile(); v++)
        {
            final int y = offsetY + v;

            // Adjust width
            final int sizeH = tiles.get(y).size();
            for (int h = 0; h < newWidth - sizeH; h++)
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
    public void addCollisionFormula(CollisionFormula formula)
    {
        formulas.put(formula.getName(), formula);
    }

    @Override
    public void addCollisionGroup(CollisionGroup group)
    {
        groups.put(group.getName(), group);
    }

    @Override
    public void removeCollisionFormula(CollisionFormula formula)
    {
        formulas.remove(formula.getName());
    }

    @Override
    public void removeCollisionGroup(CollisionGroup group)
    {
        groups.remove(group.getName());
    }

    @Override
    public void removeCollisionFormulas()
    {
        formulas.clear();
    }

    @Override
    public void removeCollisionGroups()
    {
        groups.clear();
    }

    @Override
    public CollisionResult<T> computeCollision(Transformable transformable, CollisionCategory category)
    {
        // Distance calculation
        final double sh = transformable.getOldX() + category.getOffsetX();
        final double sv = transformable.getOldY() + category.getOffsetY();

        final double dh = transformable.getX() + category.getOffsetX() - sh;
        final double dv = transformable.getY() + category.getOffsetY() - sv;

        // Search vector and number of search steps
        final double norm = Math.sqrt(dh * dh + dv * dv);
        final double sx = dh / norm;
        final double sy = dv / norm;

        double oh;
        double ov;
        int count = 0;
        for (double h = sh, v = sv; count < norm; count++)
        {
            oh = Math.floor(h);
            ov = Math.floor(v);
            h += sx;
            CollisionResult<T> result = computeCollision(category, oh, ov, h, v);
            if (result != null)
            {
                return result;
            }

            v += sy;
            result = computeCollision(category, oh, ov, h, v);
            if (result != null)
            {
                return result;
            }
        }
        return null;
    }

    @Override
    public void render(Graphic g, Viewer viewer)
    {
        render(g, viewer.getHeight(), (int) viewer.getX(), (int) viewer.getY(),
                (int) Math.ceil(viewer.getWidth() / (double) tileWidth),
                (int) Math.ceil(viewer.getHeight() / (double) tileHeight), -viewer.getViewX(), viewer.getViewY());
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
    public T getTile(Localizable localizable, int offsetX, int offsetY)
    {
        final int tx = (int) Math.floor((localizable.getX() + offsetX) / getTileWidth());
        final int ty = (int) Math.floor((localizable.getY() + offsetY) / getTileHeight());
        return getTile(tx, ty);
    }

    @Override
    public int getInTileX(Localizable localizable)
    {
        return (int) Math.floor(localizable.getX() / getTileWidth());
    }

    @Override
    public int getInTileY(Localizable localizable)
    {
        return (int) Math.floor(localizable.getY() / getTileHeight());
    }

    @Override
    public Media getPatternsDirectory()
    {
        return patternsDirectory;
    }

    @Override
    public Collection<Integer> getPatterns()
    {
        return patterns.keySet();
    }

    @Override
    public SpriteTiled getPattern(Integer pattern) throws LionEngineException
    {
        if (!patterns.containsKey(pattern))
        {
            throw new LionEngineException(ERROR_PATTERN_MISSING, pattern.toString());
        }
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
    public CollisionFormula getCollisionFormula(String name)
    {
        if (formulas.containsKey(name))
        {
            return formulas.get(name);
        }
        throw new LionEngineException(ERROR_FORMULA, name);
    }

    @Override
    public CollisionGroup getCollisionGroup(String name)
    {
        if (groups.containsKey(name))
        {
            return groups.get(name);
        }
        throw new LionEngineException(ERROR_FORMULA, name);
    }

    @Override
    public Collection<CollisionFormula> getCollisionFormulas()
    {
        return formulas.values();
    }

    @Override
    public Collection<CollisionGroup> getCollisionGroups()
    {
        return groups.values();
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
