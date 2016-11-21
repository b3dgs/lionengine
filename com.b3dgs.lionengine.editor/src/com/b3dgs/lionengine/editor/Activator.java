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

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

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

    /**
     * Create activator.
     */
    public Activator()
    {
        super();
    }

    /*
     * BundleActivator
     */

    @Override
    public void start(BundleContext context) throws Exception
    {
        EngineSwt.start(Activator.PLUGIN_NAME, Activator.PLUGIN_VERSION);
    }

    @Override
    public void stop(BundleContext context) throws Exception
    {
        Engine.terminate();
    }
}
