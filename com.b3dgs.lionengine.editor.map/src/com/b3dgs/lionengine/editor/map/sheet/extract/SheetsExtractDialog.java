/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.editor.map.sheet.extract;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.editor.dialog.DialogAbstract;
import com.b3dgs.lionengine.editor.utility.UtilIcon;
import com.b3dgs.lionengine.editor.validator.InputValidator;
import com.b3dgs.lionengine.editor.widget.BrowseWidget;
import com.b3dgs.lionengine.editor.widget.TextWidget;
import com.b3dgs.lionengine.editor.widget.levelrip.LevelRipWidget;
import com.b3dgs.lionengine.editor.widget.levelrip.LevelRipWidget.LevelRipsWidgetListener;
import com.b3dgs.lionengine.game.feature.tile.TilesExtractor;
import com.b3dgs.lionengine.game.feature.tile.map.SheetsExtractor;
import com.b3dgs.lionengine.game.feature.tile.map.TileSheetsConfig;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.ImageBuffer;
import com.b3dgs.lionengine.graphic.drawable.SpriteTiled;

/**
 * Extract sheets dialog.
 */
// CHECKSTYLE IGNORE LINE: DataAbstractionCoupling
public class SheetsExtractDialog extends DialogAbstract
{
    /** Sheets default extension. */
    public static final String SHEET_EXTENSION = ".png";
    /** Icon. */
    private static final Image ICON = UtilIcon.get("dialog", "sheets-extract.png");
    /** Dialog width. */
    private static final int DIALOG_MIN_WIDTH = 192;
    /** Dialog height. */
    private static final int DIALOG_MIN_HEIGHT = 192;

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
    private List<SpriteTiled> sheets;

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
        dialog.setMinimumSize(DIALOG_MIN_WIDTH, DIALOG_MIN_HEIGHT);
    }

    /**
     * Save extractions data to media file.
     */
    public void save()
    {
        if (destination.getMedia() != null && tileWidth.getValue().isPresent() && tileHeight.getValue().isPresent())
        {
            final String folder = getFolder();
            int i = 0;
            for (final SpriteTiled sheet : getSheets())
            {
                Graphics.saveImage(sheet.getSurface(), Medias.create(folder, i + SHEET_EXTENSION));
                i++;
            }

            final Media sheetsMedia = Medias.create(folder, TileSheetsConfig.FILENAME);
            TileSheetsConfig.exports(sheetsMedia,
                                     tileWidth.getValue().getAsInt(),
                                     tileHeight.getValue().getAsInt(),
                                     getSheetNames());
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
    public Collection<Media> getLevelRips()
    {
        return levelRips.getLevelRips();
    }

    /**
     * Get the extracted tile sheets.
     * 
     * @return The extracted tile sheets.
     */
    public List<SpriteTiled> getSheets()
    {
        if (sheets == null)
        {
            return Collections.emptyList();
        }
        return sheets;
    }

    /**
     * Check for finish button enabling.
     */
    void checkFinish()
    {
        final boolean hasRips = !levelRips.getLevelRips().isEmpty();
        final boolean hasSize = tileWidth.getValue().isPresent() && tileHeight.getValue().isPresent();
        final boolean finished = hasRips && hasSize && destination.getMedia() != null;

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
        final int items = 3;
        config.setLayout(new GridLayout(items, false));
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
        final SheetsExtractProgressDialog progress = new SheetsExtractProgressDialog(dialog, getHorizontalTiles());
        tilesExtractor.addListener(progress);
        progress.open();

        final int tw = tileWidth.getValue().getAsInt();
        final int th = tileHeight.getValue().getAsInt();
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
    private List<String> getSheetNames()
    {
        final int size = getSheets().size();
        final List<String> sheetsName = new ArrayList<>(size);
        for (int i = 0; i < size; i++)
        {
            sheetsName.add(i + SHEET_EXTENSION);
        }
        return sheetsName;
    }

    /**
     * Get the horizontal tiles value.
     * 
     * @return Horizontal tiles value.
     */
    private int getHorizontalTiles()
    {
        return horizontalTiles.getValue().orElse(0);
    }

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
                if (destination.getMedia() == null)
                {
                    destination.setLocation(media.getParentPath());
                }
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
        sheets = SheetsExtractor.extract(getTiles(), getHorizontalTiles());
    }
}
