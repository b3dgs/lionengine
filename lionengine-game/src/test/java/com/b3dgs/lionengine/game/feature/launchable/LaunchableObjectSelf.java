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
package com.b3dgs.lionengine.game.feature.launchable;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import com.b3dgs.lionengine.game.feature.IdentifiableModel;

/**
 * Launchable object test self listener.
 */
class LaunchableObjectSelf extends LaunchableObject implements LauncherListener, LaunchableListener
{
    /** Fired flag. */
    final AtomicBoolean fired = new AtomicBoolean();
    /** Fired launchable flag. */
    final AtomicReference<Launchable> firedLaunchable = new AtomicReference<>();

    /**
     * Constructor.
     */
    public LaunchableObjectSelf()
    {
        super();
        addFeature(new IdentifiableModel());
    }

    @Override
    public void notifyFired()
    {
        fired.set(true);
    }

    @Override
    public void notifyFired(Launchable launchable)
    {
        firedLaunchable.set(launchable);
    }
}
