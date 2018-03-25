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
 * Test the force configuration class.
 */
public class ForceConfigTest
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
     * Test the constructor.
     * 
     * @throws Exception If error.
     */
    @Test(expected = LionEngineException.class)
    public void testConstructor() throws Exception
    {
        UtilTests.testPrivateConstructor(ForceConfig.class);
    }

    /**
     * Test the configuration reader.
     */
    @Test
    public void testConfig()
    {
        final Force force = new Force(1.0, 2.0, 3.0, 4.0);

        final Media media = Medias.create("force.xml");
        try
        {
            final Xml root = new Xml("test");
            root.add(ForceConfig.exports(force));
            root.save(media);

            final Force loaded = ForceConfig.imports(new Xml(media));

            Assert.assertEquals(force, loaded);
            Assert.assertEquals(force, ForceConfig.imports(new Configurer(media)));
        }
        finally
        {
            Assert.assertTrue(media.getFile().delete());
        }
    }

    /**
     * Test the configuration with optional fields.
     */
    @Test
    public void testConfigOptional()
    {
        final Force force = new Force(1.0, 2.0, 0.0, 0.0);

        final Media media = Medias.create("force.xml");
        try
        {
            final Xml root = new Xml("test");
            final Xml node = root.createChild(ForceConfig.NODE_FORCE);
            node.writeDouble(ForceConfig.ATT_VX, 1.0);
            node.writeDouble(ForceConfig.ATT_VY, 2.0);
            root.save(media);

            final Force loaded = ForceConfig.imports(new Xml(media));

            Assert.assertEquals(force, loaded);
            Assert.assertEquals(force, ForceConfig.imports(new Configurer(media)));
        }
        finally
        {
            Assert.assertTrue(media.getFile().delete());
        }
    }
}
