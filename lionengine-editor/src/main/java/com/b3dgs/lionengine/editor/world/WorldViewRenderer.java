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
package com.b3dgs.lionengine.editor.world;

import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.SWT;
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
import com.b3dgs.lionengine.core.Core;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.Mouse;
import com.b3dgs.lionengine.editor.Tools;
import com.b3dgs.lionengine.editor.collision.TileCollisionPart;
import com.b3dgs.lionengine.game.CameraGame;
import com.b3dgs.lionengine.game.entity.EntityGame;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.map.TileGame;
import com.b3dgs.lionengine.geom.Point;

/**
 * World view paint listener, rendering the current world.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class WorldViewRenderer
        implements PaintListener, MouseListener, MouseMoveListener, KeyListener
{
    /** Color of the grid. */
    private static final ColorRgba COLOR_GRID = new ColorRgba(128, 128, 128, 128);
    /** Color of the selection area. */
    private static final ColorRgba COLOR_MOUSE_SELECTION = new ColorRgba(240, 240, 240, 96);
    /** Color of the box around the selected entity. */
    private static final ColorRgba COLOR_ENTITY_SELECTION = new ColorRgba(128, 240, 128, 192);
    /** Color of the selected tile. */
    private static final ColorRgba COLOR_TILE_SELECTED = new ColorRgba(192, 192, 192, 96);
    /** Grid movement sensibility. */
    private static final int GRID_MOVEMENT_SENSIBILITY = 8;

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

    /**
     * Set the camera limits.
     * 
     * @param camera The camera reference.
     * @param maxX The maximum horizontal location.
     * @param maxY The maximum vertical location.
     */
    private static void setCameraLimits(CameraGame camera, int maxX, int maxY)
    {
        if (camera.getLocationX() < 0.0)
        {
            camera.teleportX(0.0);
        }
        else if (camera.getLocationX() > maxX)
        {
            camera.teleportX(maxX);
        }
        if (camera.getLocationY() < 0.0)
        {
            camera.teleportY(0.0);
        }
        else if (camera.getLocationY() > maxY)
        {
            camera.teleportY(maxY);
        }
    }

    /** Part service. */
    private final EPartService partService;
    /** The parent. */
    private final Composite parent;
    /** The view model. */
    private final WorldViewModel model;
    /** Handler entity. */
    private final HandlerEntity handlerEntity;
    /** Selection handler. */
    private final Selection selection;
    /** Entity controller. */
    private final EntityControl entityControl;
    /** Selected tile. */
    private TileGame selectedTile;
    /** Current horizontal mouse location. */
    private int mouseX;
    /** Current vertical mouse location. */
    private int mouseY;
    /** Mouse click. */
    private int click;

    /**
     * Constructor.
     * 
     * @param partService The part service.
     * @param parent The parent container.
     */
    public WorldViewRenderer(Composite parent, EPartService partService)
    {
        this.parent = parent;
        this.partService = partService;
        model = WorldViewModel.INSTANCE;
        handlerEntity = new HandlerEntity(model.getCamera());
        selection = new Selection();
        entityControl = new EntityControl(handlerEntity);
    }

    /**
     * Update the mouse.
     * 
     * @param mx The mouse horizontal location.
     * @param my The mouse vertical location.
     */
    private void updateMouse(int mx, int my)
    {
        mouseX = mx;
        mouseY = my;
        updateRender();
    }

    /**
     * Update the keyboard.
     * 
     * @param vx The keyboard horizontal movement.
     * @param vy The keyboard vertical movement.
     */
    private void updateKeyboard(int vx, int vy)
    {
        final CameraGame camera = model.getCamera();
        final MapTile<? extends TileGame> map = model.getMap();
        final int tw = map.getTileWidth();
        final int th = map.getTileHeight();
        camera.moveLocation(1.0, vx * tw * WorldViewRenderer.GRID_MOVEMENT_SENSIBILITY, vy * th
                * WorldViewRenderer.GRID_MOVEMENT_SENSIBILITY);

        final int maxX = (map.getWidthInTile() - 1) * tw - camera.getViewWidth();
        final int maxY = map.getHeightInTile() * th - camera.getViewHeight();
        WorldViewRenderer.setCameraLimits(camera, maxX, maxY);

        updateRender();
    }

    /**
     * Update the selection when clicking (select single entity, or unselect all previous).
     * 
     * @param mx The mouse horizontal location.
     * @param my The mouse vertical location.
     */
    private void updateSelectionBefore(int mx, int my)
    {
        final EntityGame entity = entityControl.getEntity(mx, my);
        final boolean empty = entityControl.getSelectedEnties().isEmpty();
        selection.reset();

        if (!empty && entity == null)
        {
            entityControl.unSelectEntities();
            selection.start(mx, my);
        }
        else if (empty && entity != null)
        {
            entityControl.setEntitySelection(entity, true);
            selection.start(mx, my);
            selection.end(mx, my);
        }
        else
        {
            selection.start(mx, my);
        }
    }

    /**
     * Update the selection when releasing click (update the entities flags).
     * 
     * @param mx The mouse horizontal location.
     * @param my The mouse vertical location.
     */
    private void updateSelectionAfter(int mx, int my)
    {
        selection.end(mx, my);
        if (selection.isSelected())
        {
            entityControl.selectEntities(selection.getArea());
        }
        for (final EntityGame entity : entityControl.getSelectedEnties())
        {
            entityControl.setEntityLocation(entity, entity.getLocationIntX(), entity.getLocationIntY(), -1);
        }
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
     * Render the world.
     * 
     * @param g The graphic output.
     * @param width The view width.
     * @param height The view height.
     */
    private void render(Graphic g, int width, int height)
    {
        final CameraGame camera = model.getCamera();
        final MapTile<? extends TileGame> map = model.getMap();

        final int tw = map.getTileWidth();
        final int th = map.getTileHeight();
        final int areaX = UtilMath.getRounded(width, tw);
        final int areaY = UtilMath.getRounded(height, th);

        camera.setView(0, 0, areaX - tw, areaY);

        // Background
        g.setColor(ColorRgba.GRAY_LIGHT);
        g.drawRect(0, 0, width, height, true);

        // Map area
        g.setColor(ColorRgba.BLUE);
        g.drawRect(0, 0, areaX, areaY, true);

        // Renders
        if (map.getNumberPatterns() > 0)
        {
            map.render(g, camera);
        }
        handlerEntity.update(1.0);
        handlerEntity.render(g);
        renderOverAndSelectedEntities(g);
        if (selectedTile != null)
        {
            renderSelectedCollisions(g, map, camera);
        }

        selection.render(g, WorldViewRenderer.COLOR_MOUSE_SELECTION);

        renderCursor(g, tw, th, areaX, areaY);
        WorldViewRenderer.drawGrid(g, tw, th, areaX, areaY, WorldViewRenderer.COLOR_GRID);
    }

    /**
     * Render the entity over and selection flag.
     * 
     * @param g The graphic output.
     */
    private void renderOverAndSelectedEntities(Graphic g)
    {
        final CameraGame camera = model.getCamera();
        final MapTile<? extends TileGame> map = model.getMap();
        final int th = map.getTileHeight();

        for (final EntityGame entity : handlerEntity.list())
        {
            final int sx = entity.getLocationIntX();
            final int sy = entity.getLocationIntY();

            if (entityControl.isOver(entity) || entityControl.isSelected(entity))
            {
                g.setColor(WorldViewRenderer.COLOR_ENTITY_SELECTION);
                g.drawRect(sx - camera.getLocationIntX() - entity.getWidth() / 2, -sy + camera.getLocationIntY()
                        - entity.getHeight() + UtilMath.getRounded(camera.getViewHeight(), th), entity.getWidth(),
                        entity.getHeight(), true);
            }
        }
    }

    /**
     * Render the selected tiles collision.
     * 
     * @param g The graphic output.
     * @param map The map reference.
     * @param camera The camera reference.
     */
    private void renderSelectedCollisions(Graphic g, MapTile<? extends TileGame> map, CameraGame camera)
    {
        // Render selected collision
        g.setColor(WorldViewRenderer.COLOR_MOUSE_SELECTION);
        final int th = map.getTileHeight();
        for (int ty = 0; ty < map.getHeightInTile(); ty++)
        {
            for (int tx = 0; tx < map.getWidthInTile(); tx++)
            {
                final TileGame tile = map.getTile(tx, ty);
                if (tile != null)
                {
                    if (tile.getCollision() == selectedTile.getCollision())
                    {
                        g.drawRect(camera.getViewpointX(tile.getX()), camera.getViewpointY(tile.getY()) - th,
                                tile.getWidth(), tile.getHeight(), true);
                    }
                }
            }
        }

        // Render selected tile
        g.setColor(WorldViewRenderer.COLOR_TILE_SELECTED);
        g.drawRect(camera.getViewpointX(selectedTile.getX()), camera.getViewpointY(selectedTile.getY()) - th,
                selectedTile.getWidth(), selectedTile.getHeight(), true);
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
        if (!selection.isSelecting() && !entityControl.isDragging())
        {
            if (mouseX >= 0 && mouseY >= 0 && mouseX < areaX && mouseY < areaY)
            {
                final int mx = UtilMath.getRounded(mouseX, tw);
                final int my = UtilMath.getRounded(mouseY, th);

                g.setColor(WorldViewRenderer.COLOR_MOUSE_SELECTION);
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
        final GC gc = paintEvent.gc;
        final Graphic g = Core.GRAPHIC.createGraphic();
        g.setGraphic(gc);
        if (model.getMap() != null)
        {
            render(g, paintEvent.width, paintEvent.height);
        }
        else
        {
            gc.drawString("No map implementation defined, select it from the project view !", 0, 0, true);
        }
    }

    /*
     * MouseListener
     */

    @Override
    public void mouseDoubleClick(MouseEvent mouseEvent)
    {
        // Nothing to do
    }

    @Override
    public void mouseDown(MouseEvent mouseEvent)
    {
        final int mx = mouseEvent.x;
        final int my = mouseEvent.y;
        click = mouseEvent.button;

        updateSelectionBefore(mx, my);
        updateMouse(mx, my);
    }

    @Override
    public void mouseUp(MouseEvent mouseEvent)
    {
        final int mx = mouseEvent.x;
        final int my = mouseEvent.y;

        updateSelectionAfter(mx, my);

        if (!selection.isSelected() && !entityControl.isDragging())
        {
            final MapTile<? extends TileGame> map = model.getMap();
            final CameraGame camera = model.getCamera();

            if (map.isCreated())
            {
                final Point point = Tools.getMouseTile(map, camera, mx, my);
                selectedTile = map.getTile(point.getX() / map.getTileWidth(), point.getY() / map.getTileHeight());

                final TileCollisionPart part = Tools
                        .getPart(partService, TileCollisionPart.ID, TileCollisionPart.class);
                part.setSelectedTile(selectedTile);
            }

            if (click == Mouse.LEFT)
            {
                entityControl.addEntity(mx, my);
            }
            else if (click == Mouse.RIGHT)
            {
                entityControl.removeEntity(mx, my);
            }
        }

        updateMouse(mx, my);
        entityControl.stopDragging();
        click = 0;
    }

    /*
     * MouseMoveListener
     */

    @Override
    public void mouseMove(MouseEvent mouseEvent)
    {
        final int mx = mouseEvent.x;
        final int my = mouseEvent.y;

        entityControl.updateMouseOver(mx, my);
        if (click == Mouse.LEFT)
        {
            if (entityControl.getSelectedEnties().isEmpty())
            {
                selection.update(mx, my);
            }
            if (!selection.isSelecting())
            {
                entityControl.updateDragging(mouseX, mouseY, mx, my);
            }
        }
        updateMouse(mx, my);
    }

    /*
     * KeyListener
     */

    @Override
    public void keyPressed(KeyEvent keyEvent)
    {
        final int vx;
        final int vy;
        final int code = keyEvent.keyCode;
        if (code == SWT.ARROW_LEFT)
        {
            vx = -1;
        }
        else if (code == SWT.ARROW_RIGHT)
        {
            vx = 1;
        }
        else
        {
            vx = 0;
        }
        if (code == SWT.ARROW_DOWN)
        {
            vy = -1;
        }
        else if (code == SWT.ARROW_UP)
        {
            vy = 1;
        }
        else
        {
            vy = 0;
        }
        updateKeyboard(vx, vy);
    }

    @Override
    public void keyReleased(KeyEvent keyEvent)
    {
        // Nothing to do
    }
}
