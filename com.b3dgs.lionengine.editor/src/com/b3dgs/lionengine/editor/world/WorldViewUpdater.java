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

import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.core.swt.Mouse;
import com.b3dgs.lionengine.editor.Activator;
import com.b3dgs.lionengine.editor.PaletteType;
import com.b3dgs.lionengine.editor.Tools;
import com.b3dgs.lionengine.game.Camera;
import com.b3dgs.lionengine.game.map.MapTile;
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
public class WorldViewUpdater
        implements MouseListener, MouseMoveListener, KeyListener
{
    /** Extension ID. */
    public static final String EXTENSION_ID = Activator.PLUGIN_ID + ".worldViewUpdater";
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
    /** Current horizontal mouse location. */
    private int mouseX;
    /** Current vertical mouse location. */
    private int mouseY;
    /** Mouse click. */
    private int click;

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
        return mouseX;
    }

    /**
     * Get the vertical mouse location.
     * 
     * @return The vertical mouse location.
     */
    public int getMouseY()
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
     * Check if grid is enabled.
     * 
     * @return <code>true</code> if enabled, <code>false</code> else.
     */
    public boolean isGridEnabled()
    {
        return gridEnabled;
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
        if (palette == PaletteType.POINTER_OBJECT)
        {
            updatePointerFactory(mx, my);
            updateSingleEntitySelection(mx, my);
        }
        else if (palette == PaletteType.POINTER_TILE)
        {
            updatePointerMap(mx, my);
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
        if (palette == PaletteType.POINTER_OBJECT)
        {
            objectControl.updateMouseOver(mx, my);
            if (getClick() == Mouse.LEFT)
            {
                objectControl.updateDragging(mouseX, mouseY, mx, my);
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
                objectControl.updateDragging(mouseX, mouseY, mx, my);
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
     * Update the object selection with pointer.
     * 
     * @param mx The mouse horizontal location.
     * @param my The mouse vertical location.
     */
    protected void updateSingleEntitySelection(int mx, int my)
    {
        objectControl.unSelectEntities();
        final ObjectGame object = objectControl.getObject(mx, my);
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
     * Update the pointer in map case.
     * 
     * @param mx The mouse horizontal location.
     * @param my The mouse vertical location.
     */
    private void updatePointerMap(int mx, int my)
    {
        if (map.isCreated())
        {
            final Point point = Tools.getMouseTile(map, camera, mx, my);
            selectedTile = map.getTile(point.getX() / map.getTileWidth(), point.getY() / map.getTileHeight());
            if (selectedTile != null)
            {
                for (final TileSelectionListener listener : tileSelectionListeners)
                {
                    listener.notifyTileSelected(selectedTile);
                }
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
     * @param mx The mouse horizontal location.
     * @param my The mouse vertical location.
     */
    private void updatePointerFactory(int mx, int my)
    {
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
        camera.teleport(UtilMath.getRounded(camera.getX(), map.getTileWidth()),
                UtilMath.getRounded(camera.getY(), map.getTileHeight()));
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
    private void updateCamera(int vx, int vy, int step)
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
     * 
     * @param mx The mouse horizontal location.
     * @param my The mouse vertical location.
     */
    private void updateSelectionBefore(int mx, int my)
    {
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
     * 
     * @param mx The mouse horizontal location.
     * @param my The mouse vertical location.
     */
    private void updateSelectionAfter(int mx, int my)
    {
        selection.end(mx, my);
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

    /*
     * MouseListener
     */

    @Override
    public void mouseDown(MouseEvent mouseEvent)
    {
        final int mx = mouseEvent.x;
        final int my = mouseEvent.y;
        final Enum<?> palette = WorldViewModel.INSTANCE.getSelectedPalette();
        click = mouseEvent.button;

        updatePaletteBefore(palette, mx, my);
        updateMouse(mx, my);
    }

    @Override
    public void mouseUp(MouseEvent mouseEvent)
    {
        final int mx = mouseEvent.x;
        final int my = mouseEvent.y;
        final Enum<?> palette = WorldViewModel.INSTANCE.getSelectedPalette();

        updatePaletteAfter(palette, mx, my);
        updateMouse(mx, my);

        objectControl.stopDragging();
        for (final ObjectGame object : objectControl.getSelectedEnties())
        {
            if (object.hasTrait(Transformable.class))
            {
                final Transformable transformable = object.getTrait(Transformable.class);
                objectControl.setObjectLocation(transformable, (int) transformable.getX(), (int) transformable.getY(),
                        -1);
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

        final Enum<?> palette = WorldViewModel.INSTANCE.getSelectedPalette();
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
        updateCamera(vx, vy, GRID_MOVEMENT_SENSIBILITY);
    }

    @Override
    public void keyReleased(KeyEvent keyEvent)
    {
        // Nothing to do
    }
}
