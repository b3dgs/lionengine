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

import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.game.FactoryGame;

@SuppressWarnings("all")
public class Factory
        extends FactoryGame<Type, Setup>
{
    private final Object param1;
    private final Object param2;

    public Factory(Object param1, Object param2)
    {
        super(Type.class);
        this.param1 = param1;
        this.param2 = param2;
    }

    public AbstractObject create(Type type)
    {
        switch (type)
        {
            case TYPE1:
                return new ObjectType1(getSetup(type));
            case TYPE2:
                return new ObjectType2(getSetup(type));
            default:
                throw new RuntimeException("Unknown type: " + type);
        }
    }

    @Override
    protected Setup createSetup(Type type)
    {
        return new Setup(Media.get(type.name() + ".xml"), param1, param2);
    }
}
