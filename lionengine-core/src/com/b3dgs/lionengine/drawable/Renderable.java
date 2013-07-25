package com.b3dgs.lionengine.drawable;

import com.b3dgs.lionengine.Graphic;

/**
 * Anything that can be rendered on screen.
 */
public interface Renderable
{
    /**
     * Render image on current graphic output, at specified coordinates.
     * 
     * @param g The graphic output.
     * @param x The abscissa.
     * @param y The ordinate.
     */
    void render(Graphic g, int x, int y);

    /**
     * Get the the width size.
     * 
     * @return The width size.
     */
    int getWidth();

    /**
     * Get the the height size.
     * 
     * @return The height size.
     */
    int getHeight();
}
