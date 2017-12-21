/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.example.game.projectile;

import com.b3dgs.lionengine.Localizable;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.game.feature.FeaturableModel;
import com.b3dgs.lionengine.game.feature.RefreshableModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.TransformableModel;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.launchable.Launchable;
import com.b3dgs.lionengine.game.feature.launchable.LaunchableListener;
import com.b3dgs.lionengine.game.feature.launchable.Launcher;
import com.b3dgs.lionengine.game.feature.launchable.LauncherModel;

/**
 * Weapon implementation.
 */
class Weapon extends FeaturableModel implements LaunchableListener
{
    /** Media. */
    public static final Media PULSE_CANNON = Medias.create("PulseCannon.xml");

    private final Launcher launcher;
    private Transformable ownerLocalizable;
    private Collidable ownerCollidable;

    /**
     * Constructor.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     */
    public Weapon(Services services, Setup setup)
    {
        super();

        final Transformable transformable = addFeatureAndGet(new TransformableModel());
        launcher = addFeatureAndGet(new LauncherModel(services, setup));

        addFeature(new RefreshableModel(extrp -> transformable.teleport(ownerLocalizable.getX(),
                                                                        ownerLocalizable.getY())));
    }

    /**
     * Set the owner reference.
     * 
     * @param owner The owner reference.
     */
    public void setOwner(Ship owner)
    {
        ownerLocalizable = owner.getFeature(Transformable.class);
        ownerCollidable = owner.getFeature(Collidable.class);
    }

    /**
     * Set the weapon position offset.
     * 
     * @param x The horizontal offset.
     * @param y The vertical offset.
     */
    public void setOffset(int x, int y)
    {
        launcher.setOffset(x, y);
    }

    /**
     * Fire with the specified configuration.
     * 
     * @param target The launch target.
     */
    public void fire(Localizable target)
    {
        launcher.fire(target);
    }

    @Override
    public void notifyFired(Launchable launchable)
    {
        launchable.getFeature(Collidable.class).setGroup(ownerCollidable.getGroup().intValue());
    }
}
