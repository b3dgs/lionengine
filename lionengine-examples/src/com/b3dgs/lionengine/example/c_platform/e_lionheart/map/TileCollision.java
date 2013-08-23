package com.b3dgs.lionengine.example.c_platform.e_lionheart.map;

import java.util.ArrayList;
import java.util.List;

/**
 * List of collision types.
 */
public enum TileCollision
{
    /** None. */
    NONE(TileCollisionGroup.NONE),
    /** Ground. */
    GROUND(TileCollisionGroup.FLAT),
    /** Ground with spike. */
    GROUND_SPIKE(TileCollisionGroup.FLAT),
    /** Ground border right. */
    BORDER_RIGHT(TileCollisionGroup.FLAT, true),
    /** Ground border left. */
    BORDER_LEFT(TileCollisionGroup.FLAT, true),
    /** Ground border center. */
    BORDER_CENTER(TileCollisionGroup.FLAT, true),
    /** Slope right top \. */
    SLOPE_RIGHT_1(TileCollisionGroup.SLOPE),
    /** Slope right middle \. */
    SLOPE_RIGHT_2(TileCollisionGroup.SLOPE),
    /** Slope right bottom \. */
    SLOPE_RIGHT_3(TileCollisionGroup.SLOPE),
    /** Slope right border down \. */
    SLOPE_RIGHT_BORDER_DOWN(TileCollisionGroup.SLOPE, true),
    /** Slope right border up \. */
    SLOPE_RIGHT_BORDER_UP(TileCollisionGroup.SLOPE, true),
    /** Slope left top /. */
    SLOPE_LEFT_1(TileCollisionGroup.SLOPE),
    /** Slope left middle /. */
    SLOPE_LEFT_2(TileCollisionGroup.SLOPE),
    /** Slope left bottom /. */
    SLOPE_LEFT_3(TileCollisionGroup.SLOPE),
    /** Slope left border down /. */
    SLOPE_LEFT_BORDER_DOWN(TileCollisionGroup.SLOPE, true),
    /** Slope left border up /. */
    SLOPE_LEFT_BORDER_UP(TileCollisionGroup.SLOPE, true),
    /** Slide right top. */
    SLIDE_RIGHT_1(TileCollisionGroup.SLIDE),
    /** Slide right middle. */
    SLIDE_RIGHT_2(TileCollisionGroup.SLIDE),
    /** Slide right bottom. */
    SLIDE_RIGHT_3(TileCollisionGroup.SLIDE),
    /** Slide right ground. */
    SLIDE_RIGHT_GROUND(TileCollisionGroup.FLAT),
    /** Slide left top. */
    SLIDE_LEFT_1(TileCollisionGroup.SLIDE),
    /** Slide middle top. */
    SLIDE_LEFT_2(TileCollisionGroup.SLIDE),
    /** Slide bottom top. */
    SLIDE_LEFT_3(TileCollisionGroup.SLIDE),
    /** Slide left ground. */
    SLIDE_LEFT_GROUND(TileCollisionGroup.FLAT);

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

    /** Group. */
    private final TileCollisionGroup group;
    /** Border state. */
    private final boolean border;

    /**
     * Constructor.
     * 
     * @param group The collision group.
     */
    private TileCollision(TileCollisionGroup group)
    {
        this(group, false);
    }

    /**
     * Constructor.
     * 
     * @param group The collision group.
     * @param border <code>true</code> if border, <code>false</code> else.
     */
    private TileCollision(TileCollisionGroup group, boolean border)
    {
        this.group = group;
        this.border = border;
    }

    /**
     * Get the tile collision group.
     * 
     * @return The tile collision group.
     */
    public TileCollisionGroup getGroup()
    {
        return group;
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
