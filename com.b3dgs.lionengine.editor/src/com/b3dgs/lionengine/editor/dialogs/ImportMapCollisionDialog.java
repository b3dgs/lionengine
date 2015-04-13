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
package com.b3dgs.lionengine.editor.dialogs;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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
import com.b3dgs.lionengine.editor.Tools;
import com.b3dgs.lionengine.editor.UtilEclipse;
import com.b3dgs.lionengine.editor.UtilSwt;
import com.b3dgs.lionengine.editor.project.Project;

/**
 * Represents the import map dialog.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class ImportMapCollisionDialog
        extends AbstractDialog
{
    /** Icon. */
    private static final Image ICON = UtilEclipse.getIcon("dialog", "import-map.png");

    /** Collisions config file location. */
    Text collisionsText;
    /** Formulas config file location. */
    Text formulasText;
    /** Collisions config file. */
    Media collisionsConfig;
    /** Formulas config file. */
    Media formulasConfig;
    /** Found. */
    private boolean found;

    /**
     * Create an import map dialog.
     * 
     * @param parent The shell parent.
     */
    public ImportMapCollisionDialog(Shell parent)
    {
        super(parent, Messages.ImportMapDialog_Title, Messages.ImportMapDialog_HeaderTitle,
                Messages.ImportMapDialog_HeaderDesc, ImportMapCollisionDialog.ICON);
        createDialog();

        finish.setEnabled(false);
        finish.forceFocus();
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
     * Get the formulas config file location.
     * 
     * @return The formulas config file location.
     */
    public Media getFormulasLocation()
    {
        return formulasConfig;
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
    void updateTipsLabel()
    {
        tipsLabel.setVisible(false);
    }

    /**
     * Called when the collision config file location has been selected.
     * 
     * @param path The collision config file location path.
     */
    void onCollisionsConfigLocationSelected(File path)
    {
        final Project project = Project.getActive();
        collisionsText.setText(path.getAbsolutePath());
        try
        {
            collisionsConfig = project.getResourceMedia(new File(collisionsText.getText()));
        }
        catch (final LionEngineException exception)
        {
            setTipsMessage(AbstractDialog.ICON_ERROR, Messages.ImportMapDialog_ErrorLevelRip);
        }
        updateTipsLabel();
        finish.setEnabled(collisionsConfig != null && formulasConfig != null);
    }

    /**
     * Called when the formulas config file location has been selected.
     * 
     * @param path The selected formulas config file location path.
     */
    void onFormulasConfigLocationSelected(File path)
    {
        final Project project = Project.getActive();
        formulasText.setText(path.getAbsolutePath());
        boolean validSheets = false;
        try
        {
            formulasConfig = project.getResourceMedia(new File(formulasText.getText()));
            final File sheets = formulasConfig.getFile();
            if (!sheets.isFile())
            {
                setTipsMessage(AbstractDialog.ICON_ERROR, Messages.ImportMapDialog_ErrorSheets);
            }
            validSheets = sheets.isFile();
        }
        catch (final LionEngineException exception)
        {
            setTipsMessage(AbstractDialog.ICON_ERROR, Messages.ImportMapDialog_ErrorSheets);
        }
        updateTipsLabel();

        final boolean isValid = collisionsConfig != null && formulasConfig != null && validSheets;
        finish.setEnabled(isValid);
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
        locationLabel.setText(Messages.ImportMapDialog_LevelRipLocation);

        collisionsText = new Text(collisionArea, SWT.BORDER);
        collisionsText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        collisionsText.setEditable(false);

        final Button browse = UtilSwt.createButton(collisionArea, Messages.AbstractDialog_Browse, null);
        browse.setImage(AbstractDialog.ICON_BROWSE);
        browse.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent)
            {
                final File file = Tools.selectResourceFile(dialog, true, new String[]
                {
                    Messages.ImportMapDialog_LevelRipFileFilter
                }, new String[]
                {
                    "*.xml"
                });
                if (file != null)
                {
                    onCollisionsConfigLocationSelected(file);
                }
            }
        });
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
        sheetArea.setLayout(new GridLayout(4, false));

        final Label locationLabel = new Label(sheetArea, SWT.NONE);
        locationLabel.setText(Messages.ImportMapDialog_SheetsLocation);

        formulasText = new Text(sheetArea, SWT.BORDER);
        formulasText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        formulasText.setEditable(false);

        final Button browse = UtilSwt.createButton(sheetArea, Messages.AbstractDialog_Browse, null);
        browse.setImage(AbstractDialog.ICON_BROWSE);
        browse.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent)
            {
                final File file = Tools.selectResourceFile(dialog, true, new String[]
                {
                    Messages.ImportMapDialog_SheetsConfigFileFilter
                }, new String[]
                {
                    "*.xml"
                });
                if (file != null)
                {
                    onFormulasConfigLocationSelected(file);
                }
            }
        });
    }

    /*
     * AbstractDialog
     */

    @Override
    protected void createContent(Composite content)
    {
        createCollisionLocationArea(content);
        createFormulasLocationArea(content);
    }

    @Override
    protected void onFinish()
    {
        found = collisionsConfig != null && formulasConfig != null;
    }
}
