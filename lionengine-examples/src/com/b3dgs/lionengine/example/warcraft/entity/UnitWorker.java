/*
 * Copyright (C) 2013 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.example.warcraft.entity;

import java.util.Iterator;

import com.b3dgs.lionengine.anim.Animation;
import com.b3dgs.lionengine.example.warcraft.ResourceType;
import com.b3dgs.lionengine.example.warcraft.effect.Effect;
import com.b3dgs.lionengine.example.warcraft.effect.EffectType;
import com.b3dgs.lionengine.example.warcraft.effect.HandlerEffect;
import com.b3dgs.lionengine.example.warcraft.map.Tile;
import com.b3dgs.lionengine.example.warcraft.map.TileCollision;
import com.b3dgs.lionengine.game.CoordTile;
import com.b3dgs.lionengine.game.Orientation;
import com.b3dgs.lionengine.game.Tiled;
import com.b3dgs.lionengine.game.TimedMessage;
import com.b3dgs.lionengine.game.rts.ability.extractor.Extractible;
import com.b3dgs.lionengine.game.rts.ability.extractor.ExtractorModel;
import com.b3dgs.lionengine.game.rts.ability.extractor.ExtractorServices;
import com.b3dgs.lionengine.game.rts.ability.extractor.ExtractorUsedServices;
import com.b3dgs.lionengine.game.rts.ability.producer.ProducerModel;
import com.b3dgs.lionengine.game.rts.ability.producer.ProducerServices;
import com.b3dgs.lionengine.game.rts.ability.producer.ProducerUsedServices;
import com.b3dgs.lionengine.game.rts.entity.EntityNotFoundException;

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
    private final HandlerEntity handlerEntity;
    /** Handler effect. */
    private final HandlerEffect handlerEffect;
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
    /** Work animation. */
    private final Animation animWork;
    /** Carry gold animation. */
    private final Animation animCarryGold;
    /** Carry wood animation. */
    private final Animation animCarryWood;
    /** Construction surface. */
    private final Effect construction;
    /** Timed message. */
    private final TimedMessage message;
    /** Last town hall. */
    private Entity townHall;

    /**
     * Constructor.
     * 
     * @param setup The setup reference.
     */
    protected UnitWorker(SetupEntity setup)
    {
        super(setup);
        factory = setup.factoryEntity;
        handlerEntity = setup.handlerEntity;
        handlerEffect = setup.handlerEffect;
        message = setup.message;
        producer = new ProducerModel<>(this, setup.handlerEntity, setup.fps);
        extractor = new ExtractorModel<>(this, setup.fps);
        stepsPerSecond = getDataInteger("steps_per_second", "production");
        extractionSpeed = getDataInteger("extraction_speed", "extraction");
        extractionCapacity = getDataInteger("extraction_capacity", "extraction");
        dropOffSpeed = getDataInteger("drop_off_speed", "extraction");
        animWork = getDataAnimation("work");
        animCarryGold = getDataAnimation("carry_gold");
        animCarryWood = getDataAnimation("carry_wood");
        construction = setup.factoryEffect.create(EffectType.CONSTRUCTION);
    }

    /**
     * This function will search another tree, around the last cut.
     */
    private void searchNextTree()
    {
        final CoordTile tile = map.getClosestTile(this, getResourceLocation(), TileCollision.TREE, 64);
        setResource(ResourceType.WOOD, tile.getX(), tile.getY(), 1, 1);
    }

    /*
     * Unit
     */

    @Override
    public void update(double extrp)
    {
        super.update(extrp);
        producer.updateProduction(extrp);
        extractor.updateExtraction(extrp);
    }

    @Override
    public void stop()
    {
        super.stop();
        stopProduction();
        stopExtraction();
    }

    @Override
    public void decreaseLife(int damages, Attacker attacker)
    {
        super.decreaseLife(damages, attacker);
        if (attacker != null)
        {
            final Orientation opposite = attacker.getOrientation();
            setOrientation(Orientation.next(opposite, Orientation.ORIENTATIONS_NUMBER));
        }
    }

    @Override
    public void setSelection(boolean state)
    {
        super.setSelection(state);
        if (!state)
        {
            setSkillPanel(0);
        }
    }

    /*
     * ProducerUsedServices
     */

    @Override
    public boolean canProduce(ProducibleEntity producible)
    {
        return getPlayer().canSpend(producible.getCost());
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
    public Entity getEntityToProduce(EntityType type)
    {
        return factory.create(type);
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
        final Tiled resource = getResourceLocation();
        try
        {
            final Entity entity = handlerEntity.getEntityAt(resource);
            // Allow worker to enter inside the gold mine
            setIgnoreId(entity.getId(), true);
            return getDistanceInTile(entity, true) <= 1;
        }
        catch (final EntityNotFoundException entity)
        {
            if (!isDestinationReached())
            {
                return false;
            }
            final ResourceType type = map.getTile(resource).getResourceType();
            switch (type)
            {
                case WOOD:
                    return getDistanceInTile(extractor.getResourceLocation(), false) <= 1;
                default:
                    stopExtraction();
                    break;
            }
            return false;
        }
    }

    @Override
    public boolean canCarry()
    {
        setIgnoreId(townHall.getId(), true);
        return getDistanceInTile(townHall, true) <= 1;
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
        final ProductionCost cost = element.getCost();
        final String text;
        if (getPlayer().getGold() < cost.getGold())
        {
            text = "Not enough gold !";
        }
        else if (getPlayer().getWood() < cost.getWood())
        {
            text = "Not enough wood !";
        }
        else
        {
            text = "Unable to construct !";
        }
        message.addMessage(text, 72, 191, 2000);
    }

    @Override
    public void notifyStartProduction(ProducibleEntity element, Entity entity)
    {
        setActive(false);
        setVisible(false);
        setSelection(false);
        final int x = getLocationIntX() + entity.getWidth() / 2 - construction.getWidth() / 2 - 16;
        final int y = getLocationIntY() + entity.getHeight() / 2 - construction.getHeight() / 2 - 16;
        construction.setFrame(1);
        construction.start(x, y);
        handlerEffect.add(construction);
        getPlayer().spend(element.getCost());
        entity.setPlayer(getPlayer());
    }

    @Override
    public void notifyProducing(ProducibleEntity element, Entity entity)
    {
        if (entity instanceof Building)
        {
            final int progressPercent = getProductionProgressPercent();
            entity.setProgressPercent(progressPercent);
            if (progressPercent > 30 && progressPercent < 70)
            {
                construction.setFrame(2);
                entity.setVisible(false);
            }
            else if (progressPercent > 70)
            {
                handlerEffect.remove(construction);
                entity.setVisible(true);
                entity.setFrame(1);
            }
            else
            {
                entity.setVisible(false);
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

        // Increase pop if needed
        if (EntityType.FARM_ORC == entity.type || EntityType.FARM_HUMAN == entity.type)
        {
            getPlayer().changePopulationCapacity(5);
        }
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
        // Search gold mine
        try
        {
            final Entity entity = handlerEntity.getEntityAt(resourceLocation);
            if (entity instanceof Extractible)
            {
                final Extractible<?> extractible = (Extractible<?>) entity;
                extractible.extractResource(getExtractionCapacity());
                setVisible(false);
                setActive(false);
                setSelection(false);
            }
        }
        // Is it a tree ?
        catch (final EntityNotFoundException exception)
        {
            final Tile tile = map.getTile(resourceLocation);
            if (ResourceType.WOOD == tile.getResourceType())
            {
                pointTo(getResourceLocation());
                play(animWork);
            }
        }
    }

    @Override
    public void notifyExtracted(ResourceType type, int currentQuantity)
    {
        // Nothing to do
    }

    @Override
    public void notifyStartCarry(ResourceType type, int totalQuantity)
    {
        try
        {
            townHall = handlerEntity.getClosestEntity(this, Warehouse.class, true);
            if (ResourceType.GOLD == type)
            {
                setVisible(true);
                setActive(true);

                final CoordTile out = map.getClosestAvailableTile(this, 16, townHall);
                if (out != null)
                {
                    setLocation(out.getX(), out.getY());
                }
                setDestination(townHall);
                play(animCarryGold);
            }
            else if (ResourceType.WOOD == type)
            {
                map.cutTree(getResourceLocation());
                searchNextTree();
                setDestination(townHall);
                play(animCarryWood);
            }
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
        setSelection(false);
        getPlayer().increase(type, totalQuantity);
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
    }
}
