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
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MToolBar;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

import com.b3dgs.lionengine.editor.UtilEclipse;
import com.b3dgs.lionengine.editor.palette.PaletteType;
import com.b3dgs.lionengine.editor.world.WorldViewModel;
import com.b3dgs.lionengine.editor.world.WorldViewPart;

/**
 * Set selection handler.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class SetSelectionHandler
{
    /**
     * Execute the handler.
     * 
     * @param partService The part service reference.
     */
    @Execute
    public void execute(EPartService partService)
    {
        final MPart part = partService.findPart(WorldViewPart.ID);
        if (part != null)
        {
            final MToolBar toolBar = part.getToolbar();
            if (toolBar != null)
            {
                UtilEclipse.setToolItemSelection(toolBar, false, "pointer", "hand", "pipet");
                UtilEclipse.setToolItemSelection(toolBar, true, "selection");
            }
        }
        final PaletteType type = PaletteType.SELECTION;
        WorldViewModel.INSTANCE.setSelectedPalette(type);
        final WorldViewPart view = UtilEclipse.getPart(partService, WorldViewPart.ID, WorldViewPart.class);
        view.setCursor(type.getCursor());
    }
}
