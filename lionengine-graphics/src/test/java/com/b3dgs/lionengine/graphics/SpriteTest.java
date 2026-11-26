/*
 * Copyright (C) 2013-2026 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.graphics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.management.ManagementFactory;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.b3dgs.lionengine.render.CommandBuffer;
import com.b3dgs.lionengine.resources.FakeResourceProvider;
import com.b3dgs.lionengine.resources.ResourceException;
import com.b3dgs.lionengine.resources.ResourceKey;
import com.b3dgs.lionengine.resources.Resources;
import com.b3dgs.lionengine.testsupport.RecordedCommand;
import com.b3dgs.lionengine.testsupport.RecordingBackend;
import com.sun.management.ThreadMXBean;

/**
 * Test {@link Sprite}: loading, frames, transform and pivot, command emission, layers, released sources and the
 * absence of allocation (REQ-SPRITE-001, REQ-SPRITE-003, REQ-SPRITE-006, REQ-SPRITE-007, REQ-SPRITE-010).
 */
final class SpriteTest
{
    /** Key of the hero image: 64 by 32 pixels. */
    private static final ResourceKey HERO = new ResourceKey("sprites/hero.png");
    /** Key of the sign image: 6 by 4 pixels. */
    private static final ResourceKey SIGN = new ResourceKey("sprites/sign.png");
    /** Id of the hero sheet. */
    private static final ResourceKey HERO_SHEET = new ResourceKey("sheets/hero");
    /** Logical frame width. */
    private static final int WIDTH = 160;
    /** Logical frame height. */
    private static final int HEIGHT = 120;
    /** Sprites rendered per frame in the allocation measurement. */
    private static final int SPRITES = 64;
    /** Frames of the allocation measurement. */
    private static final int FRAMES = 1000;
    /** Bytes tolerated for the measurement itself: far below one allocation per call. */
    private static final long ALLOCATION_TOLERANCE = 1024L;

    /**
     * Returns where a point of the frame of a command is drawn, horizontally, as the command buffer composes an entry:
     * scale, then rotation counter-clockwise on screen (Y down), then translation, about the pivot.
     *
     * @param command The command.
     * @param frameX The horizontal position of the point in the frame, in pixels from its left edge.
     * @param frameY The vertical position of the point in the frame, in pixels from its top edge.
     * @return The horizontal position on the logical frame.
     */
    private static double drawnX(RecordedCommand command, double frameX, double frameY)
    {
        final double scaledX = (frameX - command.getPivotX()) * command.getScaleX();
        final double scaledY = (frameY - command.getPivotY()) * command.getScaleY();
        final double radians = Math.toRadians(command.getRotation());
        return command.getX() + scaledX * Math.cos(radians) + scaledY * Math.sin(radians);
    }

    /**
     * Returns where a point of the frame of a command is drawn, vertically, as the command buffer composes an entry.
     *
     * @param command The command.
     * @param frameX The horizontal position of the point in the frame, in pixels from its left edge.
     * @param frameY The vertical position of the point in the frame, in pixels from its top edge.
     * @return The vertical position on the logical frame, Y down.
     */
    private static double drawnY(RecordedCommand command, double frameX, double frameY)
    {
        final double scaledX = (frameX - command.getPivotX()) * command.getScaleX();
        final double scaledY = (frameY - command.getPivotY()) * command.getScaleY();
        final double radians = Math.toRadians(command.getRotation());
        return command.getY() - scaledX * Math.sin(radians) + scaledY * Math.cos(radians);
    }

    /** The provider of the images. */
    private FakeResourceProvider provider;
    /** The render backend: texture store and recorder of the presented frames. */
    private RecordingBackend backend;
    /** The image cache. */
    private ImageCache cache;
    /** The command buffer. */
    private CommandBuffer commands;

    /**
     * Creates the cache, the backend and the buffer of each test, with the hero and sign images.
     */
    @BeforeEach
    void prepare()
    {
        provider = new FakeResourceProvider(false);
        provider.put(HERO, PngWriter.rgba(64, 32, new int[64 * 32]).write());
        provider.put(SIGN, PngWriter.rgba(6, 4, new int[6 * 4]).write());
        backend = new RecordingBackend();
        cache = new ImageCache(new Resources(provider), backend);
        commands = new CommandBuffer(WIDTH, HEIGHT, 4);
    }

    /**
     * Renders the buffer and returns the presented commands.
     *
     * @return The commands of the frame, by layer then submission order.
     */
    private List<RecordedCommand> present()
    {
        backend.present(commands);
        return backend.getLastFrame();
    }

    /**
     * A sprite is loaded through the cache from a resource key; its only frame is the whole image, its pivot the
     * center of the image, and its transform the identity.
     */
    @Test
    void testImage()
    {
        final Image image = cache.load(SIGN);
        final Sprite sprite = new Sprite(image);

        assertSame(image, sprite.getImage());
        assertEquals(6, sprite.getWidth());
        assertEquals(4, sprite.getHeight());
        assertEquals(1, sprite.getFrameCount());
        assertEquals(0, sprite.getFrame());
        assertEquals(3.0, sprite.getPivotX());
        assertEquals(2.0, sprite.getPivotY());
        assertEquals(0.0, sprite.getX());
        assertEquals(0.0, sprite.getY());
        assertEquals(1.0, sprite.getScaleX());
        assertEquals(1.0, sprite.getScaleY());
        assertEquals(0.0, sprite.getRotation());
        assertEquals(0, sprite.getLayer());
    }

    /**
     * The default pivot of an image of odd size is the exact center, between two pixels.
     */
    @Test
    void testOddCenter()
    {
        final ResourceKey key = new ResourceKey("sprites/odd.png");
        provider.put(key, PngWriter.rgba(5, 3, new int[5 * 3]).write());

        final Sprite sprite = new Sprite(cache.load(key));

        assertEquals(2.5, sprite.getPivotX());
        assertEquals(1.5, sprite.getPivotY());
    }

    /**
     * Loading a sprite is loading its image: a missing image is the resource exception of its key, invalid data the
     * decoding exception, so no sprite can be created, and nothing stays cached.
     */
    @Test
    void testLoadErrors()
    {
        final ResourceKey missing = new ResourceKey("sprites/missing.png");
        final ResourceKey invalid = new ResourceKey("sprites/invalid.png");
        provider.put(invalid, "Not a PNG image".getBytes(StandardCharsets.UTF_8));

        final ResourceException exception = assertThrows(ResourceException.class, () -> cache.load(missing));
        assertEquals(ResourceException.Reason.MISSING, exception.getReason());
        assertThrows(ImageDecodingException.class, () -> cache.load(invalid));
        assertEquals(0, cache.size());
    }

    /**
     * A sprite over a sheet shows its cells as frames, the first one first, with the pivot of the sheet; a frame is
     * drawn from its cell.
     */
    @Test
    void testSheet()
    {
        final SheetGroup group = new SheetGroup(cache);
        final SpriteSheet sheet = group.add(HERO_SHEET, HERO, new SheetGrid(16, 16), 4.0, 12.0);
        final Sprite sprite = new Sprite(sheet);

        assertSame(sheet.getImage(), sprite.getImage());
        assertEquals(16, sprite.getWidth());
        assertEquals(16, sprite.getHeight());
        assertEquals(8, sprite.getFrameCount());
        assertEquals(4.0, sprite.getPivotX());
        assertEquals(12.0, sprite.getPivotY());

        sprite.render(commands);
        sprite.setFrame(5);
        sprite.render(commands);
        final List<RecordedCommand> frame = present();

        assertEquals(5, sprite.getFrame());
        assertEquals(0, frame.get(0).getSourceX());
        assertEquals(0, frame.get(0).getSourceY());
        assertEquals(16, frame.get(1).getSourceX());
        assertEquals(16, frame.get(1).getSourceY());
        assertEquals(16, frame.get(1).getSourceWidth());
        assertEquals(16, frame.get(1).getSourceHeight());
        group.release();
    }

    /**
     * A frame outside the sprite is refused and the frame shown does not change.
     */
    @Test
    void testFrameOutside()
    {
        final SheetGroup group = new SheetGroup(cache);
        final Sprite hero = new Sprite(group.add(HERO_SHEET, HERO, new SheetGrid(16, 16)));
        final Sprite sign = new Sprite(cache.load(SIGN));
        hero.setFrame(7);

        assertThrows(IndexOutOfBoundsException.class, () -> hero.setFrame(8));
        assertThrows(IndexOutOfBoundsException.class, () -> hero.setFrame(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> sign.setFrame(1));
        sign.setFrame(0);
        assertEquals(7, hero.getFrame());
        assertEquals(0, sign.getFrame());
        group.release();
    }

    /**
     * Rendering adds one entry with the texture of the image, the source rectangle of the frame, the position, scale,
     * rotation, pivot and layer of the sprite, no tint and full opacity; the sprite does not change.
     */
    @Test
    void testRender()
    {
        final Image image = cache.load(SIGN);
        final Sprite sprite = new Sprite(image);
        sprite.setPosition(40.5, 24.25);
        sprite.setScale(2.0, -3.0);
        sprite.setRotation(30.0);
        sprite.setPivot(1.0, 7.5);
        sprite.setLayer(3);

        sprite.render(commands);
        final RecordedCommand command = present().get(0);

        assertEquals(1, commands.size());
        assertEquals(image.getTexture(), command.getTexture());
        assertEquals(0, command.getSourceX());
        assertEquals(0, command.getSourceY());
        assertEquals(6, command.getSourceWidth());
        assertEquals(4, command.getSourceHeight());
        assertEquals(40.5F, command.getX());
        assertEquals(24.25F, command.getY());
        assertEquals(2.0F, command.getScaleX());
        assertEquals(-3.0F, command.getScaleY());
        assertEquals(30.0F, command.getRotation());
        assertEquals(1.0F, command.getPivotX());
        assertEquals(7.5F, command.getPivotY());
        assertEquals(3, command.getLayer());
        assertEquals(CommandBuffer.NO_TINT, command.getTint());
        assertEquals(1.0F, command.getAlpha());
        assertEquals(40.5, sprite.getX());
        assertEquals(24.25, sprite.getY());
    }

    /**
     * A position given to the render, interpolated or converted by the camera, is drawn without changing the sprite.
     */
    @Test
    void testRenderAt()
    {
        final Sprite sprite = new Sprite(cache.load(SIGN));
        sprite.setPosition(10.0, 20.0);

        sprite.render(commands, 12.5, 18.0);
        final RecordedCommand command = present().get(0);

        assertEquals(12.5F, command.getX());
        assertEquals(18.0F, command.getY());
        assertEquals(10.0, sprite.getX());
        assertEquals(20.0, sprite.getY());
    }

    /**
     * The pivot is drawn at the position: with the default pivot the frame is centered on the position, with a
     * top-left pivot it starts at the position; scaling and rotating keep the pivot in place.
     */
    @Test
    void testPivotPlacement()
    {
        final Sprite centered = new Sprite(cache.load(SIGN));
        final Sprite corner = new Sprite(cache.load(SIGN));
        corner.setPivot(0.0, 0.0);
        centered.setPosition(40.0, 24.0);
        corner.setPosition(40.0, 24.0);

        centered.render(commands);
        corner.render(commands);
        centered.setScale(2.0, 2.0);
        centered.setRotation(90.0);
        centered.render(commands);
        final List<RecordedCommand> frame = present();

        assertEquals(37.0, drawnX(frame.get(0), 0.0, 0.0), 1.0E-9);
        assertEquals(22.0, drawnY(frame.get(0), 0.0, 0.0), 1.0E-9);
        assertEquals(40.0, drawnX(frame.get(1), 0.0, 0.0), 1.0E-9);
        assertEquals(24.0, drawnY(frame.get(1), 0.0, 0.0), 1.0E-9);
        assertEquals(40.0, drawnX(frame.get(2), 3.0, 2.0), 1.0E-9);
        assertEquals(24.0, drawnY(frame.get(2), 3.0, 2.0), 1.0E-9);
    }

    /**
     * Scale, then rotation, then translation: a 6 by 4 frame with a top-left pivot, stretched to 12 by 4 then turned
     * by 90 degrees counter-clockwise on screen, rises 12 pixels above its position and spreads 4 pixels to its right;
     * the rotation is the command rotation, unchanged, and a negative scale mirrors the frame about the pivot.
     */
    @Test
    void testComposition()
    {
        final Sprite sprite = new Sprite(cache.load(SIGN));
        sprite.setPivot(0.0, 0.0);
        sprite.setPosition(40.0, 24.0);
        sprite.setScale(2.0, 1.0);
        sprite.setRotation(90.0);

        sprite.render(commands);
        sprite.setScale(-1.0, 1.0);
        sprite.setRotation(0.0);
        sprite.render(commands);
        final List<RecordedCommand> frame = present();

        assertEquals(90.0F, frame.get(0).getRotation());
        assertEquals(40.0, drawnX(frame.get(0), 6.0, 0.0), 1.0E-9);
        assertEquals(12.0, drawnY(frame.get(0), 6.0, 0.0), 1.0E-9);
        assertEquals(44.0, drawnX(frame.get(0), 0.0, 4.0), 1.0E-9);
        assertEquals(24.0, drawnY(frame.get(0), 0.0, 4.0), 1.0E-9);
        assertEquals(34.0, drawnX(frame.get(1), 6.0, 0.0), 1.0E-9);
        assertEquals(24.0, drawnY(frame.get(1), 6.0, 0.0), 1.0E-9);
    }

    /**
     * The rotation is kept as given, not normalized (ADR-0016), and handed to the command as is.
     *
     * @param degrees The rotation.
     */
    @ParameterizedTest
    @ValueSource(doubles =
    {
        -450.0, -90.0, 360.0, 725.5
    })
    void testRotationKept(double degrees)
    {
        final Sprite sprite = new Sprite(cache.load(SIGN));

        sprite.setRotation(degrees);
        sprite.render(commands);

        assertEquals(degrees, sprite.getRotation());
        assertEquals((float) degrees, present().get(0).getRotation());
    }

    /**
     * Commands are presented by ascending layer, then submission order.
     */
    @Test
    void testLayers()
    {
        final Image image = cache.load(SIGN);
        final Sprite front = new Sprite(image);
        final Sprite back = new Sprite(image);
        final Sprite middle = new Sprite(image);
        front.setLayer(2);
        middle.setLayer(1);
        front.setPosition(1.0, 0.0);
        back.setPosition(2.0, 0.0);
        middle.setPosition(3.0, 0.0);

        front.render(commands);
        back.render(commands);
        middle.render(commands);
        back.render(commands, 4.0, 0.0);
        final List<RecordedCommand> frame = present();

        assertEquals(2.0F, frame.get(0).getX());
        assertEquals(4.0F, frame.get(1).getX());
        assertEquals(3.0F, frame.get(2).getX());
        assertEquals(1.0F, frame.get(3).getX());
    }

    /**
     * A released image cannot make a sprite; a sprite whose image is released, alone or with its sheet group, adds
     * nothing and throws.
     */
    @Test
    void testReleased()
    {
        final Image image = cache.load(SIGN);
        final Sprite sprite = new Sprite(image);
        final SheetGroup group = new SheetGroup(cache);
        final SpriteSheet sheet = group.add(HERO_SHEET, HERO, new SheetGrid(16, 16));
        final Sprite hero = new Sprite(sheet);

        image.release();
        group.release();

        assertThrows(IllegalStateException.class, () -> new Sprite(image));
        assertThrows(IllegalStateException.class, () -> new Sprite(sheet));
        assertThrows(IllegalStateException.class, () -> sprite.render(commands));
        assertThrows(IllegalStateException.class, () -> hero.render(commands, 0.0, 0.0));
        assertEquals(0, commands.size());
        assertEquals(6, sprite.getWidth());
        assertEquals(16, hero.getWidth());
    }

    /**
     * Arguments that are {@code null} or not finite are refused and change nothing.
     *
     * @param value The value that is not finite.
     */
    @ParameterizedTest
    @ValueSource(doubles =
    {
        Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY
    })
    void testInvalidValues(double value)
    {
        final Sprite sprite = new Sprite(cache.load(SIGN));

        assertThrows(IllegalArgumentException.class, () -> sprite.setPosition(value, 0.0));
        assertThrows(IllegalArgumentException.class, () -> sprite.setPosition(0.0, value));
        assertThrows(IllegalArgumentException.class, () -> sprite.setScale(value, 1.0));
        assertThrows(IllegalArgumentException.class, () -> sprite.setScale(1.0, value));
        assertThrows(IllegalArgumentException.class, () -> sprite.setRotation(value));
        assertThrows(IllegalArgumentException.class, () -> sprite.setPivot(value, 0.0));
        assertThrows(IllegalArgumentException.class, () -> sprite.setPivot(0.0, value));
        assertThrows(IllegalArgumentException.class, () -> sprite.render(commands, value, 0.0));
        assertThrows(IllegalArgumentException.class, () -> sprite.render(commands, 0.0, value));
        assertEquals(0, commands.size());
        assertEquals(0.0, sprite.getX());
        assertEquals(1.0, sprite.getScaleY());
        assertEquals(0.0, sprite.getRotation());
        assertEquals(3.0, sprite.getPivotX());
    }

    /**
     * {@code null} arguments are refused.
     */
    @Test
    void testNull()
    {
        final Sprite sprite = new Sprite(cache.load(SIGN));

        assertThrows(IllegalArgumentException.class, () -> new Sprite((Image) null));
        assertThrows(IllegalArgumentException.class, () -> new Sprite((SpriteSheet) null));
        assertThrows(IllegalArgumentException.class, () -> sprite.render(null));
        assertThrows(IllegalArgumentException.class, () -> sprite.render(null, 0.0, 0.0));
    }

    /**
     * Once the buffer fits the busiest frame, changing and rendering sprites allocates nothing, measured with the
     * per-thread allocation counter of the JVM (ADR-0019).
     */
    @Test
    void testSteadyStateAllocatesNothing()
    {
        final ThreadMXBean bean = (ThreadMXBean) ManagementFactory.getThreadMXBean();
        final SheetGroup group = new SheetGroup(cache);
        final SpriteSheet sheet = group.add(HERO_SHEET, HERO, new SheetGrid(16, 16));
        final Sprite[] sprites = new Sprite[SPRITES];
        for (int i = 0; i < SPRITES; i++)
        {
            sprites[i] = new Sprite(sheet);
        }
        long checksum = frames(sprites);

        final long before = bean.getCurrentThreadAllocatedBytes();
        checksum += frames(sprites);
        final long allocated = bean.getCurrentThreadAllocatedBytes() - before;

        assertTrue(allocated < ALLOCATION_TOLERANCE, () -> "Allocated bytes: " + allocated);
        assertTrue(checksum > 0L);
        group.release();
    }

    /**
     * Updates and renders sprites during many frames.
     *
     * @param sprites The sprites.
     * @return A checksum of what was rendered.
     */
    private long frames(Sprite[] sprites)
    {
        long checksum = 0L;
        for (int frame = 0; frame < FRAMES; frame++)
        {
            commands.clear();
            for (int i = 0; i < sprites.length; i++)
            {
                final Sprite sprite = sprites[i];
                sprite.setFrame((frame + i) % sprite.getFrameCount());
                sprite.setPosition(i, frame);
                sprite.setScale(1.0, -1.0);
                sprite.setRotation(frame);
                sprite.setPivot(i, 0.0);
                sprite.setLayer(i % 3);
                sprite.render(commands);
            }
            for (int rank = 0; rank < commands.size(); rank++)
            {
                checksum += commands.getSourceX(commands.getEntry(rank)) + 1L;
            }
        }
        return checksum;
    }
}
