package com.b3dgs.lionengine.example.c_platform.e_lionheart.menu;

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.Text;

/**
 * Represents a choice in the menu.
 */
final class Choice
{
    /** Horizontal location. */
    final int x;
    /** Vertical location. */
    final int y;
    /** Next menu pointer. */
    final MenuType next;
    /** Text reference. */
    private final Text text;
    /** Choice name. */
    private final String name;
    /** Text align. */
    private final Align align;

    /**
     * Constructor.
     * 
     * @param text The text reference.
     * @param factorH The horizontal factor value.
     * @param factorV The vertical factor value.
     * @param name The choice name.
     * @param x The horizontal location.
     * @param y The vertical location.
     * @param align The text align.
     */
    Choice(Text text, double factorH, double factorV, String name, int x, int y, Align align)
    {
        this(text, factorH, factorV, name, x, y, align, null);
    }

    /**
     * Constructor.
     * 
     * @param text The text reference.
     * @param factorH The horizontal factor value.
     * @param factorV The vertical factor value.
     * @param name The choice name.
     * @param x The horizontal location.
     * @param y The vertical location.
     * @param align The text align.
     * @param next The next menu pointer.
     */
    Choice(Text text, double factorH, double factorV, String name, int x, int y, Align align, MenuType next)
    {
        this.text = text;
        this.name = name;
        this.x = (int) (160 * factorH) - (160 - x);
        this.y = (int) (y * factorV);
        this.align = align;
        this.next = next;
    }

    /**
     * Render the choice.
     * 
     * @param g The graphic output.
     */
    void render(Graphic g)
    {
        text.draw(g, x, y, align, name);
    }
}
