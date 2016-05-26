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

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.stream.Xml;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Test the launcher config class.
 */
public class LauncherConfigTest
{
    /**
     * Prepare test.
     */
    @BeforeClass
    public static void setUp()
    {
        Medias.setResourcesDirectory(System.getProperty("java.io.tmpdir"));
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        Medias.setResourcesDirectory(Constant.EMPTY_STRING);
    }

    /**
     * Test the launchable configuration.
     */
    @Test
    public void testConfig()
    {
        final Media media = Medias.create("launcher.xml");
        final LaunchableConfig launchable = new LaunchableConfig("media", 10, new Force(1.0, 2.0));
        final LauncherConfig launcher = new LauncherConfig(10, Arrays.asList(launchable));
        try
        {
            final XmlNode root = Xml.create("test");
            root.add(LauncherConfig.exports(launcher));
            Xml.save(root, media);

            final LauncherConfig loaded = LauncherConfig.imports(Xml.load(media)
                                                                    .getChild(LauncherConfig.NODE_LAUNCHER));
            Assert.assertEquals(launcher, loaded);
        }
        finally
        {
            Assert.assertTrue(media.getFile().delete());
        }
    }

    /**
     * Test the launcher hash code.
     */
    @Test
    public void testHashCode()
    {
        final LaunchableConfig launchable = new LaunchableConfig("media", 10, new Force(1.0, 2.0));
        final int launcher = new LauncherConfig(1, Arrays.asList(launchable)).hashCode();

        Assert.assertEquals(launcher, launcher);
        Assert.assertEquals(launcher, new LauncherConfig(1, Arrays.asList(launchable)).hashCode());
        Assert.assertNotEquals(launcher, new LauncherConfig(0, Arrays.asList(launchable)).hashCode());
        Assert.assertNotEquals(launcher, new LauncherConfig(1, new ArrayList<LaunchableConfig>()).hashCode());
    }

    /**
     * Test the launcher equality.
     */
    @Test
    public void testEquals()
    {
        final LaunchableConfig launchable = new LaunchableConfig("media", 10, new Force(1.0, 2.0));
        final LauncherConfig launcher = new LauncherConfig(1, Arrays.asList(launchable));

        Assert.assertEquals(launcher, launcher);
        Assert.assertNotEquals(launcher, null);
        Assert.assertNotEquals(launcher, new Object());
        Assert.assertNotEquals(launcher, new LauncherConfig(0, Arrays.asList(launchable)));
        Assert.assertNotEquals(launcher, new LauncherConfig(1, new ArrayList<LaunchableConfig>()));
    }
}
