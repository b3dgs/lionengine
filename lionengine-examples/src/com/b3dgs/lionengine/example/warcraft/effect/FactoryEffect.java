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
package com.b3dgs.lionengine.example.warcraft.effect;

import java.util.Locale;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.example.warcraft.AppWarcraft;
import com.b3dgs.lionengine.game.SetupSurfaceGame;
import com.b3dgs.lionengine.game.effect.FactoryEffectGame;

/**
 * Factory effect implementation.
 */
public class FactoryEffect
        extends FactoryEffectGame<TypeEffect, SetupSurfaceGame, Effect>
{
    /**
     * Constructor.
     */
    public FactoryEffect()
    {
        super(TypeEffect.class);
        loadAll(TypeEffect.values());
    }

    /*
     * FactoryEffectGame
     */

    @Override
    public Effect createEffect(TypeEffect id)
    {
        switch (id)
        {
            case CONSTRUCTION:
                return new Construction(getSetup(TypeEffect.CONSTRUCTION));
            case BURNING:
                return new Burning(getSetup(TypeEffect.BURNING));
            case EXPLODE:
                return new Explode(getSetup(TypeEffect.EXPLODE));
            default:
                throw new LionEngineException("Unknown id: " + id);
        }
    }

    @Override
    protected SetupSurfaceGame createSetup(TypeEffect id)
    {
        return new SetupSurfaceGame(Media.get(AppWarcraft.EFFECTS_DIR, id.name().toLowerCase(Locale.ENGLISH)
                + AppWarcraft.CONFIG_FILE_EXTENSION));
    }
}
