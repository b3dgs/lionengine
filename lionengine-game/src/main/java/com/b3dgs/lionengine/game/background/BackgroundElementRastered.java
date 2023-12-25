/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.UtilFile;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.ImageBuffer;
import com.b3dgs.lionengine.graphic.Renderable;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.ImageHeader;
import com.b3dgs.lionengine.graphic.drawable.ImageInfo;
import com.b3dgs.lionengine.graphic.drawable.SpriteTiled;

/**
 * Specific background element, supporting raster effects.
 */
public class BackgroundElementRastered extends BackgroundElement implements Renderable
{
    /** Raster suffix. */
    private static final String SUFFIX = "_raster";

    /** Rasters list. */
    private final SpriteTiled rasters;
    /** Tiles. */
    private final int count;

    /**
     * Create a rastered background element.
     * 
     * @param mainX The main location x.
     * @param mainY The main location y.
     * @param media The image media.
     * @param palette The image palette.
     * @param raster The raster colors.
     */
    public BackgroundElementRastered(int mainX, int mainY, Media media, Media palette, Media raster)
    {
        super(mainX, mainY, null);

        final Media rasterTile = Medias.create(media.getParentPath(),
                                               UtilFile.removeExtension(media.getName())
                                                                      + SUFFIX
                                                                      + Constant.DOT
                                                                      + UtilFile.getExtension(media.getName()));
        if (!rasterTile.exists())
        {
            final ImageBuffer[] buffers = Graphics.getRasterBufferOffset(media, palette, raster, 1);
            Graphics.generateTileset(buffers, rasterTile);
        }

        final ImageHeader info = ImageInfo.get(media);
        rasters = Drawable.loadSpriteTiled(rasterTile, info.getWidth(), info.getHeight());
        rasters.load();
        rasters.prepare();
        count = rasters.getTilesHorizontal() * rasters.getTilesVertical();
    }

    /**
     * Set raster surface from its id.
     * 
     * @param id The raster id.
     */
    public void setRaster(int id)
    {
        rasters.setTile(UtilMath.clamp(id, 0, count - 1));
    }

    /**
     * Set the location.
     * 
     * @param x The horizontal location.
     * @param y The vertical location.
     */
    public void setLocation(double x, double y)
    {
        rasters.setLocation(x, y);
    }

    @Override
    public void render(Graphic g)
    {
        rasters.render(g);
    }
}
