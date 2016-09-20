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
package com.b3dgs.lionengine.game.handler.it;

import java.io.IOException;

import org.junit.Assert;

import com.b3dgs.lionengine.core.Context;
import com.b3dgs.lionengine.core.Graphics;
import com.b3dgs.lionengine.core.InputDevicePointer;
import com.b3dgs.lionengine.game.handler.WorldGame;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Text;
import com.b3dgs.lionengine.graphic.TextStyle;
import com.b3dgs.lionengine.stream.FileReading;
import com.b3dgs.lionengine.stream.FileWriting;

/**
 * World implementation.
 */
class World extends WorldGame
{
    private final Text text = Graphics.createText(Text.SERIF, 12, TextStyle.NORMAL);
    private final boolean fail;
    private String str;

    /**
     * Create a new world.
     * 
     * @param context The context reference.
     * @param fail <code>true</code> to make fail on loading, <code>false</code> else.
     */
    public World(Context context, boolean fail)
    {
        super(context);

        this.fail = fail;
    }

    @Override
    public void render(Graphic g)
    {
        super.render(g);

        fill(g, ColorRgba.BLACK);
        text.draw(g, 10, 10, str);
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

        Assert.assertNotNull(getInputDevice(InputDevicePointer.class));
    }
}
