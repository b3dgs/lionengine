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
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Composite;

import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.Graphics;
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
public class WorldViewRenderer implements PaintListener, MouseListener, MouseMoveListener, KeyListener
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
     * Draw the grid.
     * 
     * @param g The graphic output.
     * @param tw Horizontal grid spacing (width).
     * @param th Vertical grid spacing (height).
     * @param areaX Horizontal global grid size.
     * @param areaY Vertical global grid size.
     * @param color Grid color.
     */
    private static void drawGrid(Graphic g, int tw, int th, int areaX, int areaY, ColorRgba color)
    {
        g.setColor(color);
        for (int v = 0; v <= areaY; v += tw)
        {
            g.drawLine(0, v, areaX, v);
        }
        for (int h = 0; h <= areaX; h += th)
        {
            g.drawLine(h, 0, h, areaY);
        }
    }

    /** Part service. */
    protected final EPartService partService;
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
     * @param areaX The horizontal rendering area.
     * @param areaY The vertical rendering area.
     */
    protected void render(Graphic g, int areaX, int areaY)
    {
        renderMap(g, areaX, areaY);
        renderEntities(g);
        renderOverAndSelectedEntities(g);

        final Tile selectedTile = worldViewUpdater.getSelectedTile();
        if (selectedTile != null)
        {
            renderSelectedGroup(g, selectedTile);
        }
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
     */
    private void renderSelectedGroup(Graphic g, Tile selectedTile)
    {
        // Render selected group
        g.setColor(COLOR_MOUSE_SELECTION);
        final int th = map.getTileHeight();
        for (int ty = 0; ty < map.getInTileHeight(); ty++)
        {
            for (int tx = 0; tx < map.getInTileWidth(); tx++)
            {
                final Tile tile = map.getTile(tx, ty);
                if (tile != null && groupEquals(selectedTile.getGroup(), tile.getGroup()))
                {
                    g.drawRect((int) camera.getViewpointX(tile.getX()), (int) camera.getViewpointY(tile.getY()) - th,
                            tile.getWidth(), tile.getHeight(), true);
                }
            }
        }

        // Render selected tile
        g.setColor(COLOR_TILE_SELECTED);
        g.drawRect((int) camera.getViewpointX(selectedTile.getX()),
                (int) camera.getViewpointY(selectedTile.getY()) - th, selectedTile.getWidth(), selectedTile.getHeight(),
                true);
    }

    /**
     * Render the object over and selection flag.
     * 
     * @param g The graphic output.
     */
    private void renderOverAndSelectedEntities(Graphic g)
    {
        final int th = map.getTileHeight();

        for (final Transformable object : handlerObject.get(Transformable.class))
        {
            final int sx = (int) object.getX();
            final int sy = (int) object.getY();

            if (objectControl.isOver(object.getOwner()) || objectControl.isSelected(object.getOwner()))
            {
                g.setColor(COLOR_ENTITY_SELECTION);
                final int x = sx - (int) camera.getX() - object.getWidth() / 2;
                final int y = -sy + (int) camera.getY() - object.getHeight()
                        + UtilMath.getRounded(camera.getHeight(), th);
                g.drawRect(x, y, object.getWidth(), object.getHeight(), true);
            }
        }
    }

    /**
     * Render the cursor.
     * 
     * @param g The graphic output.
     * @param tw The tile width.
     * @param th The tile height.
     * @param areaX Maximum width.
     * @param areaY Maximum height.
     */
    private void renderCursor(Graphic g, int tw, int th, int areaX, int areaY)
    {
        if (!selection.isSelecting() && !objectControl.isDragging() && !objectControl.hasOver())
        {
            final int mouseX = worldViewUpdater.getMouseX();
            final int mouseY = worldViewUpdater.getMouseY();
            if (mouseX >= 0 && mouseY >= 0 && mouseX < areaX && mouseY < areaY)
            {
                final int mx = UtilMath.getRounded(mouseX, tw);
                final int my = UtilMath.getRounded(mouseY, th);

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
        final int width = paintEvent.width;
        final int height = paintEvent.height;
        final GC gc = paintEvent.gc;
        final Graphic g = Graphics.createGraphic();
        g.setGraphic(gc);

        if (map.isCreated())
        {
            final int tw = map.getTileWidth();
            final int th = map.getTileHeight();
            final int areaX = UtilMath.getRounded(width, tw);
            final int areaY = UtilMath.getRounded(height, th);

            camera.setView(0, 0, areaX, areaY);
            camera.setLimits(map);

            g.setColor(ColorRgba.GRAY_LIGHT);
            g.drawRect(0, 0, width, height, true);

            render(g, areaX, areaY);
            if (WorldViewModel.INSTANCE.getSelectedPalette() == PaletteType.POINTER_OBJECT)
            {
                renderCursor(g, tw, th, areaX, areaY);
            }
            if (worldViewUpdater.isGridEnabled())
            {
                drawGrid(g, tw, th, areaX, areaY, COLOR_GRID);
            }

            g.setColor(ColorRgba.GRAY_LIGHT);
            g.drawRect(areaX, 0, width - areaX, height, true);
            g.drawRect(0, areaY, width, height - areaY, true);
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
     * KeyListener
     */

    @Override
    public void keyPressed(KeyEvent e)
    {
        updateRender();
    }

    @Override
    public void keyReleased(KeyEvent e)
    {
        // Nothing to do
    }
}
