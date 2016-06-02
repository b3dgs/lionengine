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
package com.b3dgs.lionengine.game.feature.launchable;

import java.util.Collection;
import java.util.HashSet;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Localizable;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Timing;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.feature.Factory;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.FeatureProvider;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.identifiable.Identifiable;
import com.b3dgs.lionengine.game.feature.transformable.Transformable;
import com.b3dgs.lionengine.game.handler.Handler;
import com.b3dgs.lionengine.util.UtilMath;

/**
 * Default launcher model implementation.
 */
public class LauncherModel extends FeatureModel implements Launcher
{
    /** Launcher listeners. */
    private final Collection<LauncherListener> listeners = new HashSet<LauncherListener>();
    /** Fire timer. */
    private final Timing fire = new Timing();
    /** Launchable configuration. */
    private final Iterable<LaunchableConfig> launchables;
    /** Factory reference. */
    private Factory factory;
    /** Handler reference. */
    private Handler handler;
    /** Localizable model. */
    private Localizable localizable;
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
     * The {@link Services} must provide the following services:
     * </p>
     * <ul>
     * <li>{@link Factory}</li>
     * <li>{@link Handler}</li>
     * </ul>
     * <p>
     * The {@link Featurable} must have:
     * </p>
     * <ul>
     * <li>{@link Transformable}</li>
     * </ul>
     * <p>
     * The {@link Setup} must provide a valid {@link LauncherConfig}.
     * </p>
     * <p>
     * If the {@link Featurable} is a {@link LauncherListener}, it will automatically
     * {@link #addListener(LauncherListener)}
     * on it.
     * </p>
     * 
     * @param setup The setup reference.
     */
    public LauncherModel(Setup setup)
    {
        super();
        final LauncherConfig config = LauncherConfig.imports(setup);
        launchables = config.getLaunchables();
        rate = config.getRate();
        fire.start();
    }

    /**
     * Called when fire is performed.
     * 
     * @throws LionEngineException If the fired object is not a {@link Launchable}.
     */
    private void fired()
    {
        for (final LaunchableConfig launchable : launchables)
        {
            final Media media = Medias.create(launchable.getMedia());
            final Featurable featurable = factory.create(media);
            try
            {
                final Launchable projectile = featurable.getFeature(Launchable.class);
                projectile.setDelay(launchable.getDelay());
                projectile.setLocation(localizable.getX() + offsetX, localizable.getY() + offsetY);
                projectile.setVector(computeVector(launchable.getVector()));
                projectile.launch();
            }
            catch (final LionEngineException exception)
            {
                featurable.getFeature(Identifiable.class).destroy();
                throw exception;
            }
            for (final LauncherListener listener : listeners)
            {
                listener.notifyFired(featurable);
            }
            handler.add(featurable);
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
            final double ray = UtilMath.getDistance(localizable.getX(),
                                                    localizable.getY(),
                                                    target.getX(),
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
    public void prepare(FeatureProvider provider, Services services)
    {
        super.prepare(provider, services);

        factory = services.get(Factory.class);
        handler = services.get(Handler.class);
        localizable = provider.getFeature(Transformable.class);

        if (provider instanceof LauncherListener)
        {
            addListener((LauncherListener) provider);
        }
    }

    @Override
    public void checkListener(Object listener)
    {
        super.checkListener(listener);

        if (listener instanceof LauncherListener)
        {
            addListener((LauncherListener) listener);
        }
    }

    @Override
    public void addListener(LauncherListener listener)
    {
        listeners.add(listener);
    }

    @Override
    public void fire()
    {
        fire(null);
    }

    @Override
    public void fire(Localizable target)
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
