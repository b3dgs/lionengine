/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.editor.collision.map.imports;

import org.eclipse.core.expressions.PropertyTester;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.editor.world.WorldModel;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionFormulaConfig;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionGroupConfig;
import com.b3dgs.lionengine.game.feature.tile.map.collision.MapTileCollision;

/**
 * Test if the map collision has been defined.
 */
public final class MapCollisionTester extends PropertyTester
{
    /** Test if map collisions are defined. */
    private static final String PROPERTY_COLLISION = "collision";
    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(MapCollisionTester.class);

    /**
     * Check if media is a formulas configuration file.
     * 
     * @param media The media to check.
     * @return <code>true</code> if describes formulas, <code>false</code> else.
     */
    public static boolean isFormulasConfig(Media media)
    {
        try
        {
            final Xml node = new Xml(media);
            return CollisionFormulaConfig.NODE_FORMULAS.equals(node.getNodeName());
        }
        catch (@SuppressWarnings("unused") final LionEngineException exception)
        {
            return false;
        }
    }

    /**
     * Check if media is a collisions configuration file.
     * 
     * @param media The media to check.
     * @return <code>true</code> if describes collisions, <code>false</code> else.
     */
    public static boolean isCollisionsConfig(Media media)
    {
        try
        {
            final Xml node = new Xml(media);
            return CollisionGroupConfig.NODE_COLLISIONS.equals(node.getNodeName());
        }
        catch (final LionEngineException exception)
        {
            LOGGER.error("isCollisionsConfig error", exception);
            return false;
        }
    }

    /**
     * Create tester.
     */
    public MapCollisionTester()
    {
        super();
    }

    /*
     * PropertyTester
     */

    @Override
    public boolean test(Object receiver, String property, Object[] args, Object expectedValue)
    {
        final MapTile map = WorldModel.INSTANCE.getMap();
        final boolean result;
        if (PROPERTY_COLLISION.equals(property))
        {
            result = map.isCreated() && map.hasFeature(MapTileCollision.class);
        }
        else
        {
            result = false;
        }
        return result;
    }
}
