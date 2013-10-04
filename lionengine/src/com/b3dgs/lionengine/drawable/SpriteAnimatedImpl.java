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

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.Transparency;
import com.b3dgs.lionengine.anim.Anim;
import com.b3dgs.lionengine.anim.AnimState;
import com.b3dgs.lionengine.anim.Animation;
import com.b3dgs.lionengine.anim.Animator;
import com.b3dgs.lionengine.core.ImageBuffer;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.UtilityImage;

/**
 * Animated sprite implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
final class SpriteAnimatedImpl
        extends SpriteImpl
        implements SpriteAnimated
{
    /** Sprite frames error. */
    private static final String ERROR_SPRITE_FRAMES = "Sprite frames must be strictly positive !";

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

    /**
     * Constructor.
     * 
     * @param media The sprite media.
     * @param surface The surface reference.
     * @param horizontalFrames The number of horizontal frames.
     * @param verticalFrames The number of vertical frames.
     */
    SpriteAnimatedImpl(Media media, ImageBuffer surface, int horizontalFrames, int verticalFrames)
    {
        super(media, surface);
        Check.argument(horizontalFrames > 0 && verticalFrames > 0, SpriteAnimatedImpl.ERROR_SPRITE_FRAMES);
        this.horizontalFrames = horizontalFrames;
        this.verticalFrames = verticalFrames;
        frameOriginalWidth = widthOriginal / horizontalFrames;
        frameOriginalHeight = heightOriginal / verticalFrames;
        framesNumber = horizontalFrames * verticalFrames;
        animator = Anim.createAnimator();
    }

    /*
     * Animator
     */

    @Override
    public void play(Animation animation)
    {
        animator.play(animation);
    }

    @Override
    public void setAnimSpeed(double speed)
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
            g.drawImage(surface, x, y, x + w, y + h, cx * w + w, cy * h, cx * w, cy * h + h);
        }
        else
        {
            g.drawImage(surface, x, y, x + w, y + h, cx * w, cy * h, cx * w + w, cy * h + h);
        }
    }

    @Override
    public void setMirror(boolean mirror)
    {
        this.mirror = mirror;
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
        final ImageBuffer buf = UtilityImage.createImageBuffer(getWidth(), getHeight(), Transparency.BITMASK);
        final Graphic g = buf.createGraphic();
        final int cx = frame % getFramesHorizontal();
        final int cy = (int) Math.floor(frame / (double) getFramesHorizontal());
        final int w = getFrameWidth();
        final int h = getFrameHeight();

        g.drawImage(surface, 0, 0, w, h, cx * w, cy * h, cx * w + w, cy * h + h);
        g.dispose();

        return buf;
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

            return sameSprite && sameFrameWidth && sameFrameHeight && sameHorizontalFrames && sameVerticalFrames
                    && sameFramesNumber;
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + (animator == null ? 0 : animator.hashCode());
        result = prime * result + frameOriginalHeight;
        result = prime * result + frameOriginalWidth;
        result = prime * result + framesNumber;
        result = prime * result + horizontalFrames;
        result = prime * result + (mirror ? 1231 : 1237);
        result = prime * result + verticalFrames;
        return result;
    }
}
