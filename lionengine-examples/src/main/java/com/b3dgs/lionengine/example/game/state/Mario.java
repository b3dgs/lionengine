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
package com.b3dgs.lionengine.example.game.state;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.core.InputDevice;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.SpriteAnimated;
import com.b3dgs.lionengine.game.Direction;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.camera.Camera;
import com.b3dgs.lionengine.game.handler.DisplayableModel;
import com.b3dgs.lionengine.game.handler.RefreshableModel;
import com.b3dgs.lionengine.game.handler.Service;
import com.b3dgs.lionengine.game.object.ObjectGame;
import com.b3dgs.lionengine.game.object.SetupSurface;
import com.b3dgs.lionengine.game.object.feature.body.Body;
import com.b3dgs.lionengine.game.object.feature.body.BodyModel;
import com.b3dgs.lionengine.game.object.feature.mirrorable.Mirrorable;
import com.b3dgs.lionengine.game.object.feature.mirrorable.MirrorableModel;
import com.b3dgs.lionengine.game.object.feature.transformable.Transformable;
import com.b3dgs.lionengine.game.object.feature.transformable.TransformableModel;
import com.b3dgs.lionengine.game.state.StateAnimationBased;
import com.b3dgs.lionengine.game.state.StateFactory;
import com.b3dgs.lionengine.game.state.StateHandler;

/**
 * Implementation of our controllable entity.
 */
class Mario extends ObjectGame
{
    /** Media reference. */
    public static final Media MEDIA = Medias.create("Mario.xml");
    /** Ground location y. */
    static final int GROUND = 32;

    private final StateFactory factory = new StateFactory();
    private final StateHandler handler = new StateHandler(factory);
    private final Force movement = new Force();
    private final Force jump = new Force();
    private final SpriteAnimated surface;

    @Service private Camera camera;

    /**
     * Constructor.
     * 
     * @param setup The setup reference.
     */
    public Mario(SetupSurface setup)
    {
        super(setup);

        final Transformable transformable = addFeatureAndGet(new TransformableModel());
        transformable.teleport(160, GROUND);

        final Body body = addFeatureAndGet(new BodyModel());
        body.setVectors(movement, jump);
        body.setDesiredFps(60);
        body.setMass(2.0);

        final Mirrorable mirrorable = addFeatureAndGet(new MirrorableModel());

        surface = Drawable.loadSpriteAnimated(setup.getSurface(), 7, 1);
        surface.setOrigin(Origin.CENTER_BOTTOM);
        surface.setFrameOffsets(-1, 0);

        addFeature(new RefreshableModel(extrp ->
        {
            handler.update(extrp);
            mirrorable.update(extrp);
            movement.update(extrp);
            jump.update(extrp);
            body.update(extrp);
            if (transformable.getY() < GROUND)
            {
                transformable.teleportY(GROUND);
                jump.setDirection(Direction.ZERO);
                body.resetGravity();
            }
            surface.setMirror(mirrorable.getMirror());
            surface.update(extrp);
            surface.setLocation(camera, transformable);
        }));

        addFeature(new DisplayableModel(g -> surface.render(g)));
    }

    /**
     * Add an input controller.
     * 
     * @param input The input reference.
     */
    public void addInput(InputDevice input)
    {
        handler.addInput(input);
    }

    /**
     * Get the movement force.
     * 
     * @return The movement force.
     */
    public Force getMovement()
    {
        return movement;
    }

    /**
     * Get the jump force.
     * 
     * @return The jump force.
     */
    public Force getJump()
    {
        return jump;
    }

    /**
     * Get the surface.
     * 
     * @return The surface reference.
     */
    public SpriteAnimated getSurface()
    {
        return surface;
    }

    @Override
    protected void onPrepared()
    {
        StateAnimationBased.Util.loadStates(MarioState.values(), factory, this, getConfigurer());
        handler.changeState(MarioState.IDLE);
    }
}
