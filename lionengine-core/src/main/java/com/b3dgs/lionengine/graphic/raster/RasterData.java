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
package com.b3dgs.lionengine.graphic.raster;

import com.b3dgs.lionengine.Xml;

/**
 * Represents the raster data for color modification.
 * <p>
 * This class is Thread-Safe.
 * </p>
 */
public final class RasterData
{
    /** Attribute raster force. */
    private static final String ATT_FORCE = "force";
    /** Attribute raster amplitude. */
    private static final String ATT_AMPLITUDE = "amplitude";
    /** Attribute raster offset. */
    private static final String ATT_OFFSET = "offset";
    /** Attribute raster type. */
    private static final String ATT_TYPE = "type";

    /**
     * Load raster data from node.
     * 
     * @param root The raster node.
     * @param color The raster color name.
     * @return The raster data.
     */
    public static RasterData load(Xml root, String color)
    {
        final Xml node = root.getChild(color);
        final double force = node.readDouble(ATT_FORCE);
        final int amplitude = node.readInteger(ATT_AMPLITUDE);
        final int offset = node.readInteger(ATT_OFFSET);
        final int type = node.readInteger(ATT_TYPE);

        return new RasterData(force, amplitude, offset, type);
    }

    /** Applied force. */
    private final double force;
    /** Maximum amplitude. */
    private final int amplitude;
    /** Starting color offset. */
    private final int offset;
    /** Raster type. */
    private final int type;

    /**
     * Create the raster data.
     * 
     * @param force The modified color force.
     * @param amplitude The amplitude color modification.
     * @param offset The offset from start.
     * @param type The raster type.
     */
    RasterData(double force, int amplitude, int offset, int type)
    {
        super();

        this.force = force;
        this.amplitude = amplitude;
        this.offset = offset;
        this.type = type;
    }

    /**
     * Get the color force.
     * 
     * @return The color force applied.
     */
    public double getForce()
    {
        return force;
    }

    /**
     * Get the maximum amplitude.
     * 
     * @return The amplitude.
     */
    public int getAmplitude()
    {
        return amplitude;
    }

    /**
     * Get the starting color offset.
     * 
     * @return The The offset.
     */
    public int getOffset()
    {
        return offset;
    }

    /**
     * Get the raster type.
     * 
     * @return The raster type.
     */
    public int getType()
    {
        return type;
    }
}
