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
package com.b3dgs.lionengine.game.trait.producible;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.game.object.Handler;
import com.b3dgs.lionengine.game.object.ObjectGame;
import com.b3dgs.lionengine.game.object.Services;
import com.b3dgs.lionengine.game.trait.TraitModel;
import com.b3dgs.lionengine.game.trait.transformable.Transformable;

/**
 * Producer model implementation.
 * <p>
 * The {@link Services} must provide the following services:
 * </p>
 * <ul>
 * <li>{@link Handler}</li>
 * <li>{@link Integer}</li> (for the desired fps).
 * </ul>
 * <p>
 * The {@link ObjectGame} must be a {@link ProducerChecker}.
 * </p>
 * <p>
 * If the {@link ObjectGame} is a {@link ProducerListener}, it will automatically {@link #addListener(ProducerListener)}
 * on it.
 * </p>
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class ProducerModel
        extends TraitModel
        implements Producer
{
    /** Producer listeners. */
    private final Collection<ProducerListener> listeners = new ArrayList<>();
    /** Production queue. */
    private final Queue<Producible> productions = new LinkedList<>();
    /** Handler reference. */
    private Handler handler;
    /** Tick timer rate. */
    private double desiredFps;
    /** Production checker. */
    private ProducerChecker checker;
    /** Steps per second. */
    private double stepsPerSecond;
    /** Current element being under production. */
    private Producible current;
    /** Current object being under production. */
    private ObjectGame currentObject;
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
     */
    public ProducerModel()
    {
        super();

        state = ProducerState.NONE;
        stepsPerSecond = 1.0;
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
            listener.notifyProducing(current, currentObject);
        }
        for (final ProducibleListener listener : current.getListeners())
        {
            listener.notifyProductionProgress();
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
            listener.notifyProduced(current, currentObject);
        }
        for (final ProducibleListener listener : current.getListeners())
        {
            listener.notifyProductionEnded();
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
     * Start production of this element. Get its corresponding instance and add it to the handler. Object will be
     * removed from handler if production is cancelled.
     * 
     * @param producible The element to produce.
     */
    private void startProduction(Producible producible)
    {
        final ObjectGame object = producible.getOwner();
        final Transformable transformable = object.getTrait(Transformable.class);
        transformable.setLocation(producible.getX(), producible.getY());
        handler.add(object);
        currentObject = object;
        speed = stepsPerSecond / desiredFps;
        steps = current.getSteps();
        progress = 0.0;
        for (final ProducerListener listener : listeners)
        {
            listener.notifyStartProduction(producible, object);
        }
        for (final ProducibleListener listener : producible.getListeners())
        {
            listener.notifyProductionStarted();
        }
    }

    /*
     * Producer
     */

    @Override
    public void prepare(ObjectGame owner, Services services)
    {
        super.prepare(owner, services);

        handler = services.get(Handler.class);
        desiredFps = services.get(Integer.class).intValue();

        if (owner instanceof ProducerListener)
        {
            addListener((ProducerListener) owner);
        }
        checker = (ProducerChecker) owner;
    }

    @Override
    public void addListener(ProducerListener listener)
    {
        listeners.add(listener);
    }

    @Override
    public void addToProductionQueue(Producible producible)
    {
        productions.add(producible);
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
            case WILL_PRODUCE: // Before production
                actionWillProduce();
                break;
            case PRODUCING: // During production
                actionProducing(extrp);
                break;
            case PRODUCED: // Production done
                actionProduced();
                break;
            case CHECK: // Next production ?
                actionCheck();
                break;
            default:
                progress = -1;
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
        return current.getMedia();
    }

    @Override
    public Iterator<Producible> iterator()
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
}
