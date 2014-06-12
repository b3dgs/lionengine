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

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Composite;

import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.core.Core;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.UtilityMedia;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.Sprite;
import com.b3dgs.lionengine.game.configurable.Configurable;
import com.b3dgs.lionengine.game.configurable.FramesData;

/**
 * Animation paint listener, rendering the current animation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class AnimationFrameSelector
        implements PaintListener, MouseListener, MouseMoveListener
{
    /** Frame color. */
    private static final ColorRgba COLOR_FRAME = new ColorRgba(128, 128, 192, 128);
    /** Frame over color. */
    private static final ColorRgba COLOR_FRAME_OVER = new ColorRgba(192, 192, 240, 192);

    /** The parent. */
    final Composite parent;
    /** The surface. */
    final Sprite surface;

    /** Graphic reference. */
    private final Graphic g;
    /** Horizontal frames. */
    private final int horizontalFrames;
    /** Vertical frames. */
    private final int verticalFrames;
    /** Frame width. */
    private final int frameWidth;
    /** Frame height. */
    private final int frameHeight;
    /** Animation properties. */
    private AnimationProperties animationProperties;
    /** Current horizontal mouse location. */
    private int mouseX;
    /** Current vertical mouse location. */
    private int mouseY;
    /** Mouse click. */
    private int click;
    /** Mouse clicked. */
    private boolean clicked;

    /**
     * Constructor.
     * 
     * @param parent The parent container.
     * @param configurable The configurable reference.
     */
    public AnimationFrameSelector(Composite parent, Configurable configurable)
    {
        this.parent = parent;
        g = Core.GRAPHIC.createGraphic();
        final Media media = UtilityMedia.get(new File(configurable.getPath(), configurable.getSurface().getImage()));
        final FramesData framesData = configurable.getFrames();
        horizontalFrames = framesData.getHorizontal();
        verticalFrames = framesData.getVertical();
        surface = Drawable.loadSprite(media);
        frameWidth = surface.getWidth() / horizontalFrames;
        frameHeight = surface.getHeight() / verticalFrames;
        surface.load(false);
    }

    /**
     * Set the animation list.
     * 
     * @param animationProperties The animation properties reference.
     */
    public void setAnimationProperties(AnimationProperties animationProperties)
    {
        this.animationProperties = animationProperties;
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
        final int x = width / 2 - surface.getWidth() / 2;
        final int y = height / 2 - surface.getHeight() / 2;

        g.setColor(AnimationFrameSelector.COLOR_FRAME);
        for (int h = 0; h < horizontalFrames; h++)
        {
            for (int v = 0; v < verticalFrames; v++)
            {
                g.drawRect(x + h * frameWidth, y + v * frameHeight, frameWidth, frameHeight, true);
            }
        }

        if (mouseX >= x && mouseX < x + surface.getWidth() && mouseY >= y && mouseY < y + surface.getHeight())
        {
            g.setColor(AnimationFrameSelector.COLOR_FRAME_OVER);
            final int offsetX = getOffset(x, frameWidth);
            final int offsetY = getOffset(y, frameHeight);

            final int rx = UtilMath.getRounded(mouseX - offsetX, frameWidth) + offsetX;
            final int ry = UtilMath.getRounded(mouseY - offsetY, frameHeight) + offsetY;
            g.drawRect(rx, ry, frameWidth, frameHeight, true);

            final int fx = (rx - x) / frameWidth;
            final int fy = (ry - y) / frameHeight;
            final int frame = fx + fy * horizontalFrames + 1;
            System.out.println(fx + " " + fy + " " + frame);
            if (click > 0 && !clicked)
            {
                animationProperties.setSelectedFrame(frame);
                clicked = true;
            }
        }

        surface.render(g, x, y);
    }

    /**
     * Get the offset rounded value to ensure it fit in the grid.
     * 
     * @param value The input value.
     * @param round The round reference.
     * @return The rounded value.
     */
    private int getOffset(int value, int round)
    {
        final double offset = value / (double) round;
        return (int) ((offset - Math.floor(offset)) * round);
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
        clicked = false;
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
}
