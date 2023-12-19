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
package com.b3dgs.lionengine.editor.properties;

import org.eclipse.swt.widgets.Tree;

import com.b3dgs.lionengine.editor.Activator;
import com.b3dgs.lionengine.game.feature.tile.Tile;

/**
 * Properties provider interface. Classes which implement this interface must provide the default public constructor.
 */
public interface PropertiesProviderTile
{
    /** Extension ID. */
    String EXTENSION_ID = Activator.PLUGIN_ID + ".propertiesTile";
    /** Properties extension. */
    String EXTENSION_PROPERTIES = "class";
    /** Properties folder. */
    String FOLDER = "properties";

    /**
     * Set the properties input.
     * 
     * @param properties The properties tree reference.
     * @param tile The tile reference.
     */
    void setInput(Tree properties, Tile tile);
}
