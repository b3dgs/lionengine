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

import java.util.ArrayList;
import java.util.Collection;

import com.b3dgs.lionengine.core.Core;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.game.object.Factory;
import com.b3dgs.lionengine.game.object.Handler;
import com.b3dgs.lionengine.game.object.ObjectGame;
import com.b3dgs.lionengine.game.object.Services;
import com.b3dgs.lionengine.game.object.SetupSurface;

/**
 * Cancel action.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
class Cancel
        extends Button
{
    /** Media reference. */
    public static final Media MEDIA = Core.MEDIA.create("action", "Cancel.xml");

    /** Factory reference. */
    private final Factory factory;
    /** Handler reference. */
    private final Handler handler;
    /** Action to delete. */
    private final Collection<ObjectGame> toDelete;

    /**
     * Create cancel action.
     * 
     * @param setup The setup reference.
     * @param services The services reference.
     */
    public Cancel(SetupSurface setup, Services services)
    {
        super(setup, services);
        toDelete = new ArrayList<>();
        factory = services.get(Factory.class);
        handler = services.get(Handler.class);
    }

    /**
     * Add an action to delete on click.
     * 
     * @param action The action to delete.
     */
    public void addToDelete(ObjectGame action)
    {
        toDelete.add(action);
    }

    /*
     * Action
     */

    @Override
    public void execute()
    {
        final ObjectGame buildings = factory.create(Buildings.MEDIA);
        handler.add(buildings);
        for (final ObjectGame current : toDelete)
        {
            current.destroy();
        }
        destroy();
    }
}
