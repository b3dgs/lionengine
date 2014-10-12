/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.drawable;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.Filter;
import com.b3dgs.lionengine.ImageInfo;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Transparency;
import com.b3dgs.lionengine.core.Core;
import com.b3dgs.lionengine.core.FactoryGraphicProvider;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.mock.FactoryGraphicMock;
import com.b3dgs.lionengine.mock.MediaMock;

/**
 * Test the parallaxed sprite class.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class SpriteParallaxedTest
{
    /** Lines number. */
    private static final int LINES = 8;
    /** Image media. */
    private static final Media MEDIA = new MediaMock("image.png");
    /** Graphic test output. */
    private static Graphic g;

    /**
     * Prepare test.
     */
    @BeforeClass
    public static void setUp()
    {
        FactoryGraphicProvider.setFactoryGraphic(new FactoryGraphicMock());
        g = Core.GRAPHIC.createImageBuffer(100, 100, Transparency.OPAQUE).createGraphic();
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        FactoryGraphicProvider.setFactoryGraphic(null);
    }

    /**
     * Test parallax sprite.
     */
    @Test
    public void testParallax()
    {
        final ImageInfo info = ImageInfo.get(MEDIA);
        final SpriteParallaxed spriteA = Drawable.loadSpriteParallaxed(MEDIA, LINES, 60, 100);

        Assert.assertEquals(0, spriteA.getWidthOriginal());
        Assert.assertEquals(0, spriteA.getHeightOriginal());

        spriteA.prepare(Filter.NONE);
        Assert.assertTrue(spriteA.equals(spriteA));
        Assert.assertEquals((int) (spriteA.getWidthOriginal() * 0.6), spriteA.getWidth());
        Assert.assertEquals(info.getWidth(), spriteA.getWidthOriginal());
        Assert.assertEquals(info.getHeight() / LINES, spriteA.getHeight());

        for (int i = 0; i < LINES; i++)
        {
            Assert.assertNotNull(spriteA.getLine(i));
            spriteA.render(g, i, 0, 0);
        }

        // Test render
        try
        {
            spriteA.render(null, 0, 0);
            Assert.fail();
        }
        catch (final NullPointerException exception)
        {
            // Success
        }
        spriteA.render(g, 0, 0);

        // Resize
        final SpriteParallaxed spriteB = Drawable.loadSpriteParallaxed(MEDIA, LINES, 60, 100);
        spriteB.scale(200);
        spriteB.prepare(Filter.BILINEAR);
        Assert.assertEquals(info.getWidth(), spriteB.getWidthOriginal());
        Assert.assertFalse(spriteB.equals(spriteA));
        Assert.assertTrue(spriteA.hashCode() != spriteB.hashCode());
        Assert.assertFalse(spriteA.equals(MEDIA));
    }

    /**
     * Test parallax sprite failure.
     */
    @Test
    public void testParallaxFailure()
    {
        try
        {
            final SpriteParallaxed spriteC = Drawable.loadSpriteParallaxed(MEDIA, 0, 60, 100);
            Assert.assertNotNull(spriteC);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }
        try
        {
            final SpriteParallaxed spriteC = Drawable.loadSpriteParallaxed(MEDIA, LINES, 60, 0);
            Assert.assertNotNull(spriteC);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }
        try
        {
            final SpriteParallaxed spriteC = Drawable.loadSpriteParallaxed(MEDIA, LINES, 0, 60);
            Assert.assertNotNull(spriteC);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }
    }
}
