package com.b3dgs.lionengine.example.c_platform.e_lionheart.map;

import java.util.ArrayList;
import java.util.List;

/**
 * List of collision types.
 */
public enum TypeTileCollision
{
    /** None. */
    NONE(TypeTileCollisionGroup.NONE),
    /** Ground. */
    GROUND(TypeTileCollisionGroup.FLAT),
    /** Ground with spike. */
    GROUND_SPIKE(TypeTileCollisionGroup.FLAT),
    /** Ground border right. */
    BORDER_RIGHT(TypeTileCollisionGroup.FLAT, true),
    /** Ground border left. */
    BORDER_LEFT(TypeTileCollisionGroup.FLAT, true),
    /** Ground border center. */
    BORDER_CENTER(TypeTileCollisionGroup.FLAT, true),
    /** Slope right top \. */
    SLOPE_RIGHT_1(TypeTileCollisionGroup.SLOPE),
    /** Slope right middle \. */
    SLOPE_RIGHT_2(TypeTileCollisionGroup.SLOPE),
    /** Slope right bottom \. */
    SLOPE_RIGHT_3(TypeTileCollisionGroup.SLOPE),
    /** Slope right border down \. */
    SLOPE_RIGHT_BORDER_DOWN(TypeTileCollisionGroup.SLOPE, true),
    /** Slope right border up \. */
    SLOPE_RIGHT_BORDER_UP(TypeTileCollisionGroup.SLOPE, true),
    /** Slope left top /. */
    SLOPE_LEFT_1(TypeTileCollisionGroup.SLOPE),
    /** Slope left middle /. */
    SLOPE_LEFT_2(TypeTileCollisionGroup.SLOPE),
    /** Slope left bottom /. */
    SLOPE_LEFT_3(TypeTileCollisionGroup.SLOPE),
    /** Slope left border down /. */
    SLOPE_LEFT_BORDER_DOWN(TypeTileCollisionGroup.SLOPE, true),
    /** Slope left border up /. */
    SLOPE_LEFT_BORDER_UP(TypeTileCollisionGroup.SLOPE, true),
    /** Slide right top. */
    SLIDE_RIGHT_1(TypeTileCollisionGroup.SLIDE),
    /** Slide right middle. */
    SLIDE_RIGHT_2(TypeTileCollisionGroup.SLIDE),
    /** Slide right bottom. */
    SLIDE_RIGHT_3(TypeTileCollisionGroup.SLIDE),
    /** Slide right ground. */
    SLIDE_RIGHT_GROUND(TypeTileCollisionGroup.FLAT),
    /** Slide left top. */
    SLIDE_LEFT_1(TypeTileCollisionGroup.SLIDE),
    /** Slide middle top. */
    SLIDE_LEFT_2(TypeTileCollisionGroup.SLIDE),
    /** Slide bottom top. */
    SLIDE_LEFT_3(TypeTileCollisionGroup.SLIDE),
    /** Slide left ground. */
    SLIDE_LEFT_GROUND(TypeTileCollisionGroup.FLAT),
    /** Liana horizontal. */
    LIANA_HORIZONTAL(TypeTileCollisionGroup.LIANA_HORIZONTAL),
    /** Liana steep right top. */
    LIANA_STEEP_RIGHT_1(TypeTileCollisionGroup.LIANA_STEEP),
    /** Liana steep right bottom. */
    LIANA_STEEP_RIGHT_2(TypeTileCollisionGroup.LIANA_STEEP),
    /** Liana steep left top. */
    LIANA_STEEP_LEFT_1(TypeTileCollisionGroup.LIANA_STEEP),
    /** Liana steep left bottom. */
    LIANA_STEEP_LEFT_2(TypeTileCollisionGroup.LIANA_STEEP);

    /** Vertical collisions list. */
    public static final List<TypeTileCollision> COLLISION_VERTICAL = new ArrayList<>(2);
    /** Horizontal collisions list. */
    public static final List<TypeTileCollision> COLLISION_HORIZONTAL = new ArrayList<>(3);
    /** Vertical collisions list. */
    public static final List<TypeTileCollision> COLLISION_LIANA = new ArrayList<>(2);

    /**
     * Static init.
     */
    static
    {
        TypeTileCollision.COLLISION_VERTICAL.add(TypeTileCollision.GROUND);
        TypeTileCollision.COLLISION_VERTICAL.add(TypeTileCollision.BORDER_LEFT);
        TypeTileCollision.COLLISION_VERTICAL.add(TypeTileCollision.BORDER_CENTER);
        TypeTileCollision.COLLISION_VERTICAL.add(TypeTileCollision.BORDER_RIGHT);
        TypeTileCollision.COLLISION_VERTICAL.add(TypeTileCollision.SLOPE_LEFT_1);
        TypeTileCollision.COLLISION_VERTICAL.add(TypeTileCollision.SLOPE_LEFT_2);
        TypeTileCollision.COLLISION_VERTICAL.add(TypeTileCollision.SLOPE_LEFT_3);
        TypeTileCollision.COLLISION_VERTICAL.add(TypeTileCollision.SLOPE_LEFT_BORDER_DOWN);
        TypeTileCollision.COLLISION_VERTICAL.add(TypeTileCollision.SLOPE_LEFT_BORDER_UP);
        TypeTileCollision.COLLISION_VERTICAL.add(TypeTileCollision.SLOPE_RIGHT_1);
        TypeTileCollision.COLLISION_VERTICAL.add(TypeTileCollision.SLOPE_RIGHT_2);
        TypeTileCollision.COLLISION_VERTICAL.add(TypeTileCollision.SLOPE_RIGHT_3);
        TypeTileCollision.COLLISION_VERTICAL.add(TypeTileCollision.SLOPE_RIGHT_BORDER_DOWN);
        TypeTileCollision.COLLISION_VERTICAL.add(TypeTileCollision.SLOPE_RIGHT_BORDER_UP);
        
        TypeTileCollision.COLLISION_LIANA.add(TypeTileCollision.LIANA_HORIZONTAL);
        TypeTileCollision.COLLISION_LIANA.add(TypeTileCollision.LIANA_STEEP_RIGHT_1);
        TypeTileCollision.COLLISION_LIANA.add(TypeTileCollision.LIANA_STEEP_RIGHT_2);
        TypeTileCollision.COLLISION_LIANA.add(TypeTileCollision.LIANA_STEEP_LEFT_1);
        TypeTileCollision.COLLISION_LIANA.add(TypeTileCollision.LIANA_STEEP_LEFT_2);
    }

    /** Group. */
    private final TypeTileCollisionGroup group;
    /** Border state. */
    private final boolean border;

    /**
     * Constructor.
     * 
     * @param group The collision group.
     */
    private TypeTileCollision(TypeTileCollisionGroup group)
    {
        this(group, false);
    }

    /**
     * Constructor.
     * 
     * @param group The collision group.
     * @param border <code>true</code> if border, <code>false</code> else.
     */
    private TypeTileCollision(TypeTileCollisionGroup group, boolean border)
    {
        this.group = group;
        this.border = border;
    }

    /**
     * Get the tile collision group.
     * 
     * @return The tile collision group.
     */
    public TypeTileCollisionGroup getGroup()
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
