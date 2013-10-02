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

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.SpriteTiled;
import com.b3dgs.lionengine.example.e_shmup.b_shipweapon.weapon.FactoryWeapon;
import com.b3dgs.lionengine.example.e_shmup.b_shipweapon.weapon.Weapon;
import com.b3dgs.lionengine.example.e_shmup.b_shipweapon.weapon.WeaponType;
import com.b3dgs.lionengine.game.CameraGame;
import com.b3dgs.lionengine.game.CollisionData;
import com.b3dgs.lionengine.game.SetupGame;
import com.b3dgs.lionengine.game.entity.EntityGame;
import com.b3dgs.lionengine.input.Mouse;
import com.b3dgs.lionengine.utility.UtilityMath;

/**
 * Ship implementation.
 */
final class Ship
        extends EntityGame
{
    /** Ship surface. */
    private final SpriteTiled sprite;
    /** Height. */
    private final int screenHeight;
    /** Weapon front. */
    private final Weapon weaponFront;
    /** Weapon rear. */
    private final Weapon weaponRear;
    /** Old offset. */
    private int offsetOldX;
    /** Tile offset. */
    private int tileOffset;

    /**
     * Constructor.
     * 
     * @param screenHeight The screen height.
     * @param factoryWeapon The weapon factory.
     */
    public Ship(int screenHeight, FactoryWeapon factoryWeapon)
    {
        super(new SetupGame(Media.get("ships", "gencore_phoenix.xml")));
        this.screenHeight = screenHeight;
        sprite = Drawable.loadSpriteTiled(Media.get("ships", "gencore_phoenix.png"), 24, 28);
        sprite.load(false);
        weaponFront = factoryWeapon.createLauncher(WeaponType.PULSE_CANNON);
        weaponFront.setOwner(this);
        weaponRear = factoryWeapon.createLauncher(WeaponType.MISSILE_LAUNCHER);
        weaponRear.setOwner(this);
        setSize(24, 28);
        setCollision(new CollisionData(12, -28, 24, 28, false));
    }

    /**
     * Update the ship.
     * 
     * @param mouse The mouse reference.
     */
    public void updateControl(Mouse mouse)
    {
        offsetOldX = getLocationOffsetX();
        final double destX = mouse.getOnWindowX() - getWidth() / 2;
        final double destY = screenHeight - mouse.getOnWindowY() + getHeight() / 2;
        final double x = UtilityMath.curveValue(getLocationOffsetX(), destX, 3.0);
        final double y = UtilityMath.curveValue(getLocationOffsetY(), destY, 3.0);
        setLocationOffset(x, y);
        if (mouse.hasClicked(Mouse.LEFT))
        {
            weaponFront.launch();
            weaponRear.launch();
        }
    }

    /**
     * Render the ship.
     * 
     * @param g The graphic output.
     * @param camera The camera reference.
     */
    public void render(Graphic g, CameraGame camera)
    {
        final int x = camera.getViewpointX(getLocationIntX()) + getLocationOffsetX();
        final int y = camera.getViewpointY(getLocationIntY()) - getLocationOffsetY();
        sprite.render(g, tileOffset, x, y);
    }

    /**
     * Update the ship sprite offset depending of its movement.
     */
    private void updateTileOffset()
    {
        final double diffX = getLocationOffsetX() - offsetOldX;
        if (diffX >= -1 && diffX <= 1)
        {
            tileOffset = 2;
        }
        else if (diffX < -3)
        {
            tileOffset = 0;
        }
        else if (diffX >= -3 && diffX < 1)
        {
            tileOffset = 1;
        }
        else if (diffX > 1 && diffX <= 3)
        {
            tileOffset = 3;
        }
        else if (diffX > 3)
        {
            tileOffset = 4;
        }
    }

    /*
     * EntityGame
     */

    @Override
    public void update(double extrp)
    {
        updateTileOffset();
        updateCollision();
    }
}
