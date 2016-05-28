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
package com.b3dgs.lionengine.graphic;

import com.b3dgs.lionengine.Localizable;
import com.b3dgs.lionengine.Shape;

/**
 * Represents something that is able to view a scene, considered as the point of view of the user, such as a camera.
 */
public interface Viewer extends Shape
{
    /**
     * Get the horizontal viewpoint from the object location.
     * 
     * @param x The object horizontal location.
     * @return The horizontal viewpoint.
     */
    double getViewpointX(double x);

    /**
     * Get the vertical viewpoint from the object location.
     * 
     * @param y The object vertical location.
     * @return The vertical viewpoint.
     */
    double getViewpointY(double y);

    /**
     * Get horizontal view offset.
     * 
     * @return The horizontal view offset.
     */
    int getViewX();

    /**
     * Get vertical view offset.
     * 
     * @return The vertical view offset.
     */
    int getViewY();

    /**
     * Return the screen height.
     * 
     * @return The screen height.
     */
    int getScreenHeight();

    /**
     * Check if the localizable is inside the view area, and so, can be seen.
     * 
     * @param shape The shape to check.
     * @param marginX The horizontal margin.
     * @param marginY The vertical margin.
     * @return <code>true</code> if viewable, <code>false</code> else.
     */
    boolean isViewable(Localizable shape, int marginX, int marginY);

    /**
     * Check if the shape is inside the view area, and so, can be seen.
     * 
     * @param shape The shape to check.
     * @param marginX The horizontal margin.
     * @param marginY The vertical margin.
     * @return <code>true</code> if viewable, <code>false</code> else.
     */
    boolean isViewable(Shape shape, int marginX, int marginY);
}
