package com.b3dgs.lionengine.test;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.Display;
import com.b3dgs.lionengine.Engine;
import com.b3dgs.lionengine.Filter;
import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.ImageInfo;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Loader;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Version;
import com.b3dgs.lionengine.anim.Anim;
import com.b3dgs.lionengine.anim.AnimState;
import com.b3dgs.lionengine.anim.Animation;
import com.b3dgs.lionengine.anim.Animator;
import com.b3dgs.lionengine.drawable.Bar;
import com.b3dgs.lionengine.drawable.Cursor;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.Image;
import com.b3dgs.lionengine.drawable.Sprite;
import com.b3dgs.lionengine.drawable.SpriteAnimated;
import com.b3dgs.lionengine.drawable.SpriteFont;
import com.b3dgs.lionengine.drawable.SpriteParallaxed;
import com.b3dgs.lionengine.drawable.SpriteTiled;
import com.b3dgs.lionengine.utility.UtilityImage;

/**
 * Test the Drawable package.
 */
public class TestDrawable
{
    /** Image media. */
    private Media media;
    /** Graphic test output. */
    private Graphic g;

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
        final Sprite spriteOriginal = sprite.instanciate();
        Assert.assertEquals(spriteOriginal, sprite);

        BufferedImage surface = sprite.getSurface();

        if (!(sprite instanceof SpriteFont))
        {
            sprite.scale(100 * scale);
            Assert.assertNotEquals(spriteOriginal, sprite);
            Assert.assertNotSame(surface, sprite.getSurface());
            Assert.assertEquals(surface.getWidth(), sprite.getWidthOriginal());
            Assert.assertEquals(surface.getWidth() * scale, sprite.getWidth());
            Assert.assertEquals(surface.getHeight(), sprite.getHeightOriginal());
            Assert.assertEquals(surface.getHeight() * scale, sprite.getHeight());
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

        sprite.setTransparency(Color.BLACK);
        sprite.setAlpha(128);
        sprite.setAlpha(0);

        if (!(sprite instanceof SpriteFont))
        {
            sprite.stretch(100, 100 * scale);
            Assert.assertNotEquals(spriteOriginal, sprite);
        }
    }

    /**
     * Test the sprite loading function.
     * 
     * @param sprite The sprite reference.
     */
    private static void testSpriteLoading(Sprite sprite)
    {
        Assert.assertNull(sprite.getSurface());
        sprite.load(false);
        Assert.assertNotNull(sprite.getSurface());
    }

    /**
     * Test sprite tiled loading error.
     * 
     * @param tw The tile width.
     * @param th The tile height.
     */
    private static void testSpriteTiledLoadError(int tw, int th)
    {
        final Media media = Media.get("dot.png");
        try
        {
            final SpriteTiled sprite = Drawable.loadSpriteTiled(
                    UtilityImage.createBufferedImage(16, 16, Transparency.OPAQUE), tw, th);
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
        final Media media = Media.get("dot.png");
        try
        {
            final SpriteAnimated sprite = Drawable.loadSpriteAnimated(
                    UtilityImage.createBufferedImage(16, 16, Transparency.OPAQUE), hf, vf);
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
     * Test creation of animation.
     * 
     * @param first The first frame.
     * @param last The last frame.
     * @param speed The speed value.
     */
    private static void testCreateAnimation(int first, int last, double speed)
    {
        try
        {
            Anim.createAnimation(first, last, speed, true, true);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }
    }

    /**
     * Test play animation.
     * 
     * @param animator The animator to play.
     * @param first The first frame.
     * @param last The last frame.
     * @param speed The speed value.
     */
    private static void testPlayAnimation(Animator animator, int first, int last, double speed)
    {
        try
        {
            animator.play(first, last, speed, true, true);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }
    }

    /**
     * Prepare test.
     */
    @Before
    public void setUp()
    {
        Engine.start("UnitTest", Version.create(1, 0, 0), Media.getPath("resources"));
        media = Media.get("dot.png");
        final Graphics2D g2d = UtilityImage.createBufferedImage(100, 100, Transparency.OPAQUE).createGraphics();
        g = new Graphic(g2d);
    }

    /**
     * Test drawable class.
     * 
     * @throws Exception If error.
     */
    @Test
    public void testDrawable() throws Exception
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
     * Test anim class.
     * 
     * @throws Exception If error.
     */
    @Test
    public void testAnim() throws Exception
    {
        final Constructor<Anim> constructor = Anim.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        try
        {
            final Anim anim = constructor.newInstance();
            Assert.assertNotNull(anim);
            Assert.fail();
        }
        catch (final InvocationTargetException exception)
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
            UtilityImage.createBufferedImage(-16, 16, Transparency.OPAQUE);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }
        try
        {
            UtilityImage.createBufferedImage(16, -16, Transparency.OPAQUE);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }

        // Load unexisting image
        try
        {
            Drawable.loadImage(Media.get("void"));
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }

        final int width = 16;
        final int height = 16;
        final BufferedImage surface = UtilityImage.createBufferedImage(width, height, Transparency.OPAQUE);
        final Image imageA = Drawable.loadImage(surface);

        Assert.assertNotNull(imageA);
        Assert.assertEquals(width, imageA.getWidth());
        Assert.assertEquals(height, imageA.getHeight());
        Assert.assertEquals(surface, imageA.getSurface());

        // Share correctly the surface
        final Image imageB = Drawable.loadImage((BufferedImage) imageA.getSurface());
        Assert.assertEquals(imageA, imageB);
        Assert.assertEquals(imageB, imageB.instanciate());

        // Load from file
        final Image imageC = Drawable.loadImage(media);
        TestDrawable.assertImageInfoCorrect(media, imageC);
        Assert.assertNotNull(imageC.getSurface());
        TestDrawable.testImageRender(g, imageC);
        Assert.assertFalse(imageC.equals(Drawable.loadImage(media)));
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
            Drawable.loadSprite(Media.get("void"));
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }

        // Sprite with existing surface
        final BufferedImage surface = UtilityImage.createBufferedImage(16, 16, Transparency.OPAQUE);
        final Sprite spriteA = Drawable.loadSprite(surface);

        Assert.assertNotNull(spriteA.getSurface());
        Assert.assertEquals(surface, spriteA.getSurface());

        TestDrawable.testSpriteModification(2, spriteA);
        // Sprite clone (share surface only)
        Assert.assertEquals(spriteA, spriteA.instanciate());

        // Load from file
        final Sprite spriteB = Drawable.loadSprite(media);
        TestDrawable.assertImageInfoCorrect(media, spriteB);

        TestDrawable.testSpriteLoading(spriteB);
        TestDrawable.testImageRender(g, spriteB);
        Assert.assertFalse(spriteB.equals(Drawable.loadSprite(media)));
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
                UtilityImage.createBufferedImage(width, height, Transparency.OPAQUE), tileSize, tileSize);

        Assert.assertNotNull(spriteA.getSurface());
        Assert.assertEquals(tileSize, spriteA.getTileWidth());
        Assert.assertEquals(tileSize, spriteA.getTileHeight());
        Assert.assertNotNull(spriteA.getTileReference(0));
        Assert.assertEquals(width, spriteA.getTilesHorizontal());
        Assert.assertEquals(height, spriteA.getTilesVertical());
        Assert.assertEquals(width * height, spriteA.getTilesNumber());

        // Load from file
        final int tileWidth = 16;
        final int tileHeight = 8;
        final SpriteTiled spriteB = Drawable.loadSpriteTiled(media, tileWidth, tileHeight);
        TestDrawable.assertImageInfoCorrect(media, spriteB);

        Assert.assertEquals(tileWidth, spriteB.getTileWidthOriginal());
        Assert.assertEquals(tileWidth, spriteB.getTileWidth());
        Assert.assertEquals(tileHeight, spriteB.getTileHeightOriginal());
        Assert.assertEquals(tileHeight, spriteB.getTileHeight());

        TestDrawable.testSpriteLoading(spriteB);

        TestDrawable.testSpriteModification(2, spriteB);
        TestDrawable.testImageRender(g, spriteB);
        spriteB.render(g, 0, 0, 0);

        // Instantiate
        Assert.assertEquals(spriteB, spriteB.instanciate());

        // Get tile
        Assert.assertNotNull(spriteB.getTile(0));
        Assert.assertNotNull(spriteB.getTile(99));
        Assert.assertNotNull(spriteB.getTile(-1));

        // Error
        TestDrawable.testSpriteTiledLoadError(0, 0);
        TestDrawable.testSpriteTiledLoadError(0, 1);
        TestDrawable.testSpriteTiledLoadError(1, 0);
        Assert.assertFalse(spriteB.equals(Drawable.loadSpriteTiled(media, tileWidth, tileHeight)));
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

        final Animator animator = Anim.createAnimator();
        final Animation animation = Anim.createAnimation(1, 6, 1.0, false, false);

        // Load from file
        final int frameHorizontal = 4;
        final int frameVertical = 2;
        final SpriteAnimated spriteC = Drawable.loadSpriteAnimated(media, frameHorizontal, frameVertical);
        final ImageInfo info = TestDrawable.assertImageInfoCorrect(media, spriteC);

        Assert.assertEquals(frameHorizontal * frameVertical, spriteC.getFramesNumber());
        Assert.assertEquals(info.getWidth() / frameHorizontal, spriteC.getFrameWidthOriginal());
        Assert.assertEquals(info.getWidth() / frameHorizontal, spriteC.getFrameWidth());
        Assert.assertEquals(info.getHeight() / frameVertical, spriteC.getFrameHeightOriginal());
        Assert.assertEquals(info.getHeight() / frameVertical, spriteC.getFrameHeight());

        TestDrawable.testSpriteLoading(spriteC);
        TestDrawable.testSpriteModification(2, spriteA);
        // Test instantiation
        Assert.assertEquals(spriteA, spriteA.instanciate());

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
        TestDrawable.testImageRender(g, spriteC);
        spriteC.setMirror(true);
        spriteC.render(g, 0, 0);

        // Error
        TestDrawable.testSpriteAnimatedLoadError(0, 0);
        TestDrawable.testSpriteAnimatedLoadError(0, 1);
        TestDrawable.testSpriteAnimatedLoadError(1, 0);

        // Animations
        spriteA.play(animation);
        spriteA.play(1, 5, 1.0, false, false);
        spriteA.setAnimSpeed(1.0);
        spriteA.updateAnimation(1.0);
        spriteA.stopAnimation();
        spriteA.setFrame(1);
        Assert.assertEquals(1, spriteA.getFrame());
        Assert.assertEquals(AnimState.STOPPED, spriteA.getAnimState());

        Assert.assertNotNull(spriteA.getFrame(1));
        Assert.assertNotNull(spriteA.getFrame(-1));
        Assert.assertNotNull(spriteA.getFrame(0));
        Assert.assertFalse(spriteC.equals(Drawable.loadSpriteAnimated(media, frameHorizontal, frameVertical)));
    }

    /**
     * Test animator related functions.
     */
    @Test
    public void testAnimator()
    {
        TestDrawable.testCreateAnimation(-1, 1, 1.0);
        TestDrawable.testCreateAnimation(1, -1, 1.0);
        TestDrawable.testCreateAnimation(1, 1, -1.0);

        final Animator animator = Anim.createAnimator();
        final Animation animation1 = Anim.createAnimation(1, 3, 1.0, true, true);

        try
        {
            animator.setFrame(0);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }
        try
        {
            animator.setAnimSpeed(-1.0);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }

        TestDrawable.testPlayAnimation(animator, -1, 1, 1.0);
        TestDrawable.testPlayAnimation(animator, 1, -1, 1.0);
        TestDrawable.testPlayAnimation(animator, 1, 1, -1.0);

        animation1.setFirst(animation1.getFirst());
        animation1.setLast(animation1.getLast());
        animation1.setSpeed(animation1.getSpeed());
        animation1.setReverse(animation1.getReverse());
        animation1.setRepeat(animation1.getRepeat());
        animator.stopAnimation();

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
        Assert.assertEquals(2, animator.getFrame());

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
        final AnimState animState = animator.getAnimState();
        Assert.assertEquals(AnimState.FINISHED, animState);
    }

    /**
     * Test parallax sprite.
     */
    @Test
    public void testParallax()
    {
        final ImageInfo info = ImageInfo.get(media);
        final int lines = 8;
        final SpriteParallaxed spriteA = Drawable.loadSpriteParallaxed(media, lines, 60, 100);

        Assert.assertEquals(0, spriteA.getWidthOriginal());

        spriteA.prepare(Filter.NONE);
        Assert.assertTrue(spriteA.equals(spriteA));
        Assert.assertEquals(info.getWidth(), spriteA.getWidthOriginal());
        Assert.assertEquals(info.getHeight() / lines, spriteA.getHeightOriginal());
        Assert.assertEquals(info.getWidth(), spriteA.getWidth());
        Assert.assertEquals(info.getHeight() / lines, spriteA.getHeight());

        for (int i = 0; i < lines; i++)
        {
            Assert.assertNotNull(spriteA.getLine(i));
            spriteA.render(g, i, 0, 0);
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
        spriteA.render(g, 0, 0);

        // Resize
        final int scale = 2;
        final SpriteParallaxed spriteB = Drawable.loadSpriteParallaxed(media, lines, 60, 100);

        spriteB.scale(100 * scale);
        spriteB.prepare(Filter.BILINEAR);
        Assert.assertEquals(info.getWidth(), spriteB.getWidthOriginal());
        Assert.assertEquals(info.getHeight() / lines, spriteB.getHeightOriginal());
        Assert.assertEquals(info.getWidth() * scale, spriteB.getWidth());
        Assert.assertEquals(info.getHeight() / lines * scale, spriteB.getHeight());
        Assert.assertFalse(spriteB.equals(spriteA));
        Assert.assertTrue(spriteA.hashCode() != spriteB.hashCode());
        Assert.assertFalse(spriteA.equals(media));

        // Error case
        try
        {
            final SpriteParallaxed spriteC = Drawable.loadSpriteParallaxed(media, 0, 60, 100);
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
        final Media fontData = Media.get("fontdata.xml");
        final String text = "a%z";
        final SpriteFont sprite = Drawable.loadSpriteFont(media, fontData, 6, 7);

        final ImageInfo info = TestDrawable.assertImageInfoCorrect(media, sprite);
        Assert.assertEquals(info.getWidth(), sprite.getWidthOriginal());
        Assert.assertEquals(info.getHeight(), sprite.getHeightOriginal());

        TestDrawable.testSpriteLoading(sprite);
        TestDrawable.testImageRender(g, sprite);
        TestDrawable.testSpriteModification(2, sprite);
        sprite.scale(200);

        sprite.draw(g, 0, 0, Align.LEFT, text);
        sprite.draw(g, 0, 0, Align.CENTER, text);
        sprite.draw(g, 0, 0, Align.RIGHT, text);
        sprite.setLineHeight(0);
        Assert.assertTrue(sprite.getTextWidth(text) >= 1);
        Assert.assertTrue(sprite.getTextHeight(text) >= 0);

        Assert.assertEquals(sprite, sprite.instanciate());
        Assert.assertFalse(sprite.equals(Drawable.loadSpriteFont(media, fontData, 6, 7)));
    }

    /**
     * Test the cursor class.
     */
    @Test
    public void testCursor()
    {
        final Display display = new Display(320, 240, 16, 60);
        try
        {
            final Cursor cursor = new Cursor(display);
            Assert.assertNotNull(cursor);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }

        final Cursor cursor = new Cursor(display, media);
        cursor.setArea(0, 0, 320, 240);
        cursor.setSensibility(1.0, 2.0);
        cursor.setSurfaceId(0);

        final Display internal = new Display(640, 480, 16, 60);
        final Display external = new Display(1280, 720, 16, 60);
        final Config config = new Config(internal, external, true);
        final Loader loader = new Loader(config);
        final Scene scene = new Scene(loader);

        cursor.setLockMouse(false);
        cursor.update(1.0, scene.mouse, false);
        cursor.update(1.0, scene.mouse, true);
        cursor.setLockMouse(true);
        cursor.update(1.0, scene.mouse, false);
        cursor.update(1.0, scene.mouse, true);

        cursor.setLocation(10, 20);
        Assert.assertEquals(10, cursor.getLocationX());
        Assert.assertEquals(20, cursor.getLocationY());
        Assert.assertEquals(1.0, cursor.getSensibilityHorizontal(), 0.000001);
        Assert.assertEquals(2.0, cursor.getSensibilityVertical(), 0.000001);
        cursor.setRenderingOffset(0, 0);
        Assert.assertEquals(1, cursor.getWidth());
        Assert.assertEquals(1, cursor.getHeight());
        Assert.assertEquals(0, cursor.getSurfaceId());
        Assert.assertEquals(0, cursor.getClick());
        cursor.render(g);
        cursor.render(g, 0, 0);
        cursor.render(g, 0, 0, 0);
    }

    /**
     * Test the bar class.
     */
    @Test
    public void testBar()
    {
        final Bar bar = new Bar(10, 20);
        bar.setBorderSize(1, 1);
        bar.setColorBackground(Color.WHITE);
        bar.setColorForeground(Color.BLACK);
        bar.setLocation(0, 0);
        bar.setMaximumSize(10, 20);
        bar.setVerticalReferential(true);
        bar.setHorizontalReferential(true);
        bar.setWidthPercent(100);
        bar.setHeightPercent(100);
        Assert.assertEquals(10, bar.getWidthMax());
        Assert.assertEquals(20, bar.getHeightMax());
        Assert.assertEquals(100, bar.getWidthPercent());
        Assert.assertEquals(100, bar.getHeightPercent());
        Assert.assertEquals(10, bar.getWidth());
        Assert.assertEquals(20, bar.getHeight());

        bar.setWidthPercent(50);
        bar.setHeightPercent(50);
        Assert.assertEquals(50, bar.getWidthPercent());
        Assert.assertEquals(50, bar.getHeightPercent());
        Assert.assertEquals(5, bar.getWidth());
        Assert.assertEquals(10, bar.getHeight());

        bar.render(g);
        bar.setColorBackground(null);
        bar.setVerticalReferential(false);
        bar.setHorizontalReferential(true);
        bar.render(g);
        bar.setColorBackground(Color.WHITE);
        bar.setColorForeground(null);
        bar.setVerticalReferential(false);
        bar.setHorizontalReferential(false);
        bar.render(g);
        bar.setColorBackground(null);
        bar.setColorForeground(null);
        bar.setVerticalReferential(true);
        bar.setHorizontalReferential(false);
        bar.render(g);

        bar.setWidthPercent(100);
        bar.setHeightPercent(0);
        bar.render(g);

        bar.setWidthPercent(0);
        bar.setHeightPercent(100);
        bar.render(g);
    }
}
