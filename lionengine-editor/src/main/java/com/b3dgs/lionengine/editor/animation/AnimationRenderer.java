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

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Composite;

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
    /** The parent. */
    final Composite parent;
    /** The animated surface. */
    final SpriteAnimated surface;

    /** Graphic reference. */
    private final Graphic g;
    /** Paused flag. */
    boolean paused;
    /** Last played frame */
    int lastPlayedFrame;

    /**
     * Constructor.
     * 
     * @param parent The parent container.
     * @param configurable The configurable reference.
     */
    public AnimationRenderer(Composite parent, Configurable configurable)
    {
        this.parent = parent;
        g = Core.GRAPHIC.createGraphic();
        final Media media = UtilityMedia.get(new File(configurable.getPath(), configurable.getSurface().getImage()));
        final FramesData framesData = configurable.getFrames();
        surface = Drawable.loadSpriteAnimated(media, framesData.getHorizontal(), framesData.getVertical());
        surface.load(false);
    }

    /**
     * Set the animation to play.
     * 
     * @param animation The animation to play.
     */
    public void setAnimation(Animation animation)
    {
        surface.stopAnimation();
        surface.play(animation);
        update();
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
     * Stop the animation.
     */
    public void stopAnimation()
    {
        surface.stopAnimation();
    }

    /**
     * Update the rendering periodically.
     */
    void update()
    {
        parent.getDisplay().asyncExec(new Runnable()
        {
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
                    if (lastPlayedFrame != surface.getFrame())
                    {
                        parent.redraw();
                    }
                    try
                    {
                        Thread.sleep(17);
                        final AnimState state = surface.getAnimState();
                        if (state == AnimState.PLAYING || state == AnimState.REVERSING)
                        {
                            update();
                        }
                    }
                    catch (final InterruptedException exception)
                    {
                        Thread.interrupted();
                        return;
                    }
                }
            }
        });
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
