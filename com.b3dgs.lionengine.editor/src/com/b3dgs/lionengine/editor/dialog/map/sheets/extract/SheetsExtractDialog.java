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
package com.b3dgs.lionengine.editor.dialog.map.sheets.extract;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.b3dgs.lionengine.ImageBuffer;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.Graphics;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.drawable.SpriteTiled;
import com.b3dgs.lionengine.editor.InputValidator;
import com.b3dgs.lionengine.editor.dialog.AbstractDialog;
import com.b3dgs.lionengine.editor.dialog.BrowseWidget;
import com.b3dgs.lionengine.editor.dialog.BrowseWidget.BrowseWidgetListener;
import com.b3dgs.lionengine.editor.dialog.LevelRipsWidget;
import com.b3dgs.lionengine.editor.dialog.LevelRipsWidget.LevelRipsWidgetListener;
import com.b3dgs.lionengine.editor.utility.UtilIcon;
import com.b3dgs.lionengine.editor.utility.UtilText;
import com.b3dgs.lionengine.game.map.SheetsExtractor;
import com.b3dgs.lionengine.game.map.TileSheetsConfig;
import com.b3dgs.lionengine.game.tile.TilesExtractor;

/**
 * Extract sheets dialog.
 */
public class SheetsExtractDialog extends AbstractDialog implements LevelRipsWidgetListener, BrowseWidgetListener
{
    /** Sheets default extension. */
    public static final String SHEET_EXTENSION = ".png";
    /** File filter. */
    public static final String FILES_FILTER = "*.bmp;*" + SHEET_EXTENSION;
    /** Icon. */
    static final Image ICON = UtilIcon.get("dialog", "import.png");

    /** Level rips widget. */
    private LevelRipsWidget levelRips;
    /** Tile width. */
    private Text widthText;
    /** Tile height. */
    private Text heightText;
    /** Horizontal tiles. */
    private Text horizontalText;
    /** Extraction destination location. */
    private BrowseWidget destination;
    /** Tile width value. */
    private int tw;
    /** Tile height value. */
    private int th;
    /** Horizontal tiles value. */
    private int horizontalTiles;
    /** Extraction cache. */
    private Collection<SpriteTiled> sheets;

    /**
     * Create the dialog.
     * 
     * @param parent The parent reference.
     */
    public SheetsExtractDialog(Shell parent)
    {
        super(parent, Messages.Title, Messages.HeaderTitle, Messages.HeaderDesc, ICON);
        createDialog();
        setTipsMessage(ICON_ERROR, Messages.NoLevelRipDefined);
        tipsLabel.setVisible(true);
        dialog.setMinimumSize(192, 192);
    }

    /**
     * Save extractions data to media file.
     */
    public void save()
    {
        if (destination.getMedia() != null)
        {
            final String folder = getFolder();
            int i = 0;
            for (final SpriteTiled sheet : getSheets())
            {
                Graphics.saveImage(sheet.getSurface(), Medias.create(folder, i + SHEET_EXTENSION));
                i++;
            }

            final Media sheetsMedia = Medias.create(folder, TileSheetsConfig.FILENAME);
            TileSheetsConfig.exports(sheetsMedia, tw, th, getSheetNames());
        }
    }

    /**
     * Get folder destination.
     * 
     * @return The folder destination.
     */
    public String getFolder()
    {
        return destination.getMedia().getPath();
    }

    /**
     * Get the current level rips list as array of media.
     * 
     * @return The level rip medias.
     */
    public Media[] getLevelRips()
    {
        return levelRips.getLevelRips();
    }

    /**
     * Get the sheets config.
     * 
     * @return The sheets config.
     */
    public TileSheetsConfig getConfig()
    {
        return new TileSheetsConfig(tw, th, getSheetNames());
    }

    /**
     * Get the extracted tile sheets.
     * 
     * @return The extracted tile sheets.
     */
    public Collection<SpriteTiled> getSheets()
    {
        if (sheets == null)
        {
            return Collections.emptySet();
        }
        return sheets;
    }

    /**
     * Check for finish button enabling.
     */
    private void checkFinish()
    {
        final boolean hasRips = levelRips.getLevelRips().length > 0;
        final boolean hasSize = !widthText.getText().isEmpty() && !heightText.getText().isEmpty();
        final boolean hasNumber = !horizontalText.getText().isEmpty();
        final boolean finished = hasRips && hasSize && hasNumber && destination.getMedia() != null;

        finish.setEnabled(finished);
        tipsLabel.setVisible(!finished);
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
     * @return The extracted unique tiles, empty list if canceled or if nothing has been found.
     */
    private Collection<ImageBuffer> getTiles()
    {
        final TilesExtractor tilesExtractor = new TilesExtractor();
        final SheetsExtractProgressDialog progress = new SheetsExtractProgressDialog(dialog, horizontalTiles);
        tilesExtractor.addListener(progress);
        progress.open();

        final Collection<ImageBuffer> tiles = tilesExtractor.extract(progress, tw, th, getLevelRips());
        progress.finish();

        if (progress.isCanceled())
        {
            return Collections.emptyList();
        }
        return tiles;
    }

    /**
     * Get the tile sheets list.
     * 
     * @return The sheets name list.
     */
    private Collection<String> getSheetNames()
    {
        final Collection<String> sheetsName = new ArrayList<>();
        final int size = getSheets().size();
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

        levelRips = new LevelRipsWidget(content);
        levelRips.addListener(this);

        createTextConfig(content);
        destination = new BrowseWidget(content, Messages.Destination);
        destination.addListener(this);
    }

    @Override
    protected void onFinish()
    {
        tw = Integer.parseInt(widthText.getText());
        th = Integer.parseInt(heightText.getText());
        horizontalTiles = Integer.parseInt(horizontalText.getText());

        sheets = SheetsExtractor.extract(getTiles(), horizontalTiles);

        levelRips.removeListener(this);
        destination.removeListener(this);
    }

    /*
     * LevelRipsWidgetListener
     */

    @Override
    public void notifyLevelRipAdded(Media media)
    {
        checkFinish();
    }

    @Override
    public void notifyLevelRipRemoved(Media media)
    {
        checkFinish();
    }

    /*
     * BrowseWidgetListener
     */

    @Override
    public void notifyMediaSelected(Media media)
    {
        checkFinish();
    }
}
