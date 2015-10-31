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
package com.b3dgs.lionengine.example.game.factory;

import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.core.Engine;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.core.Version;
import com.b3dgs.lionengine.core.awt.EngineAwt;
import com.b3dgs.lionengine.game.object.Factory;
import com.b3dgs.lionengine.game.object.Services;

/**
 * Main class.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see com.b3dgs.lionengine.example.core.minimal
 */
public class AppFactory
{
    /**
     * Main.
     * 
     * @param args The arguments.
     */
    public static void main(String[] args)
    {
        EngineAwt.start("Factory", Version.create(1, 0, 0), AppFactory.class);

        final Services services = new Services();
        final Factory factory = new Factory(services);
        final Object param = new Object();

        // Define the context and add the parameter as a service
        services.add(param);

        // Create types
        final BaseType flyMachine = factory.create(Medias.create("FlyMachine.xml"));
        final BaseType groundTruck = factory.create(Medias.create("GroundTruck.xml"));

        Verbose.info(flyMachine.toString());
        Verbose.info(groundTruck.toString());

        // Parameters are the same
        Verbose.info(flyMachine.getParam().toString());
        Verbose.info(groundTruck.getParam().toString());

        Engine.terminate();
    }
}
