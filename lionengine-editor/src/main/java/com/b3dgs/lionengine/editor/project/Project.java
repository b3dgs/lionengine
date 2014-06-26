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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Properties;

import org.osgi.framework.Bundle;
import org.osgi.framework.wiring.BundleWiring;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.core.Core;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.editor.Activator;

/**
 * Represents a project and its data.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class Project
{
    /** Properties file. */
    public static final String PROPERTIES_FILE = ".lionengine";
    /** Properties file description. */
    public static final String PROPERTIES_FILE_DESCRIPTION = "LionEngine project properties";
    /** Property project classes folder. */
    public static final String PROPERTY_PROJECT_CLASSES = "ClassesFolder";
    /** Property project resources folder. */
    public static final String PROPERTY_PROJECT_RESOURCES = "ResourcesFolder";
    /** Create project error. */
    private static final String ERROR_CREATE_PROJECT = "Unable to create the project: ";
    /** Load class error. */
    private static final String ERROR_LOAD_CLASS = "Unable to load the class: ";
    /** Active project. */
    private static Project activeProject;

    /**
     * Get the current active project.
     * 
     * @return The current active project.
     */
    public static Project getActive()
    {
        return Project.activeProject;
    }

    /**
     * Open a project from its path.
     * 
     * @param projectPath The project path.
     * @return The created project.
     * @throws LionEngineException If not able to create the project.
     */
    public static Project create(File projectPath) throws LionEngineException
    {
        try (InputStream inputStream = new FileInputStream(new File(projectPath, Project.PROPERTIES_FILE));)
        {
            final Properties properties = new Properties();
            properties.load(inputStream);

            final String classes = properties.getProperty(Project.PROPERTY_PROJECT_CLASSES);
            final String resources = properties.getProperty(Project.PROPERTY_PROJECT_RESOURCES);

            final Project project = new Project(projectPath);
            project.setName(projectPath.getName());
            project.setClasses(classes);
            project.setResources(resources);

            Project.activeProject = project;

            return project;
        }
        catch (final IOException exception)
        {
            throw new LionEngineException(exception, Project.ERROR_CREATE_PROJECT, projectPath.getPath());
        }
    }

    /** Project path. */
    private final File path;
    /** Project name. */
    private String name;
    /** Classes folder (represents the main classes folder, such as <code>bin/</code>). */
    private String classes;
    /** Resources folder (represents the main resources folder, such as <code>resources/</code>. */
    private String resources;
    /** Class loader. */
    private ClassLoader classLoader;
    /** Opened state. */
    private boolean opened;

    /**
     * Constructor.
     * 
     * @param path The project path.
     */
    private Project(File path)
    {
        this.path = path;
        opened = true;
    }

    /**
     * Open the project.
     */
    public void open()
    {
        opened = true;
    }

    /**
     * Close the project.
     */
    public void close()
    {
        opened = false;
    }

    /**
     * Get a media relative to the classes path.
     * 
     * @param path The absolute path.
     * @return The relative media.
     */
    public Media getClassMedia(String path)
    {
        final int projectPrefix = getClassesPath().getPath().length() + 1;
        final String relativePath = path.substring(projectPrefix);
        return Core.MEDIA.create(relativePath);
    }

    /**
     * Get a media relative to the resources path.
     * 
     * @param path The absolute path.
     * @return The relative media.
     */
    public Media getResourceMedia(String path)
    {
        final int projectPrefix = getResourcesPath().getPath().length() + 1;
        final String relativePath = path.substring(projectPrefix);
        return Core.MEDIA.create(relativePath);
    }

    /**
     * Set the project name.
     * 
     * @param name The project name.
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Set the classes folder.
     * 
     * @param folder The classes folder.
     * @throws MalformedURLException If error on the path.
     */
    public void setClasses(String folder) throws MalformedURLException
    {
        classes = folder;
        final URL url = new File(path, folder).toURI().toURL();
        final URL[] urls = new URL[]
        {
            url
        };
        final Bundle bundle = Activator.getContext().getBundle();
        final BundleWiring bundleWiring = bundle.adapt(BundleWiring.class);
        final ClassLoader bundleClassLoader = bundleWiring.getClassLoader();
        classLoader = new URLClassLoader(urls, bundleClassLoader);
    }

    /**
     * Set the resources folder.
     * 
     * @param folder The resource folder.
     */
    public void setResources(String folder)
    {
        resources = folder;
    }

    /**
     * Get the project path.
     * 
     * @return The project path.
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
     * Get the classes folder.
     * 
     * @return The classes folder.
     */
    public String getClasses()
    {
        return classes;
    }

    /**
     * Get the classes path.
     * 
     * @return The classes path.
     */
    public File getClassesPath()
    {
        return new File(path, classes);
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
     * Get the resources path.
     * 
     * @return The resources path.
     */
    public File getResourcesPath()
    {
        return new File(path, resources);
    }

    /**
     * Get a class from its name depending of the project class loader.
     * 
     * @param name The full class name.
     * @return The loaded class.
     * @throws LionEngineException If error when loading the class.
     */
    public Class<?> getClass(String name) throws LionEngineException
    {
        try
        {
            return classLoader.loadClass(name);
        }
        catch (final ClassNotFoundException exception)
        {
            throw new LionEngineException(exception, Project.ERROR_LOAD_CLASS, name);
        }
    }

    /**
     * Get the class reference from its file.
     * 
     * @param clazz The class to cast to.
     * @param file The class file.
     * @return The class instance.
     * @throws LionEngineException If not able to create the class.
     */
    public <C> Class<? extends C> getClass(Class<C> clazz, Media file) throws LionEngineException
    {
        final String name = file.getPath().replace("." + Property.EXTENSION_CLASS, "").replace(File.separator, ".");
        final Class<?> clazzRef = getClass(name);
        return clazzRef.asSubclass(clazz);
    }

    /**
     * Get the class instance from its file.
     * 
     * @param clazz The class to cast to.
     * @param file The class file.
     * @return The class instance.
     * @throws LionEngineException If not able to create the class.
     */
    public <C> C getInstance(Class<C> clazz, Media file) throws LionEngineException
    {
        final Class<? extends C> clazzRef = getClass(clazz, file);
        try
        {
            final Object object = clazzRef.newInstance();
            return clazz.cast(object);
        }
        catch (InstantiationException
               | IllegalAccessException exception)
        {
            throw new LionEngineException(exception, Project.ERROR_LOAD_CLASS, name);
        }
    }

    /**
     * Check if the project is opened.
     * 
     * @return <code>true</code> if opened, <code>false</code> else.
     */
    public boolean isOpened()
    {
        return opened;
    }
}
