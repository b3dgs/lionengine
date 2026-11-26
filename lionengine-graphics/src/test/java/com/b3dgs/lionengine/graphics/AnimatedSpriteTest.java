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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.management.ManagementFactory;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.render.CommandBuffer;
import com.b3dgs.lionengine.resources.FakeResourceProvider;
import com.b3dgs.lionengine.resources.ResourceKey;
import com.b3dgs.lionengine.resources.Resources;
import com.b3dgs.lionengine.testsupport.RecordedCommand;
import com.b3dgs.lionengine.testsupport.RecordingBackend;
import com.sun.management.ThreadMXBean;

/**
 * Test {@link AnimatedSprite}: a sprite over a sheet whose frame follows its animation, advanced by the update step
 * only; frames checked against the sheet; rendering that only reads; no allocation (REQ-ANIM-003, REQ-SPR-002,
 * ADR-0058).
 */
final class AnimatedSpriteTest
{
    /** Key of the hero image: 64 by 32 pixels, 8 cells of 16 pixels. */
    private static final ResourceKey HERO = new ResourceKey("sprites/hero.png");
    /** Id of the hero sheet. */
    private static final ResourceKey HERO_SHEET = new ResourceKey("sheets/hero");
    /** Step of the tests in seconds, exact in binary. */
    private static final double STEP = 0.25;
    /** The walk: the cells of the second row, looping. */
    private static final Animation WALK = new Animation(new int[]
    {
        4, 5, 6, 7
    }, STEP, AnimationLoop.LOOP);
    /** Steps of the allocation measurement. */
    private static final int ITERATIONS = 100_000;
    /** Bytes tolerated for the measurement itself: far below one allocation per call. */
    private static final long ALLOCATION_TOLERANCE = 1024L;

    /** The render backend. */
    private RecordingBackend backend;
    /** The group of the hero sheet. */
    private SheetGroup group;
    /** The command buffer. */
    private CommandBuffer commands;
    /** The animated sprite under test, over the hero sheet. */
    private AnimatedSprite hero;

    /**
     * Creates the hero sheet and its animated sprite for each test.
     */
    @BeforeEach
    void prepare()
    {
        final FakeResourceProvider provider = new FakeResourceProvider(false);
        provider.put(HERO, PngWriter.rgba(64, 32, new int[64 * 32]).write());
        backend = new RecordingBackend();
        group = new SheetGroup(new ImageCache(new Resources(provider), backend));
        final SpriteSheet sheet = group.add(HERO_SHEET, HERO, new SheetGrid(16, 16));
        hero = new AnimatedSprite(new Sprite(sheet));
        commands = new CommandBuffer(160, 120, 4);
    }

    /**
     * Renders the animated sprite and returns its command.
     *
     * @return The command of the frame.
     */
    private RecordedCommand render()
    {
        commands.clear();
        hero.render(commands);
        backend.present(commands);
        final List<RecordedCommand> frame = backend.getLastFrame();
        assertEquals(1, frame.size());
        return frame.get(0);
    }

    /**
     * A new animated sprite is stopped, without animation, and draws the frame its sprite had.
     */
    @Test
    void testNew()
    {
        hero.getSprite().setFrame(2);

        assertEquals(AnimatorState.STOPPED, hero.getState());
        assertNull(hero.getAnimation());
        hero.update(STEP);
        hero.stop();
        assertEquals(2, hero.getSprite().getFrame());
        assertEquals(32, render().getSourceX());
        assertThrows(IllegalArgumentException.class, () -> new AnimatedSprite(null));
    }

    /**
     * Each update step advances the animation and gives its cell to the sprite, whose command draws that cell.
     */
    @Test
    void testUpdateDrawsFrames()
    {
        hero.play(WALK);

        assertSame(WALK, hero.getAnimation());
        assertEquals(4, hero.getSprite().getFrame());
        assertEquals(0, render().getSourceX());
        assertEquals(16, backend.getLastFrame().get(0).getSourceY());
        hero.update(STEP);
        assertEquals(5, hero.getSprite().getFrame());
        assertEquals(16, render().getSourceX());
        hero.update(3 * STEP);
        assertEquals(4, hero.getSprite().getFrame());
        assertEquals(0, hero.getFrameIndex());
        assertEquals(0.0, hero.getFrameTime());
    }

    /**
     * Rendering only reads: rendering many times, at interpolated positions too, changes neither the frame, nor the
     * time of the animation, nor the position of the sprite (REQ-LOOP-006).
     */
    @Test
    void testRenderReadsOnly()
    {
        hero.play(WALK);
        hero.getSprite().setPosition(10.0, 20.0);
        hero.update(0.5 * STEP);

        for (int i = 0; i < 10; i++)
        {
            hero.render(commands, 10.0 + i * 0.5, 20.0);
            hero.render(commands);
            backend.present(commands);
        }

        assertEquals(4, hero.getSprite().getFrame());
        assertEquals(0.5 * STEP, hero.getFrameTime());
        assertEquals(10.0, hero.getSprite().getX());
        hero.update(0.5 * STEP);
        assertEquals(5, hero.getSprite().getFrame());
    }

    /**
     * Pause keeps the frame, resume continues, stop shows the first frame, and a {@code once} animation finishing in
     * a step shows its last frame.
     */
    @Test
    void testStates()
    {
        hero.play(WALK);
        hero.update(STEP);
        hero.pause();
        hero.update(STEP);
        assertEquals(5, hero.getSprite().getFrame());
        assertThrows(IllegalStateException.class, hero::pause);
        hero.resume();
        hero.update(STEP);
        assertEquals(6, hero.getSprite().getFrame());
        hero.stop();
        assertEquals(AnimatorState.STOPPED, hero.getState());
        assertEquals(4, hero.getSprite().getFrame());
        assertThrows(IllegalStateException.class, hero::resume);

        hero.play(new Animation(new int[]
        {
            0, 1, 2
        }, STEP, AnimationLoop.ONCE));
        hero.update(10 * STEP);
        assertEquals(AnimatorState.FINISHED, hero.getState());
        assertEquals(2, hero.getSprite().getFrame());
    }

    /**
     * An animation with a frame outside the sprite is refused and changes nothing; an image sprite has one frame.
     */
    @Test
    void testFramesOutside()
    {
        hero.play(WALK);
        hero.update(STEP);

        final Animation outside = new Animation(new int[]
        {
            0, 8
        }, STEP, AnimationLoop.LOOP);
        assertThrows(IllegalArgumentException.class, () -> hero.play(outside));
        assertSame(WALK, hero.getAnimation());
        assertEquals(5, hero.getSprite().getFrame());
        assertThrows(IllegalArgumentException.class, () -> hero.play(null));

        final AnimatedSprite single = new AnimatedSprite(new Sprite(group.get(HERO_SHEET).getImage()));
        assertThrows(IllegalArgumentException.class, () -> single.play(WALK));
        single.play(new Animation(new int[]
        {
            0, 0
        }, STEP, AnimationLoop.PING_PONG));
        assertEquals(AnimatorState.PLAYING, single.getState());
    }

    /**
     * An invalid step is refused and changes nothing; a released image cannot be drawn.
     */
    @Test
    void testInvalid()
    {
        hero.play(WALK);

        assertThrows(IllegalArgumentException.class, () -> hero.update(-STEP));
        assertEquals(4, hero.getSprite().getFrame());
        group.release();
        assertThrows(IllegalStateException.class, () -> hero.render(commands));
    }

    /**
     * Runs update steps and renders, as the game loop would.
     */
    private void steps()
    {
        for (int i = 0; i < ITERATIONS; i++)
        {
            hero.update(STEP);
            hero.render(commands);
            commands.clear();
        }
    }

    /**
     * Updating and rendering allocate nothing in the steady state, measured after a first run that loads and
     * initializes what the first calls need.
     */
    @Test
    void testAllocatesNothing()
    {
        hero.play(new Animation(new int[]
        {
            0, 1, 2, 3, 4, 5, 6, 7
        }, STEP, AnimationLoop.PING_PONG));
        final ThreadMXBean bean = (ThreadMXBean) ManagementFactory.getThreadMXBean();
        steps();

        final long before = bean.getCurrentThreadAllocatedBytes();
        steps();
        final long allocated = bean.getCurrentThreadAllocatedBytes() - before;

        assertTrue(allocated < ALLOCATION_TOLERANCE, "Allocated " + allocated + " bytes");
    }
}
