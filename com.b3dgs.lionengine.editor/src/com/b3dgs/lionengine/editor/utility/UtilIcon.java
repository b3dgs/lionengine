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

import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.UtilFile;
import com.b3dgs.lionengine.editor.Activator;

/**
 * Series of tool functions around the editor related to icons.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class UtilIcon
{
    /** Icon folder. */
    private static final String ICON_FOLDER = "icons";
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
    public static Image get(String icon) throws LionEngineException
    {
        return get(Constant.EMPTY_STRING, icon);
    }

    /**
     * Get the icon from its name.
     * 
     * @param root The icon root.
     * @param icon The icon name.
     * @return The icon instance.
     * @throws LionEngineException If error when getting icon.
     */
    public static Image get(String root, String icon) throws LionEngineException
    {
        final Bundle bundle = Activator.getContext().getBundle();
        final String path = UtilFile.getPathSeparator("/", UtilIcon.ICON_FOLDER, root, icon);
        final URL url = bundle.getEntry(path);
        if (url == null)
        {
            throw new LionEngineException(UtilIcon.ERROR_ICON_PATH + path);
        }
        try
        {
            final ImageDescriptor descriptor = ImageDescriptor.createFromURL(FileLocator.toFileURL(url));
            final Image image = descriptor.createImage();
            if (image == null)
            {
                throw new LionEngineException(UtilIcon.ERROR_ICON_CREATE + path);
            }
            return image;
        }
        catch (final IOException exception)
        {
            throw new LionEngineException(exception);
        }
    }

    /**
     * Private constructor.
     */
    private UtilIcon()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
