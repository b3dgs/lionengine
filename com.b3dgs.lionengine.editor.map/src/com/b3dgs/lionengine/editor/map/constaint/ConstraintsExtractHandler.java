/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.editor.map.constaint;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.widgets.Shell;

import com.b3dgs.lionengine.editor.world.WorldModel;
import com.b3dgs.lionengine.editor.world.view.WorldPart;

/**
 * Export map tile constraints handler.
 */
public final class ConstraintsExtractHandler
{
    /** Element ID. */
    public static final String ID = "menu.map.extract-constraints";

    /**
     * Create handler.
     */
    public ConstraintsExtractHandler()
    {
        super();
    }

    /**
     * Execute the handler.
     * 
     * @param shell The shell reference.
     */
    @Execute
    public void execute(Shell shell)
    {
        final ConstraintsExtractDialog constraintsExtractDialog = new ConstraintsExtractDialog(shell);
        constraintsExtractDialog.open();

        final WorldPart worldPart = WorldModel.INSTANCE.getServices().get(WorldPart.class);
        worldPart.update();
    }
}
