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
package com.b3dgs.lionengine.editor.world;

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
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.Graphics;
import com.b3dgs.lionengine.core.ImageBuffer;
import com.b3dgs.lionengine.core.Transform;
import com.b3dgs.lionengine.editor.Activator;
import com.b3dgs.lionengine.game.Camera;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.map.MapTileCollision;
import com.b3dgs.lionengine.game.map.Tile;
import com.b3dgs.lionengine.game.object.Handler;
import com.b3dgs.lionengine.game.object.Services;
import com.b3dgs.lionengine.game.trait.transformable.Transformable;

/**
 * World view paint listener, rendering the current world.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class WorldViewRenderer implements PaintListener, MouseListener, MouseMoveListener, MouseWheelListener,
                               KeyListener
{
    /** Extension ID. */
    public static final String EXTENSION_ID = Activator.PLUGIN_ID + ".worldViewRenderer";
    /** Color of the grid. */
    private static final ColorRgba COLOR_GRID = new ColorRgba(128, 128, 128, 128);
    /** Color of the selection area. */
    private static final ColorRgba COLOR_MOUSE_SELECTION = new ColorRgba(240, 240, 240, 96);
    /** Color of the box around the selected object. */
    private static final ColorRgba COLOR_ENTITY_SELECTION = new ColorRgba(128, 240, 128, 192);
    /** Color of the selected tile. */
    private static final ColorRgba COLOR_TILE_SELECTED = new ColorRgba(192, 192, 192, 96);

    /**
     * Check if tiles groups are same.
     * 
     * @param groupA The first group.
     * @param groupB The second group.
     * @return <code>true</code> if groups are same (<code>null</code> included).
     */
    public static boolean groupEquals(String groupA, String groupB)
    {
        final boolean result;
        if (groupA != null && groupB != null)
        {
            result = groupA.equals(groupB);
        }
        else if (groupA == null && groupB == null)
        {
            result = true;
        }
        else
        {
            result = false;
        }
        return result;
    }

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

    /**
     * Draw the grid.
     * 
     * @param g The graphic output.
     * @param width Horizontal global grid size.
     * @param height Vertical global grid size.
     * @param tw The tile width.
     * @param th The tile height.
     * @param offsetY The vertical offset.
     */
    private static void renderGrid(Graphic g, int width, int height, int tw, int th, int offsetY)
    {
        g.setColor(COLOR_GRID);
        // Render horizontal lines
        for (int v = height - offsetY; v > -offsetY; v -= th)
        {
            g.drawLine(0, v + offsetY, width - 1, v + offsetY);
        }
        // Render vertical lines
        for (int h = 0; h < width; h += tw)
        {
            g.drawLine(h, 0, h, height - 1);
        }
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
    /** Object controller. */
    private final ObjectControl objectControl;
    /** Selection handler. */
    private final Selection selection;
    /** World updater. */
    private final WorldViewUpdater worldViewUpdater;

    /**
     * Create a world view renderer with grid enabled.
     * 
     * @param parent The parent container.
     * @param partService The part services reference.
     * @param services The services reference.
     */
    public WorldViewRenderer(Composite parent, EPartService partService, Services services)
    {
        this.parent = parent;
        this.partService = partService;
        camera = services.get(Camera.class);
        map = services.get(MapTile.class);
        handlerObject = services.get(Handler.class);
        objectControl = services.get(ObjectControl.class);
        selection = services.get(Selection.class);
        worldViewUpdater = services.get(WorldViewUpdater.class);
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
        final int offsetY = height - UtilMath.getRounded(height, map.getTileHeight());
        worldViewUpdater.setOffsetY(offsetY);

        camera.setView(0, 0, width, height);
        camera.setLimits(map);

        renderBackground(g, width, height);

        renderMap(g, width, height);
        renderEntities(g);

        renderSelection(g);
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
            if (worldViewUpdater.isCollisionsEnabled() && map.hasFeature(MapTileCollision.class))
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
     * Render the current selection.
     * 
     * @param g The graphic output.
     */
    protected void renderSelection(Graphic g)
    {
        selection.render(g, COLOR_MOUSE_SELECTION);
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

    /**
     * Render the selected tiles group.
     * 
     * @param g The graphic output.
     * @param selectedTile The selected tile reference.
     * @param scale The scale factor.
     * @param tw The current tile width.
     * @param th The current tile height.
     */
    private void renderSelectedGroup(Graphic g, Tile selectedTile, double scale, int tw, int th)
    {
        g.setColor(COLOR_MOUSE_SELECTION);
        for (int ty = 0; ty < map.getInTileHeight(); ty++)
        {
            for (int tx = 0; tx < map.getInTileWidth(); tx++)
            {
                final Tile tile = map.getTile(tx, ty);
                if (tile != null && groupEquals(selectedTile.getGroup(), tile.getGroup()))
                {
                    final int x = (int) (camera.getViewpointX(tile.getX()) * scale);
                    final int y = (int) (camera.getViewpointY(tile.getY()) * scale) - th;
                    g.drawRect(x, y, tw, th, true);
                }
            }
        }
    }

    /**
     * Render the selected tile.
     * 
     * @param g The graphic output.
     * @param selectedTile The selected tile reference.
     * @param scale The scale factor.
     * @param tw The current tile width.
     * @param th The current tile height.
     */
    private void renderSelectedTile(Graphic g, Tile selectedTile, double scale, int tw, int th)
    {
        g.setColor(COLOR_TILE_SELECTED);
        final int x = (int) (camera.getViewpointX(selectedTile.getX()) * scale);
        final int y = (int) (camera.getViewpointY(selectedTile.getY()) * scale) - th;
        g.drawRect(x, y, tw, th, true);
    }

    /**
     * Render the object over and selection flag.
     * 
     * @param g The graphic output.
     * @param scale The scale factor.
     */
    private void renderOverAndSelectedEntities(Graphic g, double scale)
    {
        for (final Transformable object : handlerObject.get(Transformable.class))
        {
            if (objectControl.isOver(object.getOwner()) || objectControl.isSelected(object.getOwner()))
            {
                g.setColor(COLOR_ENTITY_SELECTION);
                final int x = (int) (camera.getViewpointX(object.getX()) * scale);
                final int y = (int) ((camera.getViewpointY(object.getY()) - object.getHeight()) * scale);
                g.drawRect(x, y, object.getWidth(), object.getHeight(), true);
            }
        }
    }

    /**
     * Render the cursor.
     * 
     * @param g The graphic output.
     * @param width Maximum width.
     * @param height Maximum height.
     * @param tw The tile width.
     * @param th The tile height.
     * @param offsetY The vertical offset.
     */
    private void renderCursor(Graphic g, int width, int height, int tw, int th, int offsetY)
    {
        if (!selection.isSelecting() && !objectControl.isDragging() && !objectControl.hasOver())
        {
            final int mouseX = worldViewUpdater.getMx();
            final int mouseY = worldViewUpdater.getMy() - offsetY;
            if (mouseX >= 0 && mouseY >= 0 && mouseX < width && mouseY < height)
            {
                final int mx = UtilMath.getRounded(mouseX, tw);
                final int my = UtilMath.getRounded(mouseY, th) + offsetY;

                g.setColor(COLOR_MOUSE_SELECTION);
                g.drawRect(mx, my, tw, th, true);
            }
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
            final double scale = worldViewUpdater.getZoomScale();
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
            final int offsetY = paintEvent.height - UtilMath.getRounded(paintEvent.height, th);

            renderOverAndSelectedEntities(g, scale);
            final Tile selectedTile = worldViewUpdater.getSelectedTile();
            if (selectedTile != null)
            {
                renderSelectedGroup(g, selectedTile, scale, tw, th);
                renderSelectedTile(g, selectedTile, scale, tw, th);
            }
            if (WorldViewModel.INSTANCE.getSelectedPalette() == PaletteType.POINTER_OBJECT)
            {
                renderCursor(g, paintEvent.width, paintEvent.height, tw, th, offsetY);
            }
            if (worldViewUpdater.isGridEnabled())
            {
                renderGrid(g, paintEvent.width, paintEvent.height, tw, th, offsetY);
            }
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
