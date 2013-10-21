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
package com.b3dgs.lionengine.example.tyrian;

import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.Sprite;
import com.b3dgs.lionengine.example.tyrian.entity.ship.Ship;
import com.b3dgs.lionengine.game.Bar;

/**
 * Hud implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
final class Hud
{
    /** Hud background. */
    private final Sprite hud;
    /** Weapon front rate. */
    private final Bar weaponFront;
    /** Ship reference. */
    private Ship ship;

    /**
     * Constructor.
     */
    Hud()
    {
        hud = Drawable.loadSprite(Media.get("sprites", "hud.png"));
        hud.load(false);
        weaponFront = new Bar(11, 96);
        weaponFront.setLocation(268, 11);
        weaponFront.setWidthPercent(100);
        weaponFront.setColorGradient(268, 11, ColorRgba.YELLOW, 279, 107, ColorRgba.RED);
    }

    /**
     * Update the hud.
     * 
     * @param extrp The extrapolation value.
     */
    public void update(double extrp)
    {
        weaponFront.setHeightPercent(ship.getEnergyPercent());
    }

    /**
     * Render the hud.
     * 
     * @param g The graphic output.
     */
    public void render(Graphic g)
    {
        hud.render(g, 0, 0);
        weaponFront.render(g);
    }

    /**
     * Set the ship reference.
     * 
     * @param ship The ship reference.
     */
    public void setShip(Ship ship)
    {
        this.ship = ship;
    }
}
