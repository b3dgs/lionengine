/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.selector.it;

import java.util.concurrent.atomic.AtomicReference;

import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.core.drawable.Drawable;
import com.b3dgs.lionengine.game.Action;
import com.b3dgs.lionengine.game.Cursor;
import com.b3dgs.lionengine.game.FeaturableModel;
import com.b3dgs.lionengine.game.Service;
import com.b3dgs.lionengine.game.Setup;
import com.b3dgs.lionengine.game.feature.DisplayableModel;
import com.b3dgs.lionengine.game.feature.Handler;
import com.b3dgs.lionengine.game.feature.LayerableModel;
import com.b3dgs.lionengine.game.feature.RefreshableModel;
import com.b3dgs.lionengine.game.feature.actionable.Actionable;
import com.b3dgs.lionengine.game.feature.actionable.ActionableModel;
import com.b3dgs.lionengine.game.feature.assignable.Assign;
import com.b3dgs.lionengine.game.feature.assignable.Assignable;
import com.b3dgs.lionengine.game.feature.assignable.AssignableModel;
import com.b3dgs.lionengine.game.feature.selector.Selector;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Renderable;
import com.b3dgs.lionengine.graphic.SpriteTiled;
import com.b3dgs.lionengine.io.Mouse;

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
    @Service protected Cursor cursor;
    /** Handler reference. */
    @Service protected Handler handler;
    /** Selector reference. */
    @Service protected Selector selector;

    /**
     * Create move action.
     * 
     * @param setup The setup reference.
     */
    public ActionModel(Setup setup)
    {
        super();

        addFeature(new LayerableModel(4, 6));

        actionable = addFeatureAndGet(new ActionableModel(setup));
        actionable.setClickAction(Mouse.LEFT);
        actionable.setAction(new Action()
        {
            @Override
            public void execute()
            {
                cursor.setSurfaceId(1);
                cursor.setRenderingOffset(-5, -5);
                selector.setEnabled(false);
                state.set(assignable);
                ActionModel.this.action();
            }
        });
        state = new AtomicReference<Updatable>(actionable);

        assignable = addFeatureAndGet(new AssignableModel());
        assignable.setClickAssign(Mouse.LEFT);
        assignable.setAssign(new Assign()
        {
            @Override
            public void assign()
            {
                ActionModel.this.assign();
                cursor.setSurfaceId(0);
                cursor.setRenderingOffset(0, 0);
                selector.setEnabled(true);
                state.set(actionable);
            }
        });

        final SpriteTiled surface = Drawable.loadSpriteTiled(setup.getSurface(), 27, 19);
        surface.setLocation(actionable.getButton().getX(), actionable.getButton().getY());

        addFeature(new RefreshableModel(new Updatable()
        {
            @Override
            public void update(double extrp)
            {
                state.get().update(extrp);
                ActionModel.this.update(extrp);
            }
        }));

        addFeature(new DisplayableModel(new Renderable()
        {
            @Override
            public void render(Graphic g)
            {
                surface.render(g);
                ActionModel.this.render(g);
            }
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
