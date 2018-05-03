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
package com.b3dgs.lionengine.game.state;

import com.b3dgs.lionengine.io.InputDevicePointer;

/**
 * Input pointer mock.
 */
final class InputPointerMock implements InputDevicePointer
{
    @Override
    public void update(double extrp)
    {
        // Nothing to do
    }

    @Override
    public int getX()
    {
        return 0;
    }

    @Override
    public int getY()
    {
        return 0;
    }

    @Override
    public int getMoveX()
    {
        return 0;
    }

    @Override
    public int getMoveY()
    {
        return 0;
    }

    @Override
    public int getClick()
    {
        return 0;
    }

    @Override
    public boolean hasClicked(int click)
    {
        return false;
    }

    @Override
    public boolean hasClickedOnce(int click)
    {
        return false;
    }

    @Override
    public boolean hasMoved()
    {
        return false;
    }
}
