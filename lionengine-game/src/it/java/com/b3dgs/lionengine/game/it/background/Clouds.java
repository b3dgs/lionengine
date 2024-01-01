/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
    private static final int LINE_WIDTH = 160;
    private static final int LINE_HEIGHT = 26;
    private static final int[] LINES_OFFSET =
    {
        0, 30, 54, 75, 90, 101
    };
    private static final double[] LINES_SPEED =
    {
        -1.5, -1.3, -1.0, -0.7, -0.5, -0.3
    };

    /**
     * Constructor.
     * 
     * @param path The resources path.
     * @param screenWidth The screen width.
     * @param decY The vertical offset.
     */
    Clouds(Media path, int screenWidth, int decY)
    {
        super(path, LINE_WIDTH, LINE_HEIGHT, screenWidth, decY);

        for (int i = 0; i < LINES_OFFSET.length; i++)
        {
            setY(i, LINES_OFFSET[i]);
        }

        for (int i = 0; i < LINES_SPEED.length; i++)
        {
            setSpeed(i, LINES_SPEED[i]);
        }
    }
}
