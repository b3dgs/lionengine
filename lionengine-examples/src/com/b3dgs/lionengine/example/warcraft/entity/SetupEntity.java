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
package com.b3dgs.lionengine.example.warcraft.entity;

import com.b3dgs.lionengine.core.ImageBuffer;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.UtilityImage;
import com.b3dgs.lionengine.example.warcraft.AppWarcraft;
import com.b3dgs.lionengine.example.warcraft.RaceType;
import com.b3dgs.lionengine.game.SetupSurfaceGame;

/**
 * Setup entity implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class SetupEntity
        extends SetupSurfaceGame
{
    /** Corpse. */
    public final ImageBuffer corpse;

    /**
     * Constructor.
     * 
     * @param config The config file.
     * @param type The entity type.
     */
    public SetupEntity(Media config, EntityType type)
    {
        super(config);
        if (type.race == RaceType.NEUTRAL)
        {
            corpse = null;
        }
        else
        {
            corpse = UtilityImage.getImageBuffer(
                    Media.get(AppWarcraft.EFFECTS_DIR, "corpse_" + type.race.asPathName() + ".png"), false);
        }
    }
}
