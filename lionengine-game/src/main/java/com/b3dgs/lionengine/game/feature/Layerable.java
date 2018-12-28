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

import com.b3dgs.lionengine.game.Feature;

/**
 * Represents something that can support layer, in order to define an order.
 */
@FeatureInterface
public interface Layerable extends Feature
{
    /**
     * Add a layer listener.
     * 
     * @param listener The layer listener reference.
     */
    void addListener(LayerableListener listener);

    /**
     * Set the layer value.
     * 
     * @param layer The layer refresh and display value.
     */
    void setLayer(int layer);

    /**
     * Set the layer value.
     * 
     * @param layerRefresh The layer refresh value.
     * @param layerDisplay The layer display value.
     */
    void setLayer(int layerRefresh, int layerDisplay);

    /**
     * Set the layer value.
     * 
     * @param layerRefresh The layer refresh value.
     * @param layerDisplay The layer display value.
     */
    void setLayer(Integer layerRefresh, Integer layerDisplay);

    /**
     * Get the current layer refresh value.
     * 
     * @return The current layer refresh value.
     */
    Integer getLayerRefresh();

    /**
     * Get the current layer display value.
     * 
     * @return The current layer display value.
     */
    Integer getLayerDisplay();
}
