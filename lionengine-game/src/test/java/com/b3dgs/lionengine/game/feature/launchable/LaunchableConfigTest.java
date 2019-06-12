/*
 * Copyright (C) 2013-2019 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertHashEquals;
import static com.b3dgs.lionengine.UtilAssert.assertHashNotEquals;
import static com.b3dgs.lionengine.UtilAssert.assertNotEquals;
import static com.b3dgs.lionengine.UtilAssert.assertThrows;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.game.Force;

/**
 * Test {@link LaunchableConfig}.
 */
public final class LaunchableConfigTest
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
     * Test exports imports.
     */
    @Test
    public void testExportsImports()
    {
        final LaunchableConfig launchable = new LaunchableConfig("media", 10, 1, 2, new Force(1.0, 2.0));

        final Xml root = new Xml("test");
        root.add(LaunchableConfig.exports(launchable));

        final Media media = Medias.create("launchable.xml");
        root.save(media);

        assertEquals(launchable, LaunchableConfig.imports(new Xml(media).getChild(LaunchableConfig.NODE_LAUNCHABLE)));

        assertTrue(media.getFile().delete());
    }

    /**
     * Test <code>null</code> media.
     */
    @Test
    public void testNullMedia()
    {
        assertThrows(() -> new LaunchableConfig(null, 0, 1, 2, new Force()), "Unexpected null argument !");
    }

    /**
     * Test <code>null</code> force.
     */
    @Test
    public void testNullForce()
    {
        assertThrows(() -> new LaunchableConfig("media", 0, 1, 2, null), "Unexpected null argument !");
    }

    /**
     * Test equals.
     */
    @Test
    public void testEquals()
    {
        final LaunchableConfig launchable = new LaunchableConfig("media", 10, 1, 2, new Force(1.0, 2.0));

        assertEquals(launchable, launchable);

        assertNotEquals(launchable, null);
        assertNotEquals(launchable, new Object());
        assertNotEquals(launchable, new LaunchableConfig("", 10, 1, 2, new Force(1.0, 2.0)));
        assertNotEquals(launchable, new LaunchableConfig("media", 0, 1, 2, new Force(1.0, 2.0)));
        assertNotEquals(launchable, new LaunchableConfig("media", 10, 1, 2, new Force(2.0, 1.0)));
        assertNotEquals(launchable, new LaunchableConfig("media", 10, 0, 2, new Force(1.0, 2.0)));
        assertNotEquals(launchable, new LaunchableConfig("media", 10, 1, 0, new Force(1.0, 2.0)));
    }

    /**
     * Test hash code.
     */
    @Test
    public void testHashCode()
    {
        final LaunchableConfig launchable = new LaunchableConfig("media", 10, 1, 2, new Force(1.0, 2.0));

        assertHashEquals(launchable, launchable);
        assertHashEquals(launchable, new LaunchableConfig("media", 10, 1, 2, new Force(1.0, 2.0)));

        assertHashNotEquals(launchable, new LaunchableConfig("", 10, 1, 2, new Force(1.0, 2.0)));
        assertHashNotEquals(launchable, new LaunchableConfig("media", 0, 1, 2, new Force(1.0, 2.0)));
        assertHashNotEquals(launchable, new LaunchableConfig("media", 10, 1, 2, new Force(2.0, 1.0)));
        assertHashNotEquals(launchable, new LaunchableConfig("media", 10, 0, 2, new Force(1.0, 2.0)));
        assertHashNotEquals(launchable, new LaunchableConfig("media", 10, 1, 0, new Force(1.0, 2.0)));
    }

    /**
     * Test to string.
     */
    @Test
    public void testToString()
    {
        final LaunchableConfig launchable = new LaunchableConfig("media", 10, 1, 2, new Force(1.0, 2.0));

        assertEquals("LaunchableConfig [media=media, delay=10, ox=1, oy=2, vector="
                     + "Force [fh=1.0, fv=2.0, velocity=0.0, sensibility=0.0]]",
                     launchable.toString());
    }
}
