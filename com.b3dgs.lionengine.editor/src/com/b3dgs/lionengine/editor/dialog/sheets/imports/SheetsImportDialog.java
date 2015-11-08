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
package com.b3dgs.lionengine.editor.dialog.sheets.imports;

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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.editor.InputValidator;
import com.b3dgs.lionengine.editor.ObjectList;
import com.b3dgs.lionengine.editor.dialog.AbstractDialog;
import com.b3dgs.lionengine.editor.project.Project;
import com.b3dgs.lionengine.editor.utility.UtilButton;
import com.b3dgs.lionengine.editor.utility.UtilDialog;
import com.b3dgs.lionengine.editor.utility.UtilIcon;
import com.b3dgs.lionengine.editor.utility.UtilText;
import com.b3dgs.lionengine.game.tile.ConfigTileSheets;
import com.b3dgs.lionengine.game.tile.TileExtractor;

/**
 * Import sheets dialog.
 */
public class SheetsImportDialog extends AbstractDialog
{
    /** Level rip filter. */
    public static final String[] LEVEL_RIP_FILTER = new String[]
    {
        "*.bmp;*.png"
    };
    /** Icon. */
    static final Image ICON = UtilIcon.get("dialog", "import.png");
    /** Error on config file generation. */
    private static final String ERROR_GENERATE = "Unable to generate sheets config file !";

    /** Level rip list. */
    private Tree levelRips;
    /** Destination folder. */
    private Text folderText;
    /** Tile width. */
    private Text widthText;
    /** Tile height. */
    private Text heightText;
    /** Horizontal tiles. */
    private Text horizontalText;
    /** Vertical tiles. */
    private Text verticalText;
    /** Generate sheets config. */
    private Button generate;
    /** Add level rip. */
    private Button addLevelRip;
    /** Remove level rip. */
    private Button removeLevelRip;
    /** Tile extractor. */
    private TileExtractor extractor;

    /**
     * Create the dialog.
     * 
     * @param parent The parent reference.
     */
    public SheetsImportDialog(Shell parent)
    {
        super(parent, Messages.Title, Messages.HeaderTitle, Messages.HeaderDesc, ICON);

        createDialog();
        setTipsMessage(ICON_ERROR, Messages.NoLevelRipDefined);
        tipsLabel.setVisible(true);
    }

    /**
     * Called on add level rip action.
     */
    private void onAddLevelRip()
    {
        final File[] files = UtilDialog.selectResourceFiles(dialog, new String[]
        {
            Messages.LevelRipFileFilter
        }, LEVEL_RIP_FILTER);
        final Project project = Project.getActive();
        for (final File file : files)
        {
            final TreeItem item = new TreeItem(levelRips, SWT.NONE);
            item.setText(file.getPath());

            final Media media = project.getResourceMedia(file);
            item.setData(media);

            if (!finish.isEnabled())
            {
                checkFinish();
            }

            if (folderText.getData() == null)
            {
                final File parent = file.getParentFile();
                folderText.setText(parent.getPath());
                final Media extractFolder = project.getResourceMedia(parent);
                folderText.setData(extractFolder);
            }
        }
    }

    /**
     * Called on remove a level rip action.
     */
    private void onRemoveLevelRip()
    {
        for (final TreeItem item : levelRips.getSelection())
        {
            item.setData(null);
            item.dispose();
        }
        if (levelRips.getItems().length == 0)
        {
            checkFinish();
        }
    }

    /**
     * Called on browse extraction location action.
     */
    private void onBrowseExtractionLocation()
    {
        final File file = UtilDialog.selectResourceFolder(dialog);
        if (file != null)
        {
            folderText.setText(file.getPath());
            final Media extractFolder = Project.getActive().getResourceMedia(file);
            folderText.setData(extractFolder);
        }
    }

    /**
     * Generate config file.
     * 
     * @param tw The tile width.
     * @param th The tile height.
     * @param extractFolder The extraction folder.
     */
    private void generateConfig(int tw, int th, Media extractFolder)
    {
        try
        {
            final Collection<String> sheets = new ArrayList<>();
            for (final Media media : extractor.getGeneratedSheets())
            {
                sheets.add(media.getFile().getName());
            }
            final File file = new File(extractFolder.getFile(), ConfigTileSheets.FILENAME);
            final Media config = Project.getActive().getResourceMedia(file);
            ConfigTileSheets.exports(config, tw, th, sheets);
        }
        catch (final LionEngineException exception)
        {
            Verbose.exception(exception, ERROR_GENERATE);
        }
    }

    /**
     * Check for finish button enabling.
     */
    private void checkFinish()
    {
        final boolean hasRips = levelRips.getItems().length > 0;
        final boolean hasSize = !widthText.getText().isEmpty() && !heightText.getText().isEmpty();
        final boolean hasNumbers = !horizontalText.getText().isEmpty() && !verticalText.getText().isEmpty();
        final boolean finished = hasRips && !folderText.getText().isEmpty() && hasSize && hasNumbers;

        finish.setEnabled(finished);
        tipsLabel.setVisible(!finished);
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
        addLevelRip.setToolTipText(Messages.AddLevelRip);
        UtilButton.setAction(addLevelRip, () -> onAddLevelRip());
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
        removeLevelRip.setToolTipText(Messages.RemoveLevelRip);
        UtilButton.setAction(removeLevelRip, () -> onRemoveLevelRip());
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
        locationLabel.setText(Messages.ExtractTo);

        folderText = new Text(area, SWT.BORDER);
        folderText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        textCheckFinish(folderText);

        final Button browse = UtilButton.createBrowse(area);
        browse.forceFocus();
        UtilButton.setAction(browse, () -> onBrowseExtractionLocation());
    }

    /**
     * Create the config text.
     * 
     * @param parent The parent composite.
     */
    private void createTextConfig(Composite parent)
    {
        final Group config = new Group(parent, SWT.NONE);
        config.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        config.setLayout(new GridLayout(2, true));

        widthText = UtilText.create(Messages.TileWidth, config);
        widthText.addVerifyListener(UtilText.createVerify(widthText, InputValidator.INTEGER_POSITIVE_STRICT_MATCH));
        textCheckFinish(widthText);

        heightText = UtilText.create(Messages.TileHeight, config);
        heightText.addVerifyListener(UtilText.createVerify(heightText, InputValidator.INTEGER_POSITIVE_STRICT_MATCH));
        textCheckFinish(heightText);

        horizontalText = UtilText.create(Messages.HorizontalTiles, config);
        horizontalText.addVerifyListener(UtilText.createVerify(horizontalText,
                                                               InputValidator.INTEGER_POSITIVE_STRICT_MATCH));
        textCheckFinish(horizontalText);

        verticalText = UtilText.create(Messages.VerticalTiles, config);
        verticalText.addVerifyListener(UtilText.createVerify(verticalText,
                                                             InputValidator.INTEGER_POSITIVE_STRICT_MATCH));
        textCheckFinish(verticalText);
    }

    /**
     * Check if finish button can be enabled on text edition.
     * 
     * @param text The text reference.
     */
    private void textCheckFinish(Text text)
    {
        text.addModifyListener(event -> checkFinish());
    }

    /*
     * AbstractDialog
     */

    @Override
    protected void createContent(Composite content)
    {
        final Group area = new Group(content, SWT.NONE);
        area.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        area.setLayout(new GridLayout(1, false));
        area.setText(Messages.RipsList);

        levelRips = new Tree(area, SWT.SINGLE);
        levelRips.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        final Composite buttons = new Composite(area, SWT.NONE);
        buttons.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        buttons.setLayout(new GridLayout(3, false));

        final Label label = new Label(buttons, SWT.NONE);
        label.setText(Messages.AddRemoveRip);

        createButtonAdd(buttons);
        createButtonRemove(buttons);

        final Composite config = new Composite(content, SWT.NONE);
        config.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        config.setLayout(new GridLayout(1, false));

        createExtractLocationArea(config);
        createTextConfig(config);

        generate = UtilButton.createCheck(Messages.GenerateSheetsConfig, config);
    }

    @Override
    protected void onFinish()
    {
        final Media extractFolder = (Media) folderText.getData();
        final int tw = Integer.parseInt(widthText.getText());
        final int th = Integer.parseInt(heightText.getText());
        final int h = Integer.parseInt(horizontalText.getText());
        final int v = Integer.parseInt(verticalText.getText());

        extractor = new TileExtractor(extractFolder, tw, th, h, v);
        for (final TreeItem item : levelRips.getItems())
        {
            extractor.addRip((Media) item.getData());
        }

        final SheetsImportProgressDialog progress = new SheetsImportProgressDialog(dialog, tw * h, th * v);
        extractor.addListener(progress);
        progress.open();
        extractor.start(progress);
        progress.finish();

        if (!progress.isCanceled())
        {
            if (generate.getSelection())
            {
                generateConfig(tw, th, extractFolder);
            }
            UtilDialog.info(getParent(), Messages.Title, Messages.Finished);
        }
    }
}
