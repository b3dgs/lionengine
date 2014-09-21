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
package com.b3dgs.lionengine.editor;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledToolItem;
import org.eclipse.e4.ui.model.application.ui.menu.MToolBar;
import org.eclipse.e4.ui.model.application.ui.menu.MToolBarElement;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.ToolItem;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.UtilFile;

/**
 * Series of tool functions around the editor related to eclipse.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class UtilEclipse
{
    /** Part error. */
    private static final String ERROR_PART = "Unable to find part: ";
    /** Create class error. */
    private static final String ERROR_CLASS_CREATE = "Unable to create the following class: ";

    /**
     * Get the icon from its name.
     * 
     * @param icon The icon name.
     * @return The icon instance.
     */
    public static Image getIcon(String icon)
    {
        try
        {
            final ImageDescriptor image = ImageDescriptor.createFromURL(FileLocator.toFileURL(Activator.getContext()
                    .getBundle().getEntry(UtilFile.getPath("icons", icon))));
            return image.createImage();
        }
        catch (final IOException exception)
        {
            throw new LionEngineException(exception);
        }
    }

    /**
     * Get the icon from its name.
     * 
     * @param root The icon root.
     * @param icon The icon name.
     * @return The icon instance.
     */
    public static Image getIcon(String root, String icon)
    {
        try
        {
            final ImageDescriptor image = ImageDescriptor.createFromURL(FileLocator.toFileURL(Activator.getContext()
                    .getBundle().getEntry(UtilFile.getPath("icons", root, icon))));
            return image.createImage();
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
            if (object != null && object.getClass().isAssignableFrom(clazz))
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
     * Set the tool item selection.
     * 
     * @param toolbar The tool bar reference.
     * @param selected The selection state.
     * @param names The elements names (relative to the tool bar ID).
     */
    public static void setToolItemSelection(MToolBar toolbar, boolean selected, String... names)
    {
        final List<String> items = Arrays.asList(names);
        for (final MToolBarElement element : toolbar.getChildren())
        {
            final String id = element.getElementId().substring(toolbar.getElementId().length() + 1);
            if (items.isEmpty() || items.contains(id))
            {
                if (element instanceof MHandledToolItem)
                {
                    ((MHandledToolItem) element).setSelected(selected);
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
    public static void setToolItemEnabled(final MToolBar toolbar, final boolean enabled, final String... names)
    {
        final List<String> items = Arrays.asList(names);
        for (final MToolBarElement element : toolbar.getChildren())
        {
            final String id = element.getElementId().substring(toolbar.getElementId().length() + 1);
            if (items.isEmpty() || items.contains(id))
            {
                if (element.getWidget() instanceof ToolItem)
                {
                    ((ToolItem) element.getWidget()).setEnabled(enabled);
                }
            }
        }
    }

    /**
     * Private constructor.
     */
    private UtilEclipse()
    {
        throw new RuntimeException();
    }
}
