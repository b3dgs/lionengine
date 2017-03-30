/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

/**
 * Plugin activator.
 */
public class Activator implements BundleActivator
{
    /** Context reference. */
    private static BundleContext context;

    /**
     * Get the context reference.
     * 
     * @return The context reference.
     */
    public static BundleContext getContext()
    {
        return context;
    }

    /*
     * BundleActivator
     */

    @Override
    public void start(BundleContext bundleContext) throws Exception
    {
        context = bundleContext;
    }

    @Override
    public void stop(BundleContext bundleContext) throws Exception
    {
        context = null;
    }
}
