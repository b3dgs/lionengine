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
package com.b3dgs.lionengine.game;

import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.game.purview.Collidable;
import com.b3dgs.lionengine.game.purview.Mirrorable;
import com.b3dgs.lionengine.game.purview.model.CollidableModel;
import com.b3dgs.lionengine.game.purview.model.MirrorableModel;
import com.b3dgs.lionengine.geom.Line;
import com.b3dgs.lionengine.geom.Rectangle;

/**
 * Main object that can be used by any higher level object for a game. It supports collision and mirror.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public abstract class EntityGame
        extends ObjectGame
        implements Collidable, Mirrorable
{
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
        mirrorable = new MirrorableModel();
        collidable = new CollidableModel(this);
    }

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
     * Collidable
     */

    @Override
    public void updateCollision()
    {
        collidable.updateCollision();
    }

    @Override
    public void setCollision(Collision collision)
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
    public Collision getCollisionData()
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
    public boolean getMirror()
    {
        return mirrorable.getMirror();
    }
}
