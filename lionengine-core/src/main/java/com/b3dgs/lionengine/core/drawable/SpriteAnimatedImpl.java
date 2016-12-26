/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.core.drawable;

import com.b3dgs.lionengine.AnimState;
import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.Animator;
import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.ImageBuffer;
import com.b3dgs.lionengine.graphic.SpriteAnimated;

/**
 * Animated sprite implementation.
 */
final class SpriteAnimatedImpl extends SpriteImpl implements SpriteAnimated
{
    /** Animator reference. */
    private final Animator animator = new AnimatorImpl();
    /** Number of horizontal frames. */
    private final int horizontalFrames;
    /** Number of vertical frames. */
    private final int verticalFrames;
    /** Frame offsets x. */
    private int frameOffsetX;
    /** Frame offsets y. */
    private int frameOffsetY;

    /**
     * Internal constructor.
     * 
     * @param media The sprite media.
     * @param horizontalFrames The number of horizontal frames.
     * @param verticalFrames The number of vertical frames.
     * @throws LionEngineException If arguments are invalid or image cannot be read.
     */
    SpriteAnimatedImpl(Media media, int horizontalFrames, int verticalFrames)
    {
        super(media);

        Check.superiorStrict(horizontalFrames, 0);
        Check.superiorStrict(verticalFrames, 0);

        this.horizontalFrames = horizontalFrames;
        this.verticalFrames = verticalFrames;
        frameOffsetX = 0;
        frameOffsetY = 0;
    }

    /**
     * Internal constructor.
     * 
     * @param surface The surface reference.
     * @param horizontalFrames The number of horizontal frames.
     * @param verticalFrames The number of vertical frames.
     * @throws LionEngineException If arguments are invalid.
     */
    SpriteAnimatedImpl(ImageBuffer surface, int horizontalFrames, int verticalFrames)
    {
        super(surface);

        Check.superiorStrict(horizontalFrames, 0);
        Check.superiorStrict(verticalFrames, 0);

        this.horizontalFrames = horizontalFrames;
        this.verticalFrames = verticalFrames;
    }

    /*
     * SpriteAnimated
     */

    @Override
    public void play(Animation animation)
    {
        animator.play(animation);
    }

    @Override
    public void stop()
    {
        animator.stop();
    }

    @Override
    public void update(double extrp)
    {
        animator.update(extrp);
    }

    @Override
    public void render(Graphic g)
    {
        final int frame = animator.getFrame() - 1;
        final int fx = getRenderX() - frameOffsetX;
        final int fy = getRenderY() + frameOffsetY;
        final int ox = frame % horizontalFrames;
        final int oy = (int) Math.floor(frame / (double) horizontalFrames);

        render(g, fx, fy, getFrameWidth(), getFrameHeight(), ox, oy);
    }

    @Override
    public void setAnimSpeed(double speed)
    {
        animator.setAnimSpeed(speed);
    }

    @Override
    public void setFrame(int frame)
    {
        animator.setFrame(frame);
    }

    @Override
    public AnimState getAnimState()
    {
        return animator.getAnimState();
    }

    @Override
    public int getFrame()
    {
        return animator.getFrame();
    }

    @Override
    public int getFrameAnim()
    {
        return animator.getFrameAnim();
    }

    @Override
    public void setFrameOffsets(int offsetX, int offsetY)
    {
        frameOffsetX = offsetX;
        frameOffsetY = offsetY;
    }

    @Override
    public int getFramesHorizontal()
    {
        return horizontalFrames;
    }

    @Override
    public int getFramesVertical()
    {
        return verticalFrames;
    }

    @Override
    public int getFrameWidth()
    {
        return getWidth() / horizontalFrames;
    }

    @Override
    public int getFrameHeight()
    {
        return getHeight() / verticalFrames;
    }

    @Override
    protected void stretch(int newWidth, int newHeight)
    {
        final int w = (int) Math.round(newWidth / (double) getFramesHorizontal()) * getFramesHorizontal();
        final int h = (int) Math.round(newHeight / (double) getFramesVertical()) * getFramesVertical();
        super.stretch(w, h);
    }

    @Override
    protected void computeRenderingPoint(int width, int height)
    {
        super.computeRenderingPoint(width / horizontalFrames, height / verticalFrames);
    }

    /*
     * Object
     */

    @Override
    public boolean equals(Object object)
    {
        if (object == this)
        {
            return true;
        }
        if (object == null || object.getClass() != getClass())
        {
            return false;
        }
        final SpriteAnimated sprite = (SpriteAnimated) object;

        final boolean sameSprite = super.equals(object);
        final boolean sameFrameWidth = sprite.getFrameWidth() == getFrameWidth();
        final boolean sameFrameHeight = sprite.getFrameHeight() == getFrameHeight();
        final boolean sameHorizontalFrames = sprite.getFramesHorizontal() == getFramesHorizontal();
        final boolean sameVerticalFrames = sprite.getFramesVertical() == getFramesVertical();

        final boolean sameSize = sameFrameWidth && sameFrameHeight;
        final boolean sameFrames = sameHorizontalFrames && sameVerticalFrames;

        return sameSize && sameFrames && sameSprite;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + animator.hashCode();
        result = prime * result + horizontalFrames;
        result = prime * result + verticalFrames;
        return result;
    }
}
