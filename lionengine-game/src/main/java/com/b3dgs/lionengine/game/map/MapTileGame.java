/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.ImageBuffer;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Localizable;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.UtilReflection;
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.SpriteTiled;
import com.b3dgs.lionengine.game.Features;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.object.Services;
import com.b3dgs.lionengine.game.tile.Tile;
import com.b3dgs.lionengine.game.tile.TileGame;
import com.b3dgs.lionengine.game.tile.TilesExtractor;
import com.b3dgs.lionengine.stream.FileReading;
import com.b3dgs.lionengine.stream.FileWriting;

/**
 * Abstract representation of a standard tile based map. This class uses a List of List to store tiles, a TreeMap to
 * store sheets references ({@link SpriteTiled}), and collisions.
 * <p>
 * The way to prepare a map is the following:
 * </p>
 * 
 * <pre>
 * {@link #create(int, int)} // prepare memory to store tiles
 * {@link #loadSheets(Media)} // load tile sheets
 * </pre>
 * 
 * <p>
 * Or import a map from a level rip with {@link #create(Media, Media)}.
 * </p>
 * <p>
 * A simple call to {@link #load(FileReading)} will automatically perform theses operations.
 * </p>
 * 
 * @see Tile
 */
public class MapTileGame implements MapTile
{
    /** Error sheet missing message. */
    private static final String ERROR_SHEET_MISSING = "Sheet missing: ";
    /** Construction error. */
    private static final String ERROR_CONSTRUCTOR_MISSING = "No recognized constructor found for: ";

    /** Sheets list. */
    private final Map<Integer, SpriteTiled> sheets = new HashMap<Integer, SpriteTiled>();
    /** Features list. */
    private final Features<MapTileFeature> features = new Features<MapTileFeature>(MapTileFeature.class);
    /** Viewer reference. */
    private final Viewer viewer;
    /** Services reference. */
    private final Services services;
    /** Sheet configuration file. */
    private Media sheetsConfig;
    /** Tile width. */
    private int tileWidth;
    /** Tile height. */
    private int tileHeight;
    /** Number of horizontal tiles. */
    private int widthInTile;
    /** Number of vertical tiles. */
    private int heightInTile;
    /** Map radius. */
    private int radius;
    /** Tiles map. */
    private List<List<Tile>> tiles;
    /** Tile renderer. */
    private MapTileRenderer renderer;

    /**
     * Create a map tile. Rendering is not enable and must not be used ({@link #render(Graphic)}). Use
     * {@link #MapTileGame(Services)} instead if rendering will be used.
     */
    public MapTileGame()
    {
        viewer = null;
        services = null;
    }

    /**
     * Create a map tile.
     * <p>
     * The {@link Services} must provide the following services:
     * </p>
     * <ul>
     * <li>{@link Viewer}</li>
     * </ul>
     * 
     * @param services The services reference.
     * @throws LionEngineException If service not found.
     */
    public MapTileGame(Services services)
    {
        this.services = services;
        viewer = services.get(Viewer.class);
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
    protected void render(Graphic g,
                          int screenHeight,
                          int sx,
                          int sy,
                          int inTileWidth,
                          int inTileHeight,
                          int offsetX,
                          int offsetY)
    {
        for (int v = 0; v <= inTileHeight; v++)
        {
            final int ty = v + (sy - offsetY) / tileHeight;
            if (!(ty < 0 || ty >= heightInTile))
            {
                renderHorizontal(g, screenHeight, sx, sy, inTileWidth, offsetX, ty);
            }
        }
    }

    /**
     * Save tile. Data are saved this way:
     * 
     * <pre>
     * (integer) sheet number
     * (integer) index number inside sheet
     * (integer) tile location x % MapTile.BLOC_SIZE
     * (integer tile location y
     * </pre>
     * 
     * @param file The file writer reference.
     * @param tile The tile to save.
     * @throws IOException If error on writing.
     */
    protected void saveTile(FileWriting file, Tile tile) throws IOException
    {
        file.writeInteger(tile.getSheet().intValue());
        file.writeInteger(tile.getNumber());
        file.writeInteger(tile.getInTileX() % BLOC_SIZE);
        file.writeInteger(tile.getInTileY());
    }

    /**
     * Load tile. Data are loaded this way:
     * 
     * <pre>
     * (integer) sheet number
     * (integer) index number inside sheet
     * (integer) tile location x
     * (integer tile location y
     * </pre>
     * 
     * @param file The file reader reference.
     * @param i The last loaded tile number.
     * @return The loaded tile.
     * @throws IOException If error on reading.
     */
    protected Tile loadTile(FileReading file, int i) throws IOException
    {
        Check.notNull(file);

        final Integer sheet = Integer.valueOf(file.readInteger());
        final int number = file.readInteger();
        final int x = file.readInteger() * getTileWidth() + i * BLOC_SIZE * getTileWidth();
        final int y = file.readInteger() * getTileHeight();
        return createTile(sheet, number, x, y);
    }

    /**
     * Render horizontal tiles.
     * 
     * @param g The graphic output.
     * @param screenHeight The view height (rendering start from bottom).
     * @param sx The starting x (view real location x).
     * @param sy The starting y (view real location y).
     * @param inTileWidth The number of rendered tiles in width.
     * @param ty The current vertical tile.
     * @param offsetX The horizontal map offset.
     */
    private void renderHorizontal(Graphic g, int screenHeight, int sx, int sy, int inTileWidth, int offsetX, int ty)
    {
        for (int h = 0; h <= inTileWidth; h++)
        {
            final int tx = h + (sx - offsetX) / tileWidth;
            if (!(tx < 0 || tx >= widthInTile))
            {
                renderTile(g, tx, ty, sx, sy, screenHeight);
            }
        }
    }

    /**
     * Get the tile at location and render it.
     * 
     * @param g The graphic output.
     * @param tx The horizontal tile location.
     * @param ty The vertical tile location.
     * @param sx The starting horizontal location.
     * @param sy The starting vertical location.
     * @param screenHeight The view height (rendering start from bottom).
     */
    private void renderTile(Graphic g, int tx, int ty, int sx, int sy, int screenHeight)
    {
        final Tile tile = getTile(tx, ty);
        if (tile != null)
        {
            final int x = (int) tile.getX() - sx;
            final int y = -(int) tile.getY() - tile.getHeight() + sy + screenHeight;
            renderer.renderTile(g, tile, x, y);
        }
    }

    /**
     * Count the active tiles.
     * 
     * @param widthInTile The horizontal tiles.
     * @param step The step number.
     * @param s The s value.
     * @return The active tiles.
     */
    private int countTiles(int widthInTile, int step, int s)
    {
        int count = 0;
        for (int tx = 0; tx < widthInTile; tx++)
        {
            for (int ty = 0; ty < getInTileHeight(); ty++)
            {
                if (getTile(tx + s * step, ty) != null)
                {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Save the active tiles.
     * 
     * @param file The output file.
     * @param widthInTile The horizontal tiles.
     * @param step The step number.
     * @param s The s value.
     * @throws IOException If error on saving.
     */
    private void saveTiles(FileWriting file, int widthInTile, int step, int s) throws IOException
    {
        for (int tx = 0; tx < widthInTile; tx++)
        {
            for (int ty = 0; ty < getInTileHeight(); ty++)
            {
                final Tile tile = getTile(tx + s * step, ty);
                if (tile != null)
                {
                    saveTile(file, tile);
                }
            }
        }
    }

    /**
     * Resize map with new size.
     * 
     * @param newWidth The new width in tile.
     * @param newHeight The new height in tile.
     */
    private void resize(int newWidth, int newHeight)
    {
        final int oldWidth = widthInTile;
        final int oldheight = heightInTile;

        // Adjust height
        for (int v = 0; v < newHeight - oldheight; v++)
        {
            tiles.add(new ArrayList<Tile>(newWidth));
        }
        // Adjust width
        for (int v = 0; v < newHeight; v++)
        {
            final int width;
            if (v < oldheight)
            {
                width = newWidth - oldWidth;
            }
            else
            {
                width = newWidth;
            }
            for (int h = 0; h < width; h++)
            {
                tiles.get(v).add(null);
            }
        }

        widthInTile = newWidth;
        heightInTile = newHeight;
        radius = (int) Math.ceil(StrictMath.sqrt(newWidth * (double) newWidth + newHeight * (double) newHeight));
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
        radius = (int) Math.ceil(StrictMath.sqrt(widthInTile * widthInTile + heightInTile * (double) heightInTile));
        clear();
        tiles = new ArrayList<List<Tile>>(heightInTile);

        for (int v = 0; v < heightInTile; v++)
        {
            tiles.add(v, new ArrayList<Tile>(widthInTile));
            for (int h = 0; h < widthInTile; h++)
            {
                tiles.get(v).add(h, null);
            }
        }
        if (renderer == null)
        {
            renderer = this;
        }
    }

    @Override
    public Tile createTile(Integer sheet, int number, double x, double y)
    {
        return new TileGame(sheet, number, x, y, tileWidth, tileHeight);
    }

    @Override
    public void load(FileReading file) throws IOException
    {
        create(file.readInteger(), file.readInteger());
        if (file.readBoolean())
        {
            loadSheets(Medias.create(file.readString()));
        }
        else
        {
            tileWidth = file.readInteger();
            tileHeight = file.readInteger();
        }

        final int t = file.readShort();
        for (int v = 0; v < t; v++)
        {
            final int n = file.readShort();
            for (int h = 0; h < n; h++)
            {
                final Tile tile = loadTile(file, v);
                if (tile.getSheet().intValue() > getSheetsNumber())
                {
                    throw new IOException(ERROR_SHEET_MISSING + Constant.DOUBLE_DOT + tile.getSheet());
                }
                final int tx = tile.getInTileX();
                final int ty = tile.getInTileY();
                final List<Tile> list = tiles.get(ty);
                list.set(tx, tile);
            }
        }
    }

    @Override
    public void save(FileWriting file) throws IOException
    {
        // Header
        file.writeInteger(widthInTile);
        file.writeInteger(heightInTile);

        final boolean hasConfig = sheetsConfig != null;
        file.writeBoolean(hasConfig);
        if (hasConfig)
        {
            file.writeString(sheetsConfig.getPath());
        }
        else
        {
            file.writeInteger(tileWidth);
            file.writeInteger(tileHeight);
        }

        final int step = BLOC_SIZE;
        final int x = Math.min(step, widthInTile);
        final int t = (int) Math.ceil(widthInTile / (double) step);

        file.writeShort((short) t);
        for (int s = 0; s < t; s++)
        {
            final int count = countTiles(x, step, s);
            file.writeShort((short) count);
            saveTiles(file, Math.min(widthInTile, BLOC_SIZE), step, s);
        }
    }

    @Override
    public void create(Media levelrip, int tileWidth, int tileHeight, int horizontalTiles)
    {
        final TilesExtractor tilesExtractor = new TilesExtractor();
        final Collection<ImageBuffer> tiles = tilesExtractor.extract(tileWidth, tileHeight, levelrip);
        loadSheets(tileWidth, tileHeight, SheetsExtractor.extract(tiles, horizontalTiles));

        for (final ImageBuffer tile : tiles)
        {
            tile.dispose();
        }

        LevelRipConverter.start(levelrip, this);
    }

    @Override
    public void create(Media levelrip)
    {
        create(levelrip, Medias.create(levelrip.getParentPath(), TileSheetsConfig.FILENAME));
    }

    @Override
    public void create(Media levelrip, Media sheetsConfig)
    {
        clear();

        loadSheets(sheetsConfig);
        final int errors = LevelRipConverter.start(levelrip, this);
        if (errors > 0)
        {
            Verbose.warning(getClass(), "create", "Number of missing tiles: ", String.valueOf(errors));
        }
        this.sheetsConfig = sheetsConfig;
    }

    @Override
    public <F extends MapTileFeature> F createFeature(Class<F> feature)
    {
        try
        {
            final F instance = UtilReflection.create(feature, new Class<?>[]
            {
                Services.class
            }, services);
            services.add(instance);
            addFeature(instance);
            return instance;
        }
        catch (final NoSuchMethodException exception)
        {
            try
            {
                final F instance = UtilReflection.create(feature, new Class<?>[0]);
                services.add(instance);
                addFeature(instance);
                return instance;
            }
            catch (final NoSuchMethodException exception2)
            {
                throw new LionEngineException(exception, ERROR_CONSTRUCTOR_MISSING + feature);
            }
        }
    }

    @Override
    public void loadSheets(int tileWidth, int tileHeight, Collection<SpriteTiled> sheets)
    {
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        int sheetId = 0;
        for (final SpriteTiled sheet : sheets)
        {
            this.sheets.put(Integer.valueOf(sheetId), sheet);
            sheetId++;
        }
    }

    @Override
    public void loadSheets(Media sheetsConfig)
    {
        this.sheetsConfig = sheetsConfig;

        final TileSheetsConfig config = TileSheetsConfig.imports(sheetsConfig);
        tileWidth = config.getTileWidth();
        tileHeight = config.getTileHeight();

        final String path = sheetsConfig.getPath();
        final String folder = path.substring(0, path.length() - sheetsConfig.getFile().getName().length());
        sheets.clear();
        int sheetId = 0;
        for (final String sheet : config.getSheets())
        {
            final Media media = Medias.create(folder, sheet);
            final SpriteTiled sprite = Drawable.loadSpriteTiled(media, tileWidth, tileHeight);
            sprite.load();
            sprite.prepare();
            sheets.put(Integer.valueOf(sheetId), sprite);
            sheetId++;
        }
    }

    @Override
    public void append(MapTile map, int offsetX, int offsetY)
    {
        Check.notNull(map);

        final int newWidth = Math.max(widthInTile, widthInTile - (widthInTile - offsetX) + map.getInTileWidth());
        final int newHeight = Math.max(heightInTile, heightInTile - (heightInTile - offsetY) + map.getInTileHeight());
        resize(newWidth, newHeight);

        for (int v = 0; v < map.getInTileHeight(); v++)
        {
            final int ty = offsetY + v;
            for (int h = 0; h < map.getInTileWidth(); h++)
            {
                final int tx = offsetX + h;
                final Tile tile = map.getTile(h, v);
                if (tile != null)
                {
                    final double x = tx * (double) tileWidth;
                    final double y = ty * (double) tileHeight;
                    setTile(createTile(tile.getSheet(), tile.getNumber(), x, y));
                }
            }
        }
    }

    @Override
    public void clear()
    {
        if (tiles != null)
        {
            for (int ty = 0; ty < tiles.size(); ty++)
            {
                final List<Tile> list = tiles.get(ty);
                if (list != null)
                {
                    list.clear();
                }
            }
            tiles.clear();
        }
    }

    @Override
    public void addFeature(MapTileFeature feature)
    {
        features.add(feature);
        if (services != null)
        {
            services.add(feature);
        }
    }

    @Override
    public void render(Graphic g)
    {
        render(g,
               viewer.getHeight(),
               (int) Math.ceil(viewer.getX()),
               (int) Math.ceil(viewer.getY()),
               (int) Math.ceil(viewer.getWidth() / (double) tileWidth),
               (int) Math.ceil(viewer.getHeight() / (double) tileHeight),
               -viewer.getViewX(),
               viewer.getViewY());
    }

    @Override
    public void renderTile(Graphic g, Tile tile, int x, int y)
    {
        final SpriteTiled sprite = getSheet(tile.getSheet());
        sprite.setLocation(x, y);
        sprite.setTile(tile.getNumber());
        sprite.render(g);
    }

    @Override
    public void setTileRenderer(MapTileRenderer renderer)
    {
        Check.notNull(renderer);
        this.renderer = renderer;
    }

    @Override
    public void setTile(Tile tile)
    {
        final int tx = tile.getInTileX();
        final int ty = tile.getInTileY();
        Check.inferiorStrict(tx, getInTileWidth());
        Check.inferiorStrict(ty, getInTileHeight());

        tiles.get(ty).set(tx, tile);
    }

    @Override
    public Tile getTile(int tx, int ty)
    {
        if (!UtilMath.isBetween(tx, 0, getInTileWidth() - 1) || !UtilMath.isBetween(ty, 0, getInTileHeight() - 1))
        {
            return null;
        }
        return tiles.get(ty).get(tx);
    }

    @Override
    public Tile getTile(Localizable localizable, int offsetX, int offsetY)
    {
        return getTileAt(localizable.getX() + offsetX, localizable.getY() + offsetY);
    }

    @Override
    public Tile getTileAt(double x, double y)
    {
        final int tx = (int) Math.floor(x / getTileWidth());
        final int ty = (int) Math.floor(y / getTileHeight());
        return getTile(tx, ty);
    }

    @Override
    public Collection<Tile> getTilesHit(double ox, double oy, double x, double y)
    {
        final Force force = Force.fromVector(ox, oy, x, y);
        final double sx = force.getDirectionHorizontal();
        final double sy = force.getDirectionVertical();

        double h = ox;
        double v = oy;

        final Collection<Tile> found = new ArrayList<Tile>();
        for (int count = 0; count < force.getVelocity(); count++)
        {
            v += sy;
            Tile tile = getTileAt(UtilMath.getRound(sx, h), UtilMath.getRound(sy, v));
            if (tile != null && !found.contains(tile))
            {
                found.add(tile);
            }

            h += sx;
            tile = getTileAt(UtilMath.getRound(sx, h), UtilMath.getRound(sy, v));
            if (tile != null && !found.contains(tile))
            {
                found.add(tile);
            }
        }
        return found;
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
    public Media getSheetsConfig()
    {
        return sheetsConfig;
    }

    @Override
    public Collection<Integer> getSheets()
    {
        return sheets.keySet();
    }

    @Override
    public SpriteTiled getSheet(Integer sheet)
    {
        if (!sheets.containsKey(sheet))
        {
            throw new LionEngineException(ERROR_SHEET_MISSING, sheet.toString());
        }
        return sheets.get(sheet);
    }

    @Override
    public <C extends MapTileFeature> C getFeature(Class<C> feature)
    {
        return features.get(feature);
    }

    @Override
    public <C extends MapTileFeature> boolean hasFeature(Class<C> feature)
    {
        return features.contains(feature);
    }

    @Override
    public int getSheetsNumber()
    {
        return sheets.size();
    }

    @Override
    public int getTilesNumber()
    {
        int tilesNumber = 0;
        for (int ty = 0; ty < heightInTile; ty++)
        {
            for (int tx = 0; tx < widthInTile; tx++)
            {
                final Tile tile = getTile(tx, ty);
                if (tile != null)
                {
                    tilesNumber++;
                }
            }
        }
        return tilesNumber;
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
    public int getInTileWidth()
    {
        return widthInTile;
    }

    @Override
    public int getInTileHeight()
    {
        return heightInTile;
    }

    @Override
    public int getInTileRadius()
    {
        return radius;
    }

    @Override
    public Iterable<? extends MapTileFeature> getFeatures()
    {
        return features.getAll();
    }

    @Override
    public boolean isCreated()
    {
        return tiles != null;
    }

    /*
     * Surface
     */

    @Override
    public int getWidth()
    {
        return getInTileWidth() * getTileWidth();
    }

    @Override
    public int getHeight()
    {
        return getInTileHeight() * getTileHeight();
    }
}
