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

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.editor.Tools;
import com.b3dgs.lionengine.stream.Stream;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Represents the tile sheets edition dialog.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class EditTilesheetsDialog
        extends AbstractDialog
{
    /** Icon. */
    private static final Image ICON = Tools.getIcon("dialog", "edit-tilesheets.png");

    /** Tile sheets media. */
    final Media tilesheets;

    /**
     * Constructor.
     * 
     * @param parent The parent shell.
     * @param tilesheets The tile sheets media.
     */
    public EditTilesheetsDialog(Shell parent, Media tilesheets)
    {
        super(parent, Messages.EditTilesheetsDialog_Title, Messages.EditTilesheetsDialog_HeaderTitle,
                Messages.EditTilesheetsDialog_HeaderDesc, EditTilesheetsDialog.ICON);
        this.tilesheets = tilesheets;
        createDialog();
        finish.setEnabled(true);
    }

    /*
     * AbstractDialog
     */

    @Override
    protected void createContent(Composite content)
    {
        final Composite composite = new Composite(content, SWT.NONE);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        composite.setLayout(new GridLayout(1, false));

        final XmlNode node = Stream.loadXml(tilesheets);
        final List<XmlNode> sheets = node.getChildren();
        for (final XmlNode sheet : sheets)
        {
            final String name = sheet.getText();
            final Button check = new Button(composite, SWT.CHECK);
            check.setText(name);
        }
    }

    @Override
    protected void onFinish()
    {
        // TODO Save
    }
}
