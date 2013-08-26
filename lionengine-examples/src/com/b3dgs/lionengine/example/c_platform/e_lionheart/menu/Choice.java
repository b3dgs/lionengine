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
    final TypeMenu next;
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
     * @param wideFactor The wide factor value.
     * @param name The choice name.
     * @param x The horizontal location.
     * @param y The vertical location.
     * @param align The text align.
     */
    Choice(Text text, int wideFactor, String name, int x, int y, Align align)
    {
        this(text, wideFactor, name, x, y, align, null);
    }

    /**
     * Constructor.
     * 
     * @param text The text reference.
     * @param wideFactor The wide factor value.
     * @param name The choice name.
     * @param x The horizontal location.
     * @param y The vertical location.
     * @param align The text align.
     * @param next The next menu pointer.
     */
    Choice(Text text, int wideFactor, String name, int x, int y, Align align, TypeMenu next)
    {
        this.text = text;
        this.name = name;
        this.x = 160 * wideFactor - (160 - x);
        this.y = y;
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
