/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.editor.map.sheet.extract;

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

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.Graphics;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.drawable.SpriteTiled;
import com.b3dgs.lionengine.editor.dialog.AbstractDialog;
import com.b3dgs.lionengine.editor.utility.UtilIcon;
import com.b3dgs.lionengine.editor.validator.InputValidator;
import com.b3dgs.lionengine.editor.widget.BrowseWidget;
import com.b3dgs.lionengine.editor.widget.TextWidget;
import com.b3dgs.lionengine.editor.widget.levelrip.LevelRipWidget;
import com.b3dgs.lionengine.editor.widget.levelrip.LevelRipWidget.LevelRipsWidgetListener;
import com.b3dgs.lionengine.game.map.SheetsExtractor;
import com.b3dgs.lionengine.game.map.TileSheetsConfig;
import com.b3dgs.lionengine.game.tile.TilesExtractor;
import com.b3dgs.lionengine.graphic.ImageBuffer;

/**
 * Extract sheets dialog.
 */
public class SheetsExtractDialog extends AbstractDialog
{
    /** Sheets default extension. */
    public static final String SHEET_EXTENSION = ".png";
    /** File filter. */
    public static final String FILES_FILTER = "*.bmp;*" + SHEET_EXTENSION;
    /** Icon. */
    private static final Image ICON = UtilIcon.get("dialog", "import.png");

    /** Level rips widget. */
    private LevelRipWidget levelRips;
    /** Tile width. */
    private TextWidget tileWidth;
    /** Tile height. */
    private TextWidget tileHeight;
    /** Horizontal tiles. */
    private TextWidget horizontalTiles;
    /** Extraction destination location. */
    private BrowseWidget destination;
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
            TileSheetsConfig.exports(sheetsMedia, tileWidth.getValue(), tileHeight.getValue(), getSheetNames());
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
        return new TileSheetsConfig(tileWidth.getValue(), tileHeight.getValue(), getSheetNames());
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
    void checkFinish()
    {
        final boolean hasRips = levelRips.getLevelRips().length > 0;
        final boolean hasSize = !tileWidth.isEmpty() && !tileHeight.isEmpty();
        final boolean finished = hasRips && hasSize && !horizontalTiles.isEmpty() && destination.getMedia() != null;

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

        tileWidth = new TextWidget(config, Messages.TileWidth, InputValidator.INTEGER_POSITIVE_STRICT_MATCH);
        tileWidth.addListener(text -> checkFinish());

        tileHeight = new TextWidget(config, Messages.TileHeight, InputValidator.INTEGER_POSITIVE_STRICT_MATCH);
        tileHeight.addListener(text -> checkFinish());

        horizontalTiles = new TextWidget(config,
                                         Messages.HorizontalTiles,
                                         InputValidator.INTEGER_POSITIVE_STRICT_MATCH);
        horizontalTiles.addListener(text -> checkFinish());
    }

    /**
     * Extract unique tiles from set of levels depending of tile size.
     * 
     * @return The extracted unique tiles, empty list if canceled or if nothing has been found.
     */
    private Collection<ImageBuffer> getTiles()
    {
        final TilesExtractor tilesExtractor = new TilesExtractor();
        final SheetsExtractProgressDialog progress = new SheetsExtractProgressDialog(dialog,
                                                                                     horizontalTiles.getValue());
        tilesExtractor.addListener(progress);
        progress.open();

        final int tw = tileWidth.getValue();
        final int th = tileHeight.getValue();
        final Collection<ImageBuffer> tiles = tilesExtractor.extract(progress, tw, th, getLevelRips());
        progress.finish();
        if (!progress.isCanceled())
        {
            return tiles;
        }
        return Collections.emptyList();
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

        levelRips = new LevelRipWidget(content);
        levelRips.addListener(new LevelRipsWidgetListener()
        {
            @Override
            public void notifyLevelRipRemoved(Media media)
            {
                checkFinish();
            }

            @Override
            public void notifyLevelRipAdded(Media media)
            {
                checkFinish();
            }
        });

        createTextConfig(content);
        destination = new BrowseWidget(content, Messages.Destination);
        destination.addListener(media -> checkFinish());
    }

    @Override
    protected void onFinish()
    {
        sheets = SheetsExtractor.extract(getTiles(), horizontalTiles.getValue());
    }
}
