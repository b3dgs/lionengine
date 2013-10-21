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
package com.b3dgs.lionengine.example.lionheart.entity.swamp;

import com.b3dgs.lionengine.example.lionheart.Level;
import com.b3dgs.lionengine.example.lionheart.Sfx;
import com.b3dgs.lionengine.example.lionheart.effect.EffectType;
import com.b3dgs.lionengine.example.lionheart.entity.player.Valdyn;
import com.b3dgs.lionengine.game.SetupSurfaceRasteredGame;

/**
 * Sword4 item. Fourth level of the sword.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class Sword4
        extends EntityItem
{
    /**
     * Constructor.
     * 
     * @param setup The setup reference.
     * @param level The level reference.
     */
    public Sword4(SetupSurfaceRasteredGame setup, Level level)
    {
        super(setup, level, EntitySwampType.SWORD4, EffectType.TAKEN);
    }

    /*
     * EntityItem
     */

    @Override
    protected void onTaken(Valdyn entity)
    {
        entity.stats.setSwordLevel(4);
        Sfx.ITEM_TAKEN.play();
    }
}
