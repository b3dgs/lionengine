/*
 * Copyright (C) 2013-2026 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import com.b3dgs.lionengine.AttributesReader;
import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.ForceConfig;

/**
 * Represents the launchable data.
 * 
 * <p>
 * This class is Thread-Safe.
 * </p>
 * 
 * @param media The media value.
 * @param delay The delay value.
 * @param ox The horizontal offset.
 * @param oy The vertical offset.
 * @param vector The vector force.
 * @param sfx The sfx value.
 */
public record LaunchableConfig(String media, int delay, int ox, int oy, Force vector, Optional<String> sfx)
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

    /**
     * Import the launchable data from node.
     * 
     * @param node The node reference (must not be <code>null</code>).
     * @return The launchable data.
     * @throws LionEngineException If unable to read node.
     */
    public static LaunchableConfig imports(AttributesReader node)
    {
        Check.notNull(node);

        final String media = node.getString(ATT_MEDIA);
        final Optional<String> sfx = node.getStringOptional(ATT_SFX);
        final int delay = node.getInteger(0, ATT_DELAY);
        final int ox = node.getInteger(0, ATT_OFFSET_X);
        final int oy = node.getInteger(0, ATT_OFFSET_Y);

        return new LaunchableConfig(media, delay, ox, oy, ForceConfig.imports(node), sfx);
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
        node.writeString(ATT_MEDIA, config.media());
        config.sfx().ifPresent(sfx -> node.writeString(ATT_SFX, sfx));
        node.writeInteger(ATT_DELAY, config.delay());
        node.writeInteger(ATT_OFFSET_X, config.ox());
        node.writeInteger(ATT_OFFSET_Y, config.oy());
        node.add(ForceConfig.exports(config.vector()));

        return node;
    }

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
        this(media, delay, ox, oy, vector, Optional.ofNullable(sfx));
    }

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
    public LaunchableConfig
    {
        Check.notNull(media);
        Check.notNull(vector);
    }
}
