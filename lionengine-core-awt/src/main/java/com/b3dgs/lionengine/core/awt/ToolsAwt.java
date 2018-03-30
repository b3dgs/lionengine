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
package com.b3dgs.lionengine.core.awt;

import java.awt.AWTException;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.geom.Rectangle;
import com.b3dgs.lionengine.graphic.ImageBuffer;
import com.b3dgs.lionengine.graphic.Transparency;
import com.b3dgs.lionengine.graphic.UtilColor;
import com.b3dgs.lionengine.util.UtilMath;

/**
 * Misc tools for AWT.
 * <p>
 * This class is Thread-Safe.
 * </p>
 */
public final class ToolsAwt
{
    /** Graphics environment. */
    private static final GraphicsEnvironment ENV = GraphicsEnvironment.getLocalGraphicsEnvironment();
    /** Graphics device. */
    private static final GraphicsDevice DEV = ENV.getDefaultScreenDevice();
    /** Graphics configuration. */
    private static final GraphicsConfiguration CONFIG = DEV.getDefaultConfiguration();

    /**
     * Get the image transparency equivalence.
     * 
     * @param transparency The transparency type (must not be <code>null</code>).
     * @return The transparency value.
     * @throws LionEngineException If invalid argument.
     */
    public static int getTransparency(Transparency transparency)
    {
        Check.notNull(transparency);

        final int value;
        if (Transparency.OPAQUE == transparency)
        {
            value = java.awt.Transparency.OPAQUE;
        }
        else if (Transparency.BITMASK == transparency)
        {
            value = java.awt.Transparency.BITMASK;
        }
        else if (Transparency.TRANSLUCENT == transparency)
        {
            value = java.awt.Transparency.TRANSLUCENT;
        }
        else
        {
            throw new LionEngineException(transparency);
        }
        return value;
    }

    /**
     * Create an image.
     * 
     * @param width The image width (must be strictly positive).
     * @param height The image height (must be strictly positive).
     * @param transparency The image transparency.
     * @return The image instance.
     * @throws LionEngineException If invalid parameters.
     */
    public static BufferedImage createImage(int width, int height, int transparency)
    {
        Check.superiorStrict(width, 0);
        Check.superiorStrict(height, 0);

        return CONFIG.createCompatibleImage(width, height, transparency);
    }

    /**
     * Get an image from an input stream.
     * 
     * @param input The image input stream.
     * @return The loaded image.
     * @throws IOException If error when reading image.
     */
    public static BufferedImage getImage(InputStream input) throws IOException
    {
        final BufferedImage buffer = ImageIO.read(input);
        if (buffer == null)
        {
            throw new IOException("Invalid image !");
        }
        return copyImage(buffer);
    }

    /**
     * Create an image.
     * 
     * @param image The image.
     * @return The image.
     */
    public static ImageBuffer getImageBuffer(BufferedImage image)
    {
        return new ImageBufferAwt(image);
    }

    /**
     * Save image to output stream.
     * 
     * @param image The image to save.
     * @param output The output stream.
     * @throws IOException If error when saving image.
     */
    public static void saveImage(BufferedImage image, OutputStream output) throws IOException
    {
        ImageIO.write(image, "png", output);
    }

    /**
     * Create an image.
     * 
     * @param image The image reference.
     * @return The image copy.
     */
    public static BufferedImage copyImage(BufferedImage image)
    {
        final BufferedImage copy = createImage(image.getWidth(),
                                               image.getHeight(),
                                               image.getColorModel().getTransparency());
        final Graphics2D g = copy.createGraphics();
        g.setComposite(AlphaComposite.Src);
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return copy;
    }

    /**
     * Get the image pixels data.
     * 
     * @param image The image reference.
     * @return The pixels array.
     */
    public static int[] getImageData(BufferedImage image)
    {
        return ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
    }

    /**
     * Apply a mask to an existing image.
     * 
     * @param image The existing image.
     * @param rgba The rgba color value.
     * @return The masked image.
     */
    public static BufferedImage applyMask(BufferedImage image, int rgba)
    {
        final BufferedImage mask = copyImage(image);
        final int height = mask.getHeight();
        final int width = mask.getWidth();

        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                final int col = mask.getRGB(x, y);
                final int flag = 0x00_FF_FF_FF;
                if (col == rgba)
                {
                    mask.setRGB(x, y, col & flag);
                }
            }
        }
        return mask;
    }

    /**
     * Rotate an image with an angle in degree.
     * 
     * @param image The input image.
     * @param angle The angle in degree to apply.
     * @return The rotated image.
     */
    public static BufferedImage rotate(BufferedImage image, int angle)
    {
        final int width = image.getWidth();
        final int height = image.getHeight();
        final int transparency = image.getColorModel().getTransparency();

        final Rectangle rectangle = new Rectangle(0, 0, width, height);
        rectangle.rotate(angle);

        final BufferedImage rotated = createImage(rectangle.getWidth(), rectangle.getHeight(), transparency);
        final Graphics2D g = rotated.createGraphics();

        optimizeGraphicsSpeed(g);
        g.rotate(Math.toRadians(angle), rectangle.getWidth() / 2.0, rectangle.getHeight() / 2.0);

        final double ox = rectangle.getWidth() - (double) width;
        final double oy = rectangle.getHeight() - (double) height;
        final double cos = UtilMath.cos(angle);
        final double sin = UtilMath.sin(angle);
        final double angleOffsetX = sin * ox + cos * oy;
        final double angleOffsetY = -sin * oy + cos * ox;

        g.drawImage(image, null, (int) ((ox - angleOffsetX) / 2.0), (int) ((oy - angleOffsetY) / 2.0));
        g.dispose();

        return rotated;
    }

    /**
     * Resize input image buffer.
     * 
     * @param image The input image buffer.
     * @param width The new width.
     * @param height The new height.
     * @return The new image buffer with new size.
     */
    public static BufferedImage resize(BufferedImage image, int width, int height)
    {
        final int transparency = image.getColorModel().getTransparency();
        final BufferedImage resized = createImage(width, height, transparency);
        final Graphics2D g = resized.createGraphics();

        optimizeGraphicsSpeed(g);
        g.drawImage(image, 0, 0, width, height, 0, 0, image.getWidth(), image.getHeight(), null);
        g.dispose();

        return resized;
    }

    /**
     * Apply an horizontal flip to the input image.
     * 
     * @param image The input image buffer.
     * @return The flipped image buffer as a new instance.
     */
    public static BufferedImage flipHorizontal(BufferedImage image)
    {
        final int width = image.getWidth();
        final int height = image.getHeight();
        final BufferedImage flipped = createImage(width, height, image.getColorModel().getTransparency());
        final Graphics2D g = flipped.createGraphics();

        optimizeGraphicsSpeed(g);
        g.drawImage(image, 0, 0, width, height, width, 0, 0, height, null);
        g.dispose();

        return flipped;
    }

    /**
     * Apply a vertical flip to the input image.
     * 
     * @param image The input image buffer.
     * @return The flipped image buffer as a new instance.
     */
    public static BufferedImage flipVertical(BufferedImage image)
    {
        final int width = image.getWidth();
        final int height = image.getHeight();
        final BufferedImage flipped = createImage(width, height, image.getColorModel().getTransparency());
        final Graphics2D g = flipped.createGraphics();

        optimizeGraphicsSpeed(g);
        g.drawImage(image, 0, 0, width, height, 0, height, width, 0, null);
        g.dispose();

        return flipped;
    }

    /**
     * Split an image into an array of sub image.
     * 
     * @param image The image to split.
     * @param h The number of horizontal divisions (strictly positive).
     * @param v The number of vertical divisions (strictly positive).
     * @return The splited images array (can not be empty).
     */
    public static BufferedImage[] splitImage(BufferedImage image, int h, int v)
    {
        final int width = image.getWidth() / h;
        final int height = image.getHeight() / v;
        final int transparency = image.getColorModel().getTransparency();
        final BufferedImage[] images = new BufferedImage[h * v];
        int frame = 0;

        for (int y = 0; y < v; y++)
        {
            for (int x = 0; x < h; x++)
            {
                images[frame] = createImage(width, height, transparency);
                final Graphics2D g = images[frame].createGraphics();
                optimizeGraphicsSpeed(g);
                g.drawImage(image, 0, 0, width, height, x * width, y * height, (x + 1) * width, (y + 1) * height, null);
                g.dispose();
                frame++;
            }
        }

        return images;
    }

    /**
     * Get raster buffer from data.
     * 
     * @param image The image buffer.
     * @param fr The first red.
     * @param fg The first green.
     * @param fb The first blue.
     * @param er The end red.
     * @param eg The end green.
     * @param eb The end blue.
     * @param size The reference size.
     * @return The rastered image.
     */
    public static BufferedImage getRasterBuffer(BufferedImage image,
                                                int fr,
                                                int fg,
                                                int fb,
                                                int er,
                                                int eg,
                                                int eb,
                                                int size)
    {
        final BufferedImage raster = createImage(image.getWidth(), image.getHeight(), image.getTransparency());

        final int divisorRed = 0x01_00_00;
        final int divisorGreen = 0x00_01_00;
        final int divisorBlue = 0x00_00_01;

        final double sr = -((er - fr) / (double) divisorRed) / size;
        final double sg = -((eg - fg) / (double) divisorGreen) / size;
        final double sb = -((eb - fb) / (double) divisorBlue) / size;

        for (int i = 0; i < raster.getWidth(); i++)
        {
            for (int j = 0; j < raster.getHeight(); j++)
            {
                final int r = (int) (sr * (j % size)) * divisorRed;
                final int g = (int) (sg * (j % size)) * divisorGreen;
                final int b = (int) (sb * (j % size)) * divisorBlue;

                raster.setRGB(i, j, UtilColor.filterRgb(image.getRGB(i, j), fr + r, fg + g, fb + b));
            }
        }

        return raster;
    }

    /**
     * Create a hidden cursor.
     * 
     * @return Hidden cursor, or default cursor if not able to create it.
     */
    public static Cursor createHiddenCursor()
    {
        final Toolkit toolkit = Toolkit.getDefaultToolkit();
        final Dimension dim = toolkit.getBestCursorSize(1, 1);
        final BufferedImage c = createImage(Math.max(1, dim.width),
                                            Math.max(1, dim.height),
                                            java.awt.Transparency.BITMASK);
        final BufferedImage buffer = applyMask(c, Color.BLACK.getRGB());
        return toolkit.createCustomCursor(buffer, new Point(0, 0), "hiddenCursor");
    }

    /**
     * Create the buffer strategy using default capabilities.
     * 
     * @param component The component reference.
     * @param conf The current configuration.
     */
    public static void createBufferStrategy(java.awt.Canvas component, GraphicsConfiguration conf)
    {
        try
        {
            component.createBufferStrategy(2, conf.getBufferCapabilities());
        }
        catch (final AWTException exception)
        {
            Verbose.exception(exception);
            component.createBufferStrategy(1);
        }
    }

    /**
     * Create the buffer strategy using default capabilities.
     * 
     * @param component The component reference.
     * @param conf The current configuration.
     */
    public static void createBufferStrategy(java.awt.Window component, GraphicsConfiguration conf)
    {
        try
        {
            component.createBufferStrategy(2, conf.getBufferCapabilities());
        }
        catch (final AWTException exception)
        {
            Verbose.exception(exception);
            component.createBufferStrategy(1);
        }
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
     * Private constructor.
     */
    private ToolsAwt()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
