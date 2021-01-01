/*
 * Copyright (C) 2013-2021 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import com.b3dgs.lionengine.game.Configurer;

/**
 * Represents the {@link Attacker} data.
 * <p>
 * This class is Thread-Safe.
 * </p>
 */
public final class AttackerConfig
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
    /** Minimum to string length. */
    private static final int MIN_LENGTH = 73;

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
    public static AttackerConfig imports(Xml root)
    {
        Check.notNull(root);

        final Xml node = root.getChild(NODE_ATTACKER);
        final int delay = node.readInteger(0, ATT_DELAY);
        final int distanceMin = node.readInteger(0, ATT_DISTANCE_MIN);
        final int distanceMax = node.readInteger(0, ATT_DISTANCE_MAX);
        final int damagesMin = node.readInteger(0, ATT_DAMAGES_MIN);
        final int damagesMax = node.readInteger(0, ATT_DAMAGES_MAX);

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

    /** The delay between attacks in tick. */
    private final int delay;
    /** The minimum distance to attack in tile. */
    private final int distanceMin;
    /** The maximum distance to attack in tile. */
    private final int distanceMax;
    /** The minimum attack damages. */
    private final int damagesMin;
    /** The maximum attack damages. */
    private final int damagesMax;

    /**
     * Create the configuration.
     * 
     * @param delay The delay between attacks.
     * @param distanceMin The minimum attack distance in tile.
     * @param distanceMax The maximum attack distance in tile.
     * @param damagesMin The minimum attack damages.
     * @param damagesMax The maximum attack damages.
     */
    public AttackerConfig(int delay, int distanceMin, int distanceMax, int damagesMin, int damagesMax)
    {
        super();

        this.delay = delay;
        this.distanceMin = distanceMin;
        this.distanceMax = distanceMax;
        this.damagesMin = damagesMin;
        this.damagesMax = damagesMax;
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

    /*
     * Object
     */

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + delay;
        result = prime * result + distanceMin;
        result = prime * result + distanceMax;
        result = prime * result + damagesMin;
        result = prime * result + damagesMax;
        return result;
    }

    @Override
    public boolean equals(Object object)
    {
        if (this == object)
        {
            return true;
        }
        if (object == null || object.getClass() != getClass())
        {
            return false;
        }
        final AttackerConfig other = (AttackerConfig) object;
        return delay == other.delay
               && distanceMin == other.distanceMin
               && distanceMax == other.distanceMax
               && damagesMin == other.damagesMin
               && damagesMax == other.damagesMax;
    }

    @Override
    public String toString()
    {
        return new StringBuilder(MIN_LENGTH).append(getClass().getSimpleName())
                                            .append(" [delay=")
                                            .append(delay)
                                            .append(", distanceMin=")
                                            .append(getDistance().getMin())
                                            .append(", distanceMax=")
                                            .append(getDistance().getMax())
                                            .append(", damagesMin=")
                                            .append(getDamages().getMin())
                                            .append(", damagesMax=")
                                            .append(getDamages().getMax())
                                            .append("]")
                                            .toString();
    }
}
