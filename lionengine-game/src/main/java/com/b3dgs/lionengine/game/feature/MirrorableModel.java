/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import com.b3dgs.lionengine.XmlReader;

/**
 * Mirrorable model implementation.
 */
public class MirrorableModel extends FeatureModel implements Mirrorable, Recyclable
{
    /** Listeners. */
    private final ListenableModel<MirrorableListener> listenable = new ListenableModel<>();
    /** Update priority. */
    private final int priorityUpdate;

    /** Current mirror. */
    private Mirror mirror = Mirror.NONE;
    /** Old mirror. */
    private Mirror old = mirror;
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
        this(services, setup, XmlReader.EMPTY);
    }

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @param config The feature configuration node (must not be <code>null</code>).
     * @throws LionEngineException If invalid argument.
     */
    public MirrorableModel(Services services, Setup setup, XmlReader config)
    {
        super(services, setup);

        Check.notNull(config);

        priorityUpdate = config.getInteger(RoutineUpdate.MIRRORABLE, FeaturableConfig.ATT_PRIORITY_UPDATE);
    }

    @Override
    public void checkListener(Object listener)
    {
        super.checkListener(listener);

        if (listener instanceof final MirrorableListener l)
        {
            addListener(l);
        }
    }

    @Override
    public void addListener(MirrorableListener listener)
    {
        Check.notNull(listener);

        listenable.addListener(listener);
    }

    @Override
    public void removeListener(MirrorableListener listener)
    {
        Check.notNull(listener);

        listenable.removeListener(listener);
    }

    @Override
    public void mirror(Mirror state)
    {
        Check.notNull(state);

        nextState = state;
    }

    @Override
    public void updateBefore()
    {
        old = mirror;
    }

    @Override
    public void update(double extrp)
    {
        mirror = nextState;
    }

    @Override
    public void updateAfter()
    {
        if (mirror != old)
        {
            final int n = listenable.size();
            for (int i = 0; i < n; i++)
            {
                listenable.get(i).notifyMirrored(old, mirror);
            }
            old = mirror;
        }
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

    @Override
    public int getPriotityUpdate()
    {
        return priorityUpdate;
    }

    @Override
    public void recycle()
    {
        mirror = Mirror.NONE;
        old = mirror;
        nextState = mirror;
    }
}
