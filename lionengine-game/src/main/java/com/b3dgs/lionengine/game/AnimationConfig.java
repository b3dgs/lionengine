/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.io.Xml;
import com.b3dgs.lionengine.io.XmlReader;

/**
 * Represents the animations data.
 * <p>
 * This class is Thread-Safe.
 * </p>
 * 
 * @see Animation
 */
public final class AnimationConfig
{
    /** Animation node name. */
    public static final String ANIMATION = Constant.XML_PREFIX + "animation";
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
     * Create the animation data from configurer.
     * 
     * @param configurer The configurer reference (must not be <code>null</code>).
     * @return The animations configuration instance.
     * @throws LionEngineException If unable to read data.
     */
    public static AnimationConfig imports(Configurer configurer)
    {
        Check.notNull(configurer);

        return imports(configurer.getRoot());
    }

    /**
     * Create the animation data from configurer.
     * 
     * @param root The root reference (must not be <code>null</code>).
     * @return The animations configuration instance.
     * @throws LionEngineException If unable to read data.
     */
    public static AnimationConfig imports(Xml root)
    {
        Check.notNull(root);

        final Map<String, Animation> animations = new HashMap<>(0);

        for (final Xml node : root.getChildren(ANIMATION))
        {
            final String anim = node.readString(ANIMATION_NAME);
            final Animation animation = createAnimation(node);
            animations.put(anim, animation);
        }

        return new AnimationConfig(animations);
    }

    /**
     * Create animation data from node.
     * 
     * @param node The animation node (must not be <code>null</code>).
     * @return The animation instance.
     * @throws LionEngineException If error when reading animation data.
     */
    public static Animation createAnimation(XmlReader node)
    {
        Check.notNull(node);

        final String name = node.readString(ANIMATION_NAME);
        final int start = node.readInteger(ANIMATION_START);
        final int end = node.readInteger(ANIMATION_END);
        final double speed = node.readDouble(ANIMATION_SPEED);
        final boolean reversed = node.readBoolean(ANIMATION_REVERSED);
        final boolean repeat = node.readBoolean(ANIMATION_REPEAT);

        return new Animation(name, start, end, speed, reversed, repeat);
    }

    /**
     * Create an XML node from an animation.
     * 
     * @param root The node root (must not be <code>null</code>).
     * @param animation The animation reference (must not be <code>null</code>).
     * @throws LionEngineException If error on writing.
     */
    public static void exports(Xml root, Animation animation)
    {
        Check.notNull(root);
        Check.notNull(animation);

        final Xml node = root.createChild(ANIMATION);
        node.writeString(ANIMATION_NAME, animation.getName());
        node.writeInteger(ANIMATION_START, animation.getFirst());
        node.writeInteger(ANIMATION_END, animation.getLast());
        node.writeDouble(ANIMATION_SPEED, animation.getSpeed());
        node.writeBoolean(ANIMATION_REVERSED, animation.hasReverse());
        node.writeBoolean(ANIMATION_REPEAT, animation.hasRepeat());
    }

    /** Animations map. */
    private final Map<String, Animation> animations;

    /**
     * Create configuration.
     * 
     * @param animations The animations mapping (must not be <code>null</code>).
     * @throws LionEngineException If invalid argument.
     */
    public AnimationConfig(Map<String, Animation> animations)
    {
        Check.notNull(animations);

        this.animations = new HashMap<>(animations);
    }

    /**
     * Get an animation data from its name.
     * 
     * @param name The animation name.
     * @return The animation reference.
     * @throws LionEngineException If the animation with the specified name is not found.
     */
    public Animation getAnimation(String name)
    {
        final Animation animation = animations.get(name);
        if (animation == null)
        {
            throw new LionEngineException("Animation not found: " + name);
        }
        return animation;
    }

    /**
     * Check if animation exists.
     * 
     * @param name The animation name.
     * @return <code>true</code> if exists, <code>false</code> else.
     */
    public boolean hasAnimation(String name)
    {
        return animations.containsKey(name);
    }

    /**
     * Get all animations as read only.
     * 
     * @return The animations list.
     */
    public Collection<Animation> getAnimations()
    {
        return Collections.unmodifiableCollection(animations.values());
    }
}
