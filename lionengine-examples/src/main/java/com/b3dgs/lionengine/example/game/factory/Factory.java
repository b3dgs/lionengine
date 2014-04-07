/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.example.game.factory;

import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.game.FactoryObjectGame;

/**
 * Factory implementation example.
 * This factory will allow to create instances of type.
 * Each type should have one configuration file, in XML.
 * According to {@link Type} and {@link Type#getPathName()}, the configuration files should be as follow:
 * <p>
 * In 'factory' folder:
 * <ul>
 * <li>fly_machine.xml</li>
 * <li>ground_truck.xml</li>
 * </ul>
 * </p>
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
final class Factory
        extends FactoryObjectGame<Type, Setup, TypeBase>
{
    /** Parameter. Can be replaced by another type if needed. */
    private final Object param;

    /**
     * Constructor.
     */
    Factory()
    {
        super(Type.class, "factory");
        param = new Object();
    }

    /*
     * FactoryObjectGame
     */

    @Override
    protected Setup createSetup(Type type, Media config)
    {
        return new Setup(config, type, param);
    }
}
