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
package com.b3dgs.lionengine.editor.object.project;

import java.io.File;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.editor.project.ProjectModel;

/**
 * Remove an object in the selected folder.
 */
public final class ObjectDeleteHandler
{
    /**
     * Create handler.
     */
    public ObjectDeleteHandler()
    {
        super();
    }

    /**
     * Execute the handler.
     * 
     * @param parent The shell parent.
     */
    @Execute
    public void execute(Shell parent)
    {
        final Media selection = ProjectModel.INSTANCE.getSelection();
        final File file = selection.getFile();
        if (file.isFile())
        {
            if (file.delete())
            {
                MessageDialog.openInformation(parent, Messages.RemoveObjectTitle, Messages.RemoveObjectText + file);
            }
            else
            {
                MessageDialog.openInformation(parent,
                                              com.b3dgs.lionengine.editor.validator.Messages.ErrorTitle,
                                              com.b3dgs.lionengine.editor.validator.Messages.ErrorText + file);
            }
        }
    }
}
