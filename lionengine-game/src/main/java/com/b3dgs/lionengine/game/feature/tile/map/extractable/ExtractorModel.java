/*
 * Copyright (C) 2013-2022 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.b3dgs.lionengine.game.feature.tile.map.extractable;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.ListenableModel;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.Tiled;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;

/**
 * This is the main implementation of the extract ability. This object can be used by any kind of unit which will
 * receive the ability of extraction.
 */
public class ExtractorModel extends FeatureModel implements Extractor, Recyclable
{
    /** Extractor listeners. */
    private final ListenableModel<ExtractorListener> listenable = new ListenableModel<>();
    /** Resources location. */
    private ResourceLocation resourceLocation;
    /** Extractor checker reference. */
    private ExtractorChecker checker = new ExtractorChecker()
    {
        @Override
        public boolean canExtract()
        {
            return true;
        }

        @Override
        public boolean canCarry()
        {
            return true;
        }
    };
    /** Current resource object. */
    private Extractable extractable;
    /** Current resources type. */
    private String resourceType;
    /** Extraction state. */
    private ExtractorState state;
    /** Extraction capacity. */
    private int extractionCapacity;
    /** Extraction count per tick. */
    private double extractPerTick;
    /** Drop off count per tick. */
    private double dropOffPerTick;
    /** Current resources count. */
    private double resourceCount;
    /** Last extraction count. */
    private int resourceCountLast;

    /**
     * Create feature.
     * <p>
     * The {@link Configurer} can provide a valid {@link ExtractorConfig}.
     * </p>
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public ExtractorModel(Services services, Setup setup)
    {
        super(services, setup);

        if (setup.hasNode(ExtractorConfig.NODE_EXTRACTOR))
        {
            final ExtractorConfig config = ExtractorConfig.imports(setup);
            extractPerTick = config.getExtract();
            dropOffPerTick = config.getDropOff();
            extractionCapacity = config.getCapacity();
        }
    }

    /**
     * Action called from update extraction in goto resource state.
     */
    protected void actionGoingToResources()
    {
        if (checker.canExtract())
        {
            for (int i = 0; i < listenable.size(); i++)
            {
                listenable.get(i).notifyStartExtraction(resourceType, resourceLocation);
            }
            state = ExtractorState.EXTRACTING;
        }
    }

    /**
     * Action called from update extraction in extract state.
     */
    protected void actionExtracting()
    {
        if (extractable == null || extractable.getResourceQuantity() > 0)
        {
            if (extractable != null)
            {
                resourceCount += Math.min(extractable.getResourceQuantity(), extractPerTick);
            }
            else
            {
                resourceCount += extractPerTick;
            }
            final int curProgress = Math.min((int) Math.floor(resourceCount), extractionCapacity);

            // Check increases
            if (curProgress > resourceCountLast)
            {
                extract(curProgress);
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
            for (int i = 0; i < listenable.size(); i++)
            {
                listenable.get(i).notifyStartDropOff(resourceType, resourceCountLast);
            }
            state = ExtractorState.DROPOFF;
        }
    }

    /**
     * Action called from update extraction in drop off state.
     */
    protected void actionDropingOff()
    {
        resourceCount -= dropOffPerTick;
        final int curProgress = (int) Math.floor(resourceCount);

        for (int i = 0; i < listenable.size(); i++)
        {
            listenable.get(i).notifyDroppedOff(resourceType, curProgress);
        }

        // Check ended
        if (curProgress <= 0)
        {
            startExtraction();
        }
    }

    /**
     * Perform extraction.
     * 
     * @param curProgress Current extraction progress.
     */
    private void extract(int curProgress)
    {
        if (extractable != null)
        {
            extractable.extractResource(curProgress - resourceCountLast);
        }
        for (int i = 0; i < listenable.size(); i++)
        {
            listenable.get(i).notifyExtracted(resourceType, curProgress);
        }
        resourceCountLast = curProgress;

        if (curProgress >= extractionCapacity)
        {
            resourceCount = extractionCapacity;
            resourceCountLast = extractionCapacity;
            state = ExtractorState.GOTO_WAREHOUSE;

            for (int i = 0; i < listenable.size(); i++)
            {
                listenable.get(i).notifyStartCarry(resourceType, resourceCountLast);
            }
        }
    }

    /*
     * ExtractorServices
     */

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        if (provider instanceof ExtractorListener)
        {
            addListener((ExtractorListener) provider);
        }
        if (provider instanceof ExtractorChecker)
        {
            checker = (ExtractorChecker) provider;
        }
    }

    @Override
    public void checkListener(Object listener)
    {
        super.checkListener(listener);

        if (listener instanceof ExtractorListener)
        {
            addListener((ExtractorListener) listener);
        }
    }

    @Override
    public void addListener(ExtractorListener listener)
    {
        listenable.addListener(listener);
    }

    @Override
    public void removeListener(ExtractorListener listener)
    {
        listenable.removeListener(listener);
    }

    @Override
    public void setChecker(ExtractorChecker checker)
    {
        Check.notNull(checker);

        this.checker = checker;
    }

    @Override
    public void update(double extrp)
    {
        switch (state)
        {
            case NONE:
                // Nothing to do
                break;
            case GOTO_RESOURCES:
                actionGoingToResources();
                break;
            case EXTRACTING:
                actionExtracting();
                break;
            case GOTO_WAREHOUSE:
                actionGoingToWarehouse();
                break;
            case DROPOFF:
                actionDropingOff();
                break;
            default:
                throw new LionEngineException(state);
        }
    }

    @Override
    public void startExtraction()
    {
        state = ExtractorState.GOTO_RESOURCES;
        resourceCount = 0.0;
        resourceCountLast = 0;

        for (int i = 0; i < listenable.size(); i++)
        {
            listenable.get(i).notifyStartGoToRessources(resourceType, resourceLocation);
        }
    }

    @Override
    public void stopExtraction()
    {
        state = ExtractorState.NONE;
        resourceType = null;

        for (int i = 0; i < listenable.size(); i++)
        {
            listenable.get(i).notifyStopped();
        }
    }

    @Override
    public boolean isExtracting()
    {
        return ExtractorState.EXTRACTING == state
               || ExtractorState.DROPOFF == state
               || ExtractorState.GOTO_WAREHOUSE == state;
    }

    @Override
    public void setResource(Extractable extractable)
    {
        this.extractable = extractable;
        setResource(extractable.getResourceType(), extractable);
    }

    @Override
    public void setResource(String type, Tiled tiled)
    {
        setResource(type, tiled.getInTileX(), tiled.getInTileY(), tiled.getInTileWidth(), tiled.getInTileHeight());
    }

    @Override
    public void setResource(String type, int tx, int ty, int tw, int th)
    {
        resourceLocation = new ResourceLocation(tx, ty, tw, th);
        resourceType = type;
    }

    @Override
    public void setExtractionSpeed(double count)
    {
        extractPerTick = count;
    }

    @Override
    public void setDropOffSpeed(double count)
    {
        dropOffPerTick = count;
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
    public double getExtractionSpeed()
    {
        return extractPerTick;
    }

    @Override
    public double getDropOffSpeed()
    {
        return dropOffPerTick;
    }

    @Override
    public Tiled getResourceLocation()
    {
        return resourceLocation;
    }

    @Override
    public String getResourceType()
    {
        return resourceType;
    }

    /*
     * Recyclable
     */

    @Override
    public void recycle()
    {
        state = ExtractorState.NONE;
        resourceCount = 0.0;
        resourceCountLast = 0;
    }
}
