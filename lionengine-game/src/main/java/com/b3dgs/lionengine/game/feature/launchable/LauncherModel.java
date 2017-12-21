/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Localizable;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Timing;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.game.Direction;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.feature.Factory;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.FeatureProvider;
import com.b3dgs.lionengine.game.feature.Handler;
import com.b3dgs.lionengine.game.feature.Identifiable;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.util.UtilMath;

/**
 * Default launcher model implementation.
 */
public class LauncherModel extends FeatureModel implements Launcher
{
    /** Launcher listeners. */
    private final Collection<LauncherListener> listenersLauncher = new HashSet<LauncherListener>();
    /** Launchable listeners. */
    private final Collection<LaunchableListener> listenersLaunchable = new HashSet<LaunchableListener>();
    /** Delayed launches. */
    private final Collection<DelayedLaunch> delayed = new ArrayList<DelayedLaunch>();
    /** Delayed launches launched. */
    private final Collection<DelayedLaunch> launched = new ArrayList<DelayedLaunch>();
    /** Fire timer. */
    private final Timing fire = new Timing();
    /** Levels configuration. */
    private final List<LauncherConfig> config;
    /** Factory reference. */
    private final Factory factory;
    /** Handler reference. */
    private final Handler handler;
    /** Launchable configuration. */
    private Iterable<LaunchableConfig> launchables;
    /** Localizable model. */
    private Localizable localizable;
    /** Target reference. */
    private Localizable target;
    /** Current level. */
    private int level;
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
     * @param services The services reference.
     * @param setup The setup reference.
     */
    public LauncherModel(Services services, Setup setup)
    {
        super();

        factory = services.get(Factory.class);
        handler = services.get(Handler.class);

        config = LauncherConfig.imports(setup);
        launchables = config.get(0).getLaunchables();
        rate = config.get(0).getRate();
        fire.start();
    }

    /**
     * Called when fire is performed.
     * 
     * @param initial The fire launch initial speed for force transfer.
     * @throws LionEngineException If the fired object is not a {@link Launchable}.
     */
    private void fired(Direction initial)
    {
        for (final LauncherListener listener : listenersLauncher)
        {
            listener.notifyFired();
        }
        for (final LaunchableConfig config : launchables)
        {
            final Media media = Medias.create(config.getMedia());
            final Featurable featurable = factory.create(media);
            try
            {
                final Launchable launchable = featurable.getFeature(Launchable.class);
                if (config.getDelay() > 0)
                {
                    delayed.add(new DelayedLaunch(config, initial, featurable, launchable));
                }
                else
                {
                    launch(config, initial, featurable, launchable);
                }
            }
            catch (final LionEngineException exception)
            {
                featurable.getFeature(Identifiable.class).destroy();
                throw exception;
            }
        }
    }

    /**
     * Launch the launchable.
     * 
     * @param config The launch configuration.
     * @param initial The fire launch initial direction for force transfer.
     * @param featurable The featurable representing the launchable.
     * @param launchable The launchable to launch.
     */
    private void launch(LaunchableConfig config, Direction initial, Featurable featurable, Launchable launchable)
    {
        final double x = localizable.getX() + config.getOffsetX() + offsetX;
        final double y = localizable.getY() + config.getOffsetY() + offsetY;
        launchable.setLocation(x, y);

        final Force vector = new Force(config.getVector());
        vector.addDirection(1.0, initial);
        launchable.setVector(computeVector(vector));
        launchable.launch();

        for (final LaunchableListener listener : listenersLaunchable)
        {
            listener.notifyFired(launchable);
        }
        handler.add(featurable);
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
        vector.setDestination(vector.getDirectionHorizontal(), vector.getDirectionVertical());
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
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        localizable = provider.getFeature(Transformable.class);

        if (provider instanceof LauncherListener)
        {
            addListener((LauncherListener) provider);
        }
        if (provider instanceof LaunchableListener)
        {
            addListener((LaunchableListener) provider);
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
        if (listener instanceof LaunchableListener)
        {
            addListener((LaunchableListener) listener);
        }
    }

    @Override
    public void addListener(LauncherListener listener)
    {
        listenersLauncher.add(listener);
    }

    @Override
    public void addListener(LaunchableListener listener)
    {
        listenersLaunchable.add(listener);
    }

    @Override
    public boolean fire()
    {
        return fire(Direction.ZERO, null);
    }

    @Override
    public boolean fire(Direction initial)
    {
        return fire(initial, null);
    }

    @Override
    public boolean fire(Localizable target)
    {
        return fire(Direction.ZERO, target);
    }

    @Override
    public boolean fire(Direction initial, Localizable target)
    {
        this.target = target;
        if (fire.elapsed(rate))
        {
            fired(initial);
            fire.restart();
            return true;
        }
        return false;
    }

    @Override
    public void update(double extrp)
    {
        for (final DelayedLaunch launch : delayed)
        {
            if (launch.isReady())
            {
                launch(launch.config, launch.initial, launch.featurable, launch.launchable);
                launched.add(launch);
            }
        }
        delayed.removeAll(launched);
        launched.clear();
    }

    @Override
    public void setOffset(int x, int y)
    {
        offsetX = x;
        offsetY = y;
    }

    @Override
    public void setLevel(int level)
    {
        Check.superiorOrEqual(level, 0);

        this.level = level;
        launchables = config.get(level).getLaunchables();
        rate = config.get(level).getRate();
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

    @Override
    public int getLevel()
    {
        return level;
    }

    @Override
    public long getRate()
    {
        return rate;
    }

    /**
     * Represents a delayed launch.
     */
    private static class DelayedLaunch
    {
        /** Delay timing. */
        private final Timing timing = new Timing();
        /** Launchable configuration reference. */
        private final LaunchableConfig config;
        /** Initial direction for launch. */
        private final Direction initial;
        /** Featurable reference to launch. */
        private final Featurable featurable;
        /** Launchable reference to launch. */
        private final Launchable launchable;

        /**
         * Create a delayed launch.
         * 
         * @param config The launch configuration.
         * @param initial The launch initial direction.
         * @param featurable The featurable to launch.
         * @param launchable The launchable to launch.
         */
        DelayedLaunch(LaunchableConfig config, Direction initial, Featurable featurable, Launchable launchable)
        {
            super();
            this.config = config;
            this.initial = initial;
            this.featurable = featurable;
            this.launchable = launchable;
            timing.start();
        }

        /**
         * Check if launch can be performed.
         * 
         * @return <code>true</code> if delayed elapsed, <code>false</code> else.
         */
        public boolean isReady()
        {
            return timing.elapsed(config.getDelay());
        }
    }
}
