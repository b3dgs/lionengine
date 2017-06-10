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
package com.b3dgs.lionengine.example.game.assign;

import java.util.concurrent.atomic.AtomicReference;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.core.drawable.Drawable;
import com.b3dgs.lionengine.game.Cursor;
import com.b3dgs.lionengine.game.FeaturableModel;
import com.b3dgs.lionengine.game.Services;
import com.b3dgs.lionengine.game.Setup;
import com.b3dgs.lionengine.game.feature.Actionable;
import com.b3dgs.lionengine.game.feature.ActionableModel;
import com.b3dgs.lionengine.game.feature.DisplayableModel;
import com.b3dgs.lionengine.game.feature.Handler;
import com.b3dgs.lionengine.game.feature.RefreshableModel;
import com.b3dgs.lionengine.game.feature.assignable.Assignable;
import com.b3dgs.lionengine.game.feature.assignable.AssignableModel;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.Pathfindable;
import com.b3dgs.lionengine.graphic.Image;
import com.b3dgs.lionengine.graphic.Text;
import com.b3dgs.lionengine.io.awt.Mouse;

/**
 * Move action.
 */
class Move extends FeaturableModel
{
    /** Media reference. */
    public static final Media MEDIA = Medias.create("Move.xml");

    /**
     * Create move action.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     */
    public Move(Services services, Setup setup)
    {
        super();

        final Actionable actionable = addFeatureAndGet(new ActionableModel(services, setup));
        actionable.setClickAction(Mouse.LEFT);

        final Image image = Drawable.loadImage(setup.getSurface());
        image.setLocation(actionable.getButton().getX(), actionable.getButton().getY());

        final Cursor cursor = services.get(Cursor.class);

        final AtomicReference<Updatable> state = new AtomicReference<>(actionable);

        final Assignable assignable = addFeatureAndGet(new AssignableModel(services));
        assignable.setClickAssign(Mouse.LEFT);

        actionable.setAction(() ->
        {
            cursor.setSurfaceId(1);
            state.set(assignable);
        });

        final Handler handler = services.get(Handler.class);

        assignable.setAssign(() ->
        {
            for (final Pathfindable pathfindable : handler.get(Pathfindable.class))
            {
                pathfindable.setDestination(cursor);
            }
            cursor.setSurfaceId(0);
            state.set(actionable);
        });

        final Text text = services.get(Text.class);

        addFeature(new RefreshableModel(extrp ->
        {
            if (actionable.isOver())
            {
                text.setText(actionable.getDescription());
            }
            state.get().update(extrp);
        }));

        addFeature(new DisplayableModel(image::render));
    }
}
