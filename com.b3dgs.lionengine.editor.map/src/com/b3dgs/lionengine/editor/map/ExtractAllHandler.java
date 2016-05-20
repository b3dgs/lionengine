/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.editor.map;

import java.util.Collection;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.widgets.Shell;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.drawable.SpriteTiled;
import com.b3dgs.lionengine.editor.map.group.menu.GroupsAssignDialog;
import com.b3dgs.lionengine.editor.map.sheet.extract.SheetsExtractDialog;
import com.b3dgs.lionengine.game.map.TileSheetsConfig;
import com.b3dgs.lionengine.game.map.feature.transition.TransitionsConfig;
import com.b3dgs.lionengine.game.tile.TileGroupsConfig;

/**
 * Export map tile constraints handler.
 */
public final class ExtractAllHandler
{
    /** Element ID. */
    public static final String ID = "menu.map.extract-all";

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

        sheetsExtractDialog.save();
        groupsAssignDialog.save();
        TransitionsConfig.exports(transitions, levels, sheets, groups);
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
     * @param shell The shell reference.
     */
    @Execute
    public void execute(Shell shell)
    {
        final SheetsExtractDialog sheetsExtractDialog = new SheetsExtractDialog(shell);
        sheetsExtractDialog.open();

        final Collection<SpriteTiled> sheets = sheetsExtractDialog.getSheets();
        if (!sheets.isEmpty())
        {
            final Collection<Media> levels = sheetsExtractDialog.getLevelRips();

            final GroupsAssignDialog groupsAssignDialog = new GroupsAssignDialog(shell);
            groupsAssignDialog.setLocation(sheetsExtractDialog.getFolder());
            groupsAssignDialog.load(sheets, levels);
            groupsAssignDialog.open();

            if (!groupsAssignDialog.isCanceled())
            {
                saveAll(sheetsExtractDialog, groupsAssignDialog, levels);
            }
        }
    }
}
