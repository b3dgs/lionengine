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
import com.b3dgs.lionengine.core.swt.Mouse;
import com.b3dgs.lionengine.editor.Activator;
import com.b3dgs.lionengine.editor.Tools;
import com.b3dgs.lionengine.editor.UtilEclipse;
import com.b3dgs.lionengine.editor.collision.TileCollisionView;
import com.b3dgs.lionengine.editor.factory.FactoryView;
import com.b3dgs.lionengine.editor.palette.PalettePart;
import com.b3dgs.lionengine.editor.palette.PaletteType;
import com.b3dgs.lionengine.game.CameraGame;
import com.b3dgs.lionengine.game.EntityGame;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.map.TileGame;
import com.b3dgs.lionengine.geom.Point;

/**
 * World view paint listener, rendering the current world.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class WorldViewRenderer
        implements PaintListener, MouseListener, MouseMoveListener, KeyListener
{
    /** Extension ID. */
    public static final String EXTENSION_ID = Activator.PLUGIN_ID + ".worldViewRenderer";
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

    /** The view model. */
    protected final WorldViewModel model;
    /** Part service. */
    protected final EPartService partService;
    /** The parent. */
    private final Composite parent;
    /** Handler entity. */
    private final HandlerEntity handlerEntity;
    /** Selection handler. */
    private final Selection selection;
    /** Entity controller. */
    private final EntityControl entityControl;
    /** Selected tile. */
    private TileGame selectedTile;
    /** Last selected tile. */
    private TileGame lastSelectedTile;
    /** Grid enabled. */
    private boolean gridEnabled;
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
        gridEnabled = true;
    }

    /**
     * Set the grid enabled state.
     */
    public void switchGridEnabled()
    {
        gridEnabled = !gridEnabled;
    }

    /**
     * Get the handler entity.
     * 
     * @return The handler entity.
     */
    public HandlerEntity getHandler()
    {
        return handlerEntity;
    }

    /**
     * Get the mouse click.
     * 
     * @return The mouse click.
     */
    public int getClick()
    {
        return click;
    }

    /**
     * Update the palette action on click down.
     * 
     * @param palette The palette type.
     * @param mx The mouse horizontal location.
     * @param my The mouse vertical location.
     */
    protected void updatePaletteBefore(Enum<?> palette, int mx, int my)
    {
        if (palette == PaletteType.POINTER)
        {
            final PalettePart part = UtilEclipse.getPart(partService, PalettePart.ID, PalettePart.class);
            updatePalettePointer(part, mx, my);

            updateSingleEntitySelection(mx, my);
        }
        else if (palette == PaletteType.SELECTION)
        {
            updateSelectionBefore(mx, my);
        }
    }

    /**
     * Update the palette action when moving mouse.
     * 
     * @param palette The palette type.
     * @param mx The mouse horizontal location.
     * @param my The mouse vertical location.
     */
    protected void updatePaletteMoving(Enum<?> palette, int mx, int my)
    {
        if (palette == PaletteType.POINTER)
        {
            entityControl.updateMouseOver(mx, my);
            if (getClick() == Mouse.LEFT)
            {
                entityControl.updateDragging(mouseX, mouseY, mx, my);
            }
        }
        else if (palette == PaletteType.SELECTION && click == Mouse.LEFT)
        {
            if (!entityControl.hasSelection())
            {
                selection.update(mx, my);
            }
            else
            {
                entityControl.updateDragging(mouseX, mouseY, mx, my);
            }
        }
        else if (palette == PaletteType.HAND && click > 0)
        {
            updateCamera(mouseX - mx, my - mouseY, 0);
        }
    }

    /**
     * Update the palette action on click up.
     * 
     * @param palette The palette type.
     * @param mx The mouse horizontal location.
     * @param my The mouse vertical location.
     */
    protected void updatePaletteAfter(Enum<?> palette, int mx, int my)
    {
        if (palette == PaletteType.SELECTION)
        {
            updateSelectionAfter(mx, my);
        }
        else if (palette == PaletteType.HAND)
        {
            updateHand();
        }
    }

    /**
     * Update the palette pointer action on click release.
     * 
     * @param part The palette part reference.
     * @param mx The mouse horizontal location.
     * @param my The mouse vertical location.
     */
    protected void updatePalettePointer(PalettePart part, int mx, int my)
    {
        updatePointerMap(part, mx, my);
        updatePointerFactory(part, mx, my);
    }

    /**
     * Update the entity selection with pointer.
     * 
     * @param mx The mouse horizontal location.
     * @param my The mouse vertical location.
     */
    protected void updateSingleEntitySelection(int mx, int my)
    {
        entityControl.unSelectEntities();
        final EntityGame entity = entityControl.getEntity(mx, my);
        if (entity != null)
        {
            entityControl.setEntitySelection(entity, true);
        }
    }

    /**
     * Render the world and its components.
     * 
     * @param g The graphic output.
     * @param camera The camera reference.
     * @param map The map reference.
     * @param areaX The horizontal rendering area.
     * @param areaY The vertical rendering area.
     */
    protected void render(Graphic g, CameraGame camera, MapTile<?> map, int areaX, int areaY)
    {
        renderMap(g, camera, map, areaX, areaY);
        renderEntities(g);
        renderOverAndSelectedEntities(g);
        if (selectedTile != null)
        {
            renderSelectedCollisions(g, map, camera);
        }
        renderSelection(g);
    }

    /**
     * Render the world background.
     * 
     * @param g The graphic output.
     * @param width The renderer width.
     * @param height The renderer height.
     */
    protected void renderBackground(Graphic g, int width, int height)
    {
        g.setColor(ColorRgba.GRAY_LIGHT);
        g.drawRect(0, 0, width, height, true);
    }

    /**
     * Render the map.
     * 
     * @param g The graphic output.
     * @param camera The camera reference.
     * @param map The map reference.
     * @param areaX The horizontal rendering area.
     * @param areaY The vertical rendering area.
     */
    protected void renderMap(Graphic g, CameraGame camera, MapTile<?> map, int areaX, int areaY)
    {
        g.setColor(ColorRgba.BLUE);
        g.drawRect(0, 0, areaX, areaY, true);

        if (map.getNumberPatterns() > 0)
        {
            map.render(g, camera);
        }
    }

    /**
     * Render the handled entities.
     * 
     * @param g The graphic output.
     */
    protected void renderEntities(Graphic g)
    {
        handlerEntity.update(1.0);
        handlerEntity.render(g);
    }

    /**
     * Render the current selection.
     * 
     * @param g The graphic output.
     */
    protected void renderSelection(Graphic g)
    {
        selection.render(g, WorldViewRenderer.COLOR_MOUSE_SELECTION);
    }

    /**
     * Update the pointer in map case.
     * 
     * @param part The current palette part.
     * @param mx The mouse horizontal location.
     * @param my The mouse vertical location.
     */
    private void updatePointerMap(PalettePart part, int mx, int my)
    {
        final MapTile<?> map = model.getMap();
        if (map.isCreated() && part.getActivePaletteId() == TileCollisionView.ID)
        {
            final CameraGame camera = model.getCamera();
            final Point point = Tools.getMouseTile(map, camera, mx, my);
            lastSelectedTile = selectedTile;
            selectedTile = map.getTile(point.getX() / map.getTileWidth(), point.getY() / map.getTileHeight());

            if (selectedTile != lastSelectedTile)
            {
                final TileCollisionView view = part.getPaletteView(TileCollisionView.ID, TileCollisionView.class);
                view.setSelectedTile(selectedTile);
            }
        }
        else
        {
            selectedTile = null;
        }
    }

    /**
     * Update the pointer in factory case.
     * 
     * @param part The current palette part.
     * @param mx The mouse horizontal location.
     * @param my The mouse vertical location.
     */
    private void updatePointerFactory(PalettePart part, int mx, int my)
    {
        if (part.getActivePaletteId() == FactoryView.ID && !entityControl.isDragging())
        {
            if (click == Mouse.LEFT && !entityControl.hasOver() && !entityControl.hasSelection())
            {
                entityControl.addEntity(mx, my);
            }
            else if (click == Mouse.RIGHT)
            {
                entityControl.removeEntity(mx, my);
            }
        }
    }

    /**
     * Update the hand palette type.
     */
    private void updateHand()
    {
        final CameraGame camera = model.getCamera();
        final MapTile<?> map = model.getMap();
        camera.setLocation(UtilMath.getRounded(camera.getLocationIntX(), map.getTileWidth()),
                UtilMath.getRounded(camera.getLocationIntY(), map.getTileHeight()));
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
     * @param step The movement sensibility.
     */
    private void updateCamera(int vx, int vy, int step)
    {
        final CameraGame camera = model.getCamera();
        final MapTile<?> map = model.getMap();
        final int tw = map.getTileWidth();
        final int th = map.getTileHeight();
        if (step > 0)
        {
            camera.moveLocation(1.0, UtilMath.getRounded(vx * tw * step, tw), UtilMath.getRounded(vy * th * step, th));
        }
        else
        {
            camera.moveLocation(1.0, vx, vy);
        }

        final int maxX = Math.max(0, (map.getWidthInTile() - 1) * tw - camera.getViewWidth());
        final int maxY = Math.max(0, map.getHeightInTile() * th - camera.getViewHeight());
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
        selection.reset();

        if (entityControl.hasSelection() && entity == null)
        {
            entityControl.unSelectEntities();
            selection.start(mx, my);
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
        final MapTile<?> map = model.getMap();

        final int tw = map.getTileWidth();
        final int th = map.getTileHeight();
        final int areaX = UtilMath.getRounded(width, tw);
        final int areaY = UtilMath.getRounded(height, th);

        camera.setView(0, 0, areaX - tw, areaY);

        renderBackground(g, width, height);
        render(g, camera, map, areaX, areaY);
        if (WorldViewModel.INSTANCE.getSelectedPalette() == PaletteType.POINTER)
        {
            renderCursor(g, tw, th, areaX, areaY);
        }
        if (gridEnabled)
        {
            WorldViewRenderer.drawGrid(g, tw, th, areaX, areaY, WorldViewRenderer.COLOR_GRID);
        }
    }

    /**
     * Render the entity over and selection flag.
     * 
     * @param g The graphic output.
     */
    private void renderOverAndSelectedEntities(Graphic g)
    {
        final CameraGame camera = model.getCamera();
        final MapTile<?> map = model.getMap();
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
    private void renderSelectedCollisions(Graphic g, MapTile<?> map, CameraGame camera)
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
        if (!selection.isSelecting() && !entityControl.isDragging() && !entityControl.hasOver())
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
            gc.drawString(Messages.WorldView_NoMapImpl, 0, 0, true);
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
        final Enum<?> palette = model.getSelectedPalette();
        click = mouseEvent.button;

        updatePaletteBefore(palette, mx, my);
        updateMouse(mx, my);
    }

    @Override
    public void mouseUp(MouseEvent mouseEvent)
    {
        final int mx = mouseEvent.x;
        final int my = mouseEvent.y;
        final Enum<?> palette = model.getSelectedPalette();

        updatePaletteAfter(palette, mx, my);
        updateMouse(mx, my);

        entityControl.stopDragging();
        for (final EntityGame entity : entityControl.getSelectedEnties())
        {
            entityControl.setEntityLocation(entity, entity.getLocationIntX(), entity.getLocationIntY(), -1);
        }
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

        final Enum<?> palette = model.getSelectedPalette();
        updatePaletteMoving(palette, mx, my);
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
        updateCamera(vx, vy, WorldViewRenderer.GRID_MOVEMENT_SENSIBILITY);
    }

    @Override
    public void keyReleased(KeyEvent keyEvent)
    {
        // Nothing to do
    }
}
