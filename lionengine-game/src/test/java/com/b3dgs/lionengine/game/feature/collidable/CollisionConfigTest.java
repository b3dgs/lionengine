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
package com.b3dgs.lionengine.game.feature.collidable;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.io.Xml;

/**
 * Test the collision configuration class.
 */
public class CollisionConfigTest
{
    /** Test configuration. */
    private static Media media;

    /**
     * Prepare test.
     */
    @BeforeClass
    public static void setUp()
    {
        Medias.setResourcesDirectory(System.getProperty("java.io.tmpdir"));
        media = Medias.create("collision.xml");
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        Assert.assertTrue(media.getFile().delete());
        Medias.setResourcesDirectory(null);
    }

    /**
     * Test the configuration.
     */
    @Test
    public void testConfiguration()
    {
        final Xml root = new Xml("collision");
        final Collision collision = new Collision("test", 0, 1, 2, 3, true);
        CollisionConfig.exports(root, collision);
        root.save(media);

        final CollisionConfig config = CollisionConfig.imports(new Configurer(media));
        final Collision imported = config.getCollision("test");

        Assert.assertEquals("test", imported.getName());
        Assert.assertEquals(0, imported.getOffsetX());
        Assert.assertEquals(1, imported.getOffsetY());
        Assert.assertEquals(2, imported.getWidth());
        Assert.assertEquals(3, imported.getHeight());
        Assert.assertTrue(imported.hasMirror());

        Assert.assertFalse(config.getCollisions().isEmpty());
        Assert.assertEquals(collision, config.getCollision("test"));
    }
}
