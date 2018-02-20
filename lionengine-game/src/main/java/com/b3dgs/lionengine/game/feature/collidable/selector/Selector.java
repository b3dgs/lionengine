/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.collidable.selector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.game.Cursor;
import com.b3dgs.lionengine.game.feature.FeaturableModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.TransformableModel;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.collidable.CollidableListener;
import com.b3dgs.lionengine.game.feature.collidable.CollidableModel;
import com.b3dgs.lionengine.game.feature.collidable.Collision;
import com.b3dgs.lionengine.geom.Rectangle;
import com.b3dgs.lionengine.graphic.ColorRgba;

/**
 * This class allows to perform a selection inside a dedicated area, and retrieve the {@link Rectangle} representing the
 * selection. Usage example:
 * <ul>
 * <li>{@link #setClickableArea(Viewer)} - Required to define the area where selection is allowed</li>
 * <li>{@link #setClickSelection(int)} - Recommended to set which mouse click should be used to start selection</li>
 * <li>{@link #setSelectionColor(ColorRgba)} - Optional for a custom color selection</li>
 * </ul>
 * Result can be retrieved by using {@link #addListener(SelectorListener)} to notify them the computed selection.
 * It will be then easy to check if objects are inside this area, and set them as <i>selected</i>.
 * <p>
 * {@link Selectable} will be notified with {@link Selectable#onSelection(boolean)} when selection changed.
 * </p>
 * 
 * @see SelectorListener
 * @see Cursor
 * @see Viewer
 */
public class Selector extends FeaturableModel implements Updatable, SelectorConfigurer, CollidableListener
{
    /** Void notify. */
    private static final Action CHECK = () ->
    {
        // Nothing to do
    };

    /** Selector model. */
    private final SelectorModel model = addFeatureAndGet(new SelectorModel());
    /** Selector refresher. */
    private final SelectorRefresher refresher;
    /** Selector displayer. */
    private final SelectorDisplayer displayer;
    /** Backed selection. */
    private final List<Selectable> selected = new ArrayList<>();
    /** Selection listeners. */
    private final List<SelectionListener> listeners = new ArrayList<>();
    /** Void notify. */
    private final Action notifyAll = () ->
    {
        final int n = listeners.size();
        for (int i = 0; i < n; i++)
        {
            listeners.get(i).notifySelected(selected);
        }
        notifyAction = CHECK;
    };
    /** Notify action. */
    private Action notifyAction = CHECK;

    /**
     * Create the selector.
     * <p>
     * The {@link Services} must provide:
     * </p>
     * <ul>
     * <li>{@link Viewer}</li>
     * <li>{@link Cursor}</li>
     * </ul>
     * 
     * @param services The services reference.
     */
    public Selector(Services services)
    {
        super();

        final Transformable transformable = addFeatureAndGet(new TransformableModel());
        final Collidable collidable = addFeatureAndGet(new CollidableModel(services));
        collidable.addCollision(Collision.AUTOMATIC);

        refresher = addFeatureAndGet(new SelectorRefresher(services, model));
        displayer = addFeatureAndGet(new SelectorDisplayer(services, model));

        addListener(new SelectorListener()
        {
            @Override
            public void notifySelectionStarted(Rectangle selection)
            {
                unSelectAll();
            }

            @Override
            public void notifySelectionDone(Rectangle selection)
            {
                transformable.transform(selection.getX(),
                                        selection.getY(),
                                        selection.getWidth(),
                                        selection.getHeight());
                notifyAction = notifyAll;
            }
        });
    }

    /**
     * Add a selector listener.
     * 
     * @param listener The selector listener reference.
     */
    public final void addListener(SelectorListener listener)
    {
        refresher.addListener(listener);
    }

    /**
     * Add a selection listener.
     * 
     * @param listener The selection listener reference.
     */
    public final void addListener(SelectionListener listener)
    {
        listeners.add(listener);
    }

    /**
     * Remove a selector listener.
     * 
     * @param listener The selector listener reference.
     */
    public final void removeListener(SelectorListener listener)
    {
        refresher.removeListener(listener);
    }

    /**
     * Set the selection color.
     * 
     * @param color The selection color.
     */
    public void setSelectionColor(ColorRgba color)
    {
        displayer.setSelectionColor(color);
    }

    /**
     * Get the current selection as read only.
     * 
     * @return The current selection.
     */
    public List<Selectable> getSelection()
    {
        return Collections.unmodifiableList(selected);
    }

    /**
     * Unselect all elements.
     */
    private void unSelectAll()
    {
        final int n = selected.size();
        for (int i = 0; i < n; i++)
        {
            selected.get(i).onSelection(false);
        }
        selected.clear();
    }

    /*
     * Updatable
     */

    @Override
    public void update(double extrp)
    {
        notifyAction.notifySelection();
    }

    /*
     * SelectorConfigurer
     */

    @Override
    public void setClickSelection(int click)
    {
        model.setClickSelection(click);
    }

    @Override
    public void setClickableArea(Rectangle area)
    {
        model.setClickableArea(area);
    }

    @Override
    public void setClickableArea(Viewer viewer)
    {
        model.setClickableArea(viewer);
    }

    @Override
    public void setEnabled(boolean enabled)
    {
        model.setEnabled(enabled);
    }

    /*
     * CollidableListener
     */

    @Override
    public void notifyCollided(Collidable collidable)
    {
        if (collidable.hasFeature(Selectable.class))
        {
            final Selectable selectable = collidable.getFeature(Selectable.class);
            selectable.onSelection(true);
            selected.add(selectable);
        }
    }

    /**
     * Notify action.
     */
    private interface Action
    {
        /**
         * Notify current selection.
         */
        void notifySelection();
    }
}
