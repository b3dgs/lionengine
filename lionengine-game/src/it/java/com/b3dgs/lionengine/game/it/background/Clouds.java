/*
 * Copyright (C) 2013-2020 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.b3dgs.lionengine.game.it.background;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.game.background.CloudsAbstract;

/**
 * Clouds implementation.
 */
final class Clouds extends CloudsAbstract
{
    /**
     * Constructor.
     * 
     * @param path The resources path.
     * @param screenWidth The screen width.
     * @param decY The vertical offset.
     */
    public Clouds(Media path, int screenWidth, int decY)
    {
        super(path, 160, 26, screenWidth, decY);

        setY(0, 0);
        setY(1, 30);
        setY(2, 54);
        setY(3, 73);
        setY(4, 89);
        setY(5, 100);

        setSpeed(0, -1.12);
        setSpeed(1, -0.95);
        setSpeed(2, -0.72);
        setSpeed(3, -0.5);
        setSpeed(4, -0.36);
        setSpeed(5, -0.28);
    }
}
