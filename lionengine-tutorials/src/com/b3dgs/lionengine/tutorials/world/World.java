/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.tutorials.world;

import java.io.IOException;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.Text;
import com.b3dgs.lionengine.TextStyle;
import com.b3dgs.lionengine.core.Sequence;
import com.b3dgs.lionengine.core.UtilityImage;
import com.b3dgs.lionengine.file.FileReading;
import com.b3dgs.lionengine.file.FileWriting;
import com.b3dgs.lionengine.game.WorldGame;

@SuppressWarnings("all")
final class World
        extends WorldGame
{
    private final Text text;
    private String str;

    World(Sequence sequence)
    {
        super(sequence);
        text = UtilityImage.createText(Text.SERIF, 12, TextStyle.NORMAL);
    }

    @Override
    public void update(double extrp)
    {
    }

    @Override
    public void render(Graphic g)
    {
        text.draw(g, 0, 0, str);
    }

    @Override
    protected void saving(FileWriting file) throws IOException
    {
        file.writeString("world");
    }

    @Override
    protected void loading(FileReading file) throws IOException
    {
        str = file.readString();
    }
}
