/*
 * Copyright (C) 2013-2019 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.background;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.UtilFile;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.graphic.ImageBuffer;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.ImageInfo;
import com.b3dgs.lionengine.graphic.drawable.Sprite;
import com.b3dgs.lionengine.graphic.raster.RasterImage;

/**
 * Specific background element, supporting raster effects.
 */
public class BackgroundElementRastered extends BackgroundElement
{
    /** Rasters list. */
    private final List<Sprite> rasters;

    /**
     * Create a rastered background element.
     * 
     * @param mainX The main location x.
     * @param mainY The main location y.
     * @param media The image media.
     * @param rasterFile The raster media.
     * @param rastersNumber The number of rasters.
     */
    public BackgroundElementRastered(int mainX, int mainY, Media media, Media rasterFile, int rastersNumber)
    {
        super(mainX, mainY, null);

        final RasterImage raster = new RasterImage(media, rasterFile, rastersNumber, false);
        raster.loadRasters(ImageInfo.get(media).getHeight(), false, UtilFile.removeExtension(media.getName()));

        final Collection<ImageBuffer> surfaces = raster.getRasters();
        rasters = new ArrayList<>(surfaces.size());
        for (final ImageBuffer surface : surfaces)
        {
            rasters.add(Drawable.loadSprite(surface));
        }
    }

    /**
     * Get raster surface from its id.
     * 
     * @param id The raster id.
     * @return The raster surface.
     */
    public Sprite getRaster(int id)
    {
        return rasters.get(UtilMath.clamp(id, 0, rasters.size() - 1));
    }
}
