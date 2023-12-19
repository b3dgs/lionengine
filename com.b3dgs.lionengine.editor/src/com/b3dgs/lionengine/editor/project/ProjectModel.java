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

import com.b3dgs.lionengine.Media;

/**
 * Describe the project model.
 */
public final class ProjectModel
{
    /** Project model. */
    public static final ProjectModel INSTANCE = new ProjectModel();

    /** Active project. */
    private Project project;
    /** Last resource selected. */
    private Media selection;

    /**
     * Private constructor.
     */
    private ProjectModel()
    {
        super();
    }

    /**
     * Set the active project.
     * 
     * @param project The active project.
     */
    public void setProject(Project project)
    {
        this.project = project;
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
     * Get the active project.
     * 
     * @return The active project.
     */
    public Project getProject()
    {
        return project;
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
