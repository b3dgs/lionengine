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
package com.b3dgs.lionengine.xsd;

import java.net.URI;
import java.net.URISyntaxException;

import com.b3dgs.lionengine.LionEngineException;

/**
 * XSD file loader.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class XsdLoader
{
    /** Main XSD file. */
    public static final String XSD_LIONENGINE = "lionengine.xsd";
    /** Object XSD. */
    public static final String XSD_OBJECT = "object.xsd";
    /** Entity platform XSD. */
    public static final String XSD_ENTITY_PLATFORM = "entityPlatform.xsd";
    /** Projectile XSD. */
    public static final String XSD_PROJECTILE = "projectile.xsd";
    /** Folder type XSD. */
    public static final String XSD_FOLDER_TYPE = "folderType.xsd";
    /** Tile collisions XSD. */
    public static final String XSD_TILE_COLLISIONS = "tileCollisions.xsd";
    /** Tile sheets XSD. */
    public static final String XSD_TILE_SHEETS = "tileSheets.xsd";

    /**
     * Get an XSD from its name.
     * 
     * @param name The XSD file name.
     * @return The XSD file instance.
     * @throws LionEngineException If error when loading the file.
     */
    public static URI get(String name) throws LionEngineException
    {
        try
        {
            return XsdLoader.class.getResource(name).toURI();
        }
        catch (final URISyntaxException exception)
        {
            throw new LionEngineException(exception);
        }
    }

    /**
     * Private constructor.
     */
    private XsdLoader()
    {
        throw new RuntimeException();
    }
}
