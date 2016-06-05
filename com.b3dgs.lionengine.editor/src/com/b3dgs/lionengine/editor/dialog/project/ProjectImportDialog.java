/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.editor.dialog.project;

import java.io.File;
import java.io.IOException;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.editor.dialog.AbstractDialog;
import com.b3dgs.lionengine.editor.project.ProjectFactory;
import com.b3dgs.lionengine.editor.utility.UtilIcon;
import com.b3dgs.lionengine.util.UtilFolder;

/**
 * Represents the new project dialog.
 */
public class ProjectImportDialog extends AbstractProjectDialog
{
    /** Project imported verbose. */
    private static final String VERBOSE_PROJECT_IMPORTED = "Project imported: ";
    /** From verbose. */
    private static final String VERBOSE_FROM = " from ";
    /** Icon. */
    private static final Image ICON = UtilIcon.get("dialog", "import.png");

    /** Already exists. */
    private boolean hasProject;
    /** Classes exist. */
    private boolean hasClasses;
    /** Libraries exist. */
    private boolean hasLibraries;
    /** Resources exist. */
    private boolean hasResources;

    /**
     * Create an import project dialog.
     * 
     * @param parent The shell parent.
     */
    public ProjectImportDialog(Shell parent)
    {
        super(parent,
              Messages.ImportProjectDialog_Title,
              Messages.ImportProjectDialog_HeaderTitle,
              Messages.ImportProjectDialog_HeaderDesc,
              ICON);

        createDialog();
        dialog.setMinimumSize(512, 100);
        projectNameText.setEditable(false);
        projectLocationText.setEditable(false);
        projectClassesText.setEditable(false);
        projectClassesBrowseFolder.setEnabled(false);
        projectClassesBrowseJar.setEnabled(false);
        projectLibrariesText.setEditable(false);
        projectLibrariesBrowse.setEnabled(false);
        projectResourcesText.setEditable(false);
        projectResourcesBrowse.setEnabled(false);

        checkClassesExistence();
        checkResourcesExistence();
        updateTipsLabel();
        finish.forceFocus();
    }

    /**
     * Check if the sources folder already exists.
     */
    private void checkClassesExistence()
    {
        final String text = projectClassesText.getText();
        if (!text.isEmpty())
        {
            final File sourcePath = new File(UtilFolder.getPath(projectLocationText.getText(), text));
            hasClasses = sourcePath.exists();
        }
        else
        {
            hasClasses = false;
        }
    }

    /**
     * Check if the libraries folder already exists.
     */
    private void checkLibrariesExistence()
    {
        final String text = projectLibrariesText.getText();
        if (!text.isEmpty())
        {
            final File librariesPath = new File(UtilFolder.getPath(projectLocationText.getText(), text));
            hasLibraries = librariesPath.exists();
        }
        else
        {
            hasLibraries = false;
        }
    }

    /**
     * Check if the resources folder already exists.
     */
    private void checkResourcesExistence()
    {
        final String text = projectResourcesText.getText();
        if (!text.isEmpty())
        {
            final File resourcePath = new File(UtilFolder.getPath(projectLocationText.getText(), text));
            hasResources = resourcePath.exists();
        }
        else
        {
            hasResources = false;
        }
    }

    /**
     * Update the tips label.
     */
    private void updateTipsLabel()
    {
        tipsLabel.setVisible(false);
        boolean enabled = true;
        if (!hasClasses && !hasResources && !hasLibraries)
        {
            setTipsMessage(AbstractDialog.ICON_INFO, Messages.ImportProjectDialog_InfoBoth);
            enabled = false;
        }
        else if (!hasClasses)
        {
            setTipsMessage(AbstractDialog.ICON_INFO, Messages.ImportProjectDialog_InfoClasses);
            enabled = false;
        }
        else if (!hasLibraries)
        {
            setTipsMessage(AbstractDialog.ICON_INFO, Messages.ImportProjectDialog_InfoLibraries);
            enabled = false;
        }
        else if (!hasResources)
        {
            setTipsMessage(AbstractDialog.ICON_INFO, Messages.ImportProjectDialog_InfoResources);
            enabled = false;
        }
        finish.setEnabled(enabled);
    }

    /**
     * Generate the project properties.
     * 
     * @param location The project location destination.
     */
    private void generateProperties(File location)
    {
        final String classes = projectClassesText.getText();
        final String libraries = projectLibrariesText.getText();
        final String resources = projectResourcesText.getText();
        try
        {
            ProjectFactory.createProperties(location, resources, classes, libraries);
        }
        catch (final IOException exception)
        {
            Verbose.exception(exception);
            MessageDialog.openError(dialog,
                                    Messages.ImportProjectDialog_ErrorTitle,
                                    Messages.ImportProjectDialog_ErrorText);
        }
    }

    /**
     * Create the project at the specified location.
     * 
     * @param location The project location destination.
     */
    private void createProject(File location)
    {
        final String name = projectNameText.getText();
        try
        {
            project = ProjectFactory.create(location);
            Verbose.info(VERBOSE_PROJECT_IMPORTED, name, VERBOSE_FROM, location.getAbsolutePath());
        }
        catch (final IOException exception)
        {
            Verbose.exception(exception);
            MessageDialog.openError(dialog,
                                    Messages.ImportProjectDialog_ErrorTitle,
                                    Messages.ImportProjectDialog_ErrorText);
        }
    }

    /**
     * Check if the project is not already existing.
     */
    private void checkProjectExistence()
    {
        final File projectPath = new File(projectLocationText.getText());
        hasProject = ProjectFactory.exists(projectPath);
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
     * Fill text fields with project properties.
     * 
     * @throws IOException If error when reading properties.
     */
    private void fillProjectProperties() throws IOException
    {
        final File projectPath = new File(projectLocationText.getText());
        final ProjectFactory.Info info = ProjectFactory.getInfo(projectPath);
        if (info != null)
        {
            projectClassesText.setText(info.getClasses());
            projectLibrariesText.setText(info.getLibraries());
            projectResourcesText.setText(info.getResources());
        }

        tipsLabel.setVisible(false);
        finish.setEnabled(true);
    }

    /*
     * AbstractProjectDialog
     */

    @Override
    protected void onLocationSelected(String path)
    {
        if (path != null)
        {
            projectLocationText.setText(path);
            final File location = new File(path);
            projectNameText.setText(location.getName());
            checkProjectExistence();
            updateTipsLabel();
            try
            {
                if (hasProject)
                {
                    fillProjectProperties();
                }
                projectClassesText.setEditable(true);
                projectClassesBrowseFolder.setEnabled(true);
                projectClassesBrowseJar.setEnabled(true);
                projectLibrariesText.setEditable(true);
                projectLibrariesBrowse.setEnabled(true);
                projectResourcesText.setEditable(true);
                projectResourcesBrowse.setEnabled(true);
            }
            catch (final IOException exception)
            {
                Verbose.exception(exception, Messages.ImportProjectDialog_InvalidImport);
                setTipsMessage(AbstractDialog.ICON_ERROR, Messages.ImportProjectDialog_InvalidImport);
                tipsLabel.setVisible(true);
                finish.setEnabled(false);
            }
        }
    }

    @Override
    protected void createProjectClassesArea(Composite content)
    {
        super.createProjectClassesArea(content);

        projectClassesText.addModifyListener(modifyEvent ->
        {
            checkClassesExistence();
            updateTipsLabel();
        });
    }

    @Override
    protected void createProjectLibrariesArea(Composite content)
    {
        super.createProjectLibrariesArea(content);

        projectLibrariesText.addModifyListener(modifyEvent ->
        {
            checkLibrariesExistence();
            updateTipsLabel();
        });
    }

    @Override
    protected void createProjectResourcesArea(Composite content)
    {
        super.createProjectResourcesArea(content);

        projectResourcesText.addModifyListener(modifyEvent ->
        {
            checkResourcesExistence();
            updateTipsLabel();
        });
    }

    @Override
    protected void onFinish()
    {
        final File projectPath = new File(projectLocationText.getText());
        try
        {
            final ProjectFactory.Info info = ProjectFactory.getInfo(projectPath);
            if (info == null
                || !info.getResources().equals(projectResourcesText.getText())
                || !info.getClasses().equals(projectClassesText.getText())
                || !info.getLibraries().equals(projectLibrariesText.getText()))
            {
                generateProperties(projectPath);
            }
        }
        catch (final IOException exception)
        {
            Verbose.exception(exception);
        }
        createProject(projectPath);
    }
}
