/*
 * Copyright (C) 2013-2020 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.rasterable;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertNotNull;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.ViewerMock;
import com.b3dgs.lionengine.game.feature.Animatable;
import com.b3dgs.lionengine.game.feature.AnimatableModel;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.FeaturableModel;
import com.b3dgs.lionengine.game.feature.MirrorableModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.TransformableModel;
import com.b3dgs.lionengine.graphic.FactoryGraphicMock;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.GraphicMock;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.raster.RasterImage;

/**
 * Test {@link RasterableModel}.
 */
final class RasterableModelTest
{
    /** Object configuration file name. */
    private static final String OBJECT_XML = "ObjectRaster.xml";
    /** Raster configuration file name. */
    private static final String RASTER_XML = "raster.xml";

    private final Services services = new Services();
    private final Graphic g = new GraphicMock();

    /**
     * Prepare test.
     */
    @BeforeAll
    public static void beforeTests()
    {
        Medias.setLoadFromJar(RasterableModelTest.class);
        Graphics.setFactoryGraphic(new FactoryGraphicMock());
    }

    /**
     * Clean up test.
     */
    @AfterAll
    public static void afterTests()
    {
        Medias.setLoadFromJar(null);
        Graphics.setFactoryGraphic(null);
    }

    /**
     * Test the model.
     */
    @Test
    void testModel()
    {
        services.add(new ViewerMock());

        final Media raster = Medias.create(RASTER_XML);
        final SetupSurfaceRastered setup = new SetupSurfaceRastered(Medias.create(OBJECT_XML), raster);

        final Featurable featurable = new FeaturableModel(services, setup);
        final Transformable transformable = featurable.addFeatureAndGet(new TransformableModel(services, setup));
        final Animatable animatable = featurable.addFeatureAndGet(new AnimatableModel(services, setup));
        featurable.addFeature(new MirrorableModel(services, setup));

        final Rasterable rasterable = new RasterableModel(services, setup);
        rasterable.setFrameOffsets(1, 2);
        rasterable.prepare(featurable);
        rasterable.setOrigin(Origin.TOP_LEFT);
        rasterable.update(1.0);

        rasterable.setEnabled(false);
        rasterable.update(1.0);
        rasterable.render(g);

        rasterable.setVisibility(false);
        rasterable.update(1.0);
        rasterable.render(g);

        assertEquals(1, rasterable.getRasterIndex(0));
        assertEquals(RasterImage.MAX_RASTERS, rasterable.getRasterIndex(240));
        assertNotNull(rasterable.getRasterAnim(0));

        transformable.teleportY(-100);
        rasterable.update(1.0);

        assertEquals(1, rasterable.getRasterIndex(0));
        assertEquals(RasterImage.MAX_RASTERS, rasterable.getRasterIndex(240));
        assertNotNull(rasterable.getRasterAnim(0));

        animatable.play(new Animation("default", 1, 5, 1.0, false, false));
        animatable.update(1.0);
        rasterable.update(1.0);

        assertEquals(1, rasterable.getRasterAnim(0).getFrame());

        rasterable.setAnimOffset(1);
        rasterable.update(1.0);

        assertEquals(1, rasterable.getRasterAnim(0).getFrame());
    }
}
