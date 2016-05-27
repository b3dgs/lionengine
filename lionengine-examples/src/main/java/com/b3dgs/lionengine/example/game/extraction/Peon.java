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
package com.b3dgs.lionengine.example.game.extraction;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.SpriteAnimated;
import com.b3dgs.lionengine.game.feature.FeaturableModel;
import com.b3dgs.lionengine.game.feature.Service;
import com.b3dgs.lionengine.game.feature.SetupSurface;
import com.b3dgs.lionengine.game.feature.displayable.DisplayableModel;
import com.b3dgs.lionengine.game.feature.extractable.Extractor;
import com.b3dgs.lionengine.game.feature.extractable.ExtractorChecker;
import com.b3dgs.lionengine.game.feature.extractable.ExtractorListener;
import com.b3dgs.lionengine.game.feature.extractable.ExtractorModel;
import com.b3dgs.lionengine.game.feature.layerable.Layerable;
import com.b3dgs.lionengine.game.feature.layerable.LayerableModel;
import com.b3dgs.lionengine.game.feature.refreshable.RefreshableModel;
import com.b3dgs.lionengine.game.feature.transformable.Transformable;
import com.b3dgs.lionengine.game.feature.transformable.TransformableModel;
import com.b3dgs.lionengine.game.pathfinding.Pathfindable;
import com.b3dgs.lionengine.game.pathfinding.PathfindableModel;
import com.b3dgs.lionengine.game.tile.Tiled;
import com.b3dgs.lionengine.graphic.Viewer;
import com.b3dgs.lionengine.util.UtilMath;

/**
 * Peon entity implementation.
 */
class Peon extends FeaturableModel implements ExtractorListener
{
    /** Media reference. */
    public static final Media MEDIA = Medias.create("Peon.xml");

    private final Pathfindable pathfindable;
    private boolean visible = true;

    @Service private Viewer viewer;

    /**
     * Create a peon.
     * 
     * @param setup The setup reference.
     */
    public Peon(SetupSurface setup)
    {
        super();

        final Transformable transformable = addFeatureAndGet(new TransformableModel());
        transformable.teleport(208, 160);

        final Layerable layerable = addFeatureAndGet(new LayerableModel());
        layerable.setLayer(2);

        final SpriteAnimated surface = Drawable.loadSpriteAnimated(setup.getSurface(), 15, 9);
        surface.setOrigin(Origin.MIDDLE);
        surface.setFrameOffsets(-8, -8);

        final Extractor extractor = addFeatureAndGet(new ExtractorModel());
        extractor.setExtractionPerSecond(1.0);
        extractor.setDropOffPerSecond(1.0);
        extractor.setCapacity(5);
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
                return true;
            }
        });

        pathfindable = addFeatureAndGet(new PathfindableModel(setup));

        addFeature(new RefreshableModel(extrp ->
        {
            pathfindable.update(extrp);
            extractor.update(extrp);
            surface.setLocation(viewer, transformable);
        }));

        addFeature(new DisplayableModel(g ->
        {
            if (visible)
            {
                surface.render(g);
            }
        }));
    }

    @Override
    public void notifyStartGoToRessources(Enum<?> type, Tiled resourceLocation)
    {
        pathfindable.setDestination(resourceLocation);
    }

    @Override
    public void notifyStartExtraction(Enum<?> type, Tiled resourceLocation)
    {
        System.out.println("Started !");
        visible = false;
    }

    @Override
    public void notifyExtracted(Enum<?> type, int currentQuantity)
    {
        System.out.println("Extracted ! " + currentQuantity);
    }

    @Override
    public void notifyStartCarry(Enum<?> type, int totalQuantity)
    {
        visible = true;
    }

    @Override
    public void notifyStartDropOff(Enum<?> type, int totalQuantity)
    {
        // Nothing to do
    }

    @Override
    public void notifyDroppedOff(Enum<?> type, int droppedQuantity)
    {
        System.out.println("done !" + droppedQuantity);
    }
}
