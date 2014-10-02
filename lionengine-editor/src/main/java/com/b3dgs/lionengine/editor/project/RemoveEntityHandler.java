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

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.editor.UtilEclipse;

/**
 * Remove an entity in the selected folder.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class RemoveEntityHandler
{
    /**
     * Execute the handler.
     * 
     * @param partService The part service reference.
     * @param parent The shell parent.
     */
    @Execute
    public void execute(EPartService partService, Shell parent)
    {
        final Media selection = ProjectsModel.INSTANCE.getSelection();
        final File file = selection.getFile();
        if (file.isFile())
        {
            if (file.delete())
            {
                final MessageBox messageBox = new MessageBox(parent, SWT.ICON_INFORMATION);
                messageBox.setText(Messages.RemoveEntity_Title);
                messageBox.setMessage(Messages.RemoveEntity_Text + file);
                messageBox.open();
                final ProjectsPart part = UtilEclipse.getPart(partService, ProjectsPart.ID, ProjectsPart.class);
                part.removeTreeItem(selection);
            }
            else
            {
                final MessageBox messageBox = new MessageBox(parent, SWT.ICON_ERROR);
                messageBox.setText(Messages.RemoveEntity_Error_Title);
                messageBox.setMessage(Messages.RemoveEntity_Error_Text + file);
                messageBox.open();
            }
        }
    }
}
