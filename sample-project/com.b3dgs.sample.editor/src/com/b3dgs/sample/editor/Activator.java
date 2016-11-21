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
package com.b3dgs.sample.editor;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.b3dgs.lionengine.core.Version;

/**
 * Plugin activator.
 */
public class Activator implements BundleActivator
{
    /** Plugin name. */
    public static final String PLUGIN_NAME = "Sample Editor";
    /** Plugin version. */
    public static final Version PLUGIN_VERSION = Version.create(0, 1, 0);
    /** Plugin website. */
    public static final String PLUGIN_WEBSITE = "www.b3dgs.com";
    /** Plugin ID. */
    public static final String PLUGIN_ID = "com.b3dgs.sample.editor";
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

    /*
     * BundleActivator
     */

    @Override
    public void start(BundleContext bundleContext) throws Exception
    {
        Activator.context = bundleContext;
    }

    @Override
    public void stop(BundleContext bundleContext) throws Exception
    {
        Activator.context = null;
    }
}
