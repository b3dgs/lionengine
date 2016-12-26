/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.FactoryGraphic;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.ImageBuffer;
import com.b3dgs.lionengine.graphic.Screen;
import com.b3dgs.lionengine.graphic.Text;
import com.b3dgs.lionengine.graphic.TextStyle;
import com.b3dgs.lionengine.graphic.Transform;
import com.b3dgs.lionengine.graphic.Transparency;

/**
 * Graphic factory implementation.
 */
public final class FactoryGraphicAndroid implements FactoryGraphic
{
    /**
     * Internal constructor.
     */
    public FactoryGraphicAndroid()
    {
        super();
    }

    /*
     * FactoryGraphic
     */

    @Override
    public Screen createScreen(Config config)
    {
        return new ScreenAndroid(config);
    }

    @Override
    public Graphic createGraphic()
    {
        return new GraphicAndroid();
    }

    @Override
    public Transform createTransform()
    {
        return new TransformAndroid();
    }

    @Override
    public Text createText(String fontName, int size, TextStyle style)
    {
        return new TextAndroid(fontName, size, style);
    }

    @Override
    public ImageBuffer createImageBuffer(int width, int height, Transparency transparency)
    {
        return UtilImage.createImage(width, height);
    }

    @Override
    public ImageBuffer getImageBuffer(Media media)
    {
        return UtilImage.getImage(media);
    }

    @Override
    public ImageBuffer getImageBuffer(ImageBuffer imageBuffer)
    {
        return UtilImage.getImage(imageBuffer);
    }

    @Override
    public ImageBuffer applyMask(ImageBuffer imageBuffer, ColorRgba maskColor)
    {
        return UtilImage.applyMask(imageBuffer, maskColor);
    }

    @Override
    public ImageBuffer[] splitImage(ImageBuffer imageBuffer, int h, int v)
    {
        return UtilImage.splitImage(imageBuffer, h, v);
    }

    @Override
    public ImageBuffer rotate(ImageBuffer imageBuffer, int angle)
    {
        return UtilImage.rotate(imageBuffer, angle);
    }

    @Override
    public ImageBuffer resize(ImageBuffer imageBuffer, int width, int height)
    {
        return UtilImage.resize(imageBuffer, width, height);
    }

    @Override
    public ImageBuffer flipHorizontal(ImageBuffer imageBuffer)
    {
        return UtilImage.flipHorizontal(imageBuffer);
    }

    @Override
    public ImageBuffer flipVertical(ImageBuffer imageBuffer)
    {
        return UtilImage.flipVertical(imageBuffer);
    }

    @Override
    public void saveImage(ImageBuffer imageBuffer, Media media)
    {
        UtilImage.saveImage(imageBuffer, media);
    }

    @Override
    public ImageBuffer getRasterBuffer(ImageBuffer imageBuffer, int fr, int fg, int fb, int er, int eg, int eb, int ref)
    {
        return UtilImage.getRasterBuffer(imageBuffer, fr, fg, fb, er, eg, eb, ref);
    }
}
