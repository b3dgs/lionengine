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
package com.b3dgs.lionengine.editor.dialogs;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.editor.Activator;

/**
 * Represents the entity edition dialog.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class EntityEditDialog
        extends AbstractDialog
{
    /** Icon. */
    private static final Image ICON = Activator.getIcon("dialog", "edit-entity.png");

    /** Entity media. */
    private final Media entity;

    /**
     * Constructor.
     * 
     * @param parent The parent shell.
     * @param entity The entity media.
     */
    public EntityEditDialog(Shell parent, Media entity)
    {
        super(parent, Messages.EditEntityDialog_Title, Messages.EditEntityDialog_HeaderTitle,
                Messages.EditEntityDialog_HeaderDesc, EntityEditDialog.ICON);
        this.entity = entity;
        finish.setEnabled(true);
        createDialog();
    }

    /*
     * AbstractDialog
     */

    @Override
    protected void createContent(Composite content)
    {
        // Nothing to do
    }

    @Override
    protected void onFinish()
    {
        // Nothing to do
    }
}
