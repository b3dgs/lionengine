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
package com.b3dgs.lionengine.example.game.action;

import java.util.ArrayList;
import java.util.Collection;

import com.b3dgs.lionengine.game.handler.Handler;
import com.b3dgs.lionengine.game.handler.Service;
import com.b3dgs.lionengine.game.object.Factory;
import com.b3dgs.lionengine.game.object.SetupSurface;
import com.b3dgs.lionengine.game.object.feature.actionable.Actionable;

/**
 * Cancel action.
 */
class Cancel extends Button
{
    private final Collection<Button> toDelete = new ArrayList<>();

    @Service private Factory factory;
    @Service private Handler handler;

    /**
     * Create cancel action.
     * 
     * @param setup The setup reference.
     */
    public Cancel(SetupSurface setup)
    {
        super(setup);

        getFeature(Actionable.class).setAction(() ->
        {
            final Button buildings = factory.create(Button.BUILDINGS);
            handler.add(buildings);
            for (final Button current : toDelete)
            {
                current.destroy();
            }
            destroy();
        });
    }

    /**
     * Add an action to delete on click.
     * 
     * @param action The action to delete.
     */
    public void addToDelete(Button action)
    {
        toDelete.add(action);
    }
}
