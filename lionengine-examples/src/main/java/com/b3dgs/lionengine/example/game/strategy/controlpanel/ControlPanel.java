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
package com.b3dgs.lionengine.example.game.strategy.controlpanel;

import com.b3dgs.lionengine.core.awt.Mouse;
import com.b3dgs.lionengine.example.game.strategy.ability.entity.Entity;
import com.b3dgs.lionengine.game.strategy.ControlPanelModel;

/**
 * Control panel implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
final class ControlPanel
        extends ControlPanelModel<Entity>
{
    /**
     * Constructor.
     */
    ControlPanel()
    {
        super();
        setClickSelection(Mouse.LEFT);
    }

    /*
     * ControlPanelModel
     */

    @Override
    protected void onStartOrder()
    {
        // Nothing to do
    }

    @Override
    protected void onTerminateOrder()
    {
        // Nothing to do
    }
}
