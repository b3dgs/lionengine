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
package com.b3dgs.lionengine.game;

/**
 * Surface representation, described by a simple location and offset plus a size.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public interface Surface
{
    /**
     * Get the current rounded horizontal location.
     * 
     * @return The current horizontal location.
     */
    int getLocationIntX();

    /**
     * Get the current rounded vertical location.
     * 
     * @return The current vertical location.
     */
    int getLocationIntY();

    /**
     * Get the width.
     * 
     * @return The width.
     */
    int getWidth();

    /**
     * Get the height.
     * 
     * @return The height.
     */
    int getHeight();
}
