/*
 * Copyright (C) 2013-2021 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.it.background;

import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.Engine;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.game.background.BackgroundAbstract;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.engine.Sequence;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionProvider;

/**
 * Game loop designed to handle our world.
 */
final class Scene extends Sequence
{
    /** Native resolution. */
    static final Resolution NATIVE = new Resolution(320, 240, 60);

    /** Background. */
    private final BackgroundAbstract background;
    /** Foreground. */
    private final Foreground foreground;
    /** Camera y. */
    private double y;
    /** Count. */
    private final Tick tick = new Tick();

    /**
     * Constructor.
     * 
     * @param context The context reference.
     */
    Scene(Context context)
    {
        super(context, NATIVE);

        foreground = new Foreground(NATIVE);
        background = new Swamp(new SourceResolutionProvider()
        {
            @Override
            public int getWidth()
            {
                return NATIVE.getWidth();
            }

            @Override
            public int getHeight()
            {
                return NATIVE.getHeight();
            }

            @Override
            public int getRate()
            {
                return NATIVE.getRate();
            }
        });
    }

    /*
     * Sequence
     */

    @Override
    public void load()
    {
        y = 230;
        tick.start();
    }

    @Override
    public void update(double extrp)
    {
        y = UtilMath.wrapDouble(y + 20, 0.0, 360.0);
        final double dy = UtilMath.sin(y) * 100 + 100;
        background.update(extrp, 10.0, dy);
        foreground.update(extrp, 10.0, dy);
        tick.update(extrp);
        if (tick.elapsed(20L))
        {
            end();
        }
    }

    @Override
    public void render(Graphic g)
    {
        background.render(g);
        foreground.render(g);
    }

    @Override
    public void onTerminated(boolean hasNextSequence)
    {
        Engine.terminate();
    }
}
