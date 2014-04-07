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

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.Filter;
import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.ImageInfo;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Transparency;
import com.b3dgs.lionengine.Version;
import com.b3dgs.lionengine.anim.Anim;
import com.b3dgs.lionengine.anim.AnimState;
import com.b3dgs.lionengine.anim.Animation;
import com.b3dgs.lionengine.anim.Animator;
import com.b3dgs.lionengine.core.Engine;
import com.b3dgs.lionengine.core.ImageBuffer;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.UtilityImage;

/**
 * Test the Drawable package.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class SpriteTest
{
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
        Engine.start("SpriteTest", Version.create(1, 0, 0), "resources");
        SpriteTest.media = Media.create("dot.png");
        SpriteTest.g = UtilityImage.createImageBuffer(100, 100, Transparency.OPAQUE).createGraphic();
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        Engine.terminate();
    }

    /**
     * Ensure that the image is conform to its informations.
     * 
     * @param media The image media file.
     * @param image The image reference.
     * @return The image info instance.
     */
    private static ImageInfo assertImageInfoCorrect(Media media, Image image)
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
    private static void testImageRender(Graphic g, Image image)
    {
        try
        {
            image.render(null, 0, 0);
            Assert.fail();
        }
        catch (final NullPointerException exception)
        {
            image.render(g, 0, 0);
        }
    }

    /**
     * Test the sprite modification functions (scale, stretch, rotate, flip).
     * 
     * @param scale The scale value.
     * @param sprite The sprite to test.
     */
    private static void testSpriteModification(int scale, Sprite sprite)
    {
        final Sprite spriteOriginal = Drawable.loadSprite(sprite.getSurface());
        Assert.assertEquals(spriteOriginal, sprite);

        ImageBuffer surface = sprite.getSurface();

        if (!(sprite instanceof SpriteFont))
        {
            sprite.scale(100 * scale);
            Assert.assertNotSame(spriteOriginal, sprite);
            Assert.assertNotSame(surface, sprite.getSurface());
            Assert.assertEquals(surface.getWidth(), sprite.getWidthOriginal());
            Assert.assertEquals(surface.getWidth() * scale, sprite.getWidth());
            Assert.assertEquals(surface.getHeight(), sprite.getHeightOriginal());
            Assert.assertEquals(surface.getHeight() * scale, sprite.getHeight());
            Assert.assertTrue(sprite.equals(sprite));
        }

        surface = sprite.getSurface();
        sprite.stretch(50, 50);
        Assert.assertNotSame(surface, sprite.getSurface());

        surface = sprite.getSurface();
        sprite.filter(Filter.BILINEAR);
        Assert.assertNotSame(surface, sprite.getSurface());

        surface = sprite.getSurface();
        sprite.flipHorizontal();
        Assert.assertNotSame(surface, sprite.getSurface());

        surface = sprite.getSurface();
        sprite.flipVertical();
        Assert.assertNotSame(surface, sprite.getSurface());

        surface = sprite.getSurface();
        sprite.rotate(90);
        Assert.assertNotSame(surface, sprite.getSurface());

        surface = sprite.getSurface();
        sprite.rotate(-1);
        Assert.assertNotSame(surface, sprite.getSurface());

        surface = sprite.getSurface();
        sprite.rotate(361);
        Assert.assertNotSame(surface, sprite.getSurface());

        try
        {
            sprite.scale(0);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }
        try
        {
            sprite.stretch(1, -1);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }
        sprite.stretch(99, 101);
        sprite.stretch(100, 101);
        sprite.stretch(99, 100);
        sprite.stretch(100, 100);

        sprite.setTransparency(ColorRgba.BLACK);
        sprite.setAlpha(128);
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
            sprite.setAlpha(256);
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
        final Sprite spriteB = Drawable.loadSprite(sprite.getSurface());
        spriteB.stretch(90, 110);
        Assert.assertFalse(spriteB.equals(sprite));

        final Sprite spriteC = Drawable.loadSprite(UtilityImage.getImageBuffer(sprite.getSurface()));
        spriteC.stretch(100, 110);
        Assert.assertFalse(spriteC.equals(sprite));

        final Sprite spriteD = Drawable.loadSprite(UtilityImage.getImageBuffer(sprite.getSurface()));
        spriteD.stretch(90, 100);
        Assert.assertFalse(spriteC.equals(sprite));
    }

    /**
     * Test the sprite loading function.
     * 
     * @param sprite The sprite reference.
     */
    private static void testSpriteLoading(Sprite sprite)
    {
        Assert.assertTrue(sprite.equals(sprite));
        Assert.assertNull(sprite.getSurface());
        sprite.load(false);
        Assert.assertNotNull(sprite.getSurface());
        sprite.load(false);
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
    private static void testSpriteTiledLoadError(int tw, int th)
    {
        final Media media = Media.create("dot.png");
        try
        {
            final SpriteTiled sprite = Drawable.loadSpriteTiled(
                    UtilityImage.createImageBuffer(16, 16, Transparency.OPAQUE), tw, th);
            Assert.assertNotNull(sprite);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }
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
    private static void testSpriteAnimatedLoadError(int hf, int vf)
    {
        final Media media = Media.create("dot.png");
        try
        {
            final SpriteAnimated sprite = Drawable.loadSpriteAnimated(
                    UtilityImage.createImageBuffer(16, 16, Transparency.OPAQUE), hf, vf);
            Assert.assertNotNull(sprite);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }
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
     * Test function around the image.
     */
    @Test
    public void testImage()
    {
        try
        {
            UtilityImage.createImageBuffer(-16, 16, Transparency.OPAQUE);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }
        try
        {
            UtilityImage.createImageBuffer(16, -16, Transparency.OPAQUE);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }

        // Load unexisting image
        try
        {
            Drawable.loadImage(Media.create("void"));
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }

        final int width = 16;
        final int height = 16;
        final ImageBuffer surface = UtilityImage.createImageBuffer(width, height, Transparency.OPAQUE);
        final Image imageA = Drawable.loadImage(surface);

        Assert.assertNotNull(imageA);
        Assert.assertEquals(width, imageA.getWidth());
        Assert.assertEquals(height, imageA.getHeight());
        Assert.assertEquals(surface, imageA.getSurface());

        // Share correctly the surface
        final Image imageB = Drawable.loadImage(imageA.getSurface());
        Assert.assertEquals(imageA, imageB);
        Assert.assertEquals(imageB, Drawable.loadImage(imageB.getSurface()));

        // Load from file
        final Image imageC = Drawable.loadImage(SpriteTest.media);
        SpriteTest.assertImageInfoCorrect(SpriteTest.media, imageC);
        Assert.assertNotNull(imageC.getSurface());
        SpriteTest.testImageRender(SpriteTest.g, imageC);
        Assert.assertFalse(imageC.equals(Drawable.loadImage(SpriteTest.media)));

        // Equals
        final ImageBuffer surfaceA = UtilityImage.createImageBuffer(16, 16, Transparency.OPAQUE);
        final Image imageD = Drawable.loadImage(surfaceA);
        final ImageBuffer surfaceB = UtilityImage.createImageBuffer(16, 20, Transparency.OPAQUE);
        final Image imageE = Drawable.loadImage(surfaceB);
        final ImageBuffer surfaceC = UtilityImage.createImageBuffer(20, 16, Transparency.OPAQUE);
        final Image imageF = Drawable.loadImage(surfaceC);
        Assert.assertTrue(imageD.equals(imageD));
        Assert.assertFalse(imageD.equals(imageE));
        Assert.assertFalse(imageD.equals(imageF));
    }

    /**
     * Test function around the sprite.
     */
    @Test
    public void testSprite()
    {
        // Load unexisting sprite
        try
        {
            Drawable.loadSprite(Media.create("void"));
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }

        // Sprite with existing surface
        final ImageBuffer surface = UtilityImage.createImageBuffer(16, 16, Transparency.OPAQUE);
        final Sprite spriteA = Drawable.loadSprite(surface);

        Assert.assertNotNull(spriteA.getSurface());
        Assert.assertEquals(surface, spriteA.getSurface());

        SpriteTest.testSpriteModification(2, spriteA);
        // Sprite clone (share surface only)
        Assert.assertEquals(spriteA, Drawable.loadSprite(spriteA.getSurface()));

        // Load from file
        final Sprite spriteB = Drawable.loadSprite(SpriteTest.media);
        SpriteTest.assertImageInfoCorrect(SpriteTest.media, spriteB);

        SpriteTest.testSpriteLoading(spriteB);
        SpriteTest.testImageRender(SpriteTest.g, spriteB);
        Assert.assertFalse(spriteB.equals(Drawable.loadSprite(SpriteTest.media)));

        // Hash code
        Assert.assertTrue(spriteA.hashCode() != spriteB.hashCode());
    }

    /**
     * Test function around tiled sprite.
     */
    @Test
    public void testSpriteTiled()
    {
        final int width = 16;
        final int height = 16;
        final int tileSize = 1;
        final SpriteTiled spriteA = Drawable.loadSpriteTiled(
                UtilityImage.createImageBuffer(width, height, Transparency.OPAQUE), tileSize, tileSize);

        Assert.assertNotNull(spriteA.getSurface());
        Assert.assertEquals(tileSize, spriteA.getTileWidth());
        Assert.assertEquals(tileSize, spriteA.getTileHeight());
        Assert.assertNotNull(spriteA.getTile(0));
        Assert.assertEquals(width, spriteA.getTilesHorizontal());
        Assert.assertEquals(height, spriteA.getTilesVertical());
        Assert.assertEquals(width * height, spriteA.getTilesNumber());

        // Load from file
        final int tileWidth = 16;
        final int tileHeight = 8;
        final SpriteTiled spriteB = Drawable.loadSpriteTiled(SpriteTest.media, tileWidth, tileHeight);
        SpriteTest.assertImageInfoCorrect(SpriteTest.media, spriteB);

        Assert.assertEquals(tileWidth, spriteB.getTileWidthOriginal());
        Assert.assertEquals(tileWidth, spriteB.getTileWidth());
        Assert.assertEquals(tileHeight, spriteB.getTileHeightOriginal());
        Assert.assertEquals(tileHeight, spriteB.getTileHeight());

        SpriteTest.testSpriteLoading(spriteB);

        SpriteTest.testSpriteModification(2, spriteB);
        SpriteTest.testImageRender(SpriteTest.g, spriteB);
        spriteB.render(SpriteTest.g, 0, 0, 0);

        Assert.assertFalse(spriteA.equals(spriteB));

        // Equals
        final SpriteTiled spriteD = Drawable.loadSpriteTiled(SpriteTest.media, tileWidth, tileHeight);
        final SpriteTiled spriteE = Drawable.loadSpriteTiled(SpriteTest.media, tileWidth + 2, tileHeight + 1);
        spriteD.load(false);
        spriteE.load(false);
        Assert.assertFalse(spriteD.equals(spriteE));
        spriteE.stretch(110, 100);
        Assert.assertFalse(spriteD.equals(spriteE));
        spriteE.stretch(100, 110);
        Assert.assertFalse(spriteD.equals(spriteE));
        spriteE.stretch(110, 110);
        Assert.assertFalse(spriteD.equals(spriteE));
        final SpriteTiled spriteF = Drawable.loadSpriteTiled(spriteD.getSurface(), tileWidth, tileHeight);
        Assert.assertTrue(spriteD.equals(spriteF));

        // Get tile
        Assert.assertNotNull(spriteB.getTile(0));
        Assert.assertNotNull(spriteB.getTile(99));
        Assert.assertNotNull(spriteB.getTile(-1));

        // Error
        SpriteTest.testSpriteTiledLoadError(0, 0);
        SpriteTest.testSpriteTiledLoadError(0, 1);
        SpriteTest.testSpriteTiledLoadError(1, 0);
        Assert.assertFalse(spriteB.equals(Drawable.loadSpriteTiled(SpriteTest.media, tileWidth, tileHeight)));
    }

    /**
     * Test function around animated sprite and animation.
     */
    @Test
    public void testSpriteAnimated()
    {
        final SpriteAnimated spriteA = Drawable.loadSpriteAnimated(
                UtilityImage.createImageBuffer(16, 16, Transparency.OPAQUE), 1, 1);

        Assert.assertNotNull(spriteA.getSurface());

        final Animator animator = Anim.createAnimator();
        final Animation animation = Anim.createAnimation(1, 6, 1.0, false, false);

        // Load from file
        final int frameHorizontal = 4;
        final int frameVertical = 2;
        final SpriteAnimated spriteC = Drawable.loadSpriteAnimated(SpriteTest.media, frameHorizontal, frameVertical);
        final ImageInfo info = SpriteTest.assertImageInfoCorrect(SpriteTest.media, spriteC);

        Assert.assertEquals(frameHorizontal * frameVertical, spriteC.getFramesNumber());
        Assert.assertEquals(info.getWidth() / frameHorizontal, spriteC.getFrameWidthOriginal());
        Assert.assertEquals(info.getWidth() / frameHorizontal, spriteC.getFrameWidth());
        Assert.assertEquals(info.getHeight() / frameVertical, spriteC.getFrameHeightOriginal());
        Assert.assertEquals(info.getHeight() / frameVertical, spriteC.getFrameHeight());

        SpriteTest.testSpriteLoading(spriteC);
        SpriteTest.testSpriteModification(2, spriteA);

        // Equals
        final SpriteAnimated spriteD = Drawable.loadSpriteAnimated(SpriteTest.media, frameHorizontal, frameVertical);
        final SpriteAnimated spriteE = Drawable.loadSpriteAnimated(SpriteTest.media, frameHorizontal + 2,
                frameVertical + 1);
        spriteD.load(false);
        spriteE.load(false);
        Assert.assertFalse(spriteD.equals(spriteE));
        spriteE.stretch(110, 100);
        spriteD.setMirror(true);
        final int hash = spriteD.hashCode();
        Assert.assertFalse(spriteD.equals(spriteE));
        spriteE.stretch(100, 110);
        spriteD.setMirror(false);
        Assert.assertFalse(spriteD.equals(spriteE));
        spriteE.stretch(110, 110);
        Assert.assertTrue(hash != spriteD.hashCode());
        Assert.assertFalse(spriteD.equals(spriteE));
        final SpriteAnimated spriteF = Drawable
                .loadSpriteAnimated(spriteD.getSurface(), frameHorizontal, frameVertical);
        Assert.assertTrue(spriteD.equals(spriteF));

        try
        {
            animator.play(null);
            animator.updateAnimation(1.0);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }
        try
        {
            animator.play(animation);
            animator.updateAnimation(1.0);
        }
        catch (final LionEngineException exception)
        {
            Assert.fail();
        }

        // Test render
        SpriteTest.testImageRender(SpriteTest.g, spriteC);
        spriteC.setMirror(true);
        spriteC.render(SpriteTest.g, 0, 0);

        // Error
        SpriteTest.testSpriteAnimatedLoadError(0, 0);
        SpriteTest.testSpriteAnimatedLoadError(0, 1);
        SpriteTest.testSpriteAnimatedLoadError(1, 0);

        // Animations
        spriteA.play(animation);
        spriteA.setAnimSpeed(1.0);
        spriteA.updateAnimation(1.0);
        spriteA.stopAnimation();
        spriteA.setFrame(1);
        Assert.assertEquals(1, spriteA.getFrame());
        Assert.assertEquals(1, spriteA.getFrameAnim());
        Assert.assertEquals(AnimState.STOPPED, spriteA.getAnimState());

        Assert.assertNotNull(spriteA.getFrame(1));
        Assert.assertNotNull(spriteA.getFrame(-1));
        Assert.assertNotNull(spriteA.getFrame(0));
        Assert.assertFalse(spriteC.equals(Drawable.loadSpriteAnimated(SpriteTest.media, frameHorizontal, frameVertical)));
    }

    /**
     * Test parallax sprite.
     */
    @Test
    public void testParallax()
    {
        final ImageInfo info = ImageInfo.get(SpriteTest.media);
        final int lines = 8;
        final SpriteParallaxed spriteA = Drawable.loadSpriteParallaxed(SpriteTest.media, lines, 60, 100);

        Assert.assertEquals(0, spriteA.getWidthOriginal());
        Assert.assertEquals(0, spriteA.getHeightOriginal());

        spriteA.prepare(Filter.NONE);
        Assert.assertTrue(spriteA.equals(spriteA));
        Assert.assertEquals((int) (spriteA.getWidthOriginal() * 0.6), spriteA.getWidth());
        Assert.assertEquals(info.getWidth(), spriteA.getWidthOriginal());
        Assert.assertEquals(info.getHeight() / lines, spriteA.getHeight());

        for (int i = 0; i < lines; i++)
        {
            Assert.assertNotNull(spriteA.getLine(i));
            spriteA.render(SpriteTest.g, i, 0, 0);
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
        spriteA.render(SpriteTest.g, 0, 0);

        // Resize
        final SpriteParallaxed spriteB = Drawable.loadSpriteParallaxed(SpriteTest.media, lines, 60, 100);
        spriteB.scale(200);
        spriteB.prepare(Filter.BILINEAR);
        Assert.assertEquals(info.getWidth(), spriteB.getWidthOriginal());
        Assert.assertFalse(spriteB.equals(spriteA));
        Assert.assertTrue(spriteA.hashCode() != spriteB.hashCode());
        Assert.assertFalse(spriteA.equals(SpriteTest.media));

        // Error case
        try
        {
            final SpriteParallaxed spriteC = Drawable.loadSpriteParallaxed(SpriteTest.media, 0, 60, 100);
            Assert.assertNotNull(spriteC);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }
        try
        {
            final SpriteParallaxed spriteC = Drawable.loadSpriteParallaxed(SpriteTest.media, lines, 60, 0);
            Assert.assertNotNull(spriteC);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }
        try
        {
            final SpriteParallaxed spriteC = Drawable.loadSpriteParallaxed(SpriteTest.media, lines, 0, 60);
            Assert.assertNotNull(spriteC);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }
    }

    /**
     * Test the sprite font class.
     */
    @Test
    public void testSpriteFont()
    {
        final Media fontData = Media.create("fontdata.xml");
        final String text = "a%z";
        final SpriteFont sprite = Drawable.loadSpriteFont(SpriteTest.media, fontData, 6, 7);
        Assert.assertTrue(sprite.equals(Drawable.loadSpriteFont(SpriteTest.media, fontData, 6, 7)));

        final ImageInfo info = SpriteTest.assertImageInfoCorrect(SpriteTest.media, sprite);
        Assert.assertEquals(info.getWidth(), sprite.getWidthOriginal());
        Assert.assertEquals(info.getHeight(), sprite.getHeightOriginal());

        SpriteTest.testSpriteLoading(sprite);
        SpriteTest.testImageRender(SpriteTest.g, sprite);
        SpriteTest.testSpriteModification(2, sprite);
        sprite.scale(200);

        sprite.draw(SpriteTest.g, 0, 0, Align.LEFT, text);
        sprite.draw(SpriteTest.g, 0, 0, Align.CENTER, text);
        sprite.draw(SpriteTest.g, 0, 0, Align.RIGHT, text);
        sprite.setLineHeight(0);
        Assert.assertTrue(sprite.getTextWidth(text) >= 1);
        Assert.assertTrue(sprite.getTextHeight(text) >= 0);

        Assert.assertFalse(sprite.equals(Drawable.loadSpriteFont(SpriteTest.media, fontData, 6, 7)));

        sprite.stretch(90, 110);
        Assert.assertFalse(sprite.equals(Drawable.loadSpriteFont(SpriteTest.media, fontData, 6, 7)));
    }
}
