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
import com.b3dgs.lionengine.WorkerPool;
import com.b3dgs.lionengine.graphics.ImageCache;
import com.b3dgs.lionengine.render.TextureStore;
import com.b3dgs.lionengine.resources.ResourceKey;
import com.b3dgs.lionengine.resources.Resources;
import com.b3dgs.lionengine.runtime.GameLoop;
import com.b3dgs.lionengine.runtime.LoopConfig;
import com.b3dgs.lionengine.runtime.LoopState;
import com.b3dgs.lionengine.runtime.SceneManager;

/**
 * A loading screen: an ordinary scene that loads the images of the level in the background, follows their progress,
 * and switches to the level when they are ready (REQ-RT-004, REQ-RT-010, ADR-0019, ADR-0031).
 *
 * <p>
 * The loading scene ({@link LoadingScene}) requests each image with
 * {@link ImageCache#loadAsync(ResourceKey, WorkerPool, com.b3dgs.lionengine.Inbox)}, giving the worker pool of the
 * platform and the inbox of the game loop, and adds the handles to a {@link com.b3dgs.lionengine.LoadGroup}. A worker
 * reads and decodes each image; the loop delivers the results at the start of an iteration, on the runtime thread,
 * where the images enter the cache. Each update the scene reads the state of the group: {@code READY} hands the
 * images to the level ({@link LevelScene}) and switches to it, {@code FAILED} reports the failure; a loading bar would
 * draw {@link com.b3dgs.lionengine.LoadGroup#getProgress()}. The level releases the images on exit; a loading scene
 * left before the end cancels the loads still pending and releases the images already loaded. With a pool without
 * worker, as in a headless test, the images are decoded at the request and delivered at the next iteration, before
 * the first update of the scene.
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
        final LevelScene level = new LevelScene(loop);
        final LoadingScene loading = new LoadingScene(cache, workers, loop.getInbox(), scenes, level);

        scenes.switchTo(loading);
        int frames = 0;
        while (loop.getState() != LoopState.STOPPED && frames < MAX_FRAMES)
        {
            loop.iterate();
            now[0] += FRAME_NANOS;
            frames++;
        }
        scenes.clear();
        return new Result(loading.getSteps(),
                          loading.getFirstProgress(),
                          level.getHeroWidth(),
                          level.getTilesWidth(),
                          cache.size());
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
}
