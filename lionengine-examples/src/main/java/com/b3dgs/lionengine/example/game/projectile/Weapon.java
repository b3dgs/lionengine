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
package com.b3dgs.lionengine.example.game.projectile;

import com.b3dgs.lionengine.Localizable;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.game.object.ObjectGame;
import com.b3dgs.lionengine.game.object.Services;
import com.b3dgs.lionengine.game.object.Setup;
import com.b3dgs.lionengine.game.trait.collidable.Collidable;
import com.b3dgs.lionengine.game.trait.launchable.Launcher;
import com.b3dgs.lionengine.game.trait.launchable.LauncherListener;
import com.b3dgs.lionengine.game.trait.launchable.LauncherModel;
import com.b3dgs.lionengine.game.trait.transformable.Transformable;
import com.b3dgs.lionengine.game.trait.transformable.TransformableModel;

/**
 * Weapon implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
class Weapon extends ObjectGame implements Updatable, LauncherListener
{
    /** Media. */
    public static final Media PULSE_CANNON = Medias.create("PulseCannon.xml");

    /** Transformable model. */
    private final Transformable transformable = addTrait(new TransformableModel());
    /** Launcher model. */
    private final Launcher launcher = addTrait(new LauncherModel());
    /** Owner localizable. */
    private Transformable ownerLocalizable;
    /** Owner collidable. */
    private Collidable ownerCollidable;

    /**
     * Constructor.
     * 
     * @param setup The setup reference.
     * @param services The services reference.
     */
    public Weapon(Setup setup, Services services)
    {
        super(setup, services);
    }

    /**
     * Set the owner reference.
     * 
     * @param owner The owner reference.
     */
    public void setOwner(Ship owner)
    {
        ownerLocalizable = owner.getTrait(Transformable.class);
        ownerCollidable = owner.getTrait(Collidable.class);
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
    public void update(double extrp)
    {
        transformable.teleport(ownerLocalizable.getX(), ownerLocalizable.getY());
    }

    @Override
    public void notifyFired(ObjectGame object)
    {
        object.getTrait(Collidable.class).addIgnore(ownerCollidable);
    }
}
