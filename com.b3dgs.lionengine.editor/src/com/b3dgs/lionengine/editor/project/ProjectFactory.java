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
package com.b3dgs.lionengine.editor.project;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Verbose;

/**
 * Handle project creation.
 */
public final class ProjectFactory
{
    /** Properties file. */
    private static final String PROPERTIES_FILE = ".lionengine";
    /** Properties file description. */
    private static final String PROPERTIES_FILE_DESCRIPTION = "LionEngine project properties";
    /** Property project resources folder. */
    private static final String PROPERTY_PROJECT_RESOURCES = "ResourcesFolder";
    /** Property project classes folder. */
    private static final String PROPERTY_PROJECT_CLASSES = "ClassesFolder";
    /** Property project libraries folder. */
    private static final String PROPERTY_PROJECT_LIBRARIES = "LibrariesFolder";
    /** Reading project properties verbose. */
    private static final String VERBOSE_READ_PROJECT_PROPERTIES = "Reading project properties for: ";

    /**
     * Create the project properties file.
     * 
     * @param resources The resources folder.
     * @param classes The classes code folder.
     * @param libraries The libraries folder.
     * @param projectPath The project path.
     * @throws IOException If not able to create the project properties file.
     */
    public static void createProperties(File projectPath, String resources, String classes, String libraries)
            throws IOException
    {
        final Properties properties = new Properties();
        properties.put(PROPERTY_PROJECT_RESOURCES, resources);
        properties.put(PROPERTY_PROJECT_CLASSES, classes);
        properties.put(PROPERTY_PROJECT_LIBRARIES, libraries);

        final File propertiesFile = new File(projectPath, PROPERTIES_FILE);
        try (final FileOutputStream stream = new FileOutputStream(propertiesFile))
        {
            properties.store(stream, PROPERTIES_FILE_DESCRIPTION);
        }
    }

    /**
     * Get the project information from properties.
     * 
     * @param projectPath The properties path.
     * @return The project information
     * @throws IOException If not able to read the project properties file.
     */
    public static Optional<Info> getInfo(File projectPath) throws IOException
    {
        final File propertiesFile = new File(projectPath, PROPERTIES_FILE);
        if (!propertiesFile.isFile())
        {
            return Optional.empty();
        }
        try (final InputStream input = new FileInputStream(propertiesFile))
        {
            final Properties properties = new Properties();
            properties.load(input);

            final String resources = properties.getProperty(PROPERTY_PROJECT_RESOURCES, Constant.EMPTY_STRING);
            final String classes = properties.getProperty(PROPERTY_PROJECT_CLASSES, Constant.EMPTY_STRING);
            final String libraries = properties.getProperty(PROPERTY_PROJECT_LIBRARIES, Constant.EMPTY_STRING);

            return Optional.of(new Info(resources, classes, libraries));
        }
    }

    /**
     * Open a project from its path.
     * 
     * @param projectPath The project path.
     * @return The created project.
     * @throws IOException If not able to create the project.
     */
    public static Project create(File projectPath) throws IOException
    {
        Verbose.info(VERBOSE_READ_PROJECT_PROPERTIES, projectPath.getAbsolutePath());

        try (final InputStream input = new FileInputStream(new File(projectPath, PROPERTIES_FILE)))
        {
            final Properties properties = new Properties();
            properties.load(input);

            final String resources = properties.getProperty(PROPERTY_PROJECT_RESOURCES);
            final String classes = properties.getProperty(PROPERTY_PROJECT_CLASSES);
            final String libraries = properties.getProperty(PROPERTY_PROJECT_LIBRARIES);

            return new Project(projectPath, resources, classes, libraries);
        }
    }

    /**
     * Check if project exists at the specified location.
     * 
     * @param path The path to check.
     * @return <code>true</code> if path contains project properties file, <code>false</code> else.
     */
    public static boolean exists(File path)
    {
        return new File(path, PROPERTIES_FILE).isFile();
    }

    /**
     * Project information.
     */
    public static final class Info
    {
        /** The resources folder. */
        private final String resources;
        /** The classes folder. */
        private final String classes;
        /** The libraries folder. */
        private final String libraries;

        /**
         * Create the information.
         * 
         * @param resources The resources folder.
         * @param classes The classes folder.
         * @param libraries The libraries folder.
         */
        public Info(String resources, String classes, String libraries)
        {
            super();
            this.resources = resources;
            this.classes = classes;
            this.libraries = libraries;
        }

        /**
         * Get the resources folder.
         * 
         * @return The resources folder.
         */
        public String getResources()
        {
            return resources;
        }

        /**
         * Get the classes folder.
         * 
         * @return The classes folder.
         */
        public String getClasses()
        {
            return classes;
        }

        /**
         * Get the libraries folder.
         * 
         * @return The libraries folder.
         */
        public String getLibraries()
        {
            return libraries;
        }
    }

    /**
     * Private constructor.
     */
    private ProjectFactory()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
