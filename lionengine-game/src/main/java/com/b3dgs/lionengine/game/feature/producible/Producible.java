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
package com.b3dgs.lionengine.game.feature.producible;

import java.util.List;

import com.b3dgs.lionengine.Listenable;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Shape;
import com.b3dgs.lionengine.game.Feature;
import com.b3dgs.lionengine.game.feature.FeatureInterface;

/**
 * Represents a producible object.
 */
@FeatureInterface
public interface Producible extends Feature, Shape, Listenable<ProducibleListener>
{
    /**
     * Get the listeners reference as read only.
     * 
     * @return The listeners reference.
     */
    List<ProducibleListener> getListeners();

    /**
     * Set the production location.
     * 
     * @param x The horizontal location.
     * @param y The vertical location.
     */
    void setLocation(double x, double y);

    /**
     * Get the media representing the producible.
     * 
     * @return The media representing the producible.
     */
    Media getMedia();

    /**
     * Get the production steps number.
     * 
     * @return The production steps number.
     */
    int getSteps();
}
