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
package com.b3dgs.lionengine.editor.formula.properties;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.widgets.Shell;

import com.b3dgs.lionengine.editor.formula.editor.FormulasEditDialog;
import com.b3dgs.lionengine.editor.world.WorldModel;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.collision.MapTileCollision;

/**
 * Remove formula handler.
 */
public final class FormulaRemoveHandler
{
    /**
     * Create handler.
     */
    public FormulaRemoveHandler()
    {
        super();
    }

    /**
     * Execute the handler.
     * 
     * @param parent The parent reference.
     */
    @Execute
    public void execute(Shell parent)
    {
        final MapTile map = WorldModel.INSTANCE.getMap();
        final MapTileCollision mapCollision = map.getFeature(MapTileCollision.class);
        final FormulasEditDialog dialog = new FormulasEditDialog(parent, mapCollision.getFormulasConfig());
        dialog.open();
    }
}
