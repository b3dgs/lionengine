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
package com.b3dgs.lionengine.editor.properties.tilecollision.dialog;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.b3dgs.lionengine.editor.UtilEclipse;
import com.b3dgs.lionengine.editor.dialog.AbstractEditor;
import com.b3dgs.lionengine.game.collision.CollisionFormula;

/**
 * Dialog for tile collision edition.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class TileCollisionEditor
        extends AbstractEditor
{
    /** Tile collision icon. */
    private static final Image ICON = UtilEclipse.getIcon("dialog", "edit-tilesheets.png");

    /**
     * Create the tile collision edition dialog.
     * 
     * @param parent The parent reference.
     */
    public TileCollisionEditor(Shell parent)
    {
        super(parent, Messages.Dialog_TileCollision_Title, ICON);
    }

    /**
     * Load an existing formula and fill fields.
     * 
     * @param formula The formula to load.
     */
    public void load(CollisionFormula formula)
    {
        // TODO
    }

    /**
     * Get the edited collision formula.
     * 
     * @return The edited collision formula instance.
     */
    public CollisionFormula getFormula()
    {
        return null;
    }

    /*
     * AbstractDialog
     */

    @Override
    protected void createContent(Composite content)
    {
        // TODO
    }
}
