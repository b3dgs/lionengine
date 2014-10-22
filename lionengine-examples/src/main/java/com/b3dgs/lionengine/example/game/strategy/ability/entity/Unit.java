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
package com.b3dgs.lionengine.example.game.strategy.ability.entity;

import java.util.Collection;

import com.b3dgs.lionengine.anim.Animation;
import com.b3dgs.lionengine.example.game.strategy.ability.map.Map;
import com.b3dgs.lionengine.game.ContextGame;
import com.b3dgs.lionengine.game.Orientation;
import com.b3dgs.lionengine.game.SetupSurfaceGame;
import com.b3dgs.lionengine.game.Tiled;
import com.b3dgs.lionengine.game.configurer.ConfigAnimations;
import com.b3dgs.lionengine.game.configurer.Configurer;
import com.b3dgs.lionengine.game.strategy.ability.mover.MoverModel;
import com.b3dgs.lionengine.game.strategy.ability.mover.MoverServices;
import com.b3dgs.lionengine.game.strategy.ability.mover.MoverUsedServices;

/**
 * Abstract mover entity implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
abstract class Unit
        extends Entity
        implements MoverUsedServices, MoverServices
{
    /** Idle animation. */
    protected final Animation animIdle;
    /** Walk animation. */
    protected final Animation animWalk;
    /** Mover model. */
    private MoverModel mover;

    /**
     * Constructor.
     * 
     * @param setup The setup reference.
     */
    protected Unit(SetupSurfaceGame setup)
    {
        super(setup);
        final Configurer configurer = setup.getConfigurer();
        final ConfigAnimations configAnimations = ConfigAnimations.create(configurer);
        animIdle = configAnimations.getAnimation("idle");
        animWalk = configAnimations.getAnimation("walk");
        setLayer(1);
        play(animIdle);
    }

    /*
     * Entity
     */

    @Override
    public void prepareEntity(ContextGame context)
    {
        super.prepareEntity(context);
        mover = new MoverModel(this, context.getService(Map.class));
    }

    @Override
    public void update(double extrp)
    {
        mover.updateMoves(extrp);
        mirror(getOrientation().ordinal() > Orientation.ORIENTATIONS_NUMBER_HALF);
        super.update(extrp);
    }

    @Override
    public void stop()
    {
        mover.stopMoves();
    }

    /*
     * Mover model
     */

    @Override
    public Orientation getMovementOrientation()
    {
        return mover.getMovementOrientation();
    }

    @Override
    public void pointTo(int tx, int ty)
    {
        mover.pointTo(tx, ty);
    }

    @Override
    public void pointTo(Tiled entity)
    {
        mover.pointTo(entity);
    }

    @Override
    public void setDestination(double extrp, double dx, double dy)
    {
        mover.setDestination(extrp, dx, dy);
    }

    @Override
    public boolean setDestination(int tx, int ty)
    {
        return mover.setDestination(tx, ty);
    }

    @Override
    public void setDestination(Tiled tiled)
    {
        mover.setDestination(tiled);
    }

    @Override
    public void setLocation(int tx, int ty)
    {
        super.setLocation(tx, ty);
        mover.setLocation(tx, ty);
    }

    @Override
    public boolean isPathAvailable(int tx, int ty)
    {
        return mover.isPathAvailable(tx, ty);
    }

    @Override
    public void setIgnoreId(Integer id, boolean state)
    {
        mover.setIgnoreId(id, state);
    }

    @Override
    public boolean isIgnoredId(Integer id)
    {
        return mover.isIgnoredId(id);
    }

    @Override
    public void clearIgnoredId()
    {
        mover.clearIgnoredId();
    }

    @Override
    public void setSharedPathIds(Collection<Integer> ids)
    {
        mover.setSharedPathIds(ids);
    }

    @Override
    public void clearSharedPathIds()
    {
        mover.clearSharedPathIds();
    }

    @Override
    public void updateMoves(double extrp)
    {
        mover.updateMoves(extrp);
    }

    @Override
    public void stopMoves()
    {
        mover.stopMoves();
    }

    @Override
    public void setSpeed(double speedX, double speedY)
    {
        mover.setSpeed(speedX, speedY);
    }

    @Override
    public boolean isMoving()
    {
        return mover.isMoving();
    }

    @Override
    public boolean isDestinationReached()
    {
        return mover.isDestinationReached();
    }

    @Override
    public double getSpeedX()
    {
        return mover.getSpeedX();
    }

    @Override
    public double getSpeedY()
    {
        return mover.getSpeedY();
    }

    @Override
    public double getMoveX()
    {
        return mover.getMoveX();
    }

    @Override
    public double getMoveY()
    {
        return mover.getMoveY();
    }

    /*
     * Mover listener
     */

    @Override
    public void notifyStartMove()
    {
        play(animWalk);
    }

    @Override
    public void notifyMoving()
    {
        // Nothing to do
    }

    @Override
    public void notifyArrived()
    {
        play(animIdle);
    }
}
