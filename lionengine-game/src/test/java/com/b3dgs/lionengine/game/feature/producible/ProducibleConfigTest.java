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
package com.b3dgs.lionengine.game.feature.producible;

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
import com.b3dgs.lionengine.game.SizeConfig;
import com.b3dgs.lionengine.game.feature.Setup;

/**
 * Test {@link ProducibleConfig}.
 */
public final class ProducibleConfigTest
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
        final ProducibleConfig producible = new ProducibleConfig(1, 2, 3);

        final Xml root = new Xml("test");
        root.add(SizeConfig.exports(new SizeConfig(producible.getWidth(), producible.getHeight())));
        root.add(ProducibleConfig.exports(producible));

        final Media media = Medias.create("producible.xml");
        root.save(media);

        assertEquals(producible, ProducibleConfig.imports(new Xml(media)));
        assertEquals(producible, ProducibleConfig.imports(new Setup(media)));
        assertEquals(producible, ProducibleConfig.imports(new Configurer(media)));

        assertTrue(media.getFile().delete());
    }

    /**
     * Test the producible equality.
     */
    @Test
    public void testEquals()
    {
        final ProducibleConfig producible = new ProducibleConfig(1, 2, 3);

        assertEquals(producible, producible);

        assertNotEquals(producible, null);
        assertNotEquals(producible, new Object());
        assertNotEquals(producible, new ProducibleConfig(0, 2, 3));
        assertNotEquals(producible, new ProducibleConfig(1, 0, 3));
        assertNotEquals(producible, new ProducibleConfig(1, 2, 0));
    }

    /**
     * Test hash code.
     */
    @Test
    public void testHashCode()
    {
        final ProducibleConfig hash = new ProducibleConfig(1, 2, 3);

        assertHashEquals(hash, new ProducibleConfig(1, 2, 3));

        assertHashNotEquals(hash, new ProducibleConfig(0, 2, 3));
        assertHashNotEquals(hash, new ProducibleConfig(1, 0, 3));
        assertHashNotEquals(hash, new ProducibleConfig(1, 2, 0));
    }

    /**
     * Test the to string.
     */
    @Test
    public void testToString()
    {
        final ProducibleConfig producible = new ProducibleConfig(1, 2, 3);

        assertEquals("ProducibleConfig [steps=1, width=2, height=3]", producible.toString());
    }
}
