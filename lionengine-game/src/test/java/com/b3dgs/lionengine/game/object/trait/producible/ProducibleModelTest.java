/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.object.trait.producible;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.game.Services;
import com.b3dgs.lionengine.game.object.ObjectGame;
import com.b3dgs.lionengine.game.object.ObjectGameTest;
import com.b3dgs.lionengine.game.object.Setup;
import com.b3dgs.lionengine.game.object.SizeConfig;
import com.b3dgs.lionengine.stream.Xml;
import com.b3dgs.lionengine.stream.XmlNode;
import com.b3dgs.lionengine.test.UtilTests;

/**
 * Test the producible model trait.
 */
public class ProducibleModelTest
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
     * Create media.
     * 
     * @return The media.
     */
    public static Media createProducibleMedia()
    {
        final ProducibleConfig producibleConfig = new ProducibleConfig(1, 2, 3);

        final Media media = ObjectGameTest.createMedia(ObjectGame.class);
        final XmlNode root = Xml.create("test");
        root.add(SizeConfig.exports(new SizeConfig(producibleConfig.getWidth(), producibleConfig.getHeight())));
        root.add(ProducibleConfig.exports(producibleConfig));
        Xml.save(root, media);

        return media;
    }

    /**
     * Create listener.
     * 
     * @return The listener.
     */
    private ProducibleListener createListener()
    {
        return new ProducibleListener()
        {
            @Override
            public void notifyProductionStarted()
            {
                // Mock
            }

            @Override
            public void notifyProductionProgress()
            {
                // Mock
            }

            @Override
            public void notifyProductionEnded()
            {
                // Mock
            }
        };
    }

    /**
     * Test the producible.
     */
    @Test
    public void testProducible()
    {
        final Services services = new Services();
        final Media media = createProducibleMedia();
        final ObjectGame object = new ObjectGame(new Setup(media), services);
        final Producible producible = new ProducibleModel();
        producible.prepare(object, services);
        final ProducibleListener listener = createListener();
        producible.setLocation(1.0, 2.0);
        producible.addListener(listener);

        Assert.assertEquals(media, producible.getMedia());
        Assert.assertEquals(1, producible.getSteps());
        Assert.assertEquals(2, producible.getWidth());
        Assert.assertEquals(3, producible.getHeight());
        Assert.assertEquals(1.0, producible.getX(), UtilTests.PRECISION);
        Assert.assertEquals(2.0, producible.getY(), UtilTests.PRECISION);
        Assert.assertTrue(producible.getListeners().contains(listener));

        ObjectGameTest.freeId(producible.getOwner());
        Assert.assertTrue(media.getFile().delete());
    }

    /**
     * Test the producible self listener.
     */
    @Test
    public void testProducibleSelf()
    {
        final Services services = new Services();
        final Media media = createProducibleMedia();
        final ProducibleListenerSelf object = new ProducibleListenerSelf(new Setup(media), services);
        final Producible producible = new ProducibleModel();
        producible.prepare(object, services);

        Assert.assertTrue(producible.getListeners().contains(object));

        ObjectGameTest.freeId(producible.getOwner());
        Assert.assertTrue(media.getFile().delete());
    }

    /**
     * Producible self listener test.
     */
    private static class ProducibleListenerSelf extends ObjectGame implements ProducibleListener
    {
        /**
         * Constructor.
         * 
         * @param setup The setup.
         * @param services The services.
         */
        public ProducibleListenerSelf(Setup setup, Services services)
        {
            super(setup, services);
        }

        @Override
        public void notifyProductionStarted()
        {
            // Mock
        }

        @Override
        public void notifyProductionProgress()
        {
            // Mock
        }

        @Override
        public void notifyProductionEnded()
        {
            // Mock
        }
    }
}
