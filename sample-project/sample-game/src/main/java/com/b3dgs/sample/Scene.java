/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.sample;

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.core.sequence.Sequence;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.Text;
import com.b3dgs.lionengine.graphic.TextStyle;

/**
 * Game scene implementation.
 */
public class Scene extends Sequence
{
    /** Scene display. */
    public static final Resolution RESOLUTION = new Resolution(240, 320, 60);
    /** Text. */
    private final Text text = Graphics.createText(Text.SANS_SERIF, 20, TextStyle.NORMAL);

    /**
     * Create the scene.
     * 
     * @param context The context reference.
     */
    public Scene(Context context)
    {
        super(context, RESOLUTION);
    }

    @Override
    public void load()
    {
        text.setAlign(Align.CENTER);
        text.setColor(ColorRgba.WHITE);
        text.setLocation(getWidth() / 2, getHeight() / 2);
    }

    @Override
    public void update(double extrp)
    {
        text.setText("FPS = " + String.valueOf(getFps()));
    }

    @Override
    public void render(Graphic g)
    {
        g.clear(0, 0, getWidth(), getHeight());
        text.draw(g, getWidth() / 2, 20, Align.CENTER, "Hello World");
        text.render(g);
    }
}
