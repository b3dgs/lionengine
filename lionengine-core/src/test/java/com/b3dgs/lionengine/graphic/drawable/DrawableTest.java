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
package com.b3dgs.lionengine.graphic.drawable;

import static com.b3dgs.lionengine.UtilAssert.assertNotNull;
import static com.b3dgs.lionengine.UtilAssert.assertPrivateConstructor;
import static com.b3dgs.lionengine.UtilAssert.assertThrows;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.FactoryMediaDefault;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.graphic.DpiType;
import com.b3dgs.lionengine.graphic.FactoryGraphicMock;
import com.b3dgs.lionengine.graphic.Graphics;

/**
 * Test {@link Drawable}.
 */
public final class DrawableTest
{
    /** Surface. */
    private static Media media;
    /** Font data. */
    private static Media font;

    /**
     * Setup tests.
     */
    @BeforeAll
    public static void beforeTests()
    {
        Medias.setFactoryMedia(new FactoryMediaDefault());
        Medias.setLoadFromJar(DrawableTest.class);
        Graphics.setFactoryGraphic(new FactoryGraphicMock());

        media = Medias.create("image.png");
        font = Medias.create("fontdata.xml");
    }

    /**
     * Clean up tests.
     */
    @AfterAll
    public static void afterTests()
    {
        Medias.setLoadFromJar(null);
        Graphics.setFactoryGraphic(null);
    }

    /**
     * Test constructor.
     */
    @Test
    public void testConstructorPrivate()
    {
        assertPrivateConstructor(Drawable.class);
    }

    /**
     * Test load sprites.
     */
    @Test
    public void testLoad()
    {
        assertNotNull(Drawable.loadImage(Graphics.createImageBuffer(16, 32)));
        assertNotNull(Drawable.loadImage(media));

        assertNotNull(Drawable.loadSprite(Graphics.createImageBuffer(16, 32)));
        assertNotNull(Drawable.loadSprite(media));

        assertNotNull(Drawable.loadSpriteAnimated(Graphics.createImageBuffer(16, 32), 1, 1));
        assertNotNull(Drawable.loadSpriteAnimated(media, 1, 1));

        assertNotNull(Drawable.loadSpriteTiled(Graphics.createImageBuffer(16, 32), 1, 1));
        assertNotNull(Drawable.loadSpriteTiled(media, 1, 1));

        assertNotNull(Drawable.loadSpriteFont(Graphics.createImageBuffer(16, 32), font, 1, 1));
        assertNotNull(Drawable.loadSpriteFont(media, font, 1, 1));

        assertNotNull(Drawable.loadSpriteParallaxed(media, 1, 1, 1));

        assertNotNull(Drawable.loadSpriteDigit(Graphics.createImageBuffer(16, 32), 1, 1, 1));
        assertNotNull(Drawable.loadSpriteDigit(media, 1, 1, 1));
    }

    /**
     * Test success cases with custom DPI.
     */
    @Test
    public void testSuccessDpi()
    {
        for (final DpiType dpi : DpiType.values())
        {
            Drawable.setDpi(dpi);

            assertNotNull(Drawable.loadImage(Graphics.createImageBuffer(16, 32)));
            assertNotNull(Drawable.loadImage(media));

            assertNotNull(Drawable.loadSprite(Graphics.createImageBuffer(16, 32)));
            assertNotNull(Drawable.loadSprite(media));

            assertNotNull(Drawable.loadSpriteAnimated(Graphics.createImageBuffer(16, 32), 1, 1));
            assertNotNull(Drawable.loadSpriteAnimated(media, 1, 1));

            assertNotNull(Drawable.loadSpriteTiled(Graphics.createImageBuffer(16, 32), 1, 1));
            assertNotNull(Drawable.loadSpriteTiled(media, 1, 1));

            assertNotNull(Drawable.loadSpriteFont(media, font, 1, 1));

            assertNotNull(Drawable.loadSpriteParallaxed(media, 1, 1, 1));

            assertNotNull(Drawable.loadSpriteDigit(Graphics.createImageBuffer(16, 32), 1, 1, 1));
            assertNotNull(Drawable.loadSpriteDigit(media, 1, 1, 1));
        }
        Drawable.setDpi(null);
    }

    /**
     * Test success cases with missing DPI.
     */
    @Test
    public void testMissingDpi()
    {
        Drawable.setDpi(new Resolution(320, 240, 60), new Config(new Resolution(1920, 1200, 16), 60, false));

        assertNotNull(Drawable.loadImage(Graphics.createImageBuffer(16, 32)));
        assertNotNull(Drawable.loadImage(media));

        assertNotNull(Drawable.loadSprite(Graphics.createImageBuffer(16, 32)));
        assertNotNull(Drawable.loadSprite(media));

        assertNotNull(Drawable.loadSpriteAnimated(Graphics.createImageBuffer(16, 32), 1, 1));
        assertNotNull(Drawable.loadSpriteAnimated(media, 1, 1));

        assertNotNull(Drawable.loadSpriteTiled(Graphics.createImageBuffer(16, 32), 1, 1));
        assertNotNull(Drawable.loadSpriteTiled(media, 1, 1));

        assertNotNull(Drawable.loadSpriteFont(media, font, 1, 1));

        assertNotNull(Drawable.loadSpriteParallaxed(media, 1, 1, 1));

        assertNotNull(Drawable.loadSpriteDigit(Graphics.createImageBuffer(16, 32), 1, 1, 1));
        assertNotNull(Drawable.loadSpriteDigit(media, 1, 1, 1));

        Drawable.setDpi(null);
    }

    /**
     * Test DPI with 0 ordinal.
     */
    @Test
    public void testDpiOrdinal()
    {
        Drawable.setDpi(DpiType.values()[0]);

        assertThrows(() -> Drawable.loadImage(Medias.create("void")), "[void] Cannot open the media !");

        Drawable.setDpi(null);
    }
}
