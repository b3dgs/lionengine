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
package com.b3dgs.lionengine.game.feature.tile.map.extractable;

import com.b3dgs.lionengine.Listenable;
import com.b3dgs.lionengine.game.Tiled;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.RoutineUpdate;

/**
 * This interface represents the ability of resource extraction.
 */
@FeatureInterface
public interface Extractor extends RoutineUpdate, Listenable<ExtractorListener>
{
    /**
     * Set the extractor checker.
     * 
     * @param checker The extractor checker reference.
     */
    void setChecker(ExtractorChecker checker);

    /**
     * Set the resources location.
     * 
     * @param extractable The extractable resource.
     */
    void setResource(Extractable extractable);

    /**
     * Set the resources location in tile.
     * 
     * @param type The resource type to extract.
     * @param tiled The tiled resource.
     */
    void setResource(String type, Tiled tiled);

    /**
     * Set the resources location in tile.
     * 
     * @param type The resource type to extract.
     * @param tx The horizontal tile.
     * @param ty The vertical tile.
     * @param tw The width in tile.
     * @param th The height in tile.
     */
    void setResource(String type, int tx, int ty, int tw, int th);

    /**
     * Set the extraction unit per tick.
     * 
     * @param count The extraction count per tick.
     */
    void setExtractionSpeed(double count);

    /**
     * Set the drop off unit per tick.
     * 
     * @param count The drop off count per tick.
     */
    void setDropOffSpeed(double count);

    /**
     * Set the maximum extractible unit number.
     * 
     * @param capacity The extraction capacity.
     */
    void setCapacity(int capacity);

    /**
     * Start extraction.
     */
    void startExtraction();

    /**
     * Stop any action related to extraction.
     */
    void stopExtraction();

    /**
     * Get the extraction capacity in unit (the maximum number of unit extractible per extraction).
     * 
     * @return The extraction capacity.
     */
    int getExtractionCapacity();

    /**
     * Get the extraction unit per tick.
     * 
     * @return The extraction unit per tick.
     */
    double getExtractionSpeed();

    /**
     * Get the drop off unit per tick.
     * 
     * @return The drop off unit per tick.
     */
    double getDropOffSpeed();

    /**
     * Get the resource location in tile.
     * 
     * @return The resource location in tile.
     */
    Tiled getResourceLocation();

    /**
     * Get resource type.
     * 
     * @return The resource type.
     */
    String getResourceType();

    /**
     * Check if extractor is currently working on extraction.
     * 
     * @return <code>true</code> if extracting, <code>false</code> else.
     */
    boolean isExtracting();
}
