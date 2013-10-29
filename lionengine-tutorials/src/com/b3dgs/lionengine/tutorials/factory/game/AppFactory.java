/*
 * Copyright (C) 2013 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.tutorials.factory.game;

import com.b3dgs.lionengine.Version;
import com.b3dgs.lionengine.core.Engine;
import com.b3dgs.lionengine.core.Media;

@SuppressWarnings("all")
public final class AppFactory
{
    public static void main(String[] args)
    {
        Engine.start("Factory", Version.create(1, 0, 0), Media.getPath("resources", "factory"));

        final Object param1 = new Object();
        final Object param2 = new Object();

        final Factory factory = new Factory(param1, param2);
        factory.load();

        final AbstractObject object1 = factory.create(Type.TYPE1);
        final AbstractObject object2 = factory.create(Type.TYPE2);

        System.out.println(object1.getParam1() + " - " + object2.getParam1()); // same
        System.out.println(object1.getParam2() + " - " + object2.getParam2()); // same
        System.out.println(object1.getIndex() + " - " + object2.getIndex());
    }
}
