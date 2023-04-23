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
package com.b3dgs.lionengine.game.feature.collidable;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertFalse;
import static com.b3dgs.lionengine.UtilAssert.assertThrows;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import java.util.HashMap;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.game.feature.FeaturableConfig;

/**
 * Test {@link CollisionConfig}.
 */
final class CollisionConfigTest
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
     * Test no node.
     */
    @Test
    void testNoNode()
    {
        final Xml root = new Xml(Constant.XML_PREFIX + FeaturableConfig.NODE_FEATURABLE);

        assertTrue(CollisionConfig.imports(root).getCollisions().isEmpty());
    }

    /**
     * Test exports imports.
     */
    @Test
    void testExportsImports()
    {
        final Xml root = new Xml(Constant.XML_PREFIX + FeaturableConfig.NODE_FEATURABLE);
        final Collision collision = new Collision("test", 0, 1, 2, 3, true);
        CollisionConfig.exports(root, collision);

        final Media media = Medias.create("collision.xml");
        root.save(media);

        final CollisionConfig config = CollisionConfig.imports(new Configurer(media));
        final Collision imported = config.getCollision("test");

        assertEquals("test", imported.getName());
        assertEquals(0, imported.getOffsetX());
        assertEquals(1, imported.getOffsetY());
        assertEquals(2, imported.getWidth());
        assertEquals(3, imported.getHeight());
        assertTrue(imported.hasMirror());

        assertFalse(config.getCollisions().isEmpty());
        assertEquals(collision, config.getCollision("test"));

        assertTrue(media.getFile().delete());
    }

    /**
     * Test get unknown collision.
     */
    @Test
    void testGetUnknownCollision()
    {
        final CollisionConfig config = new CollisionConfig(new HashMap<>());

        assertThrows(() -> config.getCollision("void"), CollisionConfig.ERROR_COLLISION_NOT_FOUND + "void");
    }
}
