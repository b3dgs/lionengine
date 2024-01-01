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
package com.b3dgs.lionengine.example.game.extraction;

import java.util.concurrent.atomic.AtomicReference;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.awt.MouseAwt;
import com.b3dgs.lionengine.game.Cursor;
import com.b3dgs.lionengine.game.feature.Actionable;
import com.b3dgs.lionengine.game.feature.ActionableModel;
import com.b3dgs.lionengine.game.feature.DisplayableModel;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.FeaturableModel;
import com.b3dgs.lionengine.game.feature.Handler;
import com.b3dgs.lionengine.game.feature.LayerableModel;
import com.b3dgs.lionengine.game.feature.RefreshableModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.assignable.Assignable;
import com.b3dgs.lionengine.game.feature.assignable.AssignableModel;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.extractable.Extractable;
import com.b3dgs.lionengine.game.feature.tile.map.extractable.Extractor;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.MapTilePath;
import com.b3dgs.lionengine.graphic.Text;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.Image;

/**
 * Resources button action.
 */
public final class Button extends FeaturableModel
{
    /** Extract media. */
    public static final Media EXTRACT = Medias.create("Extract.xml");

    /**
     * Create build button action.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     */
    public Button(Services services, Setup setup)
    {
        super(services, setup);

        addFeature(new LayerableModel(3));

        final Assignable assignable = addFeature(AssignableModel.class, services, setup);
        assignable.setClickAssign(MouseAwt.LEFT);

        final Actionable actionable = addFeature(ActionableModel.class, services, setup);
        actionable.setClickAction(MouseAwt.LEFT);

        final Cursor cursor = services.get(Cursor.class);
        final AtomicReference<Updatable> state = new AtomicReference<>(actionable);
        actionable.setAction(() ->
        {
            cursor.setSurfaceId(1);
            state.set(assignable);
        });

        final Handler handler = services.get(Handler.class);
        final MapTile map = services.get(MapTile.class);

        assignable.setAssign(() ->
        {
            final int tx = map.getInTileX(cursor);
            final int ty = map.getInTileY(cursor);

            final MapTilePath mapPath = map.getFeature(MapTilePath.class);
            for (final Extractor extractor : handler.get(Extractor.class))
            {
                for (final Integer id : mapPath.getObjectsId(tx, ty))
                {
                    final Featurable featurable = handler.get(id);
                    if (featurable.hasFeature(Extractable.class))
                    {
                        extractor.setResource(featurable.getFeature(Extractable.class));
                        extractor.startExtraction();
                    }
                }
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

        final Image image = Drawable.loadImage(setup.getSurface());
        image.setLocation(actionable.getButton().getX(), actionable.getButton().getY());

        addFeature(new DisplayableModel(image::render));
    }
}
