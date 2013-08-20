package com.b3dgs.lionengine.example.c_platform.e_lionheart.map;

import java.util.ArrayList;
import java.util.List;

/**
 * List of collisions.
 */
public enum TileCollision
{
    /** None. */
    NONE,
    /** Ground. */
    GROUND,
    /** Ground with spike. */
    GROUND_SPIKE,
    /** Ground border right. */
    BORDER_RIGHT(true),
    /** Ground border left. */
    BORDER_LEFT(true),
    /** Ground border center. */
    BORDER_CENTER(true),
    /** Slope right top \. */
    SLOPE_RIGHT_1,
    /** Slope right middle \. */
    SLOPE_RIGHT_2,
    /** Slope right bottom \. */
    SLOPE_RIGHT_3,
    /** Slope right border down \. */
    SLOPE_RIGHT_BORDER_DOWN(true),
    /** Slope right border up \. */
    SLOPE_RIGHT_BORDER_UP(true),
    /** Slope left top /. */
    SLOPE_LEFT_1,
    /** Slope left middle /. */
    SLOPE_LEFT_2,
    /** Slope left bottom /. */
    SLOPE_LEFT_3,
    /** Slope left border down /. */
    SLOPE_LEFT_BORDER_DOWN(true),
    /** Slope left border up /. */
    SLOPE_LEFT_BORDER_UP(true),
    /** Slide right top. */
    SLIDE_RIGHT_1,
    /** Slide right middle. */
    SLIDE_RIGHT_2,
    /** Slide right bottom. */
    SLIDE_RIGHT_3,
    /** Slide right ground. */
    SLIDE_RIGHT_GROUND,
    /** Slide left top. */
    SLIDE_LEFT_1,
    /** Slide middle top. */
    SLIDE_LEFT_2,
    /** Slide bottom top. */
    SLIDE_LEFT_3,
    /** Slide left ground. */
    SLIDE_LEFT_GROUND;

    /** Vertical collisions list. */
    public static final List<TileCollision> COLLISION_VERTICAL = new ArrayList<>(2);
    /** Horizontal collisions list. */
    public static final List<TileCollision> COLLISION_HORIZONTAL = new ArrayList<>(3);

    /**
     * Static init.
     */
    static
    {
        TileCollision.COLLISION_VERTICAL.add(TileCollision.GROUND);
        TileCollision.COLLISION_VERTICAL.add(TileCollision.BORDER_LEFT);
        TileCollision.COLLISION_VERTICAL.add(TileCollision.BORDER_CENTER);
        TileCollision.COLLISION_VERTICAL.add(TileCollision.BORDER_RIGHT);
        TileCollision.COLLISION_VERTICAL.add(TileCollision.SLOPE_LEFT_1);
        TileCollision.COLLISION_VERTICAL.add(TileCollision.SLOPE_LEFT_2);
        TileCollision.COLLISION_VERTICAL.add(TileCollision.SLOPE_LEFT_3);
        TileCollision.COLLISION_VERTICAL.add(TileCollision.SLOPE_LEFT_BORDER_DOWN);
        TileCollision.COLLISION_VERTICAL.add(TileCollision.SLOPE_LEFT_BORDER_UP);
        TileCollision.COLLISION_VERTICAL.add(TileCollision.SLOPE_RIGHT_1);
        TileCollision.COLLISION_VERTICAL.add(TileCollision.SLOPE_RIGHT_2);
        TileCollision.COLLISION_VERTICAL.add(TileCollision.SLOPE_RIGHT_3);
        TileCollision.COLLISION_VERTICAL.add(TileCollision.SLOPE_RIGHT_BORDER_DOWN);
        TileCollision.COLLISION_VERTICAL.add(TileCollision.SLOPE_RIGHT_BORDER_UP);
    }

    /** Border state. */
    private final boolean border;

    /**
     * Constructor.
     */
    private TileCollision()
    {
        this(false);
    }

    /**
     * Constructor.
     * 
     * @param border <code>true</code> if border, <code>false</code> else.
     */
    private TileCollision(boolean border)
    {
        this.border = border;
    }

    /**
     * Check if tile is a border.
     * 
     * @return <code>true</code> if border, <code>false</code> else.
     */
    public boolean isBorder()
    {
        return border;
    }
}
