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
package com.b3dgs.lionengine.editor.collision;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.b3dgs.lionengine.editor.Tools;
import com.b3dgs.lionengine.editor.dialogs.AbstractEditor;

/**
 * Tile collision editor.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class TileCollisionEditor
        extends AbstractEditor
{
    /** Dialog title. */
    public static final String DIALOG_TITLE = "Tile Collisions Editor";
    /** Dialog icon. */
    public static final Image DIALOG_ICON = Tools.getIcon("collision-editor", "dialog.png");

    /**
     * Constructor.
     * 
     * @param parent The parent reference.
     */
    public TileCollisionEditor(Composite parent)
    {
        super(TileCollisionEditor.DIALOG_TITLE, TileCollisionEditor.DIALOG_ICON, parent);
    }

    /*
     * AbstractEditor
     */

    @Override
    protected void createContent(Composite parent)
    {
        final Composite content = new Composite(parent, SWT.NONE);
        content.setLayout(new GridLayout(2, false));
    }
}
