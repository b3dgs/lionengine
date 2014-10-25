/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.core.swt;

import com.b3dgs.lionengine.core.FactoryMedia;
import com.b3dgs.lionengine.core.Media;

/**
 * Media factory implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
final class FactoryMediaSwt
        implements FactoryMedia
{
    /** Path separator. */
    private String separator;

    /**
     * Internal constructor.
     */
    FactoryMediaSwt()
    {
        // Nothing to do
    }

    /*
     * FactoryMedia
     */

    @Override
    public Media create(String path)
    {
        return new MediaSwt(path);
    }

    @Override
    public Media create(String... path)
    {
        final StringBuilder fullPath = new StringBuilder();
        for (int i = 0; i < path.length; i++)
        {
            if (path[i] != null)
            {
                fullPath.append(path[i]);
            }
            if (i < path.length - 1)
            {
                fullPath.append(getSeparator());
            }
        }
        return new MediaSwt(fullPath.toString());
    }

    @Override
    public String getSeparator()
    {
        return separator;
    }

    @Override
    public void setSeparator(String separator)
    {
        this.separator = separator;
    }
}
