/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.trait.layerable;

import java.util.ArrayList;
import java.util.Collection;

import com.b3dgs.lionengine.game.object.ObjectGame;
import com.b3dgs.lionengine.game.object.Services;
import com.b3dgs.lionengine.game.trait.TraitModel;

/**
 * Layerable model implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class LayerableModel
        extends TraitModel
        implements Layerable
{
    /** Layers listener. */
    private final Collection<LayerableListener> listeners = new ArrayList<>();
    /** Layer value. */
    private Integer layer;

    /**
     * Create a layerable model.
     */
    public LayerableModel()
    {
        super();
        layer = Integer.valueOf(0);
    }

    /*
     * Layerable
     */

    @Override
    public void prepare(ObjectGame owner, Services services)
    {
        super.prepare(owner, services);

        addListener(services.get(LayerableListener.class));
    }

    @Override
    public void addListener(LayerableListener listener)
    {
        listeners.add(listener);
    }

    @Override
    public void setLayer(Integer layer)
    {
        for (final LayerableListener listener : listeners)
        {
            listener.notifyLayerChanged(getOwner(), this.layer, layer);
        }
        this.layer = layer;
    }

    @Override
    public Integer getLayer()
    {
        return layer;
    }
}
