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

import com.b3dgs.lionengine.example.warcraft.Player;
import com.b3dgs.lionengine.game.CoordTile;
import com.b3dgs.lionengine.game.TimedMessage;
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
    /** Timed message. */
    private final TimedMessage message;

    /**
     * Constructor.
     * 
     * @param setup The setup reference.
     */
    protected BuildingProducer(SetupEntity setup)
    {
        super(setup);
        factory = setup.factoryEntity;
        message = setup.message;
        producer = new ProducerModel<>(this, setup.handlerEntity, setup.fps);
        stepsPerSecond = getDataInteger("steps_per_second", "production");
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
        final Player player = getPlayer();
        return !player.isPopFull() && player.canSpend(producible.getCost());
    }

    @Override
    public boolean canBeProduced(ProducibleEntity producible)
    {
        final Player player = getPlayer();
        final ProductionCost cost = producible.getCost();
        return !player.isPopFull() && player.canSpend(cost);
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
        else if (getPlayer().isPopFull())
        {
            text = "Not enough food, build more farm !";
        }
        else
        {
            text = "Unable to produce !";
        }
        message.addMessage(text, 72, 191, 2000);
    }

    @Override
    public void notifyStartProduction(ProducibleEntity element, Entity entity)
    {
        final Player owner = getPlayer();
        final ProductionCost cost = element.getCost();
        owner.spend(cost);
        entity.setPlayer(owner);
        entity.setVisible(false);
    }

    @Override
    public void notifyProducing(ProducibleEntity element, Entity entity)
    {
        entity.setProgressPercent(getProductionProgressPercent());
    }

    @Override
    public void notifyProduced(ProducibleEntity element, Entity entity)
    {
        final CoordTile coord = map.getFreeTileAround(this, 64);
        entity.setLocation(coord.getX(), coord.getY());
        entity.setVisible(true);
        getPlayer().changePopulation(1);
    }
}
