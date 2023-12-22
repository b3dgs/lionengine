/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import java.util.function.Consumer;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.ListenableModel;
import com.b3dgs.lionengine.Localizable;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.Viewer;
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
import com.b3dgs.lionengine.graphic.engine.SourceResolutionProvider;

/**
 * Launcher model implementation.
 */
// CHECKSTYLE IGNORE LINE: FanOutComplexity
public class LauncherModel extends FeatureModel implements Launcher, Recyclable
{
    /** Launcher listeners. */
    private final ListenableModel<LauncherListener> listenable = new ListenableModel<>();
    /** Launchable listeners. */
    private final Collection<LaunchableListener> listenersLaunchable = new HashSet<>();
    /** Delayed launches. */
    private final List<DelayedLaunch> delayed = new ArrayList<>();
    /** Delayed launches launched. */
    private final List<DelayedLaunch> launched = new ArrayList<>();
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
    /** Viewer reference. */
    private final Viewer viewer;
    /** Source reference. */
    private final SourceResolutionProvider source;
    /** Audio player routine. */
    private final Consumer<String> audioPlayer;
    /** Launchable configuration. */
    private Iterable<LaunchableConfig> launchables;
    /** Transformable model. */
    private Transformable transformable;
    /** Target reference. */
    private Localizable target;
    /** Mirrorable reference. */
    private Mirrorable mirrorable;
    /** Extrapolate flag. */
    private boolean extrapolate;
    /** Current level. */
    private int level;
    /** Fire delay in milli seconds. */
    private int delay;
    /** Mirror flag. */
    private boolean mirror;
    /** Centered flag. */
    private boolean centered;
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
     * <li>{@link SourceResolutionProvider}</li>
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
        viewer = services.getOptional(Viewer.class).orElse(null);
        source = services.get(SourceResolutionProvider.class);

        if (viewer == null)
        {
            audioPlayer = this::audioCacheAndPlay;
        }
        else
        {
            audioPlayer = this::audioCacheAndPlayIfViewable;
        }

        config = LauncherConfig.imports(setup);
        if (config.isEmpty())
        {
            launchables = Collections.emptyList();
            delay = 0;
        }
        else
        {
            launchables = config.get(0).getLaunchables();
            delay = config.get(0).getDelay();
            mirror = config.get(0).hasMirrorable();
            centered = config.get(0).isCentered();
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
                    delayed.add(new DelayedLaunch(source, launchableConfig, initial, featurable, launchable));
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
        final double x = transformable.getX()
                         + (centered ? transformable.getWidth() / 2.0 : 0.0)
                         + (config.getOffsetX() + offsetX) * sideX;
        final double y = transformable.getY()
                         + (centered ? transformable.getHeight() / 2.0 : 0.0)
                         + (config.getOffsetY() + offsetY) * sideY;
        launchable.setLocation(x, y);

        final Force vector = new Force(config.getVector());
        vector.addDirection(1.0, initial);

        final Force v = computeVector(vector);
        if (!v.isZero())
        {
            launchable.setVector(v);
        }
        launchable.launch();

        config.getSfx().ifPresent(audioPlayer);

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
        final double sx = transformable.getX() + transformable.getWidth() / 2.0;
        final double sy = transformable.getY() + transformable.getHeight() / 2.0;

        double dx = target.getX();
        double dy = target.getY();

        if (target instanceof final Transformable targetTransformable)
        {
            if (extrapolate)
            {
                final double ray = UtilMath.getDistance(transformable.getX(),
                                                        transformable.getY(),
                                                        target.getX(),
                                                        target.getY());
                dx += (int) ((target.getX() - targetTransformable.getOldX()) / vector.getDirectionHorizontal() * ray);
                dy += (int) ((target.getY() - targetTransformable.getOldY()) / vector.getDirectionVertical() * ray);
            }
            dx += targetTransformable.getWidth() / 2.0;
            dy += targetTransformable.getHeight() / 2.0;
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

    /**
     * Cache audio if new and play it if viewable.
     * 
     * @param sfx The audio to play.
     */
    private void audioCacheAndPlayIfViewable(String sfx)
    {
        if (viewer.isViewable(transformable, transformable.getWidth() / 2, transformable.getHeight()))
        {
            audioCacheAndPlay(sfx);
        }
    }

    /*
     * Launcher
     */

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        transformable = provider.getFeature(Transformable.class);
        if (mirror && provider.hasFeature(Mirrorable.class))
        {
            mirrorable = provider.getFeature(Mirrorable.class);
        }

        if (provider instanceof final LauncherListener l)
        {
            addListener(l);
        }
        if (provider instanceof final LaunchableListener l)
        {
            addListener(l);
        }
    }

    @Override
    public void checkListener(Object listener)
    {
        super.checkListener(listener);

        if (listener instanceof final LauncherListener l)
        {
            addListener(l);
        }
        if (listener instanceof final LaunchableListener l)
        {
            addListener(l);
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
        if (fire.elapsedTime(source.getRate(), delay))
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
        final int n = delayed.size();
        for (int i = 0; i < n; i++)
        {
            final DelayedLaunch launch = delayed.get(i);

            launch.update(extrp);
            if (launch.isReady())
            {
                launch(launch.getConfig(), launch.getInitial(), launch.getFeaturable(), launch.getLaunchable());
                launched.add(launch);
            }
        }
        if (!launched.isEmpty())
        {
            delayed.removeAll(launched);
            launched.clear();
        }
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
        delay = config.get(level).getDelay();
        mirror = config.get(level).hasMirrorable();
        centered = config.get(level).isCentered();
    }

    @Override
    public void setDelay(int delay)
    {
        this.delay = delay;
    }

    @Override
    public void setExtrapolate(boolean extrapolate)
    {
        this.extrapolate = extrapolate;
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
        return delay;
    }

    @Override
    public void recycle()
    {
        fire.restart();
        extrapolate = false;
    }
}
