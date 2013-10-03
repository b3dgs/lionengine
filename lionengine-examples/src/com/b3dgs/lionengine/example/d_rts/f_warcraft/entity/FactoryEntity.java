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
package com.b3dgs.lionengine.example.d_rts.f_warcraft.entity;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.Context;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.ResourcesLoader;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.entity.human.FactoryEntityHuman;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.entity.orc.FactoryEntityOrc;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.type.TypeEntity;
import com.b3dgs.lionengine.game.SetupSurfaceGame;
import com.b3dgs.lionengine.game.entity.FactoryEntityGame;

/**
 * Factory entity implementation.
 */
public class FactoryEntity
        extends FactoryEntityGame<TypeEntity, SetupSurfaceGame, Entity>
{
    /** Context. */
    private Context context;

    /**
     * Constructor.
     */
    public FactoryEntity()
    {
        super(TypeEntity.class);
        loadAll(TypeEntity.values());
    }

    /**
     * Set the context.
     * 
     * @param context The context
     */
    public void setContext(Context context)
    {
        this.context = context;
    }

    /*
     * FactoryEntityGame
     */

    @Override
    public Entity createEntity(TypeEntity type)
    {
        Check.notNull(type, "The type must not be null !");
        switch (type.race)
        {
            case HUMAN:
                return FactoryEntityHuman.createEntity(type, context);
            case ORC:
                return FactoryEntityOrc.createEntity(type, context);
            case NEUTRAL:
                switch (type)
                {
                    case gold_mine:
                        return new GoldMine(context);
                    default:
                        throw new LionEngineException("Entity not found: ", type.name());
                }
            default:
                throw new LionEngineException("Entity not found: ", type.name());
        }
    }

    @Override
    protected SetupSurfaceGame createSetup(TypeEntity id)
    {
        return new SetupSurfaceGame(Media.get(ResourcesLoader.ENTITIES_DIR, id + ".xml"));
    }
}
