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
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.core.drawable.Drawable;
import com.b3dgs.lionengine.game.Featurable;
import com.b3dgs.lionengine.game.FeaturableModel;
import com.b3dgs.lionengine.game.Service;
import com.b3dgs.lionengine.game.SetupSurface;
import com.b3dgs.lionengine.game.feature.DisplayableModel;
import com.b3dgs.lionengine.game.feature.LayerableModel;
import com.b3dgs.lionengine.game.feature.RefreshableModel;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.TransformableModel;
import com.b3dgs.lionengine.game.feature.producible.Producer;
import com.b3dgs.lionengine.game.feature.producible.ProducerListener;
import com.b3dgs.lionengine.game.feature.producible.ProducerModel;
import com.b3dgs.lionengine.game.feature.producible.Producible;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.CoordTile;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.MapTilePath;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.Pathfindable;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.PathfindableModel;
import com.b3dgs.lionengine.graphic.SpriteAnimated;
import com.b3dgs.lionengine.util.UtilMath;

/**
 * Peon entity implementation.
 */
class Peon extends FeaturableModel implements ProducerListener
{
    /** Media reference. */
    public static final Media MEDIA = Medias.create("Peon.xml");

    private final Pathfindable pathfindable;

    private boolean visible = true;

    @Service private MapTile map;
    @Service private Viewer viewer;

    /**
     * Create a peon.
     * 
     * @param setup The setup reference.
     */
    public Peon(SetupSurface setup)
    {
        super();

        addFeature(new LayerableModel(2));

        final SpriteAnimated surface = Drawable.loadSpriteAnimated(setup.getSurface(), 15, 9);
        surface.setOrigin(Origin.MIDDLE);
        surface.setFrameOffsets(-8, -8);

        final Transformable transformable = addFeatureAndGet(new TransformableModel());
        transformable.teleport(640, 860);

        final Producer producer = addFeatureAndGet(new ProducerModel());
        producer.setStepsPerSecond(1.0);
        producer.setChecker(featurable ->
        {
            final Producible producible = featurable.getFeature(Producible.class);
            return UtilMath.isBetween(transformable.getX(),
                                      producible.getX(),
                                      producible.getX() + producible.getWidth())
                   && UtilMath.isBetween(transformable.getY(),
                                         producible.getY() - producible.getHeight(),
                                         producible.getY());
        });

        pathfindable = addFeatureAndGet(new PathfindableModel(setup));

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
    public void notifyCanNotProduce(Featurable featurable)
    {
        // Nothing to do
    }

    @Override
    public void notifyStartProduction(Featurable featurable)
    {
        visible = false;
    }

    @Override
    public void notifyProducing(Featurable featurable)
    {
        // Nothing to do
    }

    @Override
    public void notifyProduced(Featurable featurable)
    {
        final MapTilePath mapPath = map.getFeature(MapTilePath.class);
        final Producible producible = featurable.getFeature(Producible.class);
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
