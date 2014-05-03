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
package com.b3dgs.lionengine.editor.project;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Properties;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.UtilityConversion;
import com.b3dgs.lionengine.UtilityFile;
import com.b3dgs.lionengine.editor.Activator;

/**
 * Generate a project from scratch, designed to be used from the new project dialog.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class ProjectGenerator
{
    /** Regex java package. */
    private static final String REGEX_JAVA_PACKAGE = "\\.";
    /** Regex non special character. */
    public static final String REGEX_NON_SPECIAL_CHAR = "[^\\s\\w]+";
    /** Template project package. */
    private static final String TEMPLATE_PROJECT_PACKAGE = "%PROJECT_PACKAGE%";
    /** Template class name. */
    private static final String TEMPLATE_CLASS_NAME = "%CLASS_NAME%";
    /** Template project name. */
    private static final String TEMPLATE_PROJECT_NAME = "%PROJECT_NAME%";
    /** Template project resources. */
    private static final String TEMPLATE_PROJECT_RESOURCES = "%PROJECT_RESOURCES%";
    /** Template main prefix. */
    private static final String TEMPLATE_MAIN_PREFIX = "App";
    /** Template main file. */
    private static final String TEMPLATE_MAIN_FILE = UtilityFile.getPath("templates", "main.template");
    /** Template scene file. */
    private static final String TEMPLATE_SCENE_FILE = UtilityFile.getPath("templates", "scene.template");
    /** Template get path function. */
    private static final String TEMPLATE_GET_PATH_FUNCTION = "UtilityFile.getPath";
    /** Error project location. */
    private static final String ERROR_PROJECT_LOCATION = "Illegal project location: ";
    /** Error project folder. */
    private static final String ERROR_PROJECT_FOLDER = "Unable to create the project folder: ";
    /** Error sources folder. */
    private static final String ERROR_PROJECT_SOURCES = "Unable to create the sources folder: ";
    /** Error resources folder. */
    private static final String ERROR_PROJECT_RESOURCES = "Unable to create the resources folder: ";
    /** Error properties file. */
    private static final String ERROR_PROJECT_PROPERTIES = "Unable to create the project properties file !";
    /** Error access project folder. */
    private static final String ERROR_ACCESS_PROJECT_SOURCES = "Unable to access to the project sources !";
    /** Error generate project. */
    private static final String ERROR_GENERATE_PROJECT = "Error when generating project !";

    /**
     * Create the folder and all its sub directory if needed.
     * 
     * @param root The root folder.
     * @param path The path to create folder by folder.
     * @param error The error message.
     */
    private static void createAllFolders(File root, String path, String error)
    {
        File parent = root;
        for (final String currentResource : path.split(ProjectGenerator.REGEX_NON_SPECIAL_CHAR))
        {
            final File folder = new File(parent, currentResource);
            if (!folder.exists())
            {
                if (!folder.mkdir())
                {
                    throw new LionEngineException(error, folder.getPath());
                }
            }
            parent = folder;
        }
    }

    /**
     * Generate the full source folder from the package.
     * 
     * @param sourcesPath The main sources path.
     * @param projectPackage The project package.
     * @return The last source folder.
     */
    private static File generateSourcePackageFolder(File sourcesPath, String projectPackage)
    {
        File parent = sourcesPath;
        for (final String currentPackage : projectPackage.split(ProjectGenerator.REGEX_JAVA_PACKAGE))
        {
            final File folder = new File(parent, currentPackage);
            if (!folder.exists())
            {
                if (!folder.mkdir())
                {
                    throw new LionEngineException(ProjectGenerator.ERROR_PROJECT_SOURCES, folder.getPath());
                }
            }
            parent = folder;
        }
        return parent;
    }

    /**
     * Generate the scene file from its template.
     * 
     * @param parent The parent folder.
     * @param projectPackage The project package.
     * @throws IOException If error.
     */
    private static void generateSceneFromTemplate(File parent, String projectPackage) throws IOException
    {
        final File template = Activator.getFile(ProjectGenerator.TEMPLATE_SCENE_FILE);
        String content = new String(Files.readAllBytes(template.toPath()), StandardCharsets.UTF_8);
        content = content.replace(ProjectGenerator.TEMPLATE_PROJECT_PACKAGE, projectPackage);

        final String filename = "Scene.java";
        Files.write(new File(parent, filename).toPath(), content.getBytes(StandardCharsets.UTF_8));
    }

    /** Project name. */
    private final String name;
    /** Project location. */
    private final File location;
    /** Sources folder. */
    private final String sources;
    /** Resources folder. */
    private final String resources;

    /**
     * Constructor.
     * 
     * @param name The project name.
     * @param location The project location.
     * @param sources The source code folder.
     * @param resources The resources folder.
     */
    public ProjectGenerator(String name, File location, String sources, String resources)
    {
        this.name = name;
        this.location = location;
        this.sources = sources;
        this.resources = resources;
    }

    /**
     * Create the project and its folders. Generate code if required.
     * 
     * @return The project path.
     * @throws LionEngineException If not able to create the project.
     */
    public File create() throws LionEngineException
    {
        if (!location.isDirectory())
        {
            throw new LionEngineException(ProjectGenerator.ERROR_PROJECT_LOCATION, location.getPath());
        }
        final File projectPath = new File(location, name);
        createFolders(projectPath);
        createProperties(projectPath);
        return projectPath;
    }

    /**
     * Generate the project base code.
     * 
     * @param projectPackage The project package.
     * @throws LionEngineException If not able to generate the files.
     */
    public void generate(String projectPackage) throws LionEngineException
    {
        final File projectPath = new File(location, name);
        final File sourcesPath = new File(projectPath, sources);
        if (!sourcesPath.isDirectory())
        {
            throw new LionEngineException(ProjectGenerator.ERROR_ACCESS_PROJECT_SOURCES, sourcesPath.getPath());
        }

        final File parent = ProjectGenerator.generateSourcePackageFolder(sourcesPath, projectPackage);
        try
        {
            generateMainFromTemplate(parent, projectPackage);
            ProjectGenerator.generateSceneFromTemplate(parent, projectPackage);
        }
        catch (final IOException exception)
        {
            throw new LionEngineException(exception, ProjectGenerator.ERROR_GENERATE_PROJECT);
        }
    }

    /**
     * Generate the main file from its template.
     * 
     * @param parent The parent folder.
     * @param projectPackage The project package.
     * @throws IOException If error.
     */
    private void generateMainFromTemplate(File parent, String projectPackage) throws IOException
    {
        final File template = Activator.getFile(ProjectGenerator.TEMPLATE_MAIN_FILE);
        String content = new String(Files.readAllBytes(template.toPath()), StandardCharsets.UTF_8);
        content = content.replace(ProjectGenerator.TEMPLATE_PROJECT_PACKAGE, projectPackage);

        final String className = UtilityConversion.toTitleCase(name);
        content = content.replace(ProjectGenerator.TEMPLATE_CLASS_NAME, className);
        content = content.replace(ProjectGenerator.TEMPLATE_PROJECT_NAME, name);

        final String[] split = resources.split(ProjectGenerator.REGEX_NON_SPECIAL_CHAR);
        final StringBuilder stringResources;
        if (split.length == 1)
        {
            stringResources = new StringBuilder("\"").append(resources).append("\"");
        }
        else
        {
            stringResources = new StringBuilder(ProjectGenerator.TEMPLATE_GET_PATH_FUNCTION + "(");
            for (int i = 0; i < split.length; i++)
            {
                stringResources.append("\"").append(split[i]).append("\"");
                if (i < split.length - 1)
                {
                    stringResources.append(", ");
                }
            }
            stringResources.append(")");
        }

        content = content.replace(ProjectGenerator.TEMPLATE_PROJECT_RESOURCES, stringResources.toString());

        final String filename = ProjectGenerator.TEMPLATE_MAIN_PREFIX + UtilityConversion.toTitleCase(name) + ".java";
        Files.write(new File(parent, filename).toPath(), content.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Create the project folders.
     * 
     * @param projectPath The project path.
     * @throws LionEngineException If not able to create the folders.
     */
    private void createFolders(File projectPath) throws LionEngineException
    {
        if (!projectPath.exists())
        {
            if (!projectPath.mkdir())
            {
                throw new LionEngineException(ProjectGenerator.ERROR_PROJECT_FOLDER, projectPath.getPath());
            }
        }
        ProjectGenerator.createAllFolders(projectPath, sources, ProjectGenerator.ERROR_PROJECT_SOURCES);
        ProjectGenerator.createAllFolders(projectPath, resources, ProjectGenerator.ERROR_PROJECT_RESOURCES);
    }

    /**
     * Create the project properties file.
     * 
     * @param projectPath The project path.
     * @throws LionEngineException If not able to create the project properties file.
     */
    public void createProperties(File projectPath) throws LionEngineException
    {
        final File propertiesFile = new File(projectPath, Project.PROPERTIES_FILE);
        final Properties properties = new Properties();
        properties.put(Project.PROPERTY_PROJECT_SOURCES, sources);
        properties.put(Project.PROPERTY_PROJECT_RESOURCES, resources);
        try
        {
            properties.store(new FileOutputStream(propertiesFile), Project.PROPERTIES_FILE_DESCRIPTION);
        }
        catch (final IOException exception)
        {
            throw new LionEngineException(exception, ProjectGenerator.ERROR_PROJECT_PROPERTIES);
        }
    }
}
