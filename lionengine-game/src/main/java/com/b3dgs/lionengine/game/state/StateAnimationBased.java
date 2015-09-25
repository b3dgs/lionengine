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
package com.b3dgs.lionengine.game.state;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.UtilReflection;
import com.b3dgs.lionengine.anim.Animation;
import com.b3dgs.lionengine.game.configurer.ConfigAnimations;
import com.b3dgs.lionengine.game.object.ObjectGame;

/**
 * Represents an animation based state, where the state enum is corresponding to an animation.
 * <p>
 * Class which implements this interface must have its first constructor with the following types: ({@link ObjectGame},
 * {@link Animation}).
 * </p>
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see Util#loadStates(StateAnimationBased[], StateFactory, ObjectGame)
 */
public interface StateAnimationBased
{
    /**
     * Get the state class.
     * 
     * @return The state class.
     */
    Class<? extends State> getStateClass();

    /**
     * Get the animation name.
     * 
     * @return The animation name.
     */
    String getAnimationName();

    /**
     * Utility class to load automatically states from enum.
     * 
     * @author Pierre-Alexandre (contact@b3dgs.com)
     */
    final class Util
    {
        /**
         * Load all existing animations defined in the xml file.
         * 
         * @param states The states values.
         * @param factory The factory reference.
         * @param object The object reference.
         */
        public static void loadStates(StateAnimationBased[] states, StateFactory factory, ObjectGame object)
        {
            final ConfigAnimations configAnimations = ConfigAnimations.create(object.getConfigurer());
            for (final StateAnimationBased type : states)
            {
                try
                {
                    final Animation animation = configAnimations.getAnimation(type.getAnimationName());
                    final State state = UtilReflection.create(type.getStateClass(),
                                                              UtilReflection.getParamTypes(object, animation),
                                                              object,
                                                              animation);
                    factory.addState(state);
                }
                catch (final NoSuchMethodException exception)
                {
                    throw new LionEngineException(exception);
                }
            }
        }

        /**
         * Private constructor.
         */
        private Util()
        {
            throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
        }
    }
}
