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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.Core;
import com.b3dgs.lionengine.core.FactoryGraphicProvider;
import com.b3dgs.lionengine.core.FactoryMediaProvider;
import com.b3dgs.lionengine.mock.FactoryGraphicMock;
import com.b3dgs.lionengine.mock.FactoryMediaMock;

/**
 * Test the drawable package.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class DrawableTest
{
    /** Surface. */
    protected static Media media;
    /** Surface. */
    protected static Media font;

    /**
     * Setup test.
     */
    @BeforeClass
    public static void setUp()
    {
        FactoryGraphicProvider.setFactoryGraphic(new FactoryGraphicMock());
        FactoryMediaProvider.setFactoryMedia(new FactoryMediaMock());
        DrawableTest.media = Core.MEDIA.create("src", "test", "resources", "drawable", "image.png");
        DrawableTest.font = Core.MEDIA.create("src", "test", "resources", "drawable", "fontdata.xml");
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        FactoryGraphicProvider.setFactoryGraphic(null);
        FactoryMediaProvider.setFactoryMedia(null);
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
                Assert.assertNotNull(Drawable.loadImage(Core.MEDIA.create("void")));
                Assert.fail();
            }
            else
            {
                Assert.assertNotNull(Drawable.loadImage(DrawableTest.media));
                Assert.assertNotNull(Drawable.loadImage(Core.GRAPHIC.getImageBuffer(DrawableTest.media, false)));
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
                Assert.assertNotNull(Drawable.loadSprite(Core.MEDIA.create("void")));
                Assert.fail();
            }
            else
            {
                Assert.assertNotNull(Drawable.loadSprite(DrawableTest.media));
                Assert.assertNotNull(Drawable.loadSprite(Core.GRAPHIC.getImageBuffer(DrawableTest.media, false)));
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
                        Core.GRAPHIC.getImageBuffer(DrawableTest.media, false), width, height));
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
                Assert.assertNotNull(Drawable.loadSpriteTiled(Core.GRAPHIC.getImageBuffer(DrawableTest.media, false),
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
     * @throws NoSuchMethodException If error.
     * @throws IllegalAccessException If error.
     * @throws InstantiationException If error.
     * @throws InvocationTargetException If success.
     */
    @Test(expected = InvocationTargetException.class)
    public void testDrawableFactory() throws NoSuchMethodException, InstantiationException, IllegalAccessException,
            InvocationTargetException
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
