/*
 * Copyright (C) 2013 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.tools;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import com.b3dgs.lionengine.UtilityFile;

/**
 * Level rip filter.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
final class LevelRipFilter
        extends FileFilter
{
    /** File extension. */
    private final String extension;
    /** File description. */
    private final String description;

    /**
     * Constructor.
     * 
     * @param extension Level file extension.
     * @param description Level file description.
     */
    LevelRipFilter(String extension, String description)
    {
        this.extension = extension;
        this.description = description;
    }

    @Override
    public boolean accept(File f)
    {
        if (f.isDirectory())
        {
            return true;
        }

        final String ext = UtilityFile.getExtension(f);
        if (ext != null)
        {
            if (extension.equals(ext))
            {
                return true;
            }
            return false;
        }

        return false;
    }

    @Override
    public String getDescription()
    {
        StringBuilder buf = new StringBuilder(description);
        buf = buf.append(" (*.").append(extension).append(")");
        return buf.toString();
    }
}
