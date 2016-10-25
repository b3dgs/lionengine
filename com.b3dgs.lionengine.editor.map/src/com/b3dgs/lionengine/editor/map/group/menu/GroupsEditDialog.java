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
package com.b3dgs.lionengine.editor.map.group.menu;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.SpriteTiled;
import com.b3dgs.lionengine.editor.dialog.AbstractDialog;
import com.b3dgs.lionengine.editor.utility.UtilIcon;
import com.b3dgs.lionengine.editor.utility.control.UtilButton;
import com.b3dgs.lionengine.editor.utility.dialog.UtilDialog;
import com.b3dgs.lionengine.editor.widget.BrowseWidget;
import com.b3dgs.lionengine.editor.widget.levelrip.LevelRipWidget;
import com.b3dgs.lionengine.editor.widget.levelrip.LevelRipWidget.LevelRipsWidgetListener;
import com.b3dgs.lionengine.game.map.TileSheetsConfig;

/**
 * Edit map tile groups dialog.
 */
public class GroupsEditDialog extends AbstractDialog
{
    /** Icon. */
    private static final Image ICON = UtilIcon.get("dialog", "groups-edit.png");

    /**
     * Load sheets from configuration file.
     * 
     * @param config The configuration file.
     * @param folder The folder root.
     * @return The loaded sheets.
     */
    public static Collection<SpriteTiled> loadSheets(TileSheetsConfig config, String folder)
    {
        final int tw = config.getTileWidth();
        final int th = config.getTileHeight();
        final Collection<SpriteTiled> sheets = new ArrayList<>();
        for (final String sheet : config.getSheets())
        {
            final Media media = Medias.create(folder, sheet);
            final SpriteTiled surface = Drawable.loadSpriteTiled(media, tw, th);
            surface.load();
            surface.prepare();
            sheets.add(surface);
        }
        return sheets;
    }

    /** Level rips widget. */
    private LevelRipWidget levelRips;
    /** Next button. */
    private Button next;
    /** Sheets config. */
    private BrowseWidget sheets;

    /**
     * Create the dialog.
     * 
     * @param parent The parent reference.
     */
    public GroupsEditDialog(Shell parent)
    {
        super(parent, Messages.Title, Messages.HeaderTitle, Messages.HeaderDesc, ICON, SWT.SHELL_TRIM);

        createDialog();
        dialog.setMinimumSize(640, 448);
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
        final GroupsAssignDialog assign = new GroupsAssignDialog(dialog);
        final String folderPath = sheetsMedia.getParentPath();
        final Collection<SpriteTiled> sheets = loadSheets(config, folderPath);
        if (sheets.isEmpty())
        {
            UtilDialog.error(getParent(), Messages.ErrorSheet_Title, Messages.ErrorSheet_Message);
        }
        else
        {
            assign.load(sheets, levelRips.getLevelRips());
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
        next = UtilButton.create(levelsArea, com.b3dgs.lionengine.editor.dialog.Messages.Next, AbstractDialog.ICON_OK);
        next.setEnabled(false);
        UtilButton.setAction(next, this::next);
    }

    @Override
    protected void onFinish()
    {
        // Nothing to do
    }
}
