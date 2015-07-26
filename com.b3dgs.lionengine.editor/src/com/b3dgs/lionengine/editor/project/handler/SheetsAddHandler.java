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
import org.eclipse.e4.ui.workbench.modeling.EPartService;
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
 * Add a sheets descriptor in the selected folder.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class SheetsAddHandler
{
    /** Default tile size. */
    private static final String DEFAULT_TILE_SIZE = String.valueOf(16);

    /**
     * Create the sheets.
     * 
     * @param sheets The sheets file destination.
     * @throws IOException If error when creating the sheets.
     */
    private static void createSheets(File sheets) throws IOException
    {
        final File template = Tools.getTemplate(Tools.TEMPLATE_SHEETS);
        final Collection<String> lines = Files.readAllLines(template.toPath(), StandardCharsets.UTF_8);
        final Collection<String> dest = new ArrayList<>();
        for (final String line : lines)
        {
            if (line.contains(Tools.TEMPLATE_SHEETS_WIDTH) && line.contains(Tools.TEMPLATE_SHEETS_HEIGHT))
            {
                dest.add(line.replace(Tools.TEMPLATE_SHEETS_WIDTH, DEFAULT_TILE_SIZE)
                             .replace(Tools.TEMPLATE_SHEETS_HEIGHT, DEFAULT_TILE_SIZE));
            }
            else
            {
                dest.add(line);
            }
        }
        Files.write(sheets.toPath(), dest, StandardCharsets.UTF_8);
        lines.clear();
        dest.clear();
    }

    /**
     * Create handler.
     */
    public SheetsAddHandler()
    {
        // Nothing to do
    }

    /**
     * Execute the handler.
     * 
     * @param partService The part service reference.
     * @param parent The shell parent.
     */
    @Execute
    public void execute(EPartService partService, Shell parent)
    {
        final Media selection = ProjectModel.INSTANCE.getSelection();
        final InputDialog inputDialog = new InputDialog(parent, Messages.AddSheets_Title, Messages.AddSheets_Text,
                MapTile.DEFAULT_SHEETS_FILE.replace(Constant.DOT + Factory.FILE_DATA_EXTENSION, Constant.EMPTY_STRING),
                new InputValidator(InputValidator.NAME_MATCH,
                        com.b3dgs.lionengine.editor.Messages.InputValidator_Error_Name));
        final int code = inputDialog.open();
        if (code == Window.OK)
        {
            final String name = inputDialog.getValue();
            final File sheets = new File(selection.getFile(), name + "." + Factory.FILE_DATA_EXTENSION);

            if (sheets.exists())
            {
                MessageDialog.openError(parent, Messages.AddSheets_Error_Title, Messages.AddSheets_Error_Text);
                execute(partService, parent);
            }
            else
            {
                try
                {
                    createSheets(sheets);
                }
                catch (final IOException exception)
                {
                    throw new LionEngineException(exception);
                }
            }
        }
    }
}
