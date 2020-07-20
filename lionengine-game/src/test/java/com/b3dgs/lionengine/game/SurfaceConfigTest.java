/*
 * Copyright (C) 2013-2020 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertFalse;
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

/**
 * Test {@link SurfaceConfig}.
 */
final class SurfaceConfigTest
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
     * Test exports imports with image and icon.
     */
    @Test
    void testExportsImports()
    {
        final Xml root = new Xml("test");

        final SurfaceConfig config = new SurfaceConfig("image", "icon");
        root.add(SurfaceConfig.exports(config));

        final Media media = Medias.create("Object.xml");
        root.save(media);

        assertEquals(config, SurfaceConfig.imports(new Xml(media)));
        assertEquals(config, SurfaceConfig.imports(new Configurer(media)));

        assertTrue(media.getFile().delete());
    }

    /**
     * Test exports imports with image without icon.
     */
    @Test
    void testExportsImportsNoIcon()
    {
        final Xml root = new Xml("test");

        final SurfaceConfig config = new SurfaceConfig("image", null);
        root.add(SurfaceConfig.exports(config));

        final Media media = Medias.create("Object.xml");
        root.save(media);

        assertEquals(config, SurfaceConfig.imports(new Xml(media)));
        assertEquals(config, SurfaceConfig.imports(new Configurer(media)));

        assertTrue(media.getFile().delete());
    }

    /**
     * Test <code>null</code> image.
     */
    @Test
    void testNullImage()
    {
        assertThrows(() -> new SurfaceConfig(null, "icon"), "Unexpected null argument !");
    }

    /**
     * Test with <code>null</code> icon.
     */
    @Test
    void testConfigNullIcon()
    {
        final SurfaceConfig config = new SurfaceConfig("image", null);

        assertEquals("image", config.getImage());
        assertFalse(config.getIcon().isPresent());
    }

    /**
     * Test equals.
     */
    @Test
    void testEquals()
    {
        final SurfaceConfig config = new SurfaceConfig("image", "icon");

        assertEquals(config, config);
        assertEquals(config, new SurfaceConfig("image", "icon"));
        assertEquals(new SurfaceConfig("image", null), new SurfaceConfig("image", null));

        assertNotEquals(config, null);
        assertNotEquals(config, new Object());
        assertNotEquals(config, new SurfaceConfig("", "icon"));
        assertNotEquals(config, new SurfaceConfig("image", ""));
        assertNotEquals(new SurfaceConfig("image", "icon"), new SurfaceConfig("image", null));
        assertNotEquals(new SurfaceConfig("image", null), new SurfaceConfig("image", "icon"));
    }

    /**
     * Test hash code.
     */
    @Test
    void testHashCode()
    {
        final SurfaceConfig hash = new SurfaceConfig("image", "icon");

        assertHashEquals(hash, new SurfaceConfig("image", "icon"));
        assertHashEquals(new SurfaceConfig("image", null), new SurfaceConfig("image", null));

        assertHashNotEquals(hash, new Object());
        assertHashNotEquals(hash, new SurfaceConfig("", "icon"));
        assertHashNotEquals(hash, new SurfaceConfig("image", ""));
        assertHashNotEquals(new SurfaceConfig("image", "icon"), new SurfaceConfig("image", null));
        assertHashNotEquals(new SurfaceConfig("image", null), new SurfaceConfig("image", "icon"));
    }

    /**
     * Test to string.
     */
    @Test
    void testToString()
    {
        final SurfaceConfig config = new SurfaceConfig("image", "icon");

        assertEquals("SurfaceConfig [image=image, icon=icon]", config.toString());
    }
}
