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
package com.b3dgs.lionengine.example.tyrian.entity.ship;

import com.b3dgs.lionengine.Mouse;
import com.b3dgs.lionengine.core.Click;
import com.b3dgs.lionengine.core.UtilityMath;
import com.b3dgs.lionengine.example.tyrian.effect.FactoryEffect;
import com.b3dgs.lionengine.example.tyrian.effect.HandlerEffect;
import com.b3dgs.lionengine.example.tyrian.entity.Entity;
import com.b3dgs.lionengine.example.tyrian.weapon.FactoryWeapon;
import com.b3dgs.lionengine.example.tyrian.weapon.Weapon;
import com.b3dgs.lionengine.example.tyrian.weapon.WeaponType;
import com.b3dgs.lionengine.game.Alterable;
import com.b3dgs.lionengine.game.CameraGame;
import com.b3dgs.lionengine.game.SetupSurfaceGame;

/**
 * Ship base implementation.
 */
public class Ship
        extends Entity
{
    /** Ship energy. */
    private final Alterable energy;
    /** Weapon front. */
    private final Weapon weaponFront;
    /** Weapon rear. */
    private final Weapon weaponRear;
    /** Old offset. */
    private int offsetOldX;

    /**
     * @param setup The setup reference.
     * @param factoryEffect The effect factory reference.
     * @param handlerEffect The effect handler reference.
     * @param factoryWeapon The factory weapon reference.
     */
    public Ship(SetupSurfaceGame setup, FactoryEffect factoryEffect, HandlerEffect handlerEffect,
            FactoryWeapon factoryWeapon)
    {
        super(setup, factoryEffect, handlerEffect);
        energy = new Alterable(1000);
        weaponFront = factoryWeapon.createLauncher(WeaponType.PULSE_CANNON);
        weaponFront.setOwner(this);
        weaponRear = factoryWeapon.createLauncher(WeaponType.MISSILE_LAUNCHER);
        weaponRear.setOwner(this);
        setSize(24, 28);
        setLocation(0, -216);
    }

    /**
     * Update the ship.
     * 
     * @param extrp The extrapolation value.
     * @param mouse The mouse reference.
     * @param camera The camera reference
     * @param screenHeight The screen height.
     */
    public void update(double extrp, Mouse mouse, CameraGame camera, int screenHeight)
    {
        offsetOldX = getLocationOffsetX();
        final double destX = mouse.getOnWindowX() - getWidth() / 2 + camera.getLocationIntX();
        final double destY = screenHeight - mouse.getOnWindowY();
        final double x = UtilityMath.curveValue(getLocationOffsetX(), destX, 3.0);
        final double y = UtilityMath.curveValue(getLocationOffsetY(), destY, 3.0);
        final double ox = UtilityMath.fixBetween(x, 0, camera.getViewWidth());
        final double oy = UtilityMath.fixBetween(y, getHeight() / 2, camera.getViewHeight());
        setLocationOffset(ox, oy);

        moveLocation(extrp, 0.0, 1.0);
        updateTileOffset();
        updateCollision();

        energy.increase(200);
        if (mouse.hasClickedOnce(Click.RIGHT))
        {
            weaponFront.level.increase(1);
        }
        if (mouse.hasClicked(Click.LEFT))
        {
            weaponFront.launch(energy);
            weaponRear.launch(energy);
        }
    }

    /**
     * Get energy percent.
     * 
     * @return The energy percent.
     */
    public int getEnergyPercent()
    {
        return energy.getPercent();
    }

    /**
     * Update the ship sprite offset depending of its movement.
     */
    private void updateTileOffset()
    {
        final double diffX = getLocationOffsetX() - offsetOldX;
        if (diffX >= -1 && diffX <= 1)
        {
            setTileOffset(2);
        }
        else if (diffX < -3)
        {
            setTileOffset(0);
        }
        else if (diffX >= -3 && diffX < 1)
        {
            setTileOffset(1);
        }
        else if (diffX > 1 && diffX <= 3)
        {
            setTileOffset(3);
        }
        else if (diffX > 3)
        {
            setTileOffset(4);
        }
    }
}
