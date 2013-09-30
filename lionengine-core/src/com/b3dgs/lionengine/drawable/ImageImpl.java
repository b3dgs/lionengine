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
package com.b3dgs.lionengine.drawable;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.utility.UtilityImage;

/**
 * Image implementation.
 */
final class ImageImpl
        implements Image
{
    /** Image surface. */
    private final java.awt.Image surface;

    /**
     * Create a new image.
     * 
     * @param media The image media.
     */
    ImageImpl(Media media)
    {
        this(UtilityImage.getBufferedImage(media, false));
    }

    /**
     * Copy an image from a surface (share).
     * 
     * @param surface The surface to share.
     */
    ImageImpl(java.awt.Image surface)
    {
        this.surface = surface;
    }

    /*
     * Renderable
     */

    @Override
    public void render(Graphic g, int x, int y)
    {
        g.drawImage(surface, x, y);
    }

    @Override
    public int getWidth()
    {
        return surface.getWidth(null);
    }

    @Override
    public int getHeight()
    {
        return surface.getHeight(null);
    }

    /*
     * Image
     */

    @Override
    public java.awt.Image getSurface()
    {
        return surface;
    }

    @Override
    public Image instanciate()
    {
        return new ImageImpl(surface);
    }

    /*
     * Object
     */

    @Override
    public boolean equals(Object object)
    {
        if (object == this)
        {
            return true;
        }
        if (object instanceof Image)
        {
            final Image image = (Image) object;

            final boolean sameSurface = image.getSurface() == getSurface();
            final boolean sameWidth = image.getWidth() == getWidth();
            final boolean sameHeight = image.getHeight() == getHeight();

            return sameSurface && sameWidth && sameHeight;
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + (surface == null ? 0 : surface.hashCode());
        return result;
    }
}
