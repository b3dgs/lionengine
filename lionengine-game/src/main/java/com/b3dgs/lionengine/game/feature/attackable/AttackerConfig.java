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
package com.b3dgs.lionengine.game.feature.attackable;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Range;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.XmlReader;
import com.b3dgs.lionengine.game.Configurer;

/**
 * Represents the {@link Attacker} data.
 * <p>
 * This class is Thread-Safe.
 * </p>
 * 
 * @param delay The delay between attacks.
 * @param distanceMin The minimum attack distance in tile.
 * @param distanceMax The maximum attack distance in tile.
 * @param damagesMin The minimum attack damages.
 * @param damagesMax The maximum attack damages.
 */
public record AttackerConfig(int delay, int distanceMin, int distanceMax, int damagesMin, int damagesMax)
{

    /** Attacker node name. */
    public static final String NODE_ATTACKER = Constant.XML_PREFIX + "attacker";
    /** Attack delay attribute name. */
    public static final String ATT_DELAY = "delay";
    /** Attack distance minimum attribute name. */
    public static final String ATT_DISTANCE_MIN = "distanceMin";
    /** Attack distance maximum attribute name. */
    public static final String ATT_DISTANCE_MAX = "distanceMax";
    /** Attack damages minimum attribute name. */
    public static final String ATT_DAMAGES_MIN = "damagesMin";
    /** Attack damages maximum attribute name. */
    public static final String ATT_DAMAGES_MAX = "damagesMax";

    /**
     * Imports the frames config from configurer.
     * 
     * @param configurer The configurer reference (must not be <code>null</code>).
     * @return The frames data.
     * @throws LionEngineException If unable to read node or invalid integer.
     */
    public static AttackerConfig imports(Configurer configurer)
    {
        Check.notNull(configurer);

        return imports(configurer.getRoot());
    }

    /**
     * Imports the config from node.
     * 
     * @param root The root reference (must not be <code>null</code>).
     * @return The attacker data.
     * @throws LionEngineException If unable to read node or invalid integer.
     */
    public static AttackerConfig imports(XmlReader root)
    {
        Check.notNull(root);

        final XmlReader node = root.getChild(NODE_ATTACKER);
        final int delay = node.getInteger(0, ATT_DELAY);
        final int distanceMin = node.getInteger(0, ATT_DISTANCE_MIN);
        final int distanceMax = node.getInteger(0, ATT_DISTANCE_MAX);
        final int damagesMin = node.getInteger(0, ATT_DAMAGES_MIN);
        final int damagesMax = node.getInteger(0, ATT_DAMAGES_MAX);

        return new AttackerConfig(delay, distanceMin, distanceMax, damagesMin, damagesMax);
    }

    /**
     * Exports the node from config.
     * 
     * @param config The config reference (must not be <code>null</code>).
     * @return The attacker node.
     * @throws LionEngineException If unable to read node or invalid integer.
     */
    public static Xml exports(AttackerConfig config)
    {
        Check.notNull(config);

        final Xml node = new Xml(NODE_ATTACKER);
        node.writeInteger(ATT_DELAY, config.getDelay());
        node.writeInteger(ATT_DISTANCE_MIN, config.getDistance().getMin());
        node.writeInteger(ATT_DISTANCE_MAX, config.getDistance().getMax());
        node.writeInteger(ATT_DAMAGES_MIN, config.getDamages().getMin());
        node.writeInteger(ATT_DAMAGES_MAX, config.getDamages().getMax());

        return node;
    }

    /**
     * Get delay between attacks in tick.
     * 
     * @return The delay between attacks in tick.
     */
    public int getDelay()
    {
        return delay;
    }

    /**
     * Get the attack range in tile.
     * 
     * @return The attack range in tile.
     */
    public Range getDistance()
    {
        return new Range(distanceMin, distanceMax);
    }

    /**
     * Get the attack damages.
     * 
     * @return The attack damages.
     */
    public Range getDamages()
    {
        return new Range(damagesMin, damagesMax);
    }
}
