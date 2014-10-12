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
package com.b3dgs.lionengine.game;

import java.util.HashMap;
import java.util.Map;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.core.Media;

/**
 * It performs a list of {@link SetupGame} considering their corresponding media. This way it is possible to create new
 * instances of objects related to their {@link Media} by sharing the same resources.
 * <p>
 * Sample implementation:
 * </p>
 * 
 * <pre>
 * public class Factory
 *         extends FactoryGame&lt;SetupGame&gt;
 * {
 *     public Factory()
 *     {
 *         super();
 *     }
 * 
 *     &#064;Override
 *     protected SetupGame createSetup(Media config)
 *     {
 *         return new SetupGame(config);
 *     }
 * }
 * </pre>
 * 
 * @param <S> The setup type used.
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public abstract class FactoryGame<S extends SetupGame>
{
    /** Setup error. */
    private static final String ERROR_SETUP = "Setup not found for: ";

    /** Setups list. */
    private final Map<Media, S> setups;

    /**
     * Constructor.
     */
    public FactoryGame()
    {
        setups = new HashMap<>(4);
    }

    /**
     * Create setup instance from the media.
     * 
     * @param media The setup media.
     * @return The setup instance.
     */
    protected abstract S createSetup(Media media);

    /**
     * Clear all loaded setup and their configuration.
     */
    public void clear()
    {
        for (final S setup : setups.values())
        {
            setup.clear();
        }
        setups.clear();
    }

    /**
     * Get a setup reference from its media.
     * 
     * @param media The setup media.
     * @return The setup reference.
     * @throws LionEngineException If no setup found for the media ({@link #createSetup(Media)} may have returned
     *             <code>null</code>).
     */
    public S getSetup(Media media) throws LionEngineException
    {
        if (!setups.containsKey(media))
        {
            setups.put(media, createSetup(media));
        }
        final S setup = setups.get(media);
        if (setup == null)
        {
            throw new LionEngineException(FactoryGame.ERROR_SETUP, media.getPath());
        }
        return setup;
    }
}
