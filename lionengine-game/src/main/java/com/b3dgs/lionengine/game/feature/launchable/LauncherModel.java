/*
 * Copyright (C) 2013-2021 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.launchable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.ListenableModel;
import com.b3dgs.lionengine.Localizable;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.audio.Audio;
import com.b3dgs.lionengine.audio.AudioFactory;
import com.b3dgs.lionengine.game.Direction;
import com.b3dgs.lionengine.game.DirectionNone;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.feature.Factory;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Handler;
import com.b3dgs.lionengine.game.feature.Identifiable;
import com.b3dgs.lionengine.game.feature.Mirrorable;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;

/**
 * Launcher model implementation.
 */
public class LauncherModel extends FeatureModel implements Launcher, Recyclable
{
    /** Launcher listeners. */
    private final ListenableModel<LauncherListener> listenable = new ListenableModel<>();
    /** Launchable listeners. */
    private final Collection<LaunchableListener> listenersLaunchable = new HashSet<>();
    /** Delayed launches. */
    private final Collection<DelayedLaunch> delayed = new ArrayList<>();
    /** Delayed launches launched. */
    private final Collection<DelayedLaunch> launched = new ArrayList<>();
    /** Cached audio. */
    private final Map<String, Audio> audio = new HashMap<>();
    /** Fire tick. */
    private final Tick fire = new Tick();
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
    /** Mirrorable reference. */
    private Mirrorable mirrorable;
    /** Current level. */
    private int level;
    /** Fire rate in tick. */
    private long rate;
    /** Mirrorable flag. */
    private boolean mirror;
    /** Horizontal offset. */
    private int offsetX;
    /** Vertical offset. */
    private int offsetY;

    /**
     * Create feature.
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
     * {@link #addListener(LauncherListener)} on it.
     * </p>
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid argument.
     */
    public LauncherModel(Services services, Setup setup)
    {
        super(services, setup);

        factory = services.get(Factory.class);
        handler = services.get(Handler.class);

        config = LauncherConfig.imports(setup);
        if (config.isEmpty())
        {
            launchables = Collections.emptyList();
            rate = 0;
        }
        else
        {
            launchables = config.get(0).getLaunchables();
            rate = config.get(0).getRate();
            mirror = config.get(0).hasMirrorable();
        }
    }

    /**
     * Called when fire is performed.
     * 
     * @param initial The fire launch initial speed for force transfer.
     * @throws LionEngineException If the fired object is not a {@link Launchable}.
     */
    private void fired(Direction initial)
    {
        for (int i = 0; i < listenable.size(); i++)
        {
            listenable.get(i).notifyFired();
        }
        for (final LaunchableConfig launchableConfig : launchables)
        {
            final Media media = Medias.create(launchableConfig.getMedia());
            final Featurable featurable = factory.create(media);
            try
            {
                final Launchable launchable = featurable.getFeature(Launchable.class);
                if (launchableConfig.getDelay() > 0)
                {
                    delayed.add(new DelayedLaunch(launchableConfig, initial, featurable, launchable));
                }
                else
                {
                    launch(launchableConfig, initial, featurable, launchable);
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
        int sideX = 1;
        int sideY = 1;
        if (mirror && mirrorable != null)
        {
            if (mirrorable.is(Mirror.HORIZONTAL))
            {
                sideX = -1;
            }
            if (mirrorable.is(Mirror.VERTICAL))
            {
                sideY = -1;
            }
            launchable.getFeature(Mirrorable.class).mirror(mirrorable.getMirror());
        }
        final double x = localizable.getX() + (config.getOffsetX() + offsetX) * sideX;
        final double y = localizable.getY() + (config.getOffsetY() + offsetY) * sideY;
        launchable.setLocation(x, y);

        final Force vector = new Force(config.getVector());
        vector.addDirection(1.0, initial);

        final Force v = computeVector(vector);
        if (!v.isZero())
        {
            launchable.setVector(v);
        }
        launchable.launch();

        config.getSfx().ifPresent(this::audioCacheAndPlay);

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
        int sideX = 1;
        int sideY = 1;
        if (mirror && mirrorable != null)
        {
            if (mirrorable.is(Mirror.HORIZONTAL))
            {
                sideX = -1;
            }
            else if (mirrorable.is(Mirror.VERTICAL))
            {
                sideY = -1;
            }
        }
        vector.setDestination(vector.getDirectionHorizontal() * sideX, vector.getDirectionVertical() * sideY);
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

    /**
     * Cache audio if new and play it.
     * 
     * @param sfx The audio to play.
     */
    private void audioCacheAndPlay(String sfx)
    {
        Audio sound = audio.get(sfx);
        if (sound == null)
        {
            sound = AudioFactory.loadAudio(Medias.create(sfx));
            audio.put(sfx, sound);
        }
        sound.play();
    }

    /*
     * Launcher
     */

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        localizable = provider.getFeature(Transformable.class);
        if (mirror && provider.hasFeature(Mirrorable.class))
        {
            mirrorable = provider.getFeature(Mirrorable.class);
        }

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
        listenable.addListener(listener);
    }

    @Override
    public void removeListener(LauncherListener listener)
    {
        listenable.removeListener(listener);
    }

    @Override
    public void addListener(LaunchableListener listener)
    {
        listenersLaunchable.add(listener);
    }

    @Override
    public boolean fire()
    {
        return fire(DirectionNone.INSTANCE, null);
    }

    @Override
    public boolean fire(Direction initial)
    {
        return fire(initial, null);
    }

    @Override
    public boolean fire(Localizable target)
    {
        return fire(DirectionNone.INSTANCE, target);
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
        fire.update(extrp);
        for (final DelayedLaunch launch : delayed)
        {
            if (launch.isReady())
            {
                launch(launch.getConfig(), launch.getInitial(), launch.getFeaturable(), launch.getLaunchable());
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

    @Override
    public void recycle()
    {
        fire.restart();
    }
}
