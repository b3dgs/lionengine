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
package com.b3dgs.lionengine.game;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.io.Xml;
import com.b3dgs.lionengine.util.UtilTests;

/**
 * Test {@link ForceConfig}.
 */
public final class ForceConfigTest
{
    /**
     * Prepare test.
     */
    @BeforeClass
    public static void setUp()
    {
        Medias.setResourcesDirectory(System.getProperty("java.io.tmpdir"));
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        Medias.setResourcesDirectory(null);
    }

    /**
     * Test constructor.
     * 
     * @throws Exception If error.
     */
    @Test(expected = LionEngineException.class)
    public void testConstructor() throws Exception
    {
        UtilTests.testPrivateConstructor(ForceConfig.class);
    }

    /**
     * Test exports imports
     */
    @Test
    public void testExportsImports()
    {
        final Force force = new Force(1.0, 2.0, 3.0, 4.0);

        final Xml root = new Xml("test");
        root.add(ForceConfig.exports(force));

        final Media media = Medias.create("force.xml");
        root.save(media);

        Assert.assertEquals(force, ForceConfig.imports(new Xml(media)));
        Assert.assertEquals(force, ForceConfig.imports(new Configurer(media)));

        Assert.assertTrue(media.getFile().delete());
    }

    /**
     * Test with optional fields.
     */
    @Test
    public void testOptional()
    {
        final Xml root = new Xml("test");
        final Xml node = root.createChild(ForceConfig.NODE_FORCE);

        final Force force = new Force(1.0, 2.0, 0.0, 0.0);
        node.writeDouble(ForceConfig.ATT_VX, force.getDirectionHorizontal());
        node.writeDouble(ForceConfig.ATT_VY, force.getDirectionVertical());

        final Media media = Medias.create("force.xml");
        root.save(media);

        Assert.assertEquals(force, ForceConfig.imports(new Xml(media)));
        Assert.assertEquals(force, ForceConfig.imports(new Configurer(media)));

        Assert.assertTrue(media.getFile().delete());
    }
}
