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
package com.b3dgs.lionengine.example.c_platform.e_lionheart.effect;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.AppLionheart;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.landscape.LandscapeType;
import com.b3dgs.lionengine.game.SetupSurfaceRasteredGame;
import com.b3dgs.lionengine.game.effect.FactoryEffectGame;

/**
 * Factory effect implementation.
 */
public class FactoryEffect
        extends FactoryEffectGame<EffectType, SetupSurfaceRasteredGame, Effect>
{
    /** Handler effect reference. */
    private final HandlerEffect handlerEffect;
    /** Landscape used. */
    private LandscapeType landscape;

    /**
     * Constructor.
     * 
     * @param handlerEffect The handler effect reference.
     */
    public FactoryEffect(HandlerEffect handlerEffect)
    {
        super(EffectType.class);
        this.handlerEffect = handlerEffect;
    }

    /**
     * Start an effect an the specified location.
     * 
     * @param id The effect id.
     * @param x The horizontal location.
     * @param y The vertical location.
     */
    public void startEffect(EffectType id, int x, int y)
    {
        final Effect effect = createEffect(id);
        effect.start(x, y);
        handlerEffect.add(effect);
    }

    /**
     * Set the landscape type used.
     * 
     * @param landscape The landscape type used.
     */
    public void setLandscape(LandscapeType landscape)
    {
        this.landscape = landscape;
    }

    /*
     * FactoryEffectGame
     */

    @Override
    public Effect createEffect(EffectType id)
    {
        switch (id)
        {
            case TAKEN:
                return new Taken(getSetup(EffectType.TAKEN));
            case EXPLODE:
                return new ExplodeBig(getSetup(EffectType.EXPLODE));
            default:
                throw new LionEngineException("Unknown id: " + id);
        }
    }

    @Override
    protected SetupSurfaceRasteredGame createSetup(EffectType id)
    {
        final Media raster;
        if (AppLionheart.RASTER_ENABLED)
        {
            raster = Media.get(AppLionheart.RASTERS_DIR, landscape.getRaster());
        }
        else
        {
            raster = null;
        }
        return new SetupSurfaceRasteredGame(Media.get(AppLionheart.EFFECTS_DIR, id.toString()
                + AppLionheart.CONFIG_FILE_EXTENSION), raster, false);
    }
}
