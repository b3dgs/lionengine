/*
 * Copyright (C) 2013-2021 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.awt.graphic;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.UtilMath;
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
    public ImageBuffer getImageBufferDraw(ImageBuffer image)
    {
        Check.notNull(image);

        final BufferedImage surface = image.getSurface();
        return new ImageBufferAwt(ToolsAwt.copyImageDraw(surface));
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
    public void generateTileset(ImageBuffer[] images, Media media)
    {
        Check.notNull(images);
        Check.notNull(media);

        if (images.length == 0)
        {
            throw new LionEngineException("No images found !");
        }

        final int width = images[0].getWidth();
        final int height = images[0].getHeight();
        final Transparency transparency = images[0].getTransparency();

        final int multDistance = (int) Math.ceil(width * images.length / (double) height) / 4;
        final int[] mult = UtilMath.getClosestSquareMult(images.length, multDistance);

        final ImageBuffer tile = new ImageBufferAwt(ToolsAwt.createImage(width * mult[1],
                                                                         height * mult[0],
                                                                         ToolsAwt.getTransparency(transparency)));
        int x = 0;
        int y = 0;
        int line = 0;

        final Graphic g = tile.createGraphic();
        for (final ImageBuffer b : images)
        {
            g.drawImage(b, x, y);

            x += b.getWidth();
            line++;
            if (line == mult[1])
            {
                x = 0;
                y += b.getHeight();
                line = 0;
            }
        }
        g.dispose();

        saveImage(tile, media);
    }

    @Override
    public ImageBuffer[] getRasterBuffer(ImageBuffer image, ImageBuffer palette)
    {
        Check.notNull(image);
        Check.notNull(palette);

        final BufferedImage[] rasters = ToolsAwt.getRasterBuffer(image.getSurface(), palette.getSurface());
        final ImageBuffer[] buffers = new ImageBuffer[rasters.length];
        for (int i = 0; i < buffers.length; i++)
        {
            buffers[i] = new ImageBufferAwt(rasters[i]);
        }
        return buffers;
    }

    @Override
    public ImageBuffer[] getRasterBufferInside(ImageBuffer image, ImageBuffer palette, int th)
    {
        Check.notNull(image);
        Check.notNull(palette);

        final BufferedImage[] rasters = ToolsAwt.getRasterBufferInside(image.getSurface(), palette.getSurface(), th);
        final ImageBuffer[] buffers = new ImageBuffer[rasters.length];
        for (int i = 0; i < buffers.length; i++)
        {
            buffers[i] = new ImageBufferAwt(rasters[i]);
        }
        return buffers;
    }

    @Override
    public ImageBuffer[] getRasterBufferSmooth(ImageBuffer image, ImageBuffer palette, int tileHeight)
    {
        Check.notNull(image);
        Check.notNull(palette);

        final BufferedImage[] rasters = ToolsAwt.getRasterBufferSmooth(image.getSurface(),
                                                                       palette.getSurface(),
                                                                       tileHeight);
        final ImageBuffer[] buffers = new ImageBuffer[rasters.length];
        for (int i = 0; i < buffers.length; i++)
        {
            buffers[i] = new ImageBufferAwt(rasters[i]);
        }
        return buffers;
    }

    @Override
    public ImageBuffer[] getRasterBufferSmooth(ImageBuffer image, ImageBuffer palette, int fh, int fv)
    {
        Check.notNull(image);
        Check.notNull(palette);

        final BufferedImage[] rasters = ToolsAwt.getRasterBufferSmooth(image.getSurface(),
                                                                       palette.getSurface(),
                                                                       fh,
                                                                       fv);
        final ImageBuffer[] buffers = new ImageBuffer[rasters.length];
        for (int i = 0; i < buffers.length; i++)
        {
            buffers[i] = new ImageBufferAwt(rasters[i]);
        }
        return buffers;
    }

    @Override
    public ImageBuffer[] getRasterBufferOffset(Media image, Media palette, Media raster, int offsets)
    {
        final BufferedImage[] rasters = ToolsAwt.getRasterBufferOffset(getImageBuffer(image).getSurface(),
                                                                       getImageBuffer(palette).getSurface(),
                                                                       getImageBuffer(raster).getSurface(),
                                                                       offsets);
        final ImageBuffer[] buffers = new ImageBuffer[rasters.length];
        for (int i = 0; i < buffers.length; i++)
        {
            buffers[i] = new ImageBufferAwt(rasters[i]);
        }
        return buffers;
    }
}
