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
package com.b3dgs.lionengine.game.feature.launchable;

import java.util.Optional;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.ForceConfig;

/**
 * Represents the launchable data.
 * <p>
 * This class is Thread-Safe.
 * </p>
 */
public final class LaunchableConfig
{
    /** Launchable node name. */
    public static final String NODE_LAUNCHABLE = Constant.XML_PREFIX + "launchable";
    /** Media attribute. */
    public static final String ATT_MEDIA = "media";
    /** Sfx attribute. */
    public static final String ATT_SFX = "sfx";
    /** Rate attribute. */
    public static final String ATT_DELAY = "delay";
    /** Horizontal offset attribute. */
    public static final String ATT_OFFSET_X = "ox";
    /** Vertical offset attribute. */
    public static final String ATT_OFFSET_Y = "oy";
    /** Minimum to string length. */
    private static final int MIN_LENGTH = 65;

    /**
     * Import the launchable data from node.
     * 
     * @param node The node reference (must not be <code>null</code>).
     * @return The launchable data.
     * @throws LionEngineException If unable to read node.
     */
    public static LaunchableConfig imports(Xml node)
    {
        Check.notNull(node);

        final String media = node.readString(ATT_MEDIA);
        final String sfx = node.readString(null, ATT_SFX);
        final int delay = node.readInteger(0, ATT_DELAY);
        final int ox = node.readInteger(0, ATT_OFFSET_X);
        final int oy = node.readInteger(0, ATT_OFFSET_Y);

        return new LaunchableConfig(media, sfx, delay, ox, oy, ForceConfig.imports(node));
    }

    /**
     * Export the launchable node from data.
     * 
     * @param config The config reference (must not be <code>null</code>).
     * @return The node data.
     * @throws LionEngineException If unable to write node.
     */
    public static Xml exports(LaunchableConfig config)
    {
        Check.notNull(config);

        final Xml node = new Xml(NODE_LAUNCHABLE);
        node.writeString(ATT_MEDIA, config.getMedia());
        config.getSfx().ifPresent(sfx -> node.writeString(ATT_SFX, sfx));
        node.writeInteger(ATT_DELAY, config.getDelay());
        node.writeInteger(ATT_OFFSET_X, config.getOffsetX());
        node.writeInteger(ATT_OFFSET_Y, config.getOffsetY());
        node.add(ForceConfig.exports(config.getVector()));

        return node;
    }

    /** The media value. */
    private final String media;
    /** The sfx value. */
    private final Optional<String> sfx;
    /** The delay value. */
    private final int delay;
    /** The horizontal offset. */
    private final int ox;
    /** The vertical offset. */
    private final int oy;
    /** The launchable vector. */
    private final Force vector;

    /**
     * Constructor.
     * 
     * @param media The media value (must not be <code>null</code>).
     * @param sfx The sfx value (can be <code>null</code>).
     * @param delay The delay value.
     * @param ox The horizontal offset.
     * @param oy The vertical offset.
     * @param vector The vector force (must not be <code>null</code>).
     * @throws LionEngineException If <code>null</code> arguments.
     */
    public LaunchableConfig(String media, String sfx, int delay, int ox, int oy, Force vector)
    {
        super();

        Check.notNull(media);
        Check.notNull(vector);

        this.media = media;
        this.sfx = Optional.ofNullable(sfx);
        this.delay = delay;
        this.ox = ox;
        this.oy = oy;
        this.vector = vector;
    }

    /**
     * Get the media.
     * 
     * @return The launchable media.
     */
    public String getMedia()
    {
        return media;
    }

    /**
     * Get the sfx.
     * 
     * @return The launchable sfx.
     */
    public Optional<String> getSfx()
    {
        return sfx;
    }

    /**
     * Get the launch delay value.
     * 
     * @return The launch delay value.
     */
    public int getDelay()
    {
        return delay;
    }

    /**
     * Get the horizontal offset.
     * 
     * @return The horizontal offset.
     */
    public int getOffsetX()
    {
        return ox;
    }

    /**
     * Get the vertical offset.
     * 
     * @return The vertical offset.
     */
    public int getOffsetY()
    {
        return oy;
    }

    /**
     * Get the vector value.
     * 
     * @return The vector value.
     */
    public Force getVector()
    {
        return vector;
    }

    /*
     * Object
     */

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + media.hashCode();
        result = prime * result + sfx.hashCode();
        result = prime * result + delay;
        result = prime * result + ox;
        result = prime * result + oy;
        result = prime * result + vector.hashCode();
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
        final LaunchableConfig other = (LaunchableConfig) object;
        return ox == other.ox
               && oy == other.oy
               && delay == other.delay
               && media.equals(other.media)
               && sfx.equals(other.sfx)
               && vector.equals(other.vector);
    }

    @Override
    public String toString()
    {
        return new StringBuilder(MIN_LENGTH).append(getClass().getSimpleName())
                                            .append(" [media=")
                                            .append(media)
                                            .append(", sfx=")
                                            .append(sfx.orElse(null))
                                            .append(", delay=")
                                            .append(delay)
                                            .append(", ox=")
                                            .append(ox)
                                            .append(", oy=")
                                            .append(oy)
                                            .append(", vector=")
                                            .append(vector)
                                            .append("]")
                                            .toString();
    }
}
