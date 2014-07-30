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
package com.b3dgs.lionengine.game.projectile;

import com.b3dgs.lionengine.Timing;
import com.b3dgs.lionengine.game.Damages;
import com.b3dgs.lionengine.game.EntityGame;
import com.b3dgs.lionengine.game.SetupSurfaceGame;
import com.b3dgs.lionengine.game.Surface;
import com.b3dgs.lionengine.game.configurable.Configurable;
import com.b3dgs.lionengine.game.configurable.SizeData;

/**
 * Standard projectile implementation, including collision and moves handling.
 * 
 * @param <E> The entity type used.
 * @param <E2> The source type used.
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public abstract class ProjectileGame<E extends EntityGame, E2 extends Surface>
        extends EntityGame
{
    /** Damages. */
    public final Damages damages;
    /** Projectile id. */
    private int id;
    /** Delay before being added in the handler. */
    private final Timing delay;
    /** Horizontal vector. */
    private double vecX;
    /** Vertical vector. */
    private double vecY;
    /** Horizontal offset. */
    private int offX;
    /** Vertical offset. */
    private int offY;
    /** Target. */
    private E target;
    /** Entity owner. */
    private E2 owner;
    /** Hit only target. */
    private boolean hitTargetOnly;
    /** Elapsed time. */
    private long time;

    /**
     * Create a new projectile. The projectile should contain at least theses data in the configuration file:
     * 
     * <pre>
     * {@code
     * <entity surface="projectile.png">
     *     <size width="10" height="10"/>
     *     <offset x="0" y="0"/>
     * </entity>
     * 
     * }
     * </pre>
     * 
     * @param setup The entity setup.
     */
    public ProjectileGame(SetupSurfaceGame setup)
    {
        super(setup);
        damages = new Damages();
        final Configurable configurable = setup.getConfigurable();
        final SizeData sizeData = configurable.getSize();
        setSize(sizeData.getWidth(), sizeData.getHeight());
        id = -1;
        owner = null;
        delay = new Timing();
    }

    /**
     * Action called when projectile hit an entity.
     * 
     * @param entity The entity hit.
     * @param damages The damages.
     */
    public abstract void onHit(E entity, int damages);

    /**
     * Define the projectile movement.
     * 
     * @param extrp The extrapolation value.
     * @param vecX The horizontal vector.
     * @param vecY The vertical vector.
     */
    protected abstract void updateMovement(double extrp, double vecX, double vecY);

    /**
     * Start projectile handling.
     * 
     * @param x The horizontal location.
     * @param y The vertical location.
     * @param offX The horizontal location offset.
     * @param offY The vertical location offset.
     * @param vecX The horizontal vector.
     * @param vecY The vertical vector.
     */
    public void start(int x, int y, int offX, int offY, double vecX, double vecY)
    {
        setLocation(x + offX + getWidth() / 2, y + offY + getHeight() / 2);
        this.vecX = vecX;
        this.vecY = vecY;
        this.offX = offX;
        this.offY = offY;
    }

    /**
     * Set the projectile target.
     * 
     * @param target The entity target.
     */
    public void setTarget(E target)
    {
        this.target = target;
    }

    /**
     * Set the id.
     * 
     * @param id The projectile id (when a projectile is destroyed, all projectiles with this id are also destroyed).
     *            Can be -1 to ignore it.
     */
    public void setId(int id)
    {
        this.id = id;
    }

    /**
     * Set the hit target properties.
     * 
     * @param hitTargetOnly <code>true</code> to make the projectile hit only the target, <code>false</code> to allows
     *            other hits.
     */
    public void setCanHitTargetOnly(boolean hitTargetOnly)
    {
        this.hitTargetOnly = hitTargetOnly;
    }

    /**
     * Set the projectile owner.
     * 
     * @param owner The entity owner.
     */
    public void setOwner(E2 owner)
    {
        this.owner = owner;
    }

    /**
     * Set projectile delay (time before being added in the handler).
     * 
     * @param delay The delay time.
     */
    public void setDelay(long delay)
    {
        time = delay;
        this.delay.start();
    }

    /**
     * Get the owner.
     * 
     * @return The owner.
     */
    public E2 getOwner()
    {
        return owner;
    }

    /**
     * Get the target.
     * 
     * @return The target.
     */
    public E getTarget()
    {
        return target;
    }

    /**
     * Get the id value.
     * 
     * @return The id value.
     */
    public int getProjectileId()
    {
        return id;
    }

    /**
     * Get the hit target only state.
     * 
     * @return <code>true</code> if can hit only the target, <code>false</code> else.
     */
    public boolean canHitOnlyTarget()
    {
        return hitTargetOnly;
    }

    /**
     * Check if projectile can be added in the handler (related to delay).
     * 
     * @return <code>true</code> if can be added, <code>false</code> else.
     */
    public boolean canBeAdded()
    {
        return delay.elapsed(time);
    }

    /*
     * EntityGame
     */

    @Override
    public void update(double extrp)
    {
        if (time > 0)
        {
            final int x = owner.getLocationIntX() + offX;
            final int y = owner.getLocationIntY() + offY;
            teleport(x + getWidth() / 2, y + getHeight() / 2);
            time = 0;
        }
        updateMovement(extrp, vecX, vecY);
        updateCollision();
    }
}
