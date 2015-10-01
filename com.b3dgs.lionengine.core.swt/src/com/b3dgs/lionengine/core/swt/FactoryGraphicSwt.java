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
package com.b3dgs.lionengine.core.swt;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Image;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.Filter;
import com.b3dgs.lionengine.Hq2x;
import com.b3dgs.lionengine.Hq3x;
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
import com.b3dgs.lionengine.core.Verbose;

/**
 * Graphic factory implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class FactoryGraphicSwt implements FactoryGraphic
{
    /** Reading image message. */
    private static final String ERROR_IMAGE_READING = "Error on reading image !";
    /** Save image message. */
    private static final String ERROR_IMAGE_SAVE = "Unable to save image: ";

    /**
     * Constructor.
     */
    public FactoryGraphicSwt()
    {
        // Nothing to do
    }

    /*
     * FactoryGraphic
     */

    @Override
    public Screen createScreen(Renderer renderer)
    {
        final Config config = renderer.getConfig();
        if (config.isWindowed())
        {
            return new ScreenWindowedSwt(renderer);
        }
        return new ScreenFullSwt(renderer);
    }

    @Override
    public Graphic createGraphic()
    {
        return new GraphicSwt();
    }

    @Override
    public Transform createTransform()
    {
        return new TransformSwt();
    }

    @Override
    public Text createText(String fontName, int size, TextStyle style)
    {
        return new TextSwt(ToolsSwt.getDisplay(), fontName, size, style);
    }

    @Override
    public ImageBuffer createImageBuffer(int width, int height, Transparency transparency)
    {
        Check.superiorOrEqual(width, 0);
        Check.superiorOrEqual(height, 0);

        final Image image = ToolsSwt.createImage(width, height, ToolsSwt.getTransparency(transparency));
        final ImageBufferSwt buffer = new ImageBufferSwt(image);

        final Graphic g = buffer.createGraphic();
        g.setColor(ColorRgba.BLACK);
        g.drawRect(0, 0, width, height, true);
        g.dispose();

        return buffer;
    }

    @Override
    public ImageBuffer getImageBuffer(Media media)
    {
        final InputStream input = media.getInputStream();
        try
        {
            return new ImageBufferSwt(ToolsSwt.getDisplay(), ToolsSwt.getImageData(input));
        }
        catch (final SWTException exception)
        {
            throw new LionEngineException(exception, ERROR_IMAGE_READING);
        }
        finally
        {
            try
            {
                input.close();
            }
            catch (final IOException exception2)
            {
                Verbose.exception(FactoryGraphicSwt.class, "getImageBuffer", exception2);
            }
        }
    }

    @Override
    public ImageBuffer getImageBuffer(ImageBuffer image)
    {
        return new ImageBufferSwt(ToolsSwt.getImage(ToolsSwt.getBuffer(image)));
    }

    @Override
    public ImageBuffer applyMask(ImageBuffer image, ColorRgba maskColor)
    {
        return new ImageBufferSwt(ToolsSwt.applyMask(ToolsSwt.getBuffer(image), maskColor.getRgba()));
    }

    @Override
    public ImageBuffer[] splitImage(ImageBuffer image, int h, int v)
    {
        final Image[] images = ToolsSwt.splitImage(ToolsSwt.getBuffer(image), h, v);
        final ImageBuffer[] imageBuffers = new ImageBuffer[images.length];
        for (int i = 0; i < imageBuffers.length; i++)
        {
            imageBuffers[i] = new ImageBufferSwt(images[i]);
        }
        return imageBuffers;
    }

    @Override
    public ImageBuffer rotate(ImageBuffer image, int angle)
    {
        return new ImageBufferSwt(ToolsSwt.rotate(ToolsSwt.getBuffer(image), angle));
    }

    @Override
    public ImageBuffer resize(ImageBuffer image, int width, int height)
    {
        return new ImageBufferSwt(ToolsSwt.resize(ToolsSwt.getBuffer(image), width, height));
    }

    @Override
    public ImageBuffer flipHorizontal(ImageBuffer image)
    {
        return new ImageBufferSwt(ToolsSwt.flipHorizontal(ToolsSwt.getBuffer(image)));
    }

    @Override
    public ImageBuffer flipVertical(ImageBuffer image)
    {
        return new ImageBufferSwt(ToolsSwt.flipVertical(ToolsSwt.getBuffer(image)));
    }

    @Override
    public ImageBuffer applyFilter(ImageBuffer image, Filter filter) throws LionEngineException
    {
        final ImageBuffer filtered;
        switch (filter)
        {
            case NONE:
                filtered = image;
                break;
            case BILINEAR:
                filtered = new ImageBufferSwt(ToolsSwt.applyBilinearFilter(ToolsSwt.getBuffer(image)));
                break;
            case HQ2X:
                final Hq2x hq2x = new Hq2x(image);
                filtered = hq2x.getScaledImage();
                break;
            case HQ3X:
                final Hq3x hq3x = new Hq3x(image);
                filtered = hq3x.getScaledImage();
                break;
            default:
                throw new RuntimeException();
        }
        return filtered;
    }

    @Override
    public void saveImage(ImageBuffer image, Media media)
    {
        final OutputStream output = media.getOutputStream();
        try
        {
            ToolsSwt.saveImage(ToolsSwt.getBuffer(image), output);
        }
        catch (final SWTException exception)
        {
            throw new LionEngineException(exception, ERROR_IMAGE_SAVE);
        }
        finally
        {
            try
            {
                output.close();
            }
            catch (final IOException exception2)
            {
                Verbose.exception(FactoryGraphicSwt.class, "saveImage", exception2);
            }
        }
    }

    @Override
    public ImageBuffer getRasterBuffer(ImageBuffer image, int fr, int fg, int fb, int er, int eg, int eb, int ref)
    {
        return new ImageBufferSwt(ToolsSwt.getRasterBuffer(ToolsSwt.getBuffer(image), fr, fg, fb, er, eg, eb, ref));
    }
}
