/*
 * Copyright (C) 2013 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.utility;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.DataBufferInt;
import java.awt.image.Kernel;
import java.awt.image.VolatileImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Filter;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.file.XmlNode;
import com.b3dgs.lionengine.file.XmlNodeNotFoundException;
import com.b3dgs.lionengine.file.XmlParser;

/**
 * Set of static functions related to image manipulation.
 */
public final class UtilityImage
{
    /** Null image message. */
    private static final String MESSAGE_NULL_IMAGE = "Image must not be null !";
    /** Graphics environment. */
    private static final GraphicsEnvironment ENV = GraphicsEnvironment.getLocalGraphicsEnvironment();
    /** Graphics device. */
    private static final GraphicsDevice DEV = UtilityImage.ENV.getDefaultScreenDevice();
    /** Graphics configuration. */
    private static final GraphicsConfiguration CONFIG = UtilityImage.DEV.getDefaultConfiguration();
    /** Bilinear filter. */
    private static final float[] BILINEAR_FILTER = new float[]
    {
            1 / 9f, 1 / 9f, 1 / 9f, 1 / 9f, 1 / 9f, 1 / 9f, 1 / 9f, 1 / 9f, 1 / 9f
    };

    /**
     * Create a compatible buffered image.
     * 
     * @param width The image width (must be positive).
     * @param height The image height (must be positive).
     * @param transparency The transparency value.
     * @return buffered The image reference.
     * @see Transparency
     */
    public static BufferedImage createBufferedImage(int width, int height, int transparency)
    {
        Check.argument(width > 0 && height > 0, "Image size must be strictly positive !");
        return UtilityImage.CONFIG.createCompatibleImage(width, height, transparency);
    }

    /**
     * Create a compatible volatile image.
     * 
     * @param width The image width.
     * @param height The image height.
     * @param transparency The transparency value.
     * @return The volatile image reference.
     * @see Transparency
     */
    public static VolatileImage createVolatileImage(int width, int height, int transparency)
    {
        Check.argument(width > 0 && height > 0, "Image size must be strictly positive !");
        final VolatileImage image = UtilityImage.CONFIG.createCompatibleVolatileImage(width, height, transparency);
        if (VolatileImage.IMAGE_INCOMPATIBLE == image.validate(UtilityImage.CONFIG))
        {
            return UtilityImage.CONFIG.createCompatibleVolatileImage(width, height, transparency);
        }
        return image;
    }

    /**
     * Get a buffered image from an image file.
     * 
     * @param media The image media path.
     * @param alpha <code>true</code> to enable alpha, <code>false</code> else.
     * @return The created buffered image from file.
     */
    public static BufferedImage getBufferedImage(Media media, boolean alpha)
    {
        try (InputStream inputStream = Media.getStream(media, "getBufferedImage", false);)
        {
            final BufferedImage buffer = ImageIO.read(inputStream);
            int transparency = buffer.getTransparency();
            if (alpha)
            {
                transparency = Transparency.TRANSLUCENT;
            }
            final BufferedImage image = UtilityImage.createBufferedImage(buffer.getWidth(), buffer.getHeight(),
                    transparency);
            final Graphics2D g = image.createGraphics();

            g.setComposite(AlphaComposite.Src);
            g.drawImage(buffer, 0, 0, null);
            g.dispose();

            return image;
        }
        catch (final IOException exception)
        {
            throw new LionEngineException(exception, media, "Error on reading image.");
        }
    }

    /**
     * Get a volatile image from an image file.
     * 
     * @param media The image media path.
     * @param transparency The transparency.
     * @return The created volatile image from file.
     * @see Transparency
     */
    public static VolatileImage getVolatileImage(Media media, int transparency)
    {
        final String filename = media.getPath();
        try
        {
            return UtilityImage.getVolatileImage(ImageIO.read(new File(filename)));
        }
        catch (final IOException exception)
        {
            throw new LionEngineException(exception, media, "Error on reading image.");
        }
    }

    /**
     * Get a volatile image from a buffered image.
     * 
     * @param image input buffered image.
     * @return converted buffered image to volatile image.
     */
    public static VolatileImage getVolatileImage(BufferedImage image)
    {
        Check.notNull(image, UtilityImage.MESSAGE_NULL_IMAGE);
        final VolatileImage volatileImage = UtilityImage.createVolatileImage(image.getWidth(), image.getHeight(),
                image.getTransparency());
        final Graphics2D g = volatileImage.createGraphics();

        g.setComposite(AlphaComposite.Src);
        g.drawImage(image, 0, 0, null);
        g.dispose();

        return volatileImage;
    }

    /**
     * Apply color mask to image.
     * 
     * @param image The image reference.
     * @param maskColor The color mask.
     * @return The masked buffered image.
     */
    public static BufferedImage applyMask(java.awt.Image image, Color maskColor)
    {
        Check.notNull(image, UtilityImage.MESSAGE_NULL_IMAGE);
        final BufferedImage alpha = UtilityImage.createBufferedImage(image.getWidth(null), image.getHeight(null),
                Transparency.BITMASK);
        final Graphics2D g = alpha.createGraphics();

        g.setComposite(AlphaComposite.Src);
        UtilityImage.optimizeGraphics(g);
        g.drawImage(image, 0, 0, null);
        g.dispose();

        final int height = alpha.getHeight();
        final int width = alpha.getWidth();

        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                final int col = alpha.getRGB(x, y);
                final int flag = 0x00ffffff;
                if (col == maskColor.getRGB())
                {
                    alpha.setRGB(x, y, col & flag);
                }
            }
        }

        return alpha;
    }

    /**
     * Split an image into a reference of sub image (data shared).
     * 
     * @param image The image to split.
     * @param row The number of horizontal divisions.
     * @param col The number of vertical divisions.
     * @return The splited references.
     */
    public static BufferedImage[] referenceSplit(BufferedImage image, int row, int col)
    {
        Check.notNull(image, UtilityImage.MESSAGE_NULL_IMAGE);
        final int total = row * col;
        final int width = image.getWidth() / row, height = image.getHeight() / col;
        final BufferedImage[] images = new BufferedImage[total];

        for (int y = 0; y < col; y++)
        {
            for (int x = 0; x < row; x++)
            {
                final int index = x + y * row;
                images[index] = image.getSubimage(x * width, y * height, width, height);
            }
        }

        return images;
    }

    /**
     * Split an image into an array of sub image (data not shared).
     * 
     * @param image The image to split.
     * @param row The number of horizontal divisions.
     * @param col The number of vertical divisions.
     * @return The splited images.
     */
    public static BufferedImage[] splitImage(BufferedImage image, int row, int col)
    {
        Check.notNull(image, UtilityImage.MESSAGE_NULL_IMAGE);
        final int total = row * col;
        final int width = image.getWidth() / row, height = image.getHeight() / col;
        final int transparency = image.getColorModel().getTransparency();
        final BufferedImage[] images = new BufferedImage[total];
        int frame = 0;

        for (int y = 0; y < col; y++)
        {
            for (int x = 0; x < row; x++)
            {
                images[frame] = UtilityImage.createBufferedImage(width, height, transparency);
                final Graphics2D g = images[frame].createGraphics();
                UtilityImage.optimizeGraphics(g);
                g.drawImage(image, 0, 0, width, height, x * width, y * height, (x + 1) * width, (y + 1) * height, null);
                g.dispose();
                frame++;
            }
        }

        return images;
    }

    /**
     * Rotate input buffered image.
     * 
     * @param image The input buffered image.
     * @param angle The angle to apply in degree (0-359)
     * @return The new buffered image with angle applied.
     */
    public static BufferedImage rotate(BufferedImage image, int angle)
    {
        Check.notNull(image, UtilityImage.MESSAGE_NULL_IMAGE);
        final int w = image.getWidth(), h = image.getHeight();
        final int transparency = image.getColorModel().getTransparency();
        final BufferedImage rotated = UtilityImage.createBufferedImage(w, h, transparency);
        final Graphics2D g = rotated.createGraphics();

        UtilityImage.optimizeGraphics(g);
        g.rotate(Math.toRadians(angle), w / 2.0, h / 2.0);
        g.drawImage(image, null, 0, 0);
        g.dispose();

        return rotated;
    }

    /**
     * Resize input buffered image.
     * 
     * @param image The input buffered image.
     * @param width The new width.
     * @param height The new height.
     * @return The new buffered image with new size.
     */
    public static BufferedImage resize(BufferedImage image, int width, int height)
    {
        Check.notNull(image, UtilityImage.MESSAGE_NULL_IMAGE);
        final int transparency = image.getColorModel().getTransparency();
        final BufferedImage resized = UtilityImage.createBufferedImage(width, height, transparency);
        final Graphics2D g = resized.createGraphics();

        UtilityImage.optimizeGraphics(g);
        g.drawImage(image, 0, 0, width, height, 0, 0, image.getWidth(), image.getHeight(), null);
        g.dispose();

        return resized;
    }

    /**
     * Apply an horizontal flip to the input image.
     * 
     * @param image The input buffered image.
     * @return The flipped buffered image as a new instance.
     */
    public static BufferedImage flipHorizontal(BufferedImage image)
    {
        Check.notNull(image, UtilityImage.MESSAGE_NULL_IMAGE);
        final int w = image.getWidth(), h = image.getHeight();
        final BufferedImage flipped = UtilityImage.createBufferedImage(w, h, image.getColorModel().getTransparency());
        final Graphics2D g = flipped.createGraphics();

        UtilityImage.optimizeGraphics(g);
        g.drawImage(image, 0, 0, w, h, w, 0, 0, h, null);
        g.dispose();

        return flipped;
    }

    /**
     * Apply a vertical flip to the input image.
     * 
     * @param image The input buffered image.
     * @return The flipped buffered image as a new instance.
     */
    public static BufferedImage flipVertical(BufferedImage image)
    {
        Check.notNull(image, UtilityImage.MESSAGE_NULL_IMAGE);
        final int w = image.getWidth(), h = image.getHeight();
        final BufferedImage flipped = UtilityImage.createBufferedImage(w, h, image.getColorModel().getTransparency());
        final Graphics2D g = flipped.createGraphics();

        UtilityImage.optimizeGraphics(g);
        g.drawImage(image, 0, 0, w, h, 0, h, w, 0, null);
        g.dispose();

        return flipped;
    }

    /**
     * Apply a filter to the input buffered image.
     * 
     * @param image The input image.
     * @param filter The filter to use.
     * @return The filtered image as a new instance.
     */
    public static BufferedImage applyFilter(BufferedImage image, Filter filter)
    {
        Check.notNull(image, UtilityImage.MESSAGE_NULL_IMAGE);
        Check.notNull(filter, "Filter must not be null !");
        final Kernel kernel;
        switch (filter)
        {
            case BILINEAR:
                kernel = new Kernel(3, 3, UtilityImage.BILINEAR_FILTER);
                break;
            default:
                throw new LionEngineException("Unsupported filter: " + filter.name());
        }
        final ConvolveOp op = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
        return op.filter(image, null);
    }

    /**
     * Save an image into a file.
     * 
     * @param image The image to save.
     * @param media The image output media path.
     */
    public static void saveImage(BufferedImage image, Media media)
    {
        final String imagefile = media.getPath();
        try
        {
            final File file = new File(imagefile);
            ImageIO.write(image, "png", file);
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
    public static BufferedImage getRasterBuffer(BufferedImage image, int fr, int fg, int fb, int er, int eg, int eb,
            int refSize)
    {
        Check.notNull(image, UtilityImage.MESSAGE_NULL_IMAGE);
        final boolean method = true;
        final BufferedImage raster = UtilityImage.createBufferedImage(image.getWidth(), image.getHeight(),
                image.getTransparency());

        final double sr = -((er - fr) / 0x010000) / (double) refSize;
        final double sg = -((eg - fg) / 0x000100) / (double) refSize;
        final double sb = -((eb - fb) / 0x000001) / (double) refSize;

        if (method)
        {
            for (int i = 0; i < raster.getWidth(); i++)
            {
                for (int j = 0; j < raster.getHeight(); j++)
                {
                    final int r = (int) (sr * (j % refSize)) * 0x010000;
                    final int g = (int) (sg * (j % refSize)) * 0x000100;
                    final int b = (int) (sb * (j % refSize)) * 0x000001;

                    raster.setRGB(i, j, UtilityImage.filterRGB(image.getRGB(i, j), fr + r, fg + g, fb + b));
                }
            }
        }
        else
        {
            final int[] org = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
            final int width = raster.getWidth();
            final int height = raster.getHeight();
            final int[] pixels = ((DataBufferInt) raster.getRaster().getDataBuffer()).getData();

            for (int j = 0; j < height; j++)
            {
                for (int i = 0; i < width; i++)
                {
                    pixels[j * width + i] = UtilityImage.filterRGB(org[j * width + i], fr, fg, fb);
                }
            }
        }

        return raster;
    }

    /**
     * Enable all graphics improvement. May decrease overall performances.
     * 
     * @param g The graphic context.
     */
    public static void optimizeGraphicsQuality(Graphics2D g)
    {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }

    /**
     * Disable all graphics improvement. May increase overall performances.
     * 
     * @param g The graphic context.
     */
    public static void optimizeGraphicsSpeed(Graphics2D g)
    {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
        g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);
        g.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DISABLE);
        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
    }

    /**
     * Optimize graphic to performance mode.
     * 
     * @param g The graphic context.
     */
    public static void optimizeGraphics(Graphics2D g)
    {
        UtilityImage.optimizeGraphicsSpeed(g);
    }

    /**
     * Private constructor.
     */
    private UtilityImage()
    {
        throw new RuntimeException();
    }
}
