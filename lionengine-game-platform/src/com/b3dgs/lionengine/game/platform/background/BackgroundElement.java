package com.b3dgs.lionengine.game.platform.background;

import com.b3dgs.lionengine.drawable.Renderable;

/**
 * Represents a background element (contained in a background component).
 */
public class BackgroundElement
{
    /** Sprite reference. */
    private final Renderable sprite;
    /** Main location x referential. */
    private final int mainX;
    /** Main location y referential. */
    private final int mainY;
    /** Offsets location x from reference. */
    private double offsetX;
    /** Offsets location y from reference. */
    private double offsetY;

    /**
     * Create a new background element.
     * 
     * @param mainX The main location x.
     * @param mainY The main location y.
     * @param sprite The element sprite.
     */
    public BackgroundElement(int mainX, int mainY, Renderable sprite)
    {
        this.mainX = mainX;
        this.mainY = mainY;
        this.sprite = sprite;
        offsetX = 0.0;
        offsetY = 0.0;
    }

    /**
     * Set horizontal offset value.
     * 
     * @param offsetX The horizontal offset value.
     */
    public void setOffsetX(double offsetX)
    {
        this.offsetX = offsetX;
    }

    /**
     * Set vertical offset value.
     * 
     * @param offsetY The vertical offset value.
     */
    public void setOffsetY(double offsetY)
    {
        this.offsetY = offsetY;
    }

    /**
     * Get sprite reference.
     * 
     * @return The sprite reference.
     */
    public Renderable getSprite()
    {
        return sprite;
    }

    /**
     * Get main location x.
     * 
     * @return The main location x.
     */
    public int getMainX()
    {
        return mainX;
    }

    /**
     * Get main location y.
     * 
     * @return The main location y.
     */
    public int getMainY()
    {
        return mainY;
    }

    /**
     * Get horizontal offset.
     * 
     * @return The horizontal offset.
     */
    public double getOffsetX()
    {
        return offsetX;
    }

    /**
     * Get vertical offset.
     * 
     * @return The vertical offset.
     */
    public double getOffsetY()
    {
        return offsetY;
    }
}
