/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.object;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.game.Configurer;

/**
 * Represents the frames data from a configurer node.
 */
public final class FramesConfig
{
    /** Frames node name. */
    public static final String FRAMES = Configurer.PREFIX + "frames";
    /** Frames horizontal node name. */
    public static final String FRAMES_HORIZONTAL = "horizontal";
    /** Frames vertical node name. */
    public static final String FRAMES_VERTICAL = "vertical";

    /**
     * Create the frames node.
     * 
     * @param configurer The configurer reference.
     * @return The frames data.
     * @throws LionEngineException If unable to read node or not a valid integer.
     */
    public static FramesConfig create(Configurer configurer)
    {
        final int horizontals = configurer.getInteger(FramesConfig.FRAMES_HORIZONTAL, FramesConfig.FRAMES);
        final int verticals = configurer.getInteger(FramesConfig.FRAMES_VERTICAL, FramesConfig.FRAMES);

        return new FramesConfig(horizontals, verticals);
    }

    /** The number of horizontal frames. */
    private final int horizontalFrames;
    /** The number of vertical frames. */
    private final int verticalFrames;

    /**
     * Disabled constructor.
     */
    private FramesConfig()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }

    /**
     * Constructor.
     * 
     * @param horizontalFrames The horizontal frames value.
     * @param verticalFrames The vertical frames value.
     */
    private FramesConfig(int horizontalFrames, int verticalFrames)
    {
        this.horizontalFrames = horizontalFrames;
        this.verticalFrames = verticalFrames;
    }

    /**
     * Get the number of horizontal frames.
     * 
     * @return The number of horizontal frames.
     */
    public int getHorizontal()
    {
        return horizontalFrames;
    }

    /**
     * Get the number of vertical frames.
     * 
     * @return The number of vertical frames.
     */
    public int getVertical()
    {
        return verticalFrames;
    }
}
