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
import java.util.Collections;

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

import com.b3dgs.lionengine.ImageBuffer;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.Graphics;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.drawable.SpriteTiled;
import com.b3dgs.lionengine.editor.InputValidator;
import com.b3dgs.lionengine.editor.ObjectList;
import com.b3dgs.lionengine.editor.dialog.AbstractDialog;
import com.b3dgs.lionengine.editor.dialog.groups.TileGroupEditDialog;
import com.b3dgs.lionengine.editor.project.Project;
import com.b3dgs.lionengine.editor.utility.UtilButton;
import com.b3dgs.lionengine.editor.utility.UtilDialog;
import com.b3dgs.lionengine.editor.utility.UtilIcon;
import com.b3dgs.lionengine.editor.utility.UtilText;
import com.b3dgs.lionengine.game.map.ConstraintsExtractor;
import com.b3dgs.lionengine.game.map.SheetsExtractor;
import com.b3dgs.lionengine.game.map.TileSheetsConfig;
import com.b3dgs.lionengine.game.map.TransitionsExtractor;
import com.b3dgs.lionengine.game.tile.TileConstraintsConfig;
import com.b3dgs.lionengine.game.tile.TileGroup;
import com.b3dgs.lionengine.game.tile.TileGroupsConfig;
import com.b3dgs.lionengine.game.tile.TileTransitionsConfig;
import com.b3dgs.lionengine.game.tile.TilesExtractor;

/**
 * Import sheets dialog.
 */
public class SheetsImportDialog extends AbstractDialog
{
    /** Sheets default extension. */
    public static final String SHEET_EXTENSION = ".png";
    /** File filter. */
    public static final String FILES_FILTER = "*.bmp;*" + SHEET_EXTENSION;
    /** Level rip filter. */
    public static final String[] LEVEL_RIP_FILTER = new String[]
    {
        FILES_FILTER
    };
    /** Icon. */
    static final Image ICON = UtilIcon.get("dialog", "import.png");

    /** Level rip list. */
    private Tree levelRips;
    /** Tile width. */
    private Text widthText;
    /** Tile height. */
    private Text heightText;
    /** Horizontal tiles. */
    private Text horizontalText;
    /** Add level rip. */
    private Button addLevelRip;
    /** Remove level rip. */
    private Button removeLevelRip;
    /** Extraction destination location. */
    private Text destinationText;

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
        finish.setText(Messages.Next);
        dialog.setMinimumSize(192, 192);
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
            final Media media = project.getResourceMedia(file);
            final String path = media.getPath();
            if (!containsItem(path))
            {
                final TreeItem item = new TreeItem(levelRips, SWT.NONE);
                item.setText(path);
                item.setData(media);

                if (!finish.isEnabled())
                {
                    checkFinish();
                }
            }
        }
    }

    /**
     * Check if tree contains item value.
     * 
     * @param value The value to check.
     * @return <code>true</code> if value is contained by tree, <code>false</code> else.
     */
    private boolean containsItem(String value)
    {
        for (final TreeItem item : levelRips.getItems())
        {
            if (item.getText().equals(value))
            {
                return true;
            }
        }
        return false;
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
            final Media extractFolder = Project.getActive().getResourceMedia(file);
            destinationText.setText(extractFolder.getPath());
        }
    }

    /**
     * Check for finish button enabling.
     */
    private void checkFinish()
    {
        final boolean hasRips = levelRips.getItems().length > 0;
        final boolean hasSize = !widthText.getText().isEmpty() && !heightText.getText().isEmpty();
        final boolean hasNumber = !horizontalText.getText().isEmpty();
        final boolean finished = hasRips && hasSize && hasNumber && !destinationText.getText().isEmpty();

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
        locationLabel.setText(Messages.Destination);

        destinationText = new Text(area, SWT.BORDER);
        destinationText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        destinationText.setEditable(false);
        textCheckFinish(destinationText);

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
        config.setLayout(new GridLayout(3, false));
        config.setText(Messages.Config);

        widthText = createText(Messages.TileWidth, config);
        heightText = createText(Messages.TileHeight, config);
        horizontalText = createText(Messages.HorizontalTiles, config);
    }

    /**
     * Create a checked input text accepting strictly positive values.
     * 
     * @param title The text title.
     * @param parent The composite parent.
     * @return The created checked text.
     */
    private Text createText(String title, Composite parent)
    {
        final Text text = UtilText.create(title, parent);
        ((GridData) text.getLayoutData()).minimumWidth = 24;
        ((GridData) text.getLayoutData()).widthHint = 24;
        text.addVerifyListener(UtilText.createVerify(text, InputValidator.INTEGER_POSITIVE_STRICT_MATCH));
        textCheckFinish(text);
        return text;
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

    /**
     * Extract unique tiles from set of levels depending of tile size.
     * 
     * @param tw The tile width.
     * @param th The tile height.
     * @param levels The levels list.
     * @return The extracted unique tiles, empty list if canceled or if nothing has been found.
     */
    private Collection<ImageBuffer> extractTiles(int tw, int th, Media[] levels)
    {
        final int horizontals = Integer.parseInt(horizontalText.getText());
        final TilesExtractor tilesExtractor = new TilesExtractor();
        final SheetsImportProgressDialog tilesExtractorProgress = new SheetsImportProgressDialog(dialog, horizontals);
        tilesExtractor.addListener(tilesExtractorProgress);
        tilesExtractorProgress.open();

        final Collection<ImageBuffer> tiles = tilesExtractor.extract(tilesExtractorProgress, tw, th, levels);
        tilesExtractorProgress.finish();

        if (tilesExtractorProgress.isCanceled())
        {
            return Collections.emptyList();
        }
        return tiles;
    }

    /**
     * Save extractions data to media file.
     * 
     * @param tw The tile width.
     * @param th The tile height.
     * @param levels The level rips used.
     * @param sheets The sheets list.
     * @param groups The groups list.
     */
    private void saveData(int tw, int th, Media[] levels, Collection<SpriteTiled> sheets, Collection<TileGroup> groups)
    {
        final String folder = destinationText.getText();
        int i = 0;
        for (final SpriteTiled sheet : sheets)
        {
            Graphics.saveImage(sheet.getSurface(), Medias.create(folder, i + SHEET_EXTENSION));
            i++;
        }

        final Media sheetsMedia = Medias.create(folder, TileSheetsConfig.FILENAME);
        TileSheetsConfig.exports(sheetsMedia, tw, th, getTileSheets(sheets));

        final Media groupsMedia = Medias.create(folder, TileGroupsConfig.FILENAME);
        TileGroupsConfig.exports(groupsMedia, groups);

        final Media transitionsMedia = Medias.create(folder, TileTransitionsConfig.FILENAME);
        TileTransitionsConfig.exports(transitionsMedia,
                                      TransitionsExtractor.getTransitions(levels, sheetsMedia, groupsMedia));

        final Media constraintsMedia = Medias.create(folder, TileConstraintsConfig.FILENAME);
        TileConstraintsConfig.export(constraintsMedia, ConstraintsExtractor.getConstraints(levels, sheetsMedia));
    }

    /**
     * Get the current level rips list as array of media.
     * 
     * @return The level rip medias.
     */
    private Media[] getLevelRips()
    {
        final Collection<Media> medias = new ArrayList<>();
        for (final TreeItem item : levelRips.getItems())
        {
            medias.add((Media) item.getData());
        }
        return medias.toArray(new Media[medias.size()]);
    }

    /**
     * Get the tile sheets list.
     * 
     * @param sheets The sheets image.
     * @return The sheets name list.
     */
    private Collection<String> getTileSheets(Collection<SpriteTiled> sheets)
    {
        final Collection<String> sheetsName = new ArrayList<>();
        final int size = sheets.size();
        for (int i = 0; i < size; i++)
        {
            sheetsName.add(i + SHEET_EXTENSION);
        }
        return sheetsName;

    }

    /*
     * AbstractDialog
     */

    @Override
    protected void createContent(Composite content)
    {
        content.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        final Group area = new Group(content, SWT.NONE);
        area.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        area.setLayout(new GridLayout(1, false));
        area.setText(Messages.RipsList);

        levelRips = new Tree(area, SWT.SINGLE);
        levelRips.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        final Composite buttons = new Composite(area, SWT.NONE);
        buttons.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        buttons.setLayout(new GridLayout(3, false));

        final Label label = new Label(buttons, SWT.NONE);
        label.setText(Messages.AddRemoveRip);

        createButtonAdd(buttons);
        createButtonRemove(buttons);

        createTextConfig(content);
        createExtractLocationArea(content);
    }

    @Override
    protected void onFinish()
    {
        final int tw = Integer.parseInt(widthText.getText());
        final int th = Integer.parseInt(heightText.getText());
        final Media[] levels = getLevelRips();

        final Collection<ImageBuffer> tiles = extractTiles(tw, th, levels);
        if (!tiles.isEmpty())
        {
            final Collection<SpriteTiled> sheets = SheetsExtractor.extract(tiles, 16);
            final TileGroupEditDialog tileGroupEdit = new TileGroupEditDialog(getParent());
            tileGroupEdit.load(tw, th, sheets, levels);
            tileGroupEdit.open();

            if (!tileGroupEdit.isCanceled())
            {
                saveData(tw, th, levels, sheets, tileGroupEdit.getGroups());
            }
        }
    }
}
