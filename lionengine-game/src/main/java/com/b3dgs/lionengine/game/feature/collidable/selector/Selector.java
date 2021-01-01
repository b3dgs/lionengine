/*
 * Copyright (C) 2013-2021 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.collidable.selector;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Listenable;
import com.b3dgs.lionengine.ListenableModel;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.game.Cursor;
import com.b3dgs.lionengine.game.feature.FeaturableAbstract;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.collidable.ComponentCollision;
import com.b3dgs.lionengine.geom.Area;
import com.b3dgs.lionengine.graphic.ColorRgba;

/**
 * This class allows to perform a selection inside a dedicated area, and retrieve the {@link Area} representing the
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
public class Selector extends FeaturableAbstract implements SelectorConfigurer, Listenable<SelectionListener>
{
    /** Backed selection. */
    private final List<Selectable> selected = new ArrayList<>();
    /** Selection listeners. */
    private final ListenableModel<SelectionListener> listenable = new ListenableModel<>();
    /** Selector model. */
    private final SelectorModel model;
    /** Selector refresher. */
    private final SelectorRefresher refresher;
    /** Selector displayer. */
    private final SelectorDisplayer displayer;
    /** Accept selection filter. */
    private BiPredicate<List<Selectable>, Selectable> filter = (c, s) -> true;

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
     * @param services The services reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public Selector(Services services)
    {
        super();

        Check.notNull(services);

        model = addFeatureAndGet(new SelectorModel());
        refresher = addFeatureAndGet(new SelectorRefresher(services, model));
        displayer = addFeatureAndGet(new SelectorDisplayer(services, model));

        final ComponentCollision componentCollision = services.get(ComponentCollision.class);

        // CHECKSTYLE IGNORE LINE: AnonInnerLengthCheck
        addListener(new SelectorListener()
        {
            @Override
            public void notifySelectionStarted(Area selection)
            {
                unSelectAll();

                for (int i = 0; i < listenable.size(); i++)
                {
                    listenable.get(i).notifySelectionStarted();
                }
            }

            @Override
            public void notifySelectionDone(Area selection)
            {
                checkSelection(componentCollision, selection);

                for (int i = 0; i < listenable.size(); i++)
                {
                    listenable.get(i).notifySelected(selected);
                }
            }
        });
    }

    /**
     * Set the accept function selection filter.
     * 
     * @param filter The accept filter (must not be <code>null</code>).
     * @throws LionEngineException If invalid argument.
     */
    public void setAccept(BiPredicate<List<Selectable>, Selectable> filter)
    {
        Check.notNull(filter);

        this.filter = filter;
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
     * Get the current selection.
     * 
     * @return The current selection.
     */
    public List<Selectable> getSelection()
    {
        return selected;
    }

    /**
     * Check selection depending of area.
     * 
     * @param componentCollision The component collision reference.
     * @param selection The current selection area.
     */
    private void checkSelection(ComponentCollision componentCollision, Area selection)
    {
        for (final Collidable collidable : componentCollision.getInside(selection))
        {
            if (collidable.hasFeature(Selectable.class))
            {
                final Selectable selectable = collidable.getFeature(Selectable.class);
                if (filter.test(selected, selectable))
                {
                    selectable.onSelection(true);
                    selected.add(selectable);
                }
            }
        }
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
     * Listenable
     */

    @Override
    public final void addListener(SelectionListener listener)
    {
        listenable.addListener(listener);
    }

    @Override
    public void removeListener(SelectionListener listener)
    {
        listenable.removeListener(listener);
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
    public void setClickableArea(Area area)
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
     * Featurable
     */

    /**
     * Return always <code>null</code>.
     */
    @Override
    public Media getMedia()
    {
        return null;
    }
}
