/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.b3dgs.lionengine.editor.map.group.menu;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.widgets.Shell;

/**
 * Edit tile groups handler implementation.
 */
public final class GroupsEditHandler
{
    /** Element ID. */
    public static final String ID = "menu.map.edit-groups";

    /**
     * Create handler.
     */
    public GroupsEditHandler()
    {
        super();
    }

    /**
     * Execute the handler.
     * 
     * @param partService The part service reference.
     * @param shell The shell reference.
     */
    @Execute
    public void execute(EPartService partService, Shell shell)
    {
        final GroupsEditDialog groupsEditDialog = new GroupsEditDialog(partService, shell);
        groupsEditDialog.open();
    }
}
