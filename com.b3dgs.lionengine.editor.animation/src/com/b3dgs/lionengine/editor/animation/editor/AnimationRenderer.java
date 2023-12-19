/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.editor.animation.editor;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import com.b3dgs.lionengine.AnimState;
import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.game.FramesConfig;
import com.b3dgs.lionengine.game.SurfaceConfig;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.SpriteAnimated;

/**
 * Animation paint listener, rendering the current animation.
 */
public final class AnimationRenderer implements PaintListener
{
    /** Animation update rate. */
    private static final int TIMER_INTERVAL_MILLI = 16;

    /** The parent. */
    private final Composite parent;
    /** The animated surface. */
    private final SpriteAnimated surface;
    /** Animation runner. */
    private final AnimationRunner animationRunner;
    /** Graphic reference. */
    private final Graphic g;
    /** Animation player. */
    private AnimationPlayer animationPlayer;
    /** Paused flag. */
    private boolean paused;
    /** Last played frame. */
    private int lastPlayedFrame;
    /** Last first frame. */
    private int lastFirstFrame;

    /**
     * Create an animation renderer and associate its configurer to load the surface.
     * 
     * @param parent The parent container.
     * @param configurer The configurer reference.
     */
    public AnimationRenderer(Composite parent, Configurer configurer)
    {
        this.parent = parent;
        animationRunner = new AnimationRunner(parent.getDisplay());
        g = Graphics.createGraphic();
        final SurfaceConfig configSurface = SurfaceConfig.imports(configurer);
        final Media media = Medias.get(new File(configurer.getPath(), configSurface.getImage()));
        final FramesConfig framesData = FramesConfig.imports(configurer);
        surface = Drawable.loadSpriteAnimated(media, framesData.getHorizontal(), framesData.getVertical());
        surface.load();
        surface.prepare();

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
        surface.stop();
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
            surface.stop();
            surface.update(1.0);
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
     * Get the surface.
     * 
     * @return The surface reference.
     */
    SpriteAnimated getSurface()
    {
        return surface;
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
        final int x = width / 2 - surface.getTileWidth() / 2;
        final int y = height / 2 - surface.getTileHeight() / 2;
        surface.setLocation(x, y);
        surface.render(g);
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

    /** Animator thread. */
    private final class AnimationRunner implements Runnable
    {
        /** Display reference. */
        private final Display display;

        /**
         * Internal constructor.
         * 
         * @param display The display reference.
         */
        AnimationRunner(Display display)
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
                    surface.update(1.0);
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
}
