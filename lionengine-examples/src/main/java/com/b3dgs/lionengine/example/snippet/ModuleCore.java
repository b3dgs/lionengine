/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.example.snippet;

import java.io.IOException;

import org.junit.Assert;

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Timing;
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.anim.Anim;
import com.b3dgs.lionengine.anim.Animation;
import com.b3dgs.lionengine.anim.Animator;
import com.b3dgs.lionengine.core.Config;
import com.b3dgs.lionengine.core.Context;
import com.b3dgs.lionengine.core.Graphics;
import com.b3dgs.lionengine.core.Loader;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.core.Resolution;
import com.b3dgs.lionengine.core.Sequence;
import com.b3dgs.lionengine.core.Version;
import com.b3dgs.lionengine.core.awt.EngineAwt;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.Image;
import com.b3dgs.lionengine.drawable.Sprite;
import com.b3dgs.lionengine.drawable.SpriteAnimated;
import com.b3dgs.lionengine.drawable.SpriteTiled;
import com.b3dgs.lionengine.game.Camera;
import com.b3dgs.lionengine.game.Services;
import com.b3dgs.lionengine.game.handler.Handler;
import com.b3dgs.lionengine.game.object.Factory;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.ImageInfo;
import com.b3dgs.lionengine.graphic.Text;
import com.b3dgs.lionengine.graphic.TextStyle;
import com.b3dgs.lionengine.graphic.Viewer;
import com.b3dgs.lionengine.stream.FileReading;
import com.b3dgs.lionengine.stream.FileWriting;
import com.b3dgs.lionengine.stream.Stream;
import com.b3dgs.lionengine.stream.Xml;
import com.b3dgs.lionengine.stream.XmlNode;
import com.b3dgs.lionengine.util.UtilChecksum;

public class ModuleCore
{
    int value = 0;
    double extrp = 0f;
    Graphic g = null;

    /*
     * Snippet code
     */

    private final Services services = new Services();
    private final Text text = services.add(Graphics.createText(Text.SANS_SERIF, 9, TextStyle.NORMAL));

    // Method 1
    private final Factory factory1 = services.create(Factory.class); // Already added !
    private final Camera camera1 = services.create(Camera.class); // Already added !
    private final Handler handler1 = services.create(Handler.class); // Already added !

    // Method 2
    private final Factory factory2 = new Factory(services);
    private final Camera camera2 = new Camera();
    private final Handler handler2 = new Handler(services);

    void services()
    {
        final Services services = new Services();
        services.add(new Camera());
        final Viewer viewer = services.get(Viewer.class); // Get the camera as viewer
    }

    void check()
    {
        Check.superiorStrict(value, 0);
        final Object object = null;
        Check.notNull(object);
    }

    void checksum()
    {
        final int integer = 489464795;
        final String value = "keyToBeEncoded";
        final String other = "anotherKey";
        final String signature = UtilChecksum.getSha256(value);
        final String test = UtilChecksum.getSha256(integer);

        Assert.assertTrue(UtilChecksum.checkSha256(value, signature));
        Assert.assertFalse(UtilChecksum.checkSha256(other, signature));
        Assert.assertTrue(UtilChecksum.checkSha256(integer, test));
    }

    void config()
    {
        final Resolution output = new Resolution(640, 480, 60);
        final Config config = new Config(output, 16, true);
    }

    void imageInfo()
    {
        final ImageInfo info = ImageInfo.get(Medias.create("dot.png"));
        Assert.assertEquals(64, info.getWidth());
        Assert.assertEquals(32, info.getHeight());
        Assert.assertEquals("png", info.getFormat());
    }

    void loader()
    {
        EngineAwt.start("First Code", Version.create(1, 0, 0), "resources");
        final Resolution output = new Resolution(640, 480, 60);
        final Config config = new Config(output, 16, true);
        final Loader loader = new Loader();
        loader.start(config, Scene.class);
    }

    void media()
    {
        EngineAwt.start("First Code", Version.create(1, 0, 0), "resources");
        Medias.create("img", "image.png");
        System.out.println(Medias.create()); // print: resources/img/image.png
    }

    class Scene extends Sequence
    {
        Scene(Context context)
        {
            super(context, new Resolution(320, 240, 60));
        }

        @Override
        public void load()
        {
        }

        @Override
        public void update(double extrp)
        {
        }

        @Override
        public void render(Graphic g)
        {
        }
    }

    void text()
    {
        // Create the text
        final Text text = Graphics.createText(Text.SANS_SERIF, 12, TextStyle.NORMAL);

        // Rendering type 1
        text.setText("Hello");
        text.setLocation(0, 0);
        text.setAlign(Align.CENTER);
        text.render(g);

        // Rendering type 2
        text.draw(g, 0, 0, "World");
    }

    void timing() throws InterruptedException
    {
        final Timing timer = new Timing();
        Assert.assertFalse(timer.isStarted());
        timer.start();
        Assert.assertTrue(timer.isStarted());

        Thread.sleep(100);

        Assert.assertTrue(timer.isStarted());
        Assert.assertTrue(timer.elapsed(100));
        Assert.assertTrue(timer.elapsed() >= 100);
        timer.pause();

        Thread.sleep(100);

        timer.unpause();
        Assert.assertFalse(timer.elapsed(200000000));
        timer.stop();
        Assert.assertFalse(timer.isStarted());
        Assert.assertTrue(timer.get() >= 0);
    }

    void verbose()
    {
        Verbose.info("Code reached");
        try
        {
            Thread.sleep(1000);
            Verbose.warning("Warning level here");
            Verbose.warning(ModuleCore.class, "function", "Warning level here");
            Verbose.critical(ModuleCore.class, "function", "Critical level here");
        }
        catch (final InterruptedException exception)
        {
            Thread.currentThread().interrupt();
            Verbose.exception(exception);
        }
    }

    void version()
    {
        final Version version = Version.create(1, 0, 0);
    }

    void animation()
    {
        final Animation animation = Anim.createAnimation(null, 4, 6, 0.125, false, true);
        System.out.println(animation.getFirst()); // 4
        System.out.println(animation.getLast()); // 6
        System.out.println(animation.getSpeed()); // 0.125
        System.out.println(animation.getReverse()); // false
        System.out.println(animation.getRepeat()); // true
    }

    void animator()
    {
        final Animator animator = Anim.createAnimator();
        final Animation animation = Anim.createAnimation(null, 4, 6, 0.125, false, true);
        animator.play(animation);

        // ... (loop)
        animator.update(extrp);
        // (loop) ...
    }

    void animState()
    {
        final Animator animator = Anim.createAnimator();
        final Animation animation = Anim.createAnimation(null, 1, 2, 1.0, false, false);
        animator.getAnimState(); // returns STOPPED
        animator.play(animation);
        animator.update(extrp);
        animator.getAnimState(); // returns PLAYING
    }

    void fileReading()
    {
        final Media file = Medias.create("test.txt");
        try (FileReading reading = Stream.createFileReading(file))
        {
            reading.readBoolean();
            reading.readByte();
            reading.readChar();
            reading.readShort();
            reading.readInteger();
            reading.readFloat();
            reading.readLong();
            reading.readDouble();
        }
        catch (final IOException exception)
        {
            Assert.fail(exception.getMessage());
        }
    }

    void fileWriting()
    {
        final Media file = Medias.create("test.txt");
        try (FileWriting writing = Stream.createFileWriting(file))
        {
            writing.writeBoolean(true);
            writing.writeByte((byte) 1);
            writing.writeChar('c');
            writing.writeShort((short) 2);
            writing.writeInteger(1);
            writing.writeFloat(5.1f);
            writing.writeLong(6L);
            writing.writeDouble(7.1);
        }
        catch (final IOException exception)
        {
            Assert.fail(exception.getMessage());
        }
    }

    void xmlNode()
    {
        final XmlNode node = Xml.create("node");
        node.writeBoolean("value", true);
    }

    void image()
    {
        // Load
        final Image image = Drawable.loadImage(Medias.create("image.png"));
        image.setLocation(10, 50);

        // Render
        image.render(g);
    }

    void sprite()
    {
        // Load
        final Sprite sprite = Drawable.loadSprite(Medias.create("sprite.png"));
        sprite.load();
        sprite.prepare();
        sprite.setLocation(64, 280);

        // Render
        sprite.render(g);
    }

    void spriteTiled()
    {
        // Load
        final SpriteTiled tilesheet = Drawable.loadSpriteTiled(Medias.create("tilesheet.png"), 16, 16);
        tilesheet.load();
        tilesheet.prepare();
        tilesheet.setLocation(300, 300);
        tilesheet.setTile(1);

        // Render
        tilesheet.render(g);
    }

    void spriteAnimated()
    {
        // Load
        final SpriteAnimated animation = Drawable.loadSpriteAnimated(Medias.create("animation.png"), 7, 1);
        animation.load();
        animation.prepare();

        final Animation anim = Anim.createAnimation(null, 4, 6, 0.125, false, true);
        animation.play(anim);
        animation.setLocation(160, 300);

        // Update
        animation.update(extrp);

        // Render
        animation.render(g);
    }
}
