/*
 * Copyright (C) 2013-2020 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.tile.map.extractable;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.game.Configurer;

/**
 * Represents the {@link Extractor} data.
 */
public final class ExtractorConfig
{
    /** Extractor node name. */
    public static final String NODE_EXTRACTOR = Constant.XML_PREFIX + "extractor";
    /** Extract speed attribute name. */
    public static final String ATT_EXTRACT = "extract";
    /** Drop off speed attribute name. */
    public static final String ATT_DROPOFF = "dropoff";
    /** Capacity attribute name. */
    public static final String ATT_CAPACITY = "capacity";
    /** Minimum to string length. */
    private static final int MIN_LENGTH = 54;

    /**
     * Imports the config from configurer.
     * 
     * @param configurer The configurer reference (must not be <code>null</code>).
     * @return The extractor data.
     * @throws LionEngineException If unable to read node.
     */
    public static ExtractorConfig imports(Configurer configurer)
    {
        Check.notNull(configurer);

        return imports(configurer.getRoot());
    }

    /**
     * Imports the config from node.
     * 
     * @param root The root reference (must not be <code>null</code>).
     * @return The extractor data.
     * @throws LionEngineException If unable to read node.
     */
    public static ExtractorConfig imports(Xml root)
    {
        Check.notNull(root);

        final Xml node = root.getChild(NODE_EXTRACTOR);
        final double extract = node.readDouble(0.0, ATT_EXTRACT);
        final double dropoff = node.readDouble(0.0, ATT_DROPOFF);
        final int capacity = node.readInteger(0, ATT_CAPACITY);

        return new ExtractorConfig(extract, dropoff, capacity);
    }

    /**
     * Exports the node from config.
     * 
     * @param config The config reference (must not be <code>null</code>).
     * @return The extractor node.
     * @throws LionEngineException If unable to read node.
     */
    public static Xml exports(ExtractorConfig config)
    {
        Check.notNull(config);

        final Xml node = new Xml(NODE_EXTRACTOR);
        node.writeDouble(ATT_EXTRACT, config.getExtract());
        node.writeDouble(ATT_DROPOFF, config.getDropOff());
        node.writeInteger(ATT_CAPACITY, config.getCapacity());

        return node;
    }

    /** Extraction unit per tick. */
    private final double extract;
    /** Drop off unit per tick. */
    private final double dropoff;
    /** Extraction capacity. */
    private final int capacity;

    /**
     * Create the configuration.
     * 
     * @param extract The extraction unit per tick.
     * @param dropoff The drop off unit per tick.
     * @param capacity The extraction capacity.
     */
    public ExtractorConfig(double extract, double dropoff, int capacity)
    {
        super();

        this.extract = extract;
        this.dropoff = dropoff;
        this.capacity = capacity;
    }

    /**
     * Get extraction unit per tick.
     * 
     * @return The extraction unit per tick.
     */
    public double getExtract()
    {
        return extract;
    }

    /**
     * Get drop off unit per tick.
     * 
     * @return The drop off unit per tick.
     */
    public double getDropOff()
    {
        return dropoff;
    }

    /**
     * Get extraction capacity.
     * 
     * @return The extraction capacity.
     */
    public int getCapacity()
    {
        return capacity;
    }

    /*
     * Object
     */

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(extract);
        result = prime * result + (int) (temp ^ temp >>> 32);
        temp = Double.doubleToLongBits(dropoff);
        result = prime * result + (int) (temp ^ temp >>> 32);
        result = prime * result + capacity;
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
        final ExtractorConfig other = (ExtractorConfig) object;
        return Double.compare(extract, other.extract) == 0
               && Double.compare(dropoff, other.dropoff) == 0
               && capacity == other.capacity;
    }

    @Override
    public String toString()
    {
        return new StringBuilder(MIN_LENGTH).append(getClass().getSimpleName())
                                            .append(" [extract=")
                                            .append(extract)
                                            .append(", dropoff=")
                                            .append(dropoff)
                                            .append(", capacity=")
                                            .append(capacity)
                                            .append("]")
                                            .toString();
    }
}
