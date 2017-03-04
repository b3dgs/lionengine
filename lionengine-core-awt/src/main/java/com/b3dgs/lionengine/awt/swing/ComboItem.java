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
package com.b3dgs.lionengine.awt.swing;

/**
 * Combo item class.
 */
public final class ComboItem implements CanEnable
{
    /**
     * Convert an array of objects to an array of combo item.
     * 
     * @param objects The objects array.
     * @return The items.
     */
    public static ComboItem[] get(Object[] objects)
    {
        final ComboItem[] items = new ComboItem[objects.length];
        for (int i = 0; i < items.length; i++)
        {
            items[i] = new ComboItem(objects[i]);
        }
        return items;
    }

    /** Object reference. */
    private final Object obj;
    /** Enabled flag. */
    private boolean isEnable;

    /**
     * Create a combo item.
     * 
     * @param obj The object reference.
     * @param isEnable The enabled flag.
     */
    public ComboItem(Object obj, boolean isEnable)
    {
        this.obj = obj;
        this.isEnable = isEnable;
    }

    /**
     * Create a combo item enabled by default.
     * 
     * @param obj The object reference.
     */
    public ComboItem(Object obj)
    {
        this(obj, true);
    }

    /**
     * Get the object.
     * 
     * @return The object.
     */
    public Object getObject()
    {
        return obj;
    }

    /*
     * CanEnable
     */

    @Override
    public boolean isEnabled()
    {
        return isEnable;
    }

    @Override
    public void setEnabled(boolean isEnable)
    {
        this.isEnable = isEnable;
    }

    @Override
    public String toString()
    {
        return obj.toString();
    }
}
