/*
 * Copyright (C) 2013-2019 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import java.util.function.IntSupplier;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.ListenableModel;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Handler;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionProvider;

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
    private final Handler handler;
    /** Rate provider. */
    private final IntSupplier rate;
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
     * Create feature.
     * <p>
     * The {@link Services} must provide the following services:
     * </p>
     * <ul>
     * <li>{@link Handler}</li>
     * <li>{@link SourceResolutionProvider} (for the desired fps).</li>
     * </ul>
     * <p>
     * The {@link Featurable} must be a {@link ProducerChecker}.
     * </p>
     * <p>
     * If the {@link Featurable} is a {@link ProducerListener}, it will automatically
     * {@link #addListener(ProducerListener)} on it.
     * </p>
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid argument.
     */
    public ProducerModel(Services services)
    {
        super();

        Check.notNull(services);

        handler = services.get(Handler.class);
        rate = services.get(SourceResolutionProvider.class)::getRate;
    }

    /**
     * Create feature.
     * <p>
     * The {@link Services} must provide the following services:
     * </p>
     * <ul>
     * <li>{@link Handler}</li>
     * <li>{@link SourceResolutionProvider} (for the desired fps).</li>
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

        Check.notNull(services);

        handler = services.get(Handler.class);
        rate = services.get(SourceResolutionProvider.class)::getRate;
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
     * 
     * @param extrp The extrapolation value.
     */
    private void actionProducing(double extrp)
    {
        progress += speed * extrp;

        for (int i = 0; i < listenable.size(); i++)
        {
            listenable.get(i).notifyProducing(currentObject);
        }
        for (final ProducibleListener listener : current.getFeature(Producible.class).getListeners())
        {
            listener.notifyProductionProgress(this);
        }

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
        for (int i = 0; i < listenable.size(); i++)
        {
            listenable.get(i).notifyProduced(currentObject);
        }
        final List<ProducibleListener> listeners = current.getFeature(Producible.class).getListeners();
        final int n = listeners.size();
        for (int i = 0; i < n; i++)
        {
            listeners.get(i).notifyProductionEnded(this);
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
        speed = stepsPerSecond / rate.getAsInt();
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
        stepsPerSecond = 1.0;
        progress = 0.0;
        steps = 0;
    }
}
