/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.b3dgs.lionengine.editor;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Engine;
import com.b3dgs.lionengine.Version;
import com.b3dgs.lionengine.swt.graphic.EngineSwt;

/**
 * Plugin activator.
 */
public class Activator implements BundleActivator
{
    /** Plugin name. */
    public static final String PLUGIN_NAME = Constant.ENGINE_NAME + " Editor";
    /** Plugin version. */
    public static final Version PLUGIN_VERSION = Constant.ENGINE_VERSION;
    /** Plugin website. */
    public static final String PLUGIN_WEBSITE = Constant.ENGINE_WEBSITE;
    /** Plugin ID. */
    public static final String PLUGIN_ID = "com.b3dgs.lionengine.editor";

    /**
     * Configure log4j from file.
     * 
     * @param bundle The bundle reference.
     * @throws IOException If error.
     */
    private static void configureLog4j(Bundle bundle) throws IOException
    {
        final URL url = bundle.getResource("log4j2.xml");
        final Path config = Files.createTempFile("log4j2", ".xml");
        try (InputStream input = url.openStream())
        {
            Files.copy(input, config, StandardCopyOption.REPLACE_EXISTING);
        }
        System.setProperty("log4j2.configurationFile", config.toFile().toURI().toString());
    }

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
        configureLog4j(context.getBundle());
        EngineSwt.start(Activator.PLUGIN_NAME, Activator.PLUGIN_VERSION);
    }

    @Override
    public void stop(BundleContext context) throws Exception
    {
        Engine.terminate();
    }
}
