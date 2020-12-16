/*
 * Copyright (C) 2013-2020 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.state;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Listenable;
import com.b3dgs.lionengine.ListenableModel;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.UtilReflection;
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.game.AnimationConfig;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.game.Feature;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;

/**
 * Handle the {@link State}.
 * <p>
 * Usage example:
 * </p>
 * <ul>
 * <li>{@link #changeState(Class)}</li>
 * <li>{@link #update(double)}</li>
 * </ul>
 */
@FeatureInterface
public class StateHandler extends FeatureModel implements Updatable, Recyclable, Listenable<StateTransitionListener>
{
    /** Feature parameter constructor index. */
    private static final int PARAM_FEATURE_INDEX = 0;

    /** List of available states. */
    private final Map<Class<? extends State>, State> states = new HashMap<>();
    /** Animation name converter. */
    private final Function<Class<? extends State>, String> converter;
    /** Transition listeners. */
    private final ListenableModel<StateTransitionListener> listenable = new ListenableModel<>();
    /** Class loader. */
    private final ClassLoader classLoader;
    /** Last state (<code>null</code> if none). */
    private Class<? extends State> last;
    /** Current state pointer (<code>null</code> if none). */
    private State current;
    /** Next state pointer (<code>null</code> if no change). */
    private Class<? extends State> next;

    /**
     * Create feature.
     * <p>
     * The {@link Configurer} can provide {@link Animation}.
     * </p>
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public StateHandler(Services services, Setup setup)
    {
        this(services, setup, Class::getSimpleName);
    }

    /**
     * Create feature.
     * <p>
     * The {@link Configurer} can provide {@link Animation}.
     * </p>
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @param converter The animation name converter (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    @SuppressWarnings("unchecked")
    public StateHandler(Services services, Setup setup, Function<Class<? extends State>, String> converter)
    {
        super(services, setup);

        Check.notNull(converter);

        this.converter = converter;
        classLoader = services.getOptional(ClassLoader.class).orElse(getClass().getClassLoader());
        StateConfig.imports(setup).ifPresent(state ->
        {
            try
            {
                next = (Class<? extends State>) classLoader.loadClass(state);
            }
            catch (final ClassNotFoundException exception)
            {
                Verbose.exception(exception);
            }
        });
    }

    /**
     * Change the current state.
     * 
     * @param next The next state (must not be <code>null</code>).
     * @throws LionEngineException If <code>null</code> argument or unable to change state.
     */
    public void changeState(Class<? extends State> next)
    {
        Check.notNull(next);

        this.next = next;
    }

    /**
     * Check the current state.
     * 
     * @param state The state to check.
     * @return <code>true</code> if it is this state, <code>false</code> else.
     */
    public boolean isState(Class<? extends State> state)
    {
        if (current != null)
        {
            return current.getClass().equals(state);
        }
        return false;
    }

    /**
     * Post update checking next transition if has.
     */
    public void postUpdate()
    {
        if (current != null && next == null)
        {
            final Class<? extends State> state = current.checkTransitions(last);
            if (state != null)
            {
                next = state;
            }
        }
        if (next != null)
        {
            updateState();
        }
    }

    /**
     * Update to next state defined and notify changes.
     */
    private void updateState()
    {
        final State from = current;
        if (current != null && next != current.getClass())
        {
            if (last != current.getClass())
            {
                last = current.getClass();
            }
            current.exit();
        }
        if (!states.containsKey(next))
        {
            final State state = create(next);
            states.put(next, state);
        }
        current = states.get(next);
        current.enter();

        for (int i = 0; i < listenable.size(); i++)
        {
            listenable.get(i).notifyStateTransition(from != null ? from.getClass() : null, next);
        }
        next = null;
    }

    /**
     * Create state from its type.
     * 
     * @param state The state to create (must not be <code>null</code>).
     * @return The created state.
     * @throws LionEngineException If unable to create state.
     */
    @SuppressWarnings("unchecked")
    private State create(Class<? extends State> state)
    {
        Check.notNull(state);
        try
        {
            if (setup.hasNode(AnimationConfig.NODE_ANIMATIONS))
            {
                final AnimationConfig configAnimations = AnimationConfig.imports(setup);
                final String name = converter.apply(state);
                final Animation animation = configAnimations.getAnimation(name);
                final Class<? extends Feature> feature;
                feature = (Class<? extends Feature>) UtilReflection.getCompatibleConstructor(state,
                                                                                             FeatureProvider.class,
                                                                                             Animation.class)
                                                                   .getParameters()[PARAM_FEATURE_INDEX].getType();
                return UtilReflection.create(state,
                                             UtilReflection.getParamTypes(feature, animation),
                                             getFeature(feature),
                                             animation);
            }
            return UtilReflection.createReduce(state);
        }
        catch (final NoSuchMethodException exception)
        {
            throw new LionEngineException(exception);
        }
    }

    /*
     * Listenable
     */

    @Override
    public void addListener(StateTransitionListener listener)
    {
        listenable.addListener(listener);
    }

    @Override
    public void removeListener(StateTransitionListener listener)
    {
        listenable.removeListener(listener);
    }

    /*
     * Updatable
     */

    @Override
    public void update(double extrp)
    {
        if (current != null)
        {
            current.update(extrp);
        }
    }

    /*
     * Recyclable
     */

    @Override
    public void recycle()
    {
        last = null;
        current = null;
    }
}
