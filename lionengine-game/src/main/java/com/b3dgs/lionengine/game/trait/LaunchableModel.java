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
package com.b3dgs.lionengine.game.trait;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Timing;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.handler.ObjectGame;

/**
 * Default launchable model implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class LaunchableModel
        extends TraitModel
        implements Launchable
{
    /** Transformable reference. */
    private final Transformable transformable;
    /** Launch timer. */
    private final Timing timer;
    /** Vector reference. */
    private Force vector;
    /** Launch delay. */
    private long delay;

    /**
     * Create the launchable model.
     * <p>
     * The owner must have the following {@link Trait}:
     * </p>
     * <ul>
     * <li>{@link Transformable}</li>
     * </ul>
     * 
     * @param owner The owner reference.
     * @throws LionEngineException If missing {@link Trait}.
     */
    public LaunchableModel(ObjectGame owner) throws LionEngineException
    {
        super(owner);
        transformable = owner.getTrait(Transformable.class);
        timer = new Timing();
    }

    /*
     * Launchable
     */

    @Override
    public void launch(double x, double y)
    {
        transformable.teleport(x, y);
        timer.start();
    }

    @Override
    public void update(double extrp)
    {
        if (timer.elapsed(delay) && vector != null)
        {
            vector.update(extrp);
            transformable.moveLocation(extrp, vector);
        }
    }

    @Override
    public void setVector(Force force)
    {
        vector = force;
    }

    @Override
    public void setDelay(long time)
    {
        delay = time;
    }
}
