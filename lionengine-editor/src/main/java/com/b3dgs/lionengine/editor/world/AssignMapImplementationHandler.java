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
package com.b3dgs.lionengine.editor.world;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.Verbose;
import com.b3dgs.lionengine.editor.UtilEclipse;
import com.b3dgs.lionengine.editor.collision.TileCollisionView;
import com.b3dgs.lionengine.editor.palette.PalettePart;
import com.b3dgs.lionengine.editor.project.Project;
import com.b3dgs.lionengine.editor.project.ProjectsModel;
import com.b3dgs.lionengine.game.map.MapTile;

/**
 * Assign the map implementation handler.
 * 
 * @author Pierre-Alexandre
 */
public class AssignMapImplementationHandler
{
    /** Map implementation assigned verbose. */
    private static final String VERBOSE_MAP_IMPLEMENTATION = "Map implementation assigned with: ";

    /**
     * Execute the handler.
     * 
     * @param partService The part service reference.
     */
    @Execute
    public void execute(EPartService partService)
    {
        final Media selection = ProjectsModel.INSTANCE.getSelection();
        final MapTile<?> map = Project.getActive().getInstance(MapTile.class, selection);
        WorldViewModel.INSTANCE.setMap(map);

        final PalettePart part = UtilEclipse.getPart(partService, PalettePart.ID, PalettePart.class);
        final TileCollisionView tileCollisionView = new TileCollisionView();
        part.addPalette("Tile Collision", null, tileCollisionView);

        Verbose.info(AssignMapImplementationHandler.VERBOSE_MAP_IMPLEMENTATION, map.getClass().getName());

        final WorldViewPart worldView = UtilEclipse.getPart(partService, WorldViewPart.ID, WorldViewPart.class);
        worldView.setToolBarEnabled(true);
        worldView.addListeners();
        worldView.update();
    }
}
