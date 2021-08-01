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
package com.b3dgs.lionengine.game;

import java.util.Optional;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.XmlReader;

/**
 * Represents the surface data.
 * <p>
 * This class is Thread-Safe.
 * </p>
 */
public final class SurfaceConfig
{
    /** Surface node name. */
    public static final String NODE_SURFACE = Constant.XML_PREFIX + "surface";
    /** Image attribute name. */
    public static final String ATT_IMAGE = "image";
    /** Icon attribute name. */
    public static final String ATT_ICON = "icon";
    /** Minimum to string length. */
    private static final int MIN_LENGTH = 31;

    /**
     * Create the surface data from configurer.
     * 
     * @param configurer The configurer reference (must not be <code>null</code>).
     * @return The surface data.
     * @throws LionEngineException If unable to read node.
     */
    public static SurfaceConfig imports(Configurer configurer)
    {
        Check.notNull(configurer);

        return imports(configurer.getRoot());
    }

    /**
     * Create the surface data from node.
     * 
     * @param root The root reference (must not be <code>null</code>).
     * @return The surface data.
     * @throws LionEngineException If unable to read node.
     */
    public static SurfaceConfig imports(XmlReader root)
    {
        Check.notNull(root);

        final XmlReader node = root.getChild(NODE_SURFACE);
        final String surface = node.readString(ATT_IMAGE);
        final String icon = node.readString(null, ATT_ICON);

        return new SurfaceConfig(surface, icon);
    }

    /**
     * Create the surface data from node.
     * 
     * @param config The config reference (must not be <code>null</code>).
     * @return The node data.
     * @throws LionEngineException If unable to write node.
     */
    public static Xml exports(SurfaceConfig config)
    {
        Check.notNull(config);

        final Xml node = new Xml(NODE_SURFACE);
        node.writeString(ATT_IMAGE, config.getImage());
        config.getIcon().ifPresent(icon -> node.writeString(ATT_ICON, icon));

        return node;
    }

    /** The image descriptor. */
    private final String image;
    /** The icon descriptor. */
    private final Optional<String> icon;

    /**
     * Create the surface configuration.
     * 
     * @param image The image file path.
     * @param icon The icon file path (can be <code>null</code>).
     * @throws LionEngineException If invalid argument.
     */
    public SurfaceConfig(String image, String icon)
    {
        super();

        Check.notNull(image);

        this.image = image;
        this.icon = Optional.ofNullable(icon);
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
     * @return The icon descriptor.
     */
    public Optional<String> getIcon()
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
        result = prime * result + image.hashCode();
        result = prime * result + icon.hashCode();
        return result;
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
        return icon.equals(other.icon) && image.equals(other.image);
    }

    @Override
    public String toString()
    {
        return new StringBuilder(MIN_LENGTH).append(getClass().getSimpleName())
                                            .append(" [image=")
                                            .append(image)
                                            .append(", icon=")
                                            .append(icon.orElse(null))
                                            .append("]")
                                            .toString();
    }
}
