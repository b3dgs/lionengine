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

import java.util.concurrent.atomic.AtomicReference;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.core.awt.Mouse;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.Image;
import com.b3dgs.lionengine.game.Cursor;
import com.b3dgs.lionengine.game.Service;
import com.b3dgs.lionengine.game.handler.Handler;
import com.b3dgs.lionengine.game.object.ObjectGame;
import com.b3dgs.lionengine.game.object.SetupSurface;
import com.b3dgs.lionengine.game.object.feature.actionable.Actionable;
import com.b3dgs.lionengine.game.object.feature.actionable.ActionableModel;
import com.b3dgs.lionengine.game.object.feature.assignable.Assignable;
import com.b3dgs.lionengine.game.object.feature.assignable.AssignableModel;
import com.b3dgs.lionengine.game.object.feature.displayable.DisplayableModel;
import com.b3dgs.lionengine.game.object.feature.refreshable.RefreshableModel;
import com.b3dgs.lionengine.game.pathfinding.Pathfindable;
import com.b3dgs.lionengine.graphic.Text;

/**
 * Move action.
 */
class Move extends ObjectGame
{
    /** Media reference. */
    public static final Media MEDIA = Medias.create("Move.xml");

    @Service private Text text;
    @Service private Cursor cursor;
    @Service private Handler handler;

    /**
     * Create move action.
     * 
     * @param setup The setup reference.
     */
    public Move(SetupSurface setup)
    {
        super(setup);

        final Actionable actionable = addFeatureAndGet(new ActionableModel(setup));
        actionable.setClickAction(Mouse.LEFT);
        final AtomicReference<Updatable> state = new AtomicReference<>(actionable);

        final Assignable assignable = addFeatureAndGet(new AssignableModel());
        assignable.setClickAssign(Mouse.LEFT);

        final Image image = Drawable.loadImage(setup.getSurface());
        image.setLocation(actionable.getButton().getX(), actionable.getButton().getY());

        actionable.setAction(() ->
        {
            cursor.setSurfaceId(1);
            state.set(assignable);
        });

        assignable.setAssign(() ->
        {
            for (final Pathfindable pathfindable : handler.get(Pathfindable.class))
            {
                pathfindable.setDestination(cursor);
            }
            cursor.setSurfaceId(0);
            state.set(actionable);
        });

        addFeature(new RefreshableModel(extrp ->
        {
            if (actionable.isOver())
            {
                text.setText(actionable.getDescription());
            }
            state.get().update(extrp);
        }));

        addFeature(new DisplayableModel(g -> image.render(g)));
    }
}
