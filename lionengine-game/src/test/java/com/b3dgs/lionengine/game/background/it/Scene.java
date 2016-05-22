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
package com.b3dgs.lionengine.game.background.it;

import com.b3dgs.lionengine.Timing;
import com.b3dgs.lionengine.core.Context;
import com.b3dgs.lionengine.core.Engine;
import com.b3dgs.lionengine.core.Resolution;
import com.b3dgs.lionengine.core.Sequence;
import com.b3dgs.lionengine.game.background.BackgroundGame;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.util.UtilMath;

/**
 * Game loop designed to handle our world.
 */
class Scene extends Sequence
{
    /** Native resolution. */
    static final Resolution NATIVE = new Resolution(320, 240, 60);

    /** Background. */
    private final BackgroundGame background;
    /** Foreground. */
    private final Foreground foreground;
    /** Camera y. */
    private double y;
    /** Count. */
    private final Timing timing = new Timing();

    /**
     * Constructor.
     * 
     * @param context The context reference.
     */
    public Scene(Context context)
    {
        super(context, NATIVE);
        background = new Swamp(getConfig().getSource(), 1.0, 1.0);
        foreground = new Foreground(getConfig().getSource());
    }

    /*
     * Sequence
     */

    @Override
    public void load()
    {
        y = 230;
        timing.start();
    }

    @Override
    public void update(double extrp)
    {
        y = UtilMath.wrapDouble(y + 10, 0.0, 360.0);
        final double dy = UtilMath.sin(y) * 100 + 100;
        background.update(extrp, 5.0, dy);
        foreground.update(extrp, 5.0, dy);
        if (timing.elapsed(1000L))
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
