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
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.b3dgs.lionengine.editor.Activator;
import com.b3dgs.lionengine.editor.project.Project;

/**
 * Represents the abstract project dialog.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public abstract class AbstractProjectDialog
        extends Dialog
{
    /** Maximum characters input. */
    protected static final int MAX_CHAR = 64;
    /** Bottom button width. */
    protected static final int BOTTOM_BUTTON_WIDTH = 96;

    /** Dialog shell. */
    protected final Shell dialog;
    /** Project name. */
    protected Text projectNameText;
    /** Project location. */
    protected Text projectLocationText;
    /** Project sources. */
    protected Text projectSourcesText;
    /** Project resources. */
    protected Text projectResourcesText;
    /** Finish button. */
    protected Button finish;
    /** Tips label. */
    protected CLabel tipsLabel;
    /** Project imported. */
    protected Project project;

    /**
     * Constructor.
     * 
     * @param parent The parent reference.
     * @param title The dialog title.
     */
    public AbstractProjectDialog(Shell parent, String title)
    {
        super(parent);
        dialog = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
        dialog.setMinimumSize(500, 300);
        final GridLayout dialogLayout = new GridLayout(1, false);
        dialogLayout.marginHeight = 0;
        dialogLayout.marginWidth = 0;
        dialogLayout.verticalSpacing = 0;
        dialog.setLayout(dialogLayout);
        dialog.setText(title);
        dialog.setImage(Activator.getIcon("product.png")); //$NON-NLS-1$
    }

    /**
     * Create the header part of the dialog.
     * 
     * @param header The header composite.
     */
    protected abstract void createHeader(Composite header);

    /**
     * Called when the project location has been selected.
     * 
     * @param path The selected location (can be <code>null</code>).
     */
    protected abstract void onLocationSelected(String path);

    /**
     * Called when click on finish button.
     */
    protected abstract void onFinish();

    /**
     * Open the dialog.
     * 
     * @return The project opened, <code>null</code> if none.
     */
    public Project open()
    {
        dialog.pack(true);
        Activator.center(dialog);
        dialog.open();

        final Display display = dialog.getDisplay();
        while (!dialog.isDisposed())
        {
            if (!display.readAndDispatch())
            {
                display.sleep();
            }
        }
        return project;
    }

    /**
     * Set the error message.
     * 
     * @param icon The message icon.
     * @param message The error message.
     */
    protected void setTipsMessage(Image icon, String message)
    {
        tipsLabel.setText(message);
        tipsLabel.setImage(icon);
        tipsLabel.pack(true);
        tipsLabel.setVisible(true);
    }

    /**
     * Create the dialog.
     */
    protected void createDialog()
    {
        final Composite header = new Composite(dialog, SWT.NONE);
        header.setLayout(new GridLayout(2, false));
        header.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        createHeader(header);
        header.setBackground(header.getDisplay().getSystemColor(SWT.COLOR_WHITE));

        final Label separatorHeader = new Label(dialog, SWT.SEPARATOR | SWT.HORIZONTAL);
        separatorHeader.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        final Composite content = new Composite(dialog, SWT.NONE);
        content.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true));
        content.setLayout(new GridLayout(1, false));
        createContent(content);

        final Label separatorContent = new Label(dialog, SWT.SEPARATOR | SWT.HORIZONTAL);
        separatorContent.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        final Composite bottom = new Composite(dialog, SWT.NONE);
        bottom.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        bottom.setLayout(new GridLayout(2, false));
        createBottom(bottom);
    }

    /**
     * Create the content part of the dialog.
     * 
     * @param content The content composite.
     */
    protected void createContent(Composite content)
    {
        createProjectNameArea(content);
        createProjectLocationArea(content);

        final Group folders = new Group(content, SWT.SHADOW_NONE);
        folders.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true));
        folders.setLayout(new GridLayout(1, false));
        folders.setText(Messages.AbstractProjectDialog_Folders);
        createProjectSourcesArea(folders);
        createProjectResourcesArea(folders);
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

        final Button browse = new Button(nameArea, SWT.PUSH);
        final GridData browseData = new GridData();
        browseData.widthHint = 64;
        browse.setLayoutData(browseData);
        browse.setText(Messages.AbstractProjectDialog_Browse);
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
     * Create the project sources area chooser.
     * 
     * @param content The content composite.
     */
    protected void createProjectSourcesArea(Composite content)
    {
        final Composite sourcesArea = new Composite(content, SWT.NONE);
        sourcesArea.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        sourcesArea.setLayout(new GridLayout(3, false));

        final Label sourcesLabel = new Label(sourcesArea, SWT.NONE);
        final GridData sourcesData = new GridData();
        sourcesData.widthHint = 64;
        sourcesLabel.setLayoutData(sourcesData);
        sourcesLabel.setText(Messages.AbstractProjectDialog_Sources);

        projectSourcesText = new Text(sourcesArea, SWT.BORDER);
        projectSourcesText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        projectSourcesText.setTextLimit(AbstractProjectDialog.MAX_CHAR);
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
        projectResourcesText.setTextLimit(AbstractProjectDialog.MAX_CHAR);
    }

    /**
     * Create the bottom part of the dialog.
     * 
     * @param bottom The bottom composite.
     */
    protected void createBottom(Composite bottom)
    {
        tipsLabel = new CLabel(bottom, SWT.LEFT_TO_RIGHT);
        tipsLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        tipsLabel.setVisible(false);

        final Composite buttonArea = new Composite(bottom, SWT.NONE);
        buttonArea.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
        buttonArea.setLayout(new GridLayout(2, false));

        finish = new Button(buttonArea, SWT.PUSH);
        final GridData finishData = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
        finishData.widthHint = AbstractProjectDialog.BOTTOM_BUTTON_WIDTH;
        finish.setLayoutData(finishData);
        finish.setText(Messages.AbstractProjectDialog_Finish);
        finish.setEnabled(false);
        finish.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent)
            {
                onFinish();
                dialog.dispose();
            }
        });

        final Button cancel = new Button(buttonArea, SWT.PUSH);
        final GridData cancelData = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
        cancelData.widthHint = AbstractProjectDialog.BOTTOM_BUTTON_WIDTH;
        cancel.setLayoutData(cancelData);
        cancel.setText(Messages.AbstractProjectDialog_Cancel);
        cancel.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent)
            {
                dialog.dispose();
            }
        });
    }
}
