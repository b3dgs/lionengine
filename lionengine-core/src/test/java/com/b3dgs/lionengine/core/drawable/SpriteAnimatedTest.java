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
import com.b3dgs.lionengine.Animator;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.graphic.FactoryGraphicMock;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.ImageBuffer;
import com.b3dgs.lionengine.graphic.ImageInfo;
import com.b3dgs.lionengine.graphic.SpriteAnimated;
import com.b3dgs.lionengine.graphic.Transparency;

/**
 * Test the animated sprite class.
 */
public class SpriteAnimatedTest
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
        Medias.setLoadFromJar(SpriteAnimatedTest.class);
        Graphics.setFactoryGraphic(new FactoryGraphicMock());

        media = Medias.create("image.png");
        g = Graphics.createImageBuffer(100, 100, Transparency.OPAQUE).createGraphic();
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
     * Test function around animated sprite and animation.
     */
    @Test
    public void testSpriteAnimated()
    {
        final ImageBuffer buffer = Graphics.createImageBuffer(16, 16, Transparency.OPAQUE);
        final SpriteAnimated spriteA = Drawable.loadSpriteAnimated(buffer, 1, 1);

        Assert.assertNotNull(spriteA.getSurface());

        final Animator animator = new AnimatorImpl();
        final Animation animation = new Animation(null, 1, 6, 1.0, false, false);

        // Load from file
        final int framesH = 4;
        final int framesV = 2;
        final SpriteAnimated spriteC = Drawable.loadSpriteAnimated(media, framesH, framesV);
        final ImageInfo info = DrawableTestTool.assertImageInfoCorrect(media, spriteC);

        Assert.assertEquals(info.getWidth() / framesH, spriteC.getTileWidth());
        Assert.assertEquals(info.getHeight() / framesV, spriteC.getTileHeight());
        Assert.assertEquals(0, spriteC.getFrameOffsetX());
        Assert.assertEquals(0, spriteC.getFrameOffsetY());

        spriteC.setFrameOffsets(1, 2);

        Assert.assertEquals(1, spriteC.getFrameOffsetX());
        Assert.assertEquals(2, spriteC.getFrameOffsetY());

        DrawableTestTool.testSpriteLoading(spriteC);
        DrawableTestTool.testSpriteModification(2, spriteA);

        // Equals
        final SpriteAnimated spriteD = Drawable.loadSpriteAnimated(media, framesH, framesV);
        final SpriteAnimated spriteE = Drawable.loadSpriteAnimated(media, framesH + 2, framesV + 1);
        spriteD.load();
        spriteD.prepare();
        spriteE.load();
        spriteE.prepare();
        Assert.assertFalse(spriteD.equals(spriteE));
        spriteE.stretch(110, 100);
        spriteD.setMirror(Mirror.HORIZONTAL);
        final int hash = spriteD.hashCode();
        Assert.assertFalse(spriteD.equals(spriteE));
        spriteE.stretch(100, 110);
        spriteD.setMirror(Mirror.NONE);
        Assert.assertFalse(spriteD.equals(spriteE));
        spriteE.stretch(110, 110);
        Assert.assertTrue(hash != spriteD.hashCode());
        spriteD.setMirror(Mirror.VERTICAL);
        Assert.assertTrue(hash != spriteD.hashCode());
        Assert.assertFalse(spriteD.equals(null));
        Assert.assertFalse(spriteD.equals(spriteE));
        final SpriteAnimated spriteF = Drawable.loadSpriteAnimated(spriteD.getSurface(), framesH, framesV);
        Assert.assertTrue(spriteD.equals(spriteF));

        try
        {
            animator.play(null);
            animator.update(1.0);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
            Assert.assertNotNull(exception);
        }
        try
        {
            animator.play(animation);
            animator.update(1.0);
        }
        catch (final LionEngineException exception)
        {
            Assert.fail(exception.getMessage());
        }

        // Test render
        DrawableTestTool.testImageRender(g, spriteC);
        spriteC.setMirror(Mirror.HORIZONTAL);
        spriteC.setLocation(1.0, 2.0);
        spriteC.render(g);
        spriteC.setMirror(Mirror.VERTICAL);
        spriteC.render(g);

        // Error
        DrawableTestTool.testSpriteAnimatedLoadError(0, 0);
        DrawableTestTool.testSpriteAnimatedLoadError(0, 1);
        DrawableTestTool.testSpriteAnimatedLoadError(1, 0);

        // Animations
        spriteA.play(animation);
        spriteA.setAnimSpeed(1.0);
        spriteA.update(1.0);
        spriteA.stop();
        spriteA.setFrame(1);
        Assert.assertEquals(1, spriteA.getFrame());
        Assert.assertEquals(1, spriteA.getFrameAnim());
        Assert.assertEquals(AnimState.STOPPED, spriteA.getAnimState());

        Assert.assertFalse(spriteC.equals(Drawable.loadSpriteAnimated(media, framesH, framesV)));

        spriteA.dispose();
    }
}
