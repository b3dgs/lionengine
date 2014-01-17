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
package com.b3dgs.lionengine.game.purview.model;

import com.b3dgs.lionengine.game.purview.Mirrorable;

/**
 * Default mirrorable implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class MirrorableModel
        implements Mirrorable
{
    /** Mirror state. */
    private boolean mirror;
    /** Mirror requested flag. */
    private boolean requested;
    /** Mirror next state flag. */
    private boolean nextState;
    /** Mirror cancel flags. */
    private boolean cancel;

    /**
     * Constructor.
     */
    public MirrorableModel()
    {
        mirror = false;
        requested = false;
        nextState = false;
        cancel = false;
    }

    /*
     * MirrorableModel
     */

    @Override
    public void mirror(boolean state)
    {
        requested = true;
        nextState = state;
    }

    @Override
    public void updateMirror()
    {
        if (requested)
        {
            if (!cancel)
            {
                mirror = nextState;
                requested = false;
            }
            else
            {
                cancel = false;
            }
        }
    }

    @Override
    public void setMirrorCancel(boolean state)
    {
        cancel = state;
    }

    @Override
    public boolean getMirrorCancel()
    {
        return cancel;
    }

    @Override
    public boolean getMirror()
    {
        return mirror;
    }
}
