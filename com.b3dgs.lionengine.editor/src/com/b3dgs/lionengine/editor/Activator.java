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
package com.b3dgs.lionengine.editor;

import java.io.File;

import org.eclipse.core.runtime.IProduct;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.core.Engine;
import com.b3dgs.lionengine.core.Version;
import com.b3dgs.lionengine.core.swt.EngineSwt;

/**
 * Plugin activator.
 */
public class Activator implements BundleActivator
{
    /** Plugin name. */
    public static final String PLUGIN_NAME = Engine.NAME + " Editor";
    /** Plugin version. */
    public static final Version PLUGIN_VERSION = Engine.VERSION;
    /** Plugin website. */
    public static final String PLUGIN_WEBSITE = Engine.WEBSITE;
    /** Plugin ID. */
    public static final String PLUGIN_ID = "com.b3dgs.lionengine.editor";
    /** Context reference. */
    private static volatile BundleContext context;

    /**
     * Get the context reference.
     * 
     * @return The context reference.
     */
    public static synchronized BundleContext getContext()
    {
        return Activator.context;
    }

    /**
     * Get the current main bundle.
     * 
     * @return The main bundle.
     */
    public static Bundle getMainBundle()
    {
        final IProduct product = Platform.getProduct();
        if (product != null)
        {
            return product.getDefiningBundle();
        }
        return Activator.getContext().getBundle();
    }

    /**
     * Get the bundle absolute location.
     * 
     * @return The bundle absolute location.
     */
    public static File getLocation()
    {
        final String location = Activator.getMainBundle().getLocation();
        final String path = location.substring(location.lastIndexOf(':') + 1);
        return new File(path).getAbsoluteFile();
    }

    /**
     * Set the context reference.
     * 
     * @param context The context reference.
     */
    private static void setContext(BundleContext context)
    {
        Activator.context = context;
    }

    /**
     * Create activator.
     */
    public Activator()
    {
        // Nothing to do
    }

    /*
     * BundleActivator
     */

    @Override
    public void start(BundleContext context) throws Exception
    {
        setContext(context);
        LionEngineException.setIgnoreEngineTrace(false);
        EngineSwt.start(Activator.PLUGIN_NAME, Activator.PLUGIN_VERSION);
    }

    @Override
    public void stop(BundleContext context) throws Exception
    {
        setContext(null);
    }
}
