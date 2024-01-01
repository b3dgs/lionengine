/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.b3dgs.lionengine.editor.project;

import java.io.File;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;

/**
 * Represents a project and its data.
 */
public final class Project
{
    /** Media is not in class folder. */
    private static final String ERROR_MEDIA_RELATIVE_TO_CLASS = "Media is not in class folder: ";
    /** Media is not in resources folder. */
    private static final String ERROR_MEDIA_RELATIVE_TO_RESOURCES = "Media is not in resources folder: ";

    /**
     * Get a media relative to the from path. Must be in from folder.
     * 
     * @param file The resource file.
     * @param from The folder source.
     * @param error The error message.
     * @return The relative media.
     * @throws LionEngineException If not relative to expected folder.
     */
    private static Media getRelativeMedia(File file, File from, String error)
    {
        final String fromPath = from.getPath();
        final String path = file.getAbsolutePath();
        if (!path.startsWith(fromPath))
        {
            throw new LionEngineException(error + path);
        }
        if (fromPath.length() == path.length())
        {
            // Media folder itself
            return Medias.create(Constant.EMPTY_STRING);
        }
        final int fromPrefix = fromPath.length() + 1;
        final String relativePath = path.substring(fromPrefix);
        return Medias.create(relativePath);
    }

    /** Project path. */
    private final File path;
    /** Project name. */
    private final String name;
    /** Classes folder (represents the main classes folder, such as <code>target/</code>). */
    private final String classes;
    /** Library folder (represents the libraries folder, such as <code>dependencies/</code>)). */
    private final String libraries;
    /** Resources folder (represents the main resources folder, such as <code>resources/</code>. */
    private final String resources;
    /** Project class loader. */
    private final ProjectClassLoader loader;

    /**
     * Private constructor.
     * 
     * @param path The project absolute path.
     * @param resourcesFolder The resources folder path relative to project path.
     * @param classesFolder The classes folder path relative to project path.
     * @param librariesFolder The libraries folder path relative to project path.
     */
    Project(File path, String resourcesFolder, String classesFolder, String librariesFolder)
    {
        this.path = path;
        resources = resourcesFolder;
        classes = classesFolder;
        libraries = librariesFolder;
        name = path.getName();
        loader = new ProjectClassLoader(getClassesPath(), getLibrariesPath());
    }

    /**
     * Get a class media relative to the classes path. Must be in {@link Project#getClassesPath()} folder.
     * 
     * @param file The class file.
     * @return The relative class media.
     * @throws LionEngineException If not relative to the expected folder.
     */
    public Media getClassMedia(File file)
    {
        return getRelativeMedia(file, getClassesPath(), ERROR_MEDIA_RELATIVE_TO_CLASS);
    }

    /**
     * Get a media relative to the resources path. Must be in {@link Project#getResourcesPath()} folder.
     * 
     * @param file The resource file.
     * @return The relative media.
     * @throws LionEngineException If not relative to expected folder.
     */
    public Media getResourceMedia(File file)
    {
        return getRelativeMedia(file, getResourcesPath(), ERROR_MEDIA_RELATIVE_TO_RESOURCES);
    }

    /**
     * Get the project folder path.
     * 
     * @return The project folder path.
     */
    public File getPath()
    {
        return path;
    }

    /**
     * Get the project name.
     * 
     * @return The project name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Get the classes path. Folder where are stored project classes.
     * 
     * @return The classes path.
     */
    public File getClassesPath()
    {
        return new File(path, classes);
    }

    /**
     * Get the libraries path. Folder where are stored external libraries.
     * 
     * @return The libraries path.
     */
    public File getLibrariesPath()
    {
        return new File(path, libraries);
    }

    /**
     * Get the resources path. Folder where are stored project resources.
     * 
     * @return The resources path.
     */
    public File getResourcesPath()
    {
        return new File(path, resources);
    }

    /**
     * Get the classes folder name, relative to the project path.
     * 
     * @return The classes folder name.
     */
    public String getClasses()
    {
        return classes;
    }

    /**
     * Get the resources folder name, relative to the project path.
     * 
     * @return The resources folder name.
     */
    public String getResources()
    {
        return resources;
    }

    /**
     * Get the project class loader.
     * 
     * @return The project class loader.
     */
    public ProjectClassLoader getLoader()
    {
        return loader;
    }
}
