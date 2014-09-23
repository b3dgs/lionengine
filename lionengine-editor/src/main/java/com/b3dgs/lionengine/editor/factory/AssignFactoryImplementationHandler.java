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
package com.b3dgs.lionengine.editor.factory;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;

import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.Verbose;
import com.b3dgs.lionengine.editor.UtilEclipse;
import com.b3dgs.lionengine.editor.palette.PalettePart;
import com.b3dgs.lionengine.editor.project.Project;
import com.b3dgs.lionengine.editor.project.ProjectsModel;
import com.b3dgs.lionengine.editor.world.WorldViewModel;
import com.b3dgs.lionengine.game.FactoryObjectGame;

/**
 * Assign the factory implementation handler.
 * 
 * @author Pierre-Alexandre
 */
public class AssignFactoryImplementationHandler
{
    /** Factory implementation assigned verbose. */
    private static final String VERBOSE_FACTORY_IMPLEMENTATION = "Factory implementation added: ";

    /**
     * Execute the handler.
     * 
     * @param partService The part service reference.
     */
    @Execute
    public void execute(EPartService partService)
    {
        final PalettePart part = UtilEclipse.getPart(partService, PalettePart.ID, PalettePart.class);
        final InputDialog dialog = new InputDialog(null, "Factory name", "Enter the factory name", "default", null);
        dialog.create();
        if (dialog.open() == Window.OK)
        {
            final Media selection = ProjectsModel.INSTANCE.getSelection();
            final FactoryObjectGame<?> factory = Project.getActive().getInstance(FactoryObjectGame.class, selection);
            factory.setClassLoader(Project.getActive().getClassLoader());
            factory.setPrepareEnabled(false);
            WorldViewModel.INSTANCE.setFactory(factory);

            final String name = dialog.getValue();
            final FactoryView factoryView = new FactoryView();
            factoryView.setFactory(factory);
            part.addPalette(name, factoryView);

            Verbose.info(AssignFactoryImplementationHandler.VERBOSE_FACTORY_IMPLEMENTATION, factory.getClass()
                    .getName());
        }
    }
}
