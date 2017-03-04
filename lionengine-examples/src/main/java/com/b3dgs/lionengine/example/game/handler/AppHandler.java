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
package com.b3dgs.lionengine.example.game.handler;

import com.b3dgs.lionengine.Version;
import com.b3dgs.lionengine.core.Engine;
import com.b3dgs.lionengine.core.awt.EngineAwt;
import com.b3dgs.lionengine.game.Services;
import com.b3dgs.lionengine.game.feature.ComponentDisplayable;
import com.b3dgs.lionengine.game.feature.ComponentRefreshable;
import com.b3dgs.lionengine.game.feature.Handler;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Graphics;

/**
 * Main class.
 * 
 * @see com.b3dgs.lionengine.example.core.minimal
 */
public class AppHandler
{
    /**
     * Main.
     * 
     * @param args The arguments.
     */
    public static void main(String[] args)
    {
        EngineAwt.start(AppHandler.class.getSimpleName(), Version.create(1, 0, 0), AppHandler.class);

        final Services services = new Services();

        final Handler handler = new Handler(services);
        handler.addComponent(new ComponentRefreshable());
        handler.addComponent(new ComponentDisplayable());
        handler.add(new MyObject());
        handler.add(new MyObject());

        final Graphic g = Graphics.createGraphic();
        for (int i = 0; i < 2; i++)
        {
            handler.update(1.0);
            handler.render(g);
        }

        Engine.terminate();
    }
}
