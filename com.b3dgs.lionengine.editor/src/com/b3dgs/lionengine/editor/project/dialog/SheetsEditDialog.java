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
package com.b3dgs.lionengine.editor.project.dialog;

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
import org.eclipse.swt.widgets.Text;

import com.b3dgs.lionengine.ImageInfo;
import com.b3dgs.lionengine.core.EngineCore;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.swt.UtilityMedia;
import com.b3dgs.lionengine.editor.InputValidator;
import com.b3dgs.lionengine.editor.UtilEclipse;
import com.b3dgs.lionengine.editor.UtilSwt;
import com.b3dgs.lionengine.editor.dialog.AbstractDialog;
import com.b3dgs.lionengine.game.configurer.Configurer;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.stream.Stream;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Represents the sheets edition dialog.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class SheetsEditDialog
        extends AbstractDialog
{
    /** Icon. */
    private static final Image ICON = UtilEclipse.getIcon("dialog", "edit.png");

    /** Sheets media. */
    final Media sheets;
    /** Buttons list. */
    private final Collection<Button> buttons = new ArrayList<>();
    /** Tile width text. */
    private Text tileWidthText;
    /** Tile height text. */
    private Text tileHeightText;

    /**
     * Create a sheets dialog.
     * 
     * @param parent The parent shell.
     * @param sheets The sheets media.
     */
    public SheetsEditDialog(Shell parent, Media sheets)
    {
        super(parent, Messages.EditSheetsDialog_Title, Messages.EditSheetsDialog_HeaderTitle,
                Messages.EditSheetsDialog_HeaderDesc, ICON);
        this.sheets = sheets;
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

        final Composite tileSizeArea = new Composite(composite, SWT.NONE);
        tileSizeArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        tileSizeArea.setLayout(new GridLayout(2, false));

        tileWidthText = UtilSwt.createText(Messages.EditSheetsDialog_TileWidth, tileSizeArea);
        tileWidthText.addVerifyListener(UtilSwt.createVerify(tileWidthText, InputValidator.INTEGER_POSITIVE_MATCH));
        tileHeightText = UtilSwt.createText(Messages.EditSheetsDialog_TileHeight, tileSizeArea);
        tileHeightText.addVerifyListener(UtilSwt.createVerify(tileHeightText, InputValidator.INTEGER_POSITIVE_MATCH));

        final File[] files = sheets.getFile().getParentFile().listFiles();
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

        final XmlNode node = Stream.loadXml(sheets);
        final XmlNode tileSize = node.getChild(MapTile.NODE_TILE_SIZE);
        tileWidthText.setText(tileSize.readString(MapTile.ATTRIBUTE_TILE_WIDTH));
        tileHeightText.setText(tileSize.readString(MapTile.ATTRIBUTE_TILE_HEIGHT));
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
        final XmlNode root = Stream.createXmlNode(MapTile.NODE_TILE_SHEETS);
        root.writeString(Configurer.HEADER, EngineCore.WEBSITE);
        final XmlNode tileSize = Stream.createXmlNode(MapTile.NODE_TILE_SIZE);
        tileSize.writeString(MapTile.ATTRIBUTE_TILE_WIDTH, tileWidthText.getText());
        tileSize.writeString(MapTile.ATTRIBUTE_TILE_HEIGHT, tileHeightText.getText());
        root.add(tileSize);
        for (final Button button : buttons)
        {
            if (button.getSelection())
            {
                final XmlNode node = Stream.createXmlNode(MapTile.NODE_TILE_SHEET);
                node.setText(button.getText());
                root.add(node);
            }
        }
        Stream.saveXml(root, sheets);
    }
}
