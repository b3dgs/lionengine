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
package com.b3dgs.lionengine.game.feature.tile.map.extractable;

import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.game.Feature;
import com.b3dgs.lionengine.game.Tiled;
import com.b3dgs.lionengine.game.feature.FeatureInterface;

/**
 * This interface represents the ability of resource extraction. It is also possible to specify work time.
 */
@FeatureInterface
public interface Extractor extends Feature, Updatable
{
    /**
     * Add an extractor listener.
     * 
     * @param listener The extractor listener to add.
     */
    void addListener(ExtractorListener listener);

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
     * Set the extraction unit per second.
     * 
     * @param speed The extraction unit per second.
     */
    void setExtractionPerSecond(double speed);

    /**
     * Set the drop off unit per second.
     * 
     * @param speed The drop off unit per second.
     */
    void setDropOffPerSecond(double speed);

    /**
     * Set the maximum extractible unit number.
     * 
     * @param capacity The extraction capacity.
     */
    void setCapacity(int capacity);

    /**
     * Set the resources location in tile.
     * 
     * @param type The resource type to extract.
     * @param tx The horizontal tile.
     * @param ty The vertical tile.
     * @param tw The width in tile.
     * @param th The height in tile.
     */
    void setResource(Enum<?> type, int tx, int ty, int tw, int th);

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
     * Get the extraction unit per second.
     * 
     * @return The extraction unit per second.
     */
    double getExtractionPerSecond();

    /**
     * Get the drop off unit per second.
     * 
     * @return The drop off unit per second.
     */
    double getDropOffPerSecond();

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
    Enum<?> getResourceType();

    /**
     * Check if extractor is currently working on extraction.
     * 
     * @return <code>true</code> if extracting, <code>false</code> else.
     */
    boolean isExtracting();
}
