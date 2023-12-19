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
 * 
 * @param image The image file path.
 * @param icon The icon file path.
 */
public record SurfaceConfig(String image, Optional<String> icon)
{
    /** Surface node name. */
    public static final String NODE_SURFACE = Constant.XML_PREFIX + "surface";
    /** Image attribute name. */
    public static final String ATT_IMAGE = "image";
    /** Icon attribute name. */
    public static final String ATT_ICON = "icon";

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
        final String surface = node.getString(ATT_IMAGE);
        final Optional<String> icon = node.getStringOptional(ATT_ICON);

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

    /**
     * Create the surface configuration.
     * 
     * @param image The image file path.
     * @param icon The icon file path.
     * @throws LionEngineException If invalid argument.
     */
    public SurfaceConfig
    {
        Check.notNull(image);
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
}
