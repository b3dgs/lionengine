package com.b3dgs.lionengine.drawable;

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.Graphic;

/**
 * Allows the use of image based font.
 */
public interface SpriteFont
        extends Sprite
{
    /** New line char. */
    char NEW_LINE = '%';

    /**
     * Draw a multiple text at specified location.
     * 
     * @param g The graphics output.
     * @param x The location x.
     * @param y The location y.
     * @param align The alignment.
     * @param text The input text.
     */
    void draw(Graphic g, int x, int y, Align align, String... text);

    /**
     * Draw a single text at specified location.
     * 
     * @param g The graphics output.
     * @param x The location x.
     * @param y The location y.
     * @param align The alignment.
     * @param text The input text.
     */
    void draw(Graphic g, int x, int y, Align align, String text);

    /**
     * Set line height (default = letter height).
     * 
     * @param height The line height.
     */
    void setLineHeight(int height);

    /**
     * Get global text width.
     * 
     * @param text The input text.
     * @return The text width.
     */
    int getTextWidth(String text);

    /**
     * Get global text height.
     * 
     * @param text The input height.
     * @return The text height.
     */
    int getTextHeight(String text);

    /**
     * Get instanced version of current sprite font (shares the same surface).
     * 
     * @return The cloned sprite font.
     */
    @Override
    SpriteFont instanciate();
}
