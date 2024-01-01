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
package com.b3dgs.lionengine.editor.utility;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

import com.b3dgs.lionengine.LionEngineException;

/**
 * Series of tool functions around the editor related to part.
 */
public final class UtilPart
{
    /** Part error. */
    private static final String ERROR_PART = "Unable to find part: ";

    /** Active application. */
    private static MApplication app;

    /**
     * Set the current active application.
     * 
     * @param app The active application.
     */
    public static void setApplication(MApplication app)
    {
        UtilPart.app = app;
    }

    /**
     * Get a part from its id.
     * 
     * @param id The part id.
     * @param clazz The part class type.
     * @param <C> The class type.
     * @return The part class instance.
     * @throws LionEngineException If part can not be found.
     */
    public static <C> C getPart(String id, Class<C> clazz)
    {
        final IEclipseContext activeContext = app.getContext().getActiveLeaf();
        final EPartService partService = activeContext.get(EPartService.class);
        final MPart part = partService.findPart(id);
        if (part != null)
        {
            partService.bringToTop(part);
            final Object object = part.getObject();
            if (object != null
                && (object.getClass().isAssignableFrom(clazz)
                    || clazz.isInterface() && clazz.isAssignableFrom(object.getClass())))
            {
                return clazz.cast(part.getObject());
            }
        }
        throw new LionEngineException(UtilPart.ERROR_PART + id);
    }

    /**
     * Get a part from its id.
     * 
     * @param id The part id.
     * @return The part class instance.
     * @throws LionEngineException If part can not be found.
     */
    public static MPart getMPart(String id)
    {
        final IEclipseContext activeContext = app.getContext().getActiveLeaf();
        final EPartService partService = activeContext.get(EPartService.class);
        final MPart part = partService.findPart(id);
        if (part == null)
        {
            throw new LionEngineException(UtilPart.ERROR_PART + id);
        }
        return part;
    }

    /**
     * Bring a part to top.
     * 
     * @param id The part id.
     * @throws LionEngineException If part can not be found.
     */
    public static void bringToTop(String id)
    {
        final IEclipseContext activeContext = app.getContext().getActiveLeaf();
        final EPartService partService = activeContext.get(EPartService.class);
        final MPart part = partService.findPart(id);
        if (part == null)
        {
            throw new LionEngineException(UtilPart.ERROR_PART + id);
        }
        partService.bringToTop(part);
    }

    /**
     * Private constructor.
     */
    private UtilPart()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
