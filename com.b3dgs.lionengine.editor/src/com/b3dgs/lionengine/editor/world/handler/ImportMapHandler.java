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
     * @param level The level rip.
     * @param pattern The tile sheets directory.
     * @param partService The part service reference.
     */
    private static void importMap(EPartService partService, Media level, Media pattern)
    {
        Verbose.info(ImportMapHandler.VERBOSE_IMPORT_LEVEL, level.getPath(), ImportMapHandler.VERBOSE_USING_TILESHEETS,
                pattern.getPath());

        final MapTile<?> map = WorldViewModel.INSTANCE.getMap();
        map.load(level, pattern);
        map.createCollisionDraw();
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
            final Media level = importMapDialog.getLevelRipLocation();
            final Media pattern = importMapDialog.getPatternsLocation();
            ImportMapHandler.importMap(partService, level, pattern);

            final WorldViewPart part = UtilEclipse.getPart(partService, WorldViewPart.ID, WorldViewPart.class);
            part.update();
        }
    }
}
