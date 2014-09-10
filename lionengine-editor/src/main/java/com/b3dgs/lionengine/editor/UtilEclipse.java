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

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

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
     * Private constructor.
     */
    private UtilEclipse()
    {
        throw new RuntimeException();
    }
}
