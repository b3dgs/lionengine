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
package com.b3dgs.lionengine.game.handler;

import com.b3dgs.lionengine.Localizable;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.FeaturableModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.layerable.Layerable;
import com.b3dgs.lionengine.game.feature.layerable.LayerableListener;
import com.b3dgs.lionengine.game.feature.layerable.LayerableModel;

/**
 * 
 */
/**
 * Object with features.
 */
class ObjectFeatures extends FeaturableModel implements Localizable, Layerable, LayerableListener
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
        return 0;
    }

    @Override
    public double getY()
    {
        return 0;
    }

    @Override
    public void prepare(Featurable owner, Services services)
    {
        // Mock
    }

    @Override
    public void checkListener(Object listener)
    {
        // Mock
    }

    @Override
    public <O extends Featurable> O getOwner()
    {
        return null;
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
    public void setLayer(Integer layer)
    {
        // Mock
    }

    @Override
    public Integer getLayer()
    {
        return null;
    }

    @Override
    public void notifyLayerChanged(Featurable featurable, Integer layerOld, Integer layerNew)
    {
        // Mock
    }
}