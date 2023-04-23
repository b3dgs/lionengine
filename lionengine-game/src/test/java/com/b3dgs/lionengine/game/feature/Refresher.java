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

import com.b3dgs.lionengine.game.FeatureProvider;

/**
 * Refreshable object mock.
 */
final class Refresher extends FeaturableModel implements Refreshable
{
    /** Refreshed flag. */
    private boolean refreshed;

    /**
     * Constructor.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     */
    public Refresher(Services services, Setup setup)
    {
        super(services, setup);

        addFeature(new IdentifiableModel());
    }

    /**
     * Check if has been refreshed.
     * 
     * @return <code>true</code> if refreshed, <code>false</code> else.
     */
    public boolean isRefreshed()
    {
        return refreshed;
    }

    @Override
    public void prepare(FeatureProvider provider)
    {
        // Mock
    }

    @Override
    public void checkListener(Object listener)
    {
        // Mock
    }

    @Override
    public void update(double extrp)
    {
        refreshed = true;
    }
}
