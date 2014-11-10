/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Transparency;
import com.b3dgs.lionengine.anim.Anim;
import com.b3dgs.lionengine.anim.AnimState;
import com.b3dgs.lionengine.anim.Animation;
import com.b3dgs.lionengine.anim.Animator;
import com.b3dgs.lionengine.core.Core;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.ImageBuffer;
import com.b3dgs.lionengine.core.Media;

/**
 * Animated sprite implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
final class SpriteAnimatedImpl
        extends SpriteImpl
        implements SpriteAnimated
{
    /** Animator reference. */
    private final Animator animator;
    /** Number of horizontal frames. */
    private final int horizontalFrames;
    /** Number of vertical frames. */
    private final int verticalFrames;
    /** Frames original width. */
    private final int frameOriginalWidth;
    /** Frames original height. */
    private final int frameOriginalHeight;
    /** Total number of frame. */
    private final int framesNumber;
    /** Mirror flag. */
    private boolean mirror;
    /** Mirror axis. */
    private boolean mirrorHorizontal;

    /**
     * Internal constructor.
     * 
     * @param media The sprite media.
     * @param horizontalFrames The number of horizontal frames.
     * @param verticalFrames The number of vertical frames.
     * @throws LionEngineException If arguments are invalid or image cannot be read.
     */
    SpriteAnimatedImpl(Media media, int horizontalFrames, int verticalFrames) throws LionEngineException
    {
        super(media);

        Check.superiorStrict(horizontalFrames, 0);
        Check.superiorStrict(verticalFrames, 0);

        this.horizontalFrames = horizontalFrames;
        this.verticalFrames = verticalFrames;
        frameOriginalWidth = getWidthOriginal() / horizontalFrames;
        frameOriginalHeight = getHeightOriginal() / verticalFrames;
        framesNumber = horizontalFrames * verticalFrames;
        animator = Anim.createAnimator();
        mirrorHorizontal = true;
    }

    /**
     * Internal constructor.
     * 
     * @param surface The surface reference.
     * @param horizontalFrames The number of horizontal frames.
     * @param verticalFrames The number of vertical frames.
     * @throws LionEngineException If arguments are invalid.
     */
    SpriteAnimatedImpl(ImageBuffer surface, int horizontalFrames, int verticalFrames) throws LionEngineException
    {
        super(surface);

        Check.superiorStrict(horizontalFrames, 0);
        Check.superiorStrict(verticalFrames, 0);

        this.horizontalFrames = horizontalFrames;
        this.verticalFrames = verticalFrames;
        frameOriginalWidth = getWidthOriginal() / horizontalFrames;
        frameOriginalHeight = getHeightOriginal() / verticalFrames;
        framesNumber = horizontalFrames * verticalFrames;
        animator = Anim.createAnimator();
        mirrorHorizontal = true;
    }

    /*
     * Animator
     */

    @Override
    public void play(Animation animation) throws LionEngineException
    {
        animator.play(animation);
    }

    @Override
    public void setAnimSpeed(double speed) throws LionEngineException
    {
        animator.setAnimSpeed(speed);
    }

    @Override
    public void stopAnimation()
    {
        animator.stopAnimation();
    }

    @Override
    public void updateAnimation(double extrp)
    {
        animator.updateAnimation(extrp);
    }

    @Override
    public void setFrame(int frame) throws LionEngineException
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

    /*
     * SpriteAnimated
     */

    @Override
    public void render(Graphic g, int x, int y)
    {
        render(g, animator.getFrame(), x, y);
    }

    @Override
    public void render(Graphic g, int frame, int x, int y)
    {
        final int cFrame = frame - 1;
        final int cx = cFrame % horizontalFrames;
        final int cy = (int) Math.floor(cFrame / (double) horizontalFrames);
        final int w = getFrameWidth();
        final int h = getFrameHeight();

        if (mirror)
        {
            if (mirrorHorizontal)
            {
                g.drawImage(getSurface(), x, y, x + w, y + h, cx * w + w, cy * h, cx * w, cy * h + h);
            }
            else
            {
                g.drawImage(getSurface(), x, y, x + w, y + h, cx * w, cy * h + h, cx * w + w, cy * h);
            }
        }
        else
        {
            g.drawImage(getSurface(), x, y, x + w, y + h, cx * w, cy * h, cx * w + w, cy * h + h);
        }
    }

    @Override
    public void setMirror(boolean mirror)
    {
        this.mirror = mirror;
    }

    @Override
    public void setMirrorAxis(boolean horizontal)
    {
        mirrorHorizontal = horizontal;
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
    public int getFramesNumber()
    {
        return framesNumber;
    }

    @Override
    public int getFrameWidth()
    {
        return getWidth() / getFramesHorizontal();
    }

    @Override
    public int getFrameHeight()
    {
        return getHeight() / getFramesVertical();
    }

    @Override
    public int getFrameWidthOriginal()
    {
        return frameOriginalWidth;
    }

    @Override
    public int getFrameHeightOriginal()
    {
        return frameOriginalHeight;
    }

    @Override
    public ImageBuffer getFrame(int frame)
    {
        final ImageBuffer buf = Core.GRAPHIC.createImageBuffer(getWidth(), getHeight(), Transparency.BITMASK);
        final Graphic g = buf.createGraphic();
        final int cx = frame % getFramesHorizontal();
        final int cy = (int) Math.floor(frame / (double) getFramesHorizontal());
        final int w = getFrameWidth();
        final int h = getFrameHeight();

        g.drawImage(getSurface(), 0, 0, w, h, cx * w, cy * h, cx * w + w, cy * h + h);
        g.dispose();

        return buf;
    }

    @Override
    protected void stretchSurface(int newWidth, int newHeight)
    {
        final int w = (int) Math.round(newWidth / (double) getFramesHorizontal()) * getFramesHorizontal();
        final int h = (int) Math.round(newHeight / (double) getFramesVertical()) * getFramesVertical();
        super.stretchSurface(w, h);
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
        if (object instanceof SpriteAnimated)
        {
            final SpriteAnimated sprite = (SpriteAnimated) object;

            final boolean sameSprite = super.equals(object);
            final boolean sameFrameWidth = sprite.getFrameWidth() == getFrameWidth();
            final boolean sameFrameHeight = sprite.getFrameHeight() == getFrameHeight();
            final boolean sameHorizontalFrames = sprite.getFramesHorizontal() == getFramesHorizontal();
            final boolean sameVerticalFrames = sprite.getFramesVertical() == getFramesVertical();
            final boolean sameFramesNumber = sprite.getFramesNumber() == getFramesNumber();

            return sameFrameWidth && sameFrameHeight && sameHorizontalFrames && sameVerticalFrames && sameFramesNumber
                    && sameSprite;
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + animator.hashCode();
        result = prime * result + frameOriginalHeight;
        result = prime * result + frameOriginalWidth;
        result = prime * result + framesNumber;
        result = prime * result + horizontalFrames;
        result = prime * result + (mirror ? 1231 : 1237);
        result = prime * result + (mirrorHorizontal ? 1231 : 1237);
        result = prime * result + verticalFrames;
        return result;
    }
}
