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

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertHashEquals;
import static com.b3dgs.lionengine.UtilAssert.assertHashNotEquals;
import static com.b3dgs.lionengine.UtilAssert.assertNotEquals;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.game.Force;

/**
 * Test {@link LauncherConfig}.
 */
public final class LauncherConfigTest
{
    /**
     * Prepare test.
     */
    @BeforeAll
    public static void beforeTests()
    {
        Medias.setResourcesDirectory(System.getProperty("java.io.tmpdir"));
    }

    /**
     * Clean up test.
     */
    @AfterAll
    public static void afterTests()
    {
        Medias.setResourcesDirectory(null);
    }

    /**
     * Test data.
     */
    @Test
    public void testData()
    {
        final LaunchableConfig launchable = new LaunchableConfig("media", 10, 1, 2, new Force(1.0, 2.0));
        final LauncherConfig launcher = new LauncherConfig(1, 2, Arrays.asList(launchable));

        assertEquals("media", launchable.getMedia());
        assertEquals(10, launchable.getDelay());
        assertEquals(1, launchable.getOffsetX());
        assertEquals(2, launchable.getOffsetY());
        assertEquals(new Force(1.0, 2.0), launchable.getVector());

        assertEquals(1, launcher.getLevel());
        assertEquals(2, launcher.getRate());
    }

    /**
     * Test exports imports.
     */
    @Test
    public void testExportsImports()
    {
        final LaunchableConfig launchable = new LaunchableConfig("media", 10, 1, 2, new Force(1.0, 2.0));
        final LauncherConfig launcher = new LauncherConfig(10, 10, Arrays.asList(launchable));

        final Xml root = new Xml("test");
        root.add(LauncherConfig.exports(launcher));

        final Media media = Medias.create("launcher.xml");
        root.save(media);

        assertEquals(launcher, LauncherConfig.imports(new Xml(media).getChild(LauncherConfig.NODE_LAUNCHER)));
        assertEquals(Arrays.asList(launcher), LauncherConfig.imports(new Configurer(media)));

        assertTrue(media.getFile().delete());
    }

    /**
     * Test equals.
     */
    @Test
    public void testEquals()
    {
        final LaunchableConfig launchable = new LaunchableConfig("media", 10, 1, 2, new Force(1.0, 2.0));
        final LauncherConfig launcher = new LauncherConfig(0, 1, Arrays.asList(launchable));

        assertEquals(launcher, launcher);

        assertNotEquals(launcher, null);
        assertNotEquals(launcher, new Object());
        assertNotEquals(launcher, new LauncherConfig(0, 0, Arrays.asList(launchable)));
        assertNotEquals(launcher, new LauncherConfig(0, 1, new ArrayList<LaunchableConfig>()));
        assertNotEquals(launcher, new LauncherConfig(1, 0, Arrays.asList(launchable)));
    }

    /**
     * Test hash code.
     */
    @Test
    public void testHashCode()
    {
        final LaunchableConfig launchable = new LaunchableConfig("media", 10, 1, 2, new Force(1.0, 2.0));
        final LauncherConfig launcher = new LauncherConfig(0, 1, Arrays.asList(launchable));

        assertHashEquals(launcher, launcher);
        assertHashEquals(launcher, new LauncherConfig(0, 1, Arrays.asList(launchable)));

        assertHashNotEquals(launcher, new LauncherConfig(0, 0, Arrays.asList(launchable)));
        assertHashNotEquals(launcher, new LauncherConfig(0, 1, new ArrayList<LaunchableConfig>()));
        assertHashNotEquals(launcher, new LauncherConfig(1, 0, Arrays.asList(launchable)));
    }

    /**
     * Test to string.
     */
    @Test
    public void testToString()
    {
        final LaunchableConfig launchable = new LaunchableConfig("media", 10, 1, 2, new Force(1.0, 2.0));
        final LauncherConfig launcher = new LauncherConfig(0, 1, Arrays.asList(launchable, launchable));

        assertEquals("LauncherConfig [level=0, rate=1, launchables="
                     + System.lineSeparator()
                     + Constant.TAB
                     + "LaunchableConfig [media=media, delay=10, ox=1, oy=2, vector="
                     + "Force [fh=1.0, fv=2.0, velocity=0.0, sensibility=0.0]]"
                     + System.lineSeparator()
                     + Constant.TAB
                     + "LaunchableConfig [media=media, delay=10, ox=1, oy=2, vector="
                     + "Force [fh=1.0, fv=2.0, velocity=0.0, sensibility=0.0]]]",
                     launcher.toString());
    }
}
