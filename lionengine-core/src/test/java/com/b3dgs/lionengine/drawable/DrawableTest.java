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
package com.b3dgs.lionengine.drawable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.core.Graphics;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.mock.FactoryGraphicMock;
import com.b3dgs.lionengine.mock.MediaMock;

/**
 * Test the drawable package.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
@SuppressWarnings("static-method")
public class DrawableTest
{
    /** Surface. */
    private static final Media MEDIA = new MediaMock("image.png");
    /** Font data. */
    private static final Media FONT = new MediaMock("fontdata.xml");

    /**
     * Setup test.
     */
    @BeforeClass
    public static void setUp()
    {
        Graphics.setFactoryGraphic(new FactoryGraphicMock());
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
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
                Assert.assertNotNull(Drawable.loadImage(new MediaMock("void", true)));
                Assert.fail();
            }
            else
            {
                Assert.assertNotNull(Drawable.loadImage(MEDIA));
                Assert.assertNotNull(Drawable.loadImage(Graphics.getImageBuffer(MEDIA)));
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
                Assert.assertNotNull(Drawable.loadSprite(new MediaMock("void", true)));
                Assert.fail();
            }
            else
            {
                Assert.assertNotNull(Drawable.loadSprite(MEDIA));
                Assert.assertNotNull(Drawable.loadSprite(Graphics.getImageBuffer(MEDIA)));
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
                Assert.assertNotNull(Drawable.loadSpriteAnimated(MEDIA, width, height));
                Assert.fail();
            }
            else
            {
                Assert.assertNotNull(Drawable.loadSpriteAnimated(MEDIA, width, height));
                Assert.assertNotNull(Drawable.loadSpriteAnimated(Graphics.getImageBuffer(MEDIA), width, height));
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
                Assert.assertNotNull(Drawable.loadSpriteTiled(MEDIA, width, height));
                Assert.fail();
            }
            else
            {
                Assert.assertNotNull(Drawable.loadSpriteTiled(MEDIA, width, height));
                Assert.assertNotNull(Drawable.loadSpriteTiled(Graphics.getImageBuffer(MEDIA), width, height));
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
            Assert.assertNotNull(Drawable.loadSpriteFont(MEDIA, FONT, width, height));
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
            Assert.assertNotNull(Drawable.loadSpriteParallaxed(MEDIA, lines, sx, sy));
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
     * @throws ReflectiveOperationException If error.
     */
    @Test(expected = InvocationTargetException.class)
    public void testDrawableFactory() throws ReflectiveOperationException
    {
        final Constructor<Drawable> constructor = Drawable.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        final Drawable drawable = constructor.newInstance();
        Assert.assertNotNull(drawable);
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
