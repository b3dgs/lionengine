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

import java.io.IOException;
import java.io.OutputStream;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.Filter;
import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Text;
import com.b3dgs.lionengine.TextStyle;
import com.b3dgs.lionengine.Transparency;
import com.b3dgs.lionengine.UtilityMath;
import com.b3dgs.lionengine.file.XmlNode;
import com.b3dgs.lionengine.file.XmlNodeNotFoundException;
import com.b3dgs.lionengine.file.XmlParser;

/**
 * Set of static functions related to image manipulation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class UtilityImage
{
    /** Null image message. */
    static final String ERROR_IMAGE_NULL = "Image must not be null !";
    /** Graphic factory. */
    static FactoryGraphic graphicFactory;

    /**
     * Crate a text.
     * 
     * @param fontName The font name.
     * @param size The font size (in pixel).
     * @param style The font style.
     * @return The created text.
     */
    public static Text createText(String fontName, int size, TextStyle style)
    {
        return UtilityImage.graphicFactory.createText(fontName, size, style);
    }

    /**
     * Create a graphic context.
     * 
     * @return The graphic context.
     */
    public static Graphic createGraphic()
    {
        return UtilityImage.graphicFactory.createGraphic();
    }

    /**
     * Create a compatible buffered image.
     * 
     * @param width The image width (must be positive).
     * @param height The image height (must be positive).
     * @param transparency The transparency value.
     * @return buffered The image reference.
     */
    public static ImageBuffer createImageBuffer(int width, int height, Transparency transparency)
    {
        Check.argument(width > 0 && height > 0, "Image size must be strictly positive !");

        return UtilityImage.graphicFactory.createImageBuffer(width, height, transparency);
    }

    /**
     * Get a buffered image from an image file.
     * 
     * @param media The image media path.
     * @param alpha <code>true</code> to enable alpha, <code>false</code> else.
     * @return The created buffered image from file.
     */
    public static ImageBuffer getImageBuffer(Media media, boolean alpha)
    {
        Check.notNull(media, UtilityImage.ERROR_IMAGE_NULL);

        try
        {
            return UtilityImage.graphicFactory.getImageBuffer(media, alpha);
        }
        catch (final IOException exception)
        {
            throw new LionEngineException(exception, media, "Error on reading image.");
        }
    }

    /**
     * Get a buffered image from a buffered image.
     * 
     * @param imageBuffer input buffered image.
     * @return copied buffered image.
     */
    public static ImageBuffer getImageBuffer(ImageBuffer imageBuffer)
    {
        Check.notNull(imageBuffer, UtilityImage.ERROR_IMAGE_NULL);

        return UtilityImage.graphicFactory.getImageBuffer(imageBuffer);
    }

    /**
     * Apply color mask to image.
     * 
     * @param imageBuffer The image buffer reference.
     * @param maskColor The color mask.
     * @return The masked buffered image.
     */
    public static ImageBuffer applyMask(ImageBuffer imageBuffer, ColorRgba maskColor)
    {
        Check.notNull(imageBuffer, UtilityImage.ERROR_IMAGE_NULL);

        return UtilityImage.graphicFactory.applyMask(imageBuffer, maskColor);
    }

    /**
     * Split an image into an array of sub image (data not shared).
     * 
     * @param image The image to split.
     * @param row The number of horizontal divisions.
     * @param col The number of vertical divisions.
     * @return The splited images.
     */
    public static ImageBuffer[] splitImage(ImageBuffer image, int row, int col)
    {
        Check.notNull(image, UtilityImage.ERROR_IMAGE_NULL);

        return UtilityImage.graphicFactory.splitImage(image, row, col);
    }

    /**
     * Rotate input buffered image.
     * 
     * @param image The input buffered image.
     * @param angle The angle to apply in degree (0-359)
     * @return The new buffered image with angle applied.
     */
    public static ImageBuffer rotate(ImageBuffer image, int angle)
    {
        Check.notNull(image, UtilityImage.ERROR_IMAGE_NULL);

        return UtilityImage.graphicFactory.rotate(image, angle);
    }

    /**
     * Resize input buffered image.
     * 
     * @param image The input buffered image.
     * @param width The new width.
     * @param height The new height.
     * @return The new buffered image with new size.
     */
    public static ImageBuffer resize(ImageBuffer image, int width, int height)
    {
        Check.notNull(image, UtilityImage.ERROR_IMAGE_NULL);

        return UtilityImage.graphicFactory.resize(image, width, height);
    }

    /**
     * Apply an horizontal flip to the input image.
     * 
     * @param image The input buffered image.
     * @return The flipped buffered image as a new instance.
     */
    public static ImageBuffer flipHorizontal(ImageBuffer image)
    {
        Check.notNull(image, UtilityImage.ERROR_IMAGE_NULL);

        return UtilityImage.graphicFactory.flipHorizontal(image);
    }

    /**
     * Apply a vertical flip to the input image.
     * 
     * @param image The input buffered image.
     * @return The flipped buffered image as a new instance.
     */
    public static ImageBuffer flipVertical(ImageBuffer image)
    {
        Check.notNull(image, UtilityImage.ERROR_IMAGE_NULL);

        return UtilityImage.graphicFactory.flipVertical(image);
    }

    /**
     * Apply a filter to the input buffered image.
     * 
     * @param image The input image.
     * @param filter The filter to use.
     * @return The filtered image as a new instance.
     */
    public static ImageBuffer applyFilter(ImageBuffer image, Filter filter)
    {
        Check.notNull(image, UtilityImage.ERROR_IMAGE_NULL);
        Check.notNull(filter, "Filter must not be null !");

        return UtilityImage.graphicFactory.applyFilter(image, filter);
    }

    /**
     * Save an image into a file.
     * 
     * @param image The image to save.
     * @param media The image output media path.
     */
    public static void saveImage(ImageBuffer image, Media media)
    {
        Check.notNull(image, UtilityImage.ERROR_IMAGE_NULL);
        Check.notNull(media, UtilityImage.ERROR_IMAGE_NULL);

        final String imagefile = media.getPath();
        try (OutputStream outputStream = media.getOutputStream();)
        {
            UtilityImage.graphicFactory.saveImage(image, outputStream);
        }
        catch (final IOException exception)
        {
            throw new LionEngineException(exception, "Unable to save image: ", imagefile);
        }
    }

    /**
     * Load a raster data from a file.
     * 
     * @param media The raster media path.
     * @return The raster data.
     */
    public static int[][] loadRaster(Media media)
    {
        final XmlParser xml = com.b3dgs.lionengine.file.File.createXmlParser();
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
                throw new LionEngineException(exception, "Error on loading raster data of ", media.getPath());
            }
        }
        return rasters;
    }

    /**
     * Apply a filter rgb.
     * 
     * @param rgb The original rgb.
     * @param fr The red filter.
     * @param fg The green filter.
     * @param fb The blue filter.
     * @return The filtered rgb.
     */
    public static int filterRGB(int rgb, int fr, int fg, int fb)
    {
        if (-16711423 == rgb || 0 == rgb || 16711935 == rgb)
        {
            return rgb;
        }

        final int a = rgb & 0xFF000000;
        int r = (rgb & 0xFF0000) + fr;
        int g = (rgb & 0x00FF00) + fg;
        int b = (rgb & 0x0000FF) + fb;

        if (r < 0x000000)
        {
            r = 0x000000;
        }
        if (r > 0xFF0000)
        {
            r = 0xFF0000;
        }
        if (g < 0x000000)
        {
            g = 0x000000;
        }
        if (g > 0x00FF00)
        {
            g = 0x00FF00;
        }
        if (b < 0x000000)
        {
            b = 0x000000;
        }
        if (b > 0x0000FF)
        {
            b = 0x0000FF;
        }

        return a | r | g | b;
    }

    /**
     * Get raster color.
     * 
     * @param i The color offset.
     * @param data The raster data.
     * @param max The max offset.
     * @return The rastered color.
     */
    public static int getRasterColor(int i, int[] data, int max)
    {
        if (0 == data[5])
        {
            return data[0] + data[1] * (int) (data[2] * UtilityMath.sin(i * (data[3] / (double) max) - data[4]));
        }
        return data[0] + data[1] * (int) (data[2] * UtilityMath.cos(i * (data[3] / (double) max) - data[4]));
    }

    /**
     * Get raster buffer from data.
     * 
     * @param image The buffer image.
     * @param fr The first red.
     * @param fg The first green.
     * @param fb The first blue.
     * @param er The end red.
     * @param eg The end green.
     * @param eb The end blue.
     * @param refSize The reference size.
     * @return The rastered image.
     */
    public static ImageBuffer getRasterBuffer(ImageBuffer image, int fr, int fg, int fb, int er, int eg, int eb,
            int refSize)
    {
        Check.notNull(image, UtilityImage.ERROR_IMAGE_NULL);

        return UtilityImage.graphicFactory.getRasterBuffer(image, fr, fg, fb, er, eg, eb, refSize);
    }

    /**
     * Set the graphic factory context.
     * 
     * @param graphicFactory The graphic factory context.
     */
    public static void setGraphicFactory(FactoryGraphic graphicFactory)
    {
        UtilityImage.graphicFactory = graphicFactory;
    }

    /**
     * Private constructor.
     */
    private UtilityImage()
    {
        throw new RuntimeException();
    }
}
