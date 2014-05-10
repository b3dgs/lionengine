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

import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.Keyboard;
import com.b3dgs.lionengine.core.Loader;
import com.b3dgs.lionengine.core.Sequence;
import com.b3dgs.lionengine.game.CameraGame;

/**
 * Scene implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see com.b3dgs.lionengine.example.core.minimal
 */
final class Scene
        extends Sequence
{
    /** Keyboard reference. */
    private final Keyboard keyboard;
    /** Camera. */
    private final CameraGame camera;
    /** Factory launcher. */
    private final FactoryLauncher factoryLauncher;
    /** Factory projectile. */
    private final FactoryProjectile factoryProjectile;
    /** Handler entity. */
    private final HandlerEntity handlerEntity;
    /** Handler projectile. */
    private final HandlerProjectile handlerProjectile;
    /** Launcher. */
    private final Launcher canon1;
    /** Launcher. */
    private final Launcher canon2;
    /** Entity 1. */
    private final Entity entity1;
    /** Entity 2. */
    private final Entity entity2;
    /** Location entity 1. */
    private double location;

    /**
     * Constructor.
     * 
     * @param loader The loader reference.
     */
    Scene(Loader loader)
    {
        super(loader, new Resolution(320, 240, 60));
        keyboard = getInputDevice(Keyboard.class);
        camera = new CameraGame();
        factoryProjectile = new FactoryProjectile();
        handlerEntity = new HandlerEntity(camera);
        handlerProjectile = new HandlerProjectile(camera, handlerEntity);
        factoryLauncher = new FactoryLauncher(factoryProjectile, handlerProjectile);
        canon1 = factoryLauncher.create(PulseCannon.class);
        canon2 = factoryLauncher.create(PulseCannon.class);
        entity1 = new Entity();
        entity2 = new Entity();
    }

    /*
     * Sequence
     */

    @Override
    protected void load()
    {
        camera.setView(0, 0, getWidth(), getHeight());
        canon1.setOwner(entity1);
        canon1.setAdaptative(true);
        canon2.setOwner(entity2);
        handlerEntity.add(entity1);
        handlerEntity.add(entity2);
    }

    @Override
    protected void update(double extrp)
    {
        if (keyboard.isPressed(Keyboard.ESCAPE))
        {
            end();
        }

        location += 1.0;

        entity1.setLocation(100 + UtilMath.cos(location * 1.5) * 70, 180 + UtilMath.sin(location * 2) * 40);
        entity2.setLocation(100 + UtilMath.cos(location) * 90, 60 + UtilMath.sin(location * 1.3) * 30);

        canon1.launch(entity2);
        canon2.launch(entity1);

        entity1.update(extrp);
        entity2.update(extrp);
        handlerEntity.update(extrp);
        handlerProjectile.update(extrp);
    }

    @Override
    protected void render(Graphic g)
    {
        g.clear(0, 0, getWidth(), getHeight());
        handlerEntity.render(g);
        handlerProjectile.render(g);
    }
}
