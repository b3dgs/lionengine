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

import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.identifiable.Identifiable;

/**
 * Button linking implementation.
 */
public class ButtonLink extends FeatureModel
{
    private final Collection<Button> toDelete = new ArrayList<>();

    /**
     * Add an action to delete on click.
     * 
     * @param action The action to delete.
     */
    public void addToDelete(Button action)
    {
        toDelete.add(action);
    }

    /**
     * Terminate button.
     */
    public void terminate()
    {
        for (final Button button : toDelete)
        {
            button.getFeature(Identifiable.class).destroy();
        }
        getFeature(Identifiable.class).destroy();
    }
}
