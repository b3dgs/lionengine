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
package com.b3dgs.lionengine.game.background;

import static com.b3dgs.lionengine.UtilAssert.assertThrows;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.graphic.FactoryGraphicMock;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Graphics;

/**
 * Test {@link CloudsAbstract}.
 */
final class CloudsTest
{
    /**
     * Prepare tests.
     */
    @BeforeAll
    public static void beforeTests()
    {
        Graphics.setFactoryGraphic(new FactoryGraphicMock());
        Medias.setLoadFromJar(CloudsTest.class);
    }

    /**
     * Clean up tests.
     */
    @AfterAll
    public static void afterTests()
    {
        Graphics.setFactoryGraphic(null);
        Medias.setLoadFromJar(null);
    }

    /**
     * Test clouds.
     */
    @Test
    void testClouds()
    {
        final CloudsAbstract clouds = new CloudsAbstract(Medias.create("surface.png"), 7, 1, 10, 0)
        {
            // Mock
        };
        clouds.setY(0, 1);
        clouds.setScreenWidth(20);
        clouds.setSpeed(0, 2);
        clouds.update(1.0, 1, 2, 3.0);

        assertThrows(ArrayIndexOutOfBoundsException.class, () -> clouds.setY(11, 0), "11");
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> clouds.setSpeed(11, 0), "11");

        final Graphic g = Graphics.createGraphic();
        clouds.render(g);
        g.dispose();
    }
}
