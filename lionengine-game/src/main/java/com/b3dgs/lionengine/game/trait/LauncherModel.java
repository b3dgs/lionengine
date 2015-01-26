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
package com.b3dgs.lionengine.game.trait;

import java.util.Collection;
import java.util.HashSet;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Localizable;
import com.b3dgs.lionengine.Timing;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.core.Core;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.Services;
import com.b3dgs.lionengine.game.configurer.ConfigLaunchable;
import com.b3dgs.lionengine.game.configurer.ConfigLauncher;
import com.b3dgs.lionengine.game.configurer.Configurer;
import com.b3dgs.lionengine.game.factory.Factory;
import com.b3dgs.lionengine.game.handler.Handler;
import com.b3dgs.lionengine.game.handler.ObjectGame;

/**
 * Default launcher model implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class LauncherModel
        extends TraitModel
        implements Launcher
{
    /** Media error. */
    private static final String ERROR_MEDIA = "Fired object is not a " + Launchable.class + ": ";

    /** Launcher listeners. */
    private final Collection<LauncherListener> listeners;
    /** Launchable configuration. */
    private final Iterable<ConfigLaunchable> launchables;
    /** Localizable model. */
    private final Localizable localizable;
    /** Fire timer. */
    private final Timing fire;
    /** Factory reference. */
    private final Factory factory;
    /** Handler reference. */
    private final Handler handler;
    /** Target reference. */
    private Localizable target;
    /** Fire rate in millisecond. */
    private long rate;
    /** Horizontal offset. */
    private int offsetX;
    /** Vertical offset. */
    private int offsetY;

    /**
     * Create a launcher model.
     * <p>
     * The owner must have the following {@link Trait}:
     * </p>
     * <ul>
     * <li>{@link Localizable}</li>
     * </ul>
     * <p>
     * The {@link Configurer} must provide a valid configuration compatible with {@link ConfigLauncher}.
     * </p>
     * <p>
     * The {@link Services} must provide the following services:
     * </p>
     * <ul>
     * <li>{@link Factory}</li>
     * <li>{@link Handler}</li>
     * </ul>
     * 
     * @param owner The owner reference.
     * @param configurer The configuration reference.
     * @param context The context reference.
     * @throws LionEngineException If missing {@link Trait}, bad {@link Configurer} or {@link Services} services.
     */
    public LauncherModel(ObjectGame owner, Configurer configurer, Services context) throws LionEngineException
    {
        super(owner);
        localizable = getTrait(Localizable.class);
        listeners = new HashSet<>();
        fire = new Timing();

        factory = context.get(Factory.class);
        handler = context.get(Handler.class);

        final ConfigLauncher config = ConfigLauncher.create(configurer);
        launchables = config.getLaunchables();
        rate = config.getRate();
        fire.start();
    }

    /**
     * Called when fire is performed.
     * 
     * @throws LionEngineException If the fired object is not a {@link Launchable}.
     */
    private void fired() throws LionEngineException
    {
        for (final ConfigLaunchable launchable : launchables)
        {
            final Media media = Core.MEDIA.create(launchable.getMedia());
            final ObjectGame object = factory.create(media);
            final Launchable projectile = object.getTrait(Launchable.class);
            if (projectile == null)
            {
                object.destroy();
                throw new LionEngineException(media, ERROR_MEDIA);
            }
            projectile.setDelay(launchable.getDelay());
            projectile.setVector(computeVector(launchable.getVector()));
            projectile.launch(localizable.getX() + offsetX, localizable.getY() + offsetY);

            for (final LauncherListener listener : listeners)
            {
                listener.notifyFired(object);
            }
            handler.add(object);
        }
    }

    /**
     * Compute the vector used for launch.
     * 
     * @param vector The initial vector used for launch.
     * @return The vector used for launch.
     */
    private Force computeVector(Force vector)
    {
        if (target != null)
        {
            return computeVector(vector, target);
        }
        return vector;
    }

    /**
     * Compute the force vector depending of the target.
     * 
     * @param vector The initial vector used for launch.
     * @param target The target reference.
     * @return The computed force to reach target.
     */
    private Force computeVector(Force vector, Localizable target)
    {
        final double sx = localizable.getX();
        final double sy = localizable.getY();

        double dx = target.getX();
        double dy = target.getY();

        if (target instanceof Transformable)
        {
            final Transformable transformable = (Transformable) target;
            final double ray = UtilMath.getDistance(localizable.getX(), localizable.getY(), target.getX(),
                    target.getY());
            dx += (int) ((target.getX() - transformable.getOldX()) / vector.getDirectionHorizontal() * ray);
            dy += (int) ((target.getY() - transformable.getOldY()) / vector.getDirectionVertical() * ray);
        }

        final double dist = Math.max(Math.abs(sx - dx), Math.abs(sy - dy));

        final double vecX = (dx - sx) / dist * vector.getDirectionHorizontal();
        final double vecY = (dy - sy) / dist * vector.getDirectionVertical();

        final Force force = new Force(vector);
        force.setDestination(vecX, vecY);
        return force;
    }

    /*
     * Launcher
     */

    @Override
    public void addListener(LauncherListener listener)
    {
        listeners.add(listener);
    }

    @Override
    public void fire() throws LionEngineException
    {
        fire(null);
    }

    @Override
    public void fire(Localizable target) throws LionEngineException
    {
        this.target = target;
        if (fire.elapsed(rate))
        {
            fired();
            fire.restart();
        }
    }

    @Override
    public void setOffset(int x, int y)
    {
        offsetX = x;
        offsetY = y;
    }

    @Override
    public void setRate(long rate)
    {
        this.rate = rate;
    }

    @Override
    public int getOffsetX()
    {
        return offsetX;
    }

    @Override
    public int getOffsetY()
    {
        return offsetY;
    }
}
