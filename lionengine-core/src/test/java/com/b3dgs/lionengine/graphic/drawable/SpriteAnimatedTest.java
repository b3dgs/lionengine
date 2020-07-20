/*
 * Copyright (C) 2013-2020 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.b3dgs.lionengine.graphic.drawable;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertFalse;
import static com.b3dgs.lionengine.UtilAssert.assertHashEquals;
import static com.b3dgs.lionengine.UtilAssert.assertHashNotEquals;
import static com.b3dgs.lionengine.UtilAssert.assertNotEquals;
import static com.b3dgs.lionengine.UtilAssert.assertNotNull;
import static com.b3dgs.lionengine.UtilAssert.assertNull;
import static com.b3dgs.lionengine.UtilAssert.assertThrows;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.AnimState;
import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.AnimatorListener;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.ViewerMock;
import com.b3dgs.lionengine.geom.Geom;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.FactoryGraphicMock;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.ImageBuffer;
import com.b3dgs.lionengine.graphic.filter.FilterBilinear;

/**
 * Test {@link SpriteAnimated}.
 */
final class SpriteAnimatedTest
{
    /**
     * Prepare tests.
     */
    @BeforeAll
    public static void beforeTests()
    {
        Graphics.setFactoryGraphic(new FactoryGraphicMock());
        Medias.setLoadFromJar(SpriteAnimatedTest.class);
    }

    /**
     * Clean up tests.
     */
    @AfterAll
    public static void afterTests()
    {
        Graphics.setFactoryGraphic(null);
        Medias.setLoadFromJar(null);
    }

    /**
     * Test constructor with <code>null</code> media.
     */
    @Test
    void testConstructorMediaNull()
    {
        assertThrows(() -> new SpriteAnimatedImpl((Media) null, 1, 1), "Unexpected null argument !");
    }

    /**
     * Test constructor with invalid horizontal frames.
     */
    @Test
    void testConstructorInvalidHorizontalFrames()
    {
        assertThrows(() -> new SpriteAnimatedImpl(Medias.create("image.png"), 0, 1),
                     "Invalid argument: 0 is not strictly superior to 0");
    }

    /**
     * Test constructor with invalid vertical frames.
     */
    @Test
    void testConstructorInvalidVerticalFrames()
    {
        assertThrows(() -> new SpriteAnimatedImpl(Medias.create("image.png"), 1, 0),
                     "Invalid argument: 0 is not strictly superior to 0");
    }

    /**
     * Test constructor with <code>null</code> surface.
     */
    @Test
    void testConstructorSurfaceNull()
    {
        assertThrows(() -> new SpriteAnimatedImpl((ImageBuffer) null, 1, 1), "Unexpected null argument !");
    }

    /**
     * Test constructor with media.
     */
    @Test
    void testConstructorMedia()
    {
        final Media media = Medias.create("image.png");
        final SpriteAnimated sprite = new SpriteAnimatedImpl(media, 16, 8);

        assertFalse(sprite.isLoaded());
        assertNull(sprite.getSurface());
        assertEquals(64, sprite.getWidth());
        assertEquals(32, sprite.getHeight());
        assertEquals(4, sprite.getTileWidth());
        assertEquals(4, sprite.getTileHeight());
        assertEquals(16, sprite.getFramesHorizontal());
        assertEquals(8, sprite.getFramesVertical());
    }

    /**
     * Test constructor with surface.
     */
    @Test
    void testConstructorSurface()
    {
        final ImageBuffer surface = Graphics.createImageBuffer(64, 32);
        final SpriteAnimated sprite = new SpriteAnimatedImpl(surface, 16, 8);

        assertTrue(sprite.isLoaded());
        assertEquals(surface, sprite.getSurface());
        assertEquals(64, sprite.getWidth());
        assertEquals(32, sprite.getHeight());
        assertEquals(4, sprite.getTileWidth());
        assertEquals(4, sprite.getTileHeight());
        assertEquals(16, sprite.getFramesHorizontal());
        assertEquals(8, sprite.getFramesVertical());
    }

    /**
     * Test load with media.
     */
    @Test
    void testLoadMedia()
    {
        final Media media = Medias.create("image.png");
        final SpriteAnimated sprite = new SpriteAnimatedImpl(media, 16, 8);
        sprite.load();

        assertNotNull(sprite.getSurface());

        sprite.prepare();
        sprite.dispose();
    }

    /**
     * Test load with media already loaded.
     */
    @Test
    void testLoadMediaAlready()
    {
        final Media media = Medias.create("image.png");
        final SpriteAnimated sprite = new SpriteAnimatedImpl(media, 16, 8);
        sprite.load();

        assertThrows(() -> sprite.load(), "[" + media + "] " + SpriteImpl.ERROR_ALREADY_LOADED);
    }

    /**
     * Test load with surface.
     */
    @Test
    void testLoadSurface()
    {
        final SpriteAnimated sprite = new SpriteAnimatedImpl(Graphics.createImageBuffer(64, 32), 16, 8);

        assertThrows(() -> sprite.load(), SpriteImpl.ERROR_ALREADY_LOADED);
    }

    /**
     * Test stretch sprite.
     */
    @Test
    void testStretch()
    {
        final SpriteAnimated sprite = new SpriteAnimatedImpl(Graphics.createImageBuffer(64, 32), 16, 8);
        sprite.stretch(100.0, 100.0);

        assertEquals(64, sprite.getWidth());
        assertEquals(32, sprite.getHeight());
        assertEquals(4, sprite.getTileWidth());
        assertEquals(4, sprite.getTileHeight());

        sprite.stretch(200.0, 100.0);

        assertEquals(128, sprite.getWidth());
        assertEquals(32, sprite.getHeight());
        assertEquals(8, sprite.getTileWidth());
        assertEquals(4, sprite.getTileHeight());

        sprite.stretch(100.0, 200.0);

        assertEquals(128, sprite.getWidth());
        assertEquals(64, sprite.getHeight());
        assertEquals(8, sprite.getTileWidth());
        assertEquals(8, sprite.getTileHeight());

        sprite.stretch(200.0, 200.0);

        assertEquals(256, sprite.getWidth());
        assertEquals(128, sprite.getHeight());
        assertEquals(16, sprite.getTileWidth());
        assertEquals(16, sprite.getTileHeight());
    }

    /**
     * Test stretch sprite with invalid width.
     */
    @Test
    void testStretchInvalidWidth()
    {
        final SpriteAnimated sprite = new SpriteAnimatedImpl(Graphics.createImageBuffer(64, 32), 16, 8);

        assertThrows(() -> sprite.stretch(0.0, 100.0), "Invalid argument: 0.0 is not strictly superior to 0.0");
    }

    /**
     * Test stretch sprite with invalid height.
     */
    @Test
    void testStretchInvalidHeight()
    {
        final SpriteAnimated sprite = new SpriteAnimatedImpl(Graphics.createImageBuffer(64, 32), 16, 8);

        assertThrows(() -> sprite.stretch(100, 0.0), "Invalid argument: 0.0 is not strictly superior to 0.0");
    }

    /**
     * Test rotate sprite.
     */
    @Test
    void testRotate()
    {
        final SpriteAnimated sprite = new SpriteAnimatedImpl(Graphics.createImageBuffer(64, 32), 16, 8);
        for (int angle = -720; angle < 720; angle++)
        {
            sprite.setAngleAnchor(1, 2);
            sprite.rotate(angle);

            assertTrue(sprite.getWidth() == 64, angle + Constant.SPACE + sprite.getWidth());
            assertTrue(sprite.getHeight() == 32, angle + Constant.SPACE + sprite.getHeight());
        }
    }

    /**
     * Test set location.
     */
    @Test
    void testSetLocation()
    {
        final SpriteAnimatedImpl sprite = new SpriteAnimatedImpl(Graphics.createImageBuffer(64, 32), 16, 8);

        assertEquals(0.0, sprite.getX());
        assertEquals(0.0, sprite.getY());
        assertEquals(0, sprite.getRenderX());
        assertEquals(0, sprite.getRenderY());

        sprite.setLocation(1.5, 2.5);

        assertEquals(1.5, sprite.getX());
        assertEquals(2.5, sprite.getY());
        assertEquals(2, sprite.getRenderX());
        assertEquals(3, sprite.getRenderY());
    }

    /**
     * Test set location with viewer.
     */
    @Test
    void testSetLocationViewer()
    {
        final SpriteAnimatedImpl sprite = new SpriteAnimatedImpl(Graphics.createImageBuffer(64, 32), 16, 8);
        final ViewerMock viewer = new ViewerMock();
        sprite.setLocation(viewer, Geom.createLocalizable(1.5, 2.5));

        assertEquals(1.5, sprite.getX());
        assertEquals(237.5, sprite.getY());
        assertEquals(2, sprite.getRenderX());
        assertEquals(238, sprite.getRenderY());

        viewer.set(10, 20);
        sprite.setLocation(viewer, Geom.createLocalizable(1.5, 2.5));

        assertEquals(-8.5, sprite.getX());
        assertEquals(257.5, sprite.getY());
        assertEquals(-8, sprite.getRenderX());
        assertEquals(258, sprite.getRenderY());
    }

    /**
     * Test set alpha.
     */
    @Test
    void testSetAlpha()
    {
        final SpriteAnimated sprite = new SpriteAnimatedImpl(Graphics.createImageBuffer(64, 32), 16, 8);
        for (int alpha = 0; alpha < 256; alpha++)
        {
            sprite.setAlpha(alpha);

            assertEquals(64, sprite.getWidth());
            assertEquals(32, sprite.getHeight());
        }
    }

    /**
     * Test set alpha too low.
     */
    @Test
    void testSetAlphaLow()
    {
        final SpriteAnimated sprite = new SpriteAnimatedImpl(Graphics.createImageBuffer(64, 32), 16, 8);

        assertThrows(() -> sprite.setAlpha(-1), "Invalid argument: -1 is not superior or equal to 0");
    }

    /**
     * Test set alpha too high.
     */
    @Test
    void testSetAlphaHigh()
    {
        final SpriteAnimated sprite = new SpriteAnimatedImpl(Graphics.createImageBuffer(64, 32), 16, 8);

        assertThrows(() -> sprite.setAlpha(256), "Invalid argument: 256 is not inferior or equal to 255");
    }

    /**
     * Test set transparency.
     */
    @Test
    void testSetTransparency()
    {
        final SpriteAnimated sprite = new SpriteAnimatedImpl(Graphics.createImageBuffer(64, 32), 16, 8);
        sprite.setTransparency(ColorRgba.BLACK);

        assertEquals(64, sprite.getWidth());
        assertEquals(32, sprite.getHeight());
    }

    /**
     * Test set fade.
     */
    @Test
    void testSetFade()
    {
        final SpriteAnimated sprite = new SpriteAnimatedImpl(Graphics.createImageBuffer(64, 32), 16, 8);
        sprite.setFade(128, 128);
        sprite.setFade(128, 128);

        assertEquals(64, sprite.getWidth());
        assertEquals(32, sprite.getHeight());
    }

    /**
     * Test set frame offset.
     */
    @Test
    void testSetFrameOffset()
    {
        final SpriteAnimated sprite = new SpriteAnimatedImpl(Graphics.createImageBuffer(64, 32), 16, 8);
        sprite.setFrameOffsets(1, -1);
    }

    /**
     * Test filter bilinear.
     */
    @Test
    void testFilterBilinear()
    {
        final SpriteAnimated sprite = new SpriteAnimatedImpl(Graphics.createImageBuffer(64, 32), 16, 8);
        sprite.filter(new FilterBilinear());

        assertEquals(64, sprite.getWidth());
        assertEquals(32, sprite.getHeight());
    }

    /**
     * Test filter <code>null</code>.
     */
    @Test
    void testFilterNull()
    {
        final SpriteAnimated sprite = new SpriteAnimatedImpl(Graphics.createImageBuffer(64, 32), 16, 8);

        assertThrows(() -> sprite.filter(null), "Unexpected null argument !");
    }

    /**
     * Test mirror.
     */
    @Test
    void testMirror()
    {
        final SpriteAnimated sprite = new SpriteAnimatedImpl(Graphics.createImageBuffer(64, 32), 16, 8);

        assertEquals(Mirror.NONE, sprite.getMirror());

        sprite.setMirror(Mirror.HORIZONTAL);

        assertEquals(Mirror.HORIZONTAL, sprite.getMirror());
    }

    /**
     * Test mirror <code>null</code>.
     */
    @Test
    void testMirrorNull()
    {
        final SpriteAnimated sprite = new SpriteAnimatedImpl(Graphics.createImageBuffer(64, 32), 16, 8);

        assertThrows(() -> sprite.setMirror(null), "Unexpected null argument !");
    }

    /**
     * Test rendering point.
     */
    @Test
    void testRenderingPoint()
    {
        final SpriteAnimatedImpl sprite = new SpriteAnimatedImpl(Graphics.createImageBuffer(10, 20), 10, 20);
        sprite.setLocation(5.0, 10.0);
        sprite.setOrigin(Origin.TOP_LEFT);

        assertEquals(5, sprite.getRenderX());
        assertEquals(10, sprite.getRenderY());

        sprite.setOrigin(Origin.MIDDLE);

        assertEquals(5, sprite.getRenderX());
        assertEquals(10, sprite.getRenderY());
    }

    /**
     * Test origin <code>null</code>.
     */
    @Test
    void testSetOriginNull()
    {
        final SpriteAnimated sprite = new SpriteAnimatedImpl(Graphics.createImageBuffer(64, 32), 16, 8);

        assertThrows(() -> sprite.setOrigin(null), "Unexpected null argument !");
    }

    /**
     * Test set frame with invalid value.
     */
    @Test
    void testSetTileInvalid()
    {
        final SpriteAnimated sprite = new SpriteAnimatedImpl(Graphics.createImageBuffer(64, 32), 16, 8);

        assertThrows(() -> sprite.setFrame(0),
                     "Invalid argument: 0 is not superior or equal to " + Animation.MINIMUM_FRAME);
    }

    /**
     * Test set speed.
     */
    @Test
    void testSetSpeed()
    {
        final Animation animation = new Animation(Animation.DEFAULT_NAME, 1, 3, 1.0, false, false);
        final SpriteAnimated sprite = new SpriteAnimatedImpl(Graphics.createImageBuffer(64, 32), 16, 8);
        sprite.play(animation);
        sprite.setAnimSpeed(2.0);
        sprite.update(1.0);

        assertEquals(AnimState.PLAYING, sprite.getAnimState());
        assertEquals(3, sprite.getFrame());
        assertEquals(3, sprite.getFrameAnim());
    }

    /**
     * Test invalid speed setter.
     */
    @Test
    void testSetSpeedNegative()
    {
        final SpriteAnimated sprite = new SpriteAnimatedImpl(Graphics.createImageBuffer(64, 32), 16, 8);

        assertThrows(() -> sprite.setAnimSpeed(-1.0), "Invalid argument: -1.0 is not superior or equal to 0.0");
    }

    /**
     * Test play.
     */
    @Test
    void testPlay()
    {
        final Animation animation = new Animation(Animation.DEFAULT_NAME, 1, 2, 1.5, false, false);
        final SpriteAnimated sprite = new SpriteAnimatedImpl(Graphics.createImageBuffer(64, 32), 16, 8);
        sprite.play(animation);

        assertEquals(AnimState.PLAYING, sprite.getAnimState());
        assertEquals(1, sprite.getFrame());
        assertEquals(1, sprite.getFrameAnim());

        sprite.update(1.0);

        assertEquals(AnimState.PLAYING, sprite.getAnimState());
        assertEquals(2, sprite.getFrame());
        assertEquals(2, sprite.getFrameAnim());
    }

    /**
     * Test stop.
     */
    @Test
    void testStop()
    {
        final SpriteAnimated sprite = new SpriteAnimatedImpl(Graphics.createImageBuffer(64, 32), 16, 8);
        sprite.stop();

        assertEquals(AnimState.STOPPED, sprite.getAnimState());
    }

    /**
     * Test reset.
     */
    @Test
    void testReset()
    {
        final Animation animation = new Animation(Animation.DEFAULT_NAME, 1, 2, 3.0, false, false);
        final SpriteAnimated sprite = new SpriteAnimatedImpl(Graphics.createImageBuffer(64, 32), 16, 8);
        sprite.play(animation);
        sprite.update(1.0);
        sprite.reset();

        assertEquals(AnimState.STOPPED, sprite.getAnimState());
        assertEquals(1, sprite.getFrame());
        assertEquals(1, sprite.getFrameAnim());
    }

    /**
     * Test update without loop nor reverse.
     */
    @Test
    void testUpdateNoLoopNoReverse()
    {
        final Animation animation = new Animation(Animation.DEFAULT_NAME, 1, 2, 1.0, false, false);
        final SpriteAnimated sprite = new SpriteAnimatedImpl(Graphics.createImageBuffer(64, 32), 16, 8);
        sprite.play(animation);

        assertEquals(AnimState.PLAYING, sprite.getAnimState());
        assertEquals(1, sprite.getFrame());
        assertEquals(1, sprite.getFrameAnim());

        sprite.update(1.0);

        assertEquals(AnimState.PLAYING, sprite.getAnimState());
        assertEquals(2, sprite.getFrame());
        assertEquals(2, sprite.getFrameAnim());

        sprite.update(1.0);

        assertEquals(AnimState.FINISHED, sprite.getAnimState());
        assertEquals(2, sprite.getFrame());
        assertEquals(2, sprite.getFrameAnim());

        sprite.update(1.0);

        assertEquals(AnimState.FINISHED, sprite.getAnimState());
        assertEquals(2, sprite.getFrame());
        assertEquals(2, sprite.getFrameAnim());
    }

    /**
     * Test update with loop but no reverse.
     */
    @Test
    void testUpdateLoopNoReverse()
    {
        final Animation animation = new Animation(Animation.DEFAULT_NAME, 1, 3, 1.0, false, true);
        final SpriteAnimated sprite = new SpriteAnimatedImpl(Graphics.createImageBuffer(64, 32), 16, 8);
        sprite.play(animation);

        assertEquals(AnimState.PLAYING, sprite.getAnimState());
        assertEquals(1, sprite.getFrame());
        assertEquals(1, sprite.getFrameAnim());

        sprite.update(1.0);

        assertEquals(AnimState.PLAYING, sprite.getAnimState());
        assertEquals(2, sprite.getFrame());
        assertEquals(2, sprite.getFrameAnim());

        sprite.update(1.0);

        assertEquals(AnimState.PLAYING, sprite.getAnimState());
        assertEquals(3, sprite.getFrame());
        assertEquals(3, sprite.getFrameAnim());

        sprite.update(1.0);

        assertEquals(AnimState.PLAYING, sprite.getAnimState());
        assertEquals(1, sprite.getFrame());
        assertEquals(1, sprite.getFrameAnim());

        sprite.update(1.0);

        assertEquals(AnimState.PLAYING, sprite.getAnimState());
        assertEquals(2, sprite.getFrame());
        assertEquals(2, sprite.getFrameAnim());
    }

    /**
     * Test update without loop but reverse.
     */
    @Test
    void testUpdateNoLoopReverse()
    {
        final Animation animation = new Animation(Animation.DEFAULT_NAME, 1, 3, 1.0, true, false);
        final SpriteAnimated sprite = new SpriteAnimatedImpl(Graphics.createImageBuffer(64, 32), 16, 8);
        sprite.play(animation);

        assertEquals(AnimState.PLAYING, sprite.getAnimState());
        assertEquals(1, sprite.getFrame());
        assertEquals(1, sprite.getFrameAnim());

        sprite.update(1.0);

        assertEquals(AnimState.PLAYING, sprite.getAnimState());
        assertEquals(2, sprite.getFrame());
        assertEquals(2, sprite.getFrameAnim());

        sprite.update(1.0);

        assertEquals(AnimState.PLAYING, sprite.getAnimState());
        assertEquals(3, sprite.getFrame());
        assertEquals(3, sprite.getFrameAnim());

        sprite.update(1.0);

        assertEquals(AnimState.REVERSING, sprite.getAnimState());
        assertEquals(2, sprite.getFrame());
        assertEquals(2, sprite.getFrameAnim());

        sprite.update(1.0);

        assertEquals(AnimState.FINISHED, sprite.getAnimState());
        assertEquals(1, sprite.getFrame());
        assertEquals(1, sprite.getFrameAnim());
    }

    /**
     * Test update with loop and reverse.
     */
    @Test
    void testUpdateLoopReverse()
    {
        final Animation animation = new Animation(Animation.DEFAULT_NAME, 1, 3, 1.0, true, true);
        final SpriteAnimated sprite = new SpriteAnimatedImpl(Graphics.createImageBuffer(64, 32), 16, 8);
        sprite.play(animation);

        assertEquals(AnimState.PLAYING, sprite.getAnimState());
        assertEquals(1, sprite.getFrame());
        assertEquals(1, sprite.getFrameAnim());

        sprite.update(1.0);

        assertEquals(AnimState.PLAYING, sprite.getAnimState());
        assertEquals(2, sprite.getFrame());
        assertEquals(2, sprite.getFrameAnim());

        sprite.update(1.0);

        assertEquals(AnimState.PLAYING, sprite.getAnimState());
        assertEquals(3, sprite.getFrame());
        assertEquals(3, sprite.getFrameAnim());

        sprite.update(1.0);

        assertEquals(AnimState.REVERSING, sprite.getAnimState());
        assertEquals(2, sprite.getFrame());
        assertEquals(2, sprite.getFrameAnim());

        sprite.update(1.0);

        assertEquals(AnimState.PLAYING, sprite.getAnimState());
        assertEquals(1, sprite.getFrame());
        assertEquals(1, sprite.getFrameAnim());

        sprite.update(1.0);

        assertEquals(AnimState.PLAYING, sprite.getAnimState());
        assertEquals(2, sprite.getFrame());
        assertEquals(2, sprite.getFrameAnim());

        sprite.update(1.0);

        assertEquals(AnimState.PLAYING, sprite.getAnimState());
        assertEquals(3, sprite.getFrame());
        assertEquals(3, sprite.getFrameAnim());

        sprite.update(1.0);

        assertEquals(AnimState.REVERSING, sprite.getAnimState());
        assertEquals(2, sprite.getFrame());
        assertEquals(2, sprite.getFrameAnim());
    }

    /**
     * Test with listener.
     */
    @Test
    void testAnimatorListener()
    {
        final AtomicReference<Animation> played = new AtomicReference<>();
        final AtomicReference<AnimState> stated = new AtomicReference<>();
        final AtomicReference<Integer> framed = new AtomicReference<>();
        final AnimatorListener listener = new AnimatorListener()
        {
            @Override
            public void notifyAnimPlayed(Animation anim)
            {
                played.set(anim);
            }

            @Override
            public void notifyAnimState(AnimState state)
            {
                stated.set(state);
            }

            @Override
            public void notifyAnimFrame(int frame)
            {
                framed.set(Integer.valueOf(frame));
            }
        };
        final Animation animation = new Animation(Animation.DEFAULT_NAME, 1, 3, 0.25, true, false);
        final SpriteAnimated sprite = new SpriteAnimatedImpl(Graphics.createImageBuffer(64, 32), 16, 8);
        sprite.addListener(listener);

        assertNull(played.get());
        assertNull(stated.get());
        assertNull(framed.get());

        sprite.play(animation);

        assertEquals(animation, played.get());
        assertEquals(AnimState.PLAYING, stated.get());
        assertEquals(Integer.valueOf(1), framed.get());

        played.set(null);
        stated.set(null);
        framed.set(null);

        sprite.removeListener(listener);
        sprite.play(animation);
        sprite.update(1.0);

        assertNull(played.get());
        assertNull(stated.get());
        assertNull(framed.get());
    }

    /**
     * Test render.
     */
    @Test
    void testRender()
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
    void testEquals()
    {
        final ImageBuffer surface = Graphics.createImageBuffer(64, 32);
        final SpriteAnimated sprite = new SpriteAnimatedImpl(surface, 16, 8);
        final Media media = Medias.create("image.png");
        final SpriteAnimated spriteMedia = new SpriteAnimatedImpl(media, 16, 8);
        spriteMedia.load();

        assertEquals(sprite, sprite);
        assertEquals(sprite, new SpriteAnimatedImpl(surface, 16, 8));
        assertEquals(spriteMedia, spriteMedia);

        assertNotEquals(sprite, null);
        assertNotEquals(sprite, new Object());
        assertNotEquals(sprite, new SpriteAnimatedImpl(media, 16, 8));
        assertNotEquals(spriteMedia, new SpriteAnimatedImpl(media, 16, 8));
        assertNotEquals(sprite, new SpriteAnimatedImpl(surface, 8, 16));
        assertNotEquals(sprite, new SpriteAnimatedImpl(surface, 16, 16));
        assertNotEquals(sprite, new SpriteAnimatedImpl(Graphics.createImageBuffer(64, 32), 16, 8));
        assertNotEquals(sprite, new SpriteAnimatedImpl(Graphics.createImageBuffer(32, 32), 16, 8));
        assertNotEquals(sprite, new SpriteAnimatedImpl(Graphics.createImageBuffer(64, 64), 16, 8));
        assertNotEquals(sprite, new SpriteAnimatedImpl(Graphics.createImageBuffer(32, 64), 16, 8));
        assertNotEquals(sprite, new SpriteAnimatedImpl(Graphics.createImageBuffer(64, 32), 8, 8));
        assertNotEquals(sprite, new SpriteAnimatedImpl(Graphics.createImageBuffer(64, 32), 16, 16));
        assertNotEquals(sprite, new SpriteAnimatedImpl(Graphics.createImageBuffer(32, 32), 8, 8));
        assertNotEquals(sprite, new SpriteAnimatedImpl(Graphics.createImageBuffer(64, 64), 16, 16));

        sprite.dispose();
        spriteMedia.dispose();
        surface.dispose();
    }

    /**
     * Test hash code.
     */
    @Test
    void testHashCode()
    {
        final ImageBuffer surface = Graphics.createImageBuffer(64, 32);
        final SpriteAnimated sprite = new SpriteAnimatedImpl(surface, 16, 8);
        final Media media = Medias.create("image.png");
        final SpriteAnimated spriteMedia = new SpriteAnimatedImpl(media, 16, 8);
        spriteMedia.load();

        assertHashEquals(sprite, new SpriteAnimatedImpl(surface, 16, 8));

        assertHashNotEquals(sprite, new Object());
        assertHashNotEquals(spriteMedia, new SpriteAnimatedImpl(media, 16, 8));
        assertHashNotEquals(sprite, new SpriteAnimatedImpl(surface, 8, 16));
        assertHashNotEquals(sprite, new SpriteAnimatedImpl(surface, 16, 16));
        assertHashNotEquals(sprite, new SpriteAnimatedImpl(Graphics.createImageBuffer(64, 32), 16, 8));
        assertHashNotEquals(sprite, new SpriteAnimatedImpl(Graphics.createImageBuffer(32, 32), 16, 8));
        assertHashNotEquals(sprite, new SpriteAnimatedImpl(Graphics.createImageBuffer(64, 64), 16, 8));
        assertHashNotEquals(sprite, new SpriteAnimatedImpl(Graphics.createImageBuffer(32, 64), 16, 8));
        assertHashNotEquals(sprite, new SpriteAnimatedImpl(Graphics.createImageBuffer(64, 32), 8, 8));
        assertHashNotEquals(sprite, new SpriteAnimatedImpl(Graphics.createImageBuffer(64, 32), 16, 16));
        assertHashNotEquals(sprite, new SpriteAnimatedImpl(Graphics.createImageBuffer(32, 32), 8, 8));
        assertHashNotEquals(sprite, new SpriteAnimatedImpl(Graphics.createImageBuffer(64, 64), 16, 16));

        sprite.dispose();
        spriteMedia.dispose();
        surface.dispose();
    }
}
