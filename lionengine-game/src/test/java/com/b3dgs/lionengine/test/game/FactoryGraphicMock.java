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
package com.b3dgs.lionengine.test.game;

import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.Filter;
import com.b3dgs.lionengine.ImageInfo;
import com.b3dgs.lionengine.TextStyle;
import com.b3dgs.lionengine.Transparency;
import com.b3dgs.lionengine.core.FactoryGraphic;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.ImageBuffer;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.Renderer;
import com.b3dgs.lionengine.core.Screen;
import com.b3dgs.lionengine.core.Text;
import com.b3dgs.lionengine.core.Transform;

/**
 * Factory graphic mock
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class FactoryGraphicMock
        implements FactoryGraphic
{
    @Override
    public Renderer createRenderer(Config config)
    {
        return null;
    }

    @Override
    public Screen createScreen(Renderer renderer)
    {
        return null;
    }

    @Override
    public Text createText(String fontName, int size, TextStyle style)
    {
        return null;
    }

    @Override
    public Graphic createGraphic()
    {
        return new GraphicMock();
    }

    @Override
    public Transform createTransform()
    {
        return null;
    }

    @Override
    public ImageBuffer createImageBuffer(int width, int height, Transparency transparency)
    {
        return null;
    }

    @Override
    public ImageBuffer getImageBuffer(Media media, boolean alpha)
    {
        final ImageInfo info = ImageInfo.get(media);
        return new ImageBufferMock(info.getWidth(), info.getHeight(), Transparency.OPAQUE);
    }

    @Override
    public ImageBuffer getImageBuffer(ImageBuffer imageBuffer)
    {
        return null;
    }

    @Override
    public ImageBuffer applyMask(ImageBuffer imageBuffer, ColorRgba maskColor)
    {
        return null;
    }

    @Override
    public ImageBuffer[] splitImage(ImageBuffer image, int h, int v)
    {
        return null;
    }

    @Override
    public ImageBuffer rotate(ImageBuffer image, int angle)
    {
        return null;
    }

    @Override
    public ImageBuffer resize(ImageBuffer image, int width, int height)
    {
        return null;
    }

    @Override
    public ImageBuffer flipHorizontal(ImageBuffer image)
    {
        return null;
    }

    @Override
    public ImageBuffer flipVertical(ImageBuffer image)
    {
        return null;
    }

    @Override
    public ImageBuffer applyFilter(ImageBuffer image, Filter filter)
    {
        return null;
    }

    @Override
    public void saveImage(ImageBuffer image, Media media)
    {
        // Nothing to do
    }

    @Override
    public ImageBuffer getRasterBuffer(ImageBuffer image, int fr, int fg, int fb, int er, int eg, int eb, int refSize)
    {
        return null;
    }

    @Override
    public int[][] loadRaster(Media media)
    {
        return null;
    }
}
