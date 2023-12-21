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
package com.b3dgs.lionengine.example.game.extraction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.game.AnimationConfig;
import com.b3dgs.lionengine.game.Tiled;
import com.b3dgs.lionengine.game.feature.Animatable;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.TransformableListener;
import com.b3dgs.lionengine.game.feature.rasterable.Rasterable;
import com.b3dgs.lionengine.game.feature.tile.map.extractable.Extractor;
import com.b3dgs.lionengine.game.feature.tile.map.extractable.ExtractorChecker;
import com.b3dgs.lionengine.game.feature.tile.map.extractable.ExtractorListener;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.Pathfindable;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.PathfindableListener;

/**
 * Soldier entity implementation.
 */
@FeatureInterface
public final class Soldier extends FeatureModel
                           implements TransformableListener, PathfindableListener, ExtractorListener
{
    /** Media reference. */
    public static final Media MEDIA = Medias.create("OrcSoldier.xml");
    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(Soldier.class);

    private final Rasterable rasterable;
    private final Pathfindable pathfindable;
    private final Animatable animatable;

    private final Animation idle;
    private final Animation walk;

    private int frameOffsetOld;

    /**
     * Create a peon.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     * @param rasterable The rasterable feature.
     * @param extractor The extractor feature.
     * @param pathfindable The pathfindable feature.
     * @param animatable The animatable feature.
     */
    public Soldier(Services services,
                   Setup setup,
                   Rasterable rasterable,
                   Extractor extractor,
                   Pathfindable pathfindable,
                   Animatable animatable)
    {
        super(services, setup);

        this.rasterable = rasterable;
        this.pathfindable = pathfindable;
        this.animatable = animatable;

        final AnimationConfig config = AnimationConfig.imports(setup);
        idle = config.getAnimation("idle");
        walk = config.getAnimation("walk");
        animatable.play(idle);

        extractor.setChecker(new ExtractorChecker()
        {
            @Override
            public boolean canExtract()
            {
                return UtilMath.getDistance(pathfindable.getInTileX(),
                                            pathfindable.getInTileY(),
                                            extractor.getResourceLocation().getInTileX(),
                                            extractor.getResourceLocation().getInTileY()) < 2;
            }

            @Override
            public boolean canCarry()
            {
                final Tiled warehouse = services.get(Warehouse.class);
                return UtilMath.getDistance(pathfindable.getInTileX(),
                                            pathfindable.getInTileY(),
                                            warehouse.getInTileX(),
                                            warehouse.getInTileY()) < 2;
            }
        });
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
    public void notifyTransformed(Transformable transformable)
    {
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

    @Override
    public void notifyStartGoToRessources(String type, Tiled resourceLocation)
    {
        pathfindable.setDestination(resourceLocation);
    }

    @Override
    public void notifyStartExtraction(String type, Tiled resourceLocation)
    {
        LOGGER.info("Extract started !");
        rasterable.setVisibility(false);
    }

    @Override
    public void notifyExtracted(String type, int currentQuantity)
    {
        LOGGER.info("Extracted total: " + currentQuantity);
    }

    @Override
    public void notifyStartCarry(String type, int totalQuantity)
    {
        rasterable.setVisibility(true);
        pathfindable.setDestination(services.get(Warehouse.class));
    }

    @Override
    public void notifyStartDropOff(String type, int totalQuantity)
    {
        rasterable.setVisibility(false);
    }

    @Override
    public void notifyDroppedOff(String type, int droppedQuantity)
    {
        LOGGER.info("Drop remains: " + droppedQuantity);
        if (droppedQuantity == 0)
        {
            LOGGER.info("Drop done !");
            rasterable.setVisibility(true);
        }
    }

    @Override
    public void notifyStopped()
    {
        // Nothing to do
    }
}
