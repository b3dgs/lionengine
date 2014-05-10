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
package com.b3dgs.lionengine.game.entity;

import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.game.CameraGame;
import com.b3dgs.lionengine.game.CollisionData;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.ObjectGame;
import com.b3dgs.lionengine.game.SetupGame;
import com.b3dgs.lionengine.game.purview.Body;
import com.b3dgs.lionengine.game.purview.Collidable;
import com.b3dgs.lionengine.game.purview.Mirrorable;
import com.b3dgs.lionengine.game.purview.model.BodyModel;
import com.b3dgs.lionengine.game.purview.model.CollidableModel;
import com.b3dgs.lionengine.game.purview.model.MirrorableModel;
import com.b3dgs.lionengine.geom.Line;
import com.b3dgs.lionengine.geom.Rectangle;

/**
 * Main object that can be used by any higher level object for a game. It supports external configuration, collision,
 * and mirror.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public abstract class EntityGame
        extends ObjectGame
        implements Body, Collidable, Mirrorable
{
    /** Body object reference. */
    private final Body body;
    /** Collidable object reference. */
    private final Collidable collidable;
    /** Mirrorable object reference. */
    private final Mirrorable mirrorable;

    /**
     * Constructor.
     * 
     * @param setup The setup reference.
     */
    public EntityGame(SetupGame setup)
    {
        super(setup);
        body = new BodyModel();
        mirrorable = new MirrorableModel();
        collidable = new CollidableModel(this);
    }

    /**
     * Update the entity.
     * 
     * @param extrp The extrapolation value.
     */
    public abstract void update(double extrp);

    /**
     * Get the distance between the entity and the specified other entity.
     * 
     * @param entity The entity to compare to.
     * @return The distance value.
     */
    public double getDistance(EntityGame entity)
    {
        return UtilMath.getDistance(getLocationX(), getLocationY(), entity.getLocationX(), entity.getLocationY());
    }

    /*
     * Body
     */

    @Override
    public void updateGravity(double extrp, int desiredFps, Force... forces)
    {
        body.updateGravity(extrp, desiredFps, forces);
    }

    @Override
    public void resetGravity()
    {
        body.resetGravity();
    }

    @Override
    public void invertAxisY(boolean state)
    {
        body.invertAxisY(state);
    }

    @Override
    public void setGravityMax(double max)
    {
        body.setGravityMax(max);
    }

    @Override
    public void setMass(double mass)
    {
        body.setMass(mass);
    }

    @Override
    public double getMass()
    {
        return body.getMass();
    }

    @Override
    public double getWeight()
    {
        return body.getWeight();
    }

    /*
     * Localizable
     */

    @Override
    public void teleport(double x, double y)
    {
        body.teleport(x, y);
    }

    @Override
    public void teleportX(double x)
    {
        body.teleportX(x);
    }

    @Override
    public void teleportY(double y)
    {
        body.teleportY(y);
    }

    @Override
    public void moveLocation(double extrp, Force force, Force... forces)
    {
        body.moveLocation(extrp, force, forces);
    }

    @Override
    public void moveLocation(double extrp, double vx, double vy)
    {
        body.moveLocation(extrp, vx, vy);
    }

    @Override
    public void setLocation(double x, double y)
    {
        body.setLocation(x, y);
    }

    @Override
    public void setLocationX(double x)
    {
        body.setLocationX(x);
    }

    @Override
    public void setLocationY(double y)
    {
        body.setLocationY(y);
    }

    @Override
    public void setSize(int width, int height)
    {
        body.setSize(width, height);
    }

    @Override
    public double getLocationX()
    {
        return body.getLocationX();
    }

    @Override
    public double getLocationY()
    {
        return body.getLocationY();
    }

    @Override
    public int getLocationIntX()
    {
        return body.getLocationIntX();
    }

    @Override
    public int getLocationIntY()
    {
        return body.getLocationIntY();
    }

    @Override
    public double getLocationOldX()
    {
        return body.getLocationOldX();
    }

    @Override
    public double getLocationOldY()
    {
        return body.getLocationOldY();
    }

    @Override
    public int getWidth()
    {
        return body.getWidth();
    }

    @Override
    public int getHeight()
    {
        return body.getHeight();
    }

    /*
     * Collidable
     */

    @Override
    public void updateCollision()
    {
        collidable.updateCollision();
    }

    @Override
    public void setCollision(CollisionData collision)
    {
        collidable.setCollision(collision);
    }

    @Override
    public boolean collide(Collidable entity)
    {
        return collidable.collide(entity);
    }

    @Override
    public boolean collide(Rectangle area)
    {
        return collidable.collide(area);
    }

    @Override
    public void renderCollision(Graphic g, CameraGame camera)
    {
        collidable.renderCollision(g, camera);
    }

    @Override
    public CollisionData getCollisionData()
    {
        return collidable.getCollisionData();
    }

    @Override
    public Rectangle getCollisionBounds()
    {
        return collidable.getCollisionBounds();
    }

    @Override
    public Line getCollisionRay()
    {
        return collidable.getCollisionRay();
    }

    /*
     * Mirrorable
     */

    @Override
    public void mirror(boolean state)
    {
        mirrorable.mirror(state);
    }

    @Override
    public void updateMirror()
    {
        mirrorable.updateMirror();
    }

    @Override
    public void setMirrorCancel(boolean state)
    {
        mirrorable.setMirrorCancel(state);
    }

    @Override
    public boolean getMirrorCancel()
    {
        return mirrorable.getMirrorCancel();
    }

    @Override
    public boolean getMirror()
    {
        return mirrorable.getMirror();
    }
}
