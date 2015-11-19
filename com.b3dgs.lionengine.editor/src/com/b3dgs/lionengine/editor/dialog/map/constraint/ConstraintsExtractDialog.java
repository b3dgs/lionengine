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

import java.io.File;
import java.util.Collection;
import java.util.HashSet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.editor.ObjectList;
import com.b3dgs.lionengine.editor.dialog.AbstractDialog;
import com.b3dgs.lionengine.editor.dialog.LevelRipsWidget;
import com.b3dgs.lionengine.editor.project.Project;
import com.b3dgs.lionengine.editor.utility.UtilButton;
import com.b3dgs.lionengine.editor.utility.UtilDialog;
import com.b3dgs.lionengine.editor.utility.UtilIcon;
import com.b3dgs.lionengine.editor.world.WorldModel;
import com.b3dgs.lionengine.game.map.ConstraintsExtractor;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.map.TileSheetsConfig;
import com.b3dgs.lionengine.game.map.TransitionsExtractor;
import com.b3dgs.lionengine.game.tile.TileConstraintsConfig;
import com.b3dgs.lionengine.game.tile.TileGroupsConfig;
import com.b3dgs.lionengine.game.tile.TileTransitionsConfig;

/**
 * Represents the export map tile constraints dialog.
 */
public class ConstraintsExtractDialog extends AbstractDialog
{
    /** Icon. */
    public static final Image ICON = UtilIcon.get("dialog", "import.png");

    /** Level rip list. */
    private Tree levelRips;
    /** Add level rip. */
    private Button addLevelRip;
    /** Remove level rip. */
    private Button removeLevelRip;
    /** Sheets location. */
    private Text sheetsLocationText;
    /** Groups location. */
    private Text groupsLocationText;
    /** Constraints location. */
    private Text constraintsLocationText;
    /** Transitions location. */
    private Text transitionsLocationText;
    /** Sheets config file. */
    private Media sheetsConfig;
    /** Groups config file. */
    private Media groupsConfig;
    /** Constraints config file. */
    private Media constraintsConfig;
    /** Transitions config file. */
    private Media transitionsConfig;

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
     * Create the add level rip button.
     * 
     * @param parent The composite parent.
     */
    private void createButtonAdd(Composite parent)
    {
        addLevelRip = new Button(parent, SWT.PUSH);
        addLevelRip.setImage(ObjectList.ICON_ADD);
        addLevelRip.setToolTipText(com.b3dgs.lionengine.editor.dialog.map.sheets.extract.Messages.AddLevelRip);
        UtilButton.setAction(addLevelRip, () -> onAddLevelRip());
    }

    /**
     * Create the remove level rip button.
     * 
     * @param parent The composite parent.
     */
    private void createButtonRemove(Composite parent)
    {
        removeLevelRip = new Button(parent, SWT.PUSH);
        removeLevelRip.setImage(ObjectList.ICON_REMOVE);
        removeLevelRip.setToolTipText(com.b3dgs.lionengine.editor.dialog.map.sheets.extract.Messages.RemoveLevelRip);
        UtilButton.setAction(removeLevelRip, () -> onRemoveLevelRip());
    }

    /**
     * Update the tips label.
     */
    private void updateTipsLabel()
    {
        tipsLabel.setVisible(false);
    }

    /**
     * Browse the sheets location.
     */
    private void browseSheetsLocation()
    {
        final File file = UtilDialog.selectResourceXml(dialog, false, Messages.ConstraintsConfigFileFilter);
        if (file != null)
        {
            onSheetsLocationSelected(file);
        }
    }

    /**
     * Browse the groups location.
     */
    private void browseGroupsLocation()
    {
        final File file = UtilDialog.selectResourceXml(dialog, false, Messages.ConstraintsConfigFileFilter);
        if (file != null)
        {
            onGroupsLocationSelected(file);
        }
    }

    /**
     * Browse the constraints location.
     */
    private void browseConstraintsLocation()
    {
        final File file = UtilDialog.selectResourceXml(dialog, false, Messages.ConstraintsConfigFileFilter);
        if (file != null)
        {
            onConstraintsLocationSelected(file);
        }
    }

    /**
     * Browse the transitions location.
     */
    private void browseTransitionsLocation()
    {
        final File file = UtilDialog.selectResourceXml(dialog, false, Messages.TransitionsConfigFileFilter);
        if (file != null)
        {
            onTransitionsLocationSelected(file);
        }
    }

    /**
     * Called when the sheets config location has been selected.
     * 
     * @param path The selected sheets config location path.
     */
    private void onSheetsLocationSelected(File path)
    {
        final Project project = Project.getActive();
        try
        {
            sheetsConfig = project.getResourceMedia(new File(path.getAbsolutePath()));
            sheetsLocationText.setText(sheetsConfig.getPath());
            finish.setEnabled(true);
        }
        catch (final LionEngineException exception)
        {
            setTipsMessage(AbstractDialog.ICON_ERROR, Messages.ErrorConstraints);
        }
        updateTipsLabel();
    }

    /**
     * Called when the groups config location has been selected.
     * 
     * @param path The selected groups config location path.
     */
    private void onGroupsLocationSelected(File path)
    {
        final Project project = Project.getActive();
        try
        {
            groupsConfig = project.getResourceMedia(new File(path.getAbsolutePath()));
            groupsLocationText.setText(groupsConfig.getPath());
            finish.setEnabled(true);
        }
        catch (final LionEngineException exception)
        {
            setTipsMessage(AbstractDialog.ICON_ERROR, Messages.ErrorConstraints);
        }
        updateTipsLabel();
    }

    /**
     * Called when the constraints config location has been selected.
     * 
     * @param path The selected groups config location path.
     */
    private void onConstraintsLocationSelected(File path)
    {
        final Project project = Project.getActive();
        try
        {
            constraintsConfig = project.getResourceMedia(new File(path.getAbsolutePath()));
            constraintsLocationText.setText(constraintsConfig.getPath());
            finish.setEnabled(true);
        }
        catch (final LionEngineException exception)
        {
            setTipsMessage(AbstractDialog.ICON_ERROR, Messages.ErrorConstraints);
        }
        updateTipsLabel();
    }

    /**
     * Called when the transitions config location has been selected.
     * 
     * @param path The selected groups config location path.
     */
    private void onTransitionsLocationSelected(File path)
    {
        final Project project = Project.getActive();
        try
        {
            transitionsConfig = project.getResourceMedia(new File(path.getAbsolutePath()));
            transitionsLocationText.setText(transitionsConfig.getPath());
            finish.setEnabled(true);
        }
        catch (final LionEngineException exception)
        {
            setTipsMessage(AbstractDialog.ICON_ERROR, Messages.ErrorConstraints);
        }
        updateTipsLabel();
    }

    /**
     * Create the sheets location area.
     * 
     * @param parent The parent composite.
     */
    private void createSheetsLocationArea(Composite parent)
    {
        final Composite sheetsArea = new Composite(parent, SWT.NONE);
        sheetsArea.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        sheetsArea.setLayout(new GridLayout(3, false));
        final Label locationLabel = new Label(sheetsArea, SWT.NONE);
        locationLabel.setText(com.b3dgs.lionengine.editor.dialog.map.imports.Messages.SheetsLocation);

        sheetsLocationText = new Text(sheetsArea, SWT.BORDER);
        sheetsLocationText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        sheetsLocationText.setEditable(false);

        final MapTile map = WorldModel.INSTANCE.getMap();
        sheetsConfig = Medias.create(map.getSheetsConfig().getParentPath(), TileSheetsConfig.FILENAME);
        sheetsLocationText.setText(sheetsConfig.getPath());

        final Button browseConstraints = UtilButton.createBrowse(sheetsArea);
        UtilButton.setAction(browseConstraints, () -> browseSheetsLocation());
    }

    /**
     * Create the group location area.
     * 
     * @param parent The parent composite.
     */
    private void createGroupLocationArea(Composite parent)
    {
        final Composite groups = new Composite(parent, SWT.NONE);
        groups.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        groups.setLayout(new GridLayout(3, false));
        final Label locationLabel = new Label(groups, SWT.NONE);
        locationLabel.setText(com.b3dgs.lionengine.editor.dialog.map.imports.Messages.GroupsLocation);

        groupsLocationText = new Text(groups, SWT.BORDER);
        groupsLocationText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        groupsLocationText.setEditable(false);

        final MapTile map = WorldModel.INSTANCE.getMap();
        groupsConfig = Medias.create(map.getSheetsConfig().getParentPath(), TileGroupsConfig.FILENAME);
        groupsLocationText.setText(groupsConfig.getPath());

        final Button browseConstraints = UtilButton.createBrowse(groups);
        UtilButton.setAction(browseConstraints, () -> browseGroupsLocation());
    }

    /**
     * Create the constraints location area.
     * 
     * @param parent The parent composite.
     */
    private void createConstraintsLocationArea(Composite parent)
    {
        final Composite constraintsArea = new Composite(parent, SWT.NONE);
        constraintsArea.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        constraintsArea.setLayout(new GridLayout(3, false));
        final Label locationLabel = new Label(constraintsArea, SWT.NONE);
        locationLabel.setText(Messages.ConstraintsLocation);

        constraintsLocationText = new Text(constraintsArea, SWT.BORDER);
        constraintsLocationText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        constraintsLocationText.setEditable(false);

        final MapTile map = WorldModel.INSTANCE.getMap();
        constraintsConfig = Medias.create(map.getSheetsConfig().getParentPath(), TileConstraintsConfig.FILENAME);
        constraintsLocationText.setText(constraintsConfig.getPath());

        final Button browseConstraints = UtilButton.createBrowse(constraintsArea);
        UtilButton.setAction(browseConstraints, () -> browseConstraintsLocation());
    }

    /**
     * Create the transition location area.
     * 
     * @param parent The parent composite.
     */
    private void createTransitionsLocationArea(Composite parent)
    {
        final Composite transitionsArea = new Composite(parent, SWT.NONE);
        transitionsArea.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        transitionsArea.setLayout(new GridLayout(3, false));
        final Label transitionLabel = new Label(transitionsArea, SWT.NONE);
        transitionLabel.setText(Messages.TransitionsLocation);

        transitionsLocationText = new Text(transitionsArea, SWT.BORDER);
        transitionsLocationText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        transitionsLocationText.setEditable(false);

        final Button browseTransitions = UtilButton.createBrowse(transitionsArea);
        UtilButton.setAction(browseTransitions, () -> browseTransitionsLocation());

        final MapTile map = WorldModel.INSTANCE.getMap();
        transitionsConfig = Medias.create(map.getSheetsConfig().getParentPath(), TileTransitionsConfig.FILENAME);
        transitionsLocationText.setText(transitionsConfig.getPath());
    }

    /**
     * Called on add level rip action.
     */
    private void onAddLevelRip()
    {
        final File[] files = UtilDialog.selectResourceFiles(dialog, new String[]
        {
            com.b3dgs.lionengine.editor.dialog.map.sheets.extract.Messages.LevelRipFileFilter
        }, LevelRipsWidget.LEVEL_RIP_FILTER);
        final Project project = Project.getActive();
        for (final File file : files)
        {
            final TreeItem item = new TreeItem(levelRips, SWT.NONE);
            item.setText(file.getPath());

            final Media media = project.getResourceMedia(file);
            item.setData(media);

            if (!finish.isEnabled())
            {
                checkFinish();
            }
        }
    }

    /**
     * Called on remove a level rip action.
     */
    private void onRemoveLevelRip()
    {
        for (final TreeItem item : levelRips.getSelection())
        {
            item.setData(null);
            item.dispose();
        }
        if (levelRips.getItems().length == 0)
        {
            checkFinish();
        }
    }

    /**
     * Check for finish button enabling.
     */
    private void checkFinish()
    {
        final boolean hasRips = levelRips.getItems().length > 0;
        final boolean finished = hasRips;

        finish.setEnabled(finished);
        tipsLabel.setVisible(!finished);
    }

    /*
     * AbstractDialog
     */

    @Override
    protected void createContent(Composite content)
    {
        final Group area = new Group(content, SWT.NONE);
        area.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        area.setLayout(new GridLayout(1, false));
        area.setText(com.b3dgs.lionengine.editor.dialog.map.sheets.extract.Messages.RipsList);

        levelRips = new Tree(area, SWT.SINGLE);
        levelRips.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        final Composite buttons = new Composite(area, SWT.NONE);
        buttons.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        buttons.setLayout(new GridLayout(3, false));

        final Label label = new Label(buttons, SWT.NONE);
        label.setText(com.b3dgs.lionengine.editor.dialog.map.sheets.extract.Messages.AddRemoveRip);

        createButtonAdd(buttons);
        createButtonRemove(buttons);

        final Composite groupArea = new Composite(content, SWT.NONE);
        groupArea.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        groupArea.setLayout(new GridLayout(1, false));

        createSheetsLocationArea(groupArea);
        createGroupLocationArea(groupArea);
        createConstraintsLocationArea(groupArea);
        createTransitionsLocationArea(groupArea);
    }

    @Override
    protected void onFinish()
    {
        final Collection<Media> levelsSet = new HashSet<>();
        for (final TreeItem item : levelRips.getItems())
        {
            final Media level = (Media) item.getData();
            levelsSet.add(level);
        }
        final Media[] levels = levelsSet.toArray(new Media[levelsSet.size()]);

        TileConstraintsConfig.export(constraintsConfig, ConstraintsExtractor.getConstraints(levels, sheetsConfig));
        TileTransitionsConfig.exports(transitionsConfig,
                                      TransitionsExtractor.getTransitions(levels, sheetsConfig, groupsConfig));
    }
}
