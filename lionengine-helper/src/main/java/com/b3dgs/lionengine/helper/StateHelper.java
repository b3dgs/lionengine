/*
 * Copyright (C) 2013-2022 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.helper;

import com.b3dgs.lionengine.AnimState;
import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.feature.Animatable;
import com.b3dgs.lionengine.game.feature.Mirrorable;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.body.Body;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.collidable.CollidableListener;
import com.b3dgs.lionengine.game.feature.collidable.Collision;
import com.b3dgs.lionengine.game.feature.networkable.Networkable;
import com.b3dgs.lionengine.game.feature.rasterable.Rasterable;
import com.b3dgs.lionengine.game.feature.state.StateAbstract;
import com.b3dgs.lionengine.game.feature.tile.map.collision.Axis;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionCategory;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionResult;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidable;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidableListener;
import com.b3dgs.lionengine.io.DeviceController;
import com.b3dgs.lionengine.io.DeviceControllerDelegate;
import com.b3dgs.lionengine.io.DeviceMapper;

/**
 * State base implementation.
 * 
 * @param <M> The model feature.
 */
public class StateHelper<M extends EntityModelHelper> extends StateAbstract
                        implements TileCollidableListener, CollidableListener
{
    /**
     * Check if collide on the good side depending of movement side.
     * 
     * @param side The side name.
     * @param sideA The side A.
     * @param sideB The side B.
     * @param current The current location.
     * @param old The old location.
     * @return <code>true</code> if good side, <code>false</code> else.
     */
    private static boolean collideSide(String side, String sideA, String sideB, double current, double old)
    {
        return side.startsWith(sideA) && Double.compare(current, old) <= 0
               || side.startsWith(sideB) && Double.compare(current, old) >= 0;
    }

    /** Entity model reference. */
    protected final M model;
    /** State animation data. */
    protected final Animation animation;
    /** Device reference. */
    protected final DeviceController device;
    /** Transformable reference. */
    protected final Transformable transformable;
    /** Mirrorable reference. */
    protected final Mirrorable mirrorable;
    /** Rasterable reference. */
    protected final Rasterable rasterable;
    /** Body reference. */
    protected final Body body;
    /** Animator reference. */
    protected final Animatable animatable;
    /** Tile collidable reference. */
    protected final TileCollidable tileCollidable;
    /** Collidable reference. */
    protected final Collidable collidable;
    /** Networkable reference. */
    protected final Networkable networkable;
    /** Collide horizontal flag. */
    private boolean collideX;
    /** Collide vertical flag. */
    private boolean collideY;

    /**
     * Create the state.
     * 
     * @param model The model reference.
     * @param animation The animation reference.
     */
    protected StateHelper(M model, Animation animation)
    {
        super();

        this.model = model;
        this.animation = animation;
        device = new DeviceControllerDelegate(model::getInput);
        transformable = model.getFeature(Transformable.class);
        mirrorable = model.getFeature(Mirrorable.class);
        body = model.getFeature(Body.class);
        animatable = model.getFeature(Animatable.class);
        tileCollidable = model.getFeature(TileCollidable.class);
        collidable = model.getFeature(Collidable.class);
        rasterable = model.getFeature(Rasterable.class);
        networkable = model.getFeature(Networkable.class);
    }

    /**
     * Called on collided.
     * 
     * @param result The collided tile.
     * @param category The collided axis.
     */
    protected void onCollided(CollisionResult result, CollisionCategory category)
    {
        // Nothing to do
    }

    /**
     * Perform horizontal mirroring.
     */
    protected void mirrorHorizontal()
    {
        mirrorable.mirror(mirrorable.getMirror() == Mirror.HORIZONTAL ? Mirror.NONE : Mirror.HORIZONTAL);
    }

    /**
     * Update mirror on horizontal axis (perform mirror when going on the opposite side).
     */
    protected void updateMirrorHorizontal()
    {
        if (isGoLeft())
        {
            mirrorable.mirror(Mirror.HORIZONTAL);
        }
        else if (isGoRight())
        {
            mirrorable.mirror(Mirror.NONE);
        }
    }

    /**
     * Update mirror on vertical axis (perform mirror when going on the opposite side).
     */
    protected void updateMirrorVertical()
    {
        if (isGoUp())
        {
            mirrorable.mirror(Mirror.VERTICAL);
        }
        else if (isGoDown())
        {
            mirrorable.mirror(Mirror.NONE);
        }
    }

    /**
     * Check if is anim state.
     * 
     * @param state The expected anim state.
     * @return <code>true</code> if is state, <code>false</code> else.
     */
    protected final boolean is(AnimState state)
    {
        return animatable.is(state);
    }

    /**
     * Check if is current mirror state.
     * 
     * @param mirror The expected mirror to be.
     * @return <code>true</code> if is mirror, <code>false</code> else.
     */
    protected final boolean is(Mirror mirror)
    {
        return mirrorable.is(mirror);
    }

    /**
     * Check if going somewhere.
     * 
     * @return <code>true</code> if not going to move, <code>false</code> else.
     */
    protected final boolean isGo()
    {
        return Double.compare(device.getHorizontalDirection(), 0.0) != 0
               || Double.compare(device.getVerticalDirection(), 0.0) != 0;
    }

    /**
     * Check if going nowhere.
     * 
     * @return <code>true</code> if not going to move, <code>false</code> else.
     */
    protected final boolean isGoNone()
    {
        return Double.compare(device.getHorizontalDirection(), 0.0) == 0
               && Double.compare(device.getVerticalDirection(), 0.0) == 0;
    }

    /**
     * Check if going horizontally in any way.
     * 
     * @return <code>true</code> if going to left or right, <code>false</code> else.
     */
    protected final boolean isGoHorizontal()
    {
        return Double.compare(device.getHorizontalDirection(), 0.0) != 0;
    }

    /**
     * Check if going left.
     * 
     * @return <code>true</code> if going to left, <code>false</code> else.
     */
    protected final boolean isGoLeft()
    {
        return Double.compare(device.getHorizontalDirection(), 0.0) < 0;
    }

    /**
     * Check if going right.
     * 
     * @return <code>true</code> if going to right, <code>false</code> else.
     */
    protected final boolean isGoRight()
    {
        return Double.compare(device.getHorizontalDirection(), 0.0) > 0;
    }

    /**
     * Check if going vertically in any way.
     * 
     * @return <code>true</code> if going to up or down, <code>false</code> else.
     */
    protected final boolean isGoVertical()
    {
        return Double.compare(device.getVerticalDirection(), 0.0) != 0;
    }

    /**
     * Check if going up.
     * 
     * @return <code>true</code> if going to up, <code>false</code> else.
     */
    protected final boolean isGoUp()
    {
        return Double.compare(device.getVerticalDirection(), 0.0) > 0;
    }

    /**
     * Check if going down.
     * 
     * @return <code>true</code> if going to down, <code>false</code> else.
     */
    protected final boolean isGoDown()
    {
        return Double.compare(device.getVerticalDirection(), 0.0) < 0;
    }

    /**
     * Check if going up one time.
     * 
     * @return <code>true</code> if going to up, <code>false</code> else.
     */
    protected final boolean isGoUpOnce()
    {
        return isGoUp();
    }

    /**
     * Check if going down one time.
     * 
     * @return <code>true</code> if going to down, <code>false</code> else.
     */
    protected final boolean isGoDownOnce()
    {
        return isGoDown();
    }

    /**
     * Check if going left once.
     * 
     * @return <code>true</code> if going to left, <code>false</code> else.
     */
    protected final boolean isGoLeftOnce()
    {
        return isGoLeft();
    }

    /**
     * Check if going right once.
     * 
     * @return <code>true</code> if going to right, <code>false</code> else.
     */
    protected final boolean isGoRightOnce()
    {
        return isGoRight();
    }

    /**
     * Check if fire button is enabled.
     * 
     * @param index The button index (must not be <code>null</code>, must be positive).
     * @return <code>true</code> if active, <code>false</code> else.
     */
    protected final boolean isFire(Integer index)
    {
        return device.isFired(index);
    }

    /**
     * Check if fire button is enabled.
     * 
     * @param mapper The button mapper (must not be <code>null</code>).
     * @return <code>true</code> if active, <code>false</code> else.
     */
    protected final boolean isFire(DeviceMapper mapper)
    {
        return device.isFired(mapper);
    }

    /**
     * Check if fire button is enabled once.
     * 
     * @param index The button index (must not be <code>null</code>, must be positive).
     * @return <code>true</code> if active, <code>false</code> else.
     */
    protected final boolean isFireOnce(Integer index)
    {
        return device.isFiredOnce(index);
    }

    /**
     * Check if fire button is enabled once.
     * 
     * @param mapper The button mapper (must not be <code>null</code>).
     * @return <code>true</code> if active, <code>false</code> else.
     */
    protected final boolean isFireOnce(DeviceMapper mapper)
    {
        return device.isFiredOnce(mapper);
    }

    /**
     * Check if checking horizontal direction side.
     * 
     * @param force The force reference.
     * @return <code>true</code> if going to the opposite, <code>false</code> else.
     */
    protected final boolean isChangingDirectionHorizontal(Force force)
    {
        return isGoLeft() && force.getDirectionHorizontal() > 0 || isGoRight() && force.getDirectionHorizontal() < 0;
    }

    /**
     * Check if horizontal collide.
     * 
     * @return <code>true</code> if horizontal collide, <code>false</code> else.
     */
    protected final boolean isCollideX()
    {
        return collideX;
    }

    /**
     * Check if vertical collide.
     * 
     * @return <code>true</code> if vertical collide, <code>false</code> else.
     */
    protected final boolean isCollideY()
    {
        return collideY;
    }

    @Override
    public void enter()
    {
        animatable.play(animation);
    }

    @Override
    public void update(double extrp)
    {
        collideX = false;
        collideY = false;
    }

    @Override
    public void notifyTileCollided(CollisionResult result, CollisionCategory category)
    {
        final String name = category.getName();
        if (Axis.X == category.getAxis())
        {
            if (collideSide(name, "left", "right", transformable.getX(), transformable.getOldX()))
            {
                tileCollidable.apply(result);
                collideX = true;
                onCollided(result, category);
            }
        }
        else if (Axis.Y == category.getAxis())
        {
            if (collideSide(name, "bottom", "top", transformable.getY(), transformable.getOldY()))
            {
                tileCollidable.apply(result);
                collideY = true;
                body.resetGravity();
                onCollided(result, category);
            }
        }
    }

    @Override
    public void notifyCollided(Collidable collidable, Collision with, Collision by)
    {
        // Nothing by default
    }
}
