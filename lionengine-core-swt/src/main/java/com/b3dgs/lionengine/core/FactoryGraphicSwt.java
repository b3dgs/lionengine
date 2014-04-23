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
import java.io.InputStream;
import java.io.OutputStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;

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
final class FactoryGraphicSwt
        implements FactoryGraphic
{
    /**
     * Create a hidden cursor.
     * 
     * @return Hidden cursor.
     */
    static Cursor createHiddenCursor()
    {
        final Color white = ScreenSwt.display.getSystemColor(SWT.COLOR_WHITE);
        final Color black = ScreenSwt.display.getSystemColor(SWT.COLOR_BLACK);
        final PaletteData palette = new PaletteData(new RGB[]
        {
                white.getRGB(), black.getRGB()
        });
        final ImageData sourceData = new ImageData(16, 16, 1, palette);
        sourceData.transparentPixel = 0;
        final Cursor cursor = new Cursor(ScreenSwt.display, sourceData, 0, 0);
        return cursor;
    }

    /**
     * Get the image transparency equivalence.
     * 
     * @param transparency The transparency type.
     * @return The transparency value.
     */
    static int getTransparency(Transparency transparency)
    {
        switch (transparency)
        {
            case OPAQUE:
                return SWT.TRANSPARENCY_NONE;
            case BITMASK:
                return SWT.TRANSPARENCY_MASK;
            case TRANSLUCENT:
                return SWT.TRANSPARENCY_ALPHA;
            default:
                return 0;
        }
    }

    /**
     * Get the image buffer.
     * 
     * @param imageBuffer The image buffer.
     * @return The buffer.
     */
    private static Image getBuffer(ImageBuffer imageBuffer)
    {
        return ((ImageBufferSwt) imageBuffer).getBuffer();
    }

    /**
     * Constructor.
     */
    FactoryGraphicSwt()
    {
        // Nothing to do
    }

    /*
     * FactoryGraphic
     */

    @Override
    public Renderer createRenderer(Config config)
    {
        return new RendererSwt(config);
    }

    @Override
    public Screen createScreen(Renderer renderer, Config config)
    {
        return new ScreenSwt(renderer, config);
    }

    @Override
    public Text createText(String fontName, int size, TextStyle style)
    {
        return new TextSwt(fontName, size, style);
    }

    @Override
    public Graphic createGraphic()
    {
        return new GraphicSwt();
    }

    @Override
    public ImageBuffer createImageBuffer(int width, int height, Transparency transparency)
    {
        final Image buffer = new Image(ScreenSwt.display, width, height);
        return new ImageBufferSwt(buffer);
    }

    @Override
    public ImageBuffer getImageBuffer(Media media, boolean alpha) throws IOException
    {
        try (final InputStream inputStream = media.getStream();)
        {
            final Image image = new Image(ScreenSwt.display, inputStream);
            return new ImageBufferSwt(image);
        }
    }

    @Override
    public ImageBuffer getImageBuffer(ImageBuffer imageBuffer)
    {
        final Image image = new Image(ScreenSwt.display, FactoryGraphicSwt.getBuffer(imageBuffer), SWT.IMAGE_COPY);
        return new ImageBufferSwt(image);
    }

    @Override
    public ImageBuffer applyMask(ImageBuffer imageBuffer, ColorRgba maskColor)
    {
        // TODO To be implemented
        throw new LionEngineException("Not implemented yet !");
    }

    @Override
    public ImageBuffer[] splitImage(ImageBuffer imageBuffer, int h, int v)
    {
        final Image image = FactoryGraphicSwt.getBuffer(imageBuffer);
        final int total = h * v;
        final int width = imageBuffer.getWidth() / h, height = imageBuffer.getHeight() / v;
        final Image[] images = new Image[total];
        int frame = 0;

        for (int y = 0; y < v; y++)
        {
            for (int x = 0; x < h; x++)
            {
                images[frame] = new Image(ScreenSwt.display, width, height);
                final GC gc = new GC(images[frame]);
                gc.drawImage(image, x * width, y * height, width, height, 0, 0, width, height);
                gc.dispose();
                frame++;
            }
        }
        final ImageBuffer[] imageBuffers = new ImageBuffer[images.length];
        for (int i = 0; i < imageBuffers.length; i++)
        {
            imageBuffers[i] = new ImageBufferSwt(images[i]);
        }

        return imageBuffers;
    }

    @Override
    public ImageBuffer rotate(ImageBuffer imageBuffer, int angle)
    {
        final Image image = FactoryGraphicSwt.getBuffer(imageBuffer);
        final int w = imageBuffer.getWidth(), h = imageBuffer.getHeight();

        final ImageData sourceData = image.getImageData();
        final ImageData newData = new ImageData(w, h, sourceData.depth, sourceData.palette);
        newData.transparentPixel = sourceData.transparentPixel;
        final Image rotated = new Image(ScreenSwt.display, newData);

        final GC gc = new GC(rotated);

        final org.eclipse.swt.graphics.Transform transform = new org.eclipse.swt.graphics.Transform(ScreenSwt.display);
        final float rotate = (float) Math.toRadians(angle);
        final float cos = (float) Math.cos(rotate);
        final float sin = (float) Math.sin(rotate);
        transform.setElements(cos, sin, -sin, cos, w / 2, h / 2);

        gc.setTransform(transform);
        gc.drawImage(image, -w / 2, -h / 2);
        gc.dispose();

        return new ImageBufferSwt(rotated);
    }

    @Override
    public ImageBuffer resize(ImageBuffer imageBuffer, int width, int height)
    {
        final Image image = FactoryGraphicSwt.getBuffer(imageBuffer);
        final Image resized = new Image(ScreenSwt.display, image.getImageData().scaledTo(width, height));

        return new ImageBufferSwt(resized);
    }

    @Override
    public ImageBuffer flipHorizontal(ImageBuffer imageBuffer)
    {
        final Image image = FactoryGraphicSwt.getBuffer(imageBuffer);
        final int w = imageBuffer.getWidth(), h = imageBuffer.getHeight();
        final Image flipped = new Image(ScreenSwt.display, w, h);
        final GC gc = new GC(flipped);

        final org.eclipse.swt.graphics.Transform transform = new org.eclipse.swt.graphics.Transform(ScreenSwt.display);
        transform.scale(-1.0f, 1.0f);
        gc.setTransform(transform);
        gc.drawImage(image, 0, 0);
        gc.dispose();

        return new ImageBufferSwt(flipped);
    }

    @Override
    public ImageBuffer flipVertical(ImageBuffer imageBuffer)
    {
        final Image image = FactoryGraphicSwt.getBuffer(imageBuffer);
        final int w = imageBuffer.getWidth(), h = imageBuffer.getHeight();
        final Image flipped = new Image(ScreenSwt.display, w, h);
        final GC gc = new GC(flipped);

        final org.eclipse.swt.graphics.Transform transform = new org.eclipse.swt.graphics.Transform(ScreenSwt.display);
        transform.scale(1.0f, -1.0f);
        gc.setTransform(transform);
        gc.drawImage(image, 0, 0);
        gc.dispose();

        return new ImageBufferSwt(flipped);
    }

    @Override
    public ImageBuffer applyFilter(ImageBuffer imageBuffer, Filter filter)
    {
        // TODO To be implemented
        throw new LionEngineException("Not implemented yet !");
    }

    @Override
    public void saveImage(ImageBuffer imageBuffer, OutputStream outputStream) throws IOException
    {
        final Image image = FactoryGraphicSwt.getBuffer(imageBuffer);
        final ImageLoader loader = new ImageLoader();
        loader.data = new ImageData[]
        {
            image.getImageData()
        };
        loader.save(outputStream, SWT.IMAGE_PNG);
    }

    @Override
    public ImageBuffer getRasterBuffer(ImageBuffer imageBuffer, int fr, int fg, int fb, int er, int eg, int eb,
            int refSize)
    {
        // TODO To be implemented
        throw new LionEngineException("Not implemented yet !");
    }

    @Override
    public Transform createTransform()
    {
        return new TransformSwt();
    }
}
