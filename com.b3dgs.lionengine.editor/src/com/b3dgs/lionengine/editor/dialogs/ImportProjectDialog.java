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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.UtilFile;
import com.b3dgs.lionengine.core.Verbose;
import com.b3dgs.lionengine.editor.UtilEclipse;
import com.b3dgs.lionengine.editor.project.Project;
import com.b3dgs.lionengine.editor.project.ProjectGenerator;

/**
 * Represents the new project dialog.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class ImportProjectDialog
        extends AbstractProjectDialog
{
    /** Project imported verbose. */
    private static final String VERBOSE_PROJECT_IMPORTED = "Project imported: ";
    /** From verbose. */
    private static final String VERBOSE_FROM = " from ";
    /** Icon. */
    private static final Image ICON = UtilEclipse.getIcon("dialog", "import-project.png");

    /** Already exists. */
    private boolean hasProject;
    /** Classes exist. */
    private boolean hasClasses;
    /** Libraries exist. */
    private boolean hasLibraries;
    /** Resources exist. */
    private boolean hasResources;

    /**
     * Constructor.
     * 
     * @param parent The shell parent.
     */
    public ImportProjectDialog(Shell parent)
    {
        super(parent, Messages.ImportProjectDialogTitle, Messages.ImportProjectDialog_HeaderTitle,
                Messages.ImportProjectDialog_HeaderDesc, ImportProjectDialog.ICON);
        createDialog();
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
    void checkClassesExistence()
    {
        final String text = projectClassesText.getText();
        if (!text.isEmpty())
        {
            final File sourcePath = new File(UtilFile.getPath(projectLocationText.getText(), text));
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
    void checkLibrariesExistence()
    {
        final String text = projectLibrariesText.getText();
        if (!text.isEmpty())
        {
            final File librariesPath = new File(UtilFile.getPath(projectLocationText.getText(), text));
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
    void checkResourcesExistence()
    {
        final String text = projectResourcesText.getText();
        if (!text.isEmpty())
        {
            final File resourcePath = new File(UtilFile.getPath(projectLocationText.getText(), text));
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
    void updateTipsLabel()
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
     * Check if the project is not already existing.
     */
    private void checkProjectExistence()
    {
        final File projectPath = new File(projectLocationText.getText());
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
     * Fill text fields with project properties.
     * 
     * @throws IOException If error when reading properties.
     */
    private void fillProjectProperties() throws IOException
    {
        final File file = new File(projectLocationText.getText(), Project.PROPERTIES_FILE);
        try (InputStream inputStream = new FileInputStream(file))
        {
            final Properties properties = new Properties();
            properties.load(inputStream);

            final String classes = properties.getProperty(Project.PROPERTY_PROJECT_CLASSES, "");
            final String libraries = properties.getProperty(Project.PROPERTY_PROJECT_LIBRARIES, "");
            final String resources = properties.getProperty(Project.PROPERTY_PROJECT_RESOURCES, "");
            projectClassesText.setText(classes);
            projectLibrariesText.setText(libraries);
            projectResourcesText.setText(resources);
            tipsLabel.setVisible(false);
            finish.setEnabled(true);
        }
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
            catch (final LionEngineException
                         | IOException exception)
            {
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
    protected void createProjectLibrariesArea(Composite content)
    {
        super.createProjectLibrariesArea(content);

        projectLibrariesText.addModifyListener(new ModifyListener()
        {
            @Override
            public void modifyText(ModifyEvent modifyEvent)
            {
                checkLibrariesExistence();
                updateTipsLabel();
            }
        });
    }

    @Override
    protected void createProjectResourcesArea(Composite content)
    {
        super.createProjectResourcesArea(content);

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

    @Override
    protected void onFinish()
    {
        final File location = new File(projectLocationText.getText());
        generateProperties(location);
        createProject(location);
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
        final ProjectGenerator createProject = new ProjectGenerator(classes, libraries, resources);
        try
        {
            createProject.createProperties(location);
        }
        catch (final IOException exception)
        {
            Verbose.exception(getClass(), "generateProperties", exception);
            MessageDialog.openError(dialog, Messages.ImportProjectDialog_ErrorTitle,
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
            project = Project.create(location);
            Verbose.info(ImportProjectDialog.VERBOSE_PROJECT_IMPORTED, name, ImportProjectDialog.VERBOSE_FROM,
                    location.getAbsolutePath());
        }
        catch (final IOException exception)
        {
            Verbose.exception(getClass(), "createProject", exception);
            MessageDialog.openError(dialog, Messages.ImportProjectDialog_ErrorTitle,
                    Messages.ImportProjectDialog_ErrorText);
        }
    }
}
