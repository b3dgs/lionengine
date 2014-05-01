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
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.UtilityFile;
import com.b3dgs.lionengine.Version;
import com.b3dgs.lionengine.core.Engine;
import com.b3dgs.lionengine.core.Verbose;

/**
 * Plugin activator.
 * 
 * @author Pierre-Alexandre
 */
public class Activator
        implements BundleActivator
{
    /** Plugin name. */
    public static final String PLUGIN_NAME = "LionEngine Editor";
    /** Plugin version. */
    public static final Version PLUGIN_VERSION = Version.create(6, 2, 0);
    /** Plugin website. */
    public static final String PLUGIN_WEBSITE = "www.b3dgs.com";
    /** Plugin ID. */
    public static final String PLUGIN_ID = "com.b3dgs.lionengine.editor";
    /** Context reference. */
    private static BundleContext context;

    /**
     * Get the context reference.
     * 
     * @return The context reference.
     */
    public static BundleContext getContext()
    {
        return Activator.context;
    }

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
                    .getBundle().getEntry(UtilityFile.getPath("icons", icon))));
            return image.createImage();
        }
        catch (final IOException exception)
        {
            throw new LionEngineException(exception);
        }
    }

    /**
     * Get the file from its name.
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
                    .getBundle().getEntry(UtilityFile.getPath("icons", root, icon))));
            return image.createImage();
        }
        catch (final IOException exception)
        {
            throw new LionEngineException(exception);
        }
    }

    /**
     * Center the shell on screen.
     * 
     * @param shell The shell to center.
     */
    public static void center(Shell shell)
    {
        final Monitor primary = shell.getMonitor();
        final Rectangle bounds = primary.getBounds();
        final Rectangle rect = shell.getBounds();
        final int x = bounds.x + (bounds.width - rect.width) / 2;
        final int y = bounds.y + (bounds.height - rect.height) / 2;
        shell.setLocation(x, y);
    }

    /*
     * BundleActivator
     */

    @Override
    public void start(BundleContext bundleContext) throws Exception
    {
        Activator.context = bundleContext;
        Engine.start(Activator.PLUGIN_NAME, Activator.PLUGIN_VERSION, Verbose.CRITICAL, (String) null);
    }

    @Override
    public void stop(BundleContext bundleContext) throws Exception
    {
        Activator.context = null;
    }

}
