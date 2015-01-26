/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import com.b3dgs.lionengine.core.Core;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.Updatable;
import com.b3dgs.lionengine.game.Services;
import com.b3dgs.lionengine.game.configurer.Configurer;
import com.b3dgs.lionengine.game.factory.Setup;
import com.b3dgs.lionengine.game.handler.ObjectGame;
import com.b3dgs.lionengine.game.trait.Collidable;
import com.b3dgs.lionengine.game.trait.Launcher;
import com.b3dgs.lionengine.game.trait.LauncherListener;
import com.b3dgs.lionengine.game.trait.LauncherModel;
import com.b3dgs.lionengine.game.trait.Transformable;
import com.b3dgs.lionengine.game.trait.TransformableModel;

/**
 * Weapon implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
class Weapon
        extends ObjectGame
        implements Updatable, LauncherListener
{
    /** Media. */
    public static final Media PULSE_CANNON = Core.MEDIA.create("PulseCannon.xml");

    /** Transformable model. */
    private final Transformable transformable;
    /** Launcher model. */
    private final Launcher launcher;
    /** Owner localizable. */
    private Transformable ownerLocalizable;
    /** Owner collidable. */
    private Collidable ownerCollidable;

    /**
     * Constructor.
     * 
     * @param setup The setup reference.
     * @param context The context reference.
     */
    public Weapon(Setup setup, Services context)
    {
        super(setup, context);

        final Configurer configurer = setup.getConfigurer();

        transformable = new TransformableModel(this, configurer);
        addTrait(transformable);

        launcher = new LauncherModel(this, configurer, context);
        launcher.addListener(this);
        addTrait(launcher);
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
