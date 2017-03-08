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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.game.ActionRef;
import com.b3dgs.lionengine.game.ActionsConfig;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.io.Xml;
import com.b3dgs.lionengine.util.UtilTests;

/**
 * Test the producer config class.
 */
public class ProducerConfigTest
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
        Medias.setResourcesDirectory(Constant.EMPTY_STRING);
    }

    /**
     * Test the constructor.
     * 
     * @throws Exception If error.
     */
    @Test(expected = LionEngineException.class)
    public void testConstructor() throws Exception
    {
        UtilTests.testPrivateConstructor(ActionsConfig.class);
    }

    /**
     * Test the producer configuration.
     */
    @Test
    public void testConfig()
    {
        final Media media = Medias.create("producer.xml");
        final ActionRef ref = new ActionRef("ref", false, new ArrayList<ActionRef>());
        final ActionRef ref2 = new ActionRef("ref", false, Arrays.asList(ref));
        final Collection<ActionRef> refs = Arrays.asList(new ActionRef("test", true, Arrays.asList(ref2)));

        try
        {
            final Xml root = new Xml("test");
            root.add(ActionsConfig.exports(refs));
            root.save(media);

            final Collection<ActionRef> loaded = ActionsConfig.imports(new Xml(media));
            Assert.assertEquals(refs, loaded);
            Assert.assertEquals(refs, ActionsConfig.imports(new Configurer(media)));
        }
        finally
        {
            Assert.assertTrue(media.getFile().delete());
        }
    }

    /**
     * Test the producer configuration failure because cancel flag is not used on child reference.
     */
    @Test
    public void testConfigCancelOnRef()
    {
        final Media media = Medias.create("producer.xml");
        final ActionRef ref = new ActionRef("ref", true, new ArrayList<ActionRef>());
        final Collection<ActionRef> refs = Arrays.asList(new ActionRef("test", false, Arrays.asList(ref)));

        try
        {
            final Xml root = new Xml("test");
            root.add(ActionsConfig.exports(refs));
            root.save(media);

            final Collection<ActionRef> loaded = ActionsConfig.imports(new Xml(media));
            Assert.assertNotEquals(refs, loaded);
            Assert.assertNotEquals(refs, ActionsConfig.imports(new Configurer(media)));
        }
        finally
        {
            Assert.assertTrue(media.getFile().delete());
        }
    }

    /**
     * Test the producer import without node.
     */
    @Test
    public void testImportNoNode()
    {
        final Media media = Medias.create("producer.xml");
        try
        {
            final Xml root = new Xml("test");
            root.save(media);

            Assert.assertTrue(ActionsConfig.imports(new Xml(media)).isEmpty());
        }
        finally
        {
            Assert.assertTrue(media.getFile().delete());
        }
    }
}
