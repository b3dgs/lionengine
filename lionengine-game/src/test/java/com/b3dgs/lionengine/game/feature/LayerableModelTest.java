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
package com.b3dgs.lionengine.game.feature;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.game.Featurable;
import com.b3dgs.lionengine.game.FeaturableModel;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.Services;

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

        final AtomicReference<FeatureProvider> objectRef = new AtomicReference<FeatureProvider>();
        final AtomicInteger oldLayerRef = new AtomicInteger();
        final AtomicInteger newLayerRef = new AtomicInteger();
        layerable.addListener(new LayerableListener()
        {
            @Override
            public void notifyLayerChanged(FeatureProvider provider,
                                           Integer layerRefreshOld,
                                           Integer layerRefreshNew,
                                           Integer layerDisplayOld,
                                           Integer layerDisplayNew)
            {
                objectRef.set(provider);
                oldLayerRef.set(layerRefreshOld.intValue());
                newLayerRef.set(layerRefreshNew.intValue());
            }
        });

        final Services services = new Services();
        services.add(new ComponentDisplayable());

        final Featurable featurable = new FeaturableModel();
        layerable.prepare(featurable, services);

        Assert.assertEquals(0, layerable.getLayerRefresh().intValue());
        Assert.assertEquals(0, layerable.getLayerDisplay().intValue());

        layerable.setLayer(1);

        Assert.assertEquals(1, layerable.getLayerRefresh().intValue());
        Assert.assertEquals(1, layerable.getLayerDisplay().intValue());
    }
}
