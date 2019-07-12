/*
 * Copyright (C) 2013-2019 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import java.util.concurrent.atomic.AtomicReference;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Listenable;
import com.b3dgs.lionengine.ListenableModel;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.game.Cursor;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Refreshable;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.geom.Area;
import com.b3dgs.lionengine.geom.Geom;
import com.b3dgs.lionengine.geom.Rectangle;

/**
 * Handle the selector refreshing.
 */
public class SelectorRefresher extends FeatureModel implements Refreshable, Listenable<SelectorListener>
{
    /**
     * Create check action.
     * 
     * @param cursor The cursor reference.
     * @param model The selector model.
     * @param start The start action.
     * @return The check action.
     */
    private static StateUpdater createCheck(Cursor cursor, SelectorModel model, AtomicReference<StateUpdater> start)
    {
        return (extrp, current) ->
        {
            if (model.isEnabled() && cursor.getClick() == 0)
            {
                return start.get();
            }
            return current;
        };
    }

    /** List of listeners. */
    private final ListenableModel<SelectorListener> listenable = new ListenableModel<>();
    /** Current update action. */
    private StateUpdater action;
    /** Mouse location x when started click selection. */
    private double startX;
    /** Mouse location y when started click selection. */
    private double startY;

    /**
     * Create feature.
     * <p>
     * The {@link Services} must provide:
     * </p>
     * <ul>
     * <li>{@link Viewer}</li>
     * <li>{@link Cursor}</li>
     * </ul>
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param model The model reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public SelectorRefresher(Services services, SelectorModel model)
    {
        super();

        Check.notNull(services);
        Check.notNull(model);

        final Viewer viewer = services.get(Viewer.class);
        final Cursor cursor = services.get(Cursor.class);

        final AtomicReference<StateUpdater> actionStart = new AtomicReference<>();
        final StateUpdater check = createCheck(cursor, model, actionStart);
        final StateUpdater select = createSelection(viewer, cursor, model, check);
        actionStart.set(createStart(cursor, model, check, select));

        action = check;
    }

    /**
     * Create start action.
     * 
     * @param cursor The cursor reference.
     * @param model The selector model.
     * @param check The check action.
     * @param select The selection action.
     * @return The start action.
     */
    private StateUpdater createStart(Cursor cursor, SelectorModel model, StateUpdater check, StateUpdater select)
    {
        return (extrp, current) ->
        {
            StateUpdater next = current;
            if (!model.isEnabled())
            {
                next = check;
            }
            else if (model.getSelectionClick() == cursor.getClick())
            {
                checkBeginSelection(cursor, model);
                if (model.isSelecting())
                {
                    next = select;
                }
            }
            return next;
        };
    }

    /**
     * Check if can begin selection. Notify listeners if started.
     * 
     * @param cursor The cursor reference.
     * @param model The selector model.
     */
    private void checkBeginSelection(Cursor cursor, SelectorModel model)
    {
        final boolean canClick = !model.getClickableArea().contains(cursor.getScreenX(), cursor.getScreenY());
        if (!model.isSelecting() && !canClick)
        {
            model.setSelecting(true);
            startX = cursor.getX();
            startY = cursor.getY();

            for (int i = 0; i < listenable.size(); i++)
            {
                listenable.get(i).notifySelectionStarted(Geom.createArea(startX, startY, 0, 0));
            }
        }
    }

    /**
     * Create selection action.
     * 
     * @param viewer The viewer reference.
     * @param cursor The cursor reference.
     * @param model The selector model.
     * @param check The check action.
     * @return The selection action.
     */
    private StateUpdater createSelection(Viewer viewer, Cursor cursor, SelectorModel model, StateUpdater check)
    {
        return (extrp, current) ->
        {
            computeSelection(viewer, cursor, model);

            if (model.getSelectionClick() != cursor.getClick())
            {
                final Rectangle sel = model.getSelectionArea();
                final Area done = Geom.createArea(sel.getX(), sel.getY(), sel.getWidthReal(), sel.getHeightReal());

                for (int i = 0; i < listenable.size(); i++)
                {
                    listenable.get(i).notifySelectionDone(done);
                }

                sel.set(-1, -1, 0, 0);
                model.setSelecting(false);

                return check;
            }
            return current;
        };
    }

    /**
     * Compute the selection from cursor location.
     * 
     * @param viewer The viewer reference.
     * @param cursor The cursor reference.
     * @param model The selector model.
     */
    private void computeSelection(Viewer viewer, Cursor cursor, SelectorModel model)
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
     * Listenable
     */

    @Override
    public void addListener(SelectorListener listener)
    {
        listenable.addListener(listener);
    }

    @Override
    public void removeListener(SelectorListener listener)
    {
        listenable.removeListener(listener);
    }

    /*
     * Refreshable
     */

    @Override
    public void update(double extrp)
    {
        action = action.update(extrp, action);
    }

    /**
     * State updater implementation.
     */
    @FunctionalInterface
    private interface StateUpdater
    {
        /**
         * Update the state.
         * 
         * @param extrp The extrapolation value.
         * @param current The current state.
         * @return The next state or same if unchanged.
         */
        StateUpdater update(double extrp, StateUpdater current);
    }
}
