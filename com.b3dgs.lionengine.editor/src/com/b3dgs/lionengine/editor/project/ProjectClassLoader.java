/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.osgi.framework.wiring.BundleWiring;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.UtilFile;
import com.b3dgs.lionengine.editor.utility.UtilBundle;

/**
 * Represents the project class loader.
 */
public class ProjectClassLoader
{
    /** Load class error. */
    private static final String ERROR_LOAD_CLASS = "Unable to load the class: ";
    /** Create class error. */
    private static final String ERROR_CREATE_CLASS = "Unable to create the class: ";
    /** Cast error. */
    private static final String ERROR_CLASS_CAST = "Can not cast class to: ";
    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectClassLoader.class);

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

    /** The classes path. */
    private final File classesPath;
    /** The libraries path. */
    private final File librariesPath;
    /** Class loader. */
    private final ClassLoader classLoader;

    /**
     * Create the class loader.
     * 
     * @param classesPath The classes path.
     * @param librariesPath The libraries path.
     */
    public ProjectClassLoader(File classesPath, File librariesPath)
    {
        this.classesPath = classesPath;
        this.librariesPath = librariesPath;
        final Bundle bundle = Platform.getProduct().getDefiningBundle();
        classLoader = createClassLoader(bundle);
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
            throw new LionEngineException(exception, ERROR_LOAD_CLASS + name);
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
            throw new LionEngineException(exception, media, ERROR_CLASS_CAST + clazz.getName());
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
            final Object object = clazzRef.getConstructor().newInstance();
            return clazz.cast(object);
        }
        catch (final ReflectiveOperationException exception)
        {
            throw new LionEngineException(exception, ERROR_CREATE_CLASS + clazzRef.getName());
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
        final Collection<File> places = new HashSet<>(getPotentialClassesContainers(classesPath));

        places.addAll(getPotentialClassesContainers(librariesPath));

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
                final int prefix = classesPath.getPath().length() + 1;
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
        try (JarFile jar = new JarFile(file))
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
            LOGGER.error("getImplementingJar error", exception);
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
        if (!name.isEmpty())
        {
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
            catch (@SuppressWarnings("unused") final LionEngineException exception)
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
     * @throws LionEngineException If error when creating the class loader path.
     */
    private URLClassLoader createClassLoader(Bundle bundle)
    {
        final Collection<URL> urls = new ArrayList<>();
        try
        {
            urls.add(classesPath.toURI().toURL());
            urls.add(librariesPath.toURI().toURL());
            if (librariesPath.isDirectory())
            {
                urls.addAll(getJarsUrl(UtilFile.getFiles(librariesPath)));
            }
        }
        catch (final MalformedURLException exception)
        {
            throw new LionEngineException(exception);
        }
        final BundleWiring bundleWiring = bundle.adapt(BundleWiring.class);
        return new URLClassLoader(urls.toArray(new URL[urls.size()]), bundleWiring.getClassLoader());
    }
}
