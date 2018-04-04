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
package com.b3dgs.lionengine.game.state;

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.UtilReflection;
import com.b3dgs.lionengine.game.AnimationConfig;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.game.FeatureProvider;

/**
 * Utility class to load automatically states from enum.
 */
public final class StateAnimationUtil
{
    /**
     * Load all existing animations defined in the xml file.
     * 
     * @param states The states values.
     * @param factory The factory reference.
     * @param provider The featurable reference.
     * @param configurer The configurer reference.
     */
    public static void loadStates(StateAnimationBased[] states,
                                  StateFactory factory,
                                  FeatureProvider provider,
                                  Configurer configurer)
    {
        final AnimationConfig configAnimations = AnimationConfig.imports(configurer);
        for (final StateAnimationBased type : states)
        {
            try
            {
                final Animation animation = configAnimations.getAnimation(type.getAnimationName());
                final State state = UtilReflection.create(type.getStateClass(),
                                                          UtilReflection.getParamTypes(provider, animation),
                                                          provider,
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
    private StateAnimationUtil()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
