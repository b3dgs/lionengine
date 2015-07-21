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

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseWheelListener;

import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.core.swt.Mouse;
import com.b3dgs.lionengine.editor.Activator;
import com.b3dgs.lionengine.editor.Tools;
import com.b3dgs.lionengine.editor.UtilEclipse;
import com.b3dgs.lionengine.editor.properties.PropertiesModel;
import com.b3dgs.lionengine.editor.properties.tile.PropertiesTile;
import com.b3dgs.lionengine.game.Camera;
import com.b3dgs.lionengine.game.collision.CollisionFunction;
import com.b3dgs.lionengine.game.configurer.ConfigTileGroup;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.map.MapTileCollision;
import com.b3dgs.lionengine.game.map.Tile;
import com.b3dgs.lionengine.game.object.Handler;
import com.b3dgs.lionengine.game.object.ObjectGame;
import com.b3dgs.lionengine.game.object.Services;
import com.b3dgs.lionengine.game.trait.transformable.Transformable;
import com.b3dgs.lionengine.geom.Point;

/**
 * World view updater, update the current world.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class WorldViewUpdater implements MouseListener, MouseMoveListener, MouseWheelListener, KeyListener
{
    /** Extension ID. */
    public static final String EXTENSION_ID = Activator.PLUGIN_ID + ".worldViewUpdater";
    /** Default zoom value. */
    public static final int ZOOM_DEFAULT = 100;
    /** Maximum zoom value. */
    public static final int ZOOM_MAX = 500;
    /** Minimum zoom value. */
    public static final int ZOOM_MIN = 20;
    /** Grid movement sensibility. */
    private static final int GRID_MOVEMENT_SENSIBILITY = 8;

    /** Part service. */
    protected final EPartService partService;
    /** Object selection listener. */
    private final Collection<ObjectSelectionListener> objectSelectionListeners = new ArrayList<>();
    /** Tile selection listener. */
    private final Collection<TileSelectionListener> tileSelectionListeners = new ArrayList<>();
    /** Camera reference. */
    private final Camera camera;
    /** Map reference. */
    private final MapTile map;
    /** Handler object. */
    private final Handler handlerObject;
    /** Selection handler. */
    private final Selection selection;
    /** Object controller. */
    private final ObjectControl objectControl;
    /** Selected tile. */
    private Tile selectedTile;
    /** Grid enabled. */
    private boolean gridEnabled;
    /** Collisions enabled. */
    private boolean collisionsEnabled;
    /** Current horizontal mouse location. */
    private int mouseX;
    /** Current vertical mouse location. */
    private int mouseY;
    /** Mouse click. */
    private int click;
    /** Zoom level in percent. */
    private int zoom = ZOOM_DEFAULT;
    /** Vertical offset. */
    private int offsetY;
    /** Old scale. */
    private double oldScale;

    /**
     * Create a world view renderer with grid enabled.
     * 
     * @param partService The part services reference.
     * @param services The services reference.
     */
    public WorldViewUpdater(EPartService partService, Services services)
    {
        this.partService = partService;
        camera = services.get(Camera.class);
        map = services.get(MapTile.class);
        handlerObject = services.get(Handler.class);
        objectControl = services.get(ObjectControl.class);
        selection = services.get(Selection.class);
        gridEnabled = true;
    }

    /**
     * Add an object selection listener.
     * 
     * @param listener The listener reference.
     */
    public void addListenerObject(ObjectSelectionListener listener)
    {
        objectSelectionListeners.add(listener);
    }

    /**
     * Add an tile selection listener.
     * 
     * @param listener The listener reference.
     */
    public void addListenerTile(TileSelectionListener listener)
    {
        tileSelectionListeners.add(listener);
    }

    /**
     * Remove an object selection listener.
     * 
     * @param listener The listener reference.
     */
    public void removeListenerObject(ObjectSelectionListener listener)
    {
        objectSelectionListeners.remove(listener);
    }

    /**
     * Remove a tile selection listener.
     * 
     * @param listener The listener reference.
     */
    public void removeListenerTile(TileSelectionListener listener)
    {
        tileSelectionListeners.remove(listener);
    }

    /**
     * Set the grid enabled state.
     */
    public void switchGridEnabled()
    {
        gridEnabled = !gridEnabled;
    }

    /**
     * Set the collisions enabled state.
     */
    public void switchCollisionsEnabled()
    {
        collisionsEnabled = !collisionsEnabled;

        if (map.hasFeature(MapTileCollision.class))
        {
            final MapTileCollision mapTileCollision = map.getFeature(MapTileCollision.class);
            if (collisionsEnabled)
            {
                mapTileCollision.createCollisionDraw();
            }
            else
            {
                mapTileCollision.clearCollisionDraw();
            }
        }
    }

    /**
     * Set the zoom percent value.
     * 
     * @param percent The new zoom percent value.
     */
    public void setZoomPercent(int percent)
    {
        zoom = UtilMath.fixBetween(percent, ZOOM_MIN, ZOOM_MAX);
    }

    /**
     * Set the vertical offset.
     * 
     * @param offsetY The vertical offset.
     */
    public void setOffsetY(int offsetY)
    {
        this.offsetY = offsetY;
    }

    /**
     * Get the handler object.
     * 
     * @return The handler object.
     */
    public Handler getHandler()
    {
        return handlerObject;
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
     * Get the horizontal mouse location.
     * 
     * @return The horizontal mouse location.
     */
    public int getMouseX()
    {
        return (int) (mouseX / getZoomScale());
    }

    /**
     * Get the vertical mouse location.
     * 
     * @return The vertical mouse location.
     */
    public int getMouseY()
    {
        return (int) (mouseY / getZoomScale()) - offsetY;
    }

    /**
     * Get the real horizontal mouse location.
     * 
     * @return The real horizontal mouse location.
     */
    public int getMx()
    {
        return mouseX;
    }

    /**
     * Get the real vertical mouse location.
     * 
     * @return The real vertical mouse location.
     */
    public int getMy()
    {
        return mouseY;
    }

    /**
     * Get the current selected tile.
     * 
     * @return The current selected tile.
     */
    public Tile getSelectedTile()
    {
        return selectedTile;
    }

    /**
     * Get the zoom percent value.
     * 
     * @return The zoom percent value.
     */
    public int getZoomPercent()
    {
        return zoom;
    }

    /**
     * Get the zoom scale.
     * 
     * @return The zoom scale.
     */
    public double getZoomScale()
    {
        final double realScale = zoom / 100.0;
        final double scale = Math.round(map.getTileWidth() * realScale) / (double) map.getTileWidth();
        return scale;
    }

    /**
     * Check if grid is enabled.
     * 
     * @return <code>true</code> if enabled, <code>false</code> else.
     */
    public boolean isGridEnabled()
    {
        return gridEnabled;
    }

    /**
     * Check if collisions is enabled.
     * 
     * @return <code>true</code> if enabled, <code>false</code> else.
     */
    public boolean isCollisionsEnabled()
    {
        return collisionsEnabled;
    }

    /**
     * Update the palette action on click down.
     * 
     * @param palette The palette type.
     */
    protected void updatePaletteBefore(Enum<?> palette)
    {
        if (palette == PaletteType.POINTER_OBJECT)
        {
            updatePointerFactory();
            updateSingleEntitySelection();
        }
        else if (palette == PaletteType.POINTER_TILE)
        {
            updatePointerTile();
        }
        else if (palette == PaletteType.POINTER_COLLISION)
        {
            updatePointerCollision();
        }
        else if (palette == PaletteType.SELECTION)
        {
            updateSelectionBefore();
        }
    }

    /**
     * Update the palette action when moving mouse.
     * 
     * @param palette The palette type.
     * @param oldMx The mouse old x.
     * @param oldMy The mouse old y.
     */
    protected void updatePaletteMoving(Enum<?> palette, int oldMx, int oldMy)
    {
        final int mx = getMouseX();
        final int my = getMouseY();
        if (palette == PaletteType.POINTER_OBJECT)
        {
            objectControl.updateMouseOver(mx, my);
            if (getClick() == Mouse.LEFT)
            {
                objectControl.updateDragging(oldMx, oldMy, mx, my);
            }
        }
        else if (palette == PaletteType.SELECTION && click == Mouse.LEFT)
        {
            if (!objectControl.hasSelection())
            {
                selection.update(mx, my);
            }
            else
            {
                objectControl.updateDragging(oldMx, oldMy, mx, my);
            }
        }
        else if (palette == PaletteType.HAND && click > 0)
        {
            updateCamera(oldMx - mx, my - oldMy, 0);
        }
    }

    /**
     * Update the palette action on click up.
     * 
     * @param palette The palette type.
     */
    protected void updatePaletteAfter(Enum<?> palette)
    {
        if (palette == PaletteType.SELECTION)
        {
            updateSelectionAfter();
        }
        else if (palette == PaletteType.HAND)
        {
            updateHand();
        }
    }

    /**
     * Update the object selection with pointer.
     */
    protected void updateSingleEntitySelection()
    {
        objectControl.unSelectEntities();
        final ObjectGame object = objectControl.getObject(getMouseX(), getMouseY());
        if (object != null)
        {
            objectControl.setObjectSelection(object, true);
        }
        for (final ObjectSelectionListener listener : objectSelectionListeners)
        {
            listener.notifyObjectSelected(object);
        }
    }

    /**
     * Update the pointer in tile case.
     */
    private void updatePointerTile()
    {
        if (map.isCreated())
        {
            final Point point = Tools.getMouseTile(map, camera, getMouseX(), getMouseY());
            selectedTile = map.getTile(point.getX() / map.getTileWidth(), point.getY() / map.getTileHeight());
        }
        else
        {
            selectedTile = null;
        }
        for (final TileSelectionListener listener : tileSelectionListeners)
        {
            listener.notifyTileSelected(selectedTile);
        }
    }

    /**
     * Update the pointer in collision case.
     */
    private void updatePointerCollision()
    {
        final WorldViewPart part = UtilEclipse.getPart(WorldViewPart.ID, WorldViewPart.class);
        final FormulaItem item = part.getToolItem(FormulaItem.ID, FormulaItem.class);
        final CollisionFunction function = item.getFunction();

        // TODO apply collision function here to trace line, and then check hit tiles to apply collision
    }

    /**
     * Update the pointer in factory case.
     */
    private void updatePointerFactory()
    {
        final int mx = getMouseX();
        final int my = getMouseY();
        if (!objectControl.isDragging())
        {
            if (click == Mouse.LEFT && !objectControl.hasOver() && !objectControl.hasSelection())
            {
                objectControl.addEntity(mx, my);
            }
            else if (click == Mouse.RIGHT)
            {
                objectControl.removeEntity(mx, my);
            }
        }
    }

    /**
     * Update the hand palette type.
     */
    private void updateHand()
    {
        final int tw = map.getTileWidth();
        final int th = map.getTileHeight();
        camera.teleport(UtilMath.getRounded(camera.getX() + tw / 2.0, tw),
                UtilMath.getRounded(camera.getY() - th / 2.0, th));
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
    }

    /**
     * Update the keyboard.
     * 
     * @param vx The keyboard horizontal movement.
     * @param vy The keyboard vertical movement.
     * @param step The movement sensibility.
     */
    private void updateCamera(double vx, double vy, int step)
    {
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
    }

    /**
     * Update the selection when clicking (select single object, or unselect all previous).
     */
    private void updateSelectionBefore()
    {
        final int mx = getMouseX();
        final int my = getMouseY();
        final ObjectGame object = objectControl.getObject(mx, my);
        selection.reset();

        if (objectControl.hasSelection() && object == null)
        {
            objectControl.unSelectEntities();
            selection.start(mx, my);
        }
        else
        {
            selection.start(mx, my);
        }
    }

    /**
     * Update the selection when releasing click (update the objects flags).
     */
    private void updateSelectionAfter()
    {
        selection.end(getMouseX(), getMouseY());
        if (selection.isSelected())
        {
            objectControl.selectEntities(selection.getArea());
        }
        final Collection<ObjectGame> selections = objectControl.getSelectedEnties();
        if (selections.size() == 1)
        {
            final ObjectGame object = selections.toArray(new ObjectGame[1])[0];
            for (final ObjectSelectionListener listener : objectSelectionListeners)
            {
                listener.notifyObjectSelected(object);
            }
        }
        else
        {
            for (final ObjectSelectionListener listener : objectSelectionListeners)
            {
                listener.notifyObjectsSelected(selections);
            }
        }
    }

    /**
     * Lock scroll to cursor.
     */
    private void updateScrollToCursor()
    {
        final int tw = map.getTileWidth();
        final int th = map.getTileHeight();

        final double scaleDiff = getZoomScale() - oldScale;

        final double ox;
        final double oy;
        if (scaleDiff > 0)
        {
            ox = Origin.BOTTOM_LEFT.getX(getMouseX(), camera.getWidth()) * scaleDiff;
            oy = Origin.BOTTOM_LEFT.getY(getMouseY(), camera.getHeight()) * scaleDiff;
        }
        else
        {
            ox = Origin.MIDDLE.getX(getMouseX(), camera.getWidth()) * scaleDiff;
            oy = Origin.MIDDLE.getY(getMouseY(), camera.getHeight()) * scaleDiff;
        }
        camera.teleport(UtilMath.getRounded(camera.getX() + ox, tw), UtilMath.getRounded(camera.getY() - oy, th));
    }

    /**
     * Check if property can be past from middle click.
     */
    private void checkPastProperty()
    {
        final Object copy = PropertiesModel.INSTANCE.getCopyData();
        final String group = PropertiesModel.INSTANCE.getCopyText();
        if (copy != null && selectedTile != null && ConfigTileGroup.GROUP.equals(copy))
        {
            PropertiesTile.changeTileGroup(map, selectedTile.getGroup(), group, selectedTile);
        }
    }

    /*
     * MouseListener
     */

    @Override
    public void mouseDown(MouseEvent mouseEvent)
    {
        final int mx = mouseEvent.x;
        final int my = mouseEvent.y;
        click = mouseEvent.button;
        updateMouse(mx, my);

        final Enum<?> palette = WorldViewModel.INSTANCE.getSelectedPalette();
        updatePaletteBefore(palette);

        if (mouseEvent.button == Mouse.MIDDLE)
        {
            checkPastProperty();
        }
    }

    @Override
    public void mouseUp(MouseEvent mouseEvent)
    {
        final int mx = mouseEvent.x;
        final int my = mouseEvent.y;
        updateMouse(mx, my);

        final Enum<?> palette = WorldViewModel.INSTANCE.getSelectedPalette();
        updatePaletteAfter(palette);

        objectControl.stopDragging();
        for (final ObjectGame object : objectControl.getSelectedEnties())
        {
            if (object.hasTrait(Transformable.class))
            {
                final Transformable transformable = object.getTrait(Transformable.class);
                final int x = (int) transformable.getX();
                final int y = (int) transformable.getY();
                objectControl.setObjectLocation(transformable, x, y, -1);
            }
        }
        click = 0;
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
        final int mx = mouseEvent.x;
        final int my = mouseEvent.y;
        final int oldMx = getMouseX();
        final int oldMy = getMouseY();
        updateMouse(mx, my);

        final Enum<?> palette = WorldViewModel.INSTANCE.getSelectedPalette();
        updatePaletteMoving(palette, oldMx, oldMy);
    }

    /*
     * MouseWheelListener
     */

    @Override
    public void mouseScrolled(MouseEvent event)
    {
        oldScale = getZoomScale();
        zoom = UtilMath.fixBetween(zoom + (int) Math.ceil(zoom * event.count / 100.0), ZOOM_MIN, ZOOM_MAX);
        final WorldViewPart part = UtilEclipse.getPart(WorldViewPart.ID, WorldViewPart.class);
        part.setToolItemText(ZoomItem.ID, String.valueOf(zoom));
        updateScrollToCursor();
    }

    /*
     * KeyListener
     */

    @Override
    public void keyPressed(KeyEvent keyEvent)
    {
        final int code = keyEvent.keyCode;

        final int vx;
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

        final int vy;
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

        final double scale = zoom / 100.0;
        updateCamera(vx / scale, vy / scale, GRID_MOVEMENT_SENSIBILITY);
    }

    @Override
    public void keyReleased(KeyEvent keyEvent)
    {
        // Nothing to do
    }
}
