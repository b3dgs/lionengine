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
package com.b3dgs.lionengine.game.feature;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.XmlReader;
import com.b3dgs.lionengine.game.Configurer;

/**
 * Represents the {@link Layerable} data.
 * <p>
 * This class is Thread-Safe.
 * </p>
 */
public final class LayerableConfig
{
    /** Frames node name. */
    public static final String NODE_LAYERABLE = Constant.XML_PREFIX + "layerable";
    /** Refresh layer node name. */
    public static final String ATT_REFRESH = "layerRefresh";
    /** Display layer node name. */
    public static final String ATT_DISPLAY = "layerDisplay";
    /** Minimum to string length. */
    private static final int MIN_LENGTH = 48;

    /**
     * Imports the layerable config from configurer.
     * 
     * @param configurer The configurer reference (must not be <code>null</code>).
     * @return The frames data.
     * @throws LionEngineException If unable to read node or invalid integer.
     */
    public static LayerableConfig imports(Configurer configurer)
    {
        Check.notNull(configurer);

        return imports(configurer.getRoot());
    }

    /**
     * Imports the layerable config from node.
     * 
     * @param root The root reference (must not be <code>null</code>).
     * @return The layerable data.
     * @throws LionEngineException If unable to read node or invalid integer.
     */
    public static LayerableConfig imports(XmlReader root)
    {
        Check.notNull(root);

        final XmlReader node = root.getChild(NODE_LAYERABLE);
        final int layerRefresh = node.getInteger(ATT_REFRESH);
        final int layerDisplay = node.getInteger(ATT_DISPLAY);

        return new LayerableConfig(layerRefresh, layerDisplay);
    }

    /**
     * Exports the layerable node from config.
     * 
     * @param config The config reference (must not be <code>null</code>).
     * @return The layerable node.
     * @throws LionEngineException If unable to read node or invalid integer.
     */
    public static Xml exports(LayerableConfig config)
    {
        Check.notNull(config);

        final Xml node = new Xml(NODE_LAYERABLE);
        node.writeInteger(ATT_REFRESH, config.getLayerRefresh());
        node.writeInteger(ATT_DISPLAY, config.getLayerDisplay());

        return node;
    }

    /** The layer refresh. */
    private final int layerRefresh;
    /** The layer display. */
    private final int layerDisplay;

    /**
     * Create the layerable configuration.
     * 
     * @param layerRefresh The layer refresh.
     * @param layerDisplay The layer display.
     */
    public LayerableConfig(int layerRefresh, int layerDisplay)
    {
        super();

        this.layerRefresh = layerRefresh;
        this.layerDisplay = layerDisplay;
    }

    /**
     * Get the layer refresh.
     * 
     * @return The layer refresh.
     */
    public int getLayerRefresh()
    {
        return layerRefresh;
    }

    /**
     * Get the layer display.
     * 
     * @return The layer display.
     */
    public int getLayerDisplay()
    {
        return layerDisplay;
    }

    /*
     * Object
     */

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + layerRefresh;
        result = prime * result + layerDisplay;
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
        final LayerableConfig other = (LayerableConfig) object;
        return layerRefresh == other.layerRefresh && layerDisplay == other.layerDisplay;
    }

    @Override
    public String toString()
    {
        return new StringBuilder(MIN_LENGTH).append(getClass().getSimpleName())
                                            .append(" [layerRefresh=")
                                            .append(layerRefresh)
                                            .append(", layerDisplay=")
                                            .append(layerDisplay)
                                            .append("]")
                                            .toString();
    }
}
