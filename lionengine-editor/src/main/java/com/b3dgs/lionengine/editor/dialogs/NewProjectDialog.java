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
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.b3dgs.lionengine.UtilityFile;
import com.b3dgs.lionengine.core.Core;
import com.b3dgs.lionengine.core.UtilityMedia;
import com.b3dgs.lionengine.editor.Activator;
import com.b3dgs.lionengine.editor.project.Project;
import com.b3dgs.lionengine.editor.project.ProjectGenerator;

/**
 * Represents the new project dialog.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class NewProjectDialog
        extends AbstractProjectDialog
{
    /** Icon. */
    private static final Image ICON = Activator.getIcon("dialog", "new-project.png");
    /** Project name regex. */
    private static final String REGEX_PROJECT_NAME = "[a-zA-Z0-9_\\-\\s]*";
    /** Package regex. */
    private static final String REGEX_PROJECT_PACKAGE = "([\\p{L}_$][\\p{L}\\p{N}_$]*\\.)*[\\p{L}_$][\\p{L}\\p{N}_$]*";
    /** Default project name. */
    private static final String DEFAULT_NAME = "myproject";
    /** Default classes folder. */
    private static final String DEFAULT_CLASSES = "bin";
    /** Default resources folder. */
    private static final String DEFAULT_RESOURCES = "resources";
    /** Default package. */
    private static final String DEFAULT_PACKAGE = "com.company." + NewProjectDialog.DEFAULT_NAME;

    /** Generate base code. */
    Button generateCheck;
    /*** Project package. */
    Text packageText;
    /** Already exists. */
    boolean hasProject;
    /** Classes exist. */
    boolean hasClasses;
    /** Resources exist. */
    boolean hasResources;

    /**
     * Constructor.
     * 
     * @param parent The parent reference.
     */
    public NewProjectDialog(Shell parent)
    {
        super(parent, Messages.NewProjectDialog_Title, Messages.NewProjectDialog_HeaderTitle,
                Messages.NewProjectDialog_HeaderDesc, NewProjectDialog.ICON);
        createDialog();
        projectNameText.forceFocus();
        projectLocationText.setText(UtilityMedia.WORKING_DIR);
        finish.setEnabled(true);
        checkProjectExistence();
        updateTipsLabel();
    }

    /**
     * Check if the project is not already existing.
     */
    void checkProjectExistence()
    {
        final File projectPath = new File(UtilityFile.getPath(projectLocationText.getText(), projectNameText.getText()));
        final File projectProperties = new File(projectPath, Project.PROPERTIES_FILE);
        hasProject = projectProperties.isFile();
        if (hasProject)
        {
            finish.setEnabled(false);
        }
        else
        {
            finish.setEnabled(true);
        }
        checkClassesExistence();
        checkResourcesExistence();
    }

    /**
     * Check if the sources folder already exists.
     */
    void checkClassesExistence()
    {
        final File sourcePath = new File(UtilityFile.getPath(projectLocationText.getText(), projectNameText.getText(),
                projectClassesText.getText()));
        hasClasses = sourcePath.exists();
    }

    /**
     * Check if the resources folder already exists.
     */
    void checkResourcesExistence()
    {
        final File resourcePath = new File(UtilityFile.getPath(projectLocationText.getText(),
                projectNameText.getText(), projectResourcesText.getText()));
        hasResources = resourcePath.exists();
    }

    /**
     * Update the tips label.
     */
    void updateTipsLabel()
    {
        tipsLabel.setVisible(false);
        boolean visible = false;
        if (hasProject)
        {
            setTipsMessage(AbstractDialog.ICON_ERROR, Messages.NewProjectDialog_ErrorProjectExists);
            visible = true;
        }
        else if (hasClasses && hasResources)
        {
            setTipsMessage(AbstractDialog.ICON_INFO, Messages.NewProjectDialog_InfoBoth);
            visible = true;
        }
        else if (hasClasses)
        {
            setTipsMessage(AbstractDialog.ICON_INFO, Messages.NewProjectDialog_InfoClasses);
            visible = true;
        }
        else if (hasResources)
        {
            setTipsMessage(AbstractDialog.ICON_INFO, Messages.NewProjectDialog_InfoResources);
            visible = true;
        }
        if (projectNameText.getText().isEmpty() || projectClassesText.getText().isEmpty()
                || projectResourcesText.getText().isEmpty())
        {
            finish.setEnabled(false);
            visible = true;
        }
        else
        {
            finish.setEnabled(!visible);
        }
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
        generateCheck.setText(Messages.NewProjectDialog_Generate);

        final Label packageLabel = new Label(generateArea, SWT.NONE);
        packageLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
        packageLabel.setText(Messages.NewProjectDialog_Package);
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
                    setTipsMessage(AbstractDialog.ICON_ERROR, Messages.NewProjectDialog_ErrorPackage);
                }
                tipsLabel.setVisible(!match);
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
                tipsLabel.setVisible(visible);
                if (!visible)
                {
                    finish.setEnabled(true);
                }
            }
        });
    }

    /*
     * AbstractProjectDialog
     */

    @Override
    protected void onLocationSelected(String path)
    {
        if (path != null)
        {
            projectLocationText.setText(path + Core.MEDIA.getSeparator());
        }
        checkProjectExistence();
        updateTipsLabel();
    }

    @Override
    protected void onFinish()
    {
        final String name = projectNameText.getText();
        final File location = new File(projectLocationText.getText());
        final String classes = projectClassesText.getText();
        final String resources = projectResourcesText.getText();
        final boolean generate = generateCheck.getSelection();
        final ProjectGenerator createProject = new ProjectGenerator(name, location, classes, resources);
        project = Project.create(createProject.create());
        if (generate)
        {
            createProject.generate(packageText.getText());
        }
    }

    @Override
    protected void createContent(Composite content)
    {
        super.createContent(content);
        createGenerateBox(content);
    }

    @Override
    protected void createProjectNameArea(Composite content)
    {
        super.createProjectNameArea(content);
        projectNameText.setTextLimit(AbstractDialog.MAX_CHAR);
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
        projectNameText.addModifyListener(new ModifyListener()
        {
            @Override
            public void modifyText(ModifyEvent modifyEvent)
            {
                checkProjectExistence();
                updateTipsLabel();
            }
        });
    }

    @Override
    protected void createProjectClassesArea(Composite content)
    {
        super.createProjectClassesArea(content);

        projectClassesText.setText(NewProjectDialog.DEFAULT_CLASSES);
        projectClassesText.addModifyListener(new ModifyListener()
        {
            @Override
            public void modifyText(ModifyEvent modifyEvent)
            {
                checkClassesExistence();
                updateTipsLabel();
            }
        });
    }

    @Override
    protected void createProjectResourcesArea(Composite content)
    {
        super.createProjectResourcesArea(content);

        projectResourcesText.setText(NewProjectDialog.DEFAULT_RESOURCES);
        projectResourcesText.addModifyListener(new ModifyListener()
        {
            @Override
            public void modifyText(ModifyEvent modifyEvent)
            {
                checkResourcesExistence();
                updateTipsLabel();
            }
        });
    }
}
