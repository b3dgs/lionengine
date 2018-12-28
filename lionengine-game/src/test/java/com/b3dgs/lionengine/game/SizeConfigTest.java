/*
 * Copyright (C) 2013-2018 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

/**
 * Test {@link SizeConfig}.
 */
public final class SizeConfigTest
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
        final SizeConfig config = new SizeConfig(16, 32);

        final Xml root = new Xml("test");
        root.add(SizeConfig.exports(config));

        final Media media = Medias.create("object.xml");
        root.save(media);

        assertEquals(config, SizeConfig.imports(new Xml(media)));
        assertEquals(config, SizeConfig.imports(new Configurer(media)));

        assertTrue(media.getFile().delete());
    }

    /**
     * Test equals.
     */
    @Test
    public void testEquals()
    {
        final SizeConfig config = new SizeConfig(16, 32);

        assertEquals(config, config);
        assertEquals(config, new SizeConfig(16, 32));

        assertNotEquals(config, null);
        assertNotEquals(config, new Object());
        assertNotEquals(config, new SizeConfig(0, 32));
        assertNotEquals(config, new SizeConfig(16, 0));
    }

    /**
     * Test hash code.
     */
    @Test
    public void testHashCode()
    {
        final SizeConfig hash = new SizeConfig(16, 32);

        assertHashEquals(hash, new SizeConfig(16, 32));

        assertHashNotEquals(hash, new SizeConfig(0, 32));
        assertHashNotEquals(hash, new SizeConfig(16, 0));
    }

    /**
     * Test to string.
     */
    @Test
    public void testToString()
    {
        final SizeConfig config = new SizeConfig(16, 32);

        assertHashEquals("SizeConfig [width=16, height=32]", config.toString());
    }
}
