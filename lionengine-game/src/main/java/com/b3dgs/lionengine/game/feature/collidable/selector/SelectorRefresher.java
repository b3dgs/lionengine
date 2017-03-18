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

import java.util.Collection;
import java.util.HashSet;

import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.game.Cursor;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.Services;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Refreshable;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.geom.Rectangle;
import com.b3dgs.lionengine.util.UtilMath;

/**
 * Handle the selector refreshing.
 */
public class SelectorRefresher extends FeatureModel implements Refreshable
{
    /** List of listeners. */
    private final Collection<SelectorListener> listeners = new HashSet<SelectorListener>(1);
    /** Selection check action. */
    private final Updatable actionCheck = new Updatable()
    {
        @Override
        public void update(double extrp)
        {
            if (model.isEnabled() && cursor.getClick() == 0)
            {
                action = actionStart;
            }
        }
    };
    /** Selection check action. */
    private final Updatable actionStart = new Updatable()
    {
        @Override
        public void update(double extrp)
        {
            if (!model.isEnabled())
            {
                action = actionCheck;
            }
            else if (model.getSelectionClick() == cursor.getClick())
            {
                checkBeginSelection();
                if (model.isSelecting())
                {
                    action = actionSelection;
                }
            }
        }
    };
    /** Selection action. */
    private final Updatable actionSelection = new Updatable()
    {
        @Override
        public void update(double extrp)
        {
            computeSelection();
            if (model.getSelectionClick() != cursor.getClick())
            {
                action = actionDone;
                action.update(extrp);
            }
        }
    };
    /** Selection done action. */
    private final Updatable actionDone = new Updatable()
    {
        @Override
        public void update(double extrp)
        {
            collidable.setEnabled(true);
            final Rectangle selectionArea = model.getSelectionArea();
            final Rectangle done = new Rectangle(selectionArea.getX(),
                                                 selectionArea.getY(),
                                                 selectionArea.getWidthReal(),
                                                 selectionArea.getHeightReal());
            for (final SelectorListener listener : listeners)
            {
                listener.notifySelectionDone(done);
            }
            selectionArea.set(-1, -1, 0, 0);
            model.setSelecting(false);
            action = actionReset;
        }
    };
    /** Selection reset action. */
    private final Updatable actionReset = new Updatable()
    {
        @Override
        public void update(double extrp)
        {
            collidable.setEnabled(false);
            action = actionCheck;
        }
    };
    /** Selector model reference. */
    private final SelectorModel model;
    /** Viewer reference. */
    private final Viewer viewer;
    /** Cursor reference. */
    private final Cursor cursor;
    /** Collidable reference. */
    private Collidable collidable;
    /** Current update action. */
    private Updatable action = actionCheck;
    /** Mouse location x when started click selection. */
    private double startX;
    /** Mouse location y when started click selection. */
    private double startY;

    /**
     * Create a selector.
     * <p>
     * The {@link Services} must provide:
     * </p>
     * <ul>
     * <li>{@link Viewer}</li>
     * <li>{@link Cursor}</li>
     * </ul>
     * 
     * @param services The services reference.
     * @param model The model reference.
     */
    public SelectorRefresher(Services services, SelectorModel model)
    {
        super();

        this.model = model;

        viewer = services.get(Viewer.class);
        cursor = services.get(Cursor.class);
    }

    /**
     * Add a selector listener.
     * 
     * @param listener The selector listener reference.
     */
    public void addListener(SelectorListener listener)
    {
        listeners.add(listener);
    }

    /**
     * Remove a selector listener.
     * 
     * @param listener The selector listener reference.
     */
    public void removeListener(SelectorListener listener)
    {
        listeners.add(listener);
    }

    /**
     * Check if can begin selection. Notify listeners if started.
     */
    private void checkBeginSelection()
    {
        final boolean canClick = !model.getClickableArea().contains(cursor.getScreenX(), cursor.getScreenY());
        if (!model.isSelecting() && !canClick)
        {
            model.setSelecting(true);
            startX = cursor.getX();
            startY = cursor.getY();

            for (final SelectorListener listener : listeners)
            {
                listener.notifySelectionStarted(new Rectangle(startX, startY, 0, 0));
            }
        }
    }

    /**
     * Compute the selection from cursor location.
     */
    private void computeSelection()
    {
        final double viewX = viewer.getX() + viewer.getViewX();
        final double viewY = viewer.getY() - viewer.getViewY();
        final double currentX = UtilMath.clamp(cursor.getX(), viewX, viewX + viewer.getWidth());
        final double currentY = UtilMath.clamp(cursor.getY(), viewY, viewY + viewer.getHeight());

        double selectX = startX;
        double selectY = startY;

        // Viewer Y axis is inverted compared to screen axis
        model.setSelectRawY(selectY);
        model.setSelectRawH(startY - currentY);

        double selectW = currentX - startX;
        if (selectW < 0)
        {
            selectX += selectW;
            selectW = -selectW;
        }

        double selectH = currentY - startY;
        if (selectH < 0)
        {
            selectY += selectH;
            selectH = -selectH;
        }
        model.getSelectionArea().set(selectX, selectY, selectW, selectH);
    }

    /*
     * Refreshable
     */

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        collidable = provider.getFeature(Collidable.class);
    }

    @Override
    public void update(double extrp)
    {
        action.update(extrp);
    }
}
