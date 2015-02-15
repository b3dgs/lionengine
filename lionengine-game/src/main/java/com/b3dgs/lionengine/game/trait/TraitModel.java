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
import com.b3dgs.lionengine.game.handler.ObjectGame;

/**
 * Trait model base implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class TraitModel
        implements Trait
{
    /** Error trait. */
    private static final String ERROR_TRAIT = "Trait is missing: ";

    /** The owner reference. */
    private final ObjectGame owner;

    /**
     * Create a trait model.
     * 
     * @param owner The owner reference.
     */
    public TraitModel(ObjectGame owner)
    {
        this.owner = owner;
    }

    /**
     * Get the trait if available.
     * 
     * @param trait The trait to check.
     * @return The trait type.
     * @throws LionEngineException If trait is missing.
     */
    protected final <T> T getTrait(Class<T> trait) throws LionEngineException
    {
        final T instance = owner.getTrait(trait);
        if (instance == null)
        {
            throw new LionEngineException(ERROR_TRAIT, trait.getName());
        }
        return instance;
    }

    /*
     * Trait
     */

    @Override
    public ObjectGame getOwner()
    {
        return owner;
    }
}
