/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.tutorials.mario.d;

import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.feature.Camera;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Routine;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.body.Body;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.collidable.CollidableListener;
import com.b3dgs.lionengine.game.feature.collidable.Collision;
import com.b3dgs.lionengine.game.feature.state.StateHandler;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidable;

/**
 * Mario specific implementation.
 */
@FeatureInterface
public final class Mario extends FeatureModel implements Routine, CollidableListener
{
    private static final long DEAD_TICK = 30L;

    private final Camera camera = services.get(Camera.class);

    private final Transformable transformable;
    private final Body body;
    private final Collidable collidable;
    private final TileCollidable tileCollidable;
    private final StateHandler state;
    private final EntityModel model;

    private final Tick deadTick = new Tick();

    /**
     * Constructor.
     * 
     * @param setup The setup reference.
     * @param services The services reference.
     * @param transformable The transformable feature.
     * @param body The body feature.
     * @param collidable The collidable feature.
     * @param tileCollidable The tile collidable feature.
     * @param state The state feature.
     * @param model The model feature.
     */
    public Mario(Services services,
                 Setup setup,
                 Transformable transformable,
                 Body body,
                 Collidable collidable,
                 TileCollidable tileCollidable,
                 StateHandler state,
                 EntityModel model)
    {
        super(services, setup);

        this.transformable = transformable;
        this.body = body;
        this.collidable = collidable;
        this.tileCollidable = tileCollidable;
        this.state = state;
        this.model = model;
    }

    /**
     * Force jump.
     * 
     * @param height The jump height.
     */
    public void jump(int height)
    {
        model.getJump().zero();
        model.getJump().setDirection(0.0, height);
    }

    /**
     * Respawn the mario.
     */
    public void respawn()
    {
        state.changeState(StateIdle.class);
        transformable.teleport(160, World.GROUND);
        camera.resetInterval(transformable);
        model.getJump().zero();
        body.resetGravity();
        model.getLife().fill();
        collidable.setEnabled(true);
        tileCollidable.setEnabled(true);
    }

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        state.addListener((from, to) ->
        {
            if (StateFall.class != from && StateJump.class == to)
            {
                Sfx.JUMP.play();
            }
        });
        respawn();
    }

    @Override
    public void update(double extrp)
    {
        deadTick.update(extrp);
        if (deadTick.elapsed(DEAD_TICK))
        {
            tileCollidable.setEnabled(false);
            jump(14);
            deadTick.stop();
        }

        if (transformable.getY() < 0)
        {
            respawn();
        }
    }

    @Override
    public void notifyCollided(Collidable other, Collision with, Collision by)
    {
        final Transformable collider = other.getFeature(Transformable.class);
        if (Double.compare(transformable.getY(), transformable.getOldY()) >= 0)
        {
            collidable.setEnabled(false);
            model.getLife().decrease(1);
            deadTick.start();
        }
        else if (transformable.getY() > collider.getY())
        {
            jump(9);
        }
    }
}
