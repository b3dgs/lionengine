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

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
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

import com.b3dgs.lionengine.core.Core;
import com.b3dgs.lionengine.core.UtilityMedia;
import com.b3dgs.lionengine.editor.Activator;
import com.b3dgs.lionengine.editor.project.ProjectGenerator;

/**
 * Represents the new project dialog.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class NewProjectDialog
{
    /** Error icon. */
    static final Image ICON_ERROR = Activator.getIcon("dialog", "error.png");
    /** Project name regex. */
    private static final String REGEX_PROJECT_NAME = "[a-zA-Z0-9_\\-\\s]*";
    /** Package regex. */
    private static final String REGEX_PROJECT_PACKAGE = "([\\p{L}_$][\\p{L}\\p{N}_$]*\\.)*[\\p{L}_$][\\p{L}\\p{N}_$]*";
    /** Default project name. */
    private static final String DEFAULT_NAME = "myproject";
    /** Default sources folder. */
    private static final String DEFAULT_SOURCES = "src";
    /** Default resources folder. */
    private static final String DEFAULT_RESOURCES = "resources";
    /** Default package. */
    private static final String DEFAULT_PACKAGE = "com.company." + NewProjectDialog.DEFAULT_NAME;
    /** Maximum characters input. */
    private static final int MAX_CHAR = 64;
    /** Bottom button width. */
    private static final int BOTTOM_BUTTON_WIDTH = 96;

    /**
     * Create the header part of the dialog.
     * 
     * @param header The header composite.
     */
    private static void createHeader(Composite header)
    {
        final Composite titleArea = new Composite(header, SWT.NONE);
        titleArea.setLayout(new GridLayout(1, false));
        titleArea.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        titleArea.setBackground(titleArea.getDisplay().getSystemColor(SWT.COLOR_WHITE));

        final Label title = new Label(titleArea, SWT.NONE);
        final FontData data = title.getFont().getFontData()[0];
        data.setHeight(10);
        data.setStyle(SWT.BOLD);
        title.setFont(new Font(title.getDisplay(), data));
        title.setBackground(title.getDisplay().getSystemColor(SWT.COLOR_WHITE));
        title.setText(Messages.NewProjectDialog_1);

        final Label text = new Label(titleArea, SWT.NONE);
        text.setBackground(text.getDisplay().getSystemColor(SWT.COLOR_WHITE));
        text.setText(Messages.NewProjectDialog_2);

        final Label icon = new Label(header, SWT.NONE);
        icon.setImage(Activator.getIcon("dialog", "new-project.png")); //$NON-NLS-1$
        icon.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, true, false));
        icon.setBackground(icon.getDisplay().getSystemColor(SWT.COLOR_WHITE));
    }

    /** Dialog shell. */
    final Shell dialog;
    /** Project name. */
    Text projectNameText;
    /** Project location. */
    Text projectLocationText;
    /** Project sources. */
    Text projectSourcesText;
    /** Project resources. */
    Text projectResourcesText;
    /** Generate base code. */
    Button generateCheck;
    /*** Project package. */
    Text packageText;
    /** Finish button. */
    Button finish;
    /** Error label. */
    CLabel error;

    /**
     * Constructor.
     * 
     * @param parent The parent reference.
     */
    public NewProjectDialog(Shell parent)
    {
        dialog = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
        dialog.setMinimumSize(500, 300);
        final GridLayout dialogLayout = new GridLayout(1, false);
        dialogLayout.marginHeight = 0;
        dialogLayout.marginWidth = 0;
        dialogLayout.verticalSpacing = 0;
        dialog.setLayout(dialogLayout);
        dialog.setText(Messages.NewProjectDialog_0);
        dialog.setImage(Activator.getIcon("product.png")); //$NON-NLS-1$
        createDialog();
    }

    /**
     * Open the dialog.
     */
    public void open()
    {
        dialog.pack(true);
        Activator.center(dialog);
        dialog.open();
    }

    /**
     * Create the dialog.
     */
    private void createDialog()
    {
        final Composite header = new Composite(dialog, SWT.NONE);
        header.setLayout(new GridLayout(2, false));
        header.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        NewProjectDialog.createHeader(header);
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
    private void createContent(Composite content)
    {
        createProjectNameArea(content);
        createProjectLocationArea(content);

        final Group folders = new Group(content, SWT.SHADOW_NONE);
        folders.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true));
        folders.setLayout(new GridLayout(1, false));
        folders.setText(Messages.NewProjectDialog_6);
        createProjectSourcesArea(folders);
        createProjectResourcesArea(folders);

        createGenerateBox(content);
    }

    /**
     * Create the project name area.
     * 
     * @param content The content composite.
     */
    private void createProjectNameArea(Composite content)
    {
        final Composite nameArea = new Composite(content, SWT.NONE);
        nameArea.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        nameArea.setLayout(new GridLayout(2, false));

        final Label nameLabel = new Label(nameArea, SWT.NONE);
        nameLabel.setText(Messages.NewProjectDialog_3);

        projectNameText = new Text(nameArea, SWT.BORDER);
        projectNameText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        projectNameText.setTextLimit(NewProjectDialog.MAX_CHAR);
        projectNameText.setText(NewProjectDialog.DEFAULT_NAME);
        projectNameText.forceFocus();
        projectNameText.addFocusListener(new FocusAdapter()
        {
            @Override
            public void focusGained(FocusEvent focusEvent)
            {
                projectNameText.selectAll();
            }
        });
        projectNameText.addVerifyListener(new VerifyListener()
        {
            @Override
            public void verifyText(VerifyEvent verifyEvent)
            {
                verifyEvent.doit = verifyEvent.text.matches(NewProjectDialog.REGEX_PROJECT_NAME);
            }
        });
    }

    /**
     * Create the project location area.
     * 
     * @param content The content composite.
     */
    private void createProjectLocationArea(Composite content)
    {
        final Composite nameArea = new Composite(content, SWT.NONE);
        nameArea.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        nameArea.setLayout(new GridLayout(3, false));

        final Label locationLabel = new Label(nameArea, SWT.NONE);
        locationLabel.setText(Messages.NewProjectDialog_9);

        projectLocationText = new Text(nameArea, SWT.BORDER);
        projectLocationText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        projectLocationText.setText(UtilityMedia.WORKING_DIR + Core.MEDIA.getSeparator());
        projectLocationText.setEditable(false);

        final Button browse = new Button(nameArea, SWT.PUSH);
        final GridData browseData = new GridData();
        browseData.widthHint = 64;
        browse.setLayoutData(browseData);
        browse.setText(Messages.NewProjectDialog_10);
        browse.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent)
            {
                final DirectoryDialog directoryDialog = new DirectoryDialog(dialog, SWT.APPLICATION_MODAL);
                final String path = directoryDialog.open();
                if (path != null)
                {
                    projectLocationText.setText(path + Core.MEDIA.getSeparator());
                }
            }
        });
    }

    /**
     * Create the project sources area chooser.
     * 
     * @param content The content composite.
     */
    private void createProjectSourcesArea(Composite content)
    {
        final Composite sourcesArea = new Composite(content, SWT.NONE);
        sourcesArea.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        sourcesArea.setLayout(new GridLayout(3, false));

        final Label sourcesLabel = new Label(sourcesArea, SWT.NONE);
        final GridData sourcesData = new GridData();
        sourcesData.widthHint = 64;
        sourcesLabel.setLayoutData(sourcesData);
        sourcesLabel.setText(Messages.NewProjectDialog_7);

        projectSourcesText = new Text(sourcesArea, SWT.BORDER);
        projectSourcesText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        projectSourcesText.setTextLimit(NewProjectDialog.MAX_CHAR);
        projectSourcesText.setText(NewProjectDialog.DEFAULT_SOURCES);
    }

    /**
     * Create the project resources area chooser.
     * 
     * @param content The content composite.
     */
    private void createProjectResourcesArea(Composite content)
    {
        final Composite resourcesArea = new Composite(content, SWT.NONE);
        resourcesArea.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        resourcesArea.setLayout(new GridLayout(3, false));

        final Label resourcesLabel = new Label(resourcesArea, SWT.NONE);
        final GridData resourcesData = new GridData();
        resourcesData.widthHint = 64;
        resourcesLabel.setLayoutData(resourcesData);
        resourcesLabel.setText(Messages.NewProjectDialog_8);

        projectResourcesText = new Text(resourcesArea, SWT.BORDER);
        projectResourcesText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        projectResourcesText.setTextLimit(NewProjectDialog.MAX_CHAR);
        projectResourcesText.setText(NewProjectDialog.DEFAULT_RESOURCES);
    }

    /**
     * Create the generate option check box.
     * 
     * @param content The content composite.
     */
    private void createGenerateBox(Composite content)
    {
        final Composite generateArea = new Composite(content, SWT.NONE);
        generateArea.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        generateArea.setLayout(new GridLayout(3, false));

        generateCheck = new Button(generateArea, SWT.CHECK);
        generateCheck.setText(Messages.NewProjectDialog_11);

        final Label packageLabel = new Label(generateArea, SWT.NONE);
        packageLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
        packageLabel.setText(Messages.NewProjectDialog_12);
        packageLabel.setVisible(false);

        packageText = new Text(generateArea, SWT.BORDER);
        final GridData packageData = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
        packageData.widthHint = 200;
        packageText.setLayoutData(packageData);
        packageText.setText(NewProjectDialog.DEFAULT_PACKAGE);
        packageText.addFocusListener(new FocusAdapter()
        {
            @Override
            public void focusGained(FocusEvent focusEvent)
            {
                packageText.selectAll();
            }
        });
        packageText.addModifyListener(new ModifyListener()
        {
            @Override
            public void modifyText(ModifyEvent modifyEvent)
            {
                final boolean match = packageText.getText().matches(NewProjectDialog.REGEX_PROJECT_PACKAGE);
                finish.setEnabled(match);
                if (!match)
                {
                    error.setText(Messages.NewProjectDialog_13);
                    error.setImage(NewProjectDialog.ICON_ERROR);
                    error.pack(true);
                }
                error.setVisible(!match);
            }
        });
        packageText.setVisible(false);

        generateCheck.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent)
            {
                final boolean visible = generateCheck.getSelection();
                packageLabel.setVisible(visible);
                packageText.setVisible(visible);
                packageText.forceFocus();
                error.setVisible(visible);
                if (!visible)
                {
                    finish.setEnabled(true);
                }
            }
        });
    }

    /**
     * Create the bottom part of the dialog.
     * 
     * @param bottom The bottom composite.
     */
    private void createBottom(Composite bottom)
    {
        error = new CLabel(bottom, SWT.LEFT_TO_RIGHT);
        error.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        error.setVisible(false);

        final Composite buttonArea = new Composite(bottom, SWT.NONE);
        buttonArea.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
        buttonArea.setLayout(new GridLayout(2, false));

        finish = new Button(buttonArea, SWT.PUSH);
        final GridData finishData = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
        finishData.widthHint = NewProjectDialog.BOTTOM_BUTTON_WIDTH;
        finish.setLayoutData(finishData);
        finish.setText(Messages.NewProjectDialog_5);
        finish.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent)
            {
                final String name = projectNameText.getText();
                final File location = new File(projectLocationText.getText());
                final String sources = projectSourcesText.getText();
                final String resources = projectResourcesText.getText();
                final boolean generate = generateCheck.getSelection();
                final ProjectGenerator createProject = new ProjectGenerator(name, location, sources, resources);
                createProject.create();
                if (generate)
                {
                    createProject.generate(packageText.getText());
                }
                dialog.dispose();
            }
        });

        final Button cancel = new Button(buttonArea, SWT.PUSH);
        final GridData cancelData = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
        cancelData.widthHint = NewProjectDialog.BOTTOM_BUTTON_WIDTH;
        cancel.setLayoutData(cancelData);
        cancel.setText(Messages.NewProjectDialog_4);
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
