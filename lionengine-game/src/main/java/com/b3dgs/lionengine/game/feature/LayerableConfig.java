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
package com.b3dgs.lionengine.game.feature;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.io.Xml;

/**
 * Represents the {@link Layerable} data from a configurer node.
 */
public final class LayerableConfig
{
    /** Frames node name. */
    public static final String NODE_LAYERABLE = Constant.XML_PREFIX + "layerable";
    /** Refresh layer node name. */
    public static final String ATT_REFRESH = "layerRefresh";
    /** Display layer node name. */
    public static final String ATT_DISPLAY = "layerDisplay";

    /**
     * Imports the layerable config from configurer.
     * 
     * @param configurer The configurer reference.
     * @return The frames data.
     * @throws LionEngineException If unable to read node or invalid integer.
     */
    public static LayerableConfig imports(Configurer configurer)
    {
        return imports(configurer.getRoot());
    }

    /**
     * Imports the layerable config from node.
     * 
     * @param root The root reference.
     * @return The layerable data.
     * @throws LionEngineException If unable to read node or invalid integer.
     */
    public static LayerableConfig imports(Xml root)
    {
        final Xml node = root.getChild(NODE_LAYERABLE);
        final int layerRefresh = node.readInteger(ATT_REFRESH);
        final int layerDisplay = node.readInteger(ATT_DISPLAY);

        return new LayerableConfig(layerRefresh, layerDisplay);
    }

    /**
     * Exports the layerable node from config.
     * 
     * @param config The config reference.
     * @return The layerable node.
     * @throws LionEngineException If unable to read node or invalid integer.
     */
    public static Xml exports(LayerableConfig config)
    {
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
        return other.layerRefresh == layerRefresh && other.layerDisplay == layerDisplay;
    }

    @Override
    public String toString()
    {
        return new StringBuilder().append(getClass().getSimpleName())
                                  .append(" [layerRefresh=")
                                  .append(layerRefresh)
                                  .append(", layerDisplay=")
                                  .append(layerDisplay)
                                  .append("]")
                                  .toString();
    }
}
