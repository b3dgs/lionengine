/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.editor.object.world.updater;

import java.util.ArrayList;
import java.util.Collection;

import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.editor.ObjectRepresentation;
import com.b3dgs.lionengine.editor.object.world.ObjectControl;
import com.b3dgs.lionengine.editor.world.PaletteModel;
import com.b3dgs.lionengine.editor.world.PaletteType;
import com.b3dgs.lionengine.editor.world.Selection;
import com.b3dgs.lionengine.editor.world.updater.WorldMouseClickListener;
import com.b3dgs.lionengine.editor.world.updater.WorldMouseMoveListener;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.Refreshable;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.swt.graphic.MouseSwt;

/**
 * Handle the interaction with objects.
 */
public class WorldInteractionObject implements WorldMouseClickListener, WorldMouseMoveListener
{
    /** Object selection listener. */
    private final Collection<ObjectSelectionListener> objectSelectionListeners = new ArrayList<>();
    /** Selection handler. */
    private final Selection selection;
    /** Object controller. */
    private final ObjectControl objectControl;
    /** Palette model. */
    private final PaletteModel palette;
    /** Map reference. */
    private final MapTile map;

    /**
     * Create the interactions handler.
     * 
     * @param services The services reference.
     */
    public WorldInteractionObject(Services services)
    {
        super();

        objectControl = services.create(ObjectControl.class);
        selection = services.get(Selection.class);
        palette = services.get(PaletteModel.class);
        map = services.get(MapTile.class);
    }

    /**
     * Add an object selection listener.
     * 
     * @param listener The listener reference.
     */
    public void addListener(ObjectSelectionListener listener)
    {
        objectSelectionListeners.add(listener);
    }

    /**
     * Remove an object selection listener.
     * 
     * @param listener The listener reference.
     */
    public void removeListener(ObjectSelectionListener listener)
    {
        objectSelectionListeners.remove(listener);
    }

    /**
     * Update the pointer in factory case.
     * 
     * @param click The mouse click.
     * @param mx The current horizontal location.
     * @param my The current vertical location.
     */
    private void controlObject(int click, int mx, int my)
    {
        if (!objectControl.isDragging())
        {
            if (click == MouseSwt.LEFT.intValue() && !objectControl.hasOver() && !objectControl.hasSelection())
            {
                objectControl.addAt(mx, my);
            }
            else if (click == MouseSwt.RIGHT.intValue())
            {
                objectControl.removeFrom(mx, my);
            }
        }
    }

    /**
     * Update the object selection with pointer.
     * 
     * @param mx The current horizontal location.
     * @param my The current vertical location.
     */
    private void selectObject(int mx, int my)
    {
        objectControl.unselectObjects();
        final ObjectRepresentation object = objectControl.getFeaturable(mx, my);
        if (object != null)
        {
            objectControl.setObjectSelection(object, true);
            for (final ObjectSelectionListener listener : objectSelectionListeners)
            {
                listener.notifyObjectSelected(object.getFeature(Transformable.class));
            }
        }
    }

    /**
     * Start the selection (select single object, or unselect all previous).
     * 
     * @param mx The current horizontal location.
     * @param my The current vertical location.
     */
    private void startSelection(int mx, int my)
    {
        final Featurable featurable = objectControl.getFeaturable(mx, my);
        selection.reset();

        if (featurable == null && objectControl.hasSelection())
        {
            objectControl.unselectObjects();
        }
        selection.start(mx, my);
    }

    /**
     * Update the object dragging.
     * 
     * @param click The mouse click.
     * @param oldMx The mouse old x.
     * @param oldMy The mouse old y.
     * @param mx The mouse x.
     * @param my The mouse y.
     */
    private void updateDragging(int click, int oldMx, int oldMy, int mx, int my)
    {
        objectControl.updateMouseOver(mx, my);
        if (click == MouseSwt.LEFT.intValue())
        {
            objectControl.updateDragging(oldMx, oldMy, mx, my);
        }
    }

    /**
     * Once object dragging is done, ensure location are grid aligned.
     */
    private void endDragging()
    {
        objectControl.stopDragging();
        for (final Transformable transformable : objectControl.getSelectedObjects())
        {
            alignToGrid(transformable);
        }
    }

    /**
     * Align to grid transformable.
     * 
     * @param transformable The transformable to align.
     */
    private void alignToGrid(Transformable transformable)
    {
        final int x = UtilMath.getRounded(transformable.getX(), map.isCreated() ? map.getTileWidth() : 1);
        final int y = UtilMath.getRoundedR(transformable.getY(), map.isCreated() ? map.getTileHeight() : 1);

        final double sx = x
                          + UtilMath.getRounded(transformable.getWidth(), map.getTileWidth()) / 2
                          - UtilMath.getRounded(transformable.getWidth() / 2.0, map.getTileWidth());

        transformable.teleport(sx, y);
        transformable.getFeature(Refreshable.class).update(1.0);
    }

    /**
     * Update the selection.
     * 
     * @param oldMx The mouse old x.
     * @param oldMy The mouse old y.
     * @param mx The mouse x.
     * @param my The mouse y.
     */
    private void updateSelection(int oldMx, int oldMy, int mx, int my)
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

    /**
     * End the selection (update the objects flags).
     * 
     * @param mx The current horizontal location.
     * @param my The current vertical location.
     */
    private void endSelection(int mx, int my)
    {
        selection.end(mx, my);
        if (selection.isSelected())
        {
            objectControl.selectObjects(selection.getArea());
        }
        final Collection<Transformable> selections = objectControl.getSelectedObjects();
        for (final Transformable transformable : selections)
        {
            alignToGrid(transformable);
        }
        if (selections.size() == 1)
        {
            final Transformable object = selections.toArray(new Transformable[1])[0];
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

    @Override
    public void onMousePressed(int click, int mx, int my)
    {
        if (palette.isPalette(PaletteType.POINTER_OBJECT))
        {
            controlObject(click, mx, my);
            selectObject(mx, my);
        }
        else if (palette.isPalette(PaletteType.SELECTION))
        {
            startSelection(mx, my);
        }
    }

    @Override
    public void onMouseReleased(int click, int mx, int my)
    {
        if (palette.isPalette(PaletteType.POINTER_OBJECT))
        {
            endDragging();
        }
        else if (palette.isPalette(PaletteType.SELECTION))
        {
            endSelection(mx, my);
        }
    }

    @Override
    public void onMouseMoved(int click, int oldMx, int oldMy, int mx, int my)
    {
        if (palette.isPalette(PaletteType.POINTER_OBJECT))
        {
            updateDragging(click, oldMx, oldMy, mx, my);
        }
        else if (click == MouseSwt.LEFT.intValue() && palette.isPalette(PaletteType.SELECTION))
        {
            updateSelection(oldMx, oldMy, mx, my);
        }
    }
}
