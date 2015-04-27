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
package com.b3dgs.lionengine.editor.properties.tilecollision.handler;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.b3dgs.lionengine.editor.UtilEclipse;
import com.b3dgs.lionengine.editor.properties.PropertiesPart;
import com.b3dgs.lionengine.editor.properties.tilecollision.dialog.TileCollisionEditor;
import com.b3dgs.lionengine.game.collision.CollisionFormula;
import com.b3dgs.lionengine.game.map.TileCollision;
import com.b3dgs.lionengine.game.map.TileGame;

/**
 * Edit formula handler.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class FormulaEditHandler
{
    /**
     * Execute the handler.
     * 
     * @param partService The part service reference.
     * @param parent The parent reference.
     */
    @Execute
    public void execute(EPartService partService, Shell parent)
    {
        final PropertiesPart part = UtilEclipse.getPart(partService, PropertiesPart.ID, PropertiesPart.class);
        final Tree properties = part.getTree();
        final TileGame tile = (TileGame) properties.getData();
        final TileCollision tileCollision = tile.getFeature(TileCollision.class);

        final Collection<CollisionFormula> toEdit = new ArrayList<>();
        for (final TreeItem selection : properties.getSelection())
        {
            for (final CollisionFormula formula : tileCollision.getCollisionFormulas())
            {
                if (formula.getName().equals(selection.getText(1)))
                {
                    final TileCollisionEditor dialog = new TileCollisionEditor(parent);
                    dialog.open();
                    dialog.load(formula);
                    tileCollision.addCollisionFormula(dialog.getFormula());
                    toEdit.add(formula);
                }
            }
        }

        for (final CollisionFormula formula : toEdit)
        {
            tileCollision.removeCollisionFormula(formula);
        }
        toEdit.clear();
        part.setInput(properties, tile);
    }
}
