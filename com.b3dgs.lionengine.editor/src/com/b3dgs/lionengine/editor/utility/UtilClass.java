/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.editor.utility;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.UtilFile;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.Verbose;
import com.b3dgs.lionengine.editor.Activator;
import com.b3dgs.lionengine.editor.project.Project;
import com.b3dgs.lionengine.editor.project.Property;
import com.b3dgs.lionengine.game.configurer.ConfigObject;
import com.b3dgs.lionengine.stream.Stream;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Series of tool functions around the editor related to classes.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class UtilClass
{
    /** Create class error. */
    private static final String ERROR_CLASS_CREATE = "Unable to create the following class: ";

    /**
     * Create a class from its name and call its corresponding constructor.
     * 
     * @param <C> The class type.
     * @param name The full class name.
     * @param type The class type.
     * @param params The constructor parameters.
     * @return The class instance.
     * @throws ReflectiveOperationException If error when creating the class.
     */
    public static <C> C createClass(String name, Class<C> type, Object... params) throws ReflectiveOperationException
    {
        final Class<?> clazz = Activator.getMainBundle().loadClass(name);
        for (final Constructor<?> constructor : clazz.getConstructors())
        {
            final Class<?>[] constructorParams = constructor.getParameterTypes();
            final int required = params.length;
            int found = 0;
            for (final Class<?> constructorParam : constructorParams)
            {
                if (found >= params.length || !constructorParam.isAssignableFrom(params[found].getClass()))
                {
                    break;
                }
                found++;
            }
            if (found == required)
            {
                return type.cast(constructor.newInstance(params));
            }
        }
        throw new ClassNotFoundException(UtilClass.ERROR_CLASS_CREATE + name);
    }

    /**
     * Get the class from media file, by reading the attribute {@link ConfigObject#CLASS} attribute.
     * 
     * @param media The media descriptor.
     * @return The class reference.
     * @throws LionEngineException If not able to create the class.
     */
    public static Class<?> get(Media media) throws LionEngineException
    {
        final XmlNode root = Stream.loadXml(media);
        final String className = root.getChild(ConfigObject.CLASS).getText();
        return Project.getActive().getClass(className);
    }

    /**
     * Get all classes that implements the specified type.
     * 
     * @param <C> The class type.
     * @param type The type to check.
     * @return The implementing class list.
     */
    public static <C> Collection<Class<? extends C>> getImplementing(Class<C> type)
    {
        final Collection<Class<? extends C>> found = new HashSet<>();
        final Collection<File> places = new HashSet<>();

        places.addAll(getPotentialClassesContainers(Project.getActive().getClassesPath()));
        places.addAll(getPotentialClassesContainers(Project.getActive().getLibrariesPath()));

        for (final File file : places)
        {
            if (isJar(file))
            {
                found.addAll(getImplementingJar(type, file));
            }
            else if (file.isDirectory())
            {
                getImplementing(type, file, null);
            }
        }

        found.addAll(getImplementing(type, Activator.getLocation(), Activator.class.getPackage().getName()));

        return found;
    }

    /**
     * Get all classes that implements the specified type.
     * 
     * @param <C> The class type.
     * @param type The type to check.
     * @param root The folder or jar to search.
     * @param packageStart The starting package (<code>null</code> if none).
     * @return The implementing class list.
     */
    public static <C> Collection<Class<? extends C>> getImplementing(Class<C> type, File root, String packageStart)
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
                    checkImplementing(folder, found, foldersToDo, type, root, packageStart);
                }
                folders.clear();
                folders.addAll(foldersToDo);
                foldersToDo.clear();
            }
        }
        return found;
    }

    /**
     * Check if file is a jar.
     * 
     * @param file The file to check.
     * @return <code>true</code> if jar, <code>false</code> else.
     */
    public static boolean isJar(File file)
    {
        return UtilFile.isType(file, Constant.TYPE_JAR);
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
     * @param packageStart The starting package (<code>null</code> if none).
     */
    private static <C> void checkImplementing(File folder,
                                              Collection<Class<? extends C>> found,
                                              Collection<File> foldersToDo,
                                              Class<C> type,
                                              File root,
                                              String packageStart)
    {
        for (final File current : UtilFile.getFiles(folder))
        {
            if (current.isDirectory())
            {
                foldersToDo.add(current);
            }
            else if (current.isFile())
            {
                checkAddClass(found, type, root, packageStart, current.getPath());
            }
        }
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
     * Check for classes inside jar.
     * 
     * @param <C> The class type.
     * @param type The type to check.
     * @param file The jar file.
     * @return The implementing class list.
     */
    private static <C> Collection<Class<? extends C>> getImplementingJar(Class<C> type, File file)
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
                    checkAddClass(found, type, null, null, entry.getName());
                }
            }
        }
        catch (final IOException exception)
        {
            Verbose.exception(UtilClass.class, "getImplementing", exception);
        }
        return found;
    }

    /**
     * Get class that implements the specified type.
     * 
     * @param <C> The class type.
     * @param type The type to check.
     * @param root The folder to search.
     * @param packageStart The starting package (<code>null</code> if none).
     * @param current The current class file to check.
     * @return The implementing class reference.
     */
    private static <C> Class<? extends C> getImplementing(Class<C> type, File root, String packageStart, String current)
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
        if (packageStart != null)
        {
            name = name.substring(name.indexOf(packageStart));
        }
        final Project project = Project.getActive();
        final Class<?> clazz = project.getClass(name);
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
     * @param packageStart The starting package (<code>null</code> if none).
     * @param name The class name.
     */
    private static <C> void checkAddClass(Collection<Class<? extends C>> found,
                                          Class<C> type,
                                          File root,
                                          String packageStart,
                                          String name)
    {
        if (name.endsWith(Property.EXTENSION_CLASS))
        {
            try
            {
                final Class<? extends C> clazz = getImplementing(type, root, packageStart, name);
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
     * Private constructor.
     */
    private UtilClass()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
