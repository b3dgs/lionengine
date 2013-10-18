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
package com.b3dgs.lionengine.example.warcraft.map;

import com.b3dgs.lionengine.example.warcraft.ResourceType;
import com.b3dgs.lionengine.game.rts.map.Border20;
import com.b3dgs.lionengine.game.rts.map.TileRts;

/**
 * Tile implementation.
 */
public final class Tile
        extends TileRts<TileCollision, ResourceType>
{
    /** Border values. */
    private static final Border20[] VALUES = Border20.values();
    /** The collision type color. */
    private TileColorType color;
    /** Current tile id (tree). */
    private Border20 tree;
    /** Tile offset number depending of the theme. */
    private int offset;

    /**
     * Constructor.
     * 
     * @param width The tile width.
     * @param height The tile height.
     * @param pattern The tile pattern.
     * @param number The tile number.
     * @param collision The tile collision.
     * @param theme The theme id.
     */
    public Tile(int width, int height, Integer pattern, int number, TileCollision collision, int theme)
    {
        super(width, height, pattern, number, collision);
        tree = Border20.NONE;
        offset = 0;
        if (1 == theme)
        {
            offset = 19;
        }
    }

    /**
     * Get the tile color representation on minimap.
     * 
     * @return The color on minimap.
     */
    public TileColorType getColor()
    {
        return color;
    }

    /**
     * Set tree id.
     * 
     * @param id The tree id.
     */
    public void setNumber(Border20 id)
    {
        tree = id;
        super.setNumber(id.ordinal() + 125 + offset);
    }

    /**
     * Get the tree id.
     * 
     * @return The tree id.
     */
    public Border20 getId()
    {
        return tree;
    }

    /*
     * TileRts
     */

    @Override
    public void checkResourceType(TileCollision collision)
    {
        if (collision != null)
        {
            switch (collision)
            {
                case TREE:
                    setResourceType(ResourceType.WOOD);
                    break;
                default:
                    setResourceType(ResourceType.NONE);
                    break;
            }
        }
    }

    @Override
    public boolean checkBlocking(TileCollision collision)
    {
        if (collision != null)
        {
            if (TileCollision.TREE == collision)
            {
                if (getNumber() >= 125 + offset && getNumber() <= 144 + offset)
                {
                    tree = Tile.VALUES[getNumber() - (125 + offset)];
                }
            }
            color = TileColorType.valueOf(collision.name());
            return TileCollisionGroup.GROUND != collision.getGroup();
        }
        return false;
    }

    @Override
    public void setNumber(int number)
    {
        super.setNumber(number);
        if (number >= 125 + offset && number <= 144 + offset)
        {
            tree = Tile.VALUES[number - (125 + offset)];
        }
        else
        {
            tree = Border20.NONE;
        }
    }

    @Override
    public boolean hasResources()
    {
        return getResourceType() != ResourceType.NONE;
    }
}
