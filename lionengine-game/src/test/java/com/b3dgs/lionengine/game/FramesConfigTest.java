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
package com.b3dgs.lionengine.game;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertHashEquals;
import static com.b3dgs.lionengine.UtilAssert.assertHashNotEquals;
import static com.b3dgs.lionengine.UtilAssert.assertNotEquals;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.game.feature.Setup;

/**
 * Test {@link FramesConfig}.
 */
final class FramesConfigTest
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
     * Test with no node.
     */
    @Test
    void testNoNode()
    {
        final Xml root = new Xml("test");

        assertEquals(new FramesConfig(1, 1, 0, 0), FramesConfig.imports(root));
    }

    /**
     * Test exports imports.
     */
    @Test
    void testExportsImports()
    {
        final FramesConfig config = new FramesConfig(1, 2, 3, 4);

        final Xml root = new Xml("test");
        root.add(FramesConfig.exports(config));

        final Media media = Medias.create("Object.xml");
        root.save(media);

        assertEquals(config, FramesConfig.imports(new Xml(media)));
        assertEquals(config, FramesConfig.imports(new Setup(media)));
        assertEquals(config, FramesConfig.imports(new Configurer(media)));

        assertTrue(media.getFile().delete());
    }

    /**
     * Test equals.
     */
    @Test
    void testEquals()
    {
        final FramesConfig config = new FramesConfig(1, 2, 3, 4);

        assertEquals(config, config);
        assertEquals(config, new FramesConfig(1, 2, 3, 4));

        assertNotEquals(config, null);
        assertNotEquals(config, new Object());
        assertNotEquals(config, new FramesConfig(0, 2, 3, 4));
        assertNotEquals(config, new FramesConfig(1, 0, 3, 4));
        assertNotEquals(config, new FramesConfig(1, 2, 0, 4));
        assertNotEquals(config, new FramesConfig(1, 2, 3, 0));
    }

    /**
     * Test hash code.
     */
    @Test
    void testHashCode()
    {
        final FramesConfig hash = new FramesConfig(1, 2, 3, 4);

        assertHashEquals(hash, new FramesConfig(1, 2, 3, 4));

        assertHashNotEquals(hash, new FramesConfig(0, 2, 3, 4));
        assertHashNotEquals(hash, new FramesConfig(1, 0, 3, 4));
        assertHashNotEquals(hash, new FramesConfig(1, 2, 0, 4));
        assertHashNotEquals(hash, new FramesConfig(1, 2, 3, 0));
    }

    /**
     * Test to string.
     */
    @Test
    void testToString()
    {
        final FramesConfig config = new FramesConfig(1, 2, 3, 4);

        assertEquals("FramesConfig[horizontalFrames=1, verticalFrames=2, offsetX=3, offsetY=4]", config.toString());
    }
}
