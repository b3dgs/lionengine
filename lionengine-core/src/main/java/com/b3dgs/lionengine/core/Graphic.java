/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.core;

import com.b3dgs.lionengine.ColorGradient;
import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.Viewer;

/**
 * Graphic interface representing the screen buffer.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public interface Graphic
{
    /**
     * Clear the display.
     * 
     * @param x The horizontal location.
     * @param y The vertical location.
     * @param width The width.
     * @param height The height.
     */
    void clear(int x, int y, int width, int height);

    /**
     * Dispose the graphic and release associated resources.
     */
    void dispose();

    /**
     * Copies an area of the component by a distance specified by <code>dx</code> and <code>dy</code>. From the point
     * specified by <code>x</code> and <code>y</code>, this method copies downwards and to the right.
     * 
     * @param x the <i>x</i> coordinate of the source rectangle.
     * @param y the <i>y</i> coordinate of the source rectangle.
     * @param width the width of the source rectangle.
     * @param height the height of the source rectangle.
     * @param dx the horizontal distance to copy the pixels.
     * @param dy the vertical distance to copy the pixels.
     */
    void copyArea(int x, int y, int width, int height, int dx, int dy);

    /**
     * Draw an image to the graphic.
     * 
     * @param image The image to draw.
     * @param x The horizontal location.
     * @param y The vertical location.
     */
    void drawImage(ImageBuffer image, int x, int y);

    /**
     * Draw an image to the graphic.
     * 
     * @param image The image to draw.
     * @param op The affine transformation.
     * @param x The horizontal location.
     * @param y The vertical location.
     */
    void drawImage(ImageBuffer image, Transform op, int x, int y);

    /**
     * Draw a part of an image from a source rectangle to a destination rectangle.
     * 
     * @param image The image to draw.
     * @param dx1 The <i>x</i> coordinate of the first corner of the destination rectangle.
     * @param dy1 The <i>y</i> coordinate of the first corner of the destination rectangle.
     * @param dx2 The <i>x</i> coordinate of the second corner of the destination rectangle.
     * @param dy2 The <i>y</i> coordinate of the second corner of the destination rectangle.
     * @param sx1 The <i>x</i> coordinate of the first corner of the source rectangle.
     * @param sy1 The <i>y</i> coordinate of the first corner of the source rectangle.
     * @param sx2 The <i>x</i> coordinate of the second corner of the source rectangle.
     * @param sy2 The <i>y</i> coordinate of the second corner of the source rectangle.
     */
    void drawImage(ImageBuffer image, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2);

    /**
     * Draws the specified rectangle. The left and right edges of the rectangle are at <code>x</code> and
     * <code>x&nbsp;+&nbsp;width&nbsp;-&nbsp;1</code>. The top and bottom edges are at <code>y</code> and
     * <code>y&nbsp;+&nbsp;height&nbsp;-&nbsp;1</code>. The resulting rectangle covers an area <code>width</code> pixels
     * wide by <code>height</code> pixels tall.
     * 
     * @param x The horizontal location.
     * @param y The vertical location.
     * @param width The width.
     * @param height The height.
     * @param fill <code>true</code> to fill the rectangle, <code>false</code> to draw only its borders.
     */
    void drawRect(int x, int y, int width, int height, boolean fill);

    /**
     * Draws the specified rectangle. The left and right edges of the rectangle are at <code>x</code> and
     * <code>x&nbsp;+&nbsp;width&nbsp;-&nbsp;1</code>. The top and bottom edges are at <code>y</code> and
     * <code>y&nbsp;+&nbsp;height&nbsp;-&nbsp;1</code>. The resulting rectangle covers an area <code>width</code> pixels
     * wide by <code>height</code> pixels tall.
     * Rendering is performed considering the viewer location.
     * 
     * @param viewer The viewer reference.
     * @param origin Origin point referential used.
     * @param x The horizontal location.
     * @param y The vertical location.
     * @param width The width.
     * @param height The height.
     * @param fill <code>true</code> to fill the rectangle, <code>false</code> to draw only its borders.
     */
    void drawRect(Viewer viewer, Origin origin, double x, double y, int width, int height, boolean fill);

    /**
     * Draws the specified gradient rectangle. The left and right edges of the rectangle are at <code>x</code> and
     * <code>x&nbsp;+&nbsp;width&nbsp;-&nbsp;1</code>. The top and bottom edges are at <code>y</code> and
     * <code>y&nbsp;+&nbsp;height&nbsp;-&nbsp;1</code>. The resulting rectangle covers an area <code>width</code> pixels
     * wide by <code>height</code> pixels tall.
     * The gradient is defined by {@link #setColorGradient(ColorGradient)}.
     * 
     * @param x The horizontal location.
     * @param y The vertical location.
     * @param width The width.
     * @param height The height.
     */
    void drawGradient(int x, int y, int width, int height);

    /**
     * Draws the specified gradient rectangle. The left and right edges of the rectangle are at <code>x</code> and
     * <code>x&nbsp;+&nbsp;width&nbsp;-&nbsp;1</code>. The top and bottom edges are at <code>y</code> and
     * <code>y&nbsp;+&nbsp;height&nbsp;-&nbsp;1</code>. The resulting rectangle covers an area <code>width</code> pixels
     * wide by <code>height</code> pixels tall.
     * The gradient is defined by {@link #setColorGradient(ColorGradient)}.
     * Rendering is performed considering the viewer location.
     * 
     * @param viewer The viewer reference.
     * @param origin Origin point referential used.
     * @param x The horizontal location.
     * @param y The vertical location.
     * @param width The width.
     * @param height The height.
     */
    void drawGradient(Viewer viewer, Origin origin, double x, double y, int width, int height);

    /**
     * Draws a line, using the current color, between the points <code>(x1,&nbsp;y1)</code> and
     * <code>(x2,&nbsp;y2)</code> in this graphic.
     * 
     * @param x1 The first point's <i>x</i> coordinate.
     * @param y1 The first point's <i>y</i> coordinate.
     * @param x2 The second point's <i>x</i> coordinate.
     * @param y2 The second point's <i>y</i> coordinate.
     */
    void drawLine(int x1, int y1, int x2, int y2);

    /**
     * Draws a line, using the current color, between the points <code>(x1,&nbsp;y1)</code> and
     * <code>(x2,&nbsp;y2)</code> in this graphic.
     * Rendering is performed considering the viewer location.
     * 
     * @param viewer The viewer reference.
     * @param x1 The first point's <i>x</i> coordinate.
     * @param y1 The first point's <i>y</i> coordinate.
     * @param x2 The second point's <i>x</i> coordinate.
     * @param y2 The second point's <i>y</i> coordinate.
     */
    void drawLine(Viewer viewer, double x1, double y1, double x2, double y2);

    /**
     * Fills an oval bounded by the specified rectangle with the current color.
     * 
     * @param x the <i>x</i> coordinate of the upper left corner of the oval to be filled.
     * @param y the <i>y</i> coordinate of the upper left corner of the oval to be filled.
     * @param width the width of the oval to be filled.
     * @param height the height of the oval to be filled.
     * @param fill <code>true</code> to fill the rectangle, <code>false</code> to draw only its borders.
     */
    void drawOval(int x, int y, int width, int height, boolean fill);

    /**
     * Fills an oval bounded by the specified rectangle with the current color.
     * Rendering is performed considering the viewer location.
     * 
     * @param viewer The viewer reference.
     * @param origin Origin point referential used.
     * @param x the <i>x</i> coordinate of the upper left corner of the oval to be filled.
     * @param y the <i>y</i> coordinate of the upper left corner of the oval to be filled.
     * @param width the width of the oval to be filled.
     * @param height the height of the oval to be filled.
     * @param fill <code>true</code> to fill the rectangle, <code>false</code> to draw only its borders.
     */
    void drawOval(Viewer viewer, Origin origin, double x, double y, int width, int height, boolean fill);

    /**
     * Set the current graphic color.
     * 
     * @param color The color.
     */
    void setColor(ColorRgba color);

    /**
     * Set the gradient color.
     * 
     * @param gradientColor The gradient color.
     */
    void setColorGradient(ColorGradient gradientColor);

    /**
     * Set the graphic context.
     * 
     * @param graphic The graphic context.
     */
    void setGraphic(Object graphic);

    /**
     * Get the graphic context.
     * 
     * @return The graphic context.
     */
    Object getGraphic();

    /**
     * Get the current color.
     * 
     * @return The current graphic color.
     */
    ColorRgba getColor();

}
