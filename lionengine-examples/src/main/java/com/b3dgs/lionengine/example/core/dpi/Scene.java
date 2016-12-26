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
package com.b3dgs.lionengine.example.core.dpi;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.Timing;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.core.drawable.Drawable;
import com.b3dgs.lionengine.core.sequence.Sequence;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Image;
import com.b3dgs.lionengine.io.Keyboard;

/**
 * This is where the game loop is running.
 * 
 * @see com.b3dgs.lionengine.example.core.minimal
 */
class Scene extends Sequence
{
    /** Native resolution. */
    private static final Resolution NATIVE = new Resolution(640, 360, 60);

    /** Timing exit. */
    private final Timing timing = new Timing();
    /** Image reference. */
    private final Image image;

    /**
     * Constructor.
     * 
     * @param context The context reference.
     */
    public Scene(Context context)
    {
        super(context);

        Drawable.setDpi(NATIVE, getConfig());
        image = Drawable.loadImage(Medias.create("lionengine.png"));
        getInputDevice(Keyboard.class).addActionPressed(Keyboard.ESCAPE, () -> end());
        timing.start();
    }

    @Override
    public void load()
    {
        image.load();
        image.prepare();
        image.setLocation(0, 0);
    }

    @Override
    public void update(double extrp)
    {
        if (timing.elapsed(Constant.THOUSAND))
        {
            end();
        }
    }

    @Override
    public void render(Graphic g)
    {
        g.clear(0, 0, getWidth(), getHeight());
        image.render(g);
    }
}
