/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.swt.graphic;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Image;

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
// CHECKSTYLE IGNORE LINE: DataAbstractionCoupling
public final class FactoryGraphicSwt implements FactoryGraphic
{
    /** Reading image message. */
    static final String ERROR_IMAGE_READING = "Error on reading image !";
    /** Save image message. */
    static final String ERROR_IMAGE_SAVE = "Unable to save image: ";

    /**
     * Constructor.
     */
    public FactoryGraphicSwt()
    {
        super();
    }

    @Override
    public Screen createScreen(Config config)
    {
        Check.notNull(config);

        if (config.isWindowed())
        {
            return new ScreenWindowedSwt(config);
        }
        return new ScreenFullSwt(config);
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
    public ImageBuffer createImageBuffer(int width, int height)
    {
        Check.superiorStrict(width, 0);
        Check.superiorStrict(height, 0);

        final Image image = ToolsSwt.createImage(width, height, ToolsSwt.getTransparency(Transparency.OPAQUE));
        final ImageBufferSwt buffer = new ImageBufferSwt(image);

        final Graphic g = buffer.createGraphic();
        g.setColor(ColorRgba.BLACK);
        g.drawRect(0, 0, width, height, true);
        g.dispose();

        return buffer;
    }

    @Override
    public ImageBuffer createImageBuffer(int width, int height, ColorRgba transparency)
    {
        Check.superiorStrict(width, 0);
        Check.superiorStrict(height, 0);

        final Image image = ToolsSwt.createImage(width, height, transparency);
        return new ImageBufferSwt(image);
    }

    @Override
    public ImageBuffer createImageBufferAlpha(int width, int height)
    {
        Check.superiorStrict(width, 0);
        Check.superiorStrict(height, 0);

        final Image image = ToolsSwt.createImage(width, height, ToolsSwt.getTransparency(Transparency.TRANSLUCENT));
        final ImageBufferSwt buffer = new ImageBufferSwt(image);

        final Graphic g = buffer.createGraphic();
        g.setColor(ColorRgba.TRANSPARENT);
        g.drawRect(0, 0, width, height, true);
        g.dispose();

        return buffer;
    }

    @Override
    public ImageBuffer getImageBuffer(Media media)
    {
        try (InputStream input = media.getInputStream())
        {
            return new ImageBufferSwt(ToolsSwt.getDisplay(), ToolsSwt.getImageData(input));
        }
        catch (final SWTException | LionEngineException | IOException exception)
        {
            throw new LionEngineException(exception, media, ERROR_IMAGE_READING);
        }
    }

    @Override
    public ImageBuffer getImageBuffer(ImageBuffer image)
    {
        return new ImageBufferSwt(ToolsSwt.getImage((Image) image.getSurface()));
    }

    @Override
    public ImageBuffer getImageBufferDraw(ImageBuffer image)
    {
        return new ImageBufferSwt(ToolsSwt.getImage((Image) image.getSurface()));
    }

    @Override
    public ImageBuffer applyMask(ImageBuffer image, ColorRgba maskColor)
    {
        return new ImageBufferSwt(ToolsSwt.applyMask((Image) image.getSurface(), maskColor.getRgba()));
    }

    @Override
    public ImageBuffer[] splitImage(ImageBuffer image, int h, int v)
    {
        final Image[] images = ToolsSwt.splitImage((Image) image.getSurface(), h, v);
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
        return new ImageBufferSwt(ToolsSwt.rotate((Image) image.getSurface(), angle));
    }

    @Override
    public ImageBuffer resize(ImageBuffer image, int width, int height)
    {
        return new ImageBufferSwt(ToolsSwt.resize((Image) image.getSurface(), width, height));
    }

    @Override
    public ImageBuffer flipHorizontal(ImageBuffer image)
    {
        return new ImageBufferSwt(ToolsSwt.flipHorizontal((Image) image.getSurface()));
    }

    @Override
    public ImageBuffer flipVertical(ImageBuffer image)
    {
        return new ImageBufferSwt(ToolsSwt.flipVertical((Image) image.getSurface()));
    }

    @Override
    public void saveImage(ImageBuffer image, Media media)
    {
        Check.notNull(media);

        try (OutputStream output = media.getOutputStream())
        {
            ToolsSwt.saveImage((Image) image.getSurface(), output);
        }
        catch (final SWTException | NullPointerException | IOException exception)
        {
            throw new LionEngineException(exception, media, ERROR_IMAGE_SAVE);
        }
    }

    @Override
    public void generateTileset(ImageBuffer[] images, Media media)
    {
        Check.notNull(images);
        Check.notNull(media);

        final int tiles = images.length;
        if (tiles == 0)
        {
            throw new LionEngineException("No images found !");
        }

        final int width = images[0].getWidth();
        final int height = images[0].getHeight();
        final Transparency transparency = images[0].getTransparency();

        final int multDistance = (int) Math.ceil(width * tiles / (double) height) / 4;
        final int[] mult = UtilMath.getClosestSquareMult(tiles, multDistance);

        final ImageBuffer tile = new ImageBufferSwt(ToolsSwt.createImage(width * mult[1],
                                                                         height * mult[0],
                                                                         ToolsSwt.getTransparency(transparency)));
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
        // TODO To be implemented !
        final ImageBuffer[] buffers = new ImageBuffer[palette.getHeight()];
        for (int i = 0; i < buffers.length; i++)
        {
            buffers[i] = new ImageBufferSwt(image.getSurface());
            buffers[i].prepare();
        }
        return buffers;
    }

    @Override
    public ImageBuffer[] getRasterBufferInside(ImageBuffer image, ImageBuffer palette, int th)
    {
        // TODO To be implemented !
        return getRasterBuffer(image, palette);
    }

    @Override
    public ImageBuffer[] getRasterBufferSmooth(ImageBuffer image, ImageBuffer palette, int tileHeight)
    {
        // TODO To be implemented !
        return getRasterBuffer(image, palette);
    }

    @Override
    public ImageBuffer[] getRasterBufferSmooth(ImageBuffer image, ImageBuffer palette, int fh, int fv)
    {
        // TODO To be implemented !
        return getRasterBuffer(image, palette);
    }

    @Override
    public ImageBuffer[] getRasterBufferOffset(Media image, Media palette, Media raster, int offsets)
    {
        // TODO To be implemented !
        final ImageBuffer buffer = getImageBuffer(image);
        buffer.prepare();

        return new ImageBuffer[]
        {
            new ImageBufferSwt(buffer.getSurface())
        };
    }
}
