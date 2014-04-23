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

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.ImageInfo;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Transparency;
import com.b3dgs.lionengine.anim.Anim;
import com.b3dgs.lionengine.anim.AnimState;
import com.b3dgs.lionengine.anim.Animation;
import com.b3dgs.lionengine.anim.Animator;
import com.b3dgs.lionengine.core.FactoryGraphicMock;
import com.b3dgs.lionengine.core.FactoryMediaMock;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.UtilityImage;

/**
 * Test the animated sprite class.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
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
        UtilityImage.setGraphicFactory(new FactoryGraphicMock());
        Media.setMediaFactory(new FactoryMediaMock());
        SpriteAnimatedTest.media = Media.create(Media.getPath("src", "test", "resources", "drawable", "image.png"));
        SpriteAnimatedTest.g = UtilityImage.createImageBuffer(100, 100, Transparency.OPAQUE).createGraphic();
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        UtilityImage.setGraphicFactory(null);
        Media.setMediaFactory(null);
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
        final SpriteAnimated spriteC = Drawable.loadSpriteAnimated(SpriteAnimatedTest.media, frameHorizontal,
                frameVertical);
        final ImageInfo info = DrawableTestTool.assertImageInfoCorrect(SpriteAnimatedTest.media, spriteC);

        Assert.assertEquals(frameHorizontal * frameVertical, spriteC.getFramesNumber());
        Assert.assertEquals(info.getWidth() / frameHorizontal, spriteC.getFrameWidthOriginal());
        Assert.assertEquals(info.getWidth() / frameHorizontal, spriteC.getFrameWidth());
        Assert.assertEquals(info.getHeight() / frameVertical, spriteC.getFrameHeightOriginal());
        Assert.assertEquals(info.getHeight() / frameVertical, spriteC.getFrameHeight());

        DrawableTestTool.testSpriteLoading(spriteC);
        DrawableTestTool.testSpriteModification(2, spriteA);

        // Equals
        final SpriteAnimated spriteD = Drawable.loadSpriteAnimated(SpriteAnimatedTest.media, frameHorizontal,
                frameVertical);
        final SpriteAnimated spriteE = Drawable.loadSpriteAnimated(SpriteAnimatedTest.media, frameHorizontal + 2,
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
        DrawableTestTool.testImageRender(SpriteAnimatedTest.g, spriteC);
        spriteC.setMirror(true);
        spriteC.render(SpriteAnimatedTest.g, 0, 0);

        // Error
        DrawableTestTool.testSpriteAnimatedLoadError(0, 0);
        DrawableTestTool.testSpriteAnimatedLoadError(0, 1);
        DrawableTestTool.testSpriteAnimatedLoadError(1, 0);

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
        Assert.assertFalse(spriteC.equals(Drawable.loadSpriteAnimated(SpriteAnimatedTest.media, frameHorizontal,
                frameVertical)));
    }
}
