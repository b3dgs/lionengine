/*
 * Copyright (C) 2013-2022 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertNull;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.game.FeatureProvider;

/**
 * Test {@link LayerableModel}.
 */
final class LayerableModelTest
{
    /** Object config test. */
    private static Media config;

    /**
     * Prepare test.
     */
    @BeforeAll
    public static void beforeTests()
    {
        Medias.setResourcesDirectory(System.getProperty("java.io.tmpdir"));
        config = UtilSetup.createConfig(LayerableModelTest.class);
    }

    /**
     * Clean up test.
     */
    @AfterAll
    public static void afterTests()
    {
        assertTrue(config.getFile().delete());
        Medias.setResourcesDirectory(null);
    }

    private final Services services = new Services();
    private final Setup setup = new Setup(config);

    /**
     * Test the layer functionality.
     */
    @Test
    void testLayer()
    {
        final LayerableModel layerable = new LayerableModel(services, setup);

        final AtomicReference<FeatureProvider> objectRef = new AtomicReference<>();
        final AtomicInteger oldLayerRef = new AtomicInteger();
        final AtomicInteger newLayerRef = new AtomicInteger();
        final LayerableListener listener = (provider,
                                            layerRefreshOld,
                                            layerRefreshNew,
                                            layerDisplayOld,
                                            layerDisplayNew) ->
        {
            objectRef.set(provider);
            oldLayerRef.set(layerRefreshOld.intValue());
            newLayerRef.set(layerRefreshNew.intValue());
        };
        layerable.addListener(listener);

        services.add(new ComponentDisplayable());

        final Featurable featurable = new FeaturableModel(services, setup);
        layerable.prepare(featurable);

        assertEquals(1, layerable.getLayerRefresh().intValue());
        assertEquals(2, layerable.getLayerDisplay().intValue());

        layerable.setLayer(Integer.valueOf(1), Integer.valueOf(2));

        assertEquals(1, layerable.getLayerRefresh().intValue());
        assertEquals(2, layerable.getLayerDisplay().intValue());

        layerable.removeListener(listener);
        objectRef.set(null);
        layerable.setLayer(Integer.valueOf(1), Integer.valueOf(2));

        assertNull(objectRef.get());
    }

    /**
     * Test constructor with setup.
     */
    @Test
    void testConstructorSetup()
    {
        services.add(new ComponentDisplayable());

        LayerableModel layerable = new LayerableModel(services, setup);
        assertEquals(0, layerable.getLayerRefresh().intValue());
        assertEquals(0, layerable.getLayerDisplay().intValue());

        final Xml xml = new Xml(config);
        xml.add(LayerableConfig.exports(new LayerableConfig(1, 2)));
        xml.save(config);

        layerable = new LayerableModel(services, new Setup(config));

        assertEquals(1, layerable.getLayerRefresh().intValue());
        assertEquals(2, layerable.getLayerDisplay().intValue());
    }
}
