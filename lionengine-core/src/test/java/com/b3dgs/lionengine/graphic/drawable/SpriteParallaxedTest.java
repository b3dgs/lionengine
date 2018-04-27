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
package com.b3dgs.lionengine.graphic.drawable;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertFalse;
import static com.b3dgs.lionengine.UtilAssert.assertThrows;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.graphic.FactoryGraphicMock;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Graphics;

/**
 * Test {@link SpriteParallaxed}.
 */
public final class SpriteParallaxedTest
{
    /** Lines number. */
    private static final int LINES = 8;
    /** Image media. */
    private static Media media;
    /** Graphic test output. */
    private static Graphic g;

    /**
     * Prepare tests.
     */
    @BeforeAll
    public static void beforeTests()
    {
        Medias.setLoadFromJar(SpriteParallaxedTest.class);
        Graphics.setFactoryGraphic(new FactoryGraphicMock());

        media = Medias.create("image.png");
        g = Graphics.createImageBuffer(100, 100).createGraphic();
    }

    /**
     * Clean up tests.
     */
    @AfterAll
    public static void afterTests()
    {
        g.dispose();

        Medias.setLoadFromJar(null);
        Graphics.setFactoryGraphic(null);
    }

    /**
     * Test parallax sprite.
     */
    @SuppressWarnings("unlikely-arg-type")
    @Test
    public void testParallax()
    {
        final ImageHeader info = ImageInfo.get(media);
        final SpriteParallaxed spriteA = Drawable.loadSpriteParallaxed(media, LINES, 60, 100);

        spriteA.load(false);
        assertTrue(spriteA.equals(spriteA));
        assertEquals(info.getHeight() / LINES, spriteA.getHeight());

        assertEquals(38, spriteA.getWidth());
        assertEquals(41, spriteA.getLineWidth(2));

        // Test render
        spriteA.render(g, 0, 0, 0);

        // Resize
        final SpriteParallaxed spriteB = Drawable.loadSpriteParallaxed(media, LINES, 60, 100);
        spriteB.stretch(200, 100);
        spriteB.load(true);
        assertFalse(spriteB.equals(spriteA));
        assertTrue(spriteA.hashCode() != spriteB.hashCode());
        assertFalse(spriteA.equals(media));

        final SpriteParallaxed spriteC = Drawable.loadSpriteParallaxed(media, LINES, 60, 100);
        spriteC.stretch(100, 200);
        spriteC.load(true);
        assertFalse(spriteC.equals(spriteA));
        assertTrue(spriteA.hashCode() != spriteC.hashCode());
        assertFalse(spriteA.equals(media));
    }

    /**
     * Test parallax sprite failure.
     */
    @Test
    public void testParallaxFailure()
    {
        assertThrows(() -> Drawable.loadSpriteParallaxed(media, 0, 60, 100),
                     "Invalid argument: 0 is not strictly superior to 0");
        assertThrows(() -> Drawable.loadSpriteParallaxed(media, LINES, 60, 0),
                     "Invalid argument: 0 is not strictly superior to 0");
        assertThrows(() -> Drawable.loadSpriteParallaxed(media, LINES, 0, 60),
                     "Invalid argument: 0 is not strictly superior to 0");
    }
}
