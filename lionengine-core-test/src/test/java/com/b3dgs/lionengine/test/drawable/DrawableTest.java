/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.test.drawable;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.Graphics;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.test.mock.FactoryGraphicMock;
import com.b3dgs.lionengine.test.util.UtilTests;

/**
 * Test the drawable package.
 */
public class DrawableTest
{
    /** Surface. */
    private static Media media;
    /** Font data. */
    private static Media font;

    /**
     * Setup test.
     */
    @BeforeClass
    public static void setUp()
    {
        Medias.setLoadFromJar(DrawableTest.class);
        Graphics.setFactoryGraphic(new FactoryGraphicMock());

        media = Medias.create("image.png");
        font = Medias.create("fontdata.xml");
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        Medias.setLoadFromJar(null);
        Graphics.setFactoryGraphic(null);
    }

    /**
     * Test the sprite animated.
     * 
     * @param fail <code>true</code> if should fail, <code>false</code> else.
     */
    private static void testImage(boolean fail)
    {
        try
        {
            if (fail)
            {
                Assert.assertNotNull(Drawable.loadImage(Medias.create("void")));
                Assert.fail();
            }
            else
            {
                Assert.assertNotNull(Drawable.loadImage(media));
                Assert.assertNotNull(Drawable.loadImage(Graphics.getImageBuffer(media)));
            }
        }
        catch (final LionEngineException exception)
        {
            if (!fail)
            {
                Assert.fail(exception.getMessage());
            }
        }
    }

    /**
     * Test the sprite animated.
     * 
     * @param fail <code>true</code> if should fail, <code>false</code> else.
     */
    private static void testSprite(boolean fail)
    {
        try
        {
            if (fail)
            {
                Assert.assertNotNull(Drawable.loadSprite(Medias.create("void")));
                Assert.fail();
            }
            else
            {
                Assert.assertNotNull(Drawable.loadSprite(media));
                Assert.assertNotNull(Drawable.loadSprite(Graphics.getImageBuffer(media)));
            }
        }
        catch (final LionEngineException exception)
        {
            if (!fail)
            {
                Assert.fail();
            }
        }
    }

    /**
     * Test the sprite animated failure.
     * 
     * @param fail <code>true</code> if should fail, <code>false</code> else.
     * @param width The width.
     * @param height The height
     */
    private static void testSpriteAnimated(boolean fail, int width, int height)
    {
        try
        {
            if (fail)
            {
                Assert.assertNotNull(Drawable.loadSpriteAnimated(media, width, height));
                Assert.fail();
            }
            else
            {
                Assert.assertNotNull(Drawable.loadSpriteAnimated(media, width, height));
                Assert.assertNotNull(Drawable.loadSpriteAnimated(Graphics.getImageBuffer(media), width, height));
            }
        }
        catch (final LionEngineException exception)
        {
            if (!fail)
            {
                Assert.fail();
            }
        }
    }

    /**
     * Test the sprite tiled failure.
     * 
     * @param fail <code>true</code> if should fail, <code>false</code> else.
     * @param width The width.
     * @param height The height
     */
    private static void testSpriteTiled(boolean fail, int width, int height)
    {
        try
        {
            if (fail)
            {
                Assert.assertNotNull(Drawable.loadSpriteTiled(media, width, height));
                Assert.fail();
            }
            else
            {
                Assert.assertNotNull(Drawable.loadSpriteTiled(media, width, height));
                Assert.assertNotNull(Drawable.loadSpriteTiled(Graphics.getImageBuffer(media), width, height));
            }
        }
        catch (final LionEngineException exception)
        {
            if (!fail)
            {
                Assert.fail();
            }
        }
    }

    /**
     * Test the sprite font failure.
     * 
     * @param fail <code>true</code> if should fail, <code>false</code> else.
     * @param width The width.
     * @param height The height
     */
    private static void testSpriteFont(boolean fail, int width, int height)
    {
        try
        {
            Assert.assertNotNull(Drawable.loadSpriteFont(media, font, width, height));
            if (fail)
            {
                Assert.fail();
            }
        }
        catch (final LionEngineException exception)
        {
            if (!fail)
            {
                Assert.fail();
            }
        }
    }

    /**
     * Test the sprite parallaxed failure.
     * 
     * @param fail <code>true</code> if should fail, <code>false</code> else.
     * @param lines The lines.
     * @param sx The width.
     * @param sy The height
     */
    private static void testSpriteParallaxed(boolean fail, int lines, int sx, int sy)
    {
        try
        {
            Assert.assertNotNull(Drawable.loadSpriteParallaxed(media, lines, sx, sy));
            if (fail)
            {
                Assert.fail();
            }
        }
        catch (final LionEngineException exception)
        {
            if (!fail)
            {
                Assert.fail();
            }
        }
    }

    /**
     * Test the constructor.
     * 
     * @throws ReflectiveOperationException If error.
     */
    @Test(expected = LionEngineException.class)
    public void testConstructor() throws ReflectiveOperationException
    {
        UtilTests.testPrivateConstructor(Drawable.class);
    }

    /**
     * Test the drawable failures creation.
     */
    @Test
    public void testDrawableFail()
    {
        testSprite(true);

        testSpriteAnimated(true, 0, 0);
        testSpriteAnimated(true, 0, 1);
        testSpriteAnimated(true, 1, 0);

        testSpriteTiled(true, 0, 0);
        testSpriteTiled(true, 0, 1);
        testSpriteTiled(true, 1, 0);

        testSpriteFont(true, 0, 0);
        testSpriteFont(true, 1, 0);
        testSpriteFont(true, 0, 1);

        testSpriteParallaxed(true, 0, 0, 0);
        testSpriteParallaxed(true, 0, 0, 1);
        testSpriteParallaxed(true, 0, 1, 0);
        testSpriteParallaxed(true, 0, 1, 1);
        testSpriteParallaxed(true, 1, 0, 0);
        testSpriteParallaxed(true, 1, 0, 1);
        testSpriteParallaxed(true, 1, 1, 0);
    }

    /**
     * Test success cases.
     */
    @Test
    public void testDrawableSuccess()
    {
        testImage(false);
        testSprite(false);
        testSpriteAnimated(false, 1, 1);
        testSpriteTiled(false, 1, 1);
        testSpriteFont(false, 1, 1);
        testSpriteParallaxed(false, 1, 1, 1);
    }
}
