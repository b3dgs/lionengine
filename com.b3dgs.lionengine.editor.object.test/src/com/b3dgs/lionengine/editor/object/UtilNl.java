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
package com.b3dgs.lionengine.editor.object;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.eclipse.core.runtime.Platform;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.editor.Activator;

/**
 * Utility related to NL access.
 */
public class UtilNl
{
    private static final String NL = Platform.getNL();
    private static final char NL_SEPARATOR = '_';
    private static final String NL_FOLDER = "OSGI-INF/l10n/";
    private static final String NL_FILE_PREFIX = "bundle_";
    private static final String NL_FILE_EXTENSION = ".properties";
    private static final String NL_FILE = NL_FOLDER
                                          + NL_FILE_PREFIX
                                          + NL.substring(0, NL.indexOf(NL_SEPARATOR))
                                          + NL_FILE_EXTENSION;
    private static final URL NL_PROPERTIES_FILE = Platform.getBundle(Activator.PLUGIN_ID + ".object")
                                                          .getResource(NL_FILE);
    private static final Properties PROPERTIES = new Properties();

    static
    {
        try (InputStream input = NL_PROPERTIES_FILE.openConnection().getInputStream())
        {
            PROPERTIES.load(input);
        }
        catch (final IOException exception)
        {
            throw new LionEngineException(exception);
        }
    }

    /**
     * Get NL string value from key.
     * 
     * @param key The string key.
     * @return The string value.
     */
    public static String get(String key)
    {
        return PROPERTIES.getProperty(key);
    }
}
