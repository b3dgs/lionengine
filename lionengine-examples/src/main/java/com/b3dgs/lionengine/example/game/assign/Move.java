/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.example.game.assign;

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
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.Pathfindable;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.Image;
import com.b3dgs.lionengine.graphic.drawable.SpriteFont;

/**
 * Move action.
 */
public final class Move extends FeaturableModel
{
    /**
     * Create move action.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     */
    public Move(Services services, Setup setup)
    {
        super(services, setup);

        final Actionable actionable = addFeature(ActionableModel.class, services, setup);
        actionable.setClickAction(MouseAwt.LEFT);

        addFeature(new LayerableModel(1, 3));

        final Image image = Drawable.loadImage(setup.getSurface());
        image.setLocation(actionable.getButton().getX(), actionable.getButton().getY());

        final Cursor cursor = services.get(Cursor.class);

        final AtomicReference<Updatable> state = new AtomicReference<>(actionable);

        final Assignable assignable = addFeature(AssignableModel.class, services, setup);
        assignable.setClickAssign(MouseAwt.LEFT);

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

        final SpriteFont font = services.get(SpriteFont.class);

        addFeature(new RefreshableModel(extrp ->
        {
            if (actionable.isOver())
            {
                font.setText(actionable.getDescription());
            }
            state.get().update(extrp);
        }));

        addFeature(new DisplayableModel(image::render));
    }
}
