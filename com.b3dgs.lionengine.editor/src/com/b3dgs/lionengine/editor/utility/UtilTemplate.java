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
package com.b3dgs.lionengine.editor.utility;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.FileLocator;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.UtilFile;
import com.b3dgs.lionengine.editor.Activator;

/**
 * Series of tool functions around the editor related to template.
 */
public final class UtilTemplate
{
    /** Templates extension. */
    public static final String TEMPLATE_EXTENSION = "template";
    /** Templates directory. */
    public static final String TEMPLATES_DIR = "templates";
    /** Template object. */
    public static final String TEMPLATE_OBJECT = "object." + UtilTemplate.TEMPLATE_EXTENSION;
    /** Template sheets. */
    public static final String TEMPLATE_SHEETS = "sheets." + UtilTemplate.TEMPLATE_EXTENSION;
    /** Template sheets. */
    public static final String TEMPLATE_GROUPS = "groups." + UtilTemplate.TEMPLATE_EXTENSION;
    /** Template sheets. */
    public static final String TEMPLATE_FORMULAS = "formulas." + UtilTemplate.TEMPLATE_EXTENSION;
    /** Template sheets. */
    public static final String TEMPLATE_COLLISIONS = "collisions." + UtilTemplate.TEMPLATE_EXTENSION;
    /** Template sheets. */
    public static final String TEMPLATE_MINIMAP = "minimap." + UtilTemplate.TEMPLATE_EXTENSION;
    /** Template class area. */
    public static final String TEMPLATE_CLASS_AREA = "%CLASS%";
    /** Template setup area. */
    public static final String TEMPLATE_SETUP_AREA = "%SETUP%";
    /** Template sheets tile width area. */
    public static final String TEMPLATE_SHEETS_WIDTH = "%WIDTH%";
    /** Template sheets tile height area. */
    public static final String TEMPLATE_SHEETS_HEIGHT = "%HEIGHT%";

    /**
     * Get a template file from it name. The template must be in {@link #TEMPLATES_DIR} folder.
     * 
     * @param template The template name.
     * @return The template file.
     * @throws LionEngineException If path error.
     */
    public static File getTemplate(String template)
    {
        return getFile(UtilFile.getPath(UtilTemplate.TEMPLATES_DIR, template));
    }

    /**
     * Get the file from its name, relative to the plugin path.
     * 
     * @param file The file name.
     * @return The file instance.
     * @throws LionEngineException If path error.
     */
    private static File getFile(String file)
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
     * Private constructor.
     */
    private UtilTemplate()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
