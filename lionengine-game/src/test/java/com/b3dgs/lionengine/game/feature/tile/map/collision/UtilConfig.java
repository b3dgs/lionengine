/*
 * Copyright (C) 2013-2019 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.tile.map.collision;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Xml;

/**
 * Configuration utilities.
 */
final class UtilConfig
{
    /**
     * Create formula configuration.
     * 
     * @param formulas The collision formulas.
     * @return The formula configuration.
     */
    public static Media createFormulaConfig(CollisionFormula... formulas)
    {
        final Media media = Medias.create("formulas.xml");
        final Xml root = new Xml("formulas");
        for (final CollisionFormula formula : formulas)
        {
            CollisionFormulaConfig.exports(root, formula);
        }
        root.save(media);
        return media;
    }

    /**
     * Create groups configuration.
     * 
     * @param group The collision group.
     * @return The groups configuration.
     */
    public static Media createGroupsConfig(CollisionGroup group)
    {
        final Media media = Medias.create("groups.xml");
        final Xml root = new Xml("groups");
        CollisionGroupConfig.exports(root, group);
        root.save(media);
        return media;
    }
}
