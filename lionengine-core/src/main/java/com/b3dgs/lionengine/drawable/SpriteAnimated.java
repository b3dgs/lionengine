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
package com.b3dgs.lionengine.drawable;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.anim.AnimState;
import com.b3dgs.lionengine.anim.Animation;
import com.b3dgs.lionengine.anim.Animator;
import com.b3dgs.lionengine.core.ImageBuffer;

/**
 * <p>
 * SpriteAnimated is an extended sprite that allows to play it using {@link Animation}. It works like a sprite excepted
 * that it renders only a part of it (current {@link Animator} frame).
 * </p>
 * <p>
 * {@link Animation} contains the first/last frame and the animation speed, considering the main first frame is on the
 * top-left sprite surface, and the last frame is on the down-right sprite surface, reading it from left to right.
 * </p>
 * <p>
 * The first frame number is {@link Animation#MINIMUM_FRAME}.
 * </p>
 * <p>
 * Example:
 * </p>
 * 
 * <pre>
 * // Load
 * final SpriteAnimated animation = Drawable.loadSpriteAnimated(UtilityMedia.get(&quot;animation.png&quot;), 7, 1);
 * animation.load(false);
 * final Animation anim = Anim.createAnimation(4, 6, 0.125, false, true);
 * animation.play(anim);
 * 
 * // Update
 * animation.updateAnimation(extrp);
 * 
 * // Render
 * animation.setMirror(false);
 * animation.render(g, 160, 300);
 * animation.setMirror(true);
 * animation.render(g, 200, 300);
 * </pre>
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see Animator
 * @see Animation
 * @see AnimState
 */
public interface SpriteAnimated
        extends Sprite, Animator
{
    /**
     * Render a specific frame on graphic output at specified coordinates.
     * 
     * @param g The graphic output.
     * @param frame The frame to render (>= {@link Animation#MINIMUM_FRAME}).
     * @param x The horizontal location.
     * @param y The vertical location.
     */
    void render(Graphic g, int frame, int x, int y);

    /**
     * Set the mirror state. The surface will not be modified, as flipping is directly done during rendering process.
     * 
     * @param mirror Set <code>true</code> if it is a mirror rendering (rendering is flipped), <code>false</code> if
     *            normal.
     */
    void setMirror(boolean mirror);

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

    /**
     * Get the number of frames.
     * 
     * @return The number of frames.
     */
    int getFramesNumber();

    /**
     * Get current frame width.
     * 
     * @return The tile width.
     */
    int getFrameWidth();

    /**
     * Get current frame height.
     * 
     * @return The tile height.
     */
    int getFrameHeight();

    /**
     * Get original frame width.
     * 
     * @return The tile width.
     */
    int getFrameWidthOriginal();

    /**
     * Get original frame height.
     * 
     * @return The tile height.
     */
    int getFrameHeightOriginal();

    /**
     * Get the representative surface of a frame.
     * 
     * @param frame The frame to get (>= {@link Animation#MINIMUM_FRAME}).
     * @return The frame's surface.
     */
    ImageBuffer getFrame(int frame);
}
