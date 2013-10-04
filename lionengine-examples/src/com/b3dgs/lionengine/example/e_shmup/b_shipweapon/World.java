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
package com.b3dgs.lionengine.example.e_shmup.b_shipweapon;

import java.io.IOException;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.core.Sequence;
import com.b3dgs.lionengine.example.e_shmup.b_shipweapon.effect.FactoryEffect;
import com.b3dgs.lionengine.example.e_shmup.b_shipweapon.effect.HandlerEffect;
import com.b3dgs.lionengine.example.e_shmup.b_shipweapon.projectile.FactoryProjectile;
import com.b3dgs.lionengine.example.e_shmup.b_shipweapon.projectile.HandlerProjectile;
import com.b3dgs.lionengine.example.e_shmup.b_shipweapon.weapon.FactoryWeapon;
import com.b3dgs.lionengine.file.FileReading;
import com.b3dgs.lionengine.file.FileWriting;
import com.b3dgs.lionengine.game.CameraGame;
import com.b3dgs.lionengine.game.WorldGame;

/**
 * World implementation.
 */
final class World
        extends WorldGame
{
    /** Camera reference. */
    private final CameraGame camera;
    /** Factory effect. */
    private final FactoryEffect factoryEffect;
    /** Handler effect. */
    private final HandlerEffect handlerEffect;
    /** Handler entity. */
    private final HandlerEntity handlerEntity;
    /** Factory projectile. */
    private final FactoryProjectile factoryProjectile;
    /** Handler projectile. */
    private final HandlerProjectile handlerProjectile;
    /** Weapon factory. */
    private final FactoryWeapon factoryWeapon;
    /** Ship reference. */
    private final Ship ship;

    /**
     * @see WorldGame#WorldGame(Sequence)
     */
    World(Sequence sequence)
    {
        super(sequence);
        camera = new CameraGame();
        factoryEffect = new FactoryEffect();
        handlerEffect = new HandlerEffect(camera);
        handlerEntity = new HandlerEntity();
        factoryProjectile = new FactoryProjectile(factoryEffect, handlerEffect);
        handlerProjectile = new HandlerProjectile(camera, handlerEntity);
        factoryWeapon = new FactoryWeapon(factoryProjectile, handlerProjectile);
        ship = new Ship(height, factoryWeapon);
        camera.setView(0, 0, width, height);
    }

    /*
     * WorldGame
     */

    @Override
    public void update(double extrp)
    {
        ship.updateControl(mouse);
        ship.update(extrp);
        handlerProjectile.update(extrp);
        handlerEffect.update(extrp);
    }

    @Override
    public void render(Graphic g)
    {
        g.clear(source);
        ship.render(g, camera);
        handlerProjectile.render(g);
        handlerEffect.render(g);
    }

    @Override
    protected void saving(FileWriting file) throws IOException
    {
        // Nothing to do
    }

    @Override
    protected void loading(FileReading file) throws IOException
    {
        // Nothing to do
    }
}
