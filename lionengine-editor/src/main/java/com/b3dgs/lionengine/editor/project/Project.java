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
import com.b3dgs.lionengine.core.Verbose;
import com.b3dgs.lionengine.editor.Activator;
import com.b3dgs.lionengine.editor.Tools;

/**
 * Represents a project and its data.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class Project
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
    /** Create class path directory error. */
    private static final String ERROR_CREATE_CLASSPATH_DIR = "Unable to create class path directory: ";
    /** Reading project properties verbose. */
    private static final String VERBOSE_READ_PROJECT_PROPERTIES = "Reading project properties for: ";
    /** Extract jar classes verbose. */
    private static final String VERBOSE_EXTRACT_JAR = "Extract project classes from JAR at: ";
    /** Bundle warning. */
    private static final String WARNING_BUNDLE = "No bundle found, external classLoader will not be defined !";
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
        Verbose.info(Project.VERBOSE_READ_PROJECT_PROPERTIES, projectPath.getAbsolutePath());
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

            Project.checkClassPath(project);
            Project.activeProject = project;

            return project;
        }
        catch (final IOException exception)
        {
            throw new LionEngineException(exception, Project.ERROR_CREATE_PROJECT, projectPath.getPath());
        }
    }

    /**
     * Check the class path project, and extract from JAR if necessary.
     * 
     * @param project The project to check.
     * @throws LionEngineException If error.
     */
    private static void checkClassPath(Project project) throws LionEngineException
    {
        final File classPath = project.getClassesPath();
        if (classPath.isFile())
        {
            final String name = classPath.getName();
            final String dirName = name.substring(0, name.lastIndexOf('.'));
            final File dir = new File(classPath.getParentFile(), dirName);
            if (!dir.exists())
            {
                if (!dir.mkdir())
                {
                    throw new LionEngineException(Project.ERROR_CREATE_CLASSPATH_DIR + dir.toString());
                }
            }
            try
            {
                final String classes = classPath.getAbsolutePath();
                Verbose.info(Project.VERBOSE_EXTRACT_JAR, classes);
                Tools.unzip(classes, dir.getAbsolutePath());
                project.setClasses(dir.getAbsolutePath().substring(project.getPath().getAbsolutePath().length()));
            }
            catch (final IOException exception)
            {
                throw new LionEngineException(exception);
            }
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

        final Bundle bundle = Activator.getMainBundle();
        if (bundle != null)
        {
            classLoader = createClassLoader(folder, bundle);
        }
        else
        {
            Verbose.warning(getClass(), "setClasses", Project.WARNING_BUNDLE);
        }
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
     * Get the class loader of this project.
     * 
     * @return The class loader of this project.
     */
    public ClassLoader getClassLoader()
    {
        return classLoader;
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
     * @param <C> The class type.
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
     * @param <C> The class type.
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

    /**
     * Create a class loader from a class folder.
     * 
     * @param folder The class folder.
     * @param bundle The bundle reference.
     * @return The class loader instance.
     * @throws MalformedURLException If error when creating the class loader path.
     */
    private URLClassLoader createClassLoader(String folder, Bundle bundle) throws MalformedURLException
    {
        final BundleWiring bundleWiring = bundle.adapt(BundleWiring.class);
        final ClassLoader bundleClassLoader = bundleWiring.getClassLoader();
        final URL url = new File(path, folder).toURI().toURL();
        final URL[] urls = new URL[]
        {
            url
        };
        return new URLClassLoader(urls, bundleClassLoader);
    }
}
