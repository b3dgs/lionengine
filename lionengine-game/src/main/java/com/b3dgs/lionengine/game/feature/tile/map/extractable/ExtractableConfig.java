/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.tile.map.extractable;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.XmlReader;
import com.b3dgs.lionengine.game.Configurer;

/**
 * Represents the {@link Extractable} data.
 * 
 * @param type The resource type.
 * @param quantity The resource quantity.
 */
public record ExtractableConfig(String type, int quantity)
{
    /** Extractable node name. */
    public static final String NODE_EXTRACTABLE = Constant.XML_PREFIX + "extractable";
    /** Quantity attribute name. */
    public static final String ATT_QUANTITY = "quantity";
    /** Resource type attribute name. */
    public static final String ATT_TYPE = "type";

    /**
     * Imports the config from configurer.
     * 
     * @param configurer The configurer reference (must not be <code>null</code>).
     * @return The extractable data.
     * @throws LionEngineException If unable to read node.
     */
    public static ExtractableConfig imports(Configurer configurer)
    {
        Check.notNull(configurer);

        return imports(configurer.getRoot());
    }

    /**
     * Imports the config from node.
     * 
     * @param root The root reference (must not be <code>null</code>).
     * @return The extractable data.
     * @throws LionEngineException If unable to read node.
     */
    public static ExtractableConfig imports(XmlReader root)
    {
        Check.notNull(root);

        final XmlReader node = root.getChild(NODE_EXTRACTABLE);
        final String type = node.getStringDefault(Constant.EMPTY_STRING, ATT_TYPE);
        final int quantity = node.getInteger(0, ATT_QUANTITY);

        return new ExtractableConfig(type, quantity);
    }

    /**
     * Exports the node from config.
     * 
     * @param config The config reference (must not be <code>null</code>).
     * @return The extractable node.
     * @throws LionEngineException If unable to read node.
     */
    public static Xml exports(ExtractableConfig config)
    {
        Check.notNull(config);

        final Xml node = new Xml(NODE_EXTRACTABLE);
        node.writeString(ATT_TYPE, config.getType());
        node.writeInteger(ATT_QUANTITY, config.getQuantity());

        return node;
    }

    /**
     * Get the resource type.
     * 
     * @return The resource type.
     */
    public String getType()
    {
        return type;
    }

    /**
     * Get the resource quantity.
     * 
     * @return The resource quantity.
     */
    public int getQuantity()
    {
        return quantity;
    }
}
