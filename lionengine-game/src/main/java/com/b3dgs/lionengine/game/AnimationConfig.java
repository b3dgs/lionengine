/*
 * Copyright (C) 2013-2022 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.XmlReader;

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
    /** Animations node name. */
    public static final String NODE_ANIMATIONS = Constant.XML_PREFIX + "animations";
    /** Animation node name. */
    public static final String NODE_ANIMATION = Constant.XML_PREFIX + "animation";
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
    /** Animation not found error. */
    static final String ERROR_NOT_FOUND = "Animation not found: ";

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
    public static AnimationConfig imports(XmlReader root)
    {
        Check.notNull(root);

        final Map<String, Animation> animations = new HashMap<>(0);

        if (root.hasNode(NODE_ANIMATIONS))
        {
            final Collection<XmlReader> children = root.getChild(NODE_ANIMATIONS).getChildren(NODE_ANIMATION);
            for (final XmlReader node : children)
            {
                final String anim = node.getString(ANIMATION_NAME);
                final Animation animation = createAnimation(node);
                animations.put(anim, animation);
            }
            children.clear();
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

        final String name = node.getString(ANIMATION_NAME);
        final int start = node.getInteger(ANIMATION_START);
        final int end = node.getInteger(ANIMATION_END);
        final double speed = node.getDouble(ANIMATION_SPEED);
        final boolean reversed = node.getBoolean(ANIMATION_REVERSED);
        final boolean repeat = node.getBoolean(ANIMATION_REPEAT);

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

        final Xml animations;
        if (root.hasNode(NODE_ANIMATIONS))
        {
            animations = root.getChildXml(NODE_ANIMATIONS);
        }
        else
        {
            animations = root.createChild(NODE_ANIMATIONS);
        }

        final Xml node = animations.createChild(NODE_ANIMATION);
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
        super();

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
            throw new LionEngineException(ERROR_NOT_FOUND + name);
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
