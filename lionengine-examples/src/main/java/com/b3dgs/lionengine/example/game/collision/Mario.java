/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.example.game.collision;

import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.core.Core;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.Renderable;
import com.b3dgs.lionengine.core.Updatable;
import com.b3dgs.lionengine.core.awt.Keyboard;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.SpriteAnimated;
import com.b3dgs.lionengine.game.Axis;
import com.b3dgs.lionengine.game.Camera;
import com.b3dgs.lionengine.game.Direction;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.Services;
import com.b3dgs.lionengine.game.factory.SetupSurface;
import com.b3dgs.lionengine.game.handler.ObjectGame;
import com.b3dgs.lionengine.game.map.TileGame;
import com.b3dgs.lionengine.game.trait.Body;
import com.b3dgs.lionengine.game.trait.BodyModel;
import com.b3dgs.lionengine.game.trait.TileCollidable;
import com.b3dgs.lionengine.game.trait.TileCollidableListener;
import com.b3dgs.lionengine.game.trait.TileCollidableModel;
import com.b3dgs.lionengine.game.trait.Transformable;
import com.b3dgs.lionengine.game.trait.TransformableModel;

/**
 * Implementation of our controllable entity.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see com.b3dgs.lionengine.example.game.entity
 */
class Mario
        extends ObjectGame
        implements Updatable, Renderable, TileCollidableListener
{
    /** Setup. */
    private static final SetupSurface SETUP = new SetupSurface(Core.MEDIA.create("mario.xml"));

    /** Surface. */
    private final SpriteAnimated surface;
    /** Transformable model. */
    private final Transformable transformable;
    /** Body model. */
    private final Body body;
    /** Tile collidable. */
    private final TileCollidable tileCollidable;
    /** Keyboard reference. */
    private final Keyboard keyboard;
    /** Camera reference. */
    private final Camera camera;
    /** Movement force. */
    private final Force movement;
    /** Jump force. */
    private final Force jump;

    /**
     * Constructor.
     * 
     * @param services The services reference.
     */
    public Mario(Services services)
    {
        super(SETUP, services);

        transformable = new TransformableModel(this, SETUP.getConfigurer());
        addTrait(transformable);
        tileCollidable = new TileCollidableModel(this, SETUP.getConfigurer(), services);
        tileCollidable.addListener(this);
        body = new BodyModel(this);
        jump = new Force();
        movement = new Force();
        body.setVectors(movement, jump);
        jump.setVelocity(1);
        jump.setDestination(0.0, 0.0);

        body.setDesiredFps(services.get(Integer.class).intValue());
        body.setMass(2.0);
        keyboard = services.get(Keyboard.class);
        camera = services.get(Camera.class);

        surface = Drawable.loadSpriteAnimated(SETUP.surface, 7, 1);
        surface.setOrigin(Origin.CENTER_BOTTOM);
        surface.setFrameOffsets(-1, 0);
        transformable.teleport(2220, 80);
    }

    @Override
    public void update(double extrp)
    {
        movement.setDirection(Direction.ZERO);
        if (keyboard.isPressed(Keyboard.LEFT))
        {
            movement.setDirection(-2, 0);
        }
        if (keyboard.isPressed(Keyboard.RIGHT))
        {
            movement.setDirection(2, 0);
        }
        if (keyboard.isPressedOnce(Keyboard.UP))
        {
            jump.setDirection(0.0, 16.0);
        }
        movement.update(extrp);
        jump.update(extrp);
        body.update(extrp);

        tileCollidable.update(extrp);

        if (transformable.getY() < 0)
        {
            transformable.teleportY(80);
            body.resetGravity();
        }
        camera.follow(transformable);
        surface.setLocation(camera, transformable.getX(), transformable.getY());
    }

    @Override
    public void render(Graphic g)
    {
        surface.render(g);
    }

    @Override
    public void notifyTileCollided(TileGame tile, Axis axis)
    {
        if (Axis.Y == axis)
        {
            body.resetGravity();
        }
    }
}
