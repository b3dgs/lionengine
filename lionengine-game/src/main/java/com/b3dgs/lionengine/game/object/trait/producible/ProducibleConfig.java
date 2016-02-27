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
package com.b3dgs.lionengine.game.object.trait.producible;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.game.object.SizeConfig;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Represents the producible data from a configurer.
 * 
 * @see com.b3dgs.lionengine.game.object.trait.producible.Producible
 */
public final class ProducibleConfig
{
    /** Producible root node. */
    public static final String PRODUCIBLE = Configurer.PREFIX + "producible";
    /** Production steps attribute name. */
    public static final String STEPS = "steps";

    /**
     * Create the producible data from node.
     * Must be compatible with {@link SizeConfig}.
     *
     * @param configurer The configurer reference.
     * @return The action data.
     * @throws LionEngineException If unable to read node.
     */
    public static ProducibleConfig create(Configurer configurer)
    {
        final XmlNode node = configurer.getRoot();
        final SizeConfig size = SizeConfig.imports(configurer);
        final int time = node.getChild(PRODUCIBLE).readInteger(STEPS);

        return new ProducibleConfig(time, size.getWidth(), size.getHeight());
    }

    /** Production steps number. */
    private final int steps;
    /** Production width. */
    private final int width;
    /** Production height. */
    private final int height;

    /**
     * Disabled constructor.
     */
    private ProducibleConfig()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }

    /**
     * Create producible from configuration media.
     *
     * @param steps The production steps number.
     * @param width The production width.
     * @param height The production height.
     * @throws LionEngineException If error when opening the media.
     */
    private ProducibleConfig(int steps, int width, int height)
    {
        this.steps = steps;
        this.width = width;
        this.height = height;
    }

    /**
     * Get the production width.
     *
     * @return The production width.
     */
    public int getWidth()
    {
        return width;
    }

    /**
     * Get the production height.
     *
     * @return The production height.
     */
    public int getHeight()
    {
        return height;
    }

    /**
     * Get the production steps number.
     *
     * @return The production steps number.
     */
    public int getSteps()
    {
        return steps;
    }
}
