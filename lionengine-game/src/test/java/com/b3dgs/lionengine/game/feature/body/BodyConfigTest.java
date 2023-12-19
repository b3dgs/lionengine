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
package com.b3dgs.lionengine.game.feature.body;

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
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;

/**
 * Test {@link BodyConfig}.
 */
final class BodyConfigTest
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
        final Media media = Medias.create("Object.xml");
        final Xml root = new Xml("test");
        final Xml node = root.createChild(BodyConfig.NODE_BODY);
        node.writeDouble(BodyConfig.ATT_GRAVITY, 1.0);
        node.writeDouble(BodyConfig.ATT_GRAVITY_MAX, 2.0);
        root.save(media);

        final Body body = new BodyModel(new Services(), new Setup(media));
        BodyConfig.exports(root, body);

        final BodyConfig config = BodyConfig.imports(new Setup(media));

        assertEquals(1.0, config.getGravity());
        assertEquals(2.0, config.getGravityMax());
        assertTrue(media.getFile().delete());
    }

    /**
     * Test equals.
     */
    @Test
    void testEquals()
    {
        final BodyConfig config = new BodyConfig(1.0, 2.0);

        assertEquals(config, config);
        assertEquals(config, new BodyConfig(1.0, 2.0));

        assertNotEquals(config, null);
        assertNotEquals(config, new Object());
        assertNotEquals(config, new BodyConfig(2.0, 2.0));
        assertNotEquals(config, new BodyConfig(1.0, 1.0));
    }

    /**
     * Test hash code.
     */
    @Test
    void testHashCode()
    {
        final BodyConfig config = new BodyConfig(1.0, 2.0);

        assertHashEquals(config, config);
        assertHashEquals(config, new BodyConfig(1.0, 2.0));

        assertHashNotEquals(config, new Object());
        assertHashNotEquals(config, new BodyConfig(2.0, 2.0));
        assertHashNotEquals(config, new BodyConfig(2.0, 1.0));
    }

    /**
     * Test to string.
     */
    @Test
    void testToString()
    {
        final BodyConfig config = new BodyConfig(1.0, 2.0);

        assertEquals("BodyConfig[gravity=1.0, gravityMax=2.0]", config.toString());
    }
}
