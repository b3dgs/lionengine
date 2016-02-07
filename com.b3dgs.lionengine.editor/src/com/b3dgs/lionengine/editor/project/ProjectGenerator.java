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
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Generate project elements.
 */
public class ProjectGenerator
{
    /** Classes folder. */
    private final String classes;
    /** Libraries folder. */
    private final String libraries;
    /** Resources folder. */
    private final String resources;

    /**
     * Create a project generator.
     * 
     * @param classes The classes code folder.
     * @param libraries The libraries folder.
     * @param resources The resources folder.
     */
    public ProjectGenerator(String classes, String libraries, String resources)
    {
        this.classes = classes;
        this.libraries = libraries;
        this.resources = resources;
    }

    /**
     * Create the project properties file.
     * 
     * @param projectPath The project path.
     * @throws IOException If not able to create the project properties file.
     */
    public void createProperties(File projectPath) throws IOException
    {
        final File propertiesFile = new File(projectPath, Project.PROPERTIES_FILE);
        final Properties properties = new Properties();
        properties.put(Project.PROPERTY_PROJECT_CLASSES, classes);
        properties.put(Project.PROPERTY_PROJECT_LIBRARIES, libraries);
        properties.put(Project.PROPERTY_PROJECT_RESOURCES, resources);
        try (FileOutputStream stream = new FileOutputStream(propertiesFile))
        {
            properties.store(stream, Project.PROPERTIES_FILE_DESCRIPTION);
        }
    }
}
