package com.b3dgs.lionengine.drawable;

/**
 * It allows images loading and rendering. Images can't be resized and can't use any filters.
 * <p>
 * Example
 * </p>
 * 
 * <pre>
 * // Load
 * final Image image = Drawable.loadImage(Media.get(&quot;image.png&quot;));
 * 
 * // Render
 * image.render(g, 0, 0);
 * </pre>
 */
public interface Image
        extends Renderable
{
    /**
     * Get the surface which represents the image (java type).
     * 
     * @return The java image reference.
     */
    java.awt.Image getSurface();

    /**
     * Get instanced version of current image (faster than a clone, but shares the same surface).
     * 
     * @return The instanced image.
     */
    Image instanciate();
}
