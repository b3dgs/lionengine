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
package com.b3dgs.lionengine.game.feature.rasterable;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Engine;
import com.b3dgs.lionengine.EngineMock;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.UtilFolder;
import com.b3dgs.lionengine.Version;
import com.b3dgs.lionengine.graphic.FactoryGraphicMock;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.ImageBuffer;

/**
 * Test {@link SetupSurfaceRastered}.
 */
final class SetupSurfaceRasteredTest
{
    /** Object configuration file name. */
    private static final String OBJECT_XML = "ObjectRaster.xml";

    /**
     * Start engine.
     */
    @BeforeAll
    static void beforeAll()
    {
        Engine.start(new EngineMock(SetupSurfaceRasteredTest.class.getSimpleName(), Version.DEFAULT));

        Medias.setLoadFromJar(SetupSurfaceRasteredTest.class);
        Graphics.setFactoryGraphic(new FactoryGraphicMock());
    }

    /**
     * Terminate engine.
     */
    @AfterAll
    static void afterAll()
    {
        Medias.setLoadFromJar(null);
        Graphics.setFactoryGraphic(null);

        Engine.terminate();
    }

    /**
     * Test the setup surface rastered config.
     */
    @Test
    void testConfig()
    {
        final SetupSurfaceRastered setup = new SetupSurfaceRastered(Medias.create(OBJECT_XML));

        assertEquals(Medias.create("tiles.png"), setup.getFile());

        for (final ImageBuffer buffer : setup.getRasters())
        {
            assertEquals(setup.getSurface().getWidth(), buffer.getWidth());
            assertEquals(setup.getSurface().getHeight(), buffer.getHeight());
        }

        UtilFolder.deleteDirectory(Medias.create("void").getFile().getParentFile());
    }
}
