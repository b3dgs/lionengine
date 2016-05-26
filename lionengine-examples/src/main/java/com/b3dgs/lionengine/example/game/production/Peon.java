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
package com.b3dgs.lionengine.example.game.production;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.SpriteAnimated;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.FeaturableModel;
import com.b3dgs.lionengine.game.feature.Service;
import com.b3dgs.lionengine.game.feature.SetupSurface;
import com.b3dgs.lionengine.game.feature.displayable.DisplayableModel;
import com.b3dgs.lionengine.game.feature.identifiable.IdentifiableModel;
import com.b3dgs.lionengine.game.feature.layerable.Layerable;
import com.b3dgs.lionengine.game.feature.layerable.LayerableModel;
import com.b3dgs.lionengine.game.feature.producible.Producer;
import com.b3dgs.lionengine.game.feature.producible.ProducerChecker;
import com.b3dgs.lionengine.game.feature.producible.ProducerListener;
import com.b3dgs.lionengine.game.feature.producible.ProducerModel;
import com.b3dgs.lionengine.game.feature.producible.Producible;
import com.b3dgs.lionengine.game.feature.refreshable.RefreshableModel;
import com.b3dgs.lionengine.game.feature.transformable.Transformable;
import com.b3dgs.lionengine.game.feature.transformable.TransformableModel;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.pathfinding.CoordTile;
import com.b3dgs.lionengine.game.pathfinding.MapTilePath;
import com.b3dgs.lionengine.game.pathfinding.Pathfindable;
import com.b3dgs.lionengine.game.pathfinding.PathfindableModel;
import com.b3dgs.lionengine.graphic.Viewer;
import com.b3dgs.lionengine.util.UtilMath;

/**
 * Peon entity implementation.
 */
class Peon extends FeaturableModel implements ProducerChecker, ProducerListener
{
    /** Media reference. */
    public static final Media MEDIA = Medias.create("Peon.xml");

    private final Transformable transformable = addFeatureAndGet(new TransformableModel());
    private final Pathfindable pathfindable;

    private boolean visible = true;

    @Service private MapTile map;
    @Service private MapTilePath mapPath;
    @Service private Viewer viewer;

    /**
     * Create a peon.
     * 
     * @param setup The setup reference.
     */
    public Peon(SetupSurface setup)
    {
        super();

        addFeature(new IdentifiableModel());
        final Layerable layerable = addFeatureAndGet(new LayerableModel());
        layerable.setLayer(2);

        final SpriteAnimated surface = Drawable.loadSpriteAnimated(setup.getSurface(), 15, 9);
        surface.setOrigin(Origin.MIDDLE);
        surface.setFrameOffsets(-8, -8);

        transformable.teleport(208, 160);
        pathfindable = addFeatureAndGet(new PathfindableModel(setup));

        final Producer producer = addFeatureAndGet(new ProducerModel());
        producer.setStepsPerSecond(1.0);

        addFeature(new RefreshableModel(extrp ->
        {
            pathfindable.update(extrp);
            producer.update(extrp);
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
    public boolean checkProduction(Producible producible)
    {
        return UtilMath.isBetween(transformable.getX(), producible.getX(), producible.getX() + producible.getWidth())
               && UtilMath.isBetween(transformable.getY(),
                                     producible.getY() - producible.getHeight(),
                                     producible.getY());
    }

    @Override
    public void notifyCanNotProduce(Producible producible)
    {
        // Nothing to do
    }

    @Override
    public void notifyStartProduction(Producible producible, Featurable featurable)
    {
        visible = false;
    }

    @Override
    public void notifyProducing(Producible producible, Featurable featurable)
    {
        // Nothing to do
    }

    @Override
    public void notifyProduced(Producible producible, Featurable featurable)
    {
        final CoordTile coord = mapPath.getFreeTileAround(pathfindable,
                                                          (int) producible.getX() / map.getTileWidth(),
                                                          (int) producible.getY() / map.getTileHeight(),
                                                          producible.getWidth() / map.getTileWidth(),
                                                          producible.getHeight() / map.getTileHeight(),
                                                          map.getInTileRadius());
        if (coord != null)
        {
            pathfindable.setLocation(coord.getX(), coord.getY());
        }
        visible = true;
    }
}
