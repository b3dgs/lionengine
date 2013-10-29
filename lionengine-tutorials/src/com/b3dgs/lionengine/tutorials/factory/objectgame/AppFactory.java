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
package com.b3dgs.lionengine.tutorials.factory.objectgame;

import com.b3dgs.lionengine.Version;
import com.b3dgs.lionengine.core.Engine;
import com.b3dgs.lionengine.core.Media;

@SuppressWarnings("all")
public final class AppFactory
{
    public static void main(String[] args)
    {
        Engine.start("Factory", Version.create(1, 0, 0), Media.getPath("resources"));

        final Object param = new Object();
        final Factory factory = new Factory(param);
        factory.load();

        final AbstractObject base = factory.create(Type.OBJECT_TYPE1);
        final ObjectType1 objectType1 = factory.create(Type.OBJECT_TYPE1);
        final ObjectType2 objectType2 = factory.create(Type.OBJECT_TYPE2);

        System.out.println(base.getType());
        System.out.println(objectType1.getType());
        System.out.println(objectType2.getType());

        // Parameters are the same
        System.out.println(objectType1.getParam());
        System.out.println(objectType2.getParam());
    }
}
