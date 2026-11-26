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
package com.b3dgs.lionengine.examples.graphics;

import com.b3dgs.lionengine.Log;
import com.b3dgs.lionengine.graphics.ImageCache;
import com.b3dgs.lionengine.graphics.Sprite;
import com.b3dgs.lionengine.render.CommandBuffer;
import com.b3dgs.lionengine.render.RenderBackend;
import com.b3dgs.lionengine.resources.ResourceKey;
import com.b3dgs.lionengine.resources.Resources;
import com.b3dgs.lionengine.runtime.GameLoop;
import com.b3dgs.lionengine.runtime.LoopConfig;
import com.b3dgs.lionengine.runtime.SceneManager;

/**
 * Draws two sprites in the game loop: a hero cut from a sprite sheet, walking and turning, and a sign showing a whole
 * image (REQ-SPRITE-001, REQ-SPRITE-003, REQ-SPRITE-006, REQ-SPRITE-007).
 *
 * <p>
 * The scene ({@link HeroScene}) loads its images on enter: the hero through a sheet group, whose cells are the frames
 * of its {@link Sprite}, and the sign through the {@link ImageCache}, its whole image being the only frame. A sprite
 * holds no image: the scene releases the group and the image on exit. Each update step changes the sprites, here the
 * rotation of the hero; each frame the render clears the command buffer, the scene adds one command per sprite, and
 * the backend presents the buffer. The sign is drawn at the position set on its sprite, with its pivot moved to its
 * top-left corner; the hero at a position interpolated between the last two steps and given to the render, so that
 * rendering never changes a sprite. The hero is drawn at twice its size, about its pivot, the center of its frame.
 * </p>
 *
 * @since 11.0.0
 */
public final class SpriteExample
{
    /** Key of the hero image, in the game data: 64 by 32 pixels, eight frames of 16 by 16 pixels. */
    public static final ResourceKey HERO_IMAGE = new ResourceKey("sprites/hero.png");
    /** Key of the sign image, in the game data: 16 by 8 pixels. */
    public static final ResourceKey SIGN_IMAGE = new ResourceKey("sprites/sign.png");
    /** Logical frame width in pixels. */
    public static final int WIDTH = 160;
    /** Logical frame height in pixels. */
    public static final int HEIGHT = 120;

    /** Duration of a frame of the controllable clock in nanoseconds: a 60 Hz display. */
    private static final long FRAME_NANOS = 16_666_667L;
    /** Initial capacity of the command buffer: the two sprites. */
    private static final int CAPACITY = 2;

    /**
     * Private constructor.
     */
    private SpriteExample()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Runs the scene during a number of frames with a controllable clock, then leaves it.
     *
     * @param resources The resources holding {@link #HERO_IMAGE} and {@link #SIGN_IMAGE}.
     * @param backend The render backend, which presents each frame.
     * @param log The log of the game loop.
     * @param frames The number of frames to run.
     * @return The number of update steps and of cached images at the end.
     */
    public static Result run(Resources resources, RenderBackend backend, Log log, int frames)
    {
        final long[] now = new long[1];
        final ImageCache cache = new ImageCache(resources, backend.getTextureStore());
        final CommandBuffer commands = new CommandBuffer(WIDTH, HEIGHT, CAPACITY);
        final SceneManager scenes = new SceneManager();
        final GameLoop loop = new GameLoop(LoopConfig.DEFAULT, () -> now[0], log, scenes, (alpha, delta) ->
        {
            commands.clear();
            scenes.render(alpha, delta);
            backend.present(commands);
        });
        final HeroScene scene = new HeroScene(cache, commands);

        scenes.switchTo(scene);
        for (int frame = 0; frame < frames; frame++)
        {
            loop.iterate();
            now[0] += FRAME_NANOS;
        }
        scenes.clear();
        return new Result(scene.getSteps(), cache.size());
    }

    /**
     * Result of the example.
     *
     * @param steps The number of update steps run.
     * @param cachedAtEnd The number of cached images once the scene is left.
     * @since 11.0.0
     */
    public record Result(int steps, int cachedAtEnd)
    {
        // Value type of the example.
    }
}
