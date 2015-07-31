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
package com.b3dgs.lionengine.editor.project.handler;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.widgets.Shell;

import com.b3dgs.lionengine.core.swt.UtilityMedia;
import com.b3dgs.lionengine.editor.project.Project;
import com.b3dgs.lionengine.editor.project.ProjectModel;
import com.b3dgs.lionengine.editor.project.ProjectPart;
import com.b3dgs.lionengine.editor.project.dialog.ProjectImportDialog;
import com.b3dgs.lionengine.editor.utility.UtilPart;
import com.b3dgs.lionengine.editor.world.WorldModel;
import com.b3dgs.lionengine.editor.world.WorldPart;
import com.b3dgs.lionengine.editor.world.handler.SetPointerCollisionHandler;
import com.b3dgs.lionengine.editor.world.handler.SetShowCollisionsHandler;
import com.b3dgs.lionengine.game.object.Factory;

/**
 * Import project handler implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class ProjectImportHandler
{
    /**
     * Import the project and update the view.
     * 
     * @param project The project to import.
     */
    public static void importProject(Project project)
    {
        final Factory factory = WorldModel.INSTANCE.getFactory();
        factory.setClassLoader(project.getClassLoader());

        final WorldPart worldPart = UtilPart.getPart(WorldPart.ID, WorldPart.class);
        worldPart.setToolBarEnabled(true);
        worldPart.setToolItemEnabled(SetShowCollisionsHandler.ID, false);
        worldPart.setToolItemEnabled(SetPointerCollisionHandler.ID, false);

        UtilityMedia.setResourcesDirectory(project.getResourcesPath().getPath());

        final ProjectPart projectPart = UtilPart.getPart(ProjectPart.ID, ProjectPart.class);
        ProjectModel.INSTANCE.setRoot(project.getPath());
        projectPart.setInput(project);
    }

    /**
     * Create handler.
     */
    public ProjectImportHandler()
    {
        // Nothing to do
    }

    /**
     * Execute the handler.
     * 
     * @param shell The shell reference.
     */
    @Execute
    public void execute(Shell shell)
    {
        final ProjectImportDialog importProjectDialog = new ProjectImportDialog(shell);
        importProjectDialog.open();

        final Project project = importProjectDialog.getProject();
        if (project != null)
        {
            importProject(project);
        }
    }
}
