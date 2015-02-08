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

import com.b3dgs.lionengine.UtilFile;
import com.b3dgs.lionengine.Version;
import com.b3dgs.lionengine.core.Core;
import com.b3dgs.lionengine.core.awt.Engine;
import com.b3dgs.lionengine.game.Services;
import com.b3dgs.lionengine.game.factory.Factory;

/**
 * Main class.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see com.b3dgs.lionengine.example.core.minimal
 */
public class AppGameFactory
{
    /**
     * Main.
     * 
     * @param args The arguments.
     */
    public static void main(String[] args)
    {
        Engine.start("Factory", Version.create(1, 0, 0), UtilFile.getPath("resources", "game", "factory"));

        final Factory factory = new Factory();

        final Object param = new Object();

        // Define the context and add the parameter as a service
        final Services context = new Services();
        context.add(param);
        factory.setServices(context);

        // Create types
        final BaseType flyMachine = factory.create(Core.MEDIA.create("FlyMachine.xml"));
        final BaseType groundTruck = factory.create(Core.MEDIA.create("GroundTruck.xml"));

        System.out.println(flyMachine);
        System.out.println(groundTruck);

        // Parameters are the same
        System.out.println(flyMachine.getParam());
        System.out.println(groundTruck.getParam());

        Engine.terminate();
    }
}
