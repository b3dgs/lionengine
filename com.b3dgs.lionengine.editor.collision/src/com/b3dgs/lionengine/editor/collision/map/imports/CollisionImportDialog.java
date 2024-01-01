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
package com.b3dgs.lionengine.editor.collision.map.imports;

import java.io.File;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.editor.dialog.DialogAbstract;
import com.b3dgs.lionengine.editor.project.Project;
import com.b3dgs.lionengine.editor.project.ProjectModel;
import com.b3dgs.lionengine.editor.toolbar.SetPointerCollisionHandler;
import com.b3dgs.lionengine.editor.toolbar.SetShowCollisionsHandler;
import com.b3dgs.lionengine.editor.utility.UtilIcon;
import com.b3dgs.lionengine.editor.utility.dialog.UtilDialog;
import com.b3dgs.lionengine.editor.widget.BrowseWidget;
import com.b3dgs.lionengine.editor.world.WorldModel;
import com.b3dgs.lionengine.editor.world.view.WorldPart;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGroup;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionFormulaConfig;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionGroupConfig;
import com.b3dgs.lionengine.game.feature.tile.map.collision.MapTileCollision;
import com.b3dgs.lionengine.helper.MapTileHelper;

/**
 * Represents the import map dialog.
 */
public class CollisionImportDialog extends DialogAbstract
{
    /** Icon. */
    private static final Image ICON = UtilIcon.get("dialog", "import.png");
    /** Dialog width. */
    private static final int DIALOG_WIDTH = 512;
    /** Dialog height. */
    private static final int DIALOG_HEIGHT = 160;

    /** Formulas config file location. */
    private BrowseWidget formulas;
    /** Collisions config file location. */
    private BrowseWidget collisions;

    /**
     * Create an import map dialog.
     * 
     * @param parent The shell parent.
     */
    public CollisionImportDialog(Shell parent)
    {
        super(parent, Messages.Title, Messages.HeaderTitle, Messages.HeaderDesc, ICON);
        createDialog();
        dialog.setMinimumSize(DIALOG_WIDTH, DIALOG_HEIGHT);
        finish.setEnabled(false);
        finish.forceFocus();
        loadDefaults();
    }

    /**
     * Get the formulas config file location.
     * 
     * @return The formulas config file location.
     */
    public Media getFormulasLocation()
    {
        return formulas.getMedia();
    }

    /**
     * Get the collisions config file location.
     * 
     * @return The collisions config file location.
     */
    public Media getCollisionsLocation()
    {
        return collisions.getMedia();
    }

    /**
     * Load default files.
     */
    private void loadDefaults()
    {
        final Project project = ProjectModel.INSTANCE.getProject();
        final MapTile map = WorldModel.INSTANCE.getMap();
        final File parentFile = map.getFeature(MapTileGroup.class).getGroupsConfig().getFile().getParentFile();

        final File formulasFile = new File(parentFile, CollisionFormulaConfig.FILENAME);
        if (MapCollisionTester.isFormulasConfig(project.getResourceMedia(formulasFile)))
        {
            formulas.setLocation(project.getResourceMedia(formulasFile).getPath());
        }

        final File collisionsFile = new File(parentFile, CollisionGroupConfig.FILENAME);
        if (MapCollisionTester.isCollisionsConfig(project.getResourceMedia(collisionsFile)))
        {
            collisions.setLocation(project.getResourceMedia(collisionsFile).getPath());
        }
    }

    /**
     * Check if can enable finish button.
     */
    private void checkFinish()
    {
        finish.setEnabled(formulas.getMedia() != null && collisions.getMedia() != null);
    }

    @Override
    protected void createContent(Composite content)
    {
        formulas = new BrowseWidget(content, Messages.FormulasLocation, UtilDialog.getXmlFilter(), true);
        formulas.addListener(media -> checkFinish());

        collisions = new BrowseWidget(content, Messages.CollisionsLocation, UtilDialog.getXmlFilter(), true);
        collisions.addListener(media -> checkFinish());
    }

    @Override
    protected void onFinish()
    {
        final MapTileHelper map = WorldModel.INSTANCE.getMap();

        final MapTileCollision mapCollision = map.getFeature(MapTileCollision.class);
        mapCollision.loadCollisions(formulas.getMedia(), collisions.getMedia());

        final WorldPart part = WorldModel.INSTANCE.getServices().get(WorldPart.class);
        part.setToolItemEnabled(SetShowCollisionsHandler.ID, true);
        part.setToolItemEnabled(SetPointerCollisionHandler.ID, true);
    }
}
