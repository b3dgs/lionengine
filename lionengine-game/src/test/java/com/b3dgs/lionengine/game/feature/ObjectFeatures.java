/*
 * Copyright (C) 2013-2018 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature;

import com.b3dgs.lionengine.Localizable;
import com.b3dgs.lionengine.game.FeatureProvider;

/**
 * Object with features.
 */
final class ObjectFeatures extends FeaturableModel implements Localizable, Layerable, LayerableListener
{
    /**
     * Create object.
     */
    public ObjectFeatures()
    {
        super();

        addFeature(new LayerableModel());
    }

    @Override
    public double getX()
    {
        return 0.0;
    }

    @Override
    public double getY()
    {
        return 0.0;
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
    public void addListener(LayerableListener listener)
    {
        // Mock
    }

    @Override
    public void setLayer(int layer)
    {
        // Mock
    }

    @Override
    public void setLayer(int layerRefresh, int layerDisplay)
    {
        // Mock
    }

    @Override
    public void setLayer(Integer layerRefresh, Integer layerDisplay)
    {
        // Mock
    }

    @Override
    public Integer getLayerRefresh()
    {
        return null;
    }

    @Override
    public Integer getLayerDisplay()
    {
        return null;
    }

    @Override
    public void notifyLayerChanged(FeatureProvider provider,
                                   Integer layerRefreshOld,
                                   Integer layerRefreshNew,
                                   Integer layerDisplayOld,
                                   Integer layerDisplayNew)
    {
        // Mock
    }
}
