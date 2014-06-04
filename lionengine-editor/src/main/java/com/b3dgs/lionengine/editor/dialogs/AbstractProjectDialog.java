/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.b3dgs.lionengine.editor.Tools;
import com.b3dgs.lionengine.editor.project.Project;

/**
 * Represents the abstract project dialog.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public abstract class AbstractProjectDialog
        extends AbstractDialog
{
    /** Project name. */
    protected Text projectNameText;
    /** Project location. */
    protected Text projectLocationText;
    /** Project classes. */
    protected Text projectClassesText;
    /** Project resources. */
    protected Text projectResourcesText;
    /** Project imported. */
    protected Project project;

    /**
     * Constructor.
     * 
     * @param parent The parent reference.
     * @param title The dialog title.
     * @param headerTitle The header title.
     * @param headerDesc The header description.
     * @param headerIcon The header icon.
     */
    public AbstractProjectDialog(Shell parent, String title, String headerTitle, String headerDesc, Image headerIcon)
    {
        super(parent, title, headerTitle, headerDesc, headerIcon);
    }

    /**
     * Called when the project location has been selected.
     * 
     * @param path The selected location (can be <code>null</code>).
     */
    protected abstract void onLocationSelected(String path);

    /**
     * Get the opened project.
     * 
     * @return The opened project, <code>null</code> if none.
     */
    public Project getProject()
    {
        return project;
    }

    /**
     * Create the project name area.
     * 
     * @param content The content composite.
     */
    protected void createProjectNameArea(Composite content)
    {
        final Composite nameArea = new Composite(content, SWT.NONE);
        nameArea.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        nameArea.setLayout(new GridLayout(2, false));

        final Label nameLabel = new Label(nameArea, SWT.NONE);
        nameLabel.setText(Messages.AbstractProjectDialog_Name);

        projectNameText = new Text(nameArea, SWT.BORDER);
        projectNameText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
    }

    /**
     * Create the project location area.
     * 
     * @param content The content composite.
     */
    protected void createProjectLocationArea(Composite content)
    {
        final Composite nameArea = new Composite(content, SWT.NONE);
        nameArea.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        nameArea.setLayout(new GridLayout(3, false));

        final Label locationLabel = new Label(nameArea, SWT.NONE);
        locationLabel.setText(Messages.AbstractProjectDialog_Location);

        projectLocationText = new Text(nameArea, SWT.BORDER);
        projectLocationText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        final Button browse = Tools.createButton(nameArea, Messages.AbstractDialog_Browse, null, true);
        browse.forceFocus();
        browse.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent)
            {
                final DirectoryDialog directoryDialog = new DirectoryDialog(dialog, SWT.APPLICATION_MODAL);
                final String path = directoryDialog.open();
                onLocationSelected(path);
            }
        });
    }

    /**
     * Create the project classes area chooser.
     * 
     * @param content The content composite.
     */
    protected void createProjectClassesArea(Composite content)
    {
        final Composite classesArea = new Composite(content, SWT.NONE);
        classesArea.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        classesArea.setLayout(new GridLayout(3, false));

        final Label classesLabel = new Label(classesArea, SWT.NONE);
        final GridData classesData = new GridData();
        classesData.widthHint = 64;
        classesLabel.setLayoutData(classesData);
        classesLabel.setText(Messages.AbstractProjectDialog_Classes);

        projectClassesText = new Text(classesArea, SWT.BORDER);
        projectClassesText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        projectClassesText.setTextLimit(AbstractDialog.MAX_CHAR);
    }

    /**
     * Create the project resources area chooser.
     * 
     * @param content The content composite.
     */
    protected void createProjectResourcesArea(Composite content)
    {
        final Composite resourcesArea = new Composite(content, SWT.NONE);
        resourcesArea.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        resourcesArea.setLayout(new GridLayout(3, false));

        final Label resourcesLabel = new Label(resourcesArea, SWT.NONE);
        final GridData resourcesData = new GridData();
        resourcesData.widthHint = 64;
        resourcesLabel.setLayoutData(resourcesData);
        resourcesLabel.setText(Messages.AbstractProjectDialog_Resources);

        projectResourcesText = new Text(resourcesArea, SWT.BORDER);
        projectResourcesText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        projectResourcesText.setTextLimit(AbstractDialog.MAX_CHAR);
    }

    /*
     * AbstractDialog
     */

    @Override
    protected void createContent(Composite content)
    {
        createProjectNameArea(content);
        createProjectLocationArea(content);

        final Group folders = new Group(content, SWT.SHADOW_NONE);
        folders.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true));
        folders.setLayout(new GridLayout(1, false));
        folders.setText(Messages.AbstractProjectDialog_Folders);
        createProjectClassesArea(folders);
        createProjectResourcesArea(folders);
    }
}
