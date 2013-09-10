package com.b3dgs.lionengine.game;

/**
 * Represents the collision data, offsets and size.
 */
public class CollisionData
{
    /** Horizontal offset. */
    private final int offsetX;
    /** Vertical offset. */
    private final int offsetY;
    /** Width. */
    private final int width;
    /** Height. */
    private final int height;

    /**
     * Constructor.
     * 
     * @param offsetX The collision horizontal offset.
     * @param offsetY The collision vertical offset.
     * @param width The collision width.
     * @param height The collision height.
     */
    public CollisionData(int offsetX, int offsetY, int width, int height)
    {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.width = width;
        this.height = height;
    }

    /**
     * Get the horizontal collision offset.
     * 
     * @return The horizontal collision offset.
     */
    public int getOffsetX()
    {
        return offsetX;
    }

    /**
     * Get the vertical collision offset.
     * 
     * @return The vertical collision offset.
     */
    public int getOffsetY()
    {
        return offsetY;
    }

    /**
     * Get the collision width.
     * 
     * @return The collision width.
     */
    public int getWidth()
    {
        return width;
    }

    /**
     * Get the collision height.
     * 
     * @return The collision height.
     */
    public int getHeight()
    {
        return height;
    }
}
