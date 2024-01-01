/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.graphic.engine;

import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.UtilTests;
import com.b3dgs.lionengine.graphic.Graphic;

/**
 * Sequence with screen not ready on rendering.
 */
public final class SequenceScreenNotReady extends Sequence
{
    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(SequenceScreenNotReady.class);

    private final CountDownLatch waitUpdate;
    private final CountDownLatch waitScreenUnready;

    /**
     * Create sequence.
     * 
     * @param context The context reference.
     * @param waitUpdate The wait.
     * @param waitScreenUnready The wait.
     */
    public SequenceScreenNotReady(Context context, CountDownLatch waitUpdate, CountDownLatch waitScreenUnready)
    {
        super(context, UtilTests.RESOLUTION_320_240, new LoopUnlocked());

        this.waitUpdate = waitUpdate;
        this.waitScreenUnready = waitScreenUnready;
    }

    @Override
    public void load()
    {
        // Mock
    }

    @Override
    public void update(double extrp)
    {
        waitUpdate.countDown();
        try
        {
            waitScreenUnready.await();
        }
        catch (final InterruptedException exception)
        {
            LOGGER.error("Interrupted !", exception);
        }
    }

    @Override
    public void render(Graphic g)
    {
        // Mock
    }
}
