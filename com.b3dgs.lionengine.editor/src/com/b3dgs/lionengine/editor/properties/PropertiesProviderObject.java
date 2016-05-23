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
package com.b3dgs.lionengine.editor.properties;

import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.b3dgs.lionengine.editor.Activator;
import com.b3dgs.lionengine.game.object.Configurer;

/**
 * Properties provider interface. Classes which implement this interface must provide the default public constructor.
 */
public interface PropertiesProviderObject
{
    /** Extension ID. */
    String EXTENSION_ID = Activator.PLUGIN_ID + ".propertiesObject";
    /** Properties extension. */
    String EXTENSION_PROPERTIES = "class";
    /** Properties folder. */
    String FOLDER = "properties";

    /**
     * Set the properties input.
     * 
     * @param properties The properties tree reference.
     * @param configurer The configurer reference (<code>null</code> if not a valid item).
     */
    void setInput(Tree properties, Configurer configurer);

    /**
     * Update the properties.
     * 
     * @param item The item reference.
     * @param configurer The configurer reference.
     * @return <code>true</code> if updated, <code>false</code> else.
     */
    boolean updateProperties(TreeItem item, Configurer configurer);
}
