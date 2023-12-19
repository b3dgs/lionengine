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
package com.b3dgs.lionengine.editor.map.group.menu;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.editor.dialog.DialogAbstract;
import com.b3dgs.lionengine.editor.utility.UtilIcon;
import com.b3dgs.lionengine.editor.utility.control.UtilButton;
import com.b3dgs.lionengine.editor.utility.dialog.UtilDialog;
import com.b3dgs.lionengine.editor.widget.BrowseWidget;
import com.b3dgs.lionengine.editor.widget.levelrip.LevelRipWidget;
import com.b3dgs.lionengine.editor.widget.levelrip.LevelRipWidget.LevelRipsWidgetListener;
import com.b3dgs.lionengine.game.feature.tile.map.TileSheetsConfig;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.SpriteTiled;

/**
 * Edit map tile groups dialog.
 */
public class GroupsEditDialog extends DialogAbstract
{
    /** Icon. */
    private static final Image ICON = UtilIcon.get("dialog", "groups-edit.png");
    /** Dialog width. */
    private static final int DIALOG_WIDTH = 640;
    /** Dialog height. */
    private static final int DIALOG_HEIGHT = 448;

    /**
     * Load sheets from configuration file.
     * 
     * @param config The configuration file.
     * @param folder The folder root.
     * @return The loaded sheets.
     */
    public static List<SpriteTiled> loadSheets(TileSheetsConfig config, String folder)
    {
        final int tw = config.getTileWidth();
        final int th = config.getTileHeight();
        final List<String> configSheets = config.getSheets();
        final List<SpriteTiled> sheets = new ArrayList<>(configSheets.size());
        for (final String sheet : configSheets)
        {
            final Media media = Medias.create(folder, sheet);
            final SpriteTiled surface = Drawable.loadSpriteTiled(media, tw, th);
            surface.load();
            surface.prepare();
            sheets.add(surface);
        }
        configSheets.clear();
        return sheets;
    }

    /** Part service. */
    private final EPartService partService;
    /** Level rips widget. */
    private LevelRipWidget levelRips;
    /** Next button. */
    private Button next;
    /** Sheets config. */
    private BrowseWidget sheets;

    /**
     * Create the dialog.
     * 
     * @param partService The part service.
     * @param parent The parent reference.
     */
    public GroupsEditDialog(EPartService partService, Shell parent)
    {
        super(parent, Messages.Title, Messages.HeaderTitle, Messages.HeaderDesc, ICON, SWT.SHELL_TRIM);

        this.partService = partService;

        createDialog();
        dialog.setMinimumSize(DIALOG_WIDTH, DIALOG_HEIGHT);
        finish.setEnabled(false);
    }

    /**
     * Set the save folder destination.
     * 
     * @param destination The destination folder.
     */
    public void setLocation(String destination)
    {
        sheets.setLocation(destination);
    }

    /**
     * Create the levels chooser area.
     * 
     * @param parent The parent composite.
     * @return The created area.
     */
    private Composite createLevelsArea(Composite parent)
    {
        final Composite levelsArea = new Composite(parent, SWT.NONE);
        levelsArea.setLayout(new GridLayout(1, false));
        levelsArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        levelRips = new LevelRipWidget(levelsArea);
        levelRips.addListener(new LevelRipsWidgetListener()
        {
            @Override
            public void notifyLevelRipAdded(Media media)
            {
                autofillSheetsConfig(media);
                checkNextEnabled();
            }

            @Override
            public void notifyLevelRipRemoved(Media media)
            {
                checkNextEnabled();
            }
        });

        sheets = new BrowseWidget(levelsArea,
                                  com.b3dgs.lionengine.editor.map.imports.Messages.SheetsLocation,
                                  UtilDialog.getXmlFilter(),
                                  true);
        sheets.addListener(media -> checkNextEnabled());
        return levelsArea;
    }

    /**
     * Check for auto fill sheets config.
     * 
     * @param media The media reference.
     */
    private void autofillSheetsConfig(Media media)
    {
        if (sheets.getMedia() == null)
        {
            final Media sheetsConfig = Medias.create(media.getParentPath(), TileSheetsConfig.FILENAME);
            if (sheetsConfig.exists())
            {
                sheets.setLocation(sheetsConfig.getPath());
            }
        }
    }

    /**
     * Check if button next can be enabled or not.
     */
    private void checkNextEnabled()
    {
        next.setEnabled(!levelRips.getLevelRips().isEmpty() && sheets.getMedia() != null);
    }

    /**
     * Go to next step to assign groups.
     */
    private void next()
    {
        final Media sheetsMedia = sheets.getMedia();
        final TileSheetsConfig config = TileSheetsConfig.imports(sheetsMedia);
        final GroupsAssignDialog assign = new GroupsAssignDialog(partService, dialog);
        final String folderPath = sheetsMedia.getParentPath();
        final List<SpriteTiled> sheetsLoaded = loadSheets(config, folderPath);
        if (sheetsLoaded.isEmpty())
        {
            UtilDialog.error(getParent(), Messages.ErrorSheet_Title, Messages.ErrorSheet_Message);
        }
        else
        {
            assign.load(sheetsLoaded, levelRips.getLevelRips());
            assign.open();
            assign.setLocation(folderPath);
            assign.save();
        }
        close();
    }

    /*
     * AbstractDialog
     */

    @Override
    protected void createContent(Composite content)
    {
        content.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        final Composite levelsArea = createLevelsArea(content);
        next = UtilButton.create(levelsArea, com.b3dgs.lionengine.editor.dialog.Messages.Next, DialogAbstract.ICON_OK);
        next.setEnabled(false);
        UtilButton.setAction(next, this::next);
    }

    @Override
    protected void onFinish()
    {
        // Nothing to do
    }
}
