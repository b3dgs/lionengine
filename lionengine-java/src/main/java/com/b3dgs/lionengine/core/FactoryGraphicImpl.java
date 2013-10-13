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
package com.b3dgs.lionengine.core;

import java.awt.AlphaComposite;
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
import java.awt.image.ConvolveOp;
import java.awt.image.DataBufferInt;
import java.awt.image.Kernel;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.Filter;
import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Text;
import com.b3dgs.lionengine.TextStyle;
import com.b3dgs.lionengine.Transparency;

/**
 * Graphic factory implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
final class FactoryGraphicImpl
        implements FactoryGraphic
{
    /** Bilinear filter. */
    private static final float[] BILINEAR_FILTER = new float[]
    {
            1 / 9f, 1 / 9f, 1 / 9f, 1 / 9f, 1 / 9f, 1 / 9f, 1 / 9f, 1 / 9f, 1 / 9f
    };
    /** Graphics environment. */
    private static final GraphicsEnvironment ENV = GraphicsEnvironment.getLocalGraphicsEnvironment();
    /** Graphics device. */
    private static final GraphicsDevice DEV = FactoryGraphicImpl.ENV.getDefaultScreenDevice();
    /** Graphics configuration. */
    private static final GraphicsConfiguration CONFIG = FactoryGraphicImpl.DEV.getDefaultConfiguration();

    /**
     * Create a hidden cursor.
     * 
     * @return Hidden cursor.
     */
    static Cursor createHiddenCursor()
    {
        final Toolkit toolkit = Toolkit.getDefaultToolkit();
        final Dimension dim = toolkit.getBestCursorSize(1, 1);
        final ImageBuffer cursor = UtilityImage.createImageBuffer(dim.width, dim.height, Transparency.BITMASK);
        final BufferedImage buffer = FactoryGraphicImpl.getBuffer(UtilityImage.applyMask(cursor, ColorRgba.BLACK));
        return toolkit.createCustomCursor(buffer, new Point(0, 0), "hiddenCursor");
    }

    /**
     * Get the image buffer.
     * 
     * @param imageBuffer The image buffer.
     * @return The buffer.
     */
    private static BufferedImage getBuffer(ImageBuffer imageBuffer)
    {
        return ((ImageBufferImpl) imageBuffer).getBuffer();
    }

    /**
     * Get the image transparency equivalence.
     * 
     * @param transparency The transparency type.
     * @return The transparency value.
     */
    private static int getTransparency(Transparency transparency)
    {
        switch (transparency)
        {
            case OPAQUE:
                return java.awt.Transparency.OPAQUE;
            case BITMASK:
                return java.awt.Transparency.BITMASK;
            case TRANSLUCENT:
                return java.awt.Transparency.TRANSLUCENT;
            default:
                return 0;
        }
    }

    /**
     * Enable all graphics improvement. May decrease overall performances.
     * 
     * @param g The graphic context.
     */
    static void optimizeGraphicsQuality(Graphics2D g)
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
    static void optimizeGraphicsSpeed(Graphics2D g)
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
    private static void optimizeGraphics(Graphics2D g)
    {
        FactoryGraphicImpl.optimizeGraphicsSpeed(g);
    }

    /**
     * Constructor.
     */
    FactoryGraphicImpl()
    {
        // Nothing to do
    }

    /*
     * FactoryGraphic
     */

    @Override
    public Screen createScreen(Config config)
    {
        return new ScreenImpl(config);
    }

    @Override
    public Text createText(String fontName, int size, TextStyle style)
    {
        return new TextImpl(fontName, size, style);
    }

    @Override
    public Graphic createGraphic()
    {
        return new GraphicImpl();
    }

    @Override
    public ImageBuffer createCompatibleImage(int width, int height, Transparency transparency)
    {
        final BufferedImage buffer = FactoryGraphicImpl.CONFIG.createCompatibleImage(width, height,
                FactoryGraphicImpl.getTransparency(transparency));
        return new ImageBufferImpl(buffer);
    }

    @Override
    public ImageBuffer getImageBuffer(InputStream inputStream, boolean alpha) throws IOException
    {
        final BufferedImage buffer = ImageIO.read(inputStream);
        int transparency = buffer.getTransparency();
        if (alpha)
        {
            transparency = java.awt.Transparency.TRANSLUCENT;
        }
        final BufferedImage image = FactoryGraphicImpl.CONFIG.createCompatibleImage(buffer.getWidth(),
                buffer.getHeight(), transparency);
        final Graphics2D g = image.createGraphics();

        g.setComposite(AlphaComposite.Src);
        g.drawImage(buffer, 0, 0, null);
        g.dispose();

        return new ImageBufferImpl(image);
    }

    @Override
    public ImageBuffer getImageBuffer(ImageBuffer imageBuffer)
    {
        final BufferedImage buffer = FactoryGraphicImpl.CONFIG.createCompatibleImage(imageBuffer.getWidth(),
                imageBuffer.getHeight(), FactoryGraphicImpl.getTransparency(imageBuffer.getTransparency()));
        final Graphics2D g = buffer.createGraphics();

        g.setComposite(AlphaComposite.Src);
        g.drawImage(FactoryGraphicImpl.getBuffer(imageBuffer), 0, 0, null);
        g.dispose();

        return new ImageBufferImpl(buffer);
    }

    @Override
    public ImageBuffer applyMask(ImageBuffer imageBuffer, ColorRgba maskColor)
    {
        final BufferedImage alpha = FactoryGraphicImpl.CONFIG.createCompatibleImage(imageBuffer.getWidth(),
                imageBuffer.getHeight(), java.awt.Transparency.BITMASK);
        final Graphics2D g = alpha.createGraphics();

        g.setComposite(AlphaComposite.Src);
        g.drawImage(FactoryGraphicImpl.getBuffer(imageBuffer), 0, 0, null);
        g.dispose();

        final int height = alpha.getHeight();
        final int width = alpha.getWidth();

        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                final int col = alpha.getRGB(x, y);
                final int flag = 0x00ffffff;
                if (col == maskColor.getRgba())
                {
                    alpha.setRGB(x, y, col & flag);
                }
            }
        }

        return new ImageBufferImpl(alpha);
    }

    @Override
    public ImageBuffer[] splitImage(ImageBuffer imageBuffer, int row, int col)
    {
        final BufferedImage image = FactoryGraphicImpl.getBuffer(imageBuffer);
        final int total = row * col;
        final int width = image.getWidth() / row, height = image.getHeight() / col;
        final int transparency = image.getColorModel().getTransparency();
        final BufferedImage[] images = new BufferedImage[total];
        int frame = 0;

        for (int y = 0; y < col; y++)
        {
            for (int x = 0; x < row; x++)
            {
                images[frame] = FactoryGraphicImpl.CONFIG.createCompatibleImage(width, height, transparency);
                final Graphics2D g = images[frame].createGraphics();
                FactoryGraphicImpl.optimizeGraphics(g);
                g.drawImage(image, 0, 0, width, height, x * width, y * height, (x + 1) * width, (y + 1) * height, null);
                g.dispose();
                frame++;
            }
        }
        final ImageBuffer[] imageBuffers = new ImageBuffer[images.length];
        for (int i = 0; i < imageBuffers.length; i++)
        {
            imageBuffers[i] = new ImageBufferImpl(images[i]);
        }

        return imageBuffers;
    }

    @Override
    public ImageBuffer rotate(ImageBuffer imageBuffer, int angle)
    {
        final BufferedImage image = FactoryGraphicImpl.getBuffer(imageBuffer);
        final int w = image.getWidth(), h = image.getHeight();
        final int transparency = image.getColorModel().getTransparency();
        final BufferedImage rotated = FactoryGraphicImpl.CONFIG.createCompatibleImage(w, h, transparency);
        final Graphics2D g = rotated.createGraphics();

        FactoryGraphicImpl.optimizeGraphics(g);
        g.rotate(Math.toRadians(angle), w / 2.0, h / 2.0);
        g.drawImage(image, null, 0, 0);
        g.dispose();

        return new ImageBufferImpl(rotated);
    }

    @Override
    public ImageBuffer resize(ImageBuffer imageBuffer, int width, int height)
    {
        final BufferedImage image = FactoryGraphicImpl.getBuffer(imageBuffer);
        final int transparency = image.getColorModel().getTransparency();
        final BufferedImage resized = FactoryGraphicImpl.CONFIG.createCompatibleImage(width, height, transparency);
        final Graphics2D g = resized.createGraphics();

        FactoryGraphicImpl.optimizeGraphics(g);
        g.drawImage(image, 0, 0, width, height, 0, 0, image.getWidth(), image.getHeight(), null);
        g.dispose();

        return new ImageBufferImpl(resized);
    }

    @Override
    public ImageBuffer flipHorizontal(ImageBuffer imageBuffer)
    {
        final BufferedImage image = FactoryGraphicImpl.getBuffer(imageBuffer);
        final int w = image.getWidth(), h = image.getHeight();
        final BufferedImage flipped = FactoryGraphicImpl.CONFIG.createCompatibleImage(w, h, image.getColorModel()
                .getTransparency());
        final Graphics2D g = flipped.createGraphics();

        FactoryGraphicImpl.optimizeGraphics(g);
        g.drawImage(image, 0, 0, w, h, w, 0, 0, h, null);
        g.dispose();

        return new ImageBufferImpl(flipped);
    }

    @Override
    public ImageBuffer flipVertical(ImageBuffer imageBuffer)
    {
        final BufferedImage image = FactoryGraphicImpl.getBuffer(imageBuffer);
        final int w = image.getWidth(), h = image.getHeight();
        final BufferedImage flipped = FactoryGraphicImpl.CONFIG.createCompatibleImage(w, h, image.getColorModel()
                .getTransparency());
        final Graphics2D g = flipped.createGraphics();

        FactoryGraphicImpl.optimizeGraphics(g);
        g.drawImage(image, 0, 0, w, h, 0, h, w, 0, null);
        g.dispose();

        return new ImageBufferImpl(flipped);
    }

    @Override
    public ImageBuffer applyFilter(ImageBuffer imageBuffer, Filter filter)
    {
        final BufferedImage image = FactoryGraphicImpl.getBuffer(imageBuffer);
        final Kernel kernel;
        switch (filter)
        {
            case BILINEAR:
                kernel = new Kernel(3, 3, FactoryGraphicImpl.BILINEAR_FILTER);
                break;
            default:
                throw new LionEngineException("Unsupported filter: " + filter.name());
        }
        final ConvolveOp op = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
        return new ImageBufferImpl(op.filter(image, null));
    }

    @Override
    public void saveImage(ImageBuffer imageBuffer, OutputStream outputStream) throws IOException
    {
        final BufferedImage image = FactoryGraphicImpl.getBuffer(imageBuffer);
        ImageIO.write(image, "png", outputStream);
    }

    @Override
    public ImageBuffer getRasterBuffer(ImageBuffer imageBuffer, int fr, int fg, int fb, int er, int eg, int eb,
            int refSize)
    {
        final BufferedImage image = FactoryGraphicImpl.getBuffer(imageBuffer);
        final boolean method = true;
        final BufferedImage raster = FactoryGraphicImpl.CONFIG.createCompatibleImage(image.getWidth(),
                image.getHeight(), image.getTransparency());

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

        return new ImageBufferImpl(raster);
    }
}
