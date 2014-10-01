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
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.editor.InputValidator;
import com.b3dgs.lionengine.editor.Tools;
import com.b3dgs.lionengine.game.FactoryObjectGame;

/**
 * Add an entity in the selected folder.
 * 
 * @author Pierre-Alexandre
 */
public class AddEntityHandler
{
    /** Default new entity name. */
    private static final String DEFAULT_NEW_ENTITY_NAME = "entity";

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
        final InputDialog inputDialog = new InputDialog(parent, Messages.AddEntity_Title, Messages.AddEntity_Text,
                AddEntityHandler.DEFAULT_NEW_ENTITY_NAME, new InputValidator(InputValidator.NAME_MATCH,
                        com.b3dgs.lionengine.editor.Messages.InputValidator_Error_Name));
        final int code = inputDialog.open();
        if (code == Window.OK)
        {
            final String name = inputDialog.getValue();
            final File entity = new File(selection.getFile(), name + "." + FactoryObjectGame.FILE_DATA_EXTENSION);
            final File template = Tools.getTemplate(Tools.TEMPLATE_ENTITY);
            try
            {
                Files.copy(template.toPath(), entity.toPath());
            }
            catch (final FileAlreadyExistsException exception)
            {
                MessageDialog.openError(parent, Messages.AddEntity_Error_Title, Messages.AddEntity_Error_Text);
                execute(partService, parent);
            }
            catch (final IOException exception)
            {
                throw new LionEngineException(exception);
            }
        }
    }
}
