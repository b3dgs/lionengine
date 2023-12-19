/*
 * Copyright (C) 2013-2021 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 *
 * This program is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
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
package com.b3dgs.lionengine.android.graphic;

import com.b3dgs.lionengine.Config;
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
public final class FactoryGraphicAndroid implements FactoryGraphic
{
    /**
     * Internal constructor.
     */
    public FactoryGraphicAndroid()
    {
        super();
    }

    /*
     * FactoryGraphic
     */

    @Override
    public Screen createScreen(Config config)
    {
        return new ScreenAndroid(config);
    }

    @Override
    public Graphic createGraphic()
    {
        return new GraphicAndroid();
    }

    @Override
    public Transform createTransform()
    {
        return new TransformAndroid();
    }

    @Override
    public Text createText(String fontName, int size, TextStyle style)
    {
        return new TextAndroid(fontName, size, style);
    }

    @Override
    public ImageBuffer createImageBuffer(int width, int height)
    {
        return UtilImage.createImage(width, height);
    }

    @Override
    public ImageBuffer createImageBuffer(int width, int height, ColorRgba transparency)
    {
        return UtilImage.createImage(width, height);
    }

    @Override
    public ImageBuffer createImageBufferAlpha(int width, int height)
    {
        return UtilImage.createImage(width, height);
    }

    @Override
    public ImageBuffer getImageBuffer(Media media)
    {
        return UtilImage.getImage(media);
    }

    @Override
    public ImageBuffer getImageBuffer(ImageBuffer imageBuffer)
    {
        return UtilImage.getImage(imageBuffer);
    }

    @Override
    public ImageBuffer getImageBufferDraw(ImageBuffer imageBuffer)
    {
        return UtilImage.getImage(imageBuffer);
    }

    @Override
    public ImageBuffer applyMask(ImageBuffer imageBuffer, ColorRgba maskColor)
    {
        return UtilImage.applyMask(imageBuffer, maskColor);
    }

    @Override
    public ImageBuffer[] splitImage(ImageBuffer imageBuffer, int h, int v)
    {
        return UtilImage.splitImage(imageBuffer, h, v);
    }

    @Override
    public ImageBuffer rotate(ImageBuffer imageBuffer, int angle)
    {
        return UtilImage.rotate(imageBuffer, angle);
    }

    @Override
    public ImageBuffer resize(ImageBuffer imageBuffer, int width, int height)
    {
        return UtilImage.resize(imageBuffer, width, height);
    }

    @Override
    public ImageBuffer flipHorizontal(ImageBuffer imageBuffer)
    {
        return UtilImage.flipHorizontal(imageBuffer);
    }

    @Override
    public ImageBuffer flipVertical(ImageBuffer imageBuffer)
    {
        return UtilImage.flipVertical(imageBuffer);
    }

    @Override
    public void saveImage(ImageBuffer imageBuffer, Media media)
    {
        UtilImage.saveImage(imageBuffer, media);
    }

    @Override
    public ImageBuffer[] getRasterBuffer(final ImageBuffer image, final ImageBuffer palette)
    {
        // TODO
        return new ImageBuffer[0];
    }

    @Override
    public ImageBuffer[] getRasterBufferOffset(final Media image,
                                               final Media palette,
                                               final Media raster,
                                               final int offsets)
    {
        // TODO
        return new ImageBuffer[0];
    }

    @Override
    public ImageBuffer[] getRasterBufferSmooth(final ImageBuffer image,
                                               final ImageBuffer palette,
                                               final int fh,
                                               final int fv)
    {
        // TODO
        return new ImageBuffer[0];
    }

    @Override
    public ImageBuffer[] getRasterBufferSmooth(final ImageBuffer image, final ImageBuffer palette, final int tileHeight)
    {
        // TODO
        return new ImageBuffer[0];
    }

    @Override
    public ImageBuffer[] getRasterBufferInside(ImageBuffer image, ImageBuffer palette, int th)
    {
        // TODO
        return new ImageBuffer[0];
    }

    @Override
    public void generateTileset(final ImageBuffer[] images, final Media media)
    {
        // TODO
    }
}
