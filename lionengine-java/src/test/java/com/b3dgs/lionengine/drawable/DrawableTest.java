/*
 * Copyright (C) 2013 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Version;
import com.b3dgs.lionengine.core.Engine;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.UtilityImage;

/**
 * Test the drawable package.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class DrawableTest
{
    /** Surface. */
    private static Media media;
    /** Surface. */
    private static Media font;

    /**
     * Setup test.
     */
    @BeforeClass
    public static void setUp()
    {
        Engine.start("DrawableTest", Version.create(1, 0, 0), Media.getPath("src", "test", "resources"));
        DrawableTest.media = Media.get("dot.png");
        DrawableTest.font = Media.get("fontdata.xml");
    }

    /**
     * Clean test.
     */
    @AfterClass
    public static void cleanUp()
    {
        Engine.terminate();
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
                Assert.assertNotNull(Drawable.loadImage(new Media("void")));
                Assert.fail();
            }
            else
            {
                Assert.assertNotNull(Drawable.loadImage(DrawableTest.media));
                Assert.assertNotNull(Drawable.loadImage(UtilityImage.getImageBuffer(DrawableTest.media, false)));
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
                Assert.assertNotNull(Drawable.loadSprite(new Media("void")));
                Assert.fail();
            }
            else
            {
                Assert.assertNotNull(Drawable.loadSprite(DrawableTest.media));
                Assert.assertNotNull(Drawable.loadSprite(UtilityImage.getImageBuffer(DrawableTest.media, false)));
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
                Assert.assertNotNull(Drawable.loadSpriteAnimated(DrawableTest.media, width, height));
                Assert.fail();
            }
            else
            {
                Assert.assertNotNull(Drawable.loadSpriteAnimated(DrawableTest.media, width, height));
                Assert.assertNotNull(Drawable.loadSpriteAnimated(
                        UtilityImage.getImageBuffer(DrawableTest.media, false), width, height));
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
                Assert.assertNotNull(Drawable.loadSpriteTiled(DrawableTest.media, width, height));
                Assert.fail();
            }
            else
            {
                Assert.assertNotNull(Drawable.loadSpriteTiled(DrawableTest.media, width, height));
                Assert.assertNotNull(Drawable.loadSpriteTiled(UtilityImage.getImageBuffer(DrawableTest.media, false),
                        width, height));
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
            Assert.assertNotNull(Drawable.loadSpriteFont(DrawableTest.media, DrawableTest.font, width, height));
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
            Assert.assertNotNull(Drawable.loadSpriteParallaxed(DrawableTest.media, lines, sx, sy));
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
     * Test the drawable factory.
     * 
     * @throws SecurityException If error.
     * @throws NoSuchMethodException If error.
     * @throws IllegalArgumentException If error.
     * @throws IllegalAccessException If error.
     * @throws InstantiationException If error.
     */
    @Test
    public void testDrawableFactory() throws NoSuchMethodException, SecurityException, InstantiationException,
            IllegalAccessException, IllegalArgumentException
    {
        final Constructor<Drawable> constructor = Drawable.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        try
        {
            final Drawable drawable = constructor.newInstance();
            Assert.assertNotNull(drawable);
            Assert.fail();
        }
        catch (final InvocationTargetException exception)
        {
            // Success
        }
    }

    /**
     * Test the drawable failures creation.
     */
    @Test
    public void testDrawableFail()
    {
        DrawableTest.testImage(true);

        DrawableTest.testSprite(true);

        DrawableTest.testSpriteAnimated(true, 0, 0);
        DrawableTest.testSpriteAnimated(true, 0, 1);
        DrawableTest.testSpriteAnimated(true, 1, 0);

        DrawableTest.testSpriteTiled(true, 0, 0);
        DrawableTest.testSpriteTiled(true, 0, 1);
        DrawableTest.testSpriteTiled(true, 1, 0);

        DrawableTest.testSpriteFont(true, 0, 0);
        DrawableTest.testSpriteFont(true, 1, 0);
        DrawableTest.testSpriteFont(true, 0, 1);

        DrawableTest.testSpriteParallaxed(true, 0, 0, 0);
        DrawableTest.testSpriteParallaxed(true, 0, 0, 1);
        DrawableTest.testSpriteParallaxed(true, 0, 1, 0);
        DrawableTest.testSpriteParallaxed(true, 0, 1, 1);
        DrawableTest.testSpriteParallaxed(true, 1, 0, 0);
        DrawableTest.testSpriteParallaxed(true, 1, 0, 1);
        DrawableTest.testSpriteParallaxed(true, 1, 1, 0);
    }

    /**
     * Test success cases.
     */
    @Test
    public void testDrawableSuccess()
    {
        DrawableTest.testImage(false);
        DrawableTest.testSprite(false);
        DrawableTest.testSpriteAnimated(false, 1, 1);
        DrawableTest.testSpriteTiled(false, 1, 1);
        DrawableTest.testSpriteFont(false, 1, 1);
        DrawableTest.testSpriteParallaxed(false, 1, 1, 1);
    }
}
