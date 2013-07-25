package com.b3dgs.lionengine.game.rts.ability.producer;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import com.b3dgs.lionengine.game.rts.EntityRts;
import com.b3dgs.lionengine.game.rts.HandlerEntityRts;
import com.b3dgs.lionengine.game.rts.ability.AbilityModel;

/**
 * Default and abstract model implementation.
 * 
 * @param <T> The entity enum type used.
 * @param <C> The cost type used.
 * @param <P> The producible type used.
 * @param <E> The entity type used.
 */
public class ProducerModel<T extends Enum<T>, C extends ProductionCostRts, P extends Producible<T, C>, E extends EntityRts>
        extends AbilityModel<ProducerListener<T, C, P, E>, ProducerUsedServices<T, C, P, E>>
        implements ProducerServices<T, C, P>, ProducerListener<T, C, P, E>
{
    /** Producer states. */
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
    protected final HandlerEntityRts<?, ?, E, ?> handler;
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
     * Create a producer model.
     * 
     * @param user The model user.
     * @param handler The handler reference.
     * @param desiredFps The the desired frame rate.
     */
    public ProducerModel(ProducerUsedServices<T, C, P, E> user, HandlerEntityRts<?, ?, E, ?> handler, int desiredFps)
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
     * Producer services
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
     * Producer listener
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
