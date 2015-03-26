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
public class ImportMapDialog
        extends AbstractDialog
{
    /** Icon. */
    private static final Image ICON = UtilEclipse.getIcon("dialog", "import-map.png");

    /** Level rip location. */
    Text levelRipLocationText;
    /** Sheets location. */
    Text sheetsLocationText;
    /** Groups location. */
    Text groupsLocationText;
    /** Level rip file. */
    Media levelRip;
    /** Sheets config file. */
    Media sheetsConfig;
    /** Groups config file. */
    Media groupsConfig;
    /** Found. */
    private boolean found;

    /**
     * Create an import map dialog.
     * 
     * @param parent The shell parent.
     */
    public ImportMapDialog(Shell parent)
    {
        super(parent, Messages.ImportMapDialog_Title, Messages.ImportMapDialog_HeaderTitle,
                Messages.ImportMapDialog_HeaderDesc, ImportMapDialog.ICON);
        createDialog();

        finish.setEnabled(false);
        finish.forceFocus();
    }

    /**
     * Get the level rip location.
     * 
     * @return The level rip location.
     */
    public Media getLevelRipLocation()
    {
        return levelRip;
    }

    /**
     * Get the sheets config location.
     * 
     * @return The sheets config location.
     */
    public Media getSheetsConfigLocation()
    {
        return sheetsConfig;
    }

    /**
     * Get the groups config location.
     * 
     * @return The group config location.
     */
    public Media getGroupsConfigLocation()
    {
        return groupsConfig;
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
     * Called when the level rip location has been selected.
     * 
     * @param path The level rip location path.
     */
    void onLevelRipLocationSelected(File path)
    {
        final Project project = Project.getActive();
        levelRipLocationText.setText(path.getAbsolutePath());
        try
        {
            levelRip = project.getResourceMedia(new File(levelRipLocationText.getText()));
        }
        catch (final LionEngineException exception)
        {
            setTipsMessage(AbstractDialog.ICON_ERROR, Messages.ImportMapDialog_ErrorLevelRip);
        }
        updateTipsLabel();
        finish.setEnabled(levelRip != null && sheetsConfig != null);
    }

    /**
     * Called when the sheets config location has been selected.
     * 
     * @param path The selected sheets config location path.
     */
    void onSheetsConfigLocationSelected(File path)
    {
        final Project project = Project.getActive();
        sheetsLocationText.setText(path.getAbsolutePath());
        boolean validSheets = false;
        try
        {
            sheetsConfig = project.getResourceMedia(new File(sheetsLocationText.getText()));
            final File sheets = sheetsConfig.getFile();
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

        final boolean isValid = levelRip != null && sheetsConfig != null && validSheets;
        finish.setEnabled(isValid);
    }

    /**
     * Called when the groups config location has been selected.
     * 
     * @param path The selected groups config location path.
     */
    void onGroupsConfigLocationSelected(File path)
    {
        final Project project = Project.getActive();
        groupsLocationText.setText(path.getAbsolutePath());
        boolean validGroups = false;
        try
        {
            groupsConfig = project.getResourceMedia(new File(groupsLocationText.getText()));
            final File groups = groupsConfig.getFile();
            if (!groups.isFile())
            {
                setTipsMessage(AbstractDialog.ICON_ERROR, Messages.ImportMapDialog_ErrorGroups);
            }
            validGroups = groups.isFile();
        }
        catch (final LionEngineException exception)
        {
            setTipsMessage(AbstractDialog.ICON_ERROR, Messages.ImportMapDialog_ErrorGroups);
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
        locationLabel.setText(Messages.ImportMapDialog_LevelRipLocation);

        levelRipLocationText = new Text(levelRipArea, SWT.BORDER);
        levelRipLocationText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        levelRipLocationText.setEditable(false);

        final Button browse = UtilSwt.createButton(levelRipArea, Messages.AbstractDialog_Browse, null);
        browse.setImage(AbstractDialog.ICON_BROWSE);
        browse.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent)
            {
                final File file = Tools.selectResourceFile(dialog, true, new String[]
                {
                    Messages.ImportMapDialog_FileFilter
                }, new String[]
                {
                    "*.bmp;*.png"
                });
                if (file != null)
                {
                    onLevelRipLocationSelected(file);
                }
            }
        });
    }

    /**
     * Create the patterns location area.
     * 
     * @param content The content composite.
     */
    private void createPatternsLocationArea(Composite content)
    {
        final Composite levelRipArea = new Composite(content, SWT.NONE);
        levelRipArea.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        levelRipArea.setLayout(new GridLayout(4, false));

        final Label locationLabel = new Label(levelRipArea, SWT.NONE);
        locationLabel.setText(Messages.ImportMapDialog_SheetsLocation);

        sheetsLocationText = new Text(levelRipArea, SWT.BORDER);
        sheetsLocationText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        sheetsLocationText.setEditable(false);

        groupsLocationText = new Text(levelRipArea, SWT.BORDER);
        groupsLocationText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        groupsLocationText.setEditable(false);

        final Button browse = UtilSwt.createButton(levelRipArea, Messages.AbstractDialog_Browse, null);
        browse.setImage(AbstractDialog.ICON_BROWSE);
        browse.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent)
            {
                final File folder = Tools.selectResourceFolder(dialog);
                if (folder != null)
                {
                    onSheetsConfigLocationSelected(folder);
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
        createLevelRipLocationArea(content);
        createPatternsLocationArea(content);
    }

    @Override
    protected void onFinish()
    {
        found = levelRip != null && sheetsConfig != null;
    }
}
