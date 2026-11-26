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

import com.b3dgs.lionengine.LoopControl;
import com.b3dgs.lionengine.graphics.Image;
import com.b3dgs.lionengine.runtime.Scene;

/**
 * The level of {@link LoadingScreenExample}: receives the images, runs one step, stops the loop, and releases the
 * images on exit.
 */
final class LevelScene implements Scene
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
    LevelScene(LoopControl loop)
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

    /**
     * Returns the width of the hero image received.
     *
     * @return The width in pixels, {@code 0} before {@link #enter()}.
     */
    int getHeroWidth()
    {
        return heroWidth;
    }

    /**
     * Returns the width of the tileset image received.
     *
     * @return The width in pixels, {@code 0} before {@link #enter()}.
     */
    int getTilesWidth()
    {
        return tilesWidth;
    }
}
