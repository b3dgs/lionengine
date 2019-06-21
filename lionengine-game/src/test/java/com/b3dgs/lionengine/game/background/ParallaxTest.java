/*
 * Copyright (C) 2013-2019 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.graphic.FactoryGraphicMock;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionProvider;

/**
 * Test {@link Parallax}.
 */
public final class ParallaxTest
{
    /**
     * Prepare tests.
     */
    @BeforeAll
    public static void beforeTests()
    {
        Graphics.setFactoryGraphic(new FactoryGraphicMock());
        Medias.setLoadFromJar(ParallaxTest.class);
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
    public void testParallax()
    {
        final Parallax parallax = new Parallax(new SourceResolutionProvider()
        {
            @Override
            public int getWidth()
            {
                return 320;
            }

            @Override
            public int getHeight()
            {
                return 240;
            }

            @Override
            public int getRate()
            {
                return 60;
            }
        }, Medias.create("surface.png"), 11, 1, 2, 100, 100);

        parallax.setScreenSize(320, 240);
        parallax.update(1.0, 1, 0, 200.0);
        parallax.update(1.0, 1, 200, 200.0);
        parallax.update(1.0, 1, 300, 200.0);
        parallax.update(1.0, 1, 0, -200.0);
        parallax.update(1.0, 1, 0, -200.0);
        parallax.update(1.0, 1, 0, -200.0);

        final Graphic g = Graphics.createGraphic();
        parallax.render(g);
        g.dispose();
    }
}
