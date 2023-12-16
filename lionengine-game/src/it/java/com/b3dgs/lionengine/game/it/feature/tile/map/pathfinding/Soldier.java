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
package com.b3dgs.lionengine.game.it.feature.tile.map.pathfinding;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.game.feature.DisplayableModel;
import com.b3dgs.lionengine.game.feature.FeaturableModel;
import com.b3dgs.lionengine.game.feature.LayerableModel;
import com.b3dgs.lionengine.game.feature.RefreshableModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.TransformableModel;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.Pathfindable;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.PathfindableModel;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.SpriteAnimated;

/**
 * Peon entity implementation.
 */
public final class Soldier extends FeaturableModel
{
    /** Media reference. */
    public static final Media MEDIA = Medias.create("OrcSoldier.xml");

    /**
     * Create a peon.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     */
    public Soldier(Services services, Setup setup)
    {
        super(services, setup);

        addFeature(new LayerableModel(1));

        final Transformable transformable = addFeature(TransformableModel.class, services, setup);

        final SpriteAnimated surface = Drawable.loadSpriteAnimated(setup.getSurface(), 24, 8);
        surface.setOrigin(Origin.BOTTOM_LEFT);
        surface.setFrameOffsets(8, 8);

        final Pathfindable pathfindable = addFeature(PathfindableModel.class, services, setup);
        pathfindable.setSpeed(6.0, 6.0);
        pathfindable.setRenderDebug(true);
        pathfindable.clearIgnoredId();
        pathfindable.clearPath();
        pathfindable.clearSharedPathIds();
        pathfindable.stopMoves();

        assertEquals(1, pathfindable.getInTileWidth());
        assertEquals(1, pathfindable.getInTileHeight());

        final Viewer viewer = services.get(Viewer.class);

        addFeature(new RefreshableModel(extrp ->
        {
            pathfindable.update(extrp);
            surface.setLocation(viewer, transformable);
        }));

        addFeature(new DisplayableModel(g ->
        {
            pathfindable.render(g);
            surface.render(g);
        }));
    }
}
