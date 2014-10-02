/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.configurer;

import java.util.HashMap;
import java.util.Map;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.anim.Anim;
import com.b3dgs.lionengine.anim.Animation;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Represents the animations data from a configurer node.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class ConfigAnimations
{
    /** Animation node name. */
    public static final String ANIMATION = Configurer.PREFIX + "animation";
    /** Animation attribute name. */
    public static final String ANIMATION_NAME = "name";
    /** Animation attribute start. */
    public static final String ANIMATION_START = "start";
    /** Animation attribute end. */
    public static final String ANIMATION_END = "end";
    /** Animation attribute speed. */
    public static final String ANIMATION_SPEED = "speed";
    /** Animation attribute reversed. */
    public static final String ANIMATION_REVERSED = "reversed";
    /** Animation attribute repeat. */
    public static final String ANIMATION_REPEAT = "repeat";

    /**
     * Create the size node.
     * 
     * @param configurer The configurer reference.
     * @return The config animations instance.
     * @throws LionEngineException If unable to read node or not a valid integer.
     */
    public static ConfigAnimations create(Configurer configurer)
    {
        final Map<String, Animation> animations = new HashMap<>(0);
        for (final XmlNode node : configurer.getRoot().getChildren(ConfigAnimations.ANIMATION))
        {
            final String anim = node.readString(ConfigAnimations.ANIMATION_NAME);
            final Animation animation = ConfigAnimations.createAnimation(node);
            animations.put(anim, animation);
        }
        return new ConfigAnimations(animations);
    }

    /**
     * Create an animation from its node.
     * 
     * @param node The animation node.
     * @return The animation instance.
     * @throws LionEngineException If error when reading animation data.
     */
    public static Animation createAnimation(XmlNode node) throws LionEngineException
    {
        final int start = node.readInteger(ConfigAnimations.ANIMATION_START);
        final int end = node.readInteger(ConfigAnimations.ANIMATION_END);
        final double speed = node.readDouble(ConfigAnimations.ANIMATION_SPEED);
        final boolean reversed = node.readBoolean(ConfigAnimations.ANIMATION_REVERSED);
        final boolean repeat = node.readBoolean(ConfigAnimations.ANIMATION_REPEAT);
        return Anim.createAnimation(start, end, speed, reversed, repeat);
    }

    /** Animations map. */
    private final Map<String, Animation> animations;

    /**
     * Load animations from configuration media.
     * 
     * @param animations The animations mapping.
     * @throws LionEngineException If error when opening the media.
     */
    private ConfigAnimations(Map<String, Animation> animations) throws LionEngineException
    {
        this.animations = animations;
    }

    /**
     * Clear the animations data.
     */
    public void clear()
    {
        animations.clear();
    }

    /**
     * Get an animation data from its name.
     * 
     * @param name The animation name.
     * @return The animation reference.
     * @throws LionEngineException If the animation with the specified name is not found.
     */
    public Animation getAnimation(String name) throws LionEngineException
    {
        final Animation animation = animations.get(name);
        Check.notNull(animation);
        return animation;
    }

    /**
     * Get all animations.
     * 
     * @return The animations list.
     */
    public Map<String, Animation> getAnimations()
    {
        return animations;
    }
}
