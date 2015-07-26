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
package com.b3dgs.lionengine.editor.world.updater;

import java.util.ArrayList;
import java.util.Collection;

import com.b3dgs.lionengine.core.swt.Mouse;
import com.b3dgs.lionengine.editor.world.ObjectControl;
import com.b3dgs.lionengine.editor.world.ObjectSelectionListener;
import com.b3dgs.lionengine.editor.world.PaletteType;
import com.b3dgs.lionengine.editor.world.Selection;
import com.b3dgs.lionengine.editor.world.WorldModel;
import com.b3dgs.lionengine.game.object.ObjectGame;
import com.b3dgs.lionengine.game.object.Services;
import com.b3dgs.lionengine.game.trait.transformable.Transformable;

/**
 * Handle the interaction with objects.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class WorldInteractionObject implements WorldMouseClickListener, WorldMouseMoveListener
{
    /** Object selection listener. */
    private final Collection<ObjectSelectionListener> objectSelectionListeners = new ArrayList<>();
    /** Selection handler. */
    private final Selection selection;
    /** Object controller. */
    private final ObjectControl objectControl;

    /**
     * Create the interactions handler.
     * 
     * @param services The services reference.
     */
    public WorldInteractionObject(Services services)
    {
        objectControl = services.get(ObjectControl.class);
        selection = services.get(Selection.class);
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
     * Update the object selection with pointer.
     * 
     * @param mx The current horizontal location.
     * @param my The current vertical location.
     */
    private void selectObject(int mx, int my)
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
     * Start the selection (select single object, or unselect all previous).
     * 
     * @param mx The current horizontal location.
     * @param my The current vertical location.
     */
    private void startSelection(int mx, int my)
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
        if (click == Mouse.LEFT)
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
    }

    /**
     * Update the selection.
     * 
     * @param click The mouse click.
     * @param oldMx The mouse old x.
     * @param oldMy The mouse old y.
     * @param mx The mouse x.
     * @param my The mouse y.
     */
    private void updateSelection(int click, int oldMx, int oldMy, int mx, int my)
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
     * WorldMouseClickListener
     */

    @Override
    public void onMousePressed(int click, int mx, int my)
    {
        final Enum<?> palette = WorldModel.INSTANCE.getSelectedPalette();
        if (palette == PaletteType.POINTER_OBJECT)
        {
            controlObject(click, mx, my);
            selectObject(mx, my);
        }
        else if (palette == PaletteType.SELECTION)
        {
            startSelection(mx, my);
        }
    }

    @Override
    public void onMouseReleased(int click, int mx, int my)
    {
        final Enum<?> palette = WorldModel.INSTANCE.getSelectedPalette();
        if (palette == PaletteType.POINTER_OBJECT)
        {
            endDragging();
        }
        else if (palette == PaletteType.SELECTION)
        {
            endSelection(mx, my);
        }

    }

    /*
     * WorldMouseMoveListener
     */

    @Override
    public void onMouseMoved(int click, int oldMx, int oldMy, int mx, int my)
    {
        final Enum<?> palette = WorldModel.INSTANCE.getSelectedPalette();
        if (palette == PaletteType.POINTER_OBJECT)
        {
            updateDragging(click, oldMx, oldMy, mx, my);
        }
        else if (palette == PaletteType.SELECTION && click == Mouse.LEFT)
        {
            updateSelection(click, oldMx, oldMy, mx, my);
        }
    }
}
