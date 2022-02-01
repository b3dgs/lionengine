/*
 * Copyright (C) 2013-2022 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.graphic.drawable;

import com.b3dgs.lionengine.Animator;
import com.b3dgs.lionengine.SurfaceTile;

/**
 * <p>
 * SpriteAnimated is an extended sprite that allows to play it using {@link com.b3dgs.lionengine.Animation}. It
 * works like a sprite excepted that it renders only a part of it (current {@link Animator} frame).
 * </p>
 * <p>
 * {@link com.b3dgs.lionengine.Animation} contains the first/last frame and the animation speed, considering the
 * main first frame is on the top-left sprite surface, and the last frame is on the down-right sprite surface, reading
 * it from left to right.
 * </p>
 * <p>
 * The first frame number is {@link com.b3dgs.lionengine.Animation#MINIMUM_FRAME}.
 * </p>
 * 
 * @see Animator
 * @see com.b3dgs.lionengine.Animation
 * @see com.b3dgs.lionengine.AnimState
 */
public interface SpriteAnimated extends Sprite, SurfaceTile, Animator
{
    /**
     * Get the number of horizontal frames.
     * 
     * @return The number of horizontal frames.
     */
    int getFramesHorizontal();

    /**
     * Get the number of vertical frames.
     * 
     * @return The number of vertical frames.
     */
    int getFramesVertical();
}
