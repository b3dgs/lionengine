/*
 * Copyright (C) 2013 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
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
    BORDER_RIGHT(TileCollisionGroup.FLAT, true, true),
    /** Ground border left. */
    BORDER_LEFT(TileCollisionGroup.FLAT, false, true),
    /** Ground border center. */
    BORDER_CENTER(TileCollisionGroup.FLAT, false, true),
    /** Slope right top \. */
    SLOPE_RIGHT_1(TileCollisionGroup.SLOPE, false),
    /** Slope right middle \. */
    SLOPE_RIGHT_2(TileCollisionGroup.SLOPE, false),
    /** Slope right bottom \. */
    SLOPE_RIGHT_3(TileCollisionGroup.SLOPE, false),
    /** Slope right border down \. */
    SLOPE_RIGHT_BORDER_DOWN(TileCollisionGroup.SLOPE, false, true),
    /** Slope right border up \. */
    SLOPE_RIGHT_BORDER_UP(TileCollisionGroup.SLOPE, false, true),
    /** Slope left top /. */
    SLOPE_LEFT_1(TileCollisionGroup.SLOPE, true),
    /** Slope left middle /. */
    SLOPE_LEFT_2(TileCollisionGroup.SLOPE, true),
    /** Slope left bottom /. */
    SLOPE_LEFT_3(TileCollisionGroup.SLOPE, true),
    /** Slope left border down /. */
    SLOPE_LEFT_BORDER_DOWN(TileCollisionGroup.SLOPE, true, true),
    /** Slope left border up /. */
    SLOPE_LEFT_BORDER_UP(TileCollisionGroup.SLOPE, true, true),
    /** Slide right top. */
    SLIDE_RIGHT_1(TileCollisionGroup.SLIDE, false),
    /** Slide right middle. */
    SLIDE_RIGHT_2(TileCollisionGroup.SLIDE, false),
    /** Slide right bottom. */
    SLIDE_RIGHT_3(TileCollisionGroup.SLIDE, false),
    /** Slide right ground slide. */
    SLIDE_RIGHT_GROUND_SLIDE(TileCollisionGroup.SLIDE, false),
    /** Slide left top. */
    SLIDE_LEFT_1(TileCollisionGroup.SLIDE, true),
    /** Slide middle top. */
    SLIDE_LEFT_2(TileCollisionGroup.SLIDE, true),
    /** Slide bottom top. */
    SLIDE_LEFT_3(TileCollisionGroup.SLIDE, true),
    /** Slide left ground slide. */
    SLIDE_LEFT_GROUND_SLIDE(TileCollisionGroup.SLIDE, true),
    /** Liana horizontal. */
    LIANA_HORIZONTAL(TileCollisionGroup.LIANA_HORIZONTAL),
    /** Liana steep right top. */
    LIANA_STEEP_RIGHT_1(TileCollisionGroup.LIANA_STEEP, false),
    /** Liana steep right bottom. */
    LIANA_STEEP_RIGHT_2(TileCollisionGroup.LIANA_STEEP, false),
    /** Liana steep left top. */
    LIANA_STEEP_LEFT_1(TileCollisionGroup.LIANA_STEEP, true),
    /** Liana steep left bottom. */
    LIANA_STEEP_LEFT_2(TileCollisionGroup.LIANA_STEEP, true),
    /** Liana leaning right top. */
    LIANA_LEANING_RIGHT_1(TileCollisionGroup.LIANA_LEANING, false),
    /** Liana leaning right top. */
    LIANA_LEANING_RIGHT_2(TileCollisionGroup.LIANA_LEANING, false),
    /** Liana leaning right top. */
    LIANA_LEANING_RIGHT_3(TileCollisionGroup.LIANA_LEANING, false),
    /** Liana leaning left top. */
    LIANA_LEANING_LEFT_1(TileCollisionGroup.LIANA_LEANING, true),
    /** Liana leaning left top. */
    LIANA_LEANING_LEFT_2(TileCollisionGroup.LIANA_LEANING, true),
    /** Liana leaning left top. */
    LIANA_LEANING_LEFT_3(TileCollisionGroup.LIANA_LEANING, true);

    /** Vertical collisions list. */
    public static final List<TileCollision> COLLISION_VERTICAL = new ArrayList<>(2);
    /** Horizontal collisions list. */
    public static final List<TileCollision> COLLISION_HORIZONTAL = new ArrayList<>(3);
    /** Vertical collisions list. */
    public static final List<TileCollision> COLLISION_LIANA_STEEP = new ArrayList<>(2);
    /** Vertical collisions list. */
    public static final List<TileCollision> COLLISION_LIANA_LEANING = new ArrayList<>(2);

    /**
     * Static init.
     */
    static
    {
        TileCollision.COLLISION_VERTICAL.add(TileCollision.GROUND);
        TileCollision.COLLISION_VERTICAL.add(TileCollision.GROUND_SPIKE);
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

        TileCollision.COLLISION_VERTICAL.add(TileCollision.SLIDE_LEFT_1);
        TileCollision.COLLISION_VERTICAL.add(TileCollision.SLIDE_LEFT_2);
        TileCollision.COLLISION_VERTICAL.add(TileCollision.SLIDE_LEFT_3);
        TileCollision.COLLISION_VERTICAL.add(TileCollision.SLIDE_LEFT_GROUND_SLIDE);
        TileCollision.COLLISION_VERTICAL.add(TileCollision.SLIDE_RIGHT_1);
        TileCollision.COLLISION_VERTICAL.add(TileCollision.SLIDE_RIGHT_2);
        TileCollision.COLLISION_VERTICAL.add(TileCollision.SLIDE_RIGHT_3);
        TileCollision.COLLISION_VERTICAL.add(TileCollision.SLIDE_RIGHT_GROUND_SLIDE);

        TileCollision.COLLISION_LIANA_STEEP.add(TileCollision.LIANA_HORIZONTAL);
        TileCollision.COLLISION_LIANA_STEEP.add(TileCollision.LIANA_STEEP_RIGHT_1);
        TileCollision.COLLISION_LIANA_STEEP.add(TileCollision.LIANA_STEEP_RIGHT_2);
        TileCollision.COLLISION_LIANA_STEEP.add(TileCollision.LIANA_STEEP_LEFT_1);
        TileCollision.COLLISION_LIANA_STEEP.add(TileCollision.LIANA_STEEP_LEFT_2);

        TileCollision.COLLISION_LIANA_LEANING.add(TileCollision.LIANA_LEANING_LEFT_1);
        TileCollision.COLLISION_LIANA_LEANING.add(TileCollision.LIANA_LEANING_LEFT_2);
        TileCollision.COLLISION_LIANA_LEANING.add(TileCollision.LIANA_LEANING_LEFT_3);
        TileCollision.COLLISION_LIANA_LEANING.add(TileCollision.LIANA_LEANING_RIGHT_1);
        TileCollision.COLLISION_LIANA_LEANING.add(TileCollision.LIANA_LEANING_RIGHT_2);
        TileCollision.COLLISION_LIANA_LEANING.add(TileCollision.LIANA_LEANING_RIGHT_3);

        TileCollision.COLLISION_HORIZONTAL.add(TileCollision.GROUND_SPIKE);
    }

    /** Group. */
    private final TileCollisionGroup group;
    /** Left flag. */
    private final boolean left;
    /** Border state. */
    private final boolean border;

    /**
     * Constructor.
     * 
     * @param group The collision group.
     */
    private TileCollision(TileCollisionGroup group)
    {
        this(group, false, false);
    }

    /**
     * Constructor.
     * 
     * @param group The collision group.
     * @param left The side (true = left, false = right).
     */
    private TileCollision(TileCollisionGroup group, boolean left)
    {
        this(group, left, false);
    }

    /**
     * Constructor.
     * 
     * @param group The collision group.
     * @param left The side (true = left, false = right).
     * @param border <code>true</code> if border, <code>false</code> else.
     */
    private TileCollision(TileCollisionGroup group, boolean left, boolean border)
    {
        this.group = group;
        this.left = left;
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
     * Get the left right flag (not used for flat collision group).
     * 
     * @return <code>true</code> if left, <code>false</code> if right.
     */
    public boolean isLeft()
    {
        return left;
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
