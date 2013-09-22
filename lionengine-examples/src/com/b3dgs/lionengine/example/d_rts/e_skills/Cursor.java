package com.b3dgs.lionengine.example.d_rts.e_skills;

import java.awt.Color;

import com.b3dgs.lionengine.Display;
import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.game.maptile.MapTile;
import com.b3dgs.lionengine.game.rts.CursorRts;

/**
 * Cursor implementation.
 */
public final class Cursor
        extends CursorRts
{
    /** Current cursor type. */
    private CursorType type;
    /** Box width. */
    private int boxWidth;
    /** Box height. */
    private int boxHeight;
    /** Box color. */
    private Color boxColor;

    /**
     * Create a new rts cursor.
     * 
     * @param internal The internal display reference.
     * @param map The map reference.
     * @param cursor The cursor media.
     */
    Cursor(Display internal, MapTile<?, ?> map, Media... cursor)
    {
        super(internal, map, cursor);
        type = CursorType.POINTER;
    }

    /**
     * Set the cursor type.
     * 
     * @param type The cursor type.
     */
    public void setType(CursorType type)
    {
        this.type = type;
        switch (type)
        {
            case POINTER:
                setSurfaceId(0);
                setRenderingOffset(0, 0);
                break;
            case WEN:
                setRenderingOffset(-5, -5);
                setSurfaceId(1);
                break;
            case CROSS:
                setSurfaceId(2);
                setRenderingOffset(-7, -5);
                break;
            default:
                break;
        }
    }

    /**
     * Set the box size.
     * 
     * @param width The grid width.
     * @param height The grid height.
     */
    public void setBoxSize(int width, int height)
    {
        boxWidth = width * getGridWidth();
        boxHeight = height * getGridHeight();
    }

    /**
     * Set the grid color.
     * 
     * @param color The grid color.
     */
    public void setBoxColor(Color color)
    {
        boxColor = color;
    }

    /**
     * Get the cursor type.
     * 
     * @return The cursor type.
     */
    public CursorType getType()
    {
        return type;
    }

    /**
     * Render box cursor in has.
     * 
     * @param g The graphics output.
     */
    public void renderBox(Graphic g)
    {
        if (CursorType.BOX == type)
        {
            g.setColor(boxColor);
            g.drawRect((getScreenX() + 8) / getGridWidth() * getGridWidth() - 8,
                    ((getScreenY() + 4) / getGridHeight() + 1) * getGridHeight() - 4 - boxHeight, boxWidth, boxHeight,
                    false);
        }
    }

    /*
     * CursorRts
     */

    @Override
    public void render(Graphic g)
    {
        if (CursorType.BOX != type)
        {
            super.render(g);
        }
    }
}
