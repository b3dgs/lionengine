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
package com.b3dgs.lionengine.test.util;

import org.junit.Assert;

import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Filter;
import com.b3dgs.lionengine.ImageInfo;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Transparency;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.Graphics;
import com.b3dgs.lionengine.core.ImageBuffer;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.Image;
import com.b3dgs.lionengine.drawable.Sprite;
import com.b3dgs.lionengine.drawable.SpriteAnimated;
import com.b3dgs.lionengine.drawable.SpriteFont;
import com.b3dgs.lionengine.drawable.SpriteTiled;

/**
 * Test tools set for image testing.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class DrawableTestTool
{
    /**
     * Ensure that the image is conform to its informations.
     * 
     * @param media The image media file.
     * @param image The image reference.
     * @return The image info instance.
     */
    public static ImageInfo assertImageInfoCorrect(Media media, Image image)
    {
        final ImageInfo info = ImageInfo.get(media);
        Assert.assertNotNull(info);
        Assert.assertNotNull(image);
        Assert.assertEquals(info.getWidth(), image.getWidth());
        Assert.assertEquals(info.getHeight(), image.getHeight());
        Assert.assertEquals("png", info.getFormat());
        Assert.assertTrue(image.equals(image));
        Assert.assertFalse(image.equals(info));
        Assert.assertTrue(image.hashCode() != info.hashCode());
        return info;
    }

    /**
     * Test the image rendering.
     * 
     * @param g The graphic output.
     * @param image The image to test.
     */
    public static void testImageRender(Graphic g, Image image)
    {
        try
        {
            image.render(null);
            Assert.fail();
        }
        catch (final NullPointerException exception)
        {
            image.render(g);
        }
    }

    /**
     * Test the sprite modification functions (scale, stretch, rotate, flip).
     * 
     * @param scale The scale value.
     * @param sprite The sprite to test.
     */
    public static void testSpriteModification(int scale, Sprite sprite)
    {
        final Sprite spriteOriginal = Drawable.loadSprite(sprite.getSurface());
        Assert.assertEquals(spriteOriginal, sprite);

        testSurface(sprite);

        try
        {
            sprite.stretch(1, -1);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }
        sprite.stretch(Constant.HUNDRED - 1, Constant.HUNDRED + 1);
        sprite.stretch(Constant.HUNDRED, Constant.HUNDRED + 1);
        sprite.stretch(Constant.HUNDRED - 1, Constant.HUNDRED);
        sprite.stretch(Constant.HUNDRED, Constant.HUNDRED);

        sprite.setTransparency(ColorRgba.BLACK);
        sprite.setAlpha(Constant.UNSIGNED_BYTE / 2);
        sprite.setAlpha(0);

        try
        {
            sprite.setAlpha(-1);
        }
        catch (final LionEngineException exception)
        {
            // Success
        }
        try
        {
            sprite.setAlpha(Constant.UNSIGNED_BYTE);
        }
        catch (final LionEngineException exception)
        {
            // Success
        }

        if (!(sprite instanceof SpriteFont))
        {
            sprite.stretch(100, 100 * scale);
            Assert.assertNotSame(spriteOriginal, sprite);
        }

        // Equality
        final int lower = 90;
        final int upper = 110;

        final Sprite spriteB = Drawable.loadSprite(sprite.getSurface());
        spriteB.stretch(lower, upper);
        Assert.assertFalse(spriteB.equals(sprite));

        final Sprite spriteC = Drawable.loadSprite(Graphics.getImageBuffer(sprite.getSurface()));
        spriteC.stretch(100, upper);
        Assert.assertFalse(spriteC.equals(sprite));

        final Sprite spriteD = Drawable.loadSprite(Graphics.getImageBuffer(sprite.getSurface()));
        spriteD.stretch(lower, 100);
        Assert.assertFalse(spriteC.equals(sprite));
    }

    /**
     * Test the sprite loading function.
     * 
     * @param sprite The sprite reference.
     */
    public static void testSpriteLoading(Sprite sprite)
    {
        Assert.assertTrue(sprite.equals(sprite));
        Assert.assertNull(sprite.getSurface());
        Assert.assertFalse(sprite.isLoaded());
        sprite.load();

        try
        {
            sprite.load();
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }

        Assert.assertNotNull(sprite.getSurface());
        sprite.prepare();
        Assert.assertTrue(sprite.isLoaded());
        Assert.assertNotNull(sprite.getSurface());
        Assert.assertTrue(sprite.equals(sprite));
        Assert.assertTrue(Drawable.loadSprite(sprite.getSurface()).equals(sprite));
        if (!(sprite instanceof SpriteTiled || sprite instanceof SpriteAnimated || sprite instanceof SpriteFont))
        {
            Assert.assertTrue(sprite.equals(Drawable.loadSprite(sprite.getSurface())));
        }
        else
        {
            Assert.assertFalse(sprite.equals(Drawable.loadSprite(sprite.getSurface())));
        }
    }

    /**
     * Test sprite tiled loading error.
     * 
     * @param tw The tile width.
     * @param th The tile height.
     */
    public static void testSpriteTiledLoadError(int tw, int th)
    {
        try
        {
            final ImageBuffer buffer = Graphics.createImageBuffer(16, 16, Transparency.OPAQUE);
            final SpriteTiled sprite = Drawable.loadSpriteTiled(buffer, tw, th);
            Assert.assertNotNull(sprite);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }
        final Media media = Medias.create("none.png");
        try
        {
            final SpriteTiled sprite = Drawable.loadSpriteTiled(media, tw, th);
            Assert.assertNotNull(sprite);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }
    }

    /**
     * Test sprite animated loading error.
     * 
     * @param hf The tile width.
     * @param vf The tile height.
     */
    public static void testSpriteAnimatedLoadError(int hf, int vf)
    {
        try
        {
            final ImageBuffer buffer = Graphics.createImageBuffer(16, 16, Transparency.OPAQUE);
            final SpriteAnimated sprite = Drawable.loadSpriteAnimated(buffer, hf, vf);
            Assert.assertNotNull(sprite);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }
        final Media media = Medias.create("dot.png");
        try
        {
            final SpriteAnimated sprite = Drawable.loadSpriteAnimated(media, hf, vf);
            Assert.assertNotNull(sprite);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }
    }

    /**
     * Test the sprite surface.
     * 
     * @param sprite The sprite reference.
     */
    private static void testSurface(Sprite sprite)
    {
        ImageBuffer surface = sprite.getSurface();

        surface = sprite.getSurface();
        sprite.stretch(Constant.HUNDRED / 2, Constant.HUNDRED / 2);
        Assert.assertNotSame(surface, sprite.getSurface());

        surface = sprite.getSurface();
        sprite.filter(Filter.BILINEAR);
        Assert.assertNotSame(surface, sprite.getSurface());

        surface = sprite.getSurface();
        sprite.rotate(Constant.MAX_DEGREE / (2 * 2));
        Assert.assertNotSame(surface, sprite.getSurface());

        surface = sprite.getSurface();
        sprite.rotate(-1);
        Assert.assertNotSame(surface, sprite.getSurface());

        surface = sprite.getSurface();
        sprite.rotate(Constant.MAX_DEGREE + 1);
        Assert.assertNotSame(surface, sprite.getSurface());
    }

    /**
     * Private constructor.
     */
    private DrawableTestTool()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
