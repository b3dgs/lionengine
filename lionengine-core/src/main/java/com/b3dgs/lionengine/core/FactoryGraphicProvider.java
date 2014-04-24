/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.TextStyle;
import com.b3dgs.lionengine.Transparency;
import com.b3dgs.lionengine.file.File;
import com.b3dgs.lionengine.file.XmlNode;
import com.b3dgs.lionengine.file.XmlNodeNotFoundException;
import com.b3dgs.lionengine.file.XmlParser;

/**
 * Implementation provider for the {@link FactoryGraphic}.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class FactoryGraphicProvider
        implements FactoryGraphic
{
    /** Load raster message. */
    private static final String ERROR_RASTER_LOAD = "Error on loading raster data of ";
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
        return FactoryGraphicProvider.factoryGraphic.createRenderer(config);
    }

    @Override
    public Screen createScreen(Renderer renderer, Config config)
    {
        return FactoryGraphicProvider.factoryGraphic.createScreen(renderer, config);
    }

    @Override
    public Text createText(String fontName, int size, TextStyle style)
    {
        return FactoryGraphicProvider.factoryGraphic.createText(fontName, size, style);
    }

    @Override
    public Graphic createGraphic()
    {
        return FactoryGraphicProvider.factoryGraphic.createGraphic();
    }

    @Override
    public ImageBuffer createImageBuffer(int width, int height, Transparency transparency)
    {
        return FactoryGraphicProvider.factoryGraphic.createImageBuffer(width, height, transparency);
    }

    @Override
    public ImageBuffer getImageBuffer(Media media, boolean alpha)
    {
        return FactoryGraphicProvider.factoryGraphic.getImageBuffer(media, alpha);
    }

    @Override
    public ImageBuffer getImageBuffer(ImageBuffer imageBuffer)
    {
        return FactoryGraphicProvider.factoryGraphic.getImageBuffer(imageBuffer);
    }

    @Override
    public ImageBuffer applyMask(ImageBuffer imageBuffer, ColorRgba maskColor)
    {
        return FactoryGraphicProvider.factoryGraphic.applyMask(imageBuffer, maskColor);
    }

    @Override
    public ImageBuffer[] splitImage(ImageBuffer image, int h, int v)
    {
        return FactoryGraphicProvider.factoryGraphic.splitImage(image, h, v);
    }

    @Override
    public ImageBuffer rotate(ImageBuffer image, int angle)
    {
        return FactoryGraphicProvider.factoryGraphic.rotate(image, angle);
    }

    @Override
    public ImageBuffer resize(ImageBuffer image, int width, int height)
    {
        return FactoryGraphicProvider.factoryGraphic.resize(image, width, height);
    }

    @Override
    public ImageBuffer flipHorizontal(ImageBuffer image)
    {
        return FactoryGraphicProvider.factoryGraphic.flipHorizontal(image);
    }

    @Override
    public ImageBuffer flipVertical(ImageBuffer image)
    {
        return FactoryGraphicProvider.factoryGraphic.flipVertical(image);
    }

    @Override
    public ImageBuffer applyFilter(ImageBuffer image, Filter filter)
    {
        return FactoryGraphicProvider.factoryGraphic.applyFilter(image, filter);
    }

    @Override
    public void saveImage(ImageBuffer image, Media media)
    {
        FactoryGraphicProvider.factoryGraphic.saveImage(image, media);
    }

    @Override
    public ImageBuffer getRasterBuffer(ImageBuffer image, int fr, int fg, int fb, int er, int eg, int eb, int refSize)
    {
        return FactoryGraphicProvider.factoryGraphic.getRasterBuffer(image, fr, fg, fb, er, eg, eb, refSize);
    }

    @Override
    public int[][] loadRaster(Media media)
    {
        final XmlParser xml = File.createXmlParser();
        final XmlNode raster = xml.load(media);
        final String[] colors =
        {
                "Red", "Green", "Blue"
        };
        final int[][] rasters = new int[colors.length][6];
        for (int c = 0; c < colors.length; c++)
        {
            try
            {
                final XmlNode color = raster.getChild(colors[c]);
                rasters[c][0] = Integer.decode(color.readString("start")).intValue();
                rasters[c][1] = Integer.decode(color.readString("step")).intValue();
                rasters[c][2] = color.readInteger("force");
                rasters[c][3] = color.readInteger("amplitude");
                rasters[c][4] = color.readInteger("offset");
                rasters[c][5] = color.readInteger("type");
            }
            catch (final XmlNodeNotFoundException exception)
            {
                throw new LionEngineException(exception, FactoryGraphicProvider.ERROR_RASTER_LOAD, media.getPath());
            }
        }
        return rasters;
    }

    @Override
    public Transform createTransform()
    {
        return FactoryGraphicProvider.factoryGraphic.createTransform();
    }
}
