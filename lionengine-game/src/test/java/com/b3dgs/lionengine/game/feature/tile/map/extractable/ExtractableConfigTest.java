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
package com.b3dgs.lionengine.game.feature.tile.map.extractable;

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
import com.b3dgs.lionengine.game.Configurer;

/**
 * Test {@link ExtractableConfig}.
 */
final class ExtractableConfigTest
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
    void testExportsImports()
    {
        final ExtractableConfig config = new ExtractableConfig("gold", 1);

        final Xml root = new Xml("test");
        root.add(ExtractableConfig.exports(config));

        final Media media = Medias.create("Object.xml");
        root.save(media);

        assertEquals(config, ExtractableConfig.imports(new Xml(media)));
        assertEquals(config, ExtractableConfig.imports(new Configurer(media)));

        assertTrue(media.getFile().delete());
    }

    /**
     * Test equals.
     */
    @Test
    void testEquals()
    {
        final ExtractableConfig config = new ExtractableConfig("gold", 1);

        assertEquals(config, config);
        assertEquals(config, new ExtractableConfig("gold", 1));

        assertNotEquals(config, null);
        assertNotEquals(config, new Object());
        assertNotEquals(config, new ExtractableConfig("wood", 1));
        assertNotEquals(config, new ExtractableConfig("gold", 0));
    }

    /**
     * Test hash code.
     */
    @Test
    void testHashCode()
    {
        final ExtractableConfig hash = new ExtractableConfig("gold", 1);

        assertHashEquals(hash, new ExtractableConfig("gold", 1));

        assertHashNotEquals(hash, new ExtractableConfig("wood", 1));
        assertHashNotEquals(hash, new ExtractableConfig("gold", 0));
    }

    /**
     * Test to string.
     */
    @Test
    void testToString()
    {
        final ExtractableConfig config = new ExtractableConfig("gold", 1);

        assertEquals("ExtractableConfig [type=gold quantity=1]", config.toString());
    }
}
