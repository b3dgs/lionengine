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
package com.b3dgs.lionengine.editor.dialog.map;

import java.util.Collection;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.widgets.Shell;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.drawable.SpriteTiled;
import com.b3dgs.lionengine.editor.dialog.map.groups.GroupsEditDialog;
import com.b3dgs.lionengine.editor.dialog.map.sheets.extract.SheetsExtractDialog;
import com.b3dgs.lionengine.game.map.ConstraintsExtractor;
import com.b3dgs.lionengine.game.map.TileSheetsConfig;
import com.b3dgs.lionengine.game.map.TransitionsExtractor;
import com.b3dgs.lionengine.game.tile.TileConstraintsConfig;
import com.b3dgs.lionengine.game.tile.TileGroupsConfig;
import com.b3dgs.lionengine.game.tile.TileTransitionsConfig;

/**
 * Export map tile constraints handler.
 */
public final class ExtractAllHandler
{
    /** Element ID. */
    public static final String ID = "menu.map.extract-all";

    /**
     * Create handler.
     */
    public ExtractAllHandler()
    {
        // Nothing to do
    }

    /**
     * Save all level data.
     * 
     * @param sheetsExtractDialog The sheets dialog.
     * @param groupsEditDialog The groups dialog.
     * @param levels The level rip medias.
     */
    private void saveAll(SheetsExtractDialog sheetsExtractDialog, GroupsEditDialog groupsEditDialog, Media[] levels)
    {
        final String folder = sheetsExtractDialog.getFolder();

        final Media sheets = Medias.create(folder, TileSheetsConfig.FILENAME);
        final Media groups = Medias.create(folder, TileGroupsConfig.FILENAME);
        final Media constraints = Medias.create(folder, TileConstraintsConfig.FILENAME);
        final Media transitions = Medias.create(folder, TileTransitionsConfig.FILENAME);

        sheetsExtractDialog.save();
        groupsEditDialog.save();
        TileConstraintsConfig.export(constraints, ConstraintsExtractor.getConstraints(levels, sheets));
        TileTransitionsConfig.exports(transitions, TransitionsExtractor.getTransitions(levels, sheets, groups));
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
            final TileSheetsConfig sheetsConfig = sheetsExtractDialog.getConfig();
            final int tw = sheetsConfig.getTileWidth();
            final int th = sheetsConfig.getTileHeight();
            final Media[] levels = sheetsExtractDialog.getLevelRips();

            final GroupsEditDialog groupsEditDialog = new GroupsEditDialog(shell);
            groupsEditDialog.setLocation(sheetsExtractDialog.getFolder());
            groupsEditDialog.load(tw, th, sheets, levels);
            groupsEditDialog.showWorldView();
            groupsEditDialog.open();

            if (!groupsEditDialog.isCanceled())
            {
                saveAll(sheetsExtractDialog, groupsEditDialog, levels);
            }
        }
    }
}
