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
package com.b3dgs.lionengine.awt.swing;

import java.awt.Component;
import java.awt.Container;

import com.b3dgs.lionengine.LionEngineException;

/**
 * Set of functions around swing call, in order to create easily standard JObjects.
 */
public final class UtilSwing
{
    /**
     * Set the enabled state of a components set.
     * 
     * @param components The components.
     * @param enabled The enabled state.
     */
    public static void setEnabled(Component[] components, boolean enabled)
    {
        for (final Component component : components)
        {
            component.setEnabled(enabled);
            if (component instanceof Container)
            {
                final Container container = (Container) component;
                UtilSwing.setEnabled(container.getComponents(), enabled);
            }
        }
    }

    /**
     * Private constructor.
     */
    private UtilSwing()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
