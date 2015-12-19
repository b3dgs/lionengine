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
package com.b3dgs.lionengine.editor.dialog.map.imports;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.b3dgs.lionengine.UtilFile;
import com.b3dgs.lionengine.editor.dialog.AbstractDialog;
import com.b3dgs.lionengine.editor.dialog.widget.BrowseWidget;
import com.b3dgs.lionengine.editor.dialog.widget.LevelRipsWidget;
import com.b3dgs.lionengine.editor.utility.UtilIcon;
import com.b3dgs.lionengine.editor.world.WorldModel;
import com.b3dgs.lionengine.game.map.LevelRipConverter;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.map.MapTileGroup;
import com.b3dgs.lionengine.game.map.TileSheetsConfig;
import com.b3dgs.lionengine.game.tile.TileGroupsConfig;

/**
 * Represents the import map dialog.
 */
public class MapImportDialog extends AbstractDialog
{
    /** Icon. */
    public static final Image ICON = UtilIcon.get("dialog", "import.png");

    /** Level rip location. */
    private BrowseWidget levelRip;
    /** Sheets location. */
    private BrowseWidget sheets;
    /** Groups location. */
    private BrowseWidget groups;

    /**
     * Create an import map dialog.
     * 
     * @param parent The shell parent.
     */
    public MapImportDialog(Shell parent)
    {
        super(parent, Messages.Title, Messages.HeaderTitle, Messages.HeaderDesc, ICON);
        createDialog();
        dialog.setMinimumSize(512, 160);
        finish.setEnabled(false);
        finish.forceFocus();
    }

    /**
     * Check if can enable finish button.
     */
    private void checkFinish()
    {
        finish.setEnabled(levelRip.getMedia() != null && sheets.getMedia() != null && groups.getMedia() != null);
    }

    /*
     * AbstractDialog
     */

    @Override
    protected void createContent(Composite content)
    {
        levelRip = new BrowseWidget(content,
                                    Messages.LevelRipLocation,
                                    Messages.LevelRipFileFilter,
                                    LevelRipsWidget.LEVEL_RIP_FILTER,
                                    true);
        levelRip.addListener(media ->
        {
            if (sheets.getMedia() == null)
            {
                sheets.setLocation(UtilFile.getPath(media.getParentPath(), TileSheetsConfig.FILENAME));
            }
            if (groups.getMedia() == null)
            {
                groups.setLocation(UtilFile.getPath(media.getParentPath(), TileGroupsConfig.FILENAME));
            }
            checkFinish();
        });

        sheets = new BrowseWidget(content, Messages.SheetsLocation, Messages.SheetsConfigFileFilter, false);
        sheets.addListener(media -> checkFinish());

        groups = new BrowseWidget(content, Messages.GroupsLocation, Messages.GroupsConfigFileFilter, false);
        groups.addListener(media -> checkFinish());
    }

    @Override
    protected void onFinish()
    {
        final MapTile map = WorldModel.INSTANCE.getMap();
        map.loadSheets(sheets.getMedia());

        final MapImportProgressDialog progress = new MapImportProgressDialog(dialog, levelRip.getMedia());
        progress.open();

        LevelRipConverter.start(levelRip.getMedia(), map, progress);
        progress.finish();

        if (groups.getMedia().exists())
        {
            map.getFeature(MapTileGroup.class).loadGroups(groups.getMedia());
        }
    }
}
