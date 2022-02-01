/*
 * Copyright (C) 2013-2022 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.rasterable;

import java.util.Collection;
import java.util.Optional;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.game.Feature;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.graphic.Renderable;
import com.b3dgs.lionengine.graphic.drawable.SpriteAnimated;

/**
 * Represents a surface that can be rastered.
 */
@FeatureInterface
public interface Rasterable extends Feature, Updatable, Renderable
{
    /**
     * Transform frame.
     * 
     * @param transform The frame transformer.
     */
    void setAnimTransform(FrameTransform transform);

    /**
     * Set the internal animation offset for special animation cases (0 for none).
     * 
     * @param offset The offset value.
     */
    void setAnimOffset(int offset);

    /**
     * Set the internal animation offset for second special animation cases (0 for none).
     * 
     * @param offset The offset value.
     */
    void setAnimOffset2(int offset);

    /**
     * Set frame offsets (offsets on rendering).
     * 
     * @param offsetX The horizontal offset.
     * @param offsetY The vertical offset.
     */
    void setFrameOffsets(int offsetX, int offsetY);

    /**
     * Get raster index from location.
     * 
     * @param y The current y location.
     * @return The raster index based on vertical location.
     */
    int getRasterIndex(double y);

    /**
     * Get raster animation from raster index.
     * 
     * @param rasterIndex The raster index (positive).
     * @return The raster animated sprite.
     */
    SpriteAnimated getRasterAnim(int rasterIndex);

    /**
     * Get the anim offset.
     * 
     * @return The anim offset.
     */
    int getAnimOffset();

    /**
     * Get the anim offset 2.
     * 
     * @return The anim offset 2.
     */
    int getAnimOffset2();

    /**
     * Set the raster media.
     * 
     * @param save <code>true</code> to save generated (if) rasters, <code>false</code> else.
     * @param media The raster media (must not be <code>null</code>).
     * @param rasterHeight The raster height (must be strictly positive).
     * @throws LionEngineException If invalid argument.
     */
    void setRaster(boolean save, Media media, int rasterHeight);

    /**
     * Set the raster media.
     * 
     * @param save <code>true</code> to save generated (if) rasters, <code>false</code> else.
     * @param media The raster media (must not be <code>null</code>).
     * @param rasterHeight The raster height (must be strictly positive).
     * @param linesPerRaster The lines number per raster.
     * @param rasterLineOffset The raster line offset.
     * @throws LionEngineException If invalid argument.
     */
    void setRaster(boolean save, Media media, int rasterHeight, int linesPerRaster, int rasterLineOffset);

    /**
     * Set the raster media.
     * 
     * @param save <code>true</code> to save generated (if) rasters, <code>false</code> else.
     * @param media The raster media (must not be <code>null</code>).
     * @param rasterHeight The raster height (must be strictly positive).
     * @param ignored The ignored raster indexes.
     * @throws LionEngineException If invalid argument.
     */
    void setRaster(boolean save, Media media, int rasterHeight, Collection<Integer> ignored);

    /**
     * Get the raster media.
     * 
     * @return The raster media.
     */
    Optional<Media> getMedia();

    /**
     * Set the origin location type, related to surface area. The type will affect the defined location and the
     * rendering point.
     * 
     * @param origin The origin type
     */
    void setOrigin(Origin origin);

    /**
     * Set raster enabled flag.
     * 
     * @param enabled <code>true</code> if enabled, <code>false</code> else.
     */
    void setEnabled(boolean enabled);

    /**
     * Set visibility flag.
     * 
     * @param visible <code>true</code> for visible, <code>false</code> else.
     */
    void setVisibility(boolean visible);

    /**
     * Get visibility flag.
     * 
     * @return <code>true</code> if visible, <code>false</code> else.
     */
    boolean isVisible();
}
