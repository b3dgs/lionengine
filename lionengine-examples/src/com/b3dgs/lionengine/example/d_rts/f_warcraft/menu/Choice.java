package com.b3dgs.lionengine.example.d_rts.f_warcraft.menu;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.audio.Wav;
import com.b3dgs.lionengine.drawable.Cursor;
import com.b3dgs.lionengine.drawable.SpriteTiled;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.ResourcesLoader;
import com.b3dgs.lionengine.input.Mouse;

/**
 * Choice selection button (left & right).
 */
final class Choice
{
    /** Click sound. */
    private final Wav click;
    /** Button surface. */
    private final SpriteTiled surface;
    /** Button horizontal location. */
    private final int x;
    /** Button vertical location. */
    private final int y;
    /** Button width. */
    private final int w;
    /** Button height. */
    private final int h;
    /** <code>true</code> = right side, <code>false</code> = left. */
    private final boolean right;
    /** Mouse over state. */
    private boolean over;

    /**
     * Constructor.
     * 
     * @param surface The surface reference.
     * @param x The horizontal location.
     * @param y The vertical location.
     * @param right The button side.
     */
    Choice(SpriteTiled surface, int x, int y, boolean right)
    {
        this.surface = surface;
        this.x = x;
        this.y = y;
        this.right = right;
        click = ResourcesLoader.SOUND_CLICK;
        w = surface.getTileWidth();
        h = surface.getTileHeight();
        over = false;
    }

    /**
     * Update routine.
     * 
     * @param cursor The cursor reference.
     * @return <code>true</code> if pressed, <code>false</code> else.
     */
    boolean update(Cursor cursor)
    {
        final int cx = cursor.getLocationX();
        final int cy = cursor.getLocationY();
        boolean pressed = false;
        over = cx >= x && cy >= y && cx <= x + w && cy <= y + h;
        if (!Menu.clicked && over && cursor.getClick() == Mouse.LEFT)
        {
            click.play();
            pressed = true;
            Menu.clicked = true;
        }
        return pressed;
    }

    /**
     * Render routine.
     * 
     * @param g The graphics output.
     */
    void render(Graphic g)
    {
        int a = 0;
        if (right)
        {
            a += 2;
        }
        if (over)
        {
            surface.render(g, 1 + a, x, y);
        }
        else
        {
            surface.render(g, 0 + a, x, y);
        }
    }

    /**
     * Get the button side.
     * 
     * @return The button side.
     */
    int getSide()
    {
        return right ? 1 : -1;
    }
}
