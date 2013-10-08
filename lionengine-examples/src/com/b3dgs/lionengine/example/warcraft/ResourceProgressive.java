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
package com.b3dgs.lionengine.example.warcraft;

import com.b3dgs.lionengine.core.UtilityMath;
import com.b3dgs.lionengine.game.Alterable;

/**
 * Progressive resource implementation.
 */
public final class ResourceProgressive
        extends Alterable
{
    /** Current real value. */
    private double value;

    /**
     * Constructor.
     * 
     * @param amount Initial amount of resource.
     */
    public ResourceProgressive(int amount)
    {
        super(Integer.MAX_VALUE);
        set(amount);
        value = amount;
    }

    /**
     * Update progressive value effect.
     * 
     * @param extrp extrapolation reference.
     * @param speed progressive speed.
     */
    public void update(double extrp, double speed)
    {
        value = UtilityMath.curveValue(value, super.getCurrent(), speed / extrp);
        if (value >= super.getCurrent() - 0.1 && value <= super.getCurrent() + 0.1)
        {
            value = super.getCurrent();
        }
    }

    /**
     * Get current progressive value.
     * 
     * @return The progressive value.
     */
    @Override
    public int getCurrent()
    {
        return (int) value;
    }
}
