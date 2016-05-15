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
package com.b3dgs.lionengine.tutorials.mario.c;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.core.awt.Keyboard;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.SpriteAnimated;
import com.b3dgs.lionengine.game.Axis;
import com.b3dgs.lionengine.game.Direction;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.Service;
import com.b3dgs.lionengine.game.camera.Camera;
import com.b3dgs.lionengine.game.collision.tile.TileCollidable;
import com.b3dgs.lionengine.game.collision.tile.TileCollidableListener;
import com.b3dgs.lionengine.game.collision.tile.TileCollidableModel;
import com.b3dgs.lionengine.game.object.FramesConfig;
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
import com.b3dgs.lionengine.game.tile.Tile;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Renderable;

/**
 * Implementation of our controllable entity.
 */
class Mario extends ObjectGame implements Updatable, Renderable, TileCollidableListener
{
    /** Object media. */
    public static final Media MEDIA = Medias.create("entity", "Mario.xml");
    /** Ground location y. */
    private static final int GROUND = 32;

    /** Movement force. */
    public final Force movement = new Force();
    /** Jump force. */
    public final Force jump = new Force();
    /** Surface. */
    public final SpriteAnimated surface;

    private final Mirrorable mirrorable = addFeatureAndGet(new MirrorableModel());
    private final Transformable transformable;
    private final Body body;
    private final TileCollidable tileCollidable;
    private final StateFactory factory = new StateFactory();
    private final StateHandler handler = new StateHandler(factory);

    @Service private Camera camera;
    @Service private Keyboard keyboard;

    /**
     * Constructor.
     * 
     * @param setup The setup reference.
     */
    public Mario(SetupSurface setup)
    {
        super(setup);

        transformable = addFeatureAndGet(new TransformableModel(setup));
        body = addFeatureAndGet(new BodyModel());
        tileCollidable = addFeatureAndGet(new TileCollidableModel(setup));

        final FramesConfig frames = FramesConfig.imports(setup);
        surface = Drawable.loadSpriteAnimated(setup.getSurface(), frames.getHorizontal(), frames.getVertical());
        surface.setOrigin(Origin.CENTER_BOTTOM);
        surface.setFrameOffsets(-1, 0);

        body.setVectors(movement, jump);
        body.setDesiredFps(60);
        body.setMass(2.0);
    }

    /**
     * Respawn the mario.
     */
    public void respawn()
    {
        transformable.teleport(400, GROUND);
        camera.resetInterval(transformable);
        jump.setDirection(Direction.ZERO);
        body.resetGravity();
    }

    @Override
    protected void onPrepared()
    {
        StateAnimationBased.Util.loadStates(MarioState.values(), factory, this);
        handler.changeState(MarioState.IDLE);
        handler.addInput(keyboard);
    }

    @Override
    public void update(double extrp)
    {
        handler.update(extrp);
        mirrorable.update(extrp);
        movement.update(extrp);
        jump.update(extrp);
        body.update(extrp);
        tileCollidable.update(extrp);

        if (transformable.getY() < 0)
        {
            respawn();
        }

        camera.follow(transformable);
        surface.setMirror(mirrorable.getMirror());
        surface.update(extrp);
        surface.setLocation(camera, transformable);
    }

    @Override
    public void render(Graphic g)
    {
        surface.render(g);
    }

    @Override
    public void notifyTileCollided(Tile tile, Axis axis)
    {
        if (Axis.Y == axis && transformable.getY() < transformable.getOldY())
        {
            body.resetGravity();
        }
    }
}
