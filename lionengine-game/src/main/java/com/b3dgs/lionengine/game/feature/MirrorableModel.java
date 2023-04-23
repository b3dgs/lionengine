/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.b3dgs.lionengine.game.feature;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.ListenableModel;
import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.UpdatableVoid;

/**
 * Mirrorable model implementation.
 */
public class MirrorableModel extends FeatureModel implements Mirrorable, Recyclable
{
    /** Listeners. */
    private final ListenableModel<MirrorableListener> listenable = new ListenableModel<>();
    /** Update mirror. */
    private Updatable updater = UpdatableVoid.getInstance();
    /** Current mirror. */
    private Mirror mirror = Mirror.NONE;
    /** Next mirror to apply. */
    private Mirror nextState = mirror;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid argument.
     */
    public MirrorableModel(Services services, Setup setup)
    {
        super(services, setup);
    }

    /**
     * Apply next mirror.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateMirror(double extrp)
    {
        final Mirror old = mirror;
        mirror = nextState;

        if (mirror != old)
        {
            final int n = listenable.size();
            for (int i = 0; i < n; i++)
            {
                listenable.get(i).notifyMirrored(old, mirror);
            }
        }
        updater = UpdatableVoid.getInstance();
    }

    /*
     * Mirrorable
     */

    @Override
    public void checkListener(Object listener)
    {
        super.checkListener(listener);

        if (listener instanceof MirrorableListener)
        {
            addListener((MirrorableListener) listener);
        }
    }

    @Override
    public void addListener(MirrorableListener listener)
    {
        listenable.addListener(listener);
    }

    @Override
    public void removeListener(MirrorableListener listener)
    {
        listenable.removeListener(listener);
    }

    @Override
    public void mirror(Mirror state)
    {
        Check.notNull(state);

        nextState = state;
        updater = this::updateMirror;
    }

    @Override
    public void update(double extrp)
    {
        updater.update(extrp);
    }

    @Override
    public Mirror getMirror()
    {
        return mirror;
    }

    @Override
    public boolean is(Mirror mirror)
    {
        return this.mirror == mirror;
    }

    /*
     * Recyclable
     */

    @Override
    public void recycle()
    {
        mirror = Mirror.NONE;
        nextState = mirror;
        updater = UpdatableVoid.getInstance();
    }
}
