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
package com.b3dgs.lionengine.editor.map.sheet.editor;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.UtilFile;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.editor.dialog.DialogAbstract;
import com.b3dgs.lionengine.editor.utility.UtilIcon;
import com.b3dgs.lionengine.editor.utility.control.UtilButton;
import com.b3dgs.lionengine.editor.utility.control.UtilText;
import com.b3dgs.lionengine.editor.validator.InputValidator;
import com.b3dgs.lionengine.game.feature.tile.map.TileSheetsConfig;
import com.b3dgs.lionengine.graphic.drawable.ImageInfo;

/**
 * Represents the sheets edition dialog.
 */
public class SheetsEditDialog extends DialogAbstract
{
    /** Icon. */
    private static final Image ICON = UtilIcon.get("dialog", "edit.png");
    /** Default size. */
    private static final String DEFAULT_SIZE = "16";

    /** Sheets media. */
    private final Media sheets;
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
        super(parent, Messages.Title, Messages.HeaderTitle, Messages.HeaderDesc, ICON);
        this.sheets = sheets;
        dialog.setMinimumSize(100, 100);
        createDialog();
        finish.setEnabled(true);
    }

    /**
     * Create size text area.
     * 
     * @param parent The parent composite.
     */
    private void createTextSize(Composite parent)
    {
        final Group tileSizeArea = new Group(parent, SWT.NONE);
        tileSizeArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        tileSizeArea.setLayout(new GridLayout(1, false));
        tileSizeArea.setText(Messages.TileSize);

        tileWidthText = UtilText.create(Messages.TileWidth, tileSizeArea);
        tileWidthText.setText(DEFAULT_SIZE);
        UtilText.addVerify(tileWidthText, InputValidator.INTEGER_POSITIVE_STRICT_MATCH);

        tileHeightText = UtilText.create(Messages.TileHeight, tileSizeArea);
        tileHeightText.setText(DEFAULT_SIZE);
        UtilText.addVerify(tileHeightText, InputValidator.INTEGER_POSITIVE_STRICT_MATCH);
    }

    /**
     * Create the sheets list with their check box.
     * 
     * @param parent The parent composite.
     */
    private void createCheckSheets(Composite parent)
    {
        final Group tileSheetsArea = new Group(parent, SWT.NONE);
        tileSheetsArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        tileSheetsArea.setLayout(new GridLayout(1, false));
        tileSheetsArea.setText(Messages.TileSheets);

        for (final File file : UtilFile.getFiles(sheets.getFile().getParentFile()))
        {
            final Media media = Medias.get(file);
            if (ImageInfo.isImage(media))
            {
                final Button check = new Button(tileSheetsArea, SWT.CHECK);
                check.setText(file.getName());
                check.setSelection(false);
                UtilButton.registerDirty(check, true);

                buttons.add(check);
            }
        }
    }

    /**
     * Load existing data and fill fields.
     */
    private void loadData()
    {
        final Xml root = new Xml(sheets);
        loadSize(root);
        loadSheets(root);

        UtilText.registerDirty(tileWidthText, true);
        UtilText.registerDirty(tileHeightText, true);
    }

    /**
     * Load the size node.
     * 
     * @param root The root reference.
     */
    private void loadSize(Xml root)
    {
        if (root.hasNode(TileSheetsConfig.NODE_TILE_SIZE))
        {
            final Xml tileSize = root.getChildXml(TileSheetsConfig.NODE_TILE_SIZE);
            if (tileSize.hasAttribute(TileSheetsConfig.ATT_TILE_WIDTH))
            {
                tileWidthText.setText(tileSize.getString(TileSheetsConfig.ATT_TILE_WIDTH));
            }
            if (tileSize.hasAttribute(TileSheetsConfig.ATT_TILE_HEIGHT))
            {
                tileHeightText.setText(tileSize.getString(TileSheetsConfig.ATT_TILE_HEIGHT));
            }
        }
    }

    /**
     * Load the sheet nodes.
     * 
     * @param root The root reference.
     */
    private void loadSheets(Xml root)
    {
        final Collection<Xml> nodeSheets = root.getChildrenXml();
        for (final Button button : buttons)
        {
            for (final Xml sheet : nodeSheets)
            {
                if (button.getText().equals(sheet.getText()))
                {
                    button.setSelection(true);
                    break;
                }
            }
        }
        nodeSheets.clear();
    }

    @Override
    protected void createContent(Composite content)
    {
        final Composite composite = new Composite(content, SWT.NONE);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        composite.setLayout(new GridLayout(1, false));

        createTextSize(composite);
        createCheckSheets(composite);
        loadData();
    }

    @Override
    protected void onFinish()
    {
        final Xml root = new Xml(sheets);
        final Xml tileSize;
        if (root.hasNode(TileSheetsConfig.NODE_TILE_SIZE))
        {
            tileSize = root.getChildXml(TileSheetsConfig.NODE_TILE_SIZE);
        }
        else
        {
            tileSize = root.createChild(TileSheetsConfig.NODE_TILE_SIZE);
        }
        tileSize.writeString(TileSheetsConfig.ATT_TILE_WIDTH, tileWidthText.getText());
        tileSize.writeString(TileSheetsConfig.ATT_TILE_HEIGHT, tileHeightText.getText());

        root.removeChildren(TileSheetsConfig.NODE_TILE_SHEET);
        for (final Button button : buttons)
        {
            if (button.getSelection())
            {
                final Xml node = root.createChild(TileSheetsConfig.NODE_TILE_SHEET);
                node.setText(button.getText());
            }
        }
        root.save(sheets);
    }
}
