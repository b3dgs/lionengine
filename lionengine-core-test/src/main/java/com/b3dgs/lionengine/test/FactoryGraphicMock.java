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
package com.b3dgs.lionengine.test;

import java.io.IOException;

import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.Filter;
import com.b3dgs.lionengine.Hq2x;
import com.b3dgs.lionengine.Hq3x;
import com.b3dgs.lionengine.ImageInfo;
import com.b3dgs.lionengine.LionEngineException;
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
 * Factory graphic mock.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class FactoryGraphicMock implements FactoryGraphic
{
    /**
     * Create mock.
     */
    public FactoryGraphicMock()
    {
        // Nothing to do
    }

    /*
     * FactoryGraphic
     */

    @Override
    public Screen createScreen(Renderer renderer)
    {
        return new ScreenMock(renderer);
    }

    @Override
    public Text createText(String fontName, int size, TextStyle style)
    {
        return new TextMock(fontName, size, style);
    }

    @Override
    public Graphic createGraphic()
    {
        return new GraphicMock();
    }

    @Override
    public Transform createTransform()
    {
        return new TransformMock();
    }

    @Override
    public ImageBuffer createImageBuffer(int width, int height, Transparency transparency)
    {
        return new ImageBufferMock(width, height, transparency);
    }

    @Override
    public ImageBuffer getImageBuffer(Media media)
    {
        final ImageInfo info = ImageInfo.get(media);
        return new ImageBufferMock(info.getWidth(), info.getHeight(), Transparency.OPAQUE);
    }

    @Override
    public ImageBuffer getImageBuffer(ImageBuffer imageBuffer)
    {
        return new ImageBufferMock(imageBuffer.getWidth(), imageBuffer.getHeight(), imageBuffer.getTransparency());
    }

    @Override
    public ImageBuffer applyMask(ImageBuffer imageBuffer, ColorRgba maskColor)
    {
        return new ImageBufferMock(imageBuffer.getWidth(), imageBuffer.getHeight(), imageBuffer.getTransparency());
    }

    @Override
    public ImageBuffer[] splitImage(ImageBuffer image, int h, int v)
    {
        final ImageBuffer[] buffers = new ImageBuffer[h * v];
        for (int i = 0; i < buffers.length; i++)
        {
            buffers[i] = resize(image, image.getWidth() / h, image.getHeight() / h);
        }
        return buffers;
    }

    @Override
    public ImageBuffer rotate(ImageBuffer image, int angle)
    {
        return new ImageBufferMock(image.getWidth(), image.getHeight(), image.getTransparency());
    }

    @Override
    public ImageBuffer resize(ImageBuffer image, int width, int height)
    {
        return new ImageBufferMock(width, height, image.getTransparency());
    }

    @Override
    public ImageBuffer flipHorizontal(ImageBuffer image)
    {
        return new ImageBufferMock(image.getWidth(), image.getHeight(), image.getTransparency());
    }

    @Override
    public ImageBuffer flipVertical(ImageBuffer image)
    {
        return new ImageBufferMock(image.getWidth(), image.getHeight(), image.getTransparency());
    }

    @Override
    public ImageBuffer applyFilter(ImageBuffer image, Filter filter)
    {
        final int scale;
        switch (filter)
        {
            case NONE:
                return image;
            case BILINEAR:
                scale = 1;
                break;
            case HQ2X:
                scale = Hq2x.SCALE;
                break;
            case HQ3X:
                scale = Hq3x.SCALE;
                break;
            default:
                throw new RuntimeException();
        }
        return new ImageBufferMock(image.getWidth() * scale, image.getHeight() * scale, image.getTransparency());
    }

    @Override
    public void saveImage(ImageBuffer image, Media media)
    {
        try
        {
            if (!media.getFile().createNewFile())
            {
                throw new LionEngineException(media, "Unable to create file !");
            }
        }
        catch (final IOException exception)
        {
            throw new LionEngineException(exception);
        }
    }

    @Override
    public ImageBuffer getRasterBuffer(ImageBuffer image, int fr, int fg, int fb, int er, int eg, int eb, int refSize)
    {
        return new ImageBufferMock(image.getWidth(), image.getHeight(), image.getTransparency());
    }
}
