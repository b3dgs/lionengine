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
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
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
        implements PaintListener, MouseListener, MouseMoveListener, KeyListener
{
    /** The parent. */
    final Composite parent;
    /** The animated surface. */
    final SpriteAnimated surface;

    /** Paused flag. */
    boolean paused;
    /** Last played frame */
    int lastPlayedFrame;
    /** Current horizontal mouse location. */
    private int mouseX;
    /** Current vertical mouse location. */
    private int mouseY;
    /** Mouse click. */
    private int click;

    /**
     * Constructor.
     * 
     * @param parent The parent container.
     * @param configurable The configurable reference.
     */
    public AnimationRenderer(Composite parent, Configurable configurable)
    {
        this.parent = parent;
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
     * Update the mouse.
     * 
     * @param mx The mouse horizontal location.
     * @param my The mouse vertical location.
     */
    private void updateMouse(int mx, int my)
    {
        mouseX = mx;
        mouseY = my;
        updateRender();
    }

    /**
     * Update the keyboard.
     * 
     * @param vx The keyboard horizontal movement.
     * @param vy The keyboard vertical movement.
     */
    private void updateKeyboard(int vx, int vy)
    {
        updateRender();
    }

    /**
     * Update the rendering.
     */
    private void updateRender()
    {
        if (!parent.isDisposed())
        {
            parent.redraw();
        }
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
        final Graphic g = Core.GRAPHIC.createGraphic();
        g.setGraphic(gc);
        render(g, paintEvent.width, paintEvent.height);
    }

    /*
     * MouseListener
     */

    @Override
    public void mouseDoubleClick(MouseEvent mouseEvent)
    {
        // Nothing to do
    }

    @Override
    public void mouseDown(MouseEvent mouseEvent)
    {
        final int mx = mouseEvent.x;
        final int my = mouseEvent.y;
        click = mouseEvent.button;

        updateMouse(mx, my);
    }

    @Override
    public void mouseUp(MouseEvent mouseEvent)
    {
        final int mx = mouseEvent.x;
        final int my = mouseEvent.y;

        updateMouse(mx, my);
        click = 0;
    }

    /*
     * MouseMoveListener
     */

    @Override
    public void mouseMove(MouseEvent mouseEvent)
    {
        final int mx = mouseEvent.x;
        final int my = mouseEvent.y;

        updateMouse(mx, my);
    }

    /*
     * KeyListener
     */

    @Override
    public void keyPressed(KeyEvent keyEvent)
    {
        final int vx;
        final int vy;
        final int code = keyEvent.keyCode;
        if (code == SWT.ARROW_LEFT)
        {
            vx = -1;
        }
        else if (code == SWT.ARROW_RIGHT)
        {
            vx = 1;
        }
        else
        {
            vx = 0;
        }
        if (code == SWT.ARROW_DOWN)
        {
            vy = -1;
        }
        else if (code == SWT.ARROW_UP)
        {
            vy = 1;
        }
        else
        {
            vy = 0;
        }
        updateKeyboard(vx, vy);
    }

    @Override
    public void keyReleased(KeyEvent keyEvent)
    {
        // Nothing to do
    }
}
