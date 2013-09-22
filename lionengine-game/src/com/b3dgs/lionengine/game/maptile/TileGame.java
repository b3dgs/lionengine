package com.b3dgs.lionengine.game.maptile;

import java.util.List;

/**
 * Default class tile; containing following data:
 * <ul>
 * <li><code>pattern</code> : tilesheet number</li>
 * <li><code>number</code> : tile number inside current tilesheet</li>
 * <li><code>x & y</code> : real location</li>
 * <li><code>collision</code> : collision name</li>
 * </ul>
 * <p>
 * The abstract function {@link #getCollisionFrom(String)} allows to convert a string collision name to its
 * corresponding enum.
 * </p>
 * 
 * @param <C> The collision type used.
 */
public abstract class TileGame<C extends Enum<C>>
{
    /** Tile width. */
    private final int width;
    /** Tile height. */
    private final int height;
    /** Tilesheet number where this tile is contained. */
    private Integer pattern;
    /** Location number in the tilesheet. */
    private int number;
    /** Tile x on map. */
    private int x;
    /** Tile y on map. */
    private int y;
    /** Tile collision name. */
    private C collision;

    /**
     * Create a new blank tile.
     * 
     * @param width The tile width.
     * @param height The tile height.
     */
    public TileGame(int width, int height)
    {
        this.width = width;
        this.height = height;
        pattern = null;
        number = 0;
        x = 0;
        y = 0;
        collision = null;
    }

    /**
     * Get collision type from its name as string. The parameter value is read from the file describing the map
     * collisions. The best way to store map collisions name is to use an enum with the same names.
     * 
     * @param collision The collision name.
     * @return The collision type.
     */
    public abstract C getCollisionFrom(String collision);

    /**
     * Check if tile fill condition.
     * 
     * @param collisions The collisions list.
     * @return <code>true</code> if collision is allowed, <code>false</code> else.
     */
    public boolean collisionTest(List<C> collisions)
    {
        if (collisions.isEmpty())
        {
            return true;
        }
        for (final C collision : collisions)
        {
            if (collision == getCollision())
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Get the left position of the tile.
     * 
     * @return The left position of the tile.
     */
    public int getLeft()
    {
        return getX();
    }

    /**
     * Get the right position of the tile.
     * 
     * @return The right position of the tile.
     */
    public int getRight()
    {
        return getX() + getWidth() - 1;
    }

    /**
     * Get the top position of the tile.
     * 
     * @return The top position of the tile.
     */
    public int getTop()
    {
        return getY() + getHeight() - 1;
    }

    /**
     * Get the bottom position of the tile.
     * 
     * @return The bottom position of the tile.
     */
    public int getBottom()
    {
        return getY();
    }

    /**
     * Set pattern number.
     * 
     * @param pattern The pattern number.
     */
    public void setPattern(Integer pattern)
    {
        this.pattern = pattern;
    }

    /**
     * Set tile index inside pattern.
     * 
     * @param number The tile index.
     */
    public void setNumber(int number)
    {
        this.number = number;
    }

    /**
     * Set collision name.
     * 
     * @param collision The collision name.
     */
    public void setCollision(C collision)
    {
        this.collision = collision;
    }

    /**
     * Get the width.
     * 
     * @return The tile width.
     */
    public int getWidth()
    {
        return width;
    }

    /**
     * Get the height.
     * 
     * @return The tile height.
     */
    public int getHeight()
    {
        return height;
    }

    /**
     * Get pattern number.
     * 
     * @return The pattern number.
     */
    public Integer getPattern()
    {
        return pattern;
    }

    /**
     * Get tile index number.
     * 
     * @return The tile index number.
     */
    public int getNumber()
    {
        return number;
    }

    /**
     * Get tile location x.
     * 
     * @return The tile location x.
     */
    public int getX()
    {
        return x;
    }

    /**
     * Get tile location y.
     * 
     * @return The tile location y.
     */
    public int getY()
    {
        return y;
    }

    /**
     * Get tile collision name.
     * 
     * @return The tile collision name.
     */
    public C getCollision()
    {
        return collision;
    }

    /**
     * Set tile location x. Should be used only when overriding the
     * {@link MapTileGame#loadTile(com.b3dgs.lionengine.file.FileReading, int)} function.
     * 
     * @param x The tile location x.
     */
    public void setX(int x)
    {
        this.x = x;
    }

    /**
     * Set tile location y. Should be used only when overriding the
     * {@link MapTileGame#loadTile(com.b3dgs.lionengine.file.FileReading, int)} function.
     * 
     * @param y The tile location y.
     */
    public void setY(int y)
    {
        this.y = y;
    }
}
