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

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.editor.InputValidator;
import com.b3dgs.lionengine.editor.Tools;
import com.b3dgs.lionengine.editor.project.ProjectModel;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.object.Factory;

/**
 * Add a groups descriptor in the selected folder.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class GroupsAddHandler
{
    /**
     * Create the groups.
     * 
     * @param groups The groups file destination.
     * @throws IOException If error when creating the groups.
     */
    private static void createGroups(File groups) throws IOException
    {
        final File template = Tools.getTemplate(Tools.TEMPLATE_GROUPS);
        final Collection<String> lines = Files.readAllLines(template.toPath(), StandardCharsets.UTF_8);
        final Collection<String> dest = new ArrayList<>();
        for (final String line : lines)
        {
            dest.add(line);
        }
        Files.write(groups.toPath(), dest, StandardCharsets.UTF_8);
        lines.clear();
        dest.clear();
    }

    /**
     * Create handler.
     */
    public GroupsAddHandler()
    {
        // Nothing to do
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
        final InputDialog inputDialog = new InputDialog(parent, Messages.AddGroups_Title, Messages.AddGroups_Text,
                MapTile.DEFAULT_GROUPS_FILE.replace(Constant.DOT + Factory.FILE_DATA_EXTENSION, Constant.EMPTY_STRING),
                new InputValidator(InputValidator.NAME_MATCH,
                        com.b3dgs.lionengine.editor.Messages.InputValidator_Error_Name));
        final int code = inputDialog.open();
        if (code == Window.OK)
        {
            final String name = inputDialog.getValue();
            final File groups = new File(selection.getFile(), name + "." + Factory.FILE_DATA_EXTENSION);

            if (groups.exists())
            {
                MessageDialog.openError(parent, Messages.AddGroups_Error_Title, Messages.AddGroups_Error_Text);
                execute(parent);
            }
            else
            {
                try
                {
                    createGroups(groups);
                }
                catch (final IOException exception)
                {
                    throw new LionEngineException(exception);
                }
            }
        }
    }
}
