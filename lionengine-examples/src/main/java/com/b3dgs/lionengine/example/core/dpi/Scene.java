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
package com.b3dgs.lionengine.example.core.dpi;

import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.awt.Keyboard;
import com.b3dgs.lionengine.awt.KeyboardAwt;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.Image;
import com.b3dgs.lionengine.graphic.engine.Sequence;

/**
 * This is where the game loop is running.
 */
public final class Scene extends Sequence
{
    private static final Resolution NATIVE = new Resolution(640, 360, 60);

    private final Tick tick = new Tick();
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
        getInputDevice(Keyboard.class).addActionPressed(KeyboardAwt.ESCAPE, this::end);
        tick.addAction(this::end, NATIVE.getRate());
        tick.start();
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
        tick.update(extrp);
    }

    @Override
    public void render(Graphic g)
    {
        image.render(g);
    }
}
