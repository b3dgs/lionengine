/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

/**
 * Describe the properties model.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class PropertiesModel
{
    /** Properties explorer model. */
    public static final PropertiesModel INSTANCE = new PropertiesModel();

    /** Properties tree. */
    private Tree properties;
    /** Copied property data (<code>null</code> if none). */
    private Object copyData;
    /** Copied property text (<code>null</code> if none). */
    private String copyText;

    /**
     * Private constructor.
     */
    protected PropertiesModel()
    {
        // Nothing to do
    }

    /**
     * Set the properties tree.
     * 
     * @param properties The properties tree.
     */
    public void setTree(Tree properties)
    {
        this.properties = properties;
    }

    /**
     * Set the copied element.
     * 
     * @param copyData The copied element.
     */
    public void setCopyData(Object copyData)
    {
        this.copyData = copyData;
    }

    /**
     * Set the copied element.
     * 
     * @param copyText The copied element.
     */
    public void setCopyText(String copyText)
    {
        this.copyText = copyText;
    }

    /**
     * Return the last copied element.
     * 
     * @return The last copied element, <code>null</code> if none.
     */
    public Object getCopyData()
    {
        return copyData;
    }

    /**
     * Return the last copied element.
     * 
     * @return The last copied element, <code>null</code> if none.
     */
    public String getCopyText()
    {
        return copyText;
    }

    /**
     * Get the properties root folder.
     * 
     * @return The properties root folder.
     */
    public Tree getTree()
    {
        return properties;
    }

    /**
     * Get the selected property data.
     * 
     * @return The selected data, <code>null</code> if none.
     */
    public Object getSelectedData()
    {
        for (final TreeItem item : properties.getSelection())
        {
            return item.getData();
        }
        return null;
    }

    /**
     * Check if properties are empty.
     * 
     * @return <code>true</code> if empty, <code>false</code> else.
     */
    public boolean isEmpty()
    {
        return properties.getItems().length == 0;
    }

    /**
     * Check if property node exists.
     * 
     * @param property The property to check.
     * @return <code>true</code> if property defined, <code>false</code> else.
     */
    public boolean hasProperty(String property)
    {
        for (final TreeItem item : properties.getItems())
        {
            if (property.equals(item.getData()))
            {
                return true;
            }
        }
        return false;
    }
}
