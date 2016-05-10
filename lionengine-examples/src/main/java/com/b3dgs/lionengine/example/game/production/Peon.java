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
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.SpriteAnimated;
import com.b3dgs.lionengine.game.Services;
import com.b3dgs.lionengine.game.handler.Handlable;
import com.b3dgs.lionengine.game.layer.Layerable;
import com.b3dgs.lionengine.game.layer.LayerableModel;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.object.ObjectGame;
import com.b3dgs.lionengine.game.object.SetupSurface;
import com.b3dgs.lionengine.game.object.feature.displayable.DisplayableModel;
import com.b3dgs.lionengine.game.object.feature.producible.Producer;
import com.b3dgs.lionengine.game.object.feature.producible.ProducerChecker;
import com.b3dgs.lionengine.game.object.feature.producible.ProducerListener;
import com.b3dgs.lionengine.game.object.feature.producible.ProducerModel;
import com.b3dgs.lionengine.game.object.feature.producible.Producible;
import com.b3dgs.lionengine.game.object.feature.transformable.Transformable;
import com.b3dgs.lionengine.game.object.feature.transformable.TransformableModel;
import com.b3dgs.lionengine.game.pathfinding.CoordTile;
import com.b3dgs.lionengine.game.pathfinding.MapTilePath;
import com.b3dgs.lionengine.game.pathfinding.Pathfindable;
import com.b3dgs.lionengine.game.pathfinding.PathfindableModel;
import com.b3dgs.lionengine.graphic.Viewer;
import com.b3dgs.lionengine.util.UtilMath;

/**
 * Peon entity implementation.
 */
class Peon extends ObjectGame implements Updatable, ProducerChecker, ProducerListener
{
    /** Media reference. */
    public static final Media MEDIA = Medias.create("Peon.xml");

    /** Transformable model. */
    private final Transformable transformable = addFeatureAndGet(new TransformableModel());
    /** Pathfindable model. */
    private final Pathfindable pathfindable;
    /** Producer model. */
    private final Producer producer = addFeatureAndGet(new ProducerModel());
    /** Layerable model. */
    private final Layerable layerable = addFeatureAndGet(new LayerableModel());
    /** Surface reference. */
    private final SpriteAnimated surface;
    /** Map tile reference. */
    private final MapTile map;
    /** Map tile reference. */
    private final MapTilePath mapPath;
    /** Viewer reference. */
    private final Viewer viewer;
    /** Visible. */
    private boolean visible;

    /**
     * Create a peon.
     * 
     * @param setup The setup reference.
     * @param services The services reference.
     */
    public Peon(SetupSurface setup, Services services)
    {
        super(setup, services);

        pathfindable = addFeatureAndGet(new PathfindableModel(setup.getConfigurer()));

        viewer = services.get(Viewer.class);
        map = services.get(MapTile.class);
        mapPath = services.get(MapTilePath.class);

        surface = Drawable.loadSpriteAnimated(setup.getSurface(), 15, 9);
        surface.setOrigin(Origin.MIDDLE);
        surface.setFrameOffsets(-8, -8);
        visible = true;

        transformable.teleport(208, 160);
        producer.setStepsPerSecond(1.0);
        layerable.setLayer(Integer.valueOf(2));

        addFeature(new DisplayableModel(g ->
        {
            if (visible)
            {
                surface.render(g);
            }
        }));
    }

    @Override
    public void update(double extrp)
    {
        pathfindable.update(extrp);
        producer.update(extrp);
        surface.setLocation(viewer, transformable);
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
    public void notifyStartProduction(Producible producible, Handlable handlable)
    {
        visible = false;
    }

    @Override
    public void notifyProducing(Producible producible, Handlable handlable)
    {
        // Nothing to do
    }

    @Override
    public void notifyProduced(Producible producible, Handlable handlable)
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
