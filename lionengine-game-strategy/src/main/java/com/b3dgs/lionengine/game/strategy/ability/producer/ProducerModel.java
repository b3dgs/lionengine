/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.strategy.ability.producer;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import com.b3dgs.lionengine.game.strategy.ability.AbilityModel;
import com.b3dgs.lionengine.game.strategy.entity.EntityStrategy;
import com.b3dgs.lionengine.game.strategy.entity.HandlerEntityStrategy;

/**
 * Default and abstract model implementation.
 * 
 * @param <T> The entity enum type used.
 * @param <C> The cost type used.
 * @param <P> The producible type used.
 * @param <E> The entity type used.
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class ProducerModel<T extends Enum<T>, C extends ProductionCostStrategy, P extends Producible<T, C>, E extends EntityStrategy>
        extends AbilityModel<ProducerListener<T, C, P, E>, ProducerUsedServices<T, C, P, E>>
        implements ProducerServices<T, C, P>, ProducerListener<T, C, P, E>
{
    /**
     * Producer states.
     * 
     * @author Pierre-Alexandre (contact@b3dgs.com)
     */
    private static enum State
    {
        /** State none. */
        NONE,
        /** State will produce. */
        WILL_PRODUCE,
        /** State producing. */
        PRODUCING,
        /** State produced. */
        PRODUCED,
        /** State check. */
        CHECK;
    }

    /** Handler reference. */
    protected final HandlerEntityStrategy<?, ?, E, ?> handler;
    /** Production queue. */
    private final Queue<P> productions;
    /** Tick timer rate. */
    private final double desiredFps;
    /** Current element being under production. */
    private P cur;
    /** Current entity being under production. */
    private E inProduction;
    /** Current production steps. */
    private int productionSteps;
    /** Production state. */
    private State state;
    /** Production progress. */
    private double productionProgress;
    /** Production speed. */
    private double productionSpeed;

    /**
     * Constructor.
     * 
     * @param user The model user.
     * @param handler The handler reference.
     * @param desiredFps The the desired frame rate.
     */
    public ProducerModel(ProducerUsedServices<T, C, P, E> user, HandlerEntityStrategy<?, ?, E, ?> handler, int desiredFps)
    {
        super(user);
        this.handler = handler;
        this.desiredFps = desiredFps;
        productions = new LinkedList<>();
        state = State.NONE;
    }

    /**
     * Action called from update production in will produce state.
     */
    protected void actionWillProduce()
    {
        if (user.canBeProduced(cur))
        {
            startProduction(cur);
            state = State.PRODUCING;
        }
    }

    /**
     * Action called from update production in producing state.
     * 
     * @param extrp The extrapolation value.
     */
    protected void actionProducing(double extrp)
    {
        notifyProducing(cur, inProduction);
        productionProgress += productionSpeed * extrp;

        // Production time elapsed
        if (productionProgress >= productionSteps)
        {
            productionProgress = productionSteps;
            state = State.PRODUCED;
        }
    }

    /**
     * Action called from update production in produced state.
     */
    protected void actionProduced()
    {
        notifyProduced(cur, inProduction);
        inProduction.setActive(true);
        inProduction.setPlayerId(user.getPlayerId());
        inProduction = null;
        productionProgress = -1;

        // Next production
        if (!productions.isEmpty())
        {
            state = State.CHECK;
        }
        else
        {
            state = State.NONE;
        }
    }

    /**
     * Action called from update production in check state.
     */
    protected void actionCheck()
    {
        if (user.canProduce(productions.peek()))
        {
            produce();
        }
    }

    /**
     * Start next production.
     */
    private void produce()
    {
        cur = productions.poll();
        state = State.WILL_PRODUCE;
    }

    /**
     * Start production of this element. Get its corresponding instance and add it to the handler. Entity will be
     * removed from handler if production is cancelled.
     * 
     * @param producible The element to produce.
     */
    private void startProduction(P producible)
    {
        final E entity = user.getEntityToProduce(producible.getId());
        entity.setLocation(producible.getLocationInTileX(), producible.getLocationInTileY());
        entity.setActive(true);
        handler.add(entity);
        inProduction = entity;
        productionSpeed = user.getStepsPerSecond() / desiredFps;
        productionSteps = cur.getCost().getSteps();
        productionProgress = 0.0;
        notifyStartProduction(producible, entity);
    }

    /*
     * ProducerServices
     */

    @Override
    public void addToProductionQueue(P producible)
    {
        if (user.canProduce(producible))
        {
            productions.add(producible);
            if (state == State.NONE)
            {
                produce();
            }
        }
        else
        {
            notifyCanNotProduce(producible);
        }
    }

    @Override
    public void updateProduction(double extrp)
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
                productionProgress = -1;
        }
    }

    @Override
    public void skipProduction()
    {
        if (isProducing())
        {
            handler.remove(inProduction);
            inProduction = null;
            cur = null;
            productionProgress = -1;
            productionSteps = 0;
            state = State.CHECK;
        }
    }

    @Override
    public void stopProduction()
    {
        skipProduction();
        productions.clear();
        state = State.NONE;
    }

    @Override
    public double getProductionProgress()
    {
        return productionProgress;
    }

    @Override
    public int getProductionProgressPercent()
    {
        if (productionProgress < 0)
        {
            return -1;
        }
        return (int) Math.round(productionProgress / productionSteps * 100);
    }

    @Override
    public T getProducingElement()
    {
        if (cur == null)
        {
            return null;
        }
        return cur.getId();
    }

    @Override
    public Iterator<P> getProductionIterator()
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
        return State.WILL_PRODUCE == state || State.PRODUCING == state;
    }

    /*
     * ProducerListener
     */

    @Override
    public void notifyCanNotProduce(P producible)
    {
        for (final ProducerListener<T, C, P, E> listener : listeners)
        {
            listener.notifyCanNotProduce(producible);
        }
    }

    @Override
    public void notifyStartProduction(P producible, E entity)
    {
        for (final ProducerListener<T, C, P, E> listener : listeners)
        {
            listener.notifyStartProduction(producible, entity);
        }
    }

    @Override
    public void notifyProducing(P producible, E entity)
    {
        for (final ProducerListener<T, C, P, E> listener : listeners)
        {
            listener.notifyProducing(producible, entity);
        }
    }

    @Override
    public void notifyProduced(P producible, E entity)
    {
        for (final ProducerListener<T, C, P, E> listener : listeners)
        {
            listener.notifyProduced(producible, entity);
        }
    }
}
