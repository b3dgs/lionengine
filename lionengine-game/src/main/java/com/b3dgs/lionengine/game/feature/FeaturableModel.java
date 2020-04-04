/*
 * Copyright (C) 2013-2020 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.game.Feature;

/**
 * Featurable model implementation.
 */
public class FeaturableModel extends FeaturableAbstract
{
    /** Services reference. */
    protected final Services services;
    /** Associated media (<code>null</code> if none). */
    private final Media media;

    /**
     * Create model. All features are loaded from setup.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     */
    public FeaturableModel(Services services, Setup setup)
    {
        super();

        Check.notNull(services);
        Check.notNull(setup);

        this.services = services;
        media = setup.getMedia();
        addFeature(new Recycler(services, setup));
    }

    /*
     * Featurable
     */

    @Override
    public void addFeature(Feature feature)
    {
        try
        {
            super.addFeature(feature);
        }
        catch (final LionEngineException exception)
        {
            throw new LionEngineException(exception, media, exception.getMessage());
        }
    }

    @Override
    public Media getMedia()
    {
        return media;
    }
}
