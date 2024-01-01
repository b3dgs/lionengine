/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.it.feature;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.WorldGame;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.Text;
import com.b3dgs.lionengine.graphic.TextStyle;
import com.b3dgs.lionengine.graphic.engine.TimeControl;
import com.b3dgs.lionengine.graphic.engine.Zooming;
import com.b3dgs.lionengine.io.FileReading;
import com.b3dgs.lionengine.io.FileWriting;

/**
 * World implementation.
 */
final class World extends WorldGame
{
    private final Text text = Graphics.createText(Constant.FONT_SERIF, 12, TextStyle.NORMAL);
    private final boolean fail;
    private final AtomicInteger resized = new AtomicInteger();
    private final AtomicInteger timed = new AtomicInteger();
    private String str;

    /**
     * Create a new world.
     * 
     * @param services The services reference.
     * @param fail <code>true</code> to make fail on loading, <code>false</code> else.
     */
    World(Services services, boolean fail)
    {
        super(services);

        this.fail = fail;
    }

    @Override
    public void update(double extrp)
    {
        super.update(extrp);

        services.get(Zooming.class).setZoom(2.0);
        services.get(TimeControl.class).setTime(2.0);
    }

    @Override
    public void render(Graphic g)
    {
        super.render(g);

        fill(g, ColorRgba.BLACK);
        text.draw(g, 10, 10, str);

        assertEquals(640, resized.get());
        assertEquals(120, timed.get());
    }

    @Override
    protected void saving(FileWriting file) throws IOException
    {
        if (fail)
        {
            throw new IOException("Fail save");
        }

        file.writeString("world");
    }

    @Override
    protected void loading(FileReading file) throws IOException
    {
        if (fail)
        {
            throw new IOException("Fail load");
        }
        str = file.readString();
    }

    @Override
    protected void onResolutionChanged(int width, int height)
    {
        super.onResolutionChanged(width, height);

        resized.set(width);
    }

    @Override
    protected void onRateChanged(int rate)
    {
        super.onRateChanged(rate);

        timed.set(rate);
    }
}
