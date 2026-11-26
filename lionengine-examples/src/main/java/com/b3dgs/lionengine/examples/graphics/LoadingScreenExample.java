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

import com.b3dgs.lionengine.AsyncHandle;
import com.b3dgs.lionengine.AsyncState;
import com.b3dgs.lionengine.Inbox;
import com.b3dgs.lionengine.LoadGroup;
import com.b3dgs.lionengine.Log;
import com.b3dgs.lionengine.LoopControl;
import com.b3dgs.lionengine.WorkerPool;
import com.b3dgs.lionengine.graphics.Image;
import com.b3dgs.lionengine.graphics.ImageCache;
import com.b3dgs.lionengine.render.TextureStore;
import com.b3dgs.lionengine.resources.ResourceKey;
import com.b3dgs.lionengine.resources.Resources;
import com.b3dgs.lionengine.runtime.GameLoop;
import com.b3dgs.lionengine.runtime.LoopConfig;
import com.b3dgs.lionengine.runtime.LoopState;
import com.b3dgs.lionengine.runtime.Scene;
import com.b3dgs.lionengine.runtime.SceneManager;

/**
 * A loading screen: an ordinary scene that loads the images of the level in the background, follows their progress,
 * and switches to the level when they are ready (REQ-RT-004, REQ-RT-010, ADR-0019, ADR-0031).
 *
 * <p>
 * The loading scene requests each image with {@link ImageCache#loadAsync(ResourceKey, WorkerPool, Inbox)}, giving the
 * worker pool of the platform and the inbox of the game loop, and adds the handles to a {@link LoadGroup}. A worker
 * reads and decodes each image; the loop delivers the results at the start of an iteration, on the runtime thread,
 * where the images enter the cache. Each update the scene reads the state of the group: {@code READY} hands the
 * images to the level and switches to it, {@code FAILED} reports the failure; a loading bar would draw
 * {@link LoadGroup#getProgress()}. The level releases the images on exit; a loading scene left before the end cancels
 * the loads still pending and releases the images already loaded. With a pool without worker, as in a headless test,
 * the images are decoded at the request and delivered at the next iteration, before the first update of the scene.
 * </p>
 *
 * @since 11.0.0
 */
public final class LoadingScreenExample
{
    /** Key of the hero image, in the game data. */
    public static final ResourceKey HERO = new ResourceKey("sprites/hero.png");
    /** Key of the tileset image, in the game data. */
    public static final ResourceKey TILES = new ResourceKey("tiles/forest.png");

    /** Duration of a frame of the controllable clock, in nanoseconds: 60 Hz. */
    private static final long FRAME_NANOS = 16_666_667L;
    /** Frames after which the example gives up: 10 seconds. */
    private static final int MAX_FRAMES = 600;

    /**
     * Private constructor.
     */
    private LoadingScreenExample()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Runs the loading screen, then one step of the level, on a controllable clock.
     *
     * @param resources The resources holding {@link #HERO} and {@link #TILES}.
     * @param textures The texture store of the render backend.
     * @param workers The loading worker pool of the platform.
     * @param log The log of the loop.
     * @return The steps spent loading, the progress seen by the loading screen, the sizes of the images received by
     *         the level, and the number of cached images at the end.
     */
    public static Result run(Resources resources, TextureStore textures, WorkerPool workers, Log log)
    {
        final long[] now = new long[1];
        final SceneManager scenes = new SceneManager();
        final GameLoop loop = new GameLoop(LoopConfig.DEFAULT, () -> now[0], log, scenes, scenes);
        final ImageCache cache = new ImageCache(resources, textures);
        final Level level = new Level(loop);
        final Loading loading = new Loading(cache, workers, loop.getInbox(), scenes, level);

        scenes.switchTo(loading);
        int frames = 0;
        while (loop.getState() != LoopState.STOPPED && frames < MAX_FRAMES)
        {
            loop.iterate();
            now[0] += FRAME_NANOS;
            frames++;
        }
        scenes.clear();
        return new Result(loading.steps, loading.firstProgress, level.heroWidth, level.tilesWidth, cache.size());
    }

    /**
     * Result of the example.
     *
     * @param loadingSteps The steps the loading scene ran while the images were not ready.
     * @param firstProgress The progress of the group at the first step of the loading scene.
     * @param heroWidth The width of the hero image received by the level, in pixels.
     * @param tilesWidth The width of the tileset image received by the level, in pixels.
     * @param cachedAtEnd The number of cached images once the scenes were cleared.
     * @since 11.0.0
     */
    public record Result(int loadingSteps, double firstProgress, int heroWidth, int tilesWidth, int cachedAtEnd)
    {
        // Value type of the example.
    }

    /**
     * The loading scene: starts the loads, waits for the group, hands the images over.
     */
    private static final class Loading implements Scene
    {
        /** The image cache of the game. */
        private final ImageCache cache;
        /** The worker pool of the platform. */
        private final WorkerPool workers;
        /** The inbox of the loop. */
        private final Inbox inbox;
        /** The scene manager. */
        private final SceneManager scenes;
        /** The level, receiving the images. */
        private final Level level;
        /** The loads in progress. */
        private final LoadGroup group = new LoadGroup();
        /** The hero load. */
        private AsyncHandle<Image> hero;
        /** The tileset load. */
        private AsyncHandle<Image> tiles;
        /** {@code true} once the images are handed to the level. */
        private boolean handed;
        /** Steps run. */
        private int steps;
        /** Progress at the first step, {@code -1} before. */
        private double firstProgress = -1.0;

        /**
         * Creates the scene.
         *
         * @param cache The image cache of the game.
         * @param workers The worker pool of the platform.
         * @param inbox The inbox of the loop.
         * @param scenes The scene manager.
         * @param level The level, receiving the images.
         */
        Loading(ImageCache cache, WorkerPool workers, Inbox inbox, SceneManager scenes, Level level)
        {
            this.cache = cache;
            this.workers = workers;
            this.inbox = inbox;
            this.scenes = scenes;
            this.level = level;
        }

        @Override
        public void enter()
        {
            hero = cache.loadAsync(HERO, workers, inbox);
            tiles = cache.loadAsync(TILES, workers, inbox);
            group.add(hero);
            group.add(tiles);
        }

        @Override
        public void update(double stepSeconds)
        {
            if (steps == 0)
            {
                firstProgress = group.getProgress();
            }
            final AsyncState state = group.getState();
            if (state == AsyncState.READY && !handed)
            {
                handed = true;
                level.receive(hero.get(), tiles.get());
                scenes.switchTo(level);
            }
            else if (state == AsyncState.FAILED)
            {
                throw group.getFailure();
            }
            else if (!handed)
            {
                steps++;
            }
        }

        @Override
        public void render(double alpha, double unscaledDeltaSeconds)
        {
            // A loading bar would draw group.getProgress() here.
        }

        @Override
        public void exit()
        {
            if (!handed)
            {
                group.cancel();
                release(hero);
                release(tiles);
            }
        }

        @Override
        public void dispose()
        {
            // Nothing else to release: the images belong to the level or were released on exit.
        }

        /**
         * Releases the image of a ready load.
         *
         * @param handle The load.
         */
        private static void release(AsyncHandle<Image> handle)
        {
            if (handle.getState() == AsyncState.READY)
            {
                handle.get().release();
            }
        }
    }

    /**
     * The level: receives the images, runs one step, stops the loop, releases the images on exit.
     */
    private static final class Level implements Scene
    {
        /** The loop to stop. */
        private final LoopControl loop;
        /** The hero image. */
        private Image hero;
        /** The tileset image. */
        private Image tiles;
        /** Width of the hero image. */
        private int heroWidth;
        /** Width of the tileset image. */
        private int tilesWidth;

        /**
         * Creates the level.
         *
         * @param loop The loop to stop after one step.
         */
        Level(LoopControl loop)
        {
            this.loop = loop;
        }

        /**
         * Receives the images loaded by the loading scene.
         *
         * @param heroImage The hero image, now owned by the level.
         * @param tilesImage The tileset image, now owned by the level.
         */
        void receive(Image heroImage, Image tilesImage)
        {
            hero = heroImage;
            tiles = tilesImage;
        }

        @Override
        public void enter()
        {
            heroWidth = hero.getWidth();
            tilesWidth = tiles.getWidth();
        }

        @Override
        public void update(double stepSeconds)
        {
            loop.stop();
        }

        @Override
        public void render(double alpha, double unscaledDeltaSeconds)
        {
            // The level would draw with the textures of its images.
        }

        @Override
        public void exit()
        {
            hero.release();
            tiles.release();
        }

        @Override
        public void dispose()
        {
            // The images were released on exit.
        }
    }
}
