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
package com.b3dgs.lionengine.example.game.strategy.ability.entity;

import java.util.Iterator;

import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.game.Bar;
import com.b3dgs.lionengine.game.CoordTile;
import com.b3dgs.lionengine.game.strategy.CameraStrategy;
import com.b3dgs.lionengine.game.strategy.ability.producer.ProducerModel;
import com.b3dgs.lionengine.game.strategy.ability.producer.ProducerServices;
import com.b3dgs.lionengine.game.strategy.ability.producer.ProducerUsedServices;

/**
 * Building producer implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public abstract class BuildingProducer
        extends Building
        implements ProducerUsedServices<Entity, ProductionCost, ProducibleEntity>,
        ProducerServices<Entity, ProductionCost, ProducibleEntity>
{
    /** Producer model. */
    private final ProducerModel<Entity, ProductionCost, ProducibleEntity> producer;
    /** Factory reference. */
    private final FactoryEntity factory;
    /** Production step per second. */
    private final int stepsPerSecond;
    /** Entity progress bar. */
    private final Bar barProgress;

    /**
     * Constructor.
     * 
     * @param setup The setup reference.
     */
    protected BuildingProducer(SetupEntity setup)
    {
        super(setup);
        factory = setup.factoryEntity;
        producer = new ProducerModel<>(this, setup.handlerEntity, setup.fps);
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
    public void render(Graphic g, CameraStrategy camera)
    {
        super.render(g, camera);
        if (getProductionProgress() > 0)
        {
            final int x = camera.getViewpointX(getLocationIntX());
            final int y = camera.getViewpointY(getLocationIntY() + 8 + getHeight());
            barProgress.setLocation(x, y);
            barProgress.setMaximumSize(getWidth(), 4);
            barProgress.setWidthPercent(getProductionProgressPercent());
            barProgress.setColorBackground(ColorRgba.GRAY);
            barProgress.setColorForeground(ColorRgba.RED);
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
    public <EI extends Entity> EI getEntityToProduce(Class<EI> id)
    {
        return id.cast(factory.create(id));
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
    public Class<? extends Entity> getProducingElement()
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
