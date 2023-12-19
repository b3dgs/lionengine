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
package com.b3dgs.lionengine.editor.animation.editor;

import java.util.Collection;

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.editor.ObjectListAbstract;
import com.b3dgs.lionengine.game.AnimationConfig;
import com.b3dgs.lionengine.game.Configurer;

/**
 * Represents the animation list, allowing to add and remove {@link Animation}.
 */
public class AnimationList extends ObjectListAbstract<Animation>
{
    /** Animation default speed. */
    private static final double DEFAULT_SPEED = 0.1;

    /** Configurer reference. */
    private final Configurer configurer;

    /**
     * Create animation list and associate its properties.
     * 
     * @param configurer The configurer reference.
     * @param properties The properties reference.
     */
    public AnimationList(Configurer configurer, AnimationProperties properties)
    {
        super(Animation.class, properties);
        this.configurer = configurer;
    }

    /**
     * Load the existing animations from the object configurer.
     */
    public void loadAnimations()
    {
        final AnimationConfig configAnimations = AnimationConfig.imports(configurer.getRoot());
        final Collection<Animation> animations = configAnimations.getAnimations();
        loadObjects(animations);
    }

    /*
     * ObjectList
     */

    @Override
    protected Animation copyObject(Animation animation)
    {
        return new Animation(animation.getName(),
                             animation.getFirst(),
                             animation.getLast(),
                             animation.getSpeed(),
                             animation.hasReverse(),
                             animation.hasRepeat());
    }

    @Override
    protected Animation createObject(String name)
    {
        return new Animation(name, Animation.MINIMUM_FRAME, Animation.MINIMUM_FRAME + 1, DEFAULT_SPEED, false, false);
    }
}
