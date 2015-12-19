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
package com.b3dgs.lionengine.game.object;

import com.b3dgs.lionengine.LionEngineException;

/**
 * Represents something that can be delegated to perform specialized computing and reduce owner visible complexity.
 * 
 * @see ObjectGame
 */
public interface Trait
{
    /**
     * Prepare the trait.
     * 
     * @param owner The owner reference.
     * @param services The services reference.
     */
    void prepare(ObjectGame owner, Services services);

    /**
     * Get the trait owner reference.
     * 
     * @param <O> The owner type.
     * @return The trait owner reference.
     * @throws LionEngineException If cast error.
     */
    <O extends ObjectGame> O getOwner();
}
