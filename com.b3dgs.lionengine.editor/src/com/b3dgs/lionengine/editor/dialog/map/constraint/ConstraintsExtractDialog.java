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
package com.b3dgs.lionengine.editor.dialog.map.constraint;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.UtilFile;
import com.b3dgs.lionengine.editor.dialog.AbstractDialog;
import com.b3dgs.lionengine.editor.dialog.widget.BrowseWidget;
import com.b3dgs.lionengine.editor.dialog.widget.LevelRipsWidget;
import com.b3dgs.lionengine.editor.dialog.widget.LevelRipsWidget.LevelRipsWidgetListener;
import com.b3dgs.lionengine.editor.utility.UtilIcon;
import com.b3dgs.lionengine.game.map.ConstraintsExtractor;
import com.b3dgs.lionengine.game.map.transition.TransitionsConfig;
import com.b3dgs.lionengine.game.map.transition.TransitionsExtractor;
import com.b3dgs.lionengine.game.tile.TileConstraintsConfig;
import com.b3dgs.lionengine.game.tile.TileGroupsConfig;

/**
 * Represents the export map tile constraints dialog.
 */
public class ConstraintsExtractDialog extends AbstractDialog
{
    /** Icon. */
    public static final Image ICON = UtilIcon.get("dialog", "import.png");

    /** Level rip list. */
    private LevelRipsWidget levelRips;
    /** Sheets location. */
    private BrowseWidget sheets;
    /** Groups location. */
    private BrowseWidget groups;
    /** Constraints location. */
    private BrowseWidget constraints;
    /** Transitions location. */
    private BrowseWidget transitions;

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
        final boolean hasRips = levelRips.getLevelRips().length > 0;
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
        levelRips = new LevelRipsWidget(parent);
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
    }

    /**
     * Create the sheets location chooser area.
     * 
     * @param parent The parent composite.
     */
    private void crateSheetsLocationArea(Composite parent)
    {
        final String sheetsTitle = com.b3dgs.lionengine.editor.dialog.map.imports.Messages.SheetsLocation;
        final String sheetsFilter = com.b3dgs.lionengine.editor.dialog.map.imports.Messages.SheetsConfigFileFilter;
        sheets = new BrowseWidget(parent, sheetsTitle, sheetsFilter, false);
        sheets.addListener(media ->
        {
            final String folder = media.getParentPath();
            if (groups.getMedia() == null)
            {
                groups.setLocation(UtilFile.getPath(folder, TileGroupsConfig.FILENAME));
            }
            if (constraints.getMedia() == null)
            {
                constraints.setLocation(UtilFile.getPath(folder, TileConstraintsConfig.FILENAME));
            }
            if (transitions.getMedia() == null)
            {
                transitions.setLocation(UtilFile.getPath(folder, TransitionsConfig.FILENAME));
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

        final String groupsTitle = com.b3dgs.lionengine.editor.dialog.map.imports.Messages.GroupsLocation;
        final String groupsFilter = com.b3dgs.lionengine.editor.dialog.map.imports.Messages.GroupsConfigFileFilter;
        groups = new BrowseWidget(content, groupsTitle, groupsFilter, false);
        groups.addListener(media -> checkFinish());

        constraints = new BrowseWidget(content,
                                       Messages.ConstraintsLocation,
                                       Messages.ConstraintsConfigFileFilter,
                                       false);
        constraints.addListener(media -> checkFinish());

        transitions = new BrowseWidget(content,
                                       Messages.TransitionsLocation,
                                       Messages.TransitionsConfigFileFilter,
                                       false);
        transitions.addListener(media -> checkFinish());
    }

    @Override
    protected void onFinish()
    {
        final Media[] levels = levelRips.getLevelRips();
        final Media sheetsConfig = sheets.getMedia();
        final Media groupsConfig = groups.getMedia();

        TileConstraintsConfig.export(constraints.getMedia(), ConstraintsExtractor.getConstraints(levels, sheetsConfig));

        TransitionsConfig.exports(transitions.getMedia(),
                                  TransitionsExtractor.getTransitions(levels, sheetsConfig, groupsConfig));
    }
}
