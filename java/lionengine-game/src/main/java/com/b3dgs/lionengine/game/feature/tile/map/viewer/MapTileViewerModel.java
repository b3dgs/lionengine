/*
 * Copyright (C) 2013-2026 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.b3dgs.lionengine.game.feature.tile.map.viewer;

import java.util.ArrayList;
import java.util.List;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.FeatureAbstract;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.tile.Tile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileRenderer;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileSurface;
import com.b3dgs.lionengine.game.feature.tile.map.TileSetListener;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.ImageBuffer;
import com.b3dgs.lionengine.graphic.Renderable;
import com.b3dgs.lionengine.graphic.drawable.SpriteTiled;

/**
 * Map tile renderer default implementation.
 */
public class MapTileViewerModel extends FeatureAbstract implements MapTileViewer, TileSetListener
{
    /** Chunk size. */
    private static final int CHUNK_SIZE = 256;

    /** Map tiles renderers. */
    private final List<MapTileRenderer> renderers = new ArrayList<>();

    /** Viewer reference. */
    private final Viewer viewer;

    /** Map tile surface. */
    private MapTileSurface map;
    /** Rendering mode. */
    private Renderable rendering = this::renderByChunks;
    /** Cached chunks buffer. */
    private ImageBuffer[][] chunks;
    /** In chucks width. */
    private int cnh;
    /** In chucks height. */
    private int cnv;

    /**
     * Create feature.
     * 
     * <p>
     * The {@link Featurable} must have:
     * </p>
     * <ul>
     * <li>{@link MapTileSurface}</li>
     * </ul>
     * 
     * <p>
     * The {@link Services} must provide:
     * </p>
     * <ul>
     * <li>{@link Viewer}</li>
     * </ul>
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid argument.
     */
    public MapTileViewerModel(Services services)
    {
        super();

        viewer = services.get(Viewer.class);
    }

    @Override
    public void setChunksEnabled(boolean enabled)
    {
        if (enabled)
        {
            rendering = this::renderByChunks;
        }
        else
        {
            rendering = this::renderByTiles;
        }
    }

    /**
     * Render the tile from location.
     * 
     * @param g The graphic output.
     * @param tx The horizontal tile location.
     * @param ty The vertical tile location.
     * @param viewX The horizontal view offset.
     * @param viewY The vertical view offset.
     */
    private void renderTile(Graphic g, int tx, int ty, double viewX, double viewY)
    {
        final Tile tile = map.getTile(tx, ty);
        if (tile != null)
        {
            final int x = (int) Math.round(tile.getX() - viewX);
            final int y = (int) Math.round(-tile.getY() + viewY - tile.getHeight());

            for (int i = 0; i < renderers.size(); i++)
            {
                renderers.get(i).renderTile(g, tile, x, y);
            }
        }
    }

    /**
     * Render by tiles.
     * 
     * @param g The graphic output.
     */
    private void renderByTiles(Graphic g)
    {
        final int inTileHeight = (int) Math.ceil(viewer.getHeight() / (double) map.getTileHeight());
        final int sy = (int) Math.floor((viewer.getY() - viewer.getViewY()) / map.getTileHeight());
        final double viewY = viewer.getY() + viewer.getScreenHeight();

        for (int v = 0; v <= inTileHeight; v++)
        {
            final int ty = v + sy;
            if (ty >= 0 && ty < map.getInTileHeight())
            {
                renderHorizontal(g, ty, viewY);
            }
        }
    }

    /**
     * Render horizontal tiles.
     * 
     * @param g The graphic output.
     * @param ty The current vertical tile location.
     * @param viewY The vertical view offset.
     */
    private void renderHorizontal(Graphic g, int ty, double viewY)
    {
        final int inTileWidth = (int) Math.ceil(viewer.getWidth() / (double) map.getTileWidth());
        final int sx = (int) Math.floor((viewer.getX() + viewer.getViewX()) / map.getTileWidth());
        final double viewX = viewer.getX();

        for (int h = 0; h <= inTileWidth; h++)
        {
            final int tx = h + sx;
            if (tx >= 0 && tx < map.getInTileWidth())
            {
                renderTile(g, tx, ty, viewX, viewY);
            }
        }
    }

    /**
     * Render horizontal chunks.
     * 
     * @param g The graphic output.
     * @param cy The current vertical chunk location.
     * @param viewY The vertical view offset.
     */
    private void renderHorizontalChunk(Graphic g, int cy, double viewY)
    {
        final int inChunkWidth = (int) Math.ceil(viewer.getWidth() / (double) CHUNK_SIZE);
        final int sx = (int) Math.floor((viewer.getX() + viewer.getViewX()) / CHUNK_SIZE);
        final double viewX = viewer.getX();

        for (int h = 0; h <= inChunkWidth; h++)
        {
            final int cx = h + sx;
            if (cx >= 0 && cx < cnh)
            {
                renderChunk(g, cx, cy, viewX, viewY);
            }
        }
    }

    /**
     * Render by chunks.
     * 
     * @param g The graphic output.
     */
    private void renderByChunks(Graphic g)
    {
        if (chunks == null)
        {
            createChunks();
        }

        final int inChunkHeight = (int) Math.ceil(viewer.getHeight() / (double) CHUNK_SIZE);
        final int sy = (int) Math.floor((viewer.getY() - viewer.getViewY()) / CHUNK_SIZE);
        final double viewY = viewer.getY() + viewer.getScreenHeight();

        for (int v = 0; v <= inChunkHeight; v++)
        {
            final int cy = v + sy;
            if (cy >= 0 && cy < cnv)
            {
                renderHorizontalChunk(g, cy, viewY);
            }
        }
    }

    /**
     * Create chunks surface.
     */
    private void createChunks()
    {
        final int tw = map.getTileWidth();
        final int th = map.getTileHeight();
        cnh = map.getInTileWidth() / (CHUNK_SIZE / map.getTileWidth()) + 1;
        cnv = map.getInTileHeight() / (CHUNK_SIZE / map.getTileHeight()) + 1;
        chunks = new ImageBuffer[cnv][cnh];

        for (int cy = 0; cy < cnv; cy++)
        {
            for (int cx = 0; cx < cnh; cx++)
            {
                final ImageBuffer buf = Graphics.createImageBuffer(CHUNK_SIZE, CHUNK_SIZE, ColorRgba.TRANSPARENT);
                final Graphic cg = buf.createGraphic();
                renderTilesOnChunk(cg, cy, cx, tw, th);
                cg.dispose();

                chunks[cy][cx] = buf;
            }
        }
    }

    /**
     * Render tiles on chunks by vertical.
     * 
     * @param cg The graphic output.
     * @param cy The vertical chunk.
     * @param cx The horizontal chunk.
     * @param tw The tile width.
     * @param th The tile height.
     */
    private void renderTilesOnChunk(Graphic cg, int cy, int cx, int tw, int th)
    {
        final int inTileHeight = (int) Math.ceil(CHUNK_SIZE / (double) th);
        final int sy = (int) Math.floor(cy * CHUNK_SIZE / th);
        final double viewY = cy * CHUNK_SIZE + CHUNK_SIZE;

        for (int v = 0; v <= inTileHeight; v++)
        {
            final int ty = v + sy;
            if (ty >= 0 && ty < map.getInTileHeight())
            {
                renderHorizontalTilesOnChunk(cg, cx, tw, ty, viewY);
            }
        }
    }

    /**
     * Render tiles on chunks by horizontal.
     * 
     * @param cg The graphic output.
     * @param cx The horizontal chunk.
     * @param tw The tile width.
     * @param ty The vertical tile.
     * @param viewY The view Y.
     */
    private void renderHorizontalTilesOnChunk(Graphic cg, int cx, int tw, int ty, double viewY)
    {
        final int inTileWidth = (int) Math.ceil(CHUNK_SIZE / (double) tw);
        final int sx = (int) Math.floor(cx * CHUNK_SIZE / tw);
        final double viewX = cx * CHUNK_SIZE;

        for (int h = 0; h <= inTileWidth; h++)
        {
            final int tx = h + sx;
            if (tx >= 0 && tx < map.getInTileWidth())
            {
                renderTile(cg, tx, ty, viewX, viewY);
            }
        }
    }

    /**
     * Render the tile from location on chunk.
     * 
     * @param g The graphic output.
     * @param cx The horizontal tile location.
     * @param cy The vertical tile location.
     * @param viewX The horizontal view offset.
     * @param viewY The vertical view offset.
     */
    private void renderChunk(Graphic g, int cx, int cy, double viewX, double viewY)
    {
        final int x = (int) Math.round(cx * CHUNK_SIZE - viewX);
        final int y = (int) Math.round(-cy * CHUNK_SIZE + viewY - CHUNK_SIZE);

        g.drawImage(chunks[cy][cx], x, y);
    }

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        map = provider.getFeature(MapTileSurface.class);
        renderers.add(this);
    }

    @Override
    public void addRenderer(MapTileRenderer renderer)
    {
        Check.notNull(renderer);

        renderers.add(renderer);
    }

    @Override
    public void removeRenderer(MapTileRenderer renderer)
    {
        Check.notNull(renderer);

        renderers.remove(renderer);
    }

    @Override
    public void clear()
    {
        renderers.clear();
    }

    @Override
    public void render(Graphic g)
    {
        if (map.isCreated())
        {
            rendering.render(g);
        }
    }

    @Override
    public void renderTile(Graphic g, Tile tile, int x, int y)
    {
        final SpriteTiled sprite = map.getSheet(tile.getSheet());
        sprite.setLocation(x, y);
        sprite.setTile(tile.getNumber());
        sprite.render(g);
    }

    @Override
    public void onTileSet(Tile tile)
    {
        if (chunks != null)
        {
            final int cx = (int) Math.floor(tile.getX() / CHUNK_SIZE);
            final int cy = (int) Math.floor(tile.getY() / CHUNK_SIZE);
            final ImageBuffer buf = Graphics.createImageBuffer(CHUNK_SIZE, CHUNK_SIZE, ColorRgba.TRANSPARENT);
            final Graphic cg = buf.createGraphic();
            renderTilesOnChunk(cg, cy, cx, tile.getWidth(), tile.getHeight());
            cg.dispose();

            chunks[cy][cx].dispose();
            chunks[cy][cx] = buf;
        }
    }
}
