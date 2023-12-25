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
package com.b3dgs.lionengine.editor.map.constaint;

import java.util.Collection;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.UtilFolder;
import com.b3dgs.lionengine.editor.dialog.DialogAbstract;
import com.b3dgs.lionengine.editor.utility.UtilIcon;
import com.b3dgs.lionengine.editor.utility.dialog.UtilDialog;
import com.b3dgs.lionengine.editor.widget.BrowseWidget;
import com.b3dgs.lionengine.editor.widget.levelrip.LevelRipWidget;
import com.b3dgs.lionengine.editor.widget.levelrip.LevelRipWidget.LevelRipsWidgetListener;
import com.b3dgs.lionengine.game.feature.tile.TileGroupsConfig;
import com.b3dgs.lionengine.game.feature.tile.map.TileSheetsConfig;
import com.b3dgs.lionengine.game.feature.tile.map.transition.TransitionsConfig;
import com.b3dgs.lionengine.game.feature.tile.map.transition.circuit.CircuitsConfig;

/**
 * Represents the export map tile constraints dialog.
 */
public class ConstraintsExtractDialog extends DialogAbstract
{
    /** Icon. */
    private static final Image ICON = UtilIcon.get("dialog", "constraints-extract.png");
    /** Dialog width. */
    private static final int DIALOG_WIDTH = 512;
    /** Dialog height. */
    private static final int DIALOG_HEIGHT = 160;

    /** Level rip list. */
    private LevelRipWidget levelRips;
    /** Sheets location. */
    private BrowseWidget sheets;
    /** Groups location. */
    private BrowseWidget groups;
    /** Transitions location. */
    private BrowseWidget transitions;
    /** Circuits location. */
    private BrowseWidget circuits;

    /**
     * Create an export map tile constraints dialog.
     * 
     * @param parent The shell parent.
     */
    public ConstraintsExtractDialog(Shell parent)
    {
        super(parent, Messages.Title, Messages.HeaderTitle, Messages.HeaderDesc, ICON);
        createDialog();
        dialog.setMinimumSize(DIALOG_WIDTH, DIALOG_HEIGHT);
        finish.setEnabled(true);
        finish.forceFocus();
    }

    /**
     * Check for finish button enabling.
     */
    void checkFinish()
    {
        final boolean hasRips = !levelRips.getLevelRips().isEmpty();
        final boolean finished = hasRips;

        finish.setEnabled(finished);
        tipsLabel.setVisible(!finished);
    }

    /**
     * Create the level rips chooser area.
     * 
     * @param parent The parent composite.
     */
    private void createLevelRipsArea(Composite parent)
    {
        levelRips = new LevelRipWidget(parent);
        levelRips.addListener(new LevelRipsWidgetListener()
        {
            @Override
            public void notifyLevelRipAdded(Media media)
            {
                autofillSheetsConfig(media);
                checkFinish();
            }

            @Override
            public void notifyLevelRipRemoved(Media media)
            {
                checkFinish();
            }
        });
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
     * Create the sheets location chooser area.
     * 
     * @param parent The parent composite.
     */
    private void crateSheetsLocationArea(Composite parent)
    {
        sheets = new BrowseWidget(parent,
                                  com.b3dgs.lionengine.editor.map.imports.Messages.SheetsLocation,
                                  UtilDialog.getXmlFilter(),
                                  false);
        sheets.addListener(media ->
        {
            final String folder = media.getParentPath();
            if (groups.getMedia() == null)
            {
                groups.setLocation(UtilFolder.getPath(folder, TileGroupsConfig.FILENAME));
            }
            if (transitions.getMedia() == null)
            {
                transitions.setLocation(UtilFolder.getPath(folder, TransitionsConfig.FILENAME));
            }
            if (circuits.getMedia() == null)
            {
                circuits.setLocation(UtilFolder.getPath(folder, CircuitsConfig.FILENAME));
            }
            checkFinish();
        });
    }

    @Override
    protected void createContent(Composite content)
    {
        createLevelRipsArea(content);
        crateSheetsLocationArea(content);

        groups = new BrowseWidget(content,
                                  com.b3dgs.lionengine.editor.map.imports.Messages.GroupsLocation,
                                  UtilDialog.getXmlFilter(),
                                  false);
        groups.addListener(media -> checkFinish());

        transitions = new BrowseWidget(content, Messages.TransitionsLocation, UtilDialog.getXmlFilter(), false);
        transitions.addListener(media -> checkFinish());

        circuits = new BrowseWidget(content, Messages.CircuitsLocation, UtilDialog.getXmlFilter(), false);
        circuits.addListener(media -> checkFinish());
    }

    @Override
    protected void onFinish()
    {
        final Collection<Media> levels = levelRips.getLevelRips();
        final Media sheetsConfig = sheets.getMedia();
        final Media groupsConfig = groups.getMedia();

        TransitionsConfig.exports(transitions.getMedia(), levels, sheetsConfig, groupsConfig);
        CircuitsConfig.exports(circuits.getMedia(), levels, sheetsConfig, groupsConfig);
    }
}
