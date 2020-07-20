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
package com.b3dgs.lionengine.game.feature.attackable;

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
 * Test {@link AttackerConfig}.
 */
final class AttackerConfigTest
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
        final AttackerConfig config = new AttackerConfig(1, 2, 3, 4, 5);

        final Xml root = new Xml("test");
        root.add(AttackerConfig.exports(config));

        final Media media = Medias.create("Object.xml");
        root.save(media);

        assertEquals(config, AttackerConfig.imports(new Xml(media)));
        assertEquals(config, AttackerConfig.imports(new Configurer(media)));

        assertTrue(media.getFile().delete());
    }

    /**
     * Test equals.
     */
    @Test
    void testEquals()
    {
        final AttackerConfig config = new AttackerConfig(1, 2, 3, 4, 5);

        assertEquals(config, config);
        assertEquals(config, new AttackerConfig(1, 2, 3, 4, 5));

        assertNotEquals(config, null);
        assertNotEquals(config, new Object());
        assertNotEquals(config, new AttackerConfig(0, 2, 3, 4, 5));
        assertNotEquals(config, new AttackerConfig(1, 0, 3, 4, 5));
        assertNotEquals(config, new AttackerConfig(1, 2, 0, 4, 5));
        assertNotEquals(config, new AttackerConfig(1, 2, 3, 0, 5));
        assertNotEquals(config, new AttackerConfig(1, 2, 3, 4, 0));
    }

    /**
     * Test hash code.
     */
    @Test
    void testHashCode()
    {
        final AttackerConfig hash = new AttackerConfig(1, 2, 3, 4, 5);

        assertHashEquals(hash, new AttackerConfig(1, 2, 3, 4, 5));

        assertHashNotEquals(hash, new AttackerConfig(0, 2, 3, 4, 5));
        assertHashNotEquals(hash, new AttackerConfig(1, 0, 3, 4, 5));
        assertHashNotEquals(hash, new AttackerConfig(1, 2, 0, 4, 5));
        assertHashNotEquals(hash, new AttackerConfig(1, 2, 3, 0, 5));
        assertHashNotEquals(hash, new AttackerConfig(1, 2, 3, 4, 0));
    }

    /**
     * Test to string.
     */
    @Test
    void testToString()
    {
        final AttackerConfig config = new AttackerConfig(1, 2, 3, 4, 5);

        assertEquals("AttackerConfig [delay=1, distanceMin=2, distanceMax=3, damagesMin=4, damagesMax=5]",
                     config.toString());
    }
}
