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
package com.b3dgs.lionengine.editor;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MDirectToolItem;
import org.eclipse.e4.ui.model.application.ui.menu.MToolBar;
import org.eclipse.e4.ui.model.application.ui.menu.MToolBarElement;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ToolItem;
import org.osgi.framework.Bundle;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.UtilFile;
import com.b3dgs.lionengine.editor.project.Project;
import com.b3dgs.lionengine.editor.project.Property;

/**
 * Series of tool functions around the editor related to eclipse.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class UtilEclipse
{
    /** Icon folder. */
    private static final String ICON_FOLDER = "icons";
    /** Part error. */
    private static final String ERROR_PART = "Unable to find part: ";
    /** Create class error. */
    private static final String ERROR_CLASS_CREATE = "Unable to create the following class: ";
    /** Icon not found error. */
    private static final String ERROR_ICON_PATH = "Icon not found: ";
    /** Icon not created error. */
    private static final String ERROR_ICON_CREATE = "Icon cannot be created: ";

    /**
     * Get the icon from its name.
     * 
     * @param icon The icon name.
     * @return The icon instance.
     * @throws LionEngineException If error when getting icon.
     */
    public static Image getIcon(String icon) throws LionEngineException
    {
        return UtilEclipse.getIcon("", icon);
    }

    /**
     * Get the icon from its name.
     * 
     * @param root The icon root.
     * @param icon The icon name.
     * @return The icon instance.
     * @throws LionEngineException If error when getting icon.
     */
    public static Image getIcon(String root, String icon) throws LionEngineException
    {
        final Bundle bundle = Activator.getContext().getBundle();
        final String path = UtilFile.getPathSeparator("/", UtilEclipse.ICON_FOLDER, root, icon);
        final URL url = bundle.getEntry(path);
        if (url == null)
        {
            throw new LionEngineException(UtilEclipse.ERROR_ICON_PATH + path);
        }
        try
        {
            final ImageDescriptor descriptor = ImageDescriptor.createFromURL(FileLocator.toFileURL(url));
            final Image image = descriptor.createImage();
            if (image == null)
            {
                throw new LionEngineException(UtilEclipse.ERROR_ICON_CREATE + path);
            }
            return image;
        }
        catch (final IOException exception)
        {
            throw new LionEngineException(exception);
        }
    }

    /**
     * Get the file from its name, relative to the plugin path.
     * 
     * @param file The file name.
     * @return The file instance.
     */
    public static File getFile(String file)
    {
        try
        {
            final File root = FileLocator.getBundleFile(Activator.getContext().getBundle());
            return new File(root, file);
        }
        catch (final IOException exception)
        {
            throw new LionEngineException(exception);
        }
    }

    /**
     * Get a part from its id.
     * 
     * @param <C> The class type.
     * @param partService The part service.
     * @param id The part id.
     * @param clazz The part class type.
     * @return The part class instance.
     * @throws LionEngineException If part can not be found.
     */
    public static <C> C getPart(EPartService partService, String id, Class<C> clazz) throws LionEngineException
    {
        final MPart part = partService.findPart(id);
        if (part != null)
        {
            partService.bringToTop(part);
            final Object object = part.getObject();
            if (object != null
                    && (object.getClass().isAssignableFrom(clazz) || clazz.isInterface()
                            && clazz.isAssignableFrom(object.getClass())))
            {
                return clazz.cast(part.getObject());
            }
        }
        throw new LionEngineException(UtilEclipse.ERROR_PART, id);
    }

    /**
     * Create a class from its name and call its corresponding constructor.
     * 
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
        throw new ClassNotFoundException(UtilEclipse.ERROR_CLASS_CREATE + name);
    }

    /**
     * Get all classes that implements the specified type.
     * 
     * @param type The type to check.
     * @return The implementing class list.
     */
    public static <C> Collection<Class<? extends C>> getImplementing(Class<C> type)
    {
        final Collection<Class<? extends C>> found = new HashSet<>();
        found.addAll(getImplementing(type, Project.getActive().getClassesPath()));
        found.addAll(getImplementing(type, Project.getActive().getLibrariesPath()));
        return found;
    }

    /**
     * Get all classes that implements the specified type.
     * 
     * @param type The type to check.
     * @param root The folder to search.
     * @return The implementing class list.
     */
    public static <C> Collection<Class<? extends C>> getImplementing(Class<C> type, File root)
    {
        final Collection<Class<? extends C>> found = new HashSet<>();
        final Collection<File> folders = new ArrayList<>();
        final Project project = Project.getActive();

        if (root.isDirectory())
        {
            folders.add(root);
            while (!folders.isEmpty())
            {
                final Collection<File> foldersToDo = new ArrayList<>();
                for (final File folder : folders)
                {
                    for (final File current : folder.listFiles())
                    {
                        if (current.isDirectory())
                        {
                            foldersToDo.add(current);
                        }
                        else if (current.isFile() && current.getName().endsWith(".class"))
                        {
                            final String name = current.getPath().replace(root.getPath(), "")
                                    .replace("." + Property.EXTENSION_CLASS, "").replace(File.separator, ".")
                                    .substring(1);

                            final Class<?> clazz = project.getClass(name);
                            if (type.isAssignableFrom(clazz) && clazz != type)
                            {
                                found.add(clazz.asSubclass(type));
                            }
                        }
                    }
                }
                folders.clear();
                folders.addAll(foldersToDo);
                foldersToDo.clear();
            }
        }
        return found;
    }

    /**
     * Show an error dialog.
     * 
     * @param title The error title.
     * @param message The error message.
     */
    public static void showError(String title, String message)
    {
        MessageDialog.openError(Display.getDefault().getActiveShell(), title, message);
    }

    /**
     * Set the tool item selection.
     * 
     * @param toolbar The tool bar reference.
     * @param selected The selection state.
     * @param names The elements names (relative to the tool bar ID).
     */
    public static void setToolItemSelection(MToolBar toolbar, boolean selected, String... names)
    {
        final Collection<String> items = Arrays.asList(names);
        for (final MToolBarElement element : toolbar.getChildren())
        {
            final String id = element.getElementId().substring(toolbar.getElementId().length() + 1);
            if (items.isEmpty() || items.contains(id))
            {
                if (element instanceof MDirectToolItem)
                {
                    ((MDirectToolItem) element).setSelected(selected);
                }
            }
        }
    }

    /**
     * Set the tool item enabled.
     * 
     * @param toolbar The tool bar reference.
     * @param enabled The enabled state.
     * @param names The elements names (relative to the tool bar ID).
     */
    public static void setToolItemEnabled(MToolBar toolbar, boolean enabled, String... names)
    {
        final Collection<String> items = Arrays.asList(names);
        for (final MToolBarElement element : toolbar.getChildren())
        {
            if (items.isEmpty() || UtilEclipse.toolbarElementContained(element, items))
            {
                if (element.getWidget() instanceof ToolItem)
                {
                    ((ToolItem) element.getWidget()).setEnabled(enabled);
                }
            }
        }
    }

    /**
     * Check if tool bar element is contained in the list.
     * 
     * @param element The tool bar element.
     * @param items The items list.
     * @return <code>true</code> if contained, <code>false</code> else.
     */
    private static boolean toolbarElementContained(MToolBarElement element, Collection<String> items)
    {
        final String id = element.getElementId();
        for (final String item : items)
        {
            if (id.contains(item))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Private constructor.
     */
    private UtilEclipse()
    {
        throw new RuntimeException();
    }
}
