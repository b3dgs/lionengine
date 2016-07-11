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
package com.b3dgs.lionengine.editor.map.constaint;

import java.util.Collection;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.editor.dialog.AbstractDialog;
import com.b3dgs.lionengine.editor.utility.UtilIcon;
import com.b3dgs.lionengine.editor.utility.dialog.UtilDialog;
import com.b3dgs.lionengine.editor.widget.BrowseWidget;
import com.b3dgs.lionengine.editor.widget.levelrip.LevelRipWidget;
import com.b3dgs.lionengine.editor.widget.levelrip.LevelRipWidget.LevelRipsWidgetListener;
import com.b3dgs.lionengine.game.map.TileSheetsConfig;
import com.b3dgs.lionengine.game.map.feature.circuit.CircuitsConfig;
import com.b3dgs.lionengine.game.map.feature.transition.TransitionsConfig;
import com.b3dgs.lionengine.game.tile.TileGroupsConfig;
import com.b3dgs.lionengine.util.UtilFolder;

/**
 * Represents the export map tile constraints dialog.
 */
public class ConstraintsExtractDialog extends AbstractDialog
{
    /** Icon. */
    private static final Image ICON = UtilIcon.get("dialog", "import.png");

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
        dialog.setMinimumSize(512, 160);
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

    /*
     * AbstractDialog
     */

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
