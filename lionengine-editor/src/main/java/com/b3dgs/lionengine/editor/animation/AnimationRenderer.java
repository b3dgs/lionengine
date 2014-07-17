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
package com.b3dgs.lionengine.editor.animation;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import com.b3dgs.lionengine.anim.AnimState;
import com.b3dgs.lionengine.anim.Animation;
import com.b3dgs.lionengine.core.Core;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.UtilityMedia;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.SpriteAnimated;
import com.b3dgs.lionengine.game.configurable.Configurable;
import com.b3dgs.lionengine.game.configurable.FramesData;

/**
 * Animation paint listener, rendering the current animation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class AnimationRenderer
        implements PaintListener
{
    /** Animation update rate. */
    private static final int TIMER_INTERVAL_MILLI = 16;

    /** The parent. */
    final Composite parent;
    /** The animated surface. */
    final SpriteAnimated surface;

    /** Animator thread. */
    private final class AnimationRunner
            implements Runnable
    {
        /** Display reference. */
        private final Display display;

        /**
         * Constructor.
         * 
         * @param display The display reference.
         */
        public AnimationRunner(Display display)
        {
            this.display = display;
        }

        /*
         * Runnable
         */

        @Override
        public void run()
        {
            if (!parent.isDisposed())
            {
                if (!paused)
                {
                    lastPlayedFrame = surface.getFrame();
                    surface.updateAnimation(1.0);
                }
                final AnimState state = surface.getAnimState();
                if (lastPlayedFrame != surface.getFrame())
                {
                    parent.redraw();
                }
                if (state == AnimState.PLAYING || state == AnimState.REVERSING)
                {
                    display.timerExec(AnimationRenderer.TIMER_INTERVAL_MILLI, this);
                }
                if (state == AnimState.FINISHED)
                {
                    animationPlayer.notifyAnimationFinished();
                }
            }
        }
    }

    /** Animation runner. */
    private final AnimationRunner animationRunner;
    /** Graphic reference. */
    private final Graphic g;
    /** Animation player. */
    AnimationPlayer animationPlayer;
    /** Paused flag. */
    boolean paused;
    /** Last played frame */
    int lastPlayedFrame;
    /** Last first frame. */
    private int lastFirstFrame;

    /**
     * Constructor.
     * 
     * @param parent The parent container.
     * @param configurable The configurable reference.
     */
    public AnimationRenderer(Composite parent, Configurable configurable)
    {
        this.parent = parent;
        animationRunner = new AnimationRunner(parent.getDisplay());
        g = Core.GRAPHIC.createGraphic();
        final Media media = UtilityMedia.get(new File(configurable.getPath(), configurable.getSurface().getImage()));
        final FramesData framesData = configurable.getFrames();
        surface = Drawable.loadSpriteAnimated(media, framesData.getHorizontal(), framesData.getVertical());
        surface.load(false);

        final GridData data = new GridData(surface.getWidth(), surface.getHeight());
        data.horizontalAlignment = SWT.CENTER;
        data.verticalAlignment = SWT.CENTER;
        data.grabExcessHorizontalSpace = true;
        data.grabExcessVerticalSpace = true;
        parent.setLayoutData(data);
    }

    /**
     * Stop the animation.
     */
    public void stopAnimation()
    {
        surface.stopAnimation();
        surface.setFrame(lastFirstFrame);
        parent.redraw();
        paused = false;
    }

    /**
     * Set the animation list.
     * 
     * @param animationPlayer The animation player reference.
     */
    public void setAnimationPlayer(AnimationPlayer animationPlayer)
    {
        this.animationPlayer = animationPlayer;
    }

    /**
     * Set the animation to play.
     * 
     * @param animation The animation to play.
     */
    public void setAnimation(Animation animation)
    {
        if (!paused)
        {
            surface.stopAnimation();
            surface.updateAnimation(1.0);
            surface.play(animation);
            lastFirstFrame = animation.getFirst();
            parent.redraw();
        }
        parent.getDisplay().timerExec(AnimationRenderer.TIMER_INTERVAL_MILLI, animationRunner);
    }

    /**
     * Pause the animation.
     * 
     * @param paused <code>true</code> if paused, <code>false</code> else.
     */
    public void setPause(boolean paused)
    {
        this.paused = paused;
    }

    /**
     * Get the composite parent.
     * 
     * @return The composite parent.
     */
    public Composite getParent()
    {
        return parent;
    }

    /**
     * Render the world.
     * 
     * @param g The graphic output.
     * @param width The view width.
     * @param height The view height.
     */
    private void render(Graphic g, int width, int height)
    {
        surface.render(g, width / 2 - surface.getFrameWidth() / 2, height / 2 - surface.getFrameHeight() / 2);
    }

    /*
     * PaintListener
     */

    @Override
    public void paintControl(PaintEvent paintEvent)
    {
        final GC gc = paintEvent.gc;
        g.setGraphic(gc);
        render(g, paintEvent.width, paintEvent.height);
    }
}
