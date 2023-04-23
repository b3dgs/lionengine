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
import static com.b3dgs.lionengine.UtilAssert.assertPrivateConstructor;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Xml;

/**
 * Test {@link ForceConfig}.
 */
final class ForceConfigTest
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
     * Test constructor.
     * 
     * @throws Exception If error.
     */
    @Test
    void testConstructor() throws Exception
    {
        assertPrivateConstructor(ForceConfig.class);
    }

    /**
     * Test exports imports
     */
    @Test
    void testExportsImports()
    {
        final Force force = new Force(1.0, 2.0, 3.0, 4.0);

        final Xml root = new Xml("test");
        root.add(ForceConfig.exports(force));

        final Media media = Medias.create("force.xml");
        root.save(media);

        assertEquals(force, ForceConfig.imports(new Xml(media)));
        assertEquals(force, ForceConfig.imports(new Configurer(media)));

        assertTrue(media.getFile().delete());
    }

    /**
     * Test with optional fields.
     */
    @Test
    void testOptional()
    {
        final Xml root = new Xml("test");
        final Xml node = root.createChild(ForceConfig.NODE_FORCE);

        final Force force = new Force(1.0, 2.0, 0.0, 0.0);
        node.writeDouble(ForceConfig.ATT_VX, force.getDirectionHorizontal());
        node.writeDouble(ForceConfig.ATT_VY, force.getDirectionVertical());

        final Media media = Medias.create("force.xml");
        root.save(media);

        assertEquals(force, ForceConfig.imports(new Xml(media)));
        assertEquals(force, ForceConfig.imports(new Configurer(media)));

        assertTrue(media.getFile().delete());
    }
}
