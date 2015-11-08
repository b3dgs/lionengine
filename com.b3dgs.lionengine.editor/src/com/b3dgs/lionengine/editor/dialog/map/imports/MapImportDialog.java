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

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.editor.dialog.AbstractDialog;
import com.b3dgs.lionengine.editor.dialog.sheets.imports.SheetsImportDialog;
import com.b3dgs.lionengine.editor.project.Project;
import com.b3dgs.lionengine.editor.utility.UtilButton;
import com.b3dgs.lionengine.editor.utility.UtilDialog;
import com.b3dgs.lionengine.editor.utility.UtilIcon;
import com.b3dgs.lionengine.editor.world.WorldModel;
import com.b3dgs.lionengine.game.map.LevelRipConverter;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.tile.ConfigTileGroups;
import com.b3dgs.lionengine.game.tile.ConfigTileSheets;

/**
 * Represents the import map dialog.
 */
public class MapImportDialog extends AbstractDialog
{
    /** Icon. */
    public static final Image ICON = UtilIcon.get("dialog", "import.png");

    /** Level rip location. */
    private Text levelRipLocationText;
    /** Sheets location. */
    private Text sheetsLocationText;
    /** Groups location. */
    private Text groupsLocationText;
    /** Level rip file. */
    private Media levelRip;
    /** Sheets config file. */
    private Media sheetsConfig;
    /** Groups config file. */
    private Media groupsConfig;

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
     * Update the tips label.
     */
    private void updateTipsLabel()
    {
        tipsLabel.setVisible(false);
    }

    /**
     * Browse the level rip location.
     */
    private void browseLevelRipLocation()
    {
        final File file = UtilDialog.selectResourceFile(dialog, true, new String[]
        {
            Messages.LevelRipFileFilter
        }, SheetsImportDialog.LEVEL_RIP_FILTER);
        if (file != null)
        {
            onLevelRipLocationSelected(file);
        }
    }

    /**
     * Browse the sheets location.
     */
    private void browseSheetsLocation()
    {
        final File file = UtilDialog.selectResourceXml(dialog, false, Messages.SheetsConfigFileFilter);
        if (file != null)
        {
            onSheetsConfigLocationSelected(file);
        }
    }

    /**
     * Browse the groups location.
     */
    private void browseGroupsLocation()
    {
        final File file = UtilDialog.selectResourceXml(dialog, false, Messages.GroupsConfigFileFilter);
        if (file != null)
        {
            onGroupsConfigLocationSelected(file);
        }
    }

    /**
     * Called when the level rip location has been selected.
     * 
     * @param path The level rip location path.
     */
    private void onLevelRipLocationSelected(File path)
    {
        final Project project = Project.getActive();
        try
        {
            levelRip = project.getResourceMedia(new File(path.getAbsolutePath()));
            levelRipLocationText.setText(levelRip.getPath());
        }
        catch (final LionEngineException exception)
        {
            setTipsMessage(AbstractDialog.ICON_ERROR, Messages.ErrorLevelRip);
        }
        loadDefaults();
        updateTipsLabel();
        finish.setEnabled(levelRip != null && sheetsConfig != null && groupsConfig != null);
    }

    /**
     * Called when the sheets config location has been selected.
     * 
     * @param path The selected sheets config location path.
     */
    private void onSheetsConfigLocationSelected(File path)
    {
        final Project project = Project.getActive();
        boolean validSheets = false;
        try
        {
            sheetsConfig = project.getResourceMedia(new File(path.getAbsolutePath()));
            sheetsLocationText.setText(sheetsConfig.getPath());
            final File sheets = sheetsConfig.getFile();
            if (!sheets.isFile())
            {
                setTipsMessage(AbstractDialog.ICON_ERROR, Messages.ErrorSheets);
            }
            validSheets = sheets.isFile();
        }
        catch (final LionEngineException exception)
        {
            setTipsMessage(AbstractDialog.ICON_ERROR, Messages.ErrorSheets);
        }
        updateTipsLabel();

        final boolean isValid = levelRip != null && sheetsConfig != null && validSheets;
        finish.setEnabled(isValid);
    }

    /**
     * Called when the groups config location has been selected.
     * 
     * @param path The selected groups config location path.
     */
    private void onGroupsConfigLocationSelected(File path)
    {
        final Project project = Project.getActive();
        boolean validGroups = false;
        try
        {
            groupsConfig = project.getResourceMedia(new File(path.getAbsolutePath()));
            groupsLocationText.setText(groupsConfig.getPath());
            final File groups = groupsConfig.getFile();
            if (!groups.isFile())
            {
                setTipsMessage(AbstractDialog.ICON_ERROR, Messages.ErrorGroups);
            }
            validGroups = groups.isFile();
        }
        catch (final LionEngineException exception)
        {
            setTipsMessage(AbstractDialog.ICON_ERROR, Messages.ErrorGroups);
        }
        updateTipsLabel();

        final boolean isValid = levelRip != null && groupsConfig != null && validGroups;
        finish.setEnabled(isValid);
    }

    /**
     * Create the level rip location area.
     * 
     * @param content The content composite.
     */
    private void createLevelRipLocationArea(Composite content)
    {
        final Composite levelRipArea = new Composite(content, SWT.NONE);
        levelRipArea.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        levelRipArea.setLayout(new GridLayout(3, false));

        final Label locationLabel = new Label(levelRipArea, SWT.NONE);
        locationLabel.setText(Messages.LevelRipLocation);

        levelRipLocationText = new Text(levelRipArea, SWT.BORDER);
        levelRipLocationText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        levelRipLocationText.setEditable(false);

        final Button browse = UtilButton.createBrowse(levelRipArea);
        UtilButton.setAction(browse, () -> browseLevelRipLocation());
    }

    /**
     * Create the sheets location area.
     * 
     * @param content The content composite.
     */
    private void createSheetsLocationArea(Composite content)
    {
        final Composite sheetArea = new Composite(content, SWT.NONE);
        sheetArea.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        sheetArea.setLayout(new GridLayout(3, false));

        final Label locationLabel = new Label(sheetArea, SWT.NONE);
        locationLabel.setText(Messages.SheetsLocation);

        sheetsLocationText = new Text(sheetArea, SWT.BORDER);
        sheetsLocationText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        sheetsLocationText.setEditable(false);

        final Button browse = UtilButton.createBrowse(sheetArea);
        UtilButton.setAction(browse, () -> browseSheetsLocation());
    }

    /**
     * Create the groups location area.
     * 
     * @param content The content composite.
     */
    private void createGroupsLocationArea(Composite content)
    {
        final Composite groupArea = new Composite(content, SWT.NONE);
        groupArea.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        groupArea.setLayout(new GridLayout(3, false));

        final Label locationLabel = new Label(groupArea, SWT.NONE);
        locationLabel.setText(Messages.GroupsLocation);

        groupsLocationText = new Text(groupArea, SWT.BORDER);
        groupsLocationText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        groupsLocationText.setEditable(false);

        final Button browse = UtilButton.createBrowse(groupArea);
        UtilButton.setAction(browse, () -> browseGroupsLocation());
    }

    /**
     * Load default sheets and config file.
     */
    private void loadDefaults()
    {
        if (sheetsConfig == null)
        {
            final File defaultSheetFile = new File(levelRip.getFile().getParentFile(), ConfigTileSheets.FILENAME);
            if (defaultSheetFile.isFile())
            {
                onSheetsConfigLocationSelected(defaultSheetFile);
            }
        }
        if (groupsConfig == null)
        {
            final File defaultGroupsFile = new File(levelRip.getFile().getParentFile(), ConfigTileGroups.FILENAME);
            if (defaultGroupsFile.isFile())
            {
                onGroupsConfigLocationSelected(defaultGroupsFile);
            }
        }
    }

    /*
     * AbstractDialog
     */

    @Override
    protected void createContent(Composite content)
    {
        createLevelRipLocationArea(content);
        createSheetsLocationArea(content);
        createGroupsLocationArea(content);
    }

    @Override
    protected void onFinish()
    {
        final MapTile map = WorldModel.INSTANCE.getMap();
        final MapImportProgressDialog progress = new MapImportProgressDialog(dialog, levelRip);

        progress.open();
        LevelRipConverter.start(levelRip, sheetsConfig, map, progress);
        progress.finish();

        map.loadGroups(groupsConfig);
    }
}
