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
package com.b3dgs.lionengine.editor.project;

import java.io.File;

import com.b3dgs.lionengine.core.Media;

/**
 * Describe the project model.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class ProjectsModel
{
    /** Project model. */
    public static final ProjectsModel INSTANCE = new ProjectsModel();

    /** Project root (resources folder). */
    private File root;
    /** Last resource selected. */
    private Media selection;

    /**
     * Private constructor.
     */
    private ProjectsModel()
    {
        // Nothing to do
    }

    /**
     * Set the main resources folder.
     * 
     * @param root The main resources folder.
     */
    public void setRoot(File root)
    {
        this.root = root;
    }

    /**
     * Get the resources root folder.
     * 
     * @return The resources root folder.
     */
    public File getRoot()
    {
        return root;
    }

    /**
     * Set the selected resource.
     * 
     * @param selection The selected resource.
     */
    public void setSelection(Media selection)
    {
        this.selection = selection;
    }

    /**
     * Get the selected resource.
     * 
     * @return The selected resource, <code>null</code> if none.
     */
    public Media getSelection()
    {
        return selection;
    }
}
