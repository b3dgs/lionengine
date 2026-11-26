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

import com.b3dgs.lionengine.graphics.Image;
import com.b3dgs.lionengine.graphics.ImageCache;
import com.b3dgs.lionengine.graphics.SheetGrid;
import com.b3dgs.lionengine.graphics.SheetGroup;
import com.b3dgs.lionengine.graphics.Sprite;
import com.b3dgs.lionengine.render.CommandBuffer;
import com.b3dgs.lionengine.resources.ResourceKey;
import com.b3dgs.lionengine.runtime.Scene;

/**
 * The scene of {@link SpriteExample}: a hero walking to the right and turning, drawn above a sign.
 */
final class HeroScene implements Scene
{
    /** Id of the hero sheet. */
    static final ResourceKey HERO_SHEET = new ResourceKey("sheets/hero");
    /** Frame of the hero: the second frame of the second row. */
    static final int HERO_FRAME = 5;
    /** Speed of the hero in logical-frame pixels per second. */
    static final double SPEED = 30.0;
    /** Turn of the hero in degrees per second, counter-clockwise on screen. */
    static final double TURN = 90.0;
    /** Vertical position of the hero in logical-frame pixels. */
    static final double HERO_Y = 60.0;
    /** Scale of the hero: twice its size. */
    static final double HERO_SCALE = 2.0;
    /** Layer of the hero, above the sign. */
    static final int HERO_LAYER = 1;
    /** Position of the top-left corner of the sign, in logical-frame pixels. */
    static final double SIGN_POSITION = 4.0;

    /** Size of a hero frame in pixels. */
    private static final int HERO_CELL = 16;
    /** A full turn in degrees. */
    private static final double FULL_TURN = 360.0;

    /** The image cache of the game. */
    private final ImageCache cache;
    /** The command buffer of the frame. */
    private final CommandBuffer commands;
    /** The sheets of the scene, released on exit. */
    private SheetGroup sheets;
    /** The sign image, released on exit. */
    private Image signImage;
    /** The hero sprite. */
    private Sprite hero;
    /** The sign sprite. */
    private Sprite sign;
    /** Horizontal position of the hero at the last step, in pixels. */
    private double heroX;
    /** Horizontal position of the hero at the step before, in pixels. */
    private double previousX;
    /** Number of update steps. */
    private int steps;

    /**
     * Creates the scene.
     *
     * @param cache The image cache of the game.
     * @param commands The command buffer of the frame.
     */
    HeroScene(ImageCache cache, CommandBuffer commands)
    {
        this.cache = cache;
        this.commands = commands;
    }

    @Override
    public void enter()
    {
        sheets = new SheetGroup(cache);
        hero = new Sprite(sheets.add(HERO_SHEET, SpriteExample.HERO_IMAGE, new SheetGrid(HERO_CELL, HERO_CELL)));
        hero.setFrame(HERO_FRAME);
        hero.setScale(HERO_SCALE, HERO_SCALE);
        hero.setLayer(HERO_LAYER);

        signImage = cache.load(SpriteExample.SIGN_IMAGE);
        sign = new Sprite(signImage);
        sign.setPivot(0.0, 0.0);
        sign.setPosition(SIGN_POSITION, SIGN_POSITION);
    }

    @Override
    public void update(double stepSeconds)
    {
        previousX = heroX;
        heroX += SPEED * stepSeconds;
        // The engine never normalizes an angle implicitly (ADR-0016): the scene keeps it in [0, 360).
        hero.setRotation((hero.getRotation() + TURN * stepSeconds) % FULL_TURN);
        steps++;
    }

    @Override
    public void render(double alpha, double unscaledDeltaSeconds)
    {
        sign.render(commands);
        hero.render(commands, previousX + (heroX - previousX) * alpha, HERO_Y);
    }

    @Override
    public void exit()
    {
        sheets.release();
        signImage.release();
    }

    @Override
    public void dispose()
    {
        // The images were released on exit.
    }

    /**
     * Returns the number of update steps.
     *
     * @return The number of steps since the scene was created.
     */
    int getSteps()
    {
        return steps;
    }
}
