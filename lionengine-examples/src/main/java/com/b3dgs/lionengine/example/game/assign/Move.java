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
package com.b3dgs.lionengine.example.game.assign;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.core.awt.Mouse;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.Image;
import com.b3dgs.lionengine.game.Cursor;
import com.b3dgs.lionengine.game.Services;
import com.b3dgs.lionengine.game.handler.Handler;
import com.b3dgs.lionengine.game.object.ObjectGame;
import com.b3dgs.lionengine.game.object.SetupSurface;
import com.b3dgs.lionengine.game.object.feature.actionable.Action;
import com.b3dgs.lionengine.game.object.feature.actionable.Actionable;
import com.b3dgs.lionengine.game.object.feature.actionable.ActionableModel;
import com.b3dgs.lionengine.game.object.feature.assignable.Assign;
import com.b3dgs.lionengine.game.object.feature.assignable.Assignable;
import com.b3dgs.lionengine.game.object.feature.assignable.AssignableModel;
import com.b3dgs.lionengine.game.pathfinding.Pathfindable;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Renderable;
import com.b3dgs.lionengine.graphic.Text;

/**
 * Move action.
 */
class Move extends ObjectGame implements Action, Assign, Updatable, Renderable
{
    /** Media reference. */
    public static final Media MEDIA = Medias.create("Move.xml");

    /** Assignable model. */
    private final Assignable assignable = addFeatureAndGet(new AssignableModel());
    /** Actionable model. */
    private final Actionable actionable;
    /** Button image. */
    private final Image image;
    /** Text reference. */
    private final Text text;
    /** Cursor reference. */
    private final Cursor cursor;
    /** Handler reference. */
    private final Handler handler;
    /** Current action state. */
    private Updatable state;

    /**
     * Create move action.
     * 
     * @param setup The setup reference.
     * @param services The services reference.
     */
    public Move(SetupSurface setup, Services services)
    {
        super(setup, services);

        actionable = addFeatureAndGet(new ActionableModel(setup));

        text = services.get(Text.class);
        cursor = services.get(Cursor.class);
        handler = services.get(Handler.class);
        image = Drawable.loadImage(setup.getSurface());

        actionable.setClickAction(Mouse.LEFT);
        assignable.setClickAssign(Mouse.LEFT);
        state = actionable;
    }

    @Override
    protected void onPrepared()
    {
        image.setLocation(actionable.getButton().getX(), actionable.getButton().getY());
    }

    @Override
    public void execute()
    {
        cursor.setSurfaceId(1);
        state = assignable;
    }

    @Override
    public void assign()
    {
        for (final Pathfindable pathfindable : handler.get(Pathfindable.class))
        {
            pathfindable.setDestination(cursor);
        }
        cursor.setSurfaceId(0);
        state = actionable;
    }

    @Override
    public void update(double extrp)
    {
        if (actionable.isOver())
        {
            text.setText(actionable.getDescription());
        }
        state.update(extrp);
    }

    @Override
    public void render(Graphic g)
    {
        image.render(g);
    }
}
