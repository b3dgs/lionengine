/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.example.game.action;

import com.b3dgs.lionengine.game.object.Factory;
import com.b3dgs.lionengine.game.object.Handler;
import com.b3dgs.lionengine.game.object.ObjectGame;
import com.b3dgs.lionengine.game.object.Services;
import com.b3dgs.lionengine.game.object.SetupSurface;

/**
 * Buildings action.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
class Buildings
        extends Button
{
    /** Factory reference. */
    private final Factory factory;
    /** Handler reference. */
    private final Handler handler;

    /**
     * Create buildings action.
     * 
     * @param setup The setup reference.
     * @param services The services reference.
     */
    public Buildings(SetupSurface setup, Services services)
    {
        super(setup, services);
        factory = services.get(Factory.class);
        handler = services.get(Handler.class);
    }

    @Override
    public void execute()
    {
        final ObjectGame buildFarm = factory.create(Button.BUILD_FARM);
        final ObjectGame buildBarracks = factory.create(Button.BUILD_BARRACKS);
        final Cancel cancel = factory.create(Button.CANCEL);

        cancel.addToDelete(buildFarm);
        cancel.addToDelete(buildBarracks);

        handler.add(buildFarm);
        handler.add(buildBarracks);
        handler.add(cancel);

        destroy();
    }
}
