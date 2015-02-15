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
package com.b3dgs.lionengine.core;

import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.Filter;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.TextStyle;
import com.b3dgs.lionengine.Transparency;
import com.b3dgs.lionengine.stream.Stream;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Implementation provider for the {@link FactoryGraphic}.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class FactoryGraphicProvider
        implements FactoryGraphic
{
    /** Factory graphic implementation. */
    private static FactoryGraphic factoryGraphic;

    /**
     * Set the graphic factory used.
     * 
     * @param factoryGraphic The graphic factory used.
     */
    public static void setFactoryGraphic(FactoryGraphic factoryGraphic)
    {
        FactoryGraphicProvider.factoryGraphic = factoryGraphic;
    }

    /*
     * FactoryGraphic
     */

    @Override
    public Renderer createRenderer(Config config)
    {
        return factoryGraphic.createRenderer(config);
    }

    @Override
    public Screen createScreen(Renderer renderer)
    {
        return factoryGraphic.createScreen(renderer);
    }

    @Override
    public Graphic createGraphic()
    {
        return factoryGraphic.createGraphic();
    }

    @Override
    public Transform createTransform()
    {
        return factoryGraphic.createTransform();
    }

    @Override
    public Text createText(String fontName, int size, TextStyle style)
    {
        return factoryGraphic.createText(fontName, size, style);
    }

    @Override
    public ImageBuffer createImageBuffer(int width, int height, Transparency transparency)
    {
        return factoryGraphic.createImageBuffer(width, height, transparency);
    }

    @Override
    public ImageBuffer getImageBuffer(Media media, boolean alpha) throws LionEngineException
    {
        return factoryGraphic.getImageBuffer(media, alpha);
    }

    @Override
    public ImageBuffer getImageBuffer(ImageBuffer imageBuffer)
    {
        return factoryGraphic.getImageBuffer(imageBuffer);
    }

    @Override
    public ImageBuffer applyMask(ImageBuffer imageBuffer, ColorRgba maskColor)
    {
        return factoryGraphic.applyMask(imageBuffer, maskColor);
    }

    @Override
    public ImageBuffer[] splitImage(ImageBuffer image, int h, int v)
    {
        return factoryGraphic.splitImage(image, h, v);
    }

    @Override
    public ImageBuffer rotate(ImageBuffer image, int angle)
    {
        return factoryGraphic.rotate(image, angle);
    }

    @Override
    public ImageBuffer resize(ImageBuffer image, int width, int height)
    {
        return factoryGraphic.resize(image, width, height);
    }

    @Override
    public ImageBuffer flipHorizontal(ImageBuffer image)
    {
        return factoryGraphic.flipHorizontal(image);
    }

    @Override
    public ImageBuffer flipVertical(ImageBuffer image)
    {
        return factoryGraphic.flipVertical(image);
    }

    @Override
    public ImageBuffer applyFilter(ImageBuffer image, Filter filter) throws LionEngineException
    {
        return factoryGraphic.applyFilter(image, filter);
    }

    @Override
    public void saveImage(ImageBuffer image, Media media) throws LionEngineException
    {
        factoryGraphic.saveImage(image, media);
    }

    @Override
    public ImageBuffer getRasterBuffer(ImageBuffer image, int fr, int fg, int fb, int er, int eg, int eb, int refSize)
    {
        return factoryGraphic.getRasterBuffer(image, fr, fg, fb, er, eg, eb, refSize);
    }

    @Override
    public int[][] loadRaster(Media media) throws LionEngineException
    {
        final XmlNode raster = Stream.loadXml(media);
        final String[] colors =
        {
                "Red", "Green", "Blue"
        };
        final int[][] rasters = new int[colors.length][6];
        for (int c = 0; c < colors.length; c++)
        {
            final XmlNode color = raster.getChild(colors[c]);
            rasters[c][0] = Integer.decode(color.readString("start")).intValue();
            rasters[c][1] = Integer.decode(color.readString("step")).intValue();
            rasters[c][2] = color.readInteger("force");
            rasters[c][3] = color.readInteger("amplitude");
            rasters[c][4] = color.readInteger("offset");
            rasters[c][5] = color.readInteger("type");
        }
        return rasters;
    }
}
