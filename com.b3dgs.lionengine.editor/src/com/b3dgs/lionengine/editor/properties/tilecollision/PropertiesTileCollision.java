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
package com.b3dgs.lionengine.editor.properties.tilecollision;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.b3dgs.lionengine.editor.properties.PropertiesPart;
import com.b3dgs.lionengine.editor.properties.PropertiesProviderTile;
import com.b3dgs.lionengine.editor.utility.UtilIcon;
import com.b3dgs.lionengine.game.collision.tile.CollisionFormula;
import com.b3dgs.lionengine.game.collision.tile.ConfigCollisionFormula;
import com.b3dgs.lionengine.game.collision.tile.TileCollision;
import com.b3dgs.lionengine.game.tile.Tile;

/**
 * Element properties part.
 */
public class PropertiesTileCollision implements PropertiesProviderTile
{
    /** Tile formulas icon. */
    private static final Image ICON_FORMULAS = UtilIcon.get(FOLDER, "tileformulas.png");
    /** Tile formula icon. */
    private static final Image ICON_FORMULA = UtilIcon.get(FOLDER, "tileformula.png");

    /**
     * Create the attribute formulas.
     * 
     * @param properties The properties tree reference.
     * @param tile The tile reference.
     */
    private static void createAttributeTileCollisionFormulas(Tree properties, TileCollision tile)
    {
        final TreeItem item = new TreeItem(properties, SWT.NONE);
        item.setText(Messages.Properties_TileCollisionFormulas);
        item.setData(ConfigCollisionFormula.FORMULAS);
        item.setImage(PropertiesTileCollision.ICON_FORMULAS);

        for (final CollisionFormula formula : tile.getCollisionFormulas())
        {
            final TreeItem current = new TreeItem(item, SWT.NONE);
            PropertiesPart.createLine(current, Messages.Properties_TileCollisionFormula, formula.getName());
            current.setData(ConfigCollisionFormula.FORMULA);
            current.setImage(PropertiesTileCollision.ICON_FORMULA);
        }
    }

    /**
     * Create properties.
     */
    public PropertiesTileCollision()
    {
        // Nothing to do
    }

    /*
     * PropertiesProviderTile
     */

    @Override
    public void setInput(Tree properties, Tile tile)
    {
        if (tile.hasFeature(TileCollision.class))
        {
            final TileCollision tileCollision = tile.getFeature(TileCollision.class);
            createAttributeTileCollisionFormulas(properties, tileCollision);
        }
    }
}
