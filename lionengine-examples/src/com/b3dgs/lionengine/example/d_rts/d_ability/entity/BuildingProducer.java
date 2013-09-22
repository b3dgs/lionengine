package com.b3dgs.lionengine.example.d_rts.d_ability.entity;

import java.awt.Color;
import java.util.Iterator;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.drawable.Bar;
import com.b3dgs.lionengine.example.d_rts.d_ability.Context;
import com.b3dgs.lionengine.example.d_rts.d_ability.EntityType;
import com.b3dgs.lionengine.example.d_rts.d_ability.ProducibleEntity;
import com.b3dgs.lionengine.example.d_rts.d_ability.ProductionCost;
import com.b3dgs.lionengine.game.CoordTile;
import com.b3dgs.lionengine.game.rts.CameraRts;
import com.b3dgs.lionengine.game.rts.ability.producer.ProducerModel;
import com.b3dgs.lionengine.game.rts.ability.producer.ProducerServices;
import com.b3dgs.lionengine.game.rts.ability.producer.ProducerUsedServices;

/**
 * Building producer implementation.
 */
public abstract class BuildingProducer
        extends Building
        implements ProducerUsedServices<EntityType, ProductionCost, ProducibleEntity, Entity>,
        ProducerServices<EntityType, ProductionCost, ProducibleEntity>
{
    /** Producer model. */
    private final ProducerModel<EntityType, ProductionCost, ProducibleEntity, Entity> producer;
    /** Factory reference. */
    private final FactoryEntity factory;
    /** Production step per second. */
    private final int stepsPerSecond;
    /** Entity progress bar. */
    private final Bar barProgress;

    /**
     * Constructor.
     * 
     * @param id The entity type enum.
     * @param context The context reference.
     */
    protected BuildingProducer(EntityType id, Context context)
    {
        super(id, context);
        factory = context.factoryEntity;
        producer = new ProducerModel<>(this, context.handlerEntity, context.desiredFps);
        stepsPerSecond = getDataInteger("steps_per_second", "production");
        barProgress = new Bar(0, 0);
        barProgress.setBorderSize(1, 1);
    }

    /*
     * Building
     */

    @Override
    public void update(double extrp)
    {
        super.update(extrp);
        producer.updateProduction(extrp);
    }

    @Override
    public void render(Graphic g, CameraRts camera)
    {
        super.render(g, camera);
        if (getProductionProgress() > 0)
        {
            final int x = camera.getViewpointX(getLocationIntX());
            final int y = camera.getViewpointY(getLocationIntY() + 8 + getHeight());
            barProgress.setLocation(x, y);
            barProgress.setMaximumSize(getWidth(), 4);
            barProgress.setWidthPercent(getProductionProgressPercent());
            barProgress.setColorBackground(Color.GRAY);
            barProgress.setColorForeground(Color.RED);
            barProgress.render(g);
        }
    }

    @Override
    public void stop()
    {
        stopProduction();
    }

    /*
     * Producer user
     */

    @Override
    public boolean canProduce(ProducibleEntity producible)
    {
        return true;
    }

    @Override
    public boolean canBeProduced(ProducibleEntity producible)
    {
        return true;
    }

    @Override
    public Entity getEntityToProduce(EntityType id)
    {
        return factory.createEntity(id);
    }

    @Override
    public int getStepsPerSecond()
    {
        return stepsPerSecond;
    }

    /*
     * Producer services
     */

    @Override
    public void addToProductionQueue(ProducibleEntity producible)
    {
        producer.addToProductionQueue(producible);
    }

    @Override
    public void updateProduction(double extrp)
    {
        producer.updateProduction(extrp);
    }

    @Override
    public void skipProduction()
    {
        producer.skipProduction();
    }

    @Override
    public void stopProduction()
    {
        producer.stopProduction();
    }

    @Override
    public double getProductionProgress()
    {
        return producer.getProductionProgress();
    }

    @Override
    public int getProductionProgressPercent()
    {
        return producer.getProductionProgressPercent();
    }

    @Override
    public EntityType getProducingElement()
    {
        return producer.getProducingElement();
    }

    @Override
    public Iterator<ProducibleEntity> getProductionIterator()
    {
        return producer.getProductionIterator();
    }

    @Override
    public int getQueueLength()
    {
        return producer.getQueueLength();
    }

    @Override
    public boolean isProducing()
    {
        return producer.isProducing();
    }

    /*
     * Producer listener
     */

    @Override
    public void notifyCanNotProduce(ProducibleEntity element)
    {
        // Nothing to do
    }

    @Override
    public void notifyStartProduction(ProducibleEntity element, Entity entity)
    {
        entity.setVisible(false);
    }

    @Override
    public void notifyProducing(ProducibleEntity element, Entity entity)
    {
        // Nothing to do
    }

    @Override
    public void notifyProduced(ProducibleEntity element, Entity entity)
    {
        final CoordTile coord = map.getFreeTileAround(this, 64);
        entity.setLocation(coord.getX(), coord.getY());
        entity.setVisible(true);
    }
}
