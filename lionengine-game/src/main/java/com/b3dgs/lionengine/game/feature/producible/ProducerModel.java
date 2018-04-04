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
package com.b3dgs.lionengine.game.feature.producible;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.game.ActionRef;
import com.b3dgs.lionengine.game.ActionsConfig;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Handler;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;

/**
 * Producer model implementation.
 */
public class ProducerModel extends FeatureModel implements Producer, Recyclable
{
    /** Producer listeners. */
    private final Collection<ProducerListener> listeners = new ArrayList<>();
    /** Production queue. */
    private final Queue<Featurable> productions = new ArrayDeque<>();
    /** Allowed actions name. */
    private final List<ActionRef> actions;
    /** Handler reference. */
    private final Handler handler;
    /** Tick timer rate. */
    private final double desiredFps;
    /** Production checker. */
    private ProducerChecker checker = featurable -> true;
    /** Steps per second. */
    private double stepsPerSecond;
    /** Current element being under production. */
    private Featurable current;
    /** Current object being under production. */
    private Featurable currentObject;
    /** Current production steps. */
    private int steps;
    /** Production progress. */
    private double progress;
    /** Production speed. */
    private double speed;
    /** Production state. */
    private ProducerState state;

    /**
     * Create a producer model.
     * <p>
     * The {@link Services} must provide the following services:
     * </p>
     * <ul>
     * <li>{@link Handler}</li>
     * <li>{@link Integer} (for the desired fps).</li>
     * </ul>
     * <p>
     * The {@link Featurable} must be a {@link ProducerChecker}.
     * </p>
     * <p>
     * If the {@link Featurable} is a {@link ProducerListener}, it will automatically
     * {@link #addListener(ProducerListener)} on it.
     * </p>
     * 
     * @param services The services reference.
     */
    public ProducerModel(Services services)
    {
        super();

        handler = services.get(Handler.class);
        desiredFps = services.get(Integer.class).intValue();

        actions = Collections.emptyList();

        recycle();
    }

    /**
     * Create a producer model.
     * <p>
     * The {@link Services} must provide the following services:
     * </p>
     * <ul>
     * <li>{@link Handler}</li>
     * <li>{@link Integer} (for the desired fps).</li>
     * </ul>
     * <p>
     * The {@link Featurable} must be a {@link ProducerChecker}.
     * </p>
     * <p>
     * If the {@link Featurable} is a {@link ProducerListener}, it will automatically
     * {@link #addListener(ProducerListener)} on it.
     * </p>
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     */
    public ProducerModel(Services services, Setup setup)
    {
        super();

        handler = services.get(Handler.class);
        desiredFps = services.get(Integer.class).intValue();

        actions = ActionsConfig.imports(setup);

        recycle();
    }

    /**
     * Action called from update production in will produce state.
     */
    private void actionWillProduce()
    {
        if (checker.checkProduction(current))
        {
            startProduction(current);
            state = ProducerState.PRODUCING;
        }
        else
        {
            for (final ProducerListener listener : listeners)
            {
                listener.notifyCanNotProduce(current);
            }
        }
    }

    /**
     * Action called from update production in producing state.
     * 
     * @param extrp The extrapolation value.
     */
    private void actionProducing(double extrp)
    {
        for (final ProducerListener listener : listeners)
        {
            listener.notifyProducing(currentObject);
        }
        for (final ProducibleListener listener : current.getFeature(Producible.class).getListeners())
        {
            listener.notifyProductionProgress(this);
        }
        progress += speed * extrp;

        // Production time elapsed
        if (progress >= steps)
        {
            progress = steps;
            state = ProducerState.PRODUCED;
        }
    }

    /**
     * Action called from update production in produced state.
     */
    private void actionProduced()
    {
        for (final ProducerListener listener : listeners)
        {
            listener.notifyProduced(currentObject);
        }
        for (final ProducibleListener listener : current.getFeature(Producible.class).getListeners())
        {
            listener.notifyProductionEnded(this);
        }
        currentObject = null;
        progress = -1;

        // Next production
        if (!productions.isEmpty())
        {
            state = ProducerState.CHECK;
        }
        else
        {
            state = ProducerState.NONE;
        }
    }

    /**
     * Action called from update production in check state.
     */
    private void actionCheck()
    {
        if (checker.checkProduction(productions.peek()))
        {
            produce();
        }
    }

    /**
     * Start next production.
     */
    private void produce()
    {
        current = productions.poll();
        state = ProducerState.WILL_PRODUCE;
    }

    /**
     * Start production of this element. Get its corresponding instance and add it to the handler.
     * Featurable will be removed from handler if production is cancelled.
     * 
     * @param featurable The element to produce.
     */
    private void startProduction(Featurable featurable)
    {
        final Transformable transformable = featurable.getFeature(Transformable.class);
        final Producible producible = featurable.getFeature(Producible.class);
        transformable.setLocation(producible.getX(), producible.getY());
        handler.add(featurable);
        currentObject = featurable;
        speed = stepsPerSecond / desiredFps;
        steps = current.getFeature(Producible.class).getSteps();
        progress = 0.0;
        for (final ProducerListener listener : listeners)
        {
            listener.notifyStartProduction(featurable);
        }
        for (final ProducibleListener listener : featurable.getFeature(Producible.class).getListeners())
        {
            listener.notifyProductionStarted(this);
        }
    }

    /*
     * Producer
     */

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        if (provider instanceof ProducerListener)
        {
            addListener((ProducerListener) provider);
        }
        if (provider instanceof ProducerChecker)
        {
            checker = (ProducerChecker) provider;
        }
    }

    @Override
    public void checkListener(Object listener)
    {
        super.checkListener(listener);

        if (listener instanceof ProducerListener)
        {
            addListener((ProducerListener) listener);
        }
    }

    @Override
    public void addListener(ProducerListener listener)
    {
        listeners.add(listener);
    }

    @Override
    public void setChecker(ProducerChecker checker)
    {
        Check.notNull(checker);
        this.checker = checker;
    }

    @Override
    public void addToProductionQueue(Featurable featurable)
    {
        productions.add(featurable);
        if (state == ProducerState.NONE)
        {
            produce();
        }
    }

    @Override
    public void update(double extrp)
    {
        switch (state)
        {
            case NONE:
                progress = -1;
                break;
            // Before production
            case WILL_PRODUCE:
                actionWillProduce();
                break;
            // During production
            case PRODUCING:
                actionProducing(extrp);
                break;
            // Production done
            case PRODUCED:
                actionProduced();
                break;
            // Next production ?
            case CHECK:
                actionCheck();
                break;
            default:
                throw new LionEngineException(state);
        }
    }

    @Override
    public void skipProduction()
    {
        if (isProducing())
        {
            handler.remove(currentObject);
            currentObject = null;
            current = null;
            progress = -1;
            steps = 0;
            state = ProducerState.CHECK;
        }
    }

    @Override
    public void stopProduction()
    {
        skipProduction();
        productions.clear();
        state = ProducerState.NONE;
    }

    @Override
    public void setStepsPerSecond(double stepsPerSecond)
    {
        this.stepsPerSecond = stepsPerSecond;
    }

    @Override
    public double getProgress()
    {
        return progress;
    }

    @Override
    public int getProgressPercent()
    {
        if (progress < 0)
        {
            return -1;
        }
        return (int) Math.round(progress / steps * 100);
    }

    @Override
    public Media getProducingElement()
    {
        if (current == null)
        {
            return null;
        }
        return current.getFeature(Producible.class).getMedia();
    }

    @Override
    public Iterator<Featurable> iterator()
    {
        return productions.iterator();
    }

    @Override
    public List<ActionRef> getActions()
    {
        return Collections.unmodifiableList(actions);
    }

    @Override
    public int getQueueLength()
    {
        return productions.size();
    }

    @Override
    public boolean isProducing()
    {
        return ProducerState.PRODUCING == state;
    }

    /*
     * Recyclable
     */

    @Override
    public final void recycle()
    {
        state = ProducerState.NONE;
        stepsPerSecond = 1.0;
        progress = 0.0;
        steps = 0;
    }
}
