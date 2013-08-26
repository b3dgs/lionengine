package com.b3dgs.lionengine.drawable;

import java.awt.Color;
import java.awt.image.BufferedImage;

import com.b3dgs.lionengine.Filter;
import com.b3dgs.lionengine.Graphic;

/**
 * <p>
 * Sprites work like images, but support modifications, such as scaling and filtering. They are recommended for dynamic
 * uses, such as menus, entities or backgrounds elements (which are not statics).
 * </p>
 * <p>
 * For each modifications (scale, flip, rotate...), the original surface is modified. So <code>rotate(1)</code> followed
 * by <code>rotate(-1)</code> will not give the same sprite as before.
 * </p>
 * <p>
 * There are two steps for the initialization:
 * </p>
 * <ul>
 * <li>Create the sprite.</li>
 * <li>Call {@link #load(boolean)} (this function will load the surface)</li>
 * </ul>
 * <p>
 * Example:
 * </p>
 * 
 * <pre>
 * // Load
 * final Sprite sprite = Drawable.loadSprite(Media.get(&quot;sprite.png&quot;));
 * sprite.load(false);
 * 
 * // Render
 * sprite.render(g, 64, 280);
 * </pre>
 */
public interface Sprite
        extends Image
{
    /**
     * Load surface and prepare it to be displayed. This function must be called if the surface is loaded from a file,
     * else the surface will be never prepared.
     * 
     * @param alpha Set <code>true</code> to enable alpha, <code>false</code> else.
     */
    void load(boolean alpha);

    /**
     * Method used for sprite scaling, in order to modify its size. Normal factor is equal to <code>100</code>, so
     * <code>200</code> will scale it twice bigger, whereas <code>50</code> will scale half its size.
     * 
     * @param percent The value for scaling in percent (>= 0).
     */
    void scale(int percent);

    /**
     * Works as scale, but using different width and height factor. Using different value, the ratio won't be kept, and
     * the sprite will be different.
     * 
     * @param percentWidth The percent value for scaling width (>= 0).
     * @param percentHeight The percent value for scaling height (>= 0).
     */
    void stretch(int percentWidth, int percentHeight);

    /**
     * Rotate the sprite with the specified angle.
     * 
     * @param angle The rotation angle in degree <code>[0 - 360]</code>.
     */
    void rotate(int angle);

    /**
     * Flip the sprite horizontally (horizontal mirror).
     */
    void flipHorizontal();

    /**
     * Flip the sprite vertically (vertical mirror).
     */
    void flipVertical();

    /**
     * Apply a filter to the sprite.
     * 
     * @param filter The filter to use.
     */
    void filter(Filter filter);

    /**
     * Set transparency color mask.
     * 
     * @param mask The color mask.
     */
    void setTransparency(Color mask);

    /**
     * Set alpha value.
     * 
     * @param alpha The alpha value <code>[0 - 255]</code>.
     */
    void setAlpha(int alpha);

    /**
     * Get the current sprite width (its current size, after scaling operation).
     * 
     * @return sprite The width size as integer.
     */
    int getWidthOriginal();

    /**
     * Get the current sprite height (its current size, after scaling operation).
     * 
     * @return sprite The height size as integer.
     */
    int getHeightOriginal();

    /*
     * Renderable
     */

    /**
     * Render the sprite on graphic output at specified coordinates.
     * 
     * @param g The graphic output.
     * @param x The abscissa.
     * @param y The ordinate.
     */
    @Override
    void render(Graphic g, int x, int y);

    /*
     * Image
     */

    /**
     * Get the sprite surface, represented by a BufferedImage.
     * 
     * @return The buffer reference representing the sprite.
     */
    @Override
    BufferedImage getSurface();

    /**
     * Get instanced version of current sprite (shares the same surface). The {@link #load(boolean)} function should not
     * be called for this instance as the surface has already been prepared.
     * 
     * @return The instanced sprite.
     */
    @Override
    Sprite instanciate();
}
