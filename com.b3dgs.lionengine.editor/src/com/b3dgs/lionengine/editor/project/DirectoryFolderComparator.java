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
package com.b3dgs.lionengine.editor.project;

import java.io.File;
import java.io.Serializable;
import java.util.Comparator;

/**
 * Comparator that prior directory first.
 */
public final class DirectoryFolderComparator implements Comparator<File>, Serializable
{
    private static final long serialVersionUID = 1;

    /**
     * Create comparator.
     */
    public DirectoryFolderComparator()
    {
        super();
    }

    /*
     * Comparator
     */

    @Override
    public int compare(File file1, File file2)
    {
        final int result;
        if (file1.isFile() && file2.isFile() || file1.isDirectory() && file2.isDirectory())
        {
            result = file1.compareTo(file2);
        }
        else if (file1.isFile() && file2.isDirectory())
        {
            result = 1;
        }
        else if (file1.isDirectory() && file2.isFile())
        {
            result = -1;
        }
        else
        {
            result = 0;
        }
        return result;
    }
}
