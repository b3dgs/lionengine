/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.editor.world.renderer;

import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Composite;

import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.Transparency;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.Graphics;
import com.b3dgs.lionengine.core.ImageBuffer;
import com.b3dgs.lionengine.core.Transform;
import com.b3dgs.lionengine.editor.Activator;
import com.b3dgs.lionengine.editor.world.Selection;
import com.b3dgs.lionengine.editor.world.updater.WorldUpdater;
import com.b3dgs.lionengine.editor.world.updater.WorldZoom;
import com.b3dgs.lionengine.game.Camera;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.map.MapTileCollision;
import com.b3dgs.lionengine.game.object.Handler;
import com.b3dgs.lionengine.game.object.Services;

/**
 * World paint listener, rendering the current world.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class WorldRenderer implements PaintListener, MouseListener, MouseMoveListener, MouseWheelListener, KeyListener
{
    /** Extension ID. */
    public static final String EXTENSION_ID = Activator.PLUGIN_ID + ".worldRenderer";
    /** Color of the selection area. */
    private static final ColorRgba COLOR_MOUSE_SELECTION = new ColorRgba(240, 240, 240, 96);

    /**
     * Render world background.
     * 
     * @param g The graphic output.
     * @param width The world width.
     * @param height The world height.
     */
    private static void renderBackground(Graphic g, int width, int height)
    {
        g.setColor(ColorRgba.GRAY_LIGHT);
        g.drawRect(0, 0, width, height, true);
    }

    /** Part service. */
    protected final EPartService partService;
    /** Scale transform. */
    private final Transform transform = Graphics.createTransform();
    /** The parent. */
    private final Composite parent;
    /** Camera reference. */
    private final Camera camera;
    /** Map reference. */
    private final MapTile map;
    /** Handler object. */
    private final Handler handlerObject;
    /** Selection handler. */
    private final Selection selection;
    /** World updater. */
    private final WorldUpdater worldUpdater;
    /** World zoom. */
    private final WorldZoom zoom;
    /** Grid renderer. */
    private final WorldGrid grid;
    /** Cursor renderer. */
    private final WorldCursor cursor;
    /** Selected tiles. */
    private final WorldSelectedTiles selectedTiles;
    /** Selected objects. */
    private final WorldSelectedObjects selectedObjects;

    /**
     * Create a world renderer with grid enabled.
     * 
     * @param parent The parent container.
     * @param partService The part services reference.
     * @param services The services reference.
     */
    public WorldRenderer(Composite parent, EPartService partService, Services services)
    {
        this.parent = parent;
        this.partService = partService;
        grid = new WorldGrid(services);
        cursor = new WorldCursor(services);
        selectedTiles = new WorldSelectedTiles(services);
        selectedObjects = new WorldSelectedObjects(services);
        camera = services.get(Camera.class);
        map = services.get(MapTile.class);
        handlerObject = services.get(Handler.class);
        selection = services.get(Selection.class);
        worldUpdater = services.get(WorldUpdater.class);
        zoom = services.get(WorldZoom.class);
    }

    /**
     * Render the world and its components.
     * 
     * @param g The graphic output.
     * @param width The buffer width.
     * @param height The buffer height.
     */
    protected void render(Graphic g, int width, int height)
    {
        camera.setView(0, 0, width, height);
        camera.setLimits(map);

        renderBackground(g, width, height);

        renderMap(g, width, height);
        renderEntities(g);

        selection.render(g, COLOR_MOUSE_SELECTION);
    }

    /**
     * Render the map.
     * 
     * @param g The graphic output.
     * @param areaX The horizontal rendering area.
     * @param areaY The vertical rendering area.
     */
    protected void renderMap(Graphic g, int areaX, int areaY)
    {
        g.setColor(ColorRgba.BLUE);
        g.drawRect(0, 0, areaX, areaY, true);

        if (map.getSheetsNumber() > 0)
        {
            map.render(g);
            if (worldUpdater.isCollisionsEnabled() && map.hasFeature(MapTileCollision.class))
            {
                map.getFeature(MapTileCollision.class).render(g);
            }
        }
    }

    /**
     * Render the handled objects.
     * 
     * @param g The graphic output.
     */
    protected void renderEntities(Graphic g)
    {
        handlerObject.update(1.0);
        handlerObject.render(g);
    }

    /**
     * Update the rendering.
     */
    private void updateRender()
    {
        if (!parent.isDisposed())
        {
            parent.redraw();
        }
    }

    /*
     * PaintListener
     */

    @Override
    public void paintControl(PaintEvent paintEvent)
    {
        if (map.isCreated())
        {
            final double scale = zoom.getScale();
            final int width = (int) Math.ceil(paintEvent.width / scale);
            final int height = (int) Math.ceil(paintEvent.height / scale);

            final ImageBuffer buffer = Graphics.createImageBuffer(width, height, Transparency.OPAQUE);
            final Graphic gbuffer = buffer.createGraphic();
            render(gbuffer, width, height);
            gbuffer.dispose();

            final GC gc = paintEvent.gc;
            final Graphic g = Graphics.createGraphic();
            g.setGraphic(gc);

            transform.scale(scale, scale);
            g.drawImage(buffer, transform, 0, 0);
            buffer.dispose();

            final int tw = (int) Math.ceil(map.getTileWidth() * scale);
            final int th = (int) Math.ceil(map.getTileHeight() * scale);
            selectedObjects.onRender(g, paintEvent.width, paintEvent.height, scale, tw, th);
            selectedTiles.onRender(g, paintEvent.width, paintEvent.height, scale, tw, th);
            cursor.onRender(g, paintEvent.width, paintEvent.height, scale, tw, th);
            grid.onRender(g, paintEvent.width, paintEvent.height, scale, tw, th);
        }
    }

    /*
     * MouseListener
     */

    @Override
    public void mouseDown(MouseEvent mouseEvent)
    {
        updateRender();
    }

    @Override
    public void mouseUp(MouseEvent mouseEvent)
    {
        updateRender();
    }

    @Override
    public void mouseDoubleClick(MouseEvent mouseEvent)
    {
        // Nothing to do
    }

    /*
     * MouseMoveListener
     */

    @Override
    public void mouseMove(MouseEvent mouseEvent)
    {
        updateRender();
    }

    /*
     * MouseWheelListener
     */

    @Override
    public void mouseScrolled(MouseEvent event)
    {
        updateRender();
    }

    /*
     * KeyListener
     */

    @Override
    public void keyPressed(KeyEvent event)
    {
        updateRender();
    }

    @Override
    public void keyReleased(KeyEvent event)
    {
        // Nothing to do
    }
}
