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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
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
    /** Property project libraries folder. */
    public static final String PROPERTY_PROJECT_LIBRARIES = "LibrariesFolder";
    /** Property project resources folder. */
    public static final String PROPERTY_PROJECT_RESOURCES = "ResourcesFolder";
    /** Load class error. */
    public static final String ERROR_LOAD_CLASS = "Unable to load the class: ";
    /** Create class path directory error. */
    private static final String ERROR_CREATE_CLASSPATH_DIR = "Unable to create class path directory: ";
    /** Media is not in class folder. */
    private static final String ERROR_MEDIA_RELATIVE_TO_CLASS = "Media is not in class folder: ";
    /** Media is not in resources folder. */
    private static final String ERROR_MEDIA_RELATIVE_TO_RESOURCES = "Media is not in resources folder: ";
    /** Cast error. */
    private static final String ERROR_CLASS_CAST = "Can not cast class to: ";
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
     * @return The current active project, <code>null</code> if none.
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
     * @throws IOException If not able to create the project.
     */
    public static Project create(File projectPath) throws IOException
    {
        Verbose.info(Project.VERBOSE_READ_PROJECT_PROPERTIES, projectPath.getAbsolutePath());
        try (InputStream inputStream = new FileInputStream(new File(projectPath, Project.PROPERTIES_FILE)))
        {
            final Properties properties = new Properties();
            properties.load(inputStream);

            final String classes = properties.getProperty(Project.PROPERTY_PROJECT_CLASSES);
            final String libraries = properties.getProperty(Project.PROPERTY_PROJECT_LIBRARIES);
            final String resources = properties.getProperty(Project.PROPERTY_PROJECT_RESOURCES);

            final Project project = new Project(projectPath);
            project.setName(projectPath.getName());
            project.setClasses(classes, libraries);
            project.setResources(resources);
            project.checkClassPath();

            Project.activeProject = project;

            return project;
        }
    }

    /**
     * Get a media relative to the from path. Must be in from folder.
     * 
     * @param file The resource file.
     * @param from The folder source.
     * @param error The error message.
     * @return The relative media.
     * @throws LionEngineException If not relative to expected folder.
     */
    private static Media getRelativeMedia(File file, File from, String error) throws LionEngineException
    {
        final String fromPath = from.getPath();
        final String path = file.getAbsolutePath();
        if (!path.startsWith(fromPath))
        {
            throw new LionEngineException(error + path);
        }
        final int fromPrefix = fromPath.length() + 1;
        final String relativePath = path.substring(fromPrefix);
        return Core.MEDIA.create(relativePath);
    }

    /** Project path. */
    private final File path;
    /** Project name. */
    private String name;
    /** Classes folder (represents the main classes folder, such as <code>bin/</code>). */
    private String classes;
    /** Library folder (represents the libraries folder). */
    private String libraries;
    /** Resources folder (represents the main resources folder, such as <code>resources/</code>. */
    private String resources;
    /** Class loader. */
    private ClassLoader classLoader;

    /**
     * Constructor.
     * 
     * @param path The project path.
     */
    private Project(File path)
    {
        this.path = path;
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
     * @param classesFolderName The classes folder name.
     * @param librariesFolderName The libraries folder name.
     * @throws MalformedURLException If error on the path.
     */
    public void setClasses(String classesFolderName, String librariesFolderName) throws MalformedURLException
    {
        classes = classesFolderName;
        libraries = librariesFolderName;

        final Bundle bundle = Activator.getMainBundle();
        if (bundle != null)
        {
            classLoader = createClassLoader(bundle);
        }
        else
        {
            Verbose.warning(getClass(), "setClasses", Project.WARNING_BUNDLE);
        }
    }

    /**
     * Set the resources folder name.
     * 
     * @param folderName The resource folder name.
     */
    public void setResources(String folderName)
    {
        resources = folderName;
    }

    /**
     * Get a class media relative to the classes path. Must be in {@link Project#getClassesPath()} folder.
     * 
     * @param file The class file.
     * @return The relative class media.
     * @throws LionEngineException If not relative to the expected folder.
     */
    public Media getClassMedia(File file) throws LionEngineException
    {
        return Project.getRelativeMedia(file, getClassesPath(), Project.ERROR_MEDIA_RELATIVE_TO_CLASS);
    }

    /**
     * Get a media relative to the resources path. Must be in {@link Project#getResourcesPath()} folder.
     * 
     * @param file The resource file.
     * @return The relative media.
     * @throws LionEngineException If not relative to expected folder.
     */
    public Media getResourceMedia(File file) throws LionEngineException
    {
        return Project.getRelativeMedia(file, getResourcesPath(), Project.ERROR_MEDIA_RELATIVE_TO_RESOURCES);
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
     * Get the class reference from its media.
     * 
     * @param media The class media (must be in classes folder).
     * @param clazz The class to cast to.
     * @param <C> The class type.
     * @return The class reference.
     * @throws LionEngineException If not able to load the class.
     */
    public <C> Class<? extends C> getClass(Media media, Class<C> clazz) throws LionEngineException
    {
        final String name = media.getPath().replace("." + Property.EXTENSION_CLASS, "").replace(File.separator, ".");
        final Class<?> clazzRef = getClass(name);
        try
        {
            return clazzRef.asSubclass(clazz);
        }
        catch (final LionEngineException exception)
        {
            throw new LionEngineException(exception, media, Project.ERROR_CLASS_CAST, clazz.getName());
        }
    }

    /**
     * Get the class instance from its file.
     * 
     * @param media The class media.
     * @param clazz The class to cast to.
     * @param <C> The class type.
     * @return The class instance.
     * @throws LionEngineException If not able to create the class.
     */
    public <C> C getInstance(Media media, Class<C> clazz) throws LionEngineException
    {
        final Class<? extends C> clazzRef = getClass(media, clazz);
        try
        {
            final Object object = clazzRef.newInstance();
            return clazz.cast(object);
        }
        catch (final ReflectiveOperationException exception)
        {
            throw new LionEngineException(exception, Project.ERROR_LOAD_CLASS, name);
        }
    }

    /**
     * Create a class loader from a class folder.
     * 
     * @param bundle The bundle reference.
     * @return The class loader instance.
     * @throws MalformedURLException If error when creating the class loader path.
     */
    private URLClassLoader createClassLoader(Bundle bundle) throws MalformedURLException
    {
        final BundleWiring bundleWiring = bundle.adapt(BundleWiring.class);
        final ClassLoader bundleClassLoader = bundleWiring.getClassLoader();

        final Collection<URL> urls = new ArrayList<>();
        urls.add(getClassesPath().toURI().toURL());

        final File librariesPath = getLibrariesPath();
        if (librariesPath.isDirectory())
        {
            for (final File file : librariesPath.listFiles())
            {
                if (file.isFile() && file.getName().toLowerCase(Locale.ENGLISH).endsWith(".jar"))
                {
                    urls.add(file.toURI().toURL());
                }
            }
        }
        return new URLClassLoader(urls.toArray(new URL[urls.size()]), bundleClassLoader);
    }

    /**
     * Check the class path project, and extract from JAR if necessary.
     * 
     * @throws IOException If error when creating folder, or extracting classes.
     */
    private void checkClassPath() throws IOException
    {
        final File classPath = getClassesPath();
        if (classPath.isFile())
        {
            final String name = classPath.getName();
            final String dirName = name.substring(0, name.lastIndexOf('.'));
            final File dir = new File(classPath.getParentFile(), dirName);
            if (!dir.exists() && !dir.mkdir())
            {
                throw new IOException(Project.ERROR_CREATE_CLASSPATH_DIR + dir.toString());
            }
            final String classes = classPath.getAbsolutePath();
            final File librariesPath = getLibrariesPath();
            Verbose.info(Project.VERBOSE_EXTRACT_JAR, classes);
            Tools.unzip(classes, dir.getAbsolutePath());

            final int length = getPath().getAbsolutePath().length();
            final String classesFolderName = dir.getAbsolutePath().substring(length);
            final String librariesFolderName = librariesPath.getAbsolutePath().substring(length);
            setClasses(classesFolderName, librariesFolderName);
        }
    }
}
