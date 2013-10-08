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
package com.b3dgs.lionengine.example.warcraft.projectile;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.example.warcraft.ResourcesLoader;
import com.b3dgs.lionengine.example.warcraft.type.TypeProjectile;
import com.b3dgs.lionengine.game.SetupSurfaceGame;
import com.b3dgs.lionengine.game.projectile.FactoryProjectileGame;
import com.b3dgs.lionengine.game.purview.model.ConfigurableModel;

/**
 * Factory projectile implementation.
 */
public final class FactoryProjectile
        extends FactoryProjectileGame<TypeProjectile, Projectile, SetupSurfaceGame>
{
    /**
     * Constructor.
     */
    public FactoryProjectile()
    {
        super(TypeProjectile.class);
        loadAll(TypeProjectile.values());
    }

    /*
     * FactoryProjectileGame
     */

    @Override
    public Projectile createProjectile(TypeProjectile type)
    {
        switch (type)
        {
            case spear:
                return new Spear(getSetup(TypeProjectile.spear));
            case arrow:
                return new Arrow(getSetup(TypeProjectile.arrow));
            default:
                throw new LionEngineException("Projectile not found: " + type.name());
        }
    }

    @Override
    protected SetupSurfaceGame createSetup(TypeProjectile id)
    {
        return new SetupSurfaceGame(new ConfigurableModel(), Media.get(ResourcesLoader.PROJECTILES_DIR, id + ".xml"),
                false);
    }
}
