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
import com.b3dgs.lionengine.WorkerPool;
import com.b3dgs.lionengine.graphics.Image;
import com.b3dgs.lionengine.graphics.ImageCache;
import com.b3dgs.lionengine.runtime.Scene;
import com.b3dgs.lionengine.runtime.SceneManager;

/**
 * The loading scene of {@link LoadingScreenExample}: starts the loads in {@link #enter()}, waits for the group, hands
 * the images to the level and switches to it, or cancels and releases on an early exit (REQ-RT-004, REQ-RT-010).
 */
final class LoadingScene implements Scene
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
    private final LevelScene level;
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
    LoadingScene(ImageCache cache, WorkerPool workers, Inbox inbox, SceneManager scenes, LevelScene level)
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
        hero = cache.loadAsync(LoadingScreenExample.HERO, workers, inbox);
        tiles = cache.loadAsync(LoadingScreenExample.TILES, workers, inbox);
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

    /**
     * Returns the steps run while the images were not ready.
     *
     * @return The steps.
     */
    int getSteps()
    {
        return steps;
    }

    /**
     * Returns the progress of the group at the first step.
     *
     * @return The progress, {@code -1} before the first step.
     */
    double getFirstProgress()
    {
        return firstProgress;
    }
}
