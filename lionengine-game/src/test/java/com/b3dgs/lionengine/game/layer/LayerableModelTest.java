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
package com.b3dgs.lionengine.game.layer;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.game.handler.Featurable;
import com.b3dgs.lionengine.game.handler.Services;
import com.b3dgs.lionengine.game.object.ObjectGame;
import com.b3dgs.lionengine.game.object.Setup;
import com.b3dgs.lionengine.game.object.UtilSetup;

/**
 * Test the layerable model.
 */
public class LayerableModelTest
{
    /** Object config test. */
    private static Media config;

    /**
     * Prepare test.
     */
    @BeforeClass
    public static void setUp()
    {
        Medias.setResourcesDirectory(System.getProperty("java.io.tmpdir"));
        config = UtilSetup.createConfig();
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        Assert.assertTrue(config.getFile().delete());
        Medias.setResourcesDirectory(Constant.EMPTY_STRING);
    }

    /**
     * Test the layer functionality.
     */
    @Test
    public void testLayer()
    {
        final LayerableModel layerable = new LayerableModel();

        final AtomicReference<Featurable> objectRef = new AtomicReference<Featurable>();
        final AtomicInteger oldLayerRef = new AtomicInteger();
        final AtomicInteger newLayerRef = new AtomicInteger();
        layerable.addListener(new LayerableListener()
        {
            @Override
            public void notifyLayerChanged(Featurable featurable, Integer layerOld, Integer layerNew)
            {
                objectRef.set(featurable);
                oldLayerRef.set(layerOld.intValue());
                newLayerRef.set(layerNew.intValue());
            }
        });

        final Services services = new Services();
        services.add(new ComponentDisplayerLayer());
        final Setup setup = new Setup(config);
        final ObjectGame object = new ObjectGame(setup);
        layerable.prepare(object, services);

        Assert.assertEquals(0, layerable.getLayer().intValue());

        layerable.setLayer(1);

        Assert.assertEquals(1, layerable.getLayer().intValue());
    }
}
