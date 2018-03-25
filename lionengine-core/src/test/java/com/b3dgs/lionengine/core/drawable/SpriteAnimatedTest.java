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

import com.b3dgs.lionengine.AnimState;
import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.ViewerMock;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.core.filter.FilterBilinear;
import com.b3dgs.lionengine.geom.Geom;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.FactoryGraphicMock;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.ImageBuffer;
import com.b3dgs.lionengine.graphic.SpriteAnimated;
import com.b3dgs.lionengine.util.UtilTests;

/**
 * Test {@link SpriteAnimated}.
 */
public final class SpriteAnimatedTest
{
    /** Image media. */
    private static Media media;

    /**
     * Prepare test.
     */
    @BeforeClass
    public static void setUp()
    {
        Graphics.setFactoryGraphic(new FactoryGraphicMock());
        Medias.setLoadFromJar(SpriteAnimatedTest.class);

        media = Medias.create("image.png");
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        Graphics.setFactoryGraphic(null);
        Medias.setLoadFromJar(null);
    }

    /**
     * Test constructor with media.
     */
    @Test
    public void testConstructorMedia()
    {
        final SpriteAnimated sprite = new SpriteAnimatedImpl(media, 16, 8);

        Assert.assertFalse(sprite.isLoaded());
        Assert.assertNull(sprite.getSurface());
        Assert.assertEquals(64, sprite.getWidth());
        Assert.assertEquals(32, sprite.getHeight());
        Assert.assertEquals(4, sprite.getTileWidth());
        Assert.assertEquals(4, sprite.getTileHeight());
        Assert.assertEquals(16, sprite.getFramesHorizontal());
        Assert.assertEquals(8, sprite.getFramesVertical());
    }

    /**
     * Test constructor with surface.
     */
    @Test
    public void testConstructorSurface()
    {
        final ImageBuffer surface = Graphics.createImageBuffer(64, 32);
        final SpriteAnimated sprite = new SpriteAnimatedImpl(surface, 16, 8);

        Assert.assertTrue(sprite.isLoaded());
        Assert.assertEquals(surface, sprite.getSurface());
        Assert.assertEquals(64, sprite.getWidth());
        Assert.assertEquals(32, sprite.getHeight());
        Assert.assertEquals(4, sprite.getTileWidth());
        Assert.assertEquals(4, sprite.getTileHeight());
        Assert.assertEquals(16, sprite.getFramesHorizontal());
        Assert.assertEquals(8, sprite.getFramesVertical());
    }

    /**
     * Test load with media.
     */
    @Test
    public void testLoadMedia()
    {
        final SpriteAnimated sprite = new SpriteAnimatedImpl(media, 16, 8);
        sprite.load();

        Assert.assertNotNull(sprite.getSurface());

        sprite.prepare();
        sprite.dispose();
    }

    /**
     * Test load with media already loaded.
     */
    @Test(expected = LionEngineException.class)
    public void testLoadMediaAlready()
    {
        final SpriteAnimated sprite = new SpriteAnimatedImpl(media, 16, 8);
        sprite.load();
        sprite.load();
    }

    /**
     * Test load with surface.
     */
    @Test(expected = LionEngineException.class)
    public void testLoadSurface()
    {
        final SpriteAnimated sprite = new SpriteAnimatedImpl(Graphics.createImageBuffer(64, 32), 16, 8);
        sprite.load();
    }

    /**
     * Test stretch sprite.
     */
    @Test
    public void testStretch()
    {
        final SpriteAnimated sprite = new SpriteAnimatedImpl(Graphics.createImageBuffer(64, 32), 16, 8);
        sprite.stretch(100.0, 100.0);

        Assert.assertEquals(64, sprite.getWidth());
        Assert.assertEquals(32, sprite.getHeight());
        Assert.assertEquals(4, sprite.getTileWidth());
        Assert.assertEquals(4, sprite.getTileHeight());

        sprite.stretch(200.0, 100.0);

        Assert.assertEquals(128, sprite.getWidth());
        Assert.assertEquals(32, sprite.getHeight());
        Assert.assertEquals(8, sprite.getTileWidth());
        Assert.assertEquals(4, sprite.getTileHeight());

        sprite.stretch(100.0, 200.0);

        Assert.assertEquals(128, sprite.getWidth());
        Assert.assertEquals(64, sprite.getHeight());
        Assert.assertEquals(8, sprite.getTileWidth());
        Assert.assertEquals(8, sprite.getTileHeight());

        sprite.stretch(200.0, 200.0);

        Assert.assertEquals(256, sprite.getWidth());
        Assert.assertEquals(128, sprite.getHeight());
        Assert.assertEquals(16, sprite.getTileWidth());
        Assert.assertEquals(16, sprite.getTileHeight());
    }

    /**
     * Test stretch sprite with invalid width.
     */
    @Test(expected = LionEngineException.class)
    public void testStretchInvalidWidth()
    {
        final SpriteAnimated sprite = new SpriteAnimatedImpl(Graphics.createImageBuffer(64, 32), 16, 8);
        sprite.stretch(0.0, 100.0);
    }

    /**
     * Test stretch sprite with invalid height.
     */
    @Test(expected = LionEngineException.class)
    public void testStretchInvalidHeight()
    {
        final SpriteAnimated sprite = new SpriteAnimatedImpl(Graphics.createImageBuffer(64, 32), 16, 8);
        sprite.stretch(100, 0.0);
    }

    /**
     * Test rotate sprite.
     */
    @Test
    public void testRotate()
    {
        final SpriteAnimated sprite = new SpriteAnimatedImpl(Graphics.createImageBuffer(64, 32), 16, 8);
        for (int angle = -720; angle < 720; angle++)
        {
            sprite.rotate(angle);
            Assert.assertTrue(angle + Constant.SPACE + sprite.getWidth(), sprite.getWidth() >= 64);
            Assert.assertTrue(angle + Constant.SPACE + sprite.getHeight(), sprite.getHeight() >= 32);
        }
    }

    /**
     * Test set location.
     */
    @Test
    public void testSetLocation()
    {
        final SpriteAnimatedImpl sprite = new SpriteAnimatedImpl(Graphics.createImageBuffer(64, 32), 16, 8);

        Assert.assertEquals(0.0, sprite.getX(), UtilTests.PRECISION);
        Assert.assertEquals(0.0, sprite.getY(), UtilTests.PRECISION);
        Assert.assertEquals(0, sprite.getRenderX());
        Assert.assertEquals(0, sprite.getRenderY());

        sprite.setLocation(1.5, 2.5);

        Assert.assertEquals(1.5, sprite.getX(), UtilTests.PRECISION);
        Assert.assertEquals(2.5, sprite.getY(), UtilTests.PRECISION);
        Assert.assertEquals(1, sprite.getRenderX());
        Assert.assertEquals(2, sprite.getRenderY());
    }

    /**
     * Test set location with viewer.
     */
    @Test
    public void testSetLocationViewer()
    {
        final SpriteAnimatedImpl sprite = new SpriteAnimatedImpl(Graphics.createImageBuffer(64, 32), 16, 8);
        final ViewerMock viewer = new ViewerMock();
        sprite.setLocation(viewer, Geom.createLocalizable(1.5, 2.5));

        Assert.assertEquals(1.5, sprite.getX(), UtilTests.PRECISION);
        Assert.assertEquals(237.5, sprite.getY(), UtilTests.PRECISION);
        Assert.assertEquals(1, sprite.getRenderX());
        Assert.assertEquals(237, sprite.getRenderY());

        viewer.set(10, 20);
        sprite.setLocation(viewer, Geom.createLocalizable(1.5, 2.5));

        Assert.assertEquals(-8.5, sprite.getX(), UtilTests.PRECISION);
        Assert.assertEquals(257.5, sprite.getY(), UtilTests.PRECISION);
        Assert.assertEquals(-9, sprite.getRenderX());
        Assert.assertEquals(257, sprite.getRenderY());
    }

    /**
     * Test set alpha.
     */
    @Test
    public void testSetAlpha()
    {
        final SpriteAnimated sprite = new SpriteAnimatedImpl(Graphics.createImageBuffer(64, 32), 16, 8);
        for (int alpha = 0; alpha < 256; alpha++)
        {
            sprite.setAlpha(alpha);

            Assert.assertEquals(64, sprite.getWidth());
            Assert.assertEquals(32, sprite.getHeight());
        }
    }

    /**
     * Test set alpha too low.
     */
    @Test(expected = LionEngineException.class)
    public void testSetAlphaLow()
    {
        final SpriteAnimated sprite = new SpriteAnimatedImpl(Graphics.createImageBuffer(64, 32), 16, 8);
        sprite.setAlpha(-1);
    }

    /**
     * Test set alpha too high.
     */
    @Test(expected = LionEngineException.class)
    public void testSetAlphaHigh()
    {
        final SpriteAnimated sprite = new SpriteAnimatedImpl(Graphics.createImageBuffer(64, 32), 16, 8);
        sprite.setAlpha(256);
    }

    /**
     * Test set transparency.
     */
    @Test
    public void testSetTransparency()
    {
        final SpriteAnimated sprite = new SpriteAnimatedImpl(Graphics.createImageBuffer(64, 32), 16, 8);
        sprite.setTransparency(ColorRgba.BLACK);

        Assert.assertEquals(64, sprite.getWidth());
        Assert.assertEquals(32, sprite.getHeight());
    }

    /**
     * Test set fade.
     */
    @Test
    public void testSetFade()
    {
        final SpriteAnimated sprite = new SpriteAnimatedImpl(Graphics.createImageBuffer(64, 32), 16, 8);
        sprite.setFade(128, 128);
        sprite.setFade(128, 128);

        Assert.assertEquals(64, sprite.getWidth());
        Assert.assertEquals(32, sprite.getHeight());
    }

    /**
     * Test filter bilinear.
     */
    @Test
    public void testFilterBilinear()
    {
        final SpriteAnimated sprite = new SpriteAnimatedImpl(Graphics.createImageBuffer(64, 32), 16, 8);
        sprite.filter(new FilterBilinear());

        Assert.assertEquals(64, sprite.getWidth());
        Assert.assertEquals(32, sprite.getHeight());
    }

    /**
     * Test filter <code>null</code>.
     */
    @Test(expected = LionEngineException.class)
    public void testFilterNull()
    {
        final SpriteAnimated sprite = new SpriteAnimatedImpl(Graphics.createImageBuffer(64, 32), 16, 8);
        sprite.filter(null);
    }

    /**
     * Test mirror.
     */
    @Test
    public void testMirror()
    {
        final SpriteAnimated sprite = new SpriteAnimatedImpl(Graphics.createImageBuffer(64, 32), 16, 8);

        Assert.assertEquals(Mirror.NONE, sprite.getMirror());

        sprite.setMirror(Mirror.HORIZONTAL);

        Assert.assertEquals(Mirror.HORIZONTAL, sprite.getMirror());
    }

    /**
     * Test mirror <code>null</code>.
     */
    @Test(expected = LionEngineException.class)
    public void testMirrorNull()
    {
        final SpriteAnimated sprite = new SpriteAnimatedImpl(Graphics.createImageBuffer(64, 32), 16, 8);
        sprite.setMirror(null);
    }

    /**
     * Test rendering point.
     */
    @Test
    public void testRenderingPoint()
    {
        final SpriteAnimatedImpl sprite = new SpriteAnimatedImpl(Graphics.createImageBuffer(10, 20), 10, 20);
        sprite.setLocation(5.0, 10.0);
        sprite.setOrigin(Origin.TOP_LEFT);

        Assert.assertEquals(5, sprite.getRenderX());
        Assert.assertEquals(10, sprite.getRenderY());

        sprite.setOrigin(Origin.MIDDLE);

        Assert.assertEquals(4, sprite.getRenderX());
        Assert.assertEquals(9, sprite.getRenderY());
    }

    /**
     * Test origin <code>null</code>.
     */
    @Test(expected = LionEngineException.class)
    public void testSetOriginNull()
    {
        final SpriteAnimated sprite = new SpriteAnimatedImpl(Graphics.createImageBuffer(64, 32), 16, 8);
        sprite.setOrigin(null);
    }

    /**
     * Set set frame with invalid value.
     */
    @Test(expected = LionEngineException.class)
    public void testSetTileInvalid()
    {
        final SpriteAnimated sprite = new SpriteAnimatedImpl(Graphics.createImageBuffer(64, 32), 16, 8);
        sprite.setFrame(-1);
    }

    /**
     * Test set frame offset.
     */
    @Test
    public void testSetFrameOffset()
    {
        final SpriteAnimated sprite = new SpriteAnimatedImpl(Graphics.createImageBuffer(64, 32), 16, 8);
        sprite.setFrameOffsets(1, -1);

        Assert.assertEquals(1, sprite.getFrameOffsetX());
        Assert.assertEquals(-1, sprite.getFrameOffsetY());
    }

    /**
     * Test set speed.
     */
    @Test
    public void testSetSpeed()
    {
        final Animation animation = new Animation(Animation.DEFAULT_NAME, 1, 3, 1.0, false, false);
        final SpriteAnimated sprite = new SpriteAnimatedImpl(Graphics.createImageBuffer(64, 32), 16, 8);
        sprite.play(animation);
        sprite.setAnimSpeed(2.0);
        sprite.update(1.0);

        Assert.assertEquals(AnimState.PLAYING, sprite.getAnimState());
        Assert.assertEquals(3, sprite.getFrame());
        Assert.assertEquals(3, sprite.getFrameAnim());
    }

    /**
     * Test invalid speed setter.
     */
    @Test(expected = LionEngineException.class)
    public void testSetSpeedNegative()
    {
        final SpriteAnimated sprite = new SpriteAnimatedImpl(Graphics.createImageBuffer(64, 32), 16, 8);
        sprite.setAnimSpeed(-1.0);
    }

    /**
     * Test play.
     */
    @Test
    public void testPlay()
    {
        final Animation animation = new Animation(Animation.DEFAULT_NAME, 1, 2, 3.0, false, false);
        final SpriteAnimated sprite = new SpriteAnimatedImpl(Graphics.createImageBuffer(64, 32), 16, 8);
        sprite.play(animation);

        Assert.assertEquals(AnimState.PLAYING, sprite.getAnimState());
        Assert.assertEquals(1, sprite.getFrame());
        Assert.assertEquals(1, sprite.getFrameAnim());
    }

    /**
     * Test stop.
     */
    @Test
    public void testStop()
    {
        final SpriteAnimated sprite = new SpriteAnimatedImpl(Graphics.createImageBuffer(64, 32), 16, 8);
        sprite.stop();

        Assert.assertEquals(AnimState.STOPPED, sprite.getAnimState());
    }

    /**
     * Test update without loop nor reverse.
     */
    @Test
    public void testUpdateNoLoopNoReverse()
    {
        final Animation animation = new Animation(Animation.DEFAULT_NAME, 1, 2, 1.0, false, false);
        final SpriteAnimated sprite = new SpriteAnimatedImpl(Graphics.createImageBuffer(64, 32), 16, 8);
        sprite.play(animation);

        Assert.assertEquals(AnimState.PLAYING, sprite.getAnimState());
        Assert.assertEquals(1, sprite.getFrame());
        Assert.assertEquals(1, sprite.getFrameAnim());

        sprite.update(1.0);

        Assert.assertEquals(AnimState.PLAYING, sprite.getAnimState());
        Assert.assertEquals(2, sprite.getFrame());
        Assert.assertEquals(2, sprite.getFrameAnim());

        sprite.update(1.0);

        Assert.assertEquals(AnimState.FINISHED, sprite.getAnimState());
        Assert.assertEquals(2, sprite.getFrame());
        Assert.assertEquals(2, sprite.getFrameAnim());

        sprite.update(1.0);

        Assert.assertEquals(AnimState.FINISHED, sprite.getAnimState());
        Assert.assertEquals(2, sprite.getFrame());
        Assert.assertEquals(2, sprite.getFrameAnim());
    }

    /**
     * Test update with loop but no reverse.
     */
    @Test
    public void testUpdateLoopNoReverse()
    {
        final Animation animation = new Animation(Animation.DEFAULT_NAME, 1, 3, 1.0, false, true);
        final SpriteAnimated sprite = new SpriteAnimatedImpl(Graphics.createImageBuffer(64, 32), 16, 8);
        sprite.play(animation);

        Assert.assertEquals(AnimState.PLAYING, sprite.getAnimState());
        Assert.assertEquals(1, sprite.getFrame());
        Assert.assertEquals(1, sprite.getFrameAnim());

        sprite.update(1.0);

        Assert.assertEquals(AnimState.PLAYING, sprite.getAnimState());
        Assert.assertEquals(2, sprite.getFrame());
        Assert.assertEquals(2, sprite.getFrameAnim());

        sprite.update(1.0);

        Assert.assertEquals(AnimState.PLAYING, sprite.getAnimState());
        Assert.assertEquals(3, sprite.getFrame());
        Assert.assertEquals(3, sprite.getFrameAnim());

        sprite.update(1.0);

        Assert.assertEquals(AnimState.PLAYING, sprite.getAnimState());
        Assert.assertEquals(1, sprite.getFrame());
        Assert.assertEquals(1, sprite.getFrameAnim());

        sprite.update(1.0);

        Assert.assertEquals(AnimState.PLAYING, sprite.getAnimState());
        Assert.assertEquals(2, sprite.getFrame());
        Assert.assertEquals(2, sprite.getFrameAnim());
    }

    /**
     * Test update without loop but reverse.
     */
    @Test
    public void testUpdateNoLoopReverse()
    {
        final Animation animation = new Animation(Animation.DEFAULT_NAME, 1, 3, 1.0, true, false);
        final SpriteAnimated sprite = new SpriteAnimatedImpl(Graphics.createImageBuffer(64, 32), 16, 8);
        sprite.play(animation);

        Assert.assertEquals(AnimState.PLAYING, sprite.getAnimState());
        Assert.assertEquals(1, sprite.getFrame());
        Assert.assertEquals(1, sprite.getFrameAnim());

        sprite.update(1.0);

        Assert.assertEquals(AnimState.PLAYING, sprite.getAnimState());
        Assert.assertEquals(2, sprite.getFrame());
        Assert.assertEquals(2, sprite.getFrameAnim());

        sprite.update(1.0);

        Assert.assertEquals(AnimState.PLAYING, sprite.getAnimState());
        Assert.assertEquals(3, sprite.getFrame());
        Assert.assertEquals(3, sprite.getFrameAnim());

        sprite.update(1.0);

        Assert.assertEquals(AnimState.REVERSING, sprite.getAnimState());
        Assert.assertEquals(2, sprite.getFrame());
        Assert.assertEquals(2, sprite.getFrameAnim());

        sprite.update(1.0);

        Assert.assertEquals(AnimState.REVERSING, sprite.getAnimState());
        Assert.assertEquals(1, sprite.getFrame());
        Assert.assertEquals(1, sprite.getFrameAnim());

        sprite.update(1.0);

        Assert.assertEquals(AnimState.FINISHED, sprite.getAnimState());
        Assert.assertEquals(1, sprite.getFrame());
        Assert.assertEquals(1, sprite.getFrameAnim());
    }

    /**
     * Test update with loop and reverse.
     */
    @Test
    public void testUpdateLoopReverse()
    {
        final Animation animation = new Animation(Animation.DEFAULT_NAME, 1, 3, 1.0, true, true);
        final SpriteAnimated sprite = new SpriteAnimatedImpl(Graphics.createImageBuffer(64, 32), 16, 8);
        sprite.play(animation);

        Assert.assertEquals(AnimState.PLAYING, sprite.getAnimState());
        Assert.assertEquals(1, sprite.getFrame());
        Assert.assertEquals(1, sprite.getFrameAnim());

        sprite.update(1.0);

        Assert.assertEquals(AnimState.PLAYING, sprite.getAnimState());
        Assert.assertEquals(2, sprite.getFrame());
        Assert.assertEquals(2, sprite.getFrameAnim());

        sprite.update(1.0);

        Assert.assertEquals(AnimState.PLAYING, sprite.getAnimState());
        Assert.assertEquals(3, sprite.getFrame());
        Assert.assertEquals(3, sprite.getFrameAnim());

        sprite.update(1.0);

        Assert.assertEquals(AnimState.REVERSING, sprite.getAnimState());
        Assert.assertEquals(2, sprite.getFrame());
        Assert.assertEquals(2, sprite.getFrameAnim());

        sprite.update(1.0);

        Assert.assertEquals(AnimState.REVERSING, sprite.getAnimState());
        Assert.assertEquals(1, sprite.getFrame());
        Assert.assertEquals(1, sprite.getFrameAnim());

        sprite.update(1.0);

        Assert.assertEquals(AnimState.PLAYING, sprite.getAnimState());
        Assert.assertEquals(2, sprite.getFrame());
        Assert.assertEquals(2, sprite.getFrameAnim());

        sprite.update(1.0);

        Assert.assertEquals(AnimState.PLAYING, sprite.getAnimState());
        Assert.assertEquals(3, sprite.getFrame());
        Assert.assertEquals(3, sprite.getFrameAnim());

        sprite.update(1.0);

        Assert.assertEquals(AnimState.REVERSING, sprite.getAnimState());
        Assert.assertEquals(2, sprite.getFrame());
        Assert.assertEquals(2, sprite.getFrameAnim());
    }

    /**
     * Test render.
     */
    @Test
    public void testRender()
    {
        final Graphic g = Graphics.createImageBuffer(100, 100).createGraphic();
        try
        {
            final SpriteAnimated sprite = new SpriteAnimatedImpl(Graphics.createImageBuffer(64, 32), 16, 8);
            sprite.render(g);
            sprite.setFrame(1);

            sprite.setMirror(Mirror.HORIZONTAL);
            sprite.render(g);

            sprite.setMirror(Mirror.VERTICAL);
            sprite.render(g);
        }
        finally
        {
            g.dispose();
        }
    }

    /**
     * Test equals.
     */
    @Test
    public void testEquals()
    {
        final ImageBuffer surface = Graphics.createImageBuffer(64, 32);
        final SpriteAnimated sprite = new SpriteAnimatedImpl(surface, 16, 8);
        final SpriteAnimated spriteMedia = new SpriteAnimatedImpl(media, 16, 8);
        spriteMedia.load();

        Assert.assertEquals(sprite, sprite);
        Assert.assertEquals(sprite, new SpriteAnimatedImpl(surface, 16, 8));
        Assert.assertEquals(spriteMedia, spriteMedia);

        Assert.assertNotEquals(sprite, null);
        Assert.assertNotEquals(sprite, new Object());
        Assert.assertNotEquals(sprite, new SpriteAnimatedImpl(media, 16, 8));
        Assert.assertNotEquals(spriteMedia, new SpriteAnimatedImpl(media, 16, 8));
        Assert.assertNotEquals(sprite, new SpriteAnimatedImpl(surface, 16, 16));
        Assert.assertNotEquals(sprite, new SpriteAnimatedImpl(Graphics.createImageBuffer(64, 32), 16, 8));
        Assert.assertNotEquals(sprite, new SpriteAnimatedImpl(Graphics.createImageBuffer(32, 32), 16, 8));
        Assert.assertNotEquals(sprite, new SpriteAnimatedImpl(Graphics.createImageBuffer(64, 64), 16, 8));
        Assert.assertNotEquals(sprite, new SpriteAnimatedImpl(Graphics.createImageBuffer(32, 64), 16, 8));
        Assert.assertNotEquals(sprite, new SpriteAnimatedImpl(Graphics.createImageBuffer(64, 32), 8, 8));
        Assert.assertNotEquals(sprite, new SpriteAnimatedImpl(Graphics.createImageBuffer(64, 32), 16, 16));
        Assert.assertNotEquals(sprite, new SpriteAnimatedImpl(Graphics.createImageBuffer(32, 32), 8, 8));
        Assert.assertNotEquals(sprite, new SpriteAnimatedImpl(Graphics.createImageBuffer(64, 64), 16, 16));
    }

    /**
     * Test hash code.
     */
    @Test
    public void testHashCode()
    {
        final ImageBuffer surface = Graphics.createImageBuffer(64, 32);
        final int sprite = new SpriteAnimatedImpl(surface, 16, 8).hashCode();
        final SpriteAnimated spriteMedia = new SpriteAnimatedImpl(media, 16, 8);
        spriteMedia.load();

        Assert.assertEquals(sprite, new SpriteAnimatedImpl(surface, 16, 8).hashCode());

        Assert.assertNotEquals(sprite, new Object().hashCode());
        Assert.assertNotEquals(spriteMedia.hashCode(), new SpriteAnimatedImpl(media, 16, 8).hashCode());
        Assert.assertNotEquals(sprite, new SpriteAnimatedImpl(Graphics.createImageBuffer(64, 32), 16, 8).hashCode());
        Assert.assertNotEquals(sprite, new SpriteAnimatedImpl(Graphics.createImageBuffer(32, 32), 16, 8).hashCode());
        Assert.assertNotEquals(sprite, new SpriteAnimatedImpl(Graphics.createImageBuffer(64, 64), 16, 8).hashCode());
        Assert.assertNotEquals(sprite, new SpriteAnimatedImpl(Graphics.createImageBuffer(32, 64), 16, 8).hashCode());
        Assert.assertNotEquals(sprite, new SpriteAnimatedImpl(Graphics.createImageBuffer(64, 32), 8, 8).hashCode());
        Assert.assertNotEquals(sprite, new SpriteAnimatedImpl(Graphics.createImageBuffer(64, 32), 16, 16).hashCode());
        Assert.assertNotEquals(sprite, new SpriteAnimatedImpl(Graphics.createImageBuffer(32, 32), 8, 8).hashCode());
        Assert.assertNotEquals(sprite, new SpriteAnimatedImpl(Graphics.createImageBuffer(64, 64), 16, 16).hashCode());
    }
}
