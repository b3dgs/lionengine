/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.map;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.game.Orientation;
import com.b3dgs.lionengine.game.tile.ConfigTileConstraints;
import com.b3dgs.lionengine.game.tile.Tile;
import com.b3dgs.lionengine.game.tile.TileConstraint;
import com.b3dgs.lionengine.game.tile.TileRef;
import com.b3dgs.lionengine.stream.Xml;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Find all tiles constraints and extract them to an XML file.
 * It will check from an existing map all adjacent tiles combination.
 */
public class ConstraintsExtractor
{
    /** Map reference. */
    private final MapTile map;
    /** Constraints found. */
    private final Map<TileRef, Collection<TileConstraint>> constraints;

    /**
     * Create the extractor.
     * 
     * @param map The map reference.
     */
    public ConstraintsExtractor(MapTile map)
    {
        this.map = map;
        constraints = new HashMap<TileRef, Collection<TileConstraint>>();
    }

    /**
     * Check map tile constraints.
     */
    public void check()
    {
        for (int ty = 0; ty < map.getInTileHeight(); ty++)
        {
            for (int tx = 0; tx < map.getInTileWidth(); tx++)
            {
                final Tile tile = map.getTile(tx, ty);
                if (tile != null)
                {
                    checkNeighbor(tile, tx, ty);
                }
            }
        }
    }

    /**
     * Export constraints found.
     * 
     * @param media The export media.
     */
    public void export(Media media)
    {
        final XmlNode root = ConfigTileConstraints.export(constraints);
        Xml.save(root, media);
    }

    /**
     * Check the tile neighbors.
     * 
     * @param tile The current tile.
     * @param tx The current horizontal tile index.
     * @param ty The current vertical tile index.
     */
    private void checkNeighbor(Tile tile, int tx, int ty)
    {
        final TileRef tileRef = new TileRef(tile.getSheet(), tile.getNumber());
        for (int h = tx - 1; h <= tx + 1; h++)
        {
            for (int v = ty - 1; v <= ty + 1; v++)
            {
                final Orientation orientation = Orientation.get(tx, ty, h, v);
                checkOrientation(orientation, tileRef, h, v);
            }
        }
    }

    /**
     * Check the orientation.
     * 
     * @param orientation The current orientation.
     * @param tileRef The current tile.
     * @param tx The current horizontal tile index.
     * @param ty The current vertical tile index.
     */
    private void checkOrientation(Orientation orientation, TileRef tileRef, int tx, int ty)
    {
        if (orientation != null)
        {
            final Tile neighbor = map.getTile(tx, ty);
            if (neighbor != null)
            {
                final TileRef neighborRef = new TileRef(neighbor.getSheet(), neighbor.getNumber());
                final TileConstraint constraint = getConstraint(tileRef, orientation);
                constraint.add(neighborRef);
            }
        }
    }

    /**
     * Get the constraint for the specified tile. Create an empty one if key does not already exists.
     * 
     * @param tile The tile reference.
     * @param orientation The constraint orientation.
     * @return The associated constraint.
     */
    private TileConstraint getConstraint(TileRef tile, Orientation orientation)
    {
        if (!constraints.containsKey(tile))
        {
            constraints.put(tile, new ArrayList<TileConstraint>());
        }

        final Collection<TileConstraint> orientationConstraints = constraints.get(tile);
        for (final TileConstraint constraint : orientationConstraints)
        {
            if (orientation.equals(constraint.getOrientation()))
            {
                return constraint;
            }
        }

        final TileConstraint constraint = new TileConstraint(orientation);
        orientationConstraints.add(constraint);

        return constraint;
    }
}
