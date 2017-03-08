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
package com.b3dgs.lionengine.game;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.io.Xml;

/**
 * Represents the surface data from a configurer.
 */
public final class SurfaceConfig
{
    /** Surface node name. */
    public static final String NODE_SURFACE = Constant.XML_PREFIX + "surface";
    /** Surface image node. */
    public static final String ATT_IMAGE = "image";
    /** Surface icon node. */
    public static final String ATT_ICON = "icon";

    /**
     * Create the surface data from configurer.
     * 
     * @param configurer The configurer reference.
     * @return The surface data.
     * @throws LionEngineException If unable to read node.
     */
    public static SurfaceConfig imports(Configurer configurer)
    {
        return imports(configurer.getRoot());
    }

    /**
     * Create the surface data from node.
     * 
     * @param root The root reference.
     * @return The surface data.
     * @throws LionEngineException If unable to read node.
     */
    public static SurfaceConfig imports(Xml root)
    {
        final Xml node = root.getChild(NODE_SURFACE);
        final String surface = node.readString(ATT_IMAGE);
        final String icon = node.readString(null, ATT_ICON);

        return new SurfaceConfig(surface, icon);
    }

    /**
     * Create the surface data from node.
     * 
     * @param config The config reference.
     * @return The node data.
     * @throws LionEngineException If unable to write node.
     */
    public static Xml exports(SurfaceConfig config)
    {
        final Xml node = new Xml(NODE_SURFACE);
        node.writeString(ATT_IMAGE, config.getImage());
        if (config.getIcon() != null)
        {
            node.writeString(ATT_ICON, config.getIcon());
        }

        return node;
    }

    /** The image descriptor. */
    private final String image;
    /** The icon descriptor (can be <code>null</code>). */
    private final String icon;

    /**
     * Create the surface configuration.
     * 
     * @param image The image file path.
     * @param icon The icon file path (can be <code>null</code>).
     */
    public SurfaceConfig(String image, String icon)
    {
        Check.notNull(image);

        this.image = image;
        this.icon = icon;
    }

    /**
     * Get the image descriptor.
     * 
     * @return The image descriptor.
     */
    public String getImage()
    {
        return image;
    }

    /**
     * Get the icon descriptor.
     * 
     * @return The icon descriptor (<code>null</code> if none).
     */
    public String getIcon()
    {
        return icon;
    }

    /*
     * Object
     */

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        if (icon == null)
        {
            result = prime * result;
        }
        else
        {
            result = prime * result + icon.hashCode();
        }
        return prime * result + image.hashCode();
    }

    @Override
    public boolean equals(Object object)
    {
        if (this == object)
        {
            return true;
        }
        if (object == null || object.getClass() != getClass())
        {
            return false;
        }
        final SurfaceConfig other = (SurfaceConfig) object;
        final boolean sameIcon = other.getIcon() == null && getIcon() == null
                                 || other.getIcon() != null && getIcon() != null && other.getIcon().equals(getIcon());
        return sameIcon && other.getImage().equals(getImage());
    }

    @Override
    public String toString()
    {
        return new StringBuilder().append(getClass().getSimpleName())
                                  .append(" [image=")
                                  .append(image)
                                  .append(", icon=")
                                  .append(icon)
                                  .append("]")
                                  .toString();
    }
}
