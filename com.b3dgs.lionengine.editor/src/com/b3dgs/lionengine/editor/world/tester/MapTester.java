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
package com.b3dgs.lionengine.editor.world.tester;

import org.eclipse.core.expressions.PropertyTester;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.editor.world.WorldModel;
import com.b3dgs.lionengine.game.collision.tile.CollisionFormulaConfig;
import com.b3dgs.lionengine.game.collision.tile.CollisionGroupConfig;
import com.b3dgs.lionengine.game.collision.tile.MapTileCollision;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.stream.Xml;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Test if the map has been defined.
 */
public final class MapTester extends PropertyTester
{
    /** Test if map defined. */
    private static final String PROPERTY_TEST = "test";
    /** Test if map collisions are defined. */
    private static final String PROPERTY_COLLISION = "collision";

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
            final XmlNode node = Xml.load(media);
            return CollisionFormulaConfig.FORMULAS.equals(node.getNodeName());
        }
        catch (final LionEngineException exception)
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
            final XmlNode node = Xml.load(media);
            return CollisionGroupConfig.COLLISIONS.equals(node.getNodeName());
        }
        catch (final LionEngineException exception)
        {
            return false;
        }
    }

    /**
     * Create tester.
     */
    public MapTester()
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
        if (PROPERTY_TEST.equals(property))
        {
            result = map.isCreated();
        }
        else if (PROPERTY_COLLISION.equals(property))
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
