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
package com.b3dgs.lionengine.example.game.pathfinding;

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.awt.MouseAwt;
import com.b3dgs.lionengine.game.AnimationConfig;
import com.b3dgs.lionengine.game.Cursor;
import com.b3dgs.lionengine.game.feature.Animatable;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.RoutineUpdate;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.rasterable.Rasterable;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.Pathfindable;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.PathfindableListener;

/**
 * Soldier entity implementation.
 */
@FeatureInterface
public final class Soldier extends FeatureModel implements RoutineUpdate, PathfindableListener
{
    /** Media reference. */
    public static final Media MEDIA = Medias.create("OrcSoldier.xml");

    private final Cursor cursor = services.get(Cursor.class);

    private final Pathfindable pathfindable;
    private final Animatable animatable;
    private final Rasterable rasterable;

    private final Animation idle;
    private final Animation walk;

    private int frameOffsetOld;

    /**
     * Create a soldier.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     * @param pathfindable The pathfindable feature.
     * @param animatable The animatable feature.
     * @param rasterable The rasterable feature.
     */
    public Soldier(Services services,
                   Setup setup,
                   Pathfindable pathfindable,
                   Animatable animatable,
                   Rasterable rasterable)
    {
        super(services, setup);

        this.pathfindable = pathfindable;
        this.animatable = animatable;
        this.rasterable = rasterable;

        pathfindable.setRenderDebug(true);
        pathfindable.setLocation(5, 5);

        final AnimationConfig config = AnimationConfig.imports(setup);
        idle = config.getAnimation("idle");
        walk = config.getAnimation("walk");
    }

    /**
     * Update frame offset to match animation with orientation.
     */
    private void updateFrameOffset()
    {
        final int frameOffset = pathfindable.getOrientation().ordinal();
        if (frameOffset != frameOffsetOld)
        {
            frameOffsetOld = frameOffset;
            rasterable.setAnimOffset(frameOffset * 24);
        }
    }

    @Override
    public void update(double extrp)
    {
        if (cursor.isPushedOnce(MouseAwt.RIGHT))
        {
            pathfindable.setDestination(cursor);
        }
        updateFrameOffset();
    }

    @Override
    public void notifyStartMove(Pathfindable pathfindable)
    {
        animatable.play(walk);
    }

    @Override
    public void notifyMoving(Pathfindable pathfindable, int ox, int oy, int nx, int ny)
    {
        // Void
    }

    @Override
    public void notifyArrived(Pathfindable pathfindable)
    {
        animatable.play(idle);
    }
}
