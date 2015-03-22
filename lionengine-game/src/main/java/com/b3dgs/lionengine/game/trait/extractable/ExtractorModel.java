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
package com.b3dgs.lionengine.game.trait.extractable;

import java.util.ArrayList;
import java.util.Collection;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.game.Tiled;
import com.b3dgs.lionengine.game.object.ObjectGame;
import com.b3dgs.lionengine.game.object.Services;
import com.b3dgs.lionengine.game.trait.TraitModel;

/**
 * This is the main implementation of the extract ability. This object can be used by any kind of unit which will
 * receive the ability of extraction.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class ExtractorModel
        extends TraitModel
        implements Extractor
{
    /** Extractor listeners. */
    private final Collection<ExtractorListener> listeners;
    /** Tick timer rate. */
    private final double desiredFps;
    /** Resources location. */
    private final ResourceLocation resourceLocation;
    /** Extractor checker reference. */
    private ExtractorChecker checker;
    /** Current resources type. */
    private Enum<?> resourceType;
    /** Extraction state. */
    private ExtractorState state;
    /** Extraction capacity. */
    private int extractionCapacity;
    /** Extraction speed. */
    private double extractPerSecond;
    /** Drop off speed. */
    private double dropOffPerSecond;
    /** Extraction and drop off speed time relative. */
    private double speed;
    /** Extraction progress. */
    private double progress;
    /** Last extraction progress. */
    private int lastProgress;

    /**
     * Create a layerable model.
     * 
     * @param owner The owner reference.
     * @param services The services reference.
     * @throws LionEngineException If services are <code>null</code>.
     */
    public ExtractorModel(ObjectGame owner, Services services) throws LionEngineException
    {
        super(owner, services);
        listeners = new ArrayList<>();
        desiredFps = services.get(Integer.class).intValue();
        resourceLocation = new ResourceLocation();
        resourceType = null;
        state = ExtractorState.NONE;
    }

    /**
     * Action called from update extraction in goto resource state.
     */
    protected void actionGoingToResources()
    {
        if (checker.canExtract())
        {
            for (final ExtractorListener listener : listeners)
            {
                listener.notifyStartExtraction(resourceType, resourceLocation);
            }
            state = ExtractorState.EXTRACTING;
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
            for (final ExtractorListener listener : listeners)
            {
                listener.notifyExtracted(resourceType, curProgress);
            }

            if (curProgress >= extractionCapacity)
            {
                progress = extractionCapacity;
                lastProgress = extractionCapacity;
                state = ExtractorState.GOTO_WAREHOUSE;
                for (final ExtractorListener listener : listeners)
                {
                    listener.notifyStartCarry(resourceType, lastProgress);
                }
            }
        }
    }

    /**
     * Action called from update extraction in goto warehouse state.
     */
    protected void actionGoingToWarehouse()
    {
        if (checker.canCarry())
        {
            for (final ExtractorListener listener : listeners)
            {
                listener.notifyStartDropOff(resourceType, lastProgress);
            }
            speed = dropOffPerSecond / desiredFps;
            state = ExtractorState.DROPOFF;
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
            for (final ExtractorListener listener : listeners)
            {
                listener.notifyDroppedOff(resourceType, lastProgress);
            }
            startExtraction();
        }
    }

    /*
     * ExtractorServices
     */

    @Override
    public void prepare(Services services)
    {
        if (owner instanceof ExtractorListener)
        {
            addListener((ExtractorListener) owner);
        }
        checker = (ExtractorChecker) owner;
    }

    @Override
    public void addListener(ExtractorListener listener)
    {
        listeners.add(listener);
    }

    @Override
    public void update(double extrp)
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
        state = ExtractorState.GOTO_RESOURCES;
        speed = extractPerSecond / desiredFps;
        progress = 0.0;
        lastProgress = 0;
        for (final ExtractorListener listener : listeners)
        {
            listener.notifyStartGoToRessources(resourceType, resourceLocation);
        }
    }

    @Override
    public void stopExtraction()
    {
        state = ExtractorState.NONE;
        resourceType = null;
    }

    @Override
    public boolean isExtracting()
    {
        return ExtractorState.EXTRACTING == state || ExtractorState.DROPOFF == state
                || ExtractorState.GOTO_WAREHOUSE == state;
    }

    @Override
    public void setResource(Extractable entity)
    {
        resourceLocation.setCoordinate(entity.getInTileX(), entity.getInTileY());
        resourceLocation.setSize(entity.getInTileWidth(), entity.getInTileHeight());
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
    public void setExtractionPerSecond(double speed)
    {
        extractPerSecond = speed;
    }

    @Override
    public void setDropOffPerSecond(double speed)
    {
        dropOffPerSecond = speed;
    }

    @Override
    public void setCapacity(int capacity)
    {
        extractionCapacity = capacity;
    }

    @Override
    public int getExtractionCapacity()
    {
        return extractionCapacity;
    }

    @Override
    public double getExtractionPerSecond()
    {
        return extractPerSecond;
    }

    @Override
    public double getDropOffPerSecond()
    {
        return dropOffPerSecond;
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
}
