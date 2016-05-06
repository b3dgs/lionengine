/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.object.trait.mirrorable;

import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.game.object.trait.TraitModel;

/**
 * Default mirrorable implementation.
 */
public class MirrorableModel extends TraitModel implements Mirrorable
{
    /** Mirror state. */
    private Mirror mirror;
    /** Mirror next state flag. */
    private Mirror nextState;
    /** Mirror requested flag. */
    private boolean requested;

    /**
     * Create a mirrorable model.
     */
    public MirrorableModel()
    {
        super();
        mirror = Mirror.NONE;
        nextState = Mirror.NONE;
        requested = false;
    }

    /*
     * Mirrorable
     */

    @Override
    public void mirror(Mirror state)
    {
        requested = true;
        nextState = state;
    }

    @Override
    public void update(double extrp)
    {
        if (requested)
        {
            mirror = nextState;
            requested = false;
        }
    }

    @Override
    public Mirror getMirror()
    {
        return mirror;
    }
}
