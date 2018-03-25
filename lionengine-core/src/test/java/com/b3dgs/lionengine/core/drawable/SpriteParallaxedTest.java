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
package com.b3dgs.lionengine.core.drawable;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.graphic.FactoryGraphicMock;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.ImageHeader;
import com.b3dgs.lionengine.graphic.ImageInfo;
import com.b3dgs.lionengine.graphic.SpriteParallaxed;

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
     * Prepare test.
     */
    @BeforeClass
    public static void setUp()
    {
        Medias.setLoadFromJar(SpriteParallaxedTest.class);
        Graphics.setFactoryGraphic(new FactoryGraphicMock());

        media = Medias.create("image.png");
        g = Graphics.createImageBuffer(100, 100).createGraphic();
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
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
        Assert.assertTrue(spriteA.equals(spriteA));
        Assert.assertEquals(info.getHeight() / LINES, spriteA.getHeight());

        Assert.assertEquals(38, spriteA.getWidth());
        Assert.assertEquals(41, spriteA.getLineWidth(2));

        // Test render
        spriteA.render(g, 0, 0, 0);

        // Resize
        final SpriteParallaxed spriteB = Drawable.loadSpriteParallaxed(media, LINES, 60, 100);
        spriteB.stretch(200, 100);
        spriteB.load(true);
        Assert.assertFalse(spriteB.equals(spriteA));
        Assert.assertTrue(spriteA.hashCode() != spriteB.hashCode());
        Assert.assertFalse(spriteA.equals(media));

        final SpriteParallaxed spriteC = Drawable.loadSpriteParallaxed(media, LINES, 60, 100);
        spriteC.stretch(100, 200);
        spriteC.load(true);
        Assert.assertFalse(spriteC.equals(spriteA));
        Assert.assertTrue(spriteA.hashCode() != spriteC.hashCode());
        Assert.assertFalse(spriteA.equals(media));
    }

    /**
     * Test parallax sprite failure.
     */
    @Test
    public void testParallaxFailure()
    {
        try
        {
            final SpriteParallaxed spriteC = Drawable.loadSpriteParallaxed(media, 0, 60, 100);
            Assert.assertNotNull(spriteC);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
            Assert.assertNotNull(exception);
        }
        try
        {
            final SpriteParallaxed spriteC = Drawable.loadSpriteParallaxed(media, LINES, 60, 0);
            Assert.assertNotNull(spriteC);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
            Assert.assertNotNull(exception);
        }
        try
        {
            final SpriteParallaxed spriteC = Drawable.loadSpriteParallaxed(media, LINES, 0, 60);
            Assert.assertNotNull(spriteC);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
            Assert.assertNotNull(exception);
        }
    }
}
