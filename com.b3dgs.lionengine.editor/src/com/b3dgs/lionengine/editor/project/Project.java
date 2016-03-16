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
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.osgi.framework.wiring.BundleWiring;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.UtilFile;
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.editor.utility.UtilBundle;

/**
 * Represents a project and its data.
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
    /** Media is not in class folder. */
    private static final String ERROR_MEDIA_RELATIVE_TO_CLASS = "Media is not in class folder: ";
    /** Media is not in resources folder. */
    private static final String ERROR_MEDIA_RELATIVE_TO_RESOURCES = "Media is not in resources folder: ";
    /** Cast error. */
    private static final String ERROR_CLASS_CAST = "Can not cast class to: ";
    /** Reading project properties verbose. */
    private static final String VERBOSE_READ_PROJECT_PROPERTIES = "Reading project properties for: ";
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
        return activeProject;
    }

    /**
     * Open a project from its path.
     * 
     * @param projectPath The project path.
     * @return The created project.
     * @throws IOException If not able to create the project.
     */
    public static synchronized Project create(File projectPath) throws IOException
    {
        Verbose.info(VERBOSE_READ_PROJECT_PROPERTIES, projectPath.getAbsolutePath());
        try (InputStream input = new FileInputStream(new File(projectPath, PROPERTIES_FILE)))
        {
            final Properties properties = new Properties();
            properties.load(input);

            final String classes = properties.getProperty(PROPERTY_PROJECT_CLASSES);
            final String libraries = properties.getProperty(PROPERTY_PROJECT_LIBRARIES);
            final String resources = properties.getProperty(PROPERTY_PROJECT_RESOURCES);

            final Project project = new Project(projectPath);
            project.setName(projectPath.getName());
            project.setClasses(classes, libraries);
            project.setResources(resources);

            activeProject = project;

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

    /**
     * Get the list of potential file descriptor which may contains classes (could be folder or jar).
     * 
     * @param file The file root.
     * @return The collection of places.
     */
    private static Collection<File> getPotentialClassesContainers(File file)
    {
        final Collection<File> places = new HashSet<>();
        places.add(file);
        if (file.isDirectory())
        {
            places.addAll(getJars(UtilFile.getFiles(file)));
        }
        return places;
    }

    /**
     * Get all jar in files.
     * 
     * @param files The files.
     * @return The jars.
     */
    private static Collection<File> getJars(Collection<File> files)
    {
        final Collection<File> jars = new HashSet<>();
        for (final File current : files)
        {
            if (isJar(current))
            {
                jars.add(current);
            }
        }
        return jars;
    }

    /**
     * Get all jar files as URL.
     * 
     * @param files The files.
     * @return The jars URL.
     * @throws MalformedURLException If error on URL.
     */
    private static Collection<URL> getJarsUrl(Collection<File> files) throws MalformedURLException
    {
        final Collection<URL> urls = new ArrayList<>();
        for (final File file : files)
        {
            if (isJar(file))
            {
                urls.add(file.toURI().toURL());
            }
        }
        return urls;
    }

    /**
     * Check if file is a jar.
     * 
     * @param file The file to check.
     * @return <code>true</code> if jar, <code>false</code> else.
     */
    private static boolean isJar(File file)
    {
        return UtilFile.isType(file, Constant.TYPE_JAR);
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
     * Private constructor.
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

        final Bundle bundle = Platform.getProduct().getDefiningBundle();
        if (bundle != null)
        {
            classLoader = createClassLoader(bundle);
        }
        else
        {
            Verbose.warning(getClass(), "setClasses", WARNING_BUNDLE);
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
    public Class<?> getClass(String name)
    {
        try
        {
            return classLoader.loadClass(name);
        }
        catch (final ClassNotFoundException | NoClassDefFoundError exception)
        {
            throw new LionEngineException(exception, ERROR_LOAD_CLASS, name);
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
    public <C> Class<? extends C> getClass(Media media, Class<C> clazz)
    {
        final String className = media.getPath()
                                      .replace(Property.EXTENSION_CLASS, Constant.EMPTY_STRING)
                                      .replace(File.separator, Constant.DOT);
        final Class<?> clazzRef = getClass(className);
        try
        {
            return clazzRef.asSubclass(clazz);
        }
        catch (final LionEngineException exception)
        {
            throw new LionEngineException(exception, media, ERROR_CLASS_CAST, clazz.getName());
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
    public <C> C getInstance(Media media, Class<C> clazz)
    {
        final Class<? extends C> clazzRef = getClass(media, clazz);
        try
        {
            final Object object = clazzRef.newInstance();
            return clazz.cast(object);
        }
        catch (final ReflectiveOperationException exception)
        {
            throw new LionEngineException(exception, ERROR_LOAD_CLASS, name);
        }
    }

    /**
     * Get all classes that implements the specified type.
     * 
     * @param <C> The class type.
     * @param type The type to check.
     * @return The implementing class list.
     */
    public <C> Collection<Class<? extends C>> getImplementing(Class<C> type)
    {
        final Collection<Class<? extends C>> found = new HashSet<>();
        final Collection<File> places = new HashSet<>();

        places.addAll(getPotentialClassesContainers(getClassesPath()));
        places.addAll(getPotentialClassesContainers(getLibrariesPath()));

        for (final File file : places)
        {
            if (isJar(file))
            {
                found.addAll(getImplementingJar(type, file));
            }
            else if (file.isDirectory())
            {
                found.addAll(getImplementing(type, file));
            }
        }

        found.addAll(getImplementing(type, UtilBundle.getLocation()));

        return found;
    }

    /**
     * Get all classes that implements the specified type.
     * 
     * @param <C> The class type.
     * @param type The type to check.
     * @param root The folder or jar to search.
     * @return The implementing class list.
     */
    public <C> Collection<Class<? extends C>> getImplementing(Class<C> type, File root)
    {
        final Collection<Class<? extends C>> found = new HashSet<>();
        if (root.isDirectory())
        {
            final Collection<File> folders = new HashSet<>();
            folders.add(root);
            while (!folders.isEmpty())
            {
                final Collection<File> foldersToDo = new HashSet<>();
                for (final File folder : folders)
                {
                    checkImplementing(folder, found, foldersToDo, type, root);
                }
                folders.clear();
                folders.addAll(foldersToDo);
                foldersToDo.clear();
            }
        }
        return found;
    }

    /**
     * Get all classes that implements the specified type.
     * 
     * @param <C> The class type.
     * @param folder The current folder.
     * @param found The list of class found.
     * @param foldersToDo The next folders to check.
     * @param type The type to check.
     * @param root The folder or jar to search.
     */
    private <C> void checkImplementing(File folder,
                                       Collection<Class<? extends C>> found,
                                       Collection<File> foldersToDo,
                                       Class<C> type,
                                       File root)
    {
        for (final File current : UtilFile.getFiles(folder))
        {
            if (current.isDirectory())
            {
                foldersToDo.add(current);
            }
            else if (current.isFile())
            {
                final int prefix = getClassesPath().getPath().length() + 1;
                if (prefix < current.getPath().length())
                {
                    checkAddClass(found, type, root, current.getPath().substring(prefix));
                }
            }
        }
    }

    /**
     * Check for classes inside jar.
     * 
     * @param <C> The class type.
     * @param type The type to check.
     * @param file The jar file.
     * @return The implementing class list.
     */
    private <C> Collection<Class<? extends C>> getImplementingJar(Class<C> type, File file)
    {
        final Collection<Class<? extends C>> found = new HashSet<>();
        try (final JarFile jar = new JarFile(file))
        {
            final Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements())
            {
                final JarEntry entry = entries.nextElement();
                if (!entry.isDirectory())
                {
                    checkAddClass(found, type, null, entry.getName());
                }
            }
        }
        catch (final IOException exception)
        {
            Verbose.exception(exception);
        }
        return found;
    }

    /**
     * Get class that implements the specified type.
     * 
     * @param <C> The class type.
     * @param type The type to check.
     * @param root The folder to search.
     * @param current The current class file to check.
     * @return The implementing class reference.
     */
    private <C> Class<? extends C> getImplementing(Class<C> type, File root, String current)
    {
        String name = current.replace(Property.EXTENSION_CLASS, Constant.EMPTY_STRING)
                             .replace(File.separator, Constant.DOT)
                             .replace(Constant.SLASH, Constant.DOT);
        if (root != null)
        {
            name = name.replace(root.getPath(), Constant.EMPTY_STRING);
        }
        if (name.charAt(0) == '.')
        {
            name = name.substring(1);
        }

        final Class<?> clazz = getClass(name);
        if (type.isAssignableFrom(clazz) && clazz != type)
        {
            return clazz.asSubclass(type);
        }
        return null;
    }

    /**
     * Check if can add class to collection, and add it if possible.
     * 
     * @param <C> The class type.
     * @param found The current classes found.
     * @param type The type to check.
     * @param root The folder or jar to search.
     * @param name The class name.
     */
    private <C> void checkAddClass(Collection<Class<? extends C>> found, Class<C> type, File root, String name)
    {
        if (name.endsWith(Property.EXTENSION_CLASS))
        {
            try
            {
                final Class<? extends C> clazz = getImplementing(type, root, name);
                if (clazz != null)
                {
                    found.add(clazz);
                }
            }
            catch (final LionEngineException exception)
            {
                return;
            }
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
        urls.add(librariesPath.toURI().toURL());
        if (librariesPath.isDirectory())
        {
            urls.addAll(getJarsUrl(UtilFile.getFiles(librariesPath)));
        }
        return new URLClassLoader(urls.toArray(new URL[urls.size()]), bundleClassLoader);
    }
}
