package com.b3dgs.lionengine.test;

import java.awt.Transparency;
import java.awt.image.BufferedImage;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.b3dgs.lionengine.Engine;
import com.b3dgs.lionengine.Filter;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Version;
import com.b3dgs.lionengine.anim.Anim;
import com.b3dgs.lionengine.anim.AnimState;
import com.b3dgs.lionengine.anim.Animation;
import com.b3dgs.lionengine.anim.Animator;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.Image;
import com.b3dgs.lionengine.drawable.Sprite;
import com.b3dgs.lionengine.drawable.SpriteAnimated;
import com.b3dgs.lionengine.drawable.SpriteTiled;
import com.b3dgs.lionengine.utility.UtilityImage;

/**
 * Test the Drawable package.
 */
public class TestDrawable
{
    /**
     * Prepare test.
     */
    @Before
    public void setUp()
    {
        Engine.start("UnitTest", Version.create(1, 0, 0), Media.getPath("resources"));
    }

    /**
     * Test function around the image.
     */
    @Test
    public void testImage()
    {
        // Unexisting buffered image surface path
        final int width = 16;
        final int height = 16;
        final Image imageA = Drawable.loadImage(UtilityImage.createBufferedImage(width, height, Transparency.OPAQUE));

        Assert.assertNotNull(imageA);
        Assert.assertEquals(imageA.getWidth(), width);
        Assert.assertEquals(imageA.getHeight(), height);

        // Share correctly the surface
        final Image imageB = Drawable.loadImage((BufferedImage) imageA.getSurface());

        Assert.assertEquals(imageA.getSurface(), imageB.getSurface());

        // Test instantiation
        final Image imageC = imageB.instanciate();

        Assert.assertNotSame(imageB, imageC);
        Assert.assertEquals(imageB.getSurface(), imageC.getSurface());
    }

    /**
     * Test function around the sprite.
     */
    @Test
    public void testSprite()
    {
        try
        {
            Drawable.loadSprite(Media.get("test"));
        }
        catch (final LionEngineException exception)
        {
            // Success
        }

        // Sprite with surface
        BufferedImage surface = UtilityImage.createBufferedImage(16, 16, Transparency.OPAQUE);
        final Sprite spriteB = Drawable.loadSprite(surface);

        Assert.assertNotNull(spriteB.getSurface());
        Assert.assertEquals(surface, spriteB.getSurface());

        // Sprite modification
        surface = spriteB.getSurface();
        spriteB.scale(200);
        Assert.assertNotSame(surface, spriteB.getSurface());
        Assert.assertEquals(surface.getWidth(), spriteB.getWidthOriginal());
        Assert.assertEquals(surface.getWidth() * 2, spriteB.getWidth());
        Assert.assertEquals(surface.getHeight(), spriteB.getHeightOriginal());
        Assert.assertEquals(surface.getHeight() * 2, spriteB.getHeight());

        surface = spriteB.getSurface();
        spriteB.stretch(50, 50);
        Assert.assertNotSame(surface, spriteB.getSurface());

        try
        {
            spriteB.scale(-1);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }

        surface = spriteB.getSurface();
        spriteB.filter(Filter.BILINEAR);
        Assert.assertNotSame(surface, spriteB.getSurface());

        surface = spriteB.getSurface();
        spriteB.flipHorizontal();
        Assert.assertNotSame(surface, spriteB.getSurface());

        surface = spriteB.getSurface();
        spriteB.flipVertical();
        Assert.assertNotSame(surface, spriteB.getSurface());

        surface = spriteB.getSurface();
        spriteB.rotate(90);
        Assert.assertNotSame(surface, spriteB.getSurface());

        spriteB.rotate(-1);
        spriteB.rotate(361);

        // Sprite clone (share surface only)
        final Sprite spriteC = spriteB.instanciate();

        Assert.assertNotSame(spriteB, spriteC);
        Assert.assertEquals(spriteB.getSurface(), spriteC.getSurface());
    }

    /**
     * Test function around tiled sprite.
     */
    @Test
    public void testSpriteTiled()
    {
        final SpriteTiled spriteA = Drawable.loadSpriteTiled(
                UtilityImage.createBufferedImage(16, 16, Transparency.OPAQUE), 1, 1);

        Assert.assertNotNull(spriteA.getSurface());
        Assert.assertEquals(1, spriteA.getTileWidth());
        Assert.assertEquals(1, spriteA.getTileHeight());
        Assert.assertNotNull(spriteA.getTileReference(0));
        Assert.assertEquals(16, spriteA.getTilesHorizontal());
        Assert.assertEquals(16, spriteA.getTilesVertical());
        Assert.assertEquals(256, spriteA.getTilesNumber());
    }

    /**
     * Test function around animated sprite and animation.
     */
    @Test
    public void testSpriteAnimated()
    {
        final SpriteAnimated spriteA = Drawable.loadSpriteAnimated(
                UtilityImage.createBufferedImage(16, 16, Transparency.OPAQUE), 1, 1);

        Assert.assertNotNull(spriteA.getSurface());

        // Test instantiation
        final Image spriteB = spriteA.instanciate();

        Assert.assertNotSame(spriteA, spriteB);
        Assert.assertEquals(spriteA.getSurface(), spriteB.getSurface());

        final Animator animator = Anim.createAnimator();
        final Animation animation = Anim.createAnimation(1, 6, 1.0, false, false);

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
    }

    /**
     * Test animator related functions.
     */
    @Test
    public void testAnimator()
    {
        final Animator animator = Anim.createAnimator();
        final Animation animation1 = Anim.createAnimation(1, 3, 1.0, true, true);

        Assert.assertEquals(AnimState.STOPPED, animator.getAnimState());
        animator.play(animation1);
        Assert.assertEquals(AnimState.PLAYING, animator.getAnimState());
        Assert.assertEquals(1, animator.getFrame());

        animator.updateAnimation(1.0);
        Assert.assertEquals(2, animator.getFrame());

        animator.updateAnimation(1.0);
        Assert.assertEquals(3, animator.getFrame());

        animator.updateAnimation(1.0);
        Assert.assertEquals(AnimState.REVERSING, animator.getAnimState());
        Assert.assertEquals(2, animator.getFrame());

        animator.updateAnimation(0.5);
        Assert.assertEquals(1, animator.getFrame());

        animator.updateAnimation(1.5);
        Assert.assertEquals(2, animator.getFrame());

        final Animation animation2 = Anim.createAnimation(1, 2, 1.0, false, false);

        animator.stopAnimation();
        Assert.assertEquals(AnimState.STOPPED, animator.getAnimState());
        animator.play(animation2);
        Assert.assertEquals(AnimState.PLAYING, animator.getAnimState());
        Assert.assertEquals(1, animator.getFrame());

        animator.updateAnimation(1.0);
        Assert.assertEquals(2, animator.getFrame());

        animator.updateAnimation(1.0);
        Assert.assertEquals(2, animator.getFrame());
        Assert.assertEquals(AnimState.FINISHED, animator.getAnimState());
    }
}
