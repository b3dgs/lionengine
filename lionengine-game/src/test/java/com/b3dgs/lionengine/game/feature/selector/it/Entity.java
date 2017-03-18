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
package com.b3dgs.lionengine.game.feature.selector.it;

import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.core.drawable.Drawable;
import com.b3dgs.lionengine.game.FeaturableModel;
import com.b3dgs.lionengine.game.FramesConfig;
import com.b3dgs.lionengine.game.Services;
import com.b3dgs.lionengine.game.Setup;
import com.b3dgs.lionengine.game.feature.DisplayableModel;
import com.b3dgs.lionengine.game.feature.LayerableModel;
import com.b3dgs.lionengine.game.feature.RefreshableModel;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.TransformableModel;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.collidable.CollidableModel;
import com.b3dgs.lionengine.game.feature.collidable.Collision;
import com.b3dgs.lionengine.game.feature.collidable.selector.Selectable;
import com.b3dgs.lionengine.game.feature.collidable.selector.SelectableModel;
import com.b3dgs.lionengine.game.feature.producible.Producer;
import com.b3dgs.lionengine.game.feature.producible.ProducerModel;
import com.b3dgs.lionengine.game.feature.producible.Producible;
import com.b3dgs.lionengine.game.feature.producible.ProducibleListener;
import com.b3dgs.lionengine.game.feature.producible.ProducibleModel;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.Pathfindable;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.PathfindableModel;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Renderable;
import com.b3dgs.lionengine.graphic.SpriteAnimated;

/**
 * Entity representation base.
 */
public class Entity extends FeaturableModel
{
    /**
     * Create entity.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     */
    public Entity(Services services, Setup setup)
    {
        super(services, setup);

        addFeature(new LayerableModel(services, setup));

        final Transformable transformable = addFeatureAndGet(new TransformableModel(setup));
        final Pathfindable pathfindable = addFeatureAndGet(new PathfindableModel(services, setup));

        final FramesConfig config = FramesConfig.imports(setup);
        final SpriteAnimated surface = Drawable.loadSpriteAnimated(setup.getSurface(),
                                                                   config.getHorizontal(),
                                                                   config.getVertical());
        surface.setOrigin(Origin.BOTTOM_LEFT);
        surface.setFrameOffsets(config.getOffsetX(), config.getOffsetY());

        final Collidable collidable = addFeatureAndGet(new CollidableModel(services, setup));
        collidable.setGroup(2);
        collidable.addCollision(Collision.AUTOMATIC);
        collidable.setCollisionVisibility(false);
        collidable.setOrigin(Origin.BOTTOM_LEFT);

        final Producer producer = addFeatureAndGet(new ProducerModel(services, setup));
        producer.setStepsPerSecond(1.0);

        final MapTile map = services.get(MapTile.class);

        final Producible producible = addFeatureAndGet(new ProducibleModel(setup));
        producible.addListener(new ProducibleListener()
        {
            @Override
            public void notifyProductionStarted(Producer producer)
            {
                pathfindable.setLocation(map.getInTileX(producible), map.getInTileY(producible));
                surface.setFrame(1);
            }

            @Override
            public void notifyProductionProgress(Producer producer)
            {
                // Nothing to do
            }

            @Override
            public void notifyProductionEnded(Producer producer)
            {
                surface.setFrame(2);
            }
        });

        final Viewer viewer = services.get(Viewer.class);

        addFeature(new RefreshableModel(new Updatable()
        {
            @Override
            public void update(double extrp)
            {
                pathfindable.update(extrp);
                producer.update(extrp);
                surface.setLocation(viewer, transformable);
            }
        }));

        final Selectable selectable = addFeatureAndGet(new SelectableModel());

        addFeature(new DisplayableModel(new Renderable()
        {
            @Override
            public void render(Graphic g)
            {
                surface.render(g);
                collidable.render(g);
                if (selectable.isSelected())
                {
                    g.setColor(ColorRgba.GREEN);
                    g.drawRect(viewer,
                               Origin.BOTTOM_LEFT,
                               transformable.getX(),
                               transformable.getY(),
                               transformable.getWidth(),
                               transformable.getHeight(),
                               false);
                }
            }
        }));
    }
}
