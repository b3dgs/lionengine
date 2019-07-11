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
 * Test {@link ExtractorConfig}.
 */
public final class ExtractorConfigTest
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
        final ExtractorConfig config = new ExtractorConfig(1.0, 2.0, 3);

        final Xml root = new Xml("test");
        root.add(ExtractorConfig.exports(config));

        final Media media = Medias.create("object.xml");
        root.save(media);

        assertEquals(config, ExtractorConfig.imports(new Xml(media)));
        assertEquals(config, ExtractorConfig.imports(new Configurer(media)));

        assertTrue(media.getFile().delete());
    }

    /**
     * Test equals.
     */
    @Test
    public void testEquals()
    {
        final ExtractorConfig config = new ExtractorConfig(1.0, 2.0, 3);

        assertEquals(config, config);
        assertEquals(config, new ExtractorConfig(1.0, 2.0, 3));

        assertNotEquals(config, null);
        assertNotEquals(config, new Object());
        assertNotEquals(config, new ExtractorConfig(0.0, 2.0, 3));
        assertNotEquals(config, new ExtractorConfig(1.0, 0.0, 3));
        assertNotEquals(config, new ExtractorConfig(1.0, 2.0, 0));
    }

    /**
     * Test hash code.
     */
    @Test
    public void testHashCode()
    {
        final ExtractorConfig hash = new ExtractorConfig(1.0, 2.0, 3);

        assertHashEquals(hash, new ExtractorConfig(1.0, 2.0, 3));

        assertHashNotEquals(hash, new ExtractorConfig(0.0, 2.0, 3));
        assertHashNotEquals(hash, new ExtractorConfig(1.0, 0.0, 3));
        assertHashNotEquals(hash, new ExtractorConfig(1.0, 2.0, 0));
    }

    /**
     * Test to string.
     */
    @Test
    public void testToString()
    {
        final ExtractorConfig config = new ExtractorConfig(1.0, 2.0, 3);

        assertEquals("ExtractorConfig [extract=1.0, dropoff=2.0, capacity=3]", config.toString());
    }
}
