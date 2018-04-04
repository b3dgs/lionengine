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

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import com.b3dgs.lionengine.Localizable;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.feature.Factory;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.Handler;
import com.b3dgs.lionengine.game.feature.IdentifiableModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.TransformableModel;

/**
 * Utilities dedicated to launchable test.
 */
public class UtilLaunchable
{
    /**
     * Create launchable.
     * 
     * @param services The services.
     * @param featurable The featurable.
     * @return The launchable.
     */
    public static Launchable createLaunchable(Services services, Featurable featurable)
    {
        featurable.addFeature(new IdentifiableModel());
        featurable.addFeature(new TransformableModel());

        final Launchable launchable = new LaunchableModel();
        launchable.prepare(featurable);
        launchable.setLocation(0.0, 0.0);
        launchable.setVector(new Force(0.0, 1.0));

        return launchable;
    }

    /**
     * Create the media.
     * 
     * @param launchableMedia The launchable.
     * @return The media.
     */
    public static Media createLauncherMedia(Media launchableMedia)
    {
        return createLauncherMedia(launchableMedia, 0);
    }

    /**
     * Create the media.
     * 
     * @param launchableMedia The launchable.
     * @param delay The desired delay.
     * @return The media.
     */
    public static Media createLauncherMedia(Media launchableMedia, int delay)
    {
        final Media media = Medias.create("launcher.xml");
        final LaunchableConfig launchableConfig = new LaunchableConfig(launchableMedia.getPath(),
                                                                       delay,
                                                                       1,
                                                                       2,
                                                                       new Force(1.0, 2.0));
        final LauncherConfig launcherConfig = new LauncherConfig(0, 10, Arrays.asList(launchableConfig));

        final Xml root = new Xml("test");
        root.add(LauncherConfig.exports(launcherConfig));
        root.add(LauncherConfig.exports(new LauncherConfig(1, 50, Arrays.asList(launchableConfig))));
        root.save(media);

        return media;
    }

    /**
     * Create launcher.
     * 
     * @param services The services.
     * @param setup The setup.
     * @param featurable The featurable.
     * @return The extractable.
     */
    public static Launcher createLauncher(Services services, Setup setup, Featurable featurable)
    {
        services.add(new Factory(services));
        services.add(new Handler(services));
        featurable.addFeature(new TransformableModel());

        final Launcher launcher = new LauncherModel(services, setup);
        launcher.prepare(featurable);
        launcher.setOffset(1, 2);
        launcher.setRate(10);

        return launcher;
    }

    /**
     * Create a localizable.
     * 
     * @return Localizable.
     */
    public static Localizable createLocalizable()
    {
        return new Localizable()
        {
            @Override
            public double getY()
            {
                return 0;
            }

            @Override
            public double getX()
            {
                return 0;
            }
        };
    }

    /**
     * Create a listener.
     * 
     * @param fired The fired flag.
     * @return The listener.
     */
    public static LauncherListener createListener(final AtomicBoolean fired)
    {
        return () -> fired.set(true);
    }

    /**
     * Create a listener.
     * 
     * @param firedLaunchable The fired launchable flag.
     * @return The listener.
     */
    public static LaunchableListener createListener(final AtomicReference<Launchable> firedLaunchable)
    {
        return launchable -> firedLaunchable.set(launchable);
    }
}
