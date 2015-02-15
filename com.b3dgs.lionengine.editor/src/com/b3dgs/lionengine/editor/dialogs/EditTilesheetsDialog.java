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
package com.b3dgs.lionengine.editor.dialogs;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.b3dgs.lionengine.ImageInfo;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.swt.UtilityMedia;
import com.b3dgs.lionengine.editor.UtilEclipse;
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
    private static final Image ICON = UtilEclipse.getIcon("dialog", "edit-tilesheets.png");

    /** Tile sheets media. */
    final Media tilesheets;
    /** Buttons list. */
    private final Collection<Button> buttons;

    /**
     * Create a tile sheets dialog.
     * 
     * @param parent The parent shell.
     * @param tilesheets The tile sheets media.
     */
    public EditTilesheetsDialog(Shell parent, Media tilesheets)
    {
        super(parent, Messages.EditTilesheetsDialog_Title, Messages.EditTilesheetsDialog_HeaderTitle,
                Messages.EditTilesheetsDialog_HeaderDesc, EditTilesheetsDialog.ICON);
        this.tilesheets = tilesheets;
        buttons = new ArrayList<>();
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

        final File[] files = tilesheets.getFile().getParentFile().listFiles();
        if (files != null)
        {
            for (final File file : files)
            {
                final Media media = UtilityMedia.get(file);
                if (ImageInfo.isImage(media))
                {
                    final Button check = new Button(composite, SWT.CHECK);
                    check.setText(file.getName());
                    check.setSelection(false);
                    buttons.add(check);
                }
            }
        }

        final XmlNode node = Stream.loadXml(tilesheets);
        final Collection<XmlNode> sheets = node.getChildren();
        for (final Button button : buttons)
        {
            for (final XmlNode sheet : sheets)
            {
                if (button.getText().equals(sheet.getText()))
                {
                    button.setSelection(true);
                    break;
                }
            }
        }
    }

    @Override
    protected void onFinish()
    {
        final XmlNode root = Stream.createXmlNode("lionengine:tilesheets");
        root.writeString("xmlns:lionengine", "http://lionengine.b3dgs.com");
        for (final Button button : buttons)
        {
            if (button.getSelection())
            {
                final XmlNode node = Stream.createXmlNode("lionengine:tilesheet");
                node.setText(button.getText());
                root.add(node);
            }
        }
        Stream.saveXml(root, tilesheets);
    }
}
