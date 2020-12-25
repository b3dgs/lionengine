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
package com.b3dgs.lionengine.graphic.drawable;

import com.b3dgs.lionengine.AnimState;
import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.Animator;
import com.b3dgs.lionengine.AnimatorListener;
import com.b3dgs.lionengine.AnimatorModel;
import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.ImageBuffer;

/**
 * Animated sprite implementation.
 */
final class SpriteAnimatedImpl extends SpriteImpl implements SpriteAnimated
{
    /** Animator reference. */
    private final Animator animator = new AnimatorModel();
    /** Media reference (<code>null</code> created with existing surface). */
    private final Media media;
    /** Number of horizontal frames. */
    private final int framesHorizontal;
    /** Number of vertical frames. */
    private final int framesVertical;
    /** Total frames number. */
    private final int framesNumber;

    /**
     * Internal constructor.
     * 
     * @param media The sprite media (must not be <code>null</code>).
     * @param framesHorizontal The number of horizontal frames (must be strictly positive).
     * @param framesVertical The number of vertical frames (must be strictly positive).
     * @throws LionEngineException If arguments are invalid or image cannot be read.
     */
    SpriteAnimatedImpl(Media media, int framesHorizontal, int framesVertical)
    {
        super(media);

        Check.superiorStrict(framesHorizontal, 0);
        Check.superiorStrict(framesVertical, 0);

        this.media = media;
        this.framesHorizontal = framesHorizontal;
        this.framesVertical = framesVertical;
        framesNumber = framesHorizontal * framesVertical;
    }

    /**
     * Internal constructor.
     * 
     * @param surface The surface reference (must not be <code>null</code>).
     * @param framesHorizontal The number of horizontal frames (must be strictly positive).
     * @param framesVertical The number of vertical frames (must be strictly positive).
     * @throws LionEngineException If arguments are invalid.
     */
    SpriteAnimatedImpl(ImageBuffer surface, int framesHorizontal, int framesVertical)
    {
        super(surface);

        Check.superiorStrict(framesHorizontal, 0);
        Check.superiorStrict(framesVertical, 0);

        media = null;
        this.framesHorizontal = framesHorizontal;
        this.framesVertical = framesVertical;
        framesNumber = framesHorizontal * framesVertical;
    }

    /*
     * SpriteAnimated
     */

    @Override
    public void addListener(AnimatorListener listener)
    {
        animator.addListener(listener);
    }

    @Override
    public void removeListener(AnimatorListener listener)
    {
        animator.removeListener(listener);
    }

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
    public void reset()
    {
        animator.reset();
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
        final int ox = frame % framesHorizontal;
        final int oy = (int) Math.floor(frame / (double) framesHorizontal);

        render(g, getRenderX(), getRenderY(), getTileWidth(), getTileHeight(), ox, oy);
    }

    @Override
    public void setAnimSpeed(double speed)
    {
        animator.setAnimSpeed(speed);
    }

    @Override
    public void setFrame(int frame)
    {
        Check.inferiorOrEqual(frame, framesNumber);

        animator.setFrame(frame);
    }

    @Override
    public AnimState getAnimState()
    {
        return animator.getAnimState();
    }

    @Override
    public Animation getAnim()
    {
        return animator.getAnim();
    }

    @Override
    public int getFrames()
    {
        return animator.getFrames();
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
    public int getFramesHorizontal()
    {
        return framesHorizontal;
    }

    @Override
    public int getFramesVertical()
    {
        return framesVertical;
    }

    @Override
    public int getTileWidth()
    {
        return getWidth() / framesHorizontal;
    }

    @Override
    public int getTileHeight()
    {
        return getHeight() / framesVertical;
    }

    @Override
    protected void stretch(int newWidth, int newHeight)
    {
        final int w = (int) Math.round(newWidth / (double) framesHorizontal) * framesHorizontal;
        final int h = (int) Math.round(newHeight / (double) framesVertical) * framesVertical;
        super.stretch(w, h);
    }

    @Override
    protected void computeRenderingPoint(int width, int height)
    {
        super.computeRenderingPoint(width / framesHorizontal, height / framesVertical);
    }

    /*
     * Object
     */

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        if (getSurface() != null)
        {
            result = prime * result + getSurface().hashCode();
        }
        else
        {
            result = prime * result + media.hashCode();
        }
        result = prime * result + framesHorizontal;
        result = prime * result + framesVertical;
        return result;
    }

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
        final SpriteAnimatedImpl other = (SpriteAnimatedImpl) object;
        return getSurface() == other.getSurface()
               && framesHorizontal == other.framesHorizontal
               && framesVertical == other.framesVertical;
    }
}
