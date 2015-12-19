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
package com.b3dgs.lionengine.core.awt;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.ImageBuffer;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Text;
import com.b3dgs.lionengine.TextStyle;
import com.b3dgs.lionengine.Transform;
import com.b3dgs.lionengine.Transparency;
import com.b3dgs.lionengine.UtilFile;
import com.b3dgs.lionengine.core.Config;
import com.b3dgs.lionengine.core.FactoryGraphic;
import com.b3dgs.lionengine.core.Screen;

/**
 * Graphic factory implementation.
 */
public final class FactoryGraphicAwt implements FactoryGraphic
{
    /** Reading image message. */
    private static final String ERROR_IMAGE_READING = "Error on reading image !";
    /** Save image message. */
    private static final String ERROR_IMAGE_SAVE = "Unable to save image: ";

    /**
     * Constructor.
     */
    public FactoryGraphicAwt()
    {
        // Nothing to do
    }

    /*
     * FactoryGraphic
     */

    @Override
    public Screen createScreen(Config config)
    {
        final Screen screen;
        if (config.getApplet(AppletAwt.class) != null)
        {
            screen = new ScreenAppletAwt(config);
        }
        else if (config.isWindowed())
        {
            screen = new ScreenWindowedAwt(config);
        }
        else
        {
            screen = new ScreenFullAwt(config);
        }
        return screen;
    }

    @Override
    public Graphic createGraphic()
    {
        return new GraphicAwt();
    }

    @Override
    public Transform createTransform()
    {
        return new TransformAwt();
    }

    @Override
    public Text createText(String fontName, int size, TextStyle style)
    {
        return new TextAwt(fontName, size, style);
    }

    @Override
    public ImageBuffer createImageBuffer(int width, int height, Transparency transparency)
    {
        return new ImageBufferAwt(ToolsAwt.createImage(width, height, ToolsAwt.getTransparency(transparency)));
    }

    @Override
    public ImageBuffer getImageBuffer(Media media)
    {
        Check.notNull(media);
        final InputStream input = media.getInputStream();
        try
        {
            return new ImageBufferAwt(ToolsAwt.getImage(input));
        }
        catch (final IOException exception)
        {
            throw new LionEngineException(exception, ERROR_IMAGE_READING);
        }
        finally
        {
            UtilFile.safeClose(input);
        }
    }

    @Override
    public ImageBuffer getImageBuffer(ImageBuffer image)
    {
        final BufferedImage surface = image.getSurface();
        return new ImageBufferAwt(ToolsAwt.copyImage(surface, ToolsAwt.getTransparency(image.getTransparency())));
    }

    @Override
    public ImageBuffer applyMask(ImageBuffer image, ColorRgba maskColor)
    {
        final BufferedImage surface = image.getSurface();
        return new ImageBufferAwt(ToolsAwt.applyMask(surface, maskColor.getRgba()));
    }

    @Override
    public ImageBuffer[] splitImage(ImageBuffer image, int h, int v)
    {
        final BufferedImage surface = image.getSurface();
        final BufferedImage[] images = ToolsAwt.splitImage(surface, h, v);
        final ImageBuffer[] imageBuffers = new ImageBuffer[h * v];
        for (int i = 0; i < imageBuffers.length; i++)
        {
            imageBuffers[i] = new ImageBufferAwt(images[i]);
        }
        return imageBuffers;
    }

    @Override
    public ImageBuffer rotate(ImageBuffer image, int angle)
    {
        final BufferedImage surface = image.getSurface();
        return new ImageBufferAwt(ToolsAwt.rotate(surface, angle));
    }

    @Override
    public ImageBuffer resize(ImageBuffer image, int width, int height)
    {
        final BufferedImage surface = image.getSurface();
        return new ImageBufferAwt(ToolsAwt.resize(surface, width, height));
    }

    @Override
    public ImageBuffer flipHorizontal(ImageBuffer image)
    {
        final BufferedImage surface = image.getSurface();
        return new ImageBufferAwt(ToolsAwt.flipHorizontal(surface));
    }

    @Override
    public ImageBuffer flipVertical(ImageBuffer image)
    {
        final BufferedImage surface = image.getSurface();
        return new ImageBufferAwt(ToolsAwt.flipVertical(surface));
    }

    @Override
    public void saveImage(ImageBuffer image, Media media)
    {
        Check.notNull(media);
        final BufferedImage surface = image.getSurface();
        final OutputStream output = media.getOutputStream();
        try
        {
            ToolsAwt.saveImage(surface, output);
        }
        catch (final IOException exception)
        {
            throw new LionEngineException(exception, ERROR_IMAGE_SAVE);
        }
        finally
        {
            UtilFile.safeClose(output);
        }
    }

    @Override
    public ImageBuffer getRasterBuffer(ImageBuffer image, int fr, int fg, int fb, int er, int eg, int eb, int ref)
    {
        final BufferedImage surface = image.getSurface();
        return new ImageBufferAwt(ToolsAwt.getRasterBuffer(surface, fr, fg, fb, er, eg, eb, ref));
    }
}
