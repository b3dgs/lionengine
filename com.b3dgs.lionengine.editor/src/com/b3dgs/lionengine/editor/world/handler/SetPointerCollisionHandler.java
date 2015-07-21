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
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MToolBar;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

import com.b3dgs.lionengine.editor.UtilEclipse;
import com.b3dgs.lionengine.editor.world.PaletteType;
import com.b3dgs.lionengine.editor.world.WorldViewModel;
import com.b3dgs.lionengine.editor.world.WorldViewPart;

/**
 * Set pointer collision handler.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class SetPointerCollisionHandler
{
    /** Element ID. */
    public static final String ID = "pointer-collision";
    /** Excluded elements. */
    private static final String[] EXCLUDED =
    {
        SetPointerObjectHandler.ID, SetPointerTileHandler.ID, SetHandHandler.ID, SetPipetHandler.ID,
        SetSelectionHandler.ID
    };

    /**
     * Create handler.
     */
    public SetPointerCollisionHandler()
    {
        // Nothing to do
    }

    /**
     * Execute the handler.
     * 
     * @param partService The part service reference.
     */
    @Execute
    @SuppressWarnings("static-method")
    public void execute(EPartService partService)
    {
        final MPart part = partService.findPart(WorldViewPart.ID);
        if (part != null)
        {
            final MToolBar toolBar = part.getToolbar();
            if (toolBar != null)
            {
                UtilEclipse.setToolItemSelection(toolBar, false, EXCLUDED);
                UtilEclipse.setToolItemSelection(toolBar, true, ID);
            }
        }
        final PaletteType type = PaletteType.POINTER_COLLISION;
        WorldViewModel.INSTANCE.setSelectedPalette(type);
        final WorldViewPart view = UtilEclipse.getPart(WorldViewPart.ID, WorldViewPart.class);
        view.setCursor(type.getCursor());
    }
}
