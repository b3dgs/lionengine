/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.b3dgs.lionengine.game.feature.tile;

import java.util.Set;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Nameable;

/**
 * Represents the tile group, which can be applied to a {@link Integer}.
 * Here a definition example:
 * 
 * <pre>
 * &lt;lionengine:groups xmlns:lionengine="http://lionengine.b3dgs.com"&gt;
 *    &lt;lionengine:group name="block"&gt; type="PLAIN"&gt;
 *      &lt;lionengine:tile number="1"/&gt;
 *      &lt;lionengine:tile number="5"/&gt;
 *    &lt;/lionengine:group&gt;
 *    &lt;lionengine:group name="top"&gt; type="TRANSITION"&gt;
 *      &lt;lionengine:tile number="2"/&gt;
 *      &lt;lionengine:tile number="3"/&gt;
 *    &lt;/lionengine:group&gt;
 * &lt;/lionengine:groups&gt;
 * </pre>
 * 
 * @param name The group name.
 * @param type The group type.
 * @param tiles The tiles inside the group.
 * 
 * @see com.b3dgs.lionengine.game.feature.tile.TileGroupsConfig
 */
public record TileGroup(String name, TileGroupType type, Set<Integer> tiles) implements Nameable<TileGroup>
{
    /**
     * Create group.
     * 
     * @param name The group name.
     * @param type The group type.
     * @param tiles The tiles inside the group (stores reference).
     * @throws LionEngineException If invalid arguments.
     */
    public TileGroup
    {
        Check.notNull(name);
        Check.notNull(type);
        Check.notNull(tiles);
    }

    /**
     * Check if tile is contained by the group.
     * 
     * @param tile The tile reference.
     * @return <code>true</code> if part of the group, <code>false</code> else.
     */
    public boolean contains(Tile tile)
    {
        return contains(tile.getKey());
    }

    /**
     * Check if tile is contained by the group.
     * 
     * @param number The tile number.
     * @return <code>true</code> if part of the group, <code>false</code> else.
     */
    public boolean contains(Integer number)
    {
        return tiles.contains(number);
    }

    /**
     * Get the group type.
     * 
     * @return The group type.
     */
    public TileGroupType getType()
    {
        return type;
    }

    /**
     * Get the tiles inside group.
     * 
     * @return The tiles inside group (as reference).
     */
    public Set<Integer> getTiles()
    {
        return tiles;
    }

    @Override
    public String getName()
    {
        return name;
    }
}
