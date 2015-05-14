/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.tutorials.mario.d;

import java.lang.reflect.Constructor;
import java.util.Locale;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.anim.Animation;
import com.b3dgs.lionengine.game.state.State;

/**
 * List of entity states.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
enum EntityState
{
    /** Idle state. */
    IDLE(StateIdle.class),
    /** Walk state. */
    WALK(StateWalk.class),
    /** Turn state. */
    TURN(StateTurn.class),
    /** Jump state. */
    JUMP(StateJump.class),
    /** Death mario state. */
    DEATH_MARIO(StateDieMario.class),
    /** Death goomba state. */
    DEATH_GOOMBA(StateDieGoomba.class);

    /** Class reference. */
    private final Class<?> clazz;
    /** Animation name. */
    private final String animationName;

    /**
     * Constructor.
     * 
     * @param clazz The associated class reference.
     */
    private EntityState(Class<?> clazz)
    {
        this.clazz = clazz;
        animationName = name().toLowerCase(Locale.ENGLISH);
    }

    /**
     * Create the state from its parameters.
     * 
     * @param entity The entity reference.
     * @param animation The associated animation reference.
     * @return The state instance.
     */
    public State create(Entity entity, Animation animation)
    {
        try
        {
            final Constructor<?> constructor = clazz.getConstructor(Entity.class, Animation.class);
            return State.class.cast(constructor.newInstance(entity, animation));
        }
        catch (final ReflectiveOperationException exception)
        {
            throw new LionEngineException(exception);
        }
    }

    /**
     * Get the animation name.
     * 
     * @return The animation name.
     */
    public String getAnimationName()
    {
        return animationName;
    }
}
