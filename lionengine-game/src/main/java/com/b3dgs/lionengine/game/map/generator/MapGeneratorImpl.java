/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.map.generator;

import java.util.Collection;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.map.MapTileGame;
import com.b3dgs.lionengine.game.map.MapTileGroup;
import com.b3dgs.lionengine.game.map.MapTileGroupModel;
import com.b3dgs.lionengine.game.map.circuit.MapTileCircuit;
import com.b3dgs.lionengine.game.map.circuit.MapTileCircuitModel;
import com.b3dgs.lionengine.game.map.transition.MapTileTransition;
import com.b3dgs.lionengine.game.map.transition.MapTileTransitionModel;

/**
 * Default map generator implementation.
 */
public class MapGeneratorImpl implements MapGenerator
{
    /**
     * Create map generator.
     */
    public MapGeneratorImpl()
    {
        super();
    }

    /*
     * MapGenerator
     */

    @Override
    public MapTile generateMap(GeneratorParameter parameters,
                               Collection<Media> levels,
                               Media sheetsConfig,
                               Media groupsConfig)
    {
        final MapTile map = new MapTileGame();
        map.loadSheets(sheetsConfig);

        final MapTileGroup mapGroup = map.createFeature(MapTileGroupModel.class);
        mapGroup.loadGroups(groupsConfig);

        final MapTileTransition mapTransition = map.createFeature(MapTileTransitionModel.class);
        mapTransition.loadTransitions(levels, sheetsConfig, groupsConfig);

        final MapTileCircuit mapCircuit = map.createFeature(MapTileCircuitModel.class);
        mapCircuit.loadCircuits(levels, sheetsConfig, groupsConfig);

        parameters.apply(map);

        return map;
    }
}
