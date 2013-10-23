/*
 * Copyright (C) 2013 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.example.game.rts.ability.entity;

import com.b3dgs.lionengine.example.game.rts.ability.Context;
import com.b3dgs.lionengine.example.game.rts.ability.map.Map;
import com.b3dgs.lionengine.game.Alterable;
import com.b3dgs.lionengine.game.SetupSurfaceGame;
import com.b3dgs.lionengine.game.rts.entity.EntityRts;

/**
 * Abstract entity implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public abstract class Entity
        extends EntityRts
{
    /** Entity type. */
    public final EntityType type;
    /** Entity life. */
    public final Alterable life;
    /** Map reference. */
    protected final Map map;

    /**
     * Constructor.
     * 
     * @param type The entity type enum.
     * @param setup The setup reference.
     * @param context The context reference.
     */
    protected Entity(EntityType type, SetupSurfaceGame setup, Context context)
    {
        super(setup, context.map);
        this.type = type;
        map = context.map;
        life = new Alterable(getDataInteger("life", "attributes"));
    }

    /**
     * Get the current life.
     * 
     * @return The current life.
     */
    public int getLife()
    {
        return life.getCurrent();
    }
}
