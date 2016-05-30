/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.stream.Xml;
import com.b3dgs.lionengine.stream.XmlNode;

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
    public static SurfaceConfig imports(XmlNode root)
    {
        final XmlNode node = root.getChild(SurfaceConfig.NODE_SURFACE);
        final String surface = node.readString(SurfaceConfig.ATT_IMAGE);

        return new SurfaceConfig(surface, SurfaceConfig.getSurfaceIcon(root));
    }

    /**
     * Create the surface data from node.
     * 
     * @param config The config reference.
     * @return The node data.
     * @throws LionEngineException If unable to write node.
     */
    public static XmlNode exports(SurfaceConfig config)
    {
        final XmlNode node = Xml.create(SurfaceConfig.NODE_SURFACE);
        node.writeString(SurfaceConfig.ATT_IMAGE, config.getImage());
        if (config.getIcon() != null)
        {
            node.writeString(SurfaceConfig.ATT_ICON, config.getIcon());
        }

        return node;
    }

    /**
     * Get the surface icon if existing.
     * 
     * @param root The root reference.
     * @return The surface icon, <code>null</code> if none.
     */
    private static String getSurfaceIcon(XmlNode root)
    {
        final XmlNode node = root.getChild(SurfaceConfig.NODE_SURFACE);
        if (node.hasAttribute(SurfaceConfig.ATT_ICON))
        {
            return node.readString(SurfaceConfig.ATT_ICON);
        }
        return null;
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
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (!(obj instanceof SurfaceConfig))
        {
            return false;
        }
        final SurfaceConfig other = (SurfaceConfig) obj;
        final boolean sameIcon = other.getIcon() == null && getIcon() == null
                                 || other.getIcon() != null && getIcon() != null && other.getIcon().equals(getIcon());
        return other.getImage().equals(getImage()) && sameIcon;
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
