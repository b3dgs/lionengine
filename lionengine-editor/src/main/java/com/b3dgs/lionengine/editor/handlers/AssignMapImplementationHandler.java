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
package com.b3dgs.lionengine.editor.handlers;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.editor.Tools;
import com.b3dgs.lionengine.editor.collision.TileCollisionPart;
import com.b3dgs.lionengine.editor.project.Project;
import com.b3dgs.lionengine.editor.project.ProjectsModel;
import com.b3dgs.lionengine.editor.world.WorldViewModel;
import com.b3dgs.lionengine.editor.world.WorldViewPart;
import com.b3dgs.lionengine.game.map.MapTile;

/**
 * Assign the map implementation handler.
 * 
 * @author Pierre-Alexandre
 */
public class AssignMapImplementationHandler
{
    /**
     * Execute the handler.
     * 
     * @param partService The part service reference.
     */
    @Execute
    public void execute(EPartService partService)
    {
        final Media selection = ProjectsModel.INSTANCE.getSelection();
        final MapTile<?, ?> map = Project.getActive().getInstance(MapTile.class, selection);
        WorldViewModel.INSTANCE.setMap(map);

        final WorldViewPart worldView = Tools.getPart(partService, WorldViewPart.ID, WorldViewPart.class);
        worldView.setToolBarEnabled(true);
        worldView.addListeners();
        worldView.update();

        final TileCollisionPart tileCollision = Tools.getPart(partService, TileCollisionPart.ID,
                TileCollisionPart.class);
        tileCollision.setEnabled(true);
    }
}
