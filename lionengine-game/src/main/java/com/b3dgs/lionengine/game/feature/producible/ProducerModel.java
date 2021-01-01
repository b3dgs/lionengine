/*
 * Copyright (C) 2013-2021 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.producible;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.ListenableModel;
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
    private final ListenableModel<ProducerListener> listenable = new ListenableModel<>();
    /** Production queue. */
    private final Queue<Featurable> productions = new ArrayDeque<>();
    /** Handler reference. */
    private final Handler handler = services.get(Handler.class);
    /** Production checker. */
    private ProducerChecker checker = featurable -> true;
    /** Steps per tick. */
    private double stepsPerTick = 1.0;
    /** Current element being under production. */
    private Featurable current;
    /** Current production steps. */
    private int steps;
    /** Production progress. */
    private double progress;
    /** Production state. */
    private ProducerState state = ProducerState.NONE;

    /**
     * Create feature.
     * <p>
     * The {@link Services} must provide the following services:
     * </p>
     * <ul>
     * <li>{@link Handler}</li>
     * </ul>
     * <p>
     * The {@link Featurable} can be a {@link ProducerChecker}.
     * </p>
     * <p>
     * If the {@link Featurable} is a {@link ProducerListener}, it will automatically
     * {@link #addListener(ProducerListener)} on it.
     * </p>
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public ProducerModel(Services services, Setup setup)
    {
        super(services, setup);
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
            for (int i = 0; i < listenable.size(); i++)
            {
                listenable.get(i).notifyCanNotProduce(current);
            }
        }
    }

    /**
     * Action called from update production in producing state.
     */
    private void actionProducing()
    {
        progress += stepsPerTick;

        for (int i = 0; i < listenable.size(); i++)
        {
            listenable.get(i).notifyProducing(current);
        }
        for (final ProducibleListener listener : current.getFeature(Producible.class).getListeners())
        {
            listener.notifyProductionProgress(this);
        }

        // Production finished
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
        final List<ProducibleListener> listeners = current.getFeature(Producible.class).getListeners();
        final int n = listeners.size();
        for (int i = 0; i < n; i++)
        {
            listeners.get(i).notifyProductionEnded(this);
        }
        for (int i = 0; i < listenable.size(); i++)
        {
            listenable.get(i).notifyProduced(current);
        }
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
        transformable.teleport(producible.getX(), producible.getY());
        handler.add(featurable);
        steps = current.getFeature(Producible.class).getSteps();
        progress = 0.0;
        for (int i = 0; i < listenable.size(); i++)
        {
            listenable.get(i).notifyStartProduction(featurable);
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
        listenable.addListener(listener);
    }

    @Override
    public void removeListener(ProducerListener listener)
    {
        listenable.removeListener(listener);
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
        Check.notNull(featurable);

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
            case WILL_PRODUCE:
                actionWillProduce();
                break;
            case PRODUCING:
                actionProducing();
                break;
            case PRODUCED:
                actionProduced();
                break;
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
    public void setStepsSpeed(double stepsPerTick)
    {
        Check.superiorStrict(stepsPerTick, 0.0);

        this.stepsPerTick = stepsPerTick;
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
    public Featurable getProducingElement()
    {
        return current;
    }

    @Override
    public Iterator<Featurable> iterator()
    {
        return productions.iterator();
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
    public void recycle()
    {
        state = ProducerState.NONE;
        stepsPerTick = 1.0;
        progress = 0.0;
        steps = 0;
    }
}
