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
package com.b3dgs.lionengine.editor.world.dialog;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.editor.InputValidator;
import com.b3dgs.lionengine.editor.ObjectList;
import com.b3dgs.lionengine.editor.Tools;
import com.b3dgs.lionengine.editor.UtilEclipse;
import com.b3dgs.lionengine.editor.UtilSwt;
import com.b3dgs.lionengine.editor.dialog.AbstractDialog;
import com.b3dgs.lionengine.editor.project.Project;
import com.b3dgs.lionengine.game.map.TileExtractor;

/**
 * Import sheets dialog.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class SheetsImportDialog
        extends AbstractDialog
{
    /** Icon. */
    private static final Image ICON = UtilEclipse.getIcon("dialog", "import.png");

    /** Level rip list. */
    Tree levelRips;
    /** Destination folder. */
    Text folderText;
    /** Tile width. */
    Text widthText;
    /** Tile height. */
    Text heightText;
    /** Horizontal tiles. */
    Text horizontalText;
    /** Vertical tiles. */
    Text verticalText;
    /** Add level rip. */
    private Button addLevelRip;
    /** Remove level rip. */
    private Button removeLevelRip;

    /**
     * Create the dialog.
     * 
     * @param parent The parent reference.
     */
    public SheetsImportDialog(Shell parent)
    {
        super(parent, Messages.SheetsImportDialog_Title, Messages.SheetsImportDialog_HeaderTitle,
                Messages.SheetsImportDialog_HeaderDesc, ICON);

        createDialog();
        setTipsMessage(ICON_ERROR, Messages.SheetsImportDialog_NoLevelRipDefined);
        tipsLabel.setVisible(true);
    }

    /**
     * Called on add level rip action.
     */
    void onAddLevelRip()
    {
        final File file = Tools.selectResourceFile(dialog, true, new String[]
        {
            com.b3dgs.lionengine.editor.world.dialog.Messages.ImportMapDialog_LevelRipFileFilter
        }, new String[]
        {
            "*.bmp;*.png"
        });
        if (file != null)
        {
            final TreeItem item = new TreeItem(levelRips, SWT.NONE);
            item.setText(file.getPath());

            final Media media = Project.getActive().getResourceMedia(file);
            item.setData(media);

            if (!finish.isEnabled())
            {
                finish.setEnabled(true);
                tipsLabel.setVisible(false);
            }
        }
    }

    /**
     * Called on remove a level rip action.
     */
    void onRemoveLevelRip()
    {
        for (final TreeItem item : levelRips.getSelection())
        {
            item.setData(null);
            item.dispose();
        }
        if (levelRips.getItems().length == 0)
        {
            finish.setEnabled(false);
            tipsLabel.setVisible(true);
        }
    }

    /**
     * Called on browse extraction location action.
     */
    void onBrowseExtractionLocation()
    {
        final File file = Tools.selectResourceFolder(dialog);
        if (file != null)
        {
            folderText.setText(file.getPath());
            final Media extractFolder = Project.getActive().getResourceMedia(file);
            folderText.setData(extractFolder);
        }
    }

    /**
     * Create the add level rip button.
     * 
     * @param parent The composite parent.
     */
    private void createButtonAdd(Composite parent)
    {
        addLevelRip = new Button(parent, SWT.PUSH);
        addLevelRip.setImage(ObjectList.ICON_ADD);
        addLevelRip.setToolTipText(Messages.SheetsImportDialog_AddLevelRip);
        addLevelRip.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent event)
            {
                onAddLevelRip();
            }
        });
    }

    /**
     * Create the remove level rip button.
     * 
     * @param parent The composite parent.
     */
    private void createButtonRemove(Composite parent)
    {
        removeLevelRip = new Button(parent, SWT.PUSH);
        removeLevelRip.setImage(ObjectList.ICON_REMOVE);
        removeLevelRip.setToolTipText(Messages.SheetsImportDialog_RemoveLevelRip);
        removeLevelRip.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent event)
            {
                onRemoveLevelRip();
            }
        });
    }

    /**
     * Create the extraction location area.
     * 
     * @param content The content composite.
     */
    private void createExtractLocationArea(Composite content)
    {
        final Composite area = new Composite(content, SWT.NONE);
        area.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        area.setLayout(new GridLayout(3, false));

        final Label locationLabel = new Label(area, SWT.NONE);
        locationLabel.setText(com.b3dgs.lionengine.editor.project.dialog.Messages.AbstractProjectDialog_Location);

        folderText = new Text(area, SWT.BORDER);
        folderText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        final Button browse = UtilSwt.createButton(area,
                com.b3dgs.lionengine.editor.dialog.Messages.AbstractDialog_Browse, null);
        browse.setImage(AbstractDialog.ICON_BROWSE);
        browse.forceFocus();
        browse.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent)
            {
                onBrowseExtractionLocation();
            }
        });
    }

    /**
     * Create the config text.
     * 
     * @param parent The parent composite.
     */
    private void createTextConfig(Composite parent)
    {
        final Composite config = new Composite(parent, SWT.NONE);
        config.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        config.setLayout(new GridLayout(2, true));

        widthText = UtilSwt.createText(Messages.SheetsImportDialog_TileWidth, config);
        widthText.addVerifyListener(UtilSwt.createVerify(widthText, InputValidator.INTEGER_POSITIVE_MATCH));

        heightText = UtilSwt.createText(Messages.SheetsImportDialog_TileHeight, config);
        heightText.addVerifyListener(UtilSwt.createVerify(heightText, InputValidator.INTEGER_POSITIVE_MATCH));

        horizontalText = UtilSwt.createText(Messages.SheetsImportDialog_HorizontalTiles, config);
        horizontalText.addVerifyListener(UtilSwt.createVerify(horizontalText, InputValidator.INTEGER_POSITIVE_MATCH));

        verticalText = UtilSwt.createText(Messages.SheetsImportDialog_VerticalTiles, config);
        verticalText.addVerifyListener(UtilSwt.createVerify(verticalText, InputValidator.INTEGER_POSITIVE_MATCH));
    }

    /*
     * AbstractDialog
     */

    @Override
    protected void createContent(Composite content)
    {
        final Composite area = new Composite(content, SWT.NONE);
        area.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        area.setLayout(new GridLayout(1, false));

        levelRips = new Tree(area, SWT.SINGLE);
        levelRips.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        final Composite buttons = new Composite(area, SWT.NONE);
        buttons.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        buttons.setLayout(new GridLayout(2, true));

        createButtonAdd(buttons);
        createButtonRemove(buttons);

        createExtractLocationArea(content);
        createTextConfig(content);
    }

    @Override
    protected void onFinish()
    {
        final Media extractFolder = (Media) folderText.getData();
        final int tw = Integer.parseInt(widthText.getText());
        final int th = Integer.parseInt(heightText.getText());
        final int h = Integer.parseInt(horizontalText.getText());
        final int v = Integer.parseInt(verticalText.getText());
        final TileExtractor tileExtractor = new TileExtractor(extractFolder, tw, th, h, v);
        for (final TreeItem item : levelRips.getItems())
        {
            tileExtractor.addRip((Media) item.getData());
        }
        tileExtractor.start();
    }
}
