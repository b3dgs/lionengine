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
package com.b3dgs.lionengine.example.game.action;

import com.b3dgs.lionengine.game.Action;
import com.b3dgs.lionengine.game.Services;
import com.b3dgs.lionengine.game.Setup;
import com.b3dgs.lionengine.game.feature.Factory;
import com.b3dgs.lionengine.game.feature.Handler;

/**
 * Buildings action implementation.
 */
class ActionBuildings extends ActionFeature
{
    /**
     * Create feature.
     * 
     * @param setup The setup reference.
     */
    public ActionBuildings(Setup setup)
    {
        super(setup);
    }

    @Override
    public Action create(Services services)
    {
        return () ->
        {
            final Factory factory = services.get(Factory.class);
            final Handler handler = services.get(Handler.class);

            final Button cancel = factory.create(Button.CANCEL);
            handler.add(cancel);

            final Button buildFarm = factory.create(Button.BUILD_FARM);
            cancel.getFeature(ButtonLink.class).addToDelete(buildFarm);
            handler.add(buildFarm);

            final Button buildBarracks = factory.create(Button.BUILD_BARRACKS);
            cancel.getFeature(ButtonLink.class).addToDelete(buildBarracks);
            handler.add(buildBarracks);

            getFeature(ButtonLink.class).terminate();
        };
    }
}
