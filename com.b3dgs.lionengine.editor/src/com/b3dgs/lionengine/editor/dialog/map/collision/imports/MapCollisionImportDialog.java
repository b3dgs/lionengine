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
package com.b3dgs.lionengine.editor.dialog.map.collision.imports;

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
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.editor.dialog.AbstractDialog;
import com.b3dgs.lionengine.editor.project.Project;
import com.b3dgs.lionengine.editor.utility.UtilButton;
import com.b3dgs.lionengine.editor.utility.UtilDialog;
import com.b3dgs.lionengine.editor.utility.UtilIcon;
import com.b3dgs.lionengine.editor.world.WorldModel;
import com.b3dgs.lionengine.editor.world.WorldPart;
import com.b3dgs.lionengine.editor.world.handler.SetPointerCollisionHandler;
import com.b3dgs.lionengine.editor.world.handler.SetShowCollisionsHandler;
import com.b3dgs.lionengine.editor.world.tester.MapTester;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.map.MapTileCollision;
import com.b3dgs.lionengine.game.map.MapTileCollisionModel;

/**
 * Represents the import map dialog.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class MapCollisionImportDialog extends AbstractDialog
{
    /** Icon. */
    private static final Image ICON = UtilIcon.get("dialog", "import.png");
    /** Xml filter. */
    private static final String XML = "*.xml";

    /** World part reference. */
    private final WorldPart part;
    /** Formulas config file location. */
    private Text formulasText;
    /** Collisions config file location. */
    private Text collisionsText;
    /** Formulas config file. */
    private Media formulasConfig;
    /** Collisions config file. */
    private Media collisionsConfig;
    /** Found. */
    private boolean found;

    /**
     * Create an import map dialog.
     * 
     * @param parent The shell parent.
     */
    public MapCollisionImportDialog(Shell parent)
    {
        super(parent, Messages.Title, Messages.HeaderTitle, Messages.HeaderDesc, ICON);
        createDialog();
        dialog.setMinimumSize(512, 160);
        part = WorldModel.INSTANCE.getServices().get(WorldPart.class);
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
        return formulasConfig;
    }

    /**
     * Get the collisions config file location.
     * 
     * @return The collisions config file location.
     */
    public Media getCollisionsLocation()
    {
        return collisionsConfig;
    }

    /**
     * Check if import is found.
     * 
     * @return <code>true</code> if found, <code>false</code> else.
     */
    public boolean isFound()
    {
        return found;
    }

    /**
     * Update the tips label.
     */
    private void updateTipsLabel()
    {
        tipsLabel.setVisible(false);
    }

    /**
     * Browse the formulas location.
     */
    private void browseFormulasLocation()
    {
        final File file = UtilDialog.selectResourceFile(dialog, true, new String[]
        {
            Messages.FormulasConfigFileFilter
        }, new String[]
        {
            XML
        });
        if (file != null)
        {
            onFormulasConfigLocationSelected(file);
        }
    }

    /**
     * Browse the collision location.
     */
    private void browseCollisionLocation()
    {
        final File file = UtilDialog.selectResourceFile(dialog, true, new String[]
        {
            Messages.CollisionsFileFilter
        }, new String[]
        {
            XML
        });
        if (file != null)
        {
            onCollisionsConfigLocationSelected(file);
        }
    }

    /**
     * Called when the formulas config file location has been selected.
     * 
     * @param path The selected formulas config file location path.
     */
    private void onFormulasConfigLocationSelected(File path)
    {
        final Project project = Project.getActive();
        boolean validSheets = false;
        try
        {
            formulasConfig = project.getResourceMedia(new File(path.getAbsolutePath()));
            formulasText.setText(formulasConfig.getPath());
            final File sheets = formulasConfig.getFile();
            if (!sheets.isFile())
            {
                setTipsMessage(AbstractDialog.ICON_ERROR, Messages.ErrorFormulas);
            }
            validSheets = sheets.isFile();
        }
        catch (final LionEngineException exception)
        {
            setTipsMessage(AbstractDialog.ICON_ERROR, Messages.ErrorFormulas);
        }
        updateTipsLabel();

        final boolean isValid = collisionsConfig != null && formulasConfig != null && validSheets;
        finish.setEnabled(isValid);
    }

    /**
     * Called when the collision config file location has been selected.
     * 
     * @param path The collision config file location path.
     */
    private void onCollisionsConfigLocationSelected(File path)
    {
        final Project project = Project.getActive();
        try
        {
            collisionsConfig = project.getResourceMedia(new File(path.getAbsolutePath()));
            collisionsText.setText(collisionsConfig.getPath());
        }
        catch (final LionEngineException exception)
        {
            setTipsMessage(AbstractDialog.ICON_ERROR, Messages.ErrorCollisions);
        }
        updateTipsLabel();
        finish.setEnabled(collisionsConfig != null && formulasConfig != null);
    }

    /**
     * Create the formulas location area.
     * 
     * @param content The content composite.
     */
    private void createFormulasLocationArea(Composite content)
    {
        final Composite sheetArea = new Composite(content, SWT.NONE);
        sheetArea.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        sheetArea.setLayout(new GridLayout(3, false));

        final Label locationLabel = new Label(sheetArea, SWT.NONE);
        locationLabel.setText(Messages.FormulasLocation);

        formulasText = new Text(sheetArea, SWT.BORDER);
        formulasText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        formulasText.setEditable(false);

        final Button browse = UtilButton.createBrowse(sheetArea);
        UtilButton.setAction(browse, () -> browseFormulasLocation());
    }

    /**
     * Create the collision location area.
     * 
     * @param content The content composite.
     */
    private void createCollisionLocationArea(Composite content)
    {
        final Composite collisionArea = new Composite(content, SWT.NONE);
        collisionArea.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        collisionArea.setLayout(new GridLayout(3, false));

        final Label locationLabel = new Label(collisionArea, SWT.NONE);
        locationLabel.setText(Messages.CollisionsLocation);

        collisionsText = new Text(collisionArea, SWT.BORDER);
        collisionsText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        collisionsText.setEditable(false);

        final Button browse = UtilButton.createBrowse(collisionArea);
        UtilButton.setAction(browse, () -> browseCollisionLocation());
    }

    /**
     * Load default files.
     */
    private void loadDefaults()
    {
        final Project project = Project.getActive();
        final MapTile map = WorldModel.INSTANCE.getMap();
        final File parentFile = map.getGroupsConfig().getFile().getParentFile();

        final File formulas = new File(parentFile, MapTileCollision.DEFAULT_FORMULAS_FILE);
        if (MapTester.isFormulasConfig(project.getResourceMedia(formulas)))
        {
            onFormulasConfigLocationSelected(formulas);
        }

        final File collisions = new File(parentFile, MapTileCollision.DEFAULT_COLLISIONS_FILE);
        if (MapTester.isCollisionsConfig(project.getResourceMedia(collisions)))
        {
            onCollisionsConfigLocationSelected(collisions);
        }
    }

    /*
     * AbstractDialog
     */

    @Override
    protected void createContent(Composite content)
    {
        createFormulasLocationArea(content);
        createCollisionLocationArea(content);
    }

    @Override
    protected void onFinish()
    {
        found = formulasConfig != null && collisionsConfig != null;
        if (found)
        {
            final MapTile map = WorldModel.INSTANCE.getMap();
            if (!map.hasFeature(MapTileCollision.class))
            {
                map.createFeature(MapTileCollisionModel.class);
            }
            final MapTileCollision mapCollision = map.getFeature(MapTileCollision.class);
            mapCollision.loadCollisions(formulasConfig, collisionsConfig);
            mapCollision.createCollisionDraw();

            part.setToolItemEnabled(SetShowCollisionsHandler.ID, true);
            part.setToolItemEnabled(SetPointerCollisionHandler.ID, true);
        }
    }
}
