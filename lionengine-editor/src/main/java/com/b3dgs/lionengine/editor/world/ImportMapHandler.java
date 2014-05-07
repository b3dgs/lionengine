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
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.widgets.Shell;

import com.b3dgs.lionengine.core.Core;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.editor.dialogs.ImportMapDialog;
import com.b3dgs.lionengine.game.map.MapTile;

/**
 * Assign the map implementation handler.
 * 
 * @author Pierre-Alexandre
 */
public class ImportMapHandler
{
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
        if (importMapDialog.open())
        {
            final Media levelRip = Core.MEDIA.create(importMapDialog.getLevelRipLocation());
            final Media patternsDirectory = Core.MEDIA.create(importMapDialog.getPatternsLocation());

            final MapTile<?, ?> map = WorldViewModel.INSTANCE.getMap();
            map.load(levelRip, patternsDirectory);

            final MPart part = partService.findPart(WorldViewPart.ID);
            if (part != null && part.getObject() instanceof WorldViewPart)
            {
                ((WorldViewPart) part.getObject()).update();
            }
        }
    }
}
