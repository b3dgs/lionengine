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
package com.b3dgs.lionengine.editor.map;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.editor.map.group.menu.GroupsAssignDialog;
import com.b3dgs.lionengine.editor.map.sheet.extract.SheetsExtractDialog;
import com.b3dgs.lionengine.editor.project.ProjectModel;
import com.b3dgs.lionengine.game.feature.tile.TileGroupsConfig;
import com.b3dgs.lionengine.game.feature.tile.map.TileSheetsConfig;
import com.b3dgs.lionengine.game.feature.tile.map.transition.TransitionsConfig;
import com.b3dgs.lionengine.graphic.drawable.SpriteTiled;

/**
 * Export map tile constraints handler.
 */
public final class ExtractAllHandler
{
    /** Element ID. */
    public static final String ID = "menu.map.extract-all";
    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ExtractAllHandler.class);

    /**
     * Save all level data.
     * 
     * @param sheetsExtractDialog The sheets dialog.
     * @param groupsAssignDialog The groups dialog.
     * @param levels The level rip medias.
     */
    private static void saveAll(SheetsExtractDialog sheetsExtractDialog,
                                GroupsAssignDialog groupsAssignDialog,
                                Collection<Media> levels)
    {
        final String folder = sheetsExtractDialog.getFolder();

        final Media sheets = Medias.create(folder, TileSheetsConfig.FILENAME);
        final Media groups = Medias.create(folder, TileGroupsConfig.FILENAME);
        final Media transitions = Medias.create(folder, TransitionsConfig.FILENAME);
        try
        {
            final File root = new File(ProjectModel.INSTANCE.getProject().getResourcesPath(), folder);
            if (!root.exists() && !root.mkdirs())
            {
                throw new IOException("Unable to create folders: " + root);
            }
            new File(root, TileSheetsConfig.FILENAME).createNewFile();
            new File(root, TileGroupsConfig.FILENAME).createNewFile();
            new File(root, TransitionsConfig.FILENAME).createNewFile();

            sheetsExtractDialog.save();
            groupsAssignDialog.save();
            TransitionsConfig.exports(transitions, levels, sheets, groups);
        }
        catch (final IOException exception)
        {
            LOGGER.error("saveAll error", exception);
        }
    }

    /**
     * Create handler.
     */
    public ExtractAllHandler()
    {
        super();
    }

    /**
     * Execute the handler.
     * 
     * @param partService The part service reference.
     * @param shell The shell reference.
     */
    @Execute
    public void execute(EPartService partService, Shell shell)
    {
        final SheetsExtractDialog sheetsExtractDialog = new SheetsExtractDialog(shell);
        sheetsExtractDialog.open();

        final List<SpriteTiled> sheets = sheetsExtractDialog.getSheets();
        if (!sheets.isEmpty())
        {
            final Collection<Media> levels = sheetsExtractDialog.getLevelRips();

            final GroupsAssignDialog groupsAssignDialog = new GroupsAssignDialog(partService, shell);
            groupsAssignDialog.setLocation(sheetsExtractDialog.getFolder());
            groupsAssignDialog.load(sheets, levels);
            groupsAssignDialog.open();

            if (!groupsAssignDialog.isCanceled())
            {
                saveAll(sheetsExtractDialog, groupsAssignDialog, levels);
            }
            sheets.clear();
        }
    }
}
