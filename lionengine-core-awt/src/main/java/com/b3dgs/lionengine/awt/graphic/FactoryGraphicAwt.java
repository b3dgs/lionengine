/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.awt.graphic;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.FactoryGraphic;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.ImageBuffer;
import com.b3dgs.lionengine.graphic.Screen;
import com.b3dgs.lionengine.graphic.Text;
import com.b3dgs.lionengine.graphic.TextStyle;
import com.b3dgs.lionengine.graphic.Transform;

/**
 * Graphic factory implementation.
 */
// CHECKSTYLE IGNORE LINE: ClassDataAbstractionCoupling
public final class FactoryGraphicAwt implements FactoryGraphic
{
    /** Reading image message. */
    static final String ERROR_IMAGE_READING = "Error on reading image !";
    /** Save image message. */
    static final String ERROR_IMAGE_SAVE = "Unable to save image: ";

    /**
     * Constructor.
     */
    public FactoryGraphicAwt()
    {
        super();
    }

    /*
     * FactoryGraphic
     */

    @Override
    public Screen createScreen(Config config)
    {
        Check.notNull(config);

        final Screen screen;
        if (config.isWindowed())
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
    public ImageBuffer createImageBuffer(int width, int height)
    {
        final BufferedImage image = ToolsAwt.createImage(width, height, java.awt.Transparency.OPAQUE);
        final ImageBuffer buffer = new ImageBufferAwt(image);

        final Graphic g = buffer.createGraphic();
        g.setColor(ColorRgba.BLACK);
        g.drawRect(0, 0, width, height, true);
        g.dispose();

        return buffer;
    }

    @Override
    public ImageBuffer createImageBuffer(int width, int height, ColorRgba transparency)
    {
        Check.notNull(transparency);

        final BufferedImage image = ToolsAwt.createImage(width, height, java.awt.Transparency.BITMASK);
        final ImageBuffer buffer = new ImageBufferAwt(image);

        final Graphic g = buffer.createGraphic();
        g.setColor(transparency);
        g.drawRect(0, 0, width, height, true);
        g.dispose();

        return buffer;
    }

    @Override
    public ImageBuffer getImageBuffer(Media media)
    {
        Check.notNull(media);

        try (InputStream input = media.getInputStream())
        {
            return new ImageBufferAwt(ToolsAwt.getImage(input));
        }
        catch (final LionEngineException | IOException exception)
        {
            throw new LionEngineException(exception, media, ERROR_IMAGE_READING);
        }
    }

    @Override
    public ImageBuffer getImageBuffer(ImageBuffer image)
    {
        Check.notNull(image);

        final BufferedImage surface = image.getSurface();
        return new ImageBufferAwt(ToolsAwt.copyImage(surface));
    }

    @Override
    public ImageBuffer applyMask(ImageBuffer image, ColorRgba maskColor)
    {
        Check.notNull(image);
        Check.notNull(maskColor);

        final BufferedImage surface = image.getSurface();
        return new ImageBufferAwt(ToolsAwt.applyMask(surface, maskColor.getRgba()));
    }

    @Override
    public ImageBuffer[] splitImage(ImageBuffer image, int h, int v)
    {
        Check.notNull(image);

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
        try (OutputStream output = media.getOutputStream())
        {
            ToolsAwt.saveImage(surface, output);
        }
        catch (final IOException exception)
        {
            throw new LionEngineException(exception, media, ERROR_IMAGE_SAVE);
        }
    }

    @Override
    public ImageBuffer getRasterBuffer(ImageBuffer image, double fr, double fg, double fb)
    {
        Check.notNull(image);

        final BufferedImage surface = image.getSurface();
        return new ImageBufferAwt(ToolsAwt.getRasterBuffer(surface, fr, fg, fb));
    }
}
