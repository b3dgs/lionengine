package com.b3dgs.lionengine.drawable;

import java.awt.image.BufferedImage;

import com.b3dgs.lionengine.Filter;
import com.b3dgs.lionengine.Graphic;

/**
 * ParallaxedSprites are used for parallax effect (2.5D perspective). It cuts a sprite surface into an array of lines.
 * They are scaled using a trapeze representation, for the perspective effect.
 */
public interface SpriteParallaxed
        extends Renderable
{
    /**
     * Method used for sprite scaling, in order to modify its size. Normal factor is equal to 100, so 200 will scale it
     * twice bigger, whereas 50 will scale half its size.
     * 
     * @param percent value for scaling (>= 0).
     */
    void scale(int percent);

    /**
     * Works as scale, but using different width and height factor. Using different values, the ratio won't be kept, and
     * the sprite will be different !
     * 
     * @param percentWidth The percent value for scaling width (>= 0).
     * @param percentHeight The percent value for scaling height (>= 0).
     */
    void stretch(int percentWidth, int percentHeight);

    /**
     * Update all changes. Need to be called when changes are done.
     * 
     * @param filter The filter to use.
     */
    void prepare(Filter filter);

    /**
     * Render a line of parallax to the specified coordinates.
     * 
     * @param g The graphic output.
     * @param line The line to render (>= 0).
     * @param x The abscissa.
     * @param y The ordinate.
     */
    void render(Graphic g, int line, int x, int y);

    /**
     * Get the current sprite width (its current size, after scaling operation).
     * 
     * @return The sprite width size as integer.
     */
    int getWidthOriginal();

    /**
     * Get the current sprite height (its current size, after scaling operation).
     * 
     * @return The sprite height size as integer.
     */
    int getHeightOriginal();

    /**
     * Get a parallax line (first index is 0).
     * 
     * @param line The desired line (>= 0).
     * @return The line's surface.
     */
    BufferedImage getLine(int line);
}
