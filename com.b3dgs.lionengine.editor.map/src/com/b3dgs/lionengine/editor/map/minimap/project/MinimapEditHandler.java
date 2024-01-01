/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.editor.map.minimap.project;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.widgets.Shell;

import com.b3dgs.lionengine.editor.map.minimap.editor.MinimapEditDialog;
import com.b3dgs.lionengine.editor.project.ProjectModel;

/**
 * Edit minimap handler implementation.
 */
public final class MinimapEditHandler
{
    /**
     * Execute the handler.
     * 
     * @param parent The shell parent.
     */
    public static void executeHandler(Shell parent)
    {
        final MinimapEditDialog dialog = new MinimapEditDialog(parent, ProjectModel.INSTANCE.getSelection().getPath());
        dialog.open();
    }

    /**
     * Create handler.
     */
    public MinimapEditHandler()
    {
        super();
    }

    /**
     * Execute the handler.
     * 
     * @param shell The shell reference.
     */
    @Execute
    public void execute(Shell shell)
    {
        executeHandler(shell);
    }
}
