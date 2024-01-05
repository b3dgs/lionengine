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
package com.b3dgs.lionengine.tutorials.platform.c;

import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.awt.Keyboard;
import com.b3dgs.lionengine.awt.KeyboardAwt;
import com.b3dgs.lionengine.game.feature.SequenceGame;
import com.b3dgs.lionengine.helper.MapTileHelper;

/**
 * Game loop designed to handle our world.
 */
public final class Scene extends SequenceGame<World>
{
    /** Native resolution. */
    public static final Resolution NATIVE = new Resolution(320, 240, 60);
    private static final Media LEVEL = Medias.create("map", "level.lvl");

    /**
     * Constructor.
     * 
     * @param context The context reference.
     */
    public Scene(Context context)
    {
        super(context, NATIVE, World::new);

        getInputDevice(Keyboard.class).addActionPressed(KeyboardAwt.ESCAPE, this::end);
    }

    @Override
    public void load()
    {
        if (!LEVEL.exists())
        {
            MapTileHelper.importAndSave(Medias.create("map", "level.png"), LEVEL);
        }
        world.loadFromFile(LEVEL);
    }
}
