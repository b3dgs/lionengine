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
package com.b3dgs.lionengine.editor.world.handler;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.widgets.Shell;

import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.Verbose;
import com.b3dgs.lionengine.editor.UtilEclipse;
import com.b3dgs.lionengine.editor.dialogs.ImportMapDialog;
import com.b3dgs.lionengine.editor.world.WorldViewModel;
import com.b3dgs.lionengine.editor.world.WorldViewPart;
import com.b3dgs.lionengine.game.Camera;
import com.b3dgs.lionengine.game.map.MapTile;

/**
 * Import map handler.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class ImportMapHandler
{
    /** Import level verbose. */
    private static final String VERBOSE_IMPORT_LEVEL = "Importing map from level rip: ";
    /** Using tile sheet verbose. */
    private static final String VERBOSE_USING_TILESHEETS = " using the following sheets: ";

    /**
     * Import map from its level rip and tile sheets.
     * 
     * @param levelrip The level rip.
     * @param sheetsConfig The tile sheets directory.
     * @param groupsConfig The groups configuration.
     * @param partService The part service reference.
     */
    private static void importMap(EPartService partService, Media levelrip, Media sheetsConfig, Media groupsConfig)
    {
        Verbose.info(ImportMapHandler.VERBOSE_IMPORT_LEVEL, levelrip.getPath(),
                ImportMapHandler.VERBOSE_USING_TILESHEETS, sheetsConfig.getPath());

        final MapTile map = WorldViewModel.INSTANCE.getMap();
        map.create(levelrip, sheetsConfig, groupsConfig);

        final Camera camera = WorldViewModel.INSTANCE.getCamera();
        camera.setLimits(map);
    }

    /**
     * Execute the handler.
     * 
     * @param shell The shell reference.
     * @param partService The part service reference.
     */
    @Execute
    public void execute(Shell shell, EPartService partService)
    {
        final ImportMapDialog importMapDialog = new ImportMapDialog(shell);
        importMapDialog.open();

        if (importMapDialog.isFound())
        {
            final Media levelrip = importMapDialog.getLevelRipLocation();
            final Media sheetsConfig = importMapDialog.getSheetsConfigLocation();
            final Media groupsConfig = importMapDialog.getGroupsConfigLocation();
            ImportMapHandler.importMap(partService, levelrip, sheetsConfig, groupsConfig);

            final WorldViewPart part = UtilEclipse.getPart(partService, WorldViewPart.ID, WorldViewPart.class);
            part.update();
        }
    }
}
