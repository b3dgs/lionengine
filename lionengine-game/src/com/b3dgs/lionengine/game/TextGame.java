package com.b3dgs.lionengine.game;

import java.awt.Color;

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.Text;
import com.b3dgs.lionengine.game.purview.Localizable;

/**
 * Game text implementation. This class enhance the text in order to use it in a game referential. It is perfect to
 * display text over an entity for example, or to give the impression that the text is in the world and not simply on
 * screen.
 * <p>
 * The use is strictly the same as {@link Text}, just including an additional function which is
 * {@link TextGame#update(CameraGame)}, needed to update the text location.
 * </p>
 */
public final class TextGame
        extends Text
{
    /** Location x. */
    private int x;
    /** Location y. */
    private int y;
    /** Camera view height. */
    private int height;

    /**
     * Constructor.
     * 
     * @param fontName The font name.
     * @param size The font size.
     * @param style The font style.
     */
    public TextGame(String fontName, int size, int style)
    {
        super(fontName, size, style);
    }

    /**
     * Update game text to store current location view.
     * 
     * @param camera The camera reference.
     */
    public void update(CameraGame camera)
    {
        x = camera.getLocationIntX();
        y = camera.getLocationIntY();
        height = camera.getViewHeight();
    }

    /**
     * Renders text on graphic output, to the specified location using the specified localizable referential.
     * 
     * @param g The graphic output.
     * @param localizable The localizable used to draw the text.
     * @param offsetX The horizontal offset from the localizable horizontal location.
     * @param offsetY The vertical offset from the localizable vertical location.
     * @param align The alignment value.
     * @param text The text string.
     */
    public void draw(Graphic g, Localizable localizable, int offsetX, int offsetY, Align align, String text)
    {
        draw(g, localizable.getLocationIntX() + offsetX, localizable.getLocationIntY() + offsetY, align, text);
    }

    /**
     * Renders text on graphic output, to the specified location using the specified localizable referential.
     * 
     * @param g The graphic output.
     * @param color The rectangle color.
     * @param x The horizontal location.
     * @param y The vertical location.
     * @param width The rectangle width.
     * @param height The rectangle height.
     */
    public void drawRect(Graphic g, Color color, int x, int y, int width, int height)
    {
        g.setColor(color);
        g.drawRect(x - this.x, this.y - y - height + this.height, width, height, false);
    }

    /*
     * Text
     */

    @Override
    public void draw(Graphic g, int x, int y, Align alignment, String text)
    {
        super.draw(g, x - this.x, this.y - y + height, alignment, text);
    }
}
