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
package com.b3dgs.lionengine.game.feature.attackable;

import java.util.concurrent.atomic.AtomicBoolean;

import com.b3dgs.lionengine.game.FeaturableModel;
import com.b3dgs.lionengine.game.feature.Transformable;

/**
 * Attacker test.
 */
class ObjectAttackerSelf extends FeaturableModel implements AttackerChecker, AttackerListener
{
    /** Flag. */
    final AtomicBoolean flag = new AtomicBoolean();

    /**
     * Constructor.
     */
    public ObjectAttackerSelf()
    {
        super();
    }

    @Override
    public boolean canAttack()
    {
        return true;
    }

    @Override
    public void notifyReachingTarget(Transformable target)
    {
        flag.set(true);
    }

    @Override
    public void notifyAttackStarted(Transformable target)
    {
        // Mock
    }

    @Override
    public void notifyAttackEnded(int damages, Transformable target)
    {
        // Mock
    }

    @Override
    public void notifyAttackAnimEnded()
    {
        // Mock
    }

    @Override
    public void notifyPreparingAttack()
    {
        // Mock
    }
}
