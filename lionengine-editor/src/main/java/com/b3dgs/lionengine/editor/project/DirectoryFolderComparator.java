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
package com.b3dgs.lionengine.editor.project;

import java.io.File;
import java.util.Comparator;

/**
 * Comparator that prior directory first.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class DirectoryFolderComparator
        implements Comparator<File>
{
    @Override
    public int compare(File file1, File file2)
    {
        if (file1.isFile() && file2.isFile() || file1.isDirectory() && file2.isDirectory())
        {
            return file1.compareTo(file2);
        }
        if (file1.isFile() && file2.isDirectory())
        {
            return 1;
        }
        else if (file1.isDirectory() && file2.isFile())
        {
            return -1;
        }
        return 0;
    }
}
