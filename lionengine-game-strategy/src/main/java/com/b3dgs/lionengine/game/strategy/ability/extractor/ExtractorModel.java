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
package com.b3dgs.lionengine.game.strategy.ability.extractor;

import com.b3dgs.lionengine.game.Tiled;
import com.b3dgs.lionengine.game.strategy.ability.AbilityModel;

/**
 * This is the main implementation of the extract ability. This object can be used by any kind of unit which will
 * receive the ability of extraction.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class ExtractorModel
        extends AbilityModel<ExtractorListener, ExtractorUsedServices>
        implements ExtractorServices, ExtractorListener
{
    /**
     * Extractor states.
     * 
     * @author Pierre-Alexandre (contact@b3dgs.com)
     */
    private static enum State
    {
        /** State none. */
        NONE,
        /** State go to resources. */
        GOTO_RESOURCES,
        /** State extracting. */
        EXTRACTING,
        /** State go to warehouse. */
        GOTO_WAREHOUSE,
        /** State drop off. */
        DROPOFF;
    }

    /** Tick timer rate. */
    private final double desiredFps;
    /** Resources location. */
    private final ResourceLocation resourceLocation;
    /** Current resources type. */
    private Enum<?> resourceType;
    /** Extraction state. */
    private State state;
    /** Extraction speed. */
    private double speed;
    /** Extraction progress. */
    private double progress;
    /** Last extraction progress. */
    private int lastProgress;
    /** Extraction max quantity. */
    private int quantityMax;
    /** Time to drop off. */
    private int dropOffPerSecond;

    /**
     * Constructor.
     * 
     * @param user The concerned worker reference.
     * @param desiredFps The the desired frame rate.
     */
    public ExtractorModel(ExtractorUsedServices user, int desiredFps)
    {
        super(user);
        this.desiredFps = desiredFps;
        resourceLocation = new ResourceLocation();
        resourceType = null;
        state = State.NONE;
    }

    /**
     * Action called from update extraction in goto resource state.
     */
    protected void actionGoingToResources()
    {
        if (user.canExtract())
        {
            notifyStartExtraction(resourceType, resourceLocation);
            state = State.EXTRACTING;
        }
    }

    /**
     * Action called from update extraction in extract state.
     * 
     * @param extrp extrapolation value.
     */
    protected void actionExtracting(double extrp)
    {
        progress += speed * extrp;
        final int curProgress = (int) Math.floor(progress);

        // Check increases
        if (curProgress > lastProgress)
        {
            lastProgress = curProgress;
            notifyExtracted(resourceType, curProgress);

            if (curProgress >= quantityMax)
            {
                progress = quantityMax;
                lastProgress = quantityMax;
                state = State.GOTO_WAREHOUSE;
                notifyStartCarry(resourceType, lastProgress);
            }
        }
    }

    /**
     * Action called from update extraction in goto warehouse state.
     */
    protected void actionGoingToWarehouse()
    {
        if (user.canCarry())
        {
            notifyStartDropOff(resourceType, lastProgress);
            speed = dropOffPerSecond / desiredFps;
            state = State.DROPOFF;
        }
    }

    /**
     * Action called from update extraction in drop off state.
     * 
     * @param extrp extrapolation value.
     */
    protected void actionDropingOff(double extrp)
    {
        progress -= speed * extrp;
        final int curProgress = (int) Math.floor(progress);

        // Check ended
        if (curProgress <= 0)
        {
            notifyDroppedOff(resourceType, lastProgress);
            startExtraction();
        }
    }

    /*
     * ExtractorServices
     */

    @Override
    public void updateExtraction(double extrp)
    {
        switch (state)
        {
            case GOTO_RESOURCES:
                actionGoingToResources();
                break;
            case EXTRACTING:
                actionExtracting(extrp);
                break;
            case GOTO_WAREHOUSE:
                actionGoingToWarehouse();
                break;
            case DROPOFF:
                actionDropingOff(extrp);
                break;
            default:
                break;
        }
    }

    @Override
    public void startExtraction()
    {
        dropOffPerSecond = user.getDropOffSpeed();
        quantityMax = user.getExtractionCapacity();
        state = State.GOTO_RESOURCES;
        speed = user.getExtractionSpeed() / desiredFps;
        progress = 0.0;
        lastProgress = 0;
        notifyStartGoToRessources(resourceType, resourceLocation);
    }

    @Override
    public void stopExtraction()
    {
        state = State.NONE;
        resourceType = null;
    }

    @Override
    public boolean isExtracting()
    {
        return State.EXTRACTING == state || State.DROPOFF == state || State.GOTO_WAREHOUSE == state;
    }

    @Override
    public void setResource(Extractible entity)
    {
        resourceLocation.setCoordinate(entity.getLocationInTileX(), entity.getLocationInTileY());
        resourceLocation.setSize(entity.getWidthInTile(), entity.getHeightInTile());
        resourceType = entity.getResourceType();
    }

    @Override
    public void setResource(Enum<?> type, int tx, int ty, int tw, int th)
    {
        resourceLocation.setCoordinate(tx, ty);
        resourceLocation.setSize(tw, th);
        resourceType = type;
    }

    @Override
    public Tiled getResourceLocation()
    {
        return resourceLocation;
    }

    @Override
    public Enum<?> getResourceType()
    {
        return resourceType;
    }

    /*
     * ExtractorListener
     */

    @Override
    public void notifyStartGoToRessources(Enum<?> type, Tiled resourceLocation)
    {
        for (final ExtractorListener listener : listeners)
        {
            listener.notifyStartGoToRessources(type, resourceLocation);
        }
    }

    @Override
    public void notifyStartExtraction(Enum<?> type, Tiled resourceLocation)
    {
        for (final ExtractorListener listener : listeners)
        {
            listener.notifyStartExtraction(type, resourceLocation);
        }
    }

    @Override
    public void notifyExtracted(Enum<?> type, int currentQuantity)
    {
        for (final ExtractorListener listener : listeners)
        {
            listener.notifyExtracted(type, currentQuantity);
        }
    }

    @Override
    public void notifyStartCarry(Enum<?> type, int totalQuantity)
    {
        for (final ExtractorListener listener : listeners)
        {
            listener.notifyStartCarry(type, totalQuantity);
        }
    }

    @Override
    public void notifyStartDropOff(Enum<?> type, int totalQuantity)
    {
        for (final ExtractorListener listener : listeners)
        {
            listener.notifyStartDropOff(type, totalQuantity);
        }
    }

    @Override
    public void notifyDroppedOff(Enum<?> type, int droppedQuantity)
    {
        for (final ExtractorListener listener : listeners)
        {
            listener.notifyDroppedOff(type, droppedQuantity);
        }
    }
}
