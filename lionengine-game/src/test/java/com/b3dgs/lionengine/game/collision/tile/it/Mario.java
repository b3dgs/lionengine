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
package com.b3dgs.lionengine.game.collision.tile.it;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.SpriteAnimated;
import com.b3dgs.lionengine.game.Axis;
import com.b3dgs.lionengine.game.Camera;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.Services;
import com.b3dgs.lionengine.game.collision.object.Collidable;
import com.b3dgs.lionengine.game.collision.object.CollidableModel;
import com.b3dgs.lionengine.game.collision.tile.TileCollidable;
import com.b3dgs.lionengine.game.collision.tile.TileCollidableListener;
import com.b3dgs.lionengine.game.collision.tile.TileCollidableModel;
import com.b3dgs.lionengine.game.object.ObjectGame;
import com.b3dgs.lionengine.game.object.SetupSurface;
import com.b3dgs.lionengine.game.object.feature.body.Body;
import com.b3dgs.lionengine.game.object.feature.body.BodyModel;
import com.b3dgs.lionengine.game.object.feature.transformable.Transformable;
import com.b3dgs.lionengine.game.object.feature.transformable.TransformableModel;
import com.b3dgs.lionengine.game.tile.Tile;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Renderable;

/**
 * Implementation of our controllable entity.
 */
class Mario extends ObjectGame implements Updatable, Renderable, TileCollidableListener
{
    /** Media reference. */
    public static final Media MEDIA = Medias.create("Mario.xml");

    /** Movement force. */
    private final Force movement = new Force();
    /** Jump force. */
    private final Force jump = new Force();
    /** Transformable model. */
    private final Transformable transformable;
    /** Body model. */
    private final Body body;
    /** Tile collidable. */
    private final TileCollidable tileCollidable;
    /** Object collidable. */
    private final Collidable collidable;
    /** Surface. */
    private final SpriteAnimated surface;
    /** Camera reference. */
    private final Camera camera;

    /**
     * Constructor.
     * 
     * @param setup The setup reference.
     * @param services The services reference.
     */
    public Mario(SetupSurface setup, Services services)
    {
        super(setup, services);
        camera = services.get(Camera.class);

        final Configurer configurer = getConfigurer();
        transformable = addFeatureAndGet(new TransformableModel(configurer));
        transformable.teleport(256, 32);

        body = addFeatureAndGet(new BodyModel());

        collidable = addFeatureAndGet(new CollidableModel(configurer));
        tileCollidable = addFeatureAndGet(new TileCollidableModel(configurer));

        jump.setVelocity(0.1);
        jump.setDestination(0.0, 0.0);

        body.setVectors(movement, jump);
        body.setDesiredFps(60);
        body.setMass(2.0);

        collidable.setCollisionVisibility(true);
        collidable.setOrigin(Origin.CENTER_BOTTOM);

        surface = Drawable.loadSpriteAnimated(setup.getSurface(), 7, 1);
        surface.setOrigin(Origin.CENTER_BOTTOM);
        surface.setFrameOffsets(-1, 0);
    }

    @Override
    protected void onPrepared()
    {
        tileCollidable.addListener(this);
    }

    @Override
    public void update(double extrp)
    {
        movement.setDirection(2, 0);
        jump.setDirection(0.0, 8.0);

        movement.update(extrp);
        jump.update(extrp);
        body.update(extrp);

        tileCollidable.update(extrp);
        collidable.update(extrp);

        if (transformable.getY() < 0)
        {
            transformable.teleportY(80);
            body.resetGravity();
        }
        camera.follow(transformable);
        surface.setLocation(camera, transformable);
    }

    @Override
    public void render(Graphic g)
    {
        surface.render(g);
        g.setColor(ColorRgba.GREEN);
        collidable.render(g);
    }

    @Override
    public void notifyTileCollided(Tile tile, Axis axis)
    {
        if (Axis.Y == axis)
        {
            body.resetGravity();
        }
    }
}
