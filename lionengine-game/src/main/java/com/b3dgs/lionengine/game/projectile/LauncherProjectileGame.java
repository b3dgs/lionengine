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

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Timing;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.game.Alterable;
import com.b3dgs.lionengine.game.ContextGame;
import com.b3dgs.lionengine.game.EntityGame;
import com.b3dgs.lionengine.game.FactoryObjectGame;
import com.b3dgs.lionengine.game.SetupGame;
import com.b3dgs.lionengine.game.Surface;
import com.b3dgs.lionengine.game.purview.Fabricable;

/**
 * Represents a projectile launcher. It allows to handle projectile shots from an entity.
 * 
 * @param <E> The entity type used.
 * @param <E2> The entity attacker type used.
 * @param <P> The projectile type used.
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public abstract class LauncherProjectileGame<E extends EntityGame, E2 extends Surface, P extends ProjectileGame<E, E2>>
        implements Fabricable
{
    /** Launcher level. */
    public final Alterable level;
    /** The shoot timer. */
    private final Timing timer;
    /** The projectile factory reference. */
    private FactoryObjectGame<?> factory;
    /** The projectile handler reference. */
    private HandlerProjectileGame<E, P> handler;
    /** Adapt to target movement. */
    private boolean adaptative;
    /** Entity owner. */
    private E2 owner;
    /** Hit only target. */
    private boolean hitTargetOnly;
    /** Horizontal offset. */
    private int offsetX;
    /** Vertical offset. */
    private int offsetY;
    /** The shoot rate. */
    private int rate;

    /**
     * Constructor.
     * 
     * @param setup The setup reference.
     */
    public LauncherProjectileGame(SetupGame setup)
    {
        owner = null;
        timer = new Timing();
        level = new Alterable(99);
        level.set(0);
        timer.start();
    }

    /**
     * Prepare the fabricated projectile.
     * 
     * @param context The context reference.
     */
    protected abstract void prepareProjectile(ContextGame context);

    /**
     * Perform an attack from the owner (called when the launcher fired its projectile(s)).
     * <p>
     * Use theses functions to add projectiles:
     * </p>
     * <ul>
     * <li>{@link #addProjectile(Media, int, int, double, double, int, int)}</li>
     * </ul>
     * 
     * @param owner The owner reference.
     */
    protected abstract void launchProjectile(E2 owner);

    /**
     * Perform an attack from the owner to target (called when the launcher fired its projectile(s)).
     * 
     * @param owner The owner reference.
     * @param target The target reference.
     */
    protected abstract void launchProjectile(E2 owner, E target);

    /**
     * Start shoot.
     * 
     * @return <code>true</code> if shot, <code>false</code> else.
     */
    public boolean launch()
    {
        if (timer.elapsed(rate))
        {
            launchProjectile(owner);
            timer.restart();
            return true;
        }
        return false;
    }

    /**
     * Start shoot.
     * 
     * @param target The target.
     * @return <code>true</code> if shot, <code>false</code> else.
     */
    public boolean launch(E target)
    {
        if (timer.elapsed(rate))
        {
            launchProjectile(owner, target);
            timer.restart();
            return true;
        }
        return false;
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
     * Set the adaptative flag.
     * 
     * @param adaptative <code>true</code> to anticipate target movement, <code>false</code> else.
     */
    public void setAdaptative(boolean adaptative)
    {
        this.adaptative = adaptative;
    }

    /**
     * Set shoot rate (in millisecond).
     * 
     * @param rate shoot rate.
     */
    public void setRate(int rate)
    {
        this.rate = rate;
    }

    /**
     * Set global starting projectile horizontal offset.
     * 
     * @param x horizontal starting shoot offset.
     */
    public void setOffsetX(int x)
    {
        this.offsetX = x;
    }

    /**
     * Set global starting projectile vertical offset.
     * 
     * @param y vertical starting shoot offset.
     */
    public void setOffsetY(int y)
    {
        this.offsetY = y;
    }

    /**
     * Get rate shoot percent.
     * 
     * @return rate percent value.
     */
    public int getRatePercent()
    {
        final int percent = (int) (timer.elapsed() * 100.0 / rate);
        return UtilMath.fixBetween(percent, 0, 100);
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
     * Add a projectile for the shoot.
     * 
     * @param media The projectile media.
     * @param dmg The projectile damage.
     * @param target The target to reach.
     * @param speed The projectile speed.
     * @param offX The horizontal projectile location offset.
     * @param offY The vertical projectile location offset.
     * @return The created projectile.
     * @throws LionEngineException If media is <code>null</code> or not setup found.
     */
    protected P addProjectile(Media media, int dmg, E target, double speed, int offX, int offY)
            throws LionEngineException
    {
        if (target != null)
        {
            final int sx = owner.getLocationIntX() + owner.getWidth() / 2;
            final int sy = owner.getLocationIntY() + owner.getHeight() / 2;

            int dx = target.getLocationIntX() + target.getWidth() / 2;
            int dy = target.getLocationIntY() + target.getHeight() / 2;

            if (adaptative)
            {
                final int ray = UtilMath.getDistance(owner.getLocationIntX(), owner.getLocationIntY(),
                        target.getLocationIntX(), target.getLocationIntY());
                dx += (int) ((target.getLocationX() - target.getLocationOldX()) / speed * ray);
                dy += (int) ((target.getLocationY() - target.getLocationOldY()) / speed * ray);
            }

            final double dist = Math.max(Math.abs(sx - dx), Math.abs(sy - dy));
            final double vecX = (dx - sx) / dist * speed;
            final double vecY = (dy - sy) / dist * speed;
            return addProjectile(media, -1, 0, dmg, vecX, vecY, offX, offY, target);
        }
        return null;
    }

    /**
     * Add a projectile for the shoot.
     * 
     * @param media The projectile media.
     * @param dmg The projectile damage.
     * @param vecX The horizontal projectile move.
     * @param vecY The vertical projectile move.
     * @param offX The horizontal projectile location offset.
     * @param offY The vertical projectile location offset.
     * @return The created projectile.
     * @throws LionEngineException If media is <code>null</code> or not setup found.
     */
    protected P addProjectile(Media media, int dmg, double vecX, double vecY, int offX, int offY)
            throws LionEngineException
    {
        return addProjectile(media, -1, 0, dmg, vecX, vecY, offX, offY, null);
    }

    /**
     * Add a linked projectile for the shoot.
     * 
     * @param media The projectile media.
     * @param id projectile id (when a projectile is destroyed, all projectiles with this id are also destroyed).
     * @param dmg The projectile damage.
     * @param vecX The horizontal projectile move.
     * @param vecY The vertical projectile move.
     * @param offX The horizontal projectile location offset.
     * @param offY The vertical projectile location offset.
     * @return The created projectile.
     * @throws LionEngineException If media is <code>null</code> or not setup found.
     */
    protected P addProjectile(Media media, int id, int dmg, double vecX, double vecY, int offX, int offY)
            throws LionEngineException
    {
        return addProjectile(media, id, 0, dmg, vecX, vecY, offX, offY, null);
    }

    /**
     * Add a delayed projectile for the shoot.
     * 
     * @param media The projectile media.
     * @param delay delay before be thrown.
     * @param dmg The projectile damage.
     * @param vecX The horizontal projectile move.
     * @param vecY The vertical projectile move.
     * @param offX The horizontal projectile location offset.
     * @param offY The vertical projectile location offset.
     * @return The created projectile.
     * @throws LionEngineException If media is <code>null</code> or not setup found.
     */
    protected P addProjectile(Media media, long delay, int dmg, double vecX, double vecY, int offX, int offY)
            throws LionEngineException
    {
        return addProjectile(media, -1, delay, dmg, vecX, vecY, offX, offY, null);
    }

    /**
     * Add a linked projectile for the shoot.
     * 
     * @param media The projectile media.
     * @param id projectile id (when a projectile is destroyed, all projectiles with this id are also destroyed).
     * @param delay delay before be thrown.
     * @param dmg The projectile damage.
     * @param vecX The horizontal projectile move.
     * @param vecY The vertical projectile move.
     * @param offX The horizontal projectile location offset.
     * @param offY The vertical projectile location offset.
     * @param target The target reference.
     * @return The created projectile.
     * @throws LionEngineException If media is <code>null</code> or not setup found.
     */
    private P addProjectile(Media media, int id, long delay, int dmg, double vecX, double vecY, int offX, int offY,
            E target) throws LionEngineException
    {
        final P projectile = factory.create(media);

        projectile.setOwner(owner);
        projectile.setId(id);
        projectile.setTarget(target);
        projectile.setCanHitTargetOnly(hitTargetOnly);
        projectile.damages.setMin(dmg);
        projectile.damages.setMax(dmg);
        final int x = owner.getLocationIntX();
        final int y = owner.getLocationIntY();
        projectile.start(x, y, offX + offsetX, offY + offsetY, vecX, vecY);
        projectile.setDelay(delay);

        handler.add(projectile);
        return projectile;
    }

    /*
     * Fabricable
     */

    @Override
    public void prepare(ContextGame context) throws LionEngineException
    {
        factory = context.getService(FactoryObjectGame.class);
        handler = context.getService(HandlerProjectileGame.class);
        prepareProjectile(context);
    }
}
