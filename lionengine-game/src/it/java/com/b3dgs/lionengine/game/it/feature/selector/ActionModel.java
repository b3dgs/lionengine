/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.it.feature.selector;

import java.util.concurrent.atomic.AtomicReference;

import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.awt.MouseAwt;
import com.b3dgs.lionengine.game.Cursor;
import com.b3dgs.lionengine.game.feature.Actionable;
import com.b3dgs.lionengine.game.feature.ActionableModel;
import com.b3dgs.lionengine.game.feature.DisplayableModel;
import com.b3dgs.lionengine.game.feature.FeaturableModel;
import com.b3dgs.lionengine.game.feature.Handler;
import com.b3dgs.lionengine.game.feature.LayerableModel;
import com.b3dgs.lionengine.game.feature.RefreshableModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.assignable.Assignable;
import com.b3dgs.lionengine.game.feature.assignable.AssignableModel;
import com.b3dgs.lionengine.game.feature.collidable.selector.Selector;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.SpriteTiled;

/**
 * Move action.
 */
public class ActionModel extends FeaturableModel
{
    /** Actionnable reference. */
    protected final Actionable actionable;
    /** Assignable reference. */
    protected final Assignable assignable;
    /** Current state. */
    protected final AtomicReference<Updatable> state;

    /** Cursor reference. */
    protected final Cursor cursor;
    /** Handler reference. */
    protected final Handler handler;
    /** Selector reference. */
    protected final Selector selector;

    /**
     * Create move action.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     */
    public ActionModel(Services services, Setup setup)
    {
        super(services, setup);

        cursor = services.get(Cursor.class);
        handler = services.get(Handler.class);
        selector = services.get(Selector.class);

        addFeature(new LayerableModel(4, 6));

        actionable = addFeature(ActionableModel.class, services, setup);
        actionable.setClickAction(MouseAwt.LEFT);

        state = new AtomicReference<>(actionable);

        assignable = addFeature(AssignableModel.class, services, setup);
        assignable.setClickAssign(MouseAwt.LEFT);

        actionable.setAction(() ->
        {
            cursor.setSurfaceId(1);
            cursor.setRenderingOffset(-5, -5);
            selector.setEnabled(false);
            state.set(assignable);
            ActionModel.this.action();
        });

        assignable.setAssign(() ->
        {
            ActionModel.this.assign();
            cursor.setSurfaceId(0);
            cursor.setRenderingOffset(0, 0);
            selector.setEnabled(true);
            state.set(actionable);
        });

        final SpriteTiled surface = Drawable.loadSpriteTiled(setup.getSurface(), 27, 19);
        surface.setLocation(actionable.getButton().getX(), actionable.getButton().getY());

        addFeature(new RefreshableModel(extrp ->
        {
            state.get().update(extrp);
            ActionModel.this.update(extrp);
        }));

        addFeature(new DisplayableModel(g ->
        {
            surface.render(g);
            ActionModel.this.render(g);
        }));
    }

    /**
     * Action implementation.
     */
    protected void action()
    {
        // Nothing by default
    }

    /**
     * Assign implementation.
     */
    protected void assign()
    {
        // Nothing by default
    }

    /**
     * Additional update implementation.
     * 
     * @param extrp The extrapolation value.
     */
    protected void update(double extrp)
    {
        // Nothing by default
    }

    /**
     * Additional render implementation.
     * 
     * @param g The output graphic.
     */
    protected void render(Graphic g)
    {
        // Nothing by default
    }
}
