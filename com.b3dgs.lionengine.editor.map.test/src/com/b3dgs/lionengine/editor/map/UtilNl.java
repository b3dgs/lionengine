/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.editor.map;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;

import org.eclipse.core.runtime.Platform;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.editor.Activator;

/**
 * Utility related to NL access.
 */
public class UtilNl
{
    private static final String[] NLS =
    {
        "fr"
    };
    private static final String NL_FOLDER = "OSGI-INF/l10n/";
    private static final String NL_REPLACER = "%NL%";
    private static final String NL_FILE_PREFIX = "bundle" + NL_REPLACER;
    private static final String NL_FILE_EXTENSION = ".properties";
    private static final Properties PROPERTIES = new Properties();

    static
    {
        try (InputStream input = Platform.getBundle(Activator.PLUGIN_ID + ".map")
                                         .getResource(NL_FOLDER
                                                      + NL_FILE_PREFIX.replace(NL_REPLACER, getNl())
                                                      + NL_FILE_EXTENSION)
                                         .openConnection()
                                         .getInputStream())
        {
            PROPERTIES.load(input);
        }
        catch (final IOException exception)
        {
            throw new LionEngineException(exception);
        }
    }

    /**
     * Get NL value.
     * 
     * @return The NL value, empty if default.
     */
    private static String getNl()
    {
        final String nl = Platform.getNL();
        for (final String current : NLS)
        {
            if (nl.toLowerCase(Locale.ENGLISH).contains(current))
            {
                return Constant.UNDERSCORE + current;
            }
        }
        return Constant.EMPTY_STRING;
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
