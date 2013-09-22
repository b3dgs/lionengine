package com.b3dgs.lionengine.example.d_rts.e_skills.entity;

import java.awt.Color;
import java.util.Iterator;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.drawable.Bar;
import com.b3dgs.lionengine.example.d_rts.e_skills.Context;
import com.b3dgs.lionengine.example.d_rts.e_skills.EntityType;
import com.b3dgs.lionengine.example.d_rts.e_skills.HandlerEntity;
import com.b3dgs.lionengine.example.d_rts.e_skills.ProducibleEntity;
import com.b3dgs.lionengine.example.d_rts.e_skills.ProductionCost;
import com.b3dgs.lionengine.example.d_rts.e_skills.ResourceType;
import com.b3dgs.lionengine.game.CoordTile;
import com.b3dgs.lionengine.game.Tiled;
import com.b3dgs.lionengine.game.entity.EntityNotFoundException;
import com.b3dgs.lionengine.game.rts.CameraRts;
import com.b3dgs.lionengine.game.rts.ability.extractor.Extractible;
import com.b3dgs.lionengine.game.rts.ability.extractor.ExtractorModel;
import com.b3dgs.lionengine.game.rts.ability.extractor.ExtractorServices;
import com.b3dgs.lionengine.game.rts.ability.extractor.ExtractorUsedServices;
import com.b3dgs.lionengine.game.rts.ability.producer.ProducerModel;
import com.b3dgs.lionengine.game.rts.ability.producer.ProducerServices;
import com.b3dgs.lionengine.game.rts.ability.producer.ProducerUsedServices;

/**
 * Worker unit implementation.
 */
public abstract class UnitWorker
        extends Unit
        implements ProducerUsedServices<EntityType, ProductionCost, ProducibleEntity, Entity>,
        ProducerServices<EntityType, ProductionCost, ProducibleEntity>, ExtractorUsedServices<ResourceType>,
        ExtractorServices<ResourceType>
{
    /** Producer model. */
    private final ProducerModel<EntityType, ProductionCost, ProducibleEntity, Entity> producer;
    /** Factory reference. */
    private final FactoryEntity factory;
    /** Handler reference. */
    private final HandlerEntity handler;
    /** Extractor model. */
    private final ExtractorModel<ResourceType> extractor;
    /** Production step per second. */
    private final int stepsPerSecond;
    /** Extraction speed. */
    private final int extractionSpeed;
    /** Extraction capacity. */
    private final int extractionCapacity;
    /** Drop off speed. */
    private final int dropOffSpeed;
    /** Last town hall. */
    private Entity townHall;
    /** Entity progress bar. */
    private final Bar barProgress;

    /**
     * Constructor.
     * 
     * @param id The entity type enum.
     * @param context The context reference.
     */
    protected UnitWorker(EntityType id, Context context)
    {
        super(id, context);
        factory = context.factoryEntity;
        handler = context.handlerEntity;
        producer = new ProducerModel<>(this, context.handlerEntity, context.desiredFps);
        extractor = new ExtractorModel<>(this, context.desiredFps);
        stepsPerSecond = getDataInteger("steps_per_second", "production");
        extractionSpeed = getDataInteger("extraction_speed", "extraction");
        extractionCapacity = getDataInteger("extraction_capacity", "extraction");
        dropOffSpeed = getDataInteger("drop_off_speed", "extraction");
        barProgress = new Bar(0, 0);
        barProgress.setBorderSize(1, 1);
    }

    /*
     * Unit
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
        if (isProducing() && getProductionProgress() > 0)
        {
            final int x = camera.getViewpointX(getLocationIntX());
            final int y = camera.getViewpointY(getLocationIntY() + getHeight() + 20);
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
        super.stop();
        stopProduction();
    }

    /*
     * ProducerUsedServices
     */

    @Override
    public boolean canProduce(ProducibleEntity producible)
    {
        return true;
    }

    @Override
    public boolean canBeProduced(ProducibleEntity producible)
    {
        final int dist = getDistanceInTile(producible, false);
        if (!isMoving())
        {
            if (dist > 0)
            {
                setDestination(producible);
            }
            else
            {
                return true;
            }
        }
        return false;
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
     * ExtractorUsedServices
     */

    @Override
    public boolean canExtract()
    {
        try
        {
            final Tiled resource = getResourceLocation();
            final Entity entity = handler.getEntityAt(resource.getLocationInTileX(), resource.getLocationInTileY());
            // Allow worker to enter inside the gold mine
            setIgnoreId(entity.getId(), true);
            return getDistanceInTile(entity, true) <= 1;
        }
        catch (final EntityNotFoundException entity)
        {
            return getDistanceInTile(extractor.getResourceLocation(), false) < 1;
        }
    }

    @Override
    public boolean canCarry()
    {
        setIgnoreId(townHall.getId(), true);
        return getDistanceInTile(townHall, true) < 1;
    }

    @Override
    public int getExtractionCapacity()
    {
        return extractionCapacity;
    }

    @Override
    public int getExtractionSpeed()
    {
        return extractionSpeed;
    }

    @Override
    public int getDropOffSpeed()
    {
        return dropOffSpeed;
    }

    /*
     * ProducerServices
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
     * ExtractorServices
     */

    @Override
    public void updateExtraction(double extrp)
    {
        extractor.updateExtraction(extrp);
    }

    @Override
    public void setResource(Extractible<ResourceType> entity)
    {
        extractor.setResource(entity);
    }

    @Override
    public void setResource(ResourceType type, int tx, int ty, int tw, int th)
    {
        extractor.setResource(type, tx, ty, tw, th);
    }

    @Override
    public Tiled getResourceLocation()
    {
        return extractor.getResourceLocation();
    }

    @Override
    public ResourceType getResourceType()
    {
        return extractor.getResourceType();
    }

    @Override
    public void startExtraction()
    {
        extractor.startExtraction();
    }

    @Override
    public void stopExtraction()
    {
        extractor.stopExtraction();
    }

    @Override
    public boolean isExtracting()
    {
        return extractor.isExtracting();
    }

    /*
     * ProducerListener
     */

    @Override
    public void notifyCanNotProduce(ProducibleEntity element)
    {
        // Nothing to do
    }

    @Override
    public void notifyStartProduction(ProducibleEntity element, Entity entity)
    {
        setActive(false);
        setVisible(false);
        setSelection(false);
    }

    @Override
    public void notifyProducing(ProducibleEntity element, Entity entity)
    {
        if (entity instanceof Building)
        {
            if (getProductionProgress() < 100)
            {
                entity.setFrame(1);
            }
        }
    }

    @Override
    public void notifyProduced(ProducibleEntity element, Entity entity)
    {
        entity.setFrame(2);
        final CoordTile coord = map.getFreeTileAround(this, 64);
        setLocation(coord.getX(), coord.getY());
        setActive(true);
        setVisible(true);
    }

    /*
     * ExtractorListener
     */

    @Override
    public void notifyStartGoToRessources(ResourceType type, Tiled resourceLocation)
    {
        setDestination(resourceLocation);
    }

    @Override
    public void notifyStartExtraction(ResourceType type, Tiled resourceLocation)
    {
        setVisible(false);
        setActive(false);
    }

    @Override
    public void notifyExtracted(ResourceType type, int currentQuantity)
    {
        // Nothing to do
    }

    @Override
    public void notifyStartCarry(ResourceType type, int totalQuantity)
    {
        setVisible(true);
        setActive(true);
        try
        {
            townHall = handler.getClosestEntity(this, Warehouse.class, true);
            final CoordTile out = map.getClosestAvailableTile(this, 16, townHall);
            if (out != null)
            {
                setLocation(out.getX(), out.getY());
            }
            setDestination(townHall);
        }
        catch (final EntityNotFoundException exception)
        {
            stopExtraction();
        }
    }

    @Override
    public void notifyStartDropOff(ResourceType type, int totalQuantity)
    {
        setVisible(false);
        setActive(false);
    }

    @Override
    public void notifyDroppedOff(ResourceType type, int droppedQuantity)
    {
        final CoordTile out = map.getClosestAvailableTile(this, 16, getResourceLocation());
        if (out != null)
        {
            setLocation(out.getX(), out.getY());
        }
        setVisible(true);
        setActive(true);
        // Can retrieve resources here (using type and droppedQuantity)
    }
}
