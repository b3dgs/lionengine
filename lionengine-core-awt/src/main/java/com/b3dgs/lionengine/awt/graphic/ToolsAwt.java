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

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.IndexColorModel;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;

import javax.imageio.ImageIO;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.geom.Rectangle;
import com.b3dgs.lionengine.graphic.ImageBuffer;
import com.b3dgs.lionengine.graphic.Transparency;

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
    /** Image loading strategy. */
    private static volatile ImageLoadStrategy imageLoadStragegy = ImageLoadStrategy.FAST_RENDERING;

    /**
     * Set image load strategy.
     * 
     * @param strategy The strategy used.
     */
    public static void setLoadStrategy(ImageLoadStrategy strategy)
    {
        imageLoadStragegy = strategy;
    }

    /**
     * Check if same display.
     * 
     * @param a The first display.
     * @param b The second display.
     * @return <code>true</code> if same, <code>false</code> else.
     */
    public static boolean sameDisplay(DisplayMode a, DisplayMode b)
    {
        final boolean multiDepth = b.getBitDepth() != DisplayMode.BIT_DEPTH_MULTI && a.equals(b);
        return multiDepth
               || b.getBitDepth() == DisplayMode.BIT_DEPTH_MULTI
                  && a.getWidth() == b.getWidth()
                  && a.getHeight() == b.getHeight();
    }

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
     * @see #setLoadStrategy(ImageLoadStrategy)
     */
    public static BufferedImage getImage(InputStream input) throws IOException
    {
        final BufferedImage buffer = ImageIO.read(input);
        if (buffer == null)
        {
            throw new IOException("Invalid image !");
        }
        final BufferedImage copy;
        switch (imageLoadStragegy)
        {
            case FAST_LOADING:
                copy = buffer;
                break;
            case FAST_RENDERING:
                copy = copyImageDraw(buffer);
                break;
            case LOW_MEMORY:
                copy = copyImage(buffer);
                break;
            default:
                throw new LionEngineException(imageLoadStragegy);
        }
        return copy;
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
    public static BufferedImage copyImageDraw(BufferedImage image)
    {
        final BufferedImage copy = createImage(image.getWidth(), image.getHeight(), java.awt.Transparency.TRANSLUCENT);
        final Graphics2D g = copy.createGraphics();
        g.setComposite(java.awt.AlphaComposite.Src);
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return copy;
    }

    /**
     * Create an image.
     * 
     * @param image The image reference.
     * @return The image copy.
     */
    public static BufferedImage copyImage(BufferedImage image)
    {
        final int size = Constant.UNSIGNED_BYTE;
        final byte[] reds = new byte[size];
        final byte[] greens = new byte[size];
        final byte[] blues = new byte[size];

        final Set<Integer> ok = new HashSet<>();
        int transparent = -1;
        int i = 0;
        final int w = image.getWidth();
        final int h = image.getHeight();
        for (int x = 0; x < w; x++)
        {
            for (int y = 0; y < h; y++)
            {
                final int value = image.getRGB(x, y);
                if (ok.add(Integer.valueOf(value)))
                {
                    if (transparent == -1 && value >> Constant.BYTE_4 == 0)
                    {
                        transparent = i;
                    }
                    reds[i] = (byte) (value >> Constant.BYTE_3 & 0xFF);
                    greens[i] = (byte) (value >> Constant.BYTE_2 & 0xFF);
                    blues[i] = (byte) (value >> Constant.BYTE_1 & 0xFF);

                    i++;
                    if (i > size - 1)
                    {
                        return copyImageDraw(image);
                    }
                }
            }
        }
        ok.clear();

        return toImageWithIndexColor(image, transparent, size, reds, greens, blues);
    }

    /**
     * Get index color model image version.
     * 
     * @param image The original image.
     * @param transparent The transparent pixel (-1 if none).
     * @param size The color size.
     * @param reds The reds color.
     * @param greens The green color.
     * @param blues The blues color.
     * @return The created image.
     */
    private static BufferedImage toImageWithIndexColor(BufferedImage image,
                                                       int transparent,
                                                       int size,
                                                       byte[] reds,
                                                       byte[] greens,
                                                       byte[] blues)
    {
        final IndexColorModel model;
        if (transparent == -1)
        {
            model = new IndexColorModel(Constant.BYTE_2, size, reds, greens, blues);
        }
        else
        {
            model = new IndexColorModel(Constant.BYTE_2, size, reds, greens, blues, transparent);
        }

        final BufferedImage copy = new BufferedImage(image.getWidth(),
                                                     image.getHeight(),
                                                     BufferedImage.TYPE_BYTE_INDEXED,
                                                     model);

        final int[] data = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
        copy.setRGB(0, 0, image.getWidth(), image.getHeight(), data, 0, image.getWidth());

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
     * Get raster buffers from palette.
     * 
     * @param image The image buffer (must not be <code>null</code>).
     * @param palette The raster palette (must not be <code>null</code>).
     * @return The rastered images.
     * @throws LionEngineException If invalid arguments.
     */
    public static BufferedImage[] getRasterBuffer(BufferedImage image, BufferedImage palette)
    {
        final int rastersCount = palette.getHeight() - 1;
        final BufferedImage[] rasters = new BufferedImage[rastersCount];
        for (int rasterIndex = 0; rasterIndex < rastersCount; rasterIndex++)
        {
            rasters[rasterIndex] = getRasterBuffer(image, palette, rasterIndex);
        }
        return rasters;
    }

    /**
     * Get raster buffers from palette with raster inside each line.
     * 
     * @param image The image buffer (must not be <code>null</code>).
     * @param palette The raster palette (must not be <code>null</code>).
     * @param th The tile height.
     * @return The rastered images.
     * @throws LionEngineException If invalid arguments.
     */
    public static BufferedImage[] getRasterBufferInside(BufferedImage image, BufferedImage palette, int th)
    {
        final int rastersCount = (int) Math.ceil((palette.getHeight() - 1.0) / th);
        final BufferedImage[] rasters = new BufferedImage[rastersCount];
        for (int rasterIndex = 0; rasterIndex < rastersCount; rasterIndex++)
        {
            rasters[rasterIndex] = getRasterBuffer(image, palette, th, rasterIndex);
        }
        return rasters;
    }

    /**
     * Get raster buffer from palette at specified index.
     * 
     * @param image The image buffer (must not be <code>null</code>).
     * @param palette The raster palette (must not be <code>null</code>).
     * @param rasterIndex The raster index on palette.
     * @return The rastered images.
     * @throws LionEngineException If invalid arguments.
     */
    private static BufferedImage getRasterBuffer(BufferedImage image, BufferedImage palette, int rasterIndex)
    {
        final int width = image.getWidth();
        final int height = image.getHeight();
        final int paletteColors = palette.getWidth();

        final BufferedImage raster = createImage(width, height, image.getTransparency());
        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                final int originalRgb = image.getRGB(x, y);
                raster.setRGB(x, y, findRaster(paletteColors, palette, originalRgb, rasterIndex));
            }
        }
        return raster;
    }

    /**
     * Get raster buffer from palette at specified index.
     * 
     * @param image The image buffer (must not be <code>null</code>).
     * @param palette The raster palette (must not be <code>null</code>).
     * @param th The tile height.
     * @param rasterIndex The raster index on palette.
     * @return The rastered images.
     * @throws LionEngineException If invalid arguments.
     */
    private static BufferedImage getRasterBuffer(BufferedImage image, BufferedImage palette, int th, int rasterIndex)
    {
        final int width = image.getWidth();
        final int height = image.getHeight();
        final int paletteColors = palette.getWidth();

        final BufferedImage raster = createImage(width, height, image.getTransparency());
        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                final int originalRgb = image.getRGB(x, y);
                raster.setRGB(x,
                              y,
                              findRaster(paletteColors,
                                         palette,
                                         originalRgb,
                                         (height - y - 1) % th + rasterIndex * th));
            }
        }
        return raster;
    }

    /**
     * Get raster buffer from first palette, fill for each height until tile size.
     * 
     * @param image The image buffer (must not be <code>null</code>).
     * @param palette The raster palette (must not be <code>null</code>).
     * @param tileHeight The tile height.
     * @return The rastered images.
     * @throws LionEngineException If invalid arguments.
     */
    public static BufferedImage[] getRasterBufferSmooth(BufferedImage image, BufferedImage palette, int tileHeight)
    {
        final int height = image.getHeight();
        final BufferedImage[] rasters = new BufferedImage[tileHeight];

        for (int maxHeight = 0; maxHeight < tileHeight; maxHeight++)
        {
            final BufferedImage raster = createImage(image.getWidth(), height, image.getTransparency());
            for (int ty = 0; ty < height / tileHeight; ty++)
            {
                fillBuffer(image, palette, raster, tileHeight, maxHeight, ty);
            }
            rasters[maxHeight] = raster;
        }
        return rasters;
    }

    /**
     * Get raster buffer from first palette, fill for each height until tile size.
     * 
     * @param image The image buffer (must not be <code>null</code>).
     * @param palette The raster palette (must not be <code>null</code>).
     * @param fh The horizontal frames.
     * @param fv The vertical frames.
     * @return The rastered images.
     * @throws LionEngineException If invalid arguments.
     */
    public static BufferedImage[] getRasterBufferSmooth(BufferedImage image, BufferedImage palette, int fh, int fv)
    {
        final int height = image.getHeight();
        final int frameHeight = height / fv;
        final BufferedImage[] rasters = new BufferedImage[frameHeight];

        for (int maxHeight = 0; maxHeight < frameHeight; maxHeight++)
        {
            final BufferedImage raster = createImage(image.getWidth(), height, image.getTransparency());
            fillBuffer(image, palette, raster, frameHeight, maxHeight, 0);
            rasters[maxHeight] = raster;
        }
        return rasters;
    }

    /**
     * Fill raster buffer from palette.
     * 
     * @param image The image buffer (must not be <code>null</code>).
     * @param palette The raster palette (must not be <code>null</code>).
     * @param raster The raster to fill (must not be <code>null</code>).
     * @param tileHeight The tile height.
     * @param maxHeight The max height for palette fill on raster tile.
     * @param ty The vertical tile to fill.
     * @throws LionEngineException If invalid arguments.
     */
    private static void fillBuffer(BufferedImage image,
                                   BufferedImage palette,
                                   BufferedImage raster,
                                   int tileHeight,
                                   int maxHeight,
                                   int ty)
    {
        final int paletteColors = palette.getWidth();
        for (int y = 0; y < tileHeight; y++)
        {
            for (int x = 0; x < image.getWidth(); x++)
            {
                final int ry = tileHeight - 1 - y + ty * tileHeight;
                final int originalRgb = image.getRGB(x, ry);
                if (y <= maxHeight)
                {
                    final int rasterRgb = findRaster(paletteColors, palette, originalRgb, 0);
                    raster.setRGB(x, ry, rasterRgb);
                }
                else
                {
                    raster.setRGB(x, ry, originalRgb);
                }
            }
        }
    }

    /**
     * Find corresponding raster color from original one.
     * 
     * @param paletteColors The number of rasters in palette.
     * @param palette The palette data.
     * @param originalRgb The original color.
     * @param rasterIndex The raster index to use.
     * @return The associated raster color.
     */
    private static int findRaster(int paletteColors, BufferedImage palette, int originalRgb, int rasterIndex)
    {
        for (int p = 0; p < paletteColors; p++)
        {
            if (palette.getRGB(p, 0) == originalRgb && rasterIndex + 1 < palette.getHeight())
            {
                return palette.getRGB(p, rasterIndex + 1);
            }
        }
        return originalRgb;
    }

    /**
     * Get raster buffer with offsets applied.
     * 
     * @param image The image buffer (must not be <code>null</code>).
     * @param palette The palette offset (must not be <code>null</code>).
     * @param raster The raster color (must not be <code>null</code>).
     * @param offsets The offsets number (rasters inside).
     * @return The rastered images.
     * @throws LionEngineException If invalid arguments.
     */
    public static BufferedImage[] getRasterBufferOffset(BufferedImage image,
                                                        BufferedImage palette,
                                                        BufferedImage raster,
                                                        int offsets)
    {
        final int rasterHeight = raster.getHeight();
        final BufferedImage[] rasters = new BufferedImage[rasterHeight];
        final int width = image.getWidth();
        final int height = image.getHeight();

        for (int rasterNumber = 0; rasterNumber < rasters.length; rasterNumber++)
        {
            final BufferedImage buffer = createImage(width, height, image.getTransparency());
            fillBufferOffset(image, palette, raster, buffer, offsets, rasterNumber);
            rasters[rasterNumber] = buffer;
        }
        return rasters;
    }

    /**
     * Fill buffer offset.
     * 
     * @param image The image source reference.
     * @param palette The palette reference.
     * @param raster The raster colors.
     * @param buffer The created buffer to fill.
     * @param offsets The offsets value.
     * @param rasterNumber The current raster number.
     */
    private static void fillBufferOffset(BufferedImage image,
                                         BufferedImage palette,
                                         BufferedImage raster,
                                         BufferedImage buffer,
                                         int offsets,
                                         int rasterNumber)
    {
        final int rasterHeight = raster.getHeight();
        final int width = image.getWidth();
        final int height = image.getHeight();

        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                final int color = image.getRGB(x, y);
                if ((color >> Constant.BYTE_4 & 0xFF) != 0)
                {
                    final int currentOffset = (height - 1 - y) / offsets;
                    final int paletteOffset = findPaletteOffset(palette, color);
                    final int py = rasterHeight - 1 - rasterNumber - currentOffset * offsets - paletteOffset * 3;
                    buffer.setRGB(x, y, raster.getRGB(0, UtilMath.clamp(py, 0, rasterHeight - 1)));
                }
            }
        }
    }

    /**
     * Find palette offset.
     * 
     * @param palette The palette reference.
     * @param color The color to find.
     * @return The palette offset.
     */
    private static int findPaletteOffset(BufferedImage palette, int color)
    {
        for (int y = 0; y < palette.getHeight(); y++)
        {
            if (palette.getRGB(0, y) == color)
            {
                return y;
            }
        }
        return 0;
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
