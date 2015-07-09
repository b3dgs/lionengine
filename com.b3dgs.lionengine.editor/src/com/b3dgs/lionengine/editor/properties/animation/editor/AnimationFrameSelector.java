/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.editor.properties.animation.editor;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.Graphics;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.swt.UtilityMedia;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.Sprite;
import com.b3dgs.lionengine.game.configurer.ConfigFrames;
import com.b3dgs.lionengine.game.configurer.ConfigSurface;
import com.b3dgs.lionengine.game.configurer.Configurer;

/**
 * Animation paint listener, rendering the current animation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class AnimationFrameSelector implements PaintListener, MouseListener, MouseMoveListener
{
    /** Frame color. */
    private static final ColorRgba COLOR_FRAME = new ColorRgba(128, 128, 192, 255);
    /** Frame over color. */
    private static final ColorRgba COLOR_FRAME_OVER = new ColorRgba(240, 192, 192, 192);
    /** Frame selected color. */
    private static final ColorRgba COLOR_FRAME_SELECTED = new ColorRgba(192, 240, 192, 192);

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
    /** Animation list reference. */
    private AnimationList animationList;
    /** Animation properties reference. */
    private AnimationProperties animationProperties;
    /** Initial selected frame. */
    private int selectedInitialFrame;
    /** First selected frame. */
    private int selectedFirstFrame;
    /** Last selected frame. */
    private int selectedLastFrame;
    /** Current horizontal mouse location. */
    private int mouseX;
    /** Current vertical mouse location. */
    private int mouseY;
    /** Mouse clicked. */
    private boolean clicked;

    /**
     * Create an animation frame selector and load surface from configurer.
     * 
     * @param parent The parent container.
     * @param configurer The configurer reference.
     */
    public AnimationFrameSelector(Composite parent, Configurer configurer)
    {
        this.parent = parent;
        g = Graphics.createGraphic();
        final ConfigSurface configSurface = ConfigSurface.create(configurer);
        final Media media = UtilityMedia.get(new File(configurer.getPath(), configSurface.getImage()));
        final ConfigFrames framesData = ConfigFrames.create(configurer);
        horizontalFrames = framesData.getHorizontal();
        verticalFrames = framesData.getVertical();
        surface = Drawable.loadSprite(media);
        frameWidth = surface.getWidth() / horizontalFrames;
        frameHeight = surface.getHeight() / verticalFrames;
        surface.load();
        surface.prepare();
        surface.setLocation(0, 0);

        final GridData data = new GridData(surface.getWidth(), surface.getHeight());
        data.horizontalAlignment = SWT.CENTER;
        data.verticalAlignment = SWT.CENTER;
        data.grabExcessHorizontalSpace = true;
        data.grabExcessVerticalSpace = true;
        parent.setLayoutData(data);
        parent.setSize(surface.getWidth(), surface.getHeight());
        parent.layout(true, true);
    }

    /**
     * Set the animation list reference.
     * 
     * @param animationList The animation list reference.
     */
    public void setAnimationList(AnimationList animationList)
    {
        this.animationList = animationList;
    }

    /**
     * Set the animation properties reference.
     * 
     * @param animationProperties The animation properties reference.
     */
    public void setAnimationProperties(AnimationProperties animationProperties)
    {
        this.animationProperties = animationProperties;
    }

    /**
     * Set the frames selection index.
     * 
     * @param start The first selected frame.
     * @param end The last selected frame.
     */
    public void setSelectedFrames(int start, int end)
    {
        selectedFirstFrame = start;
        selectedLastFrame = end;
        updateRender();
    }

    /**
     * Start the frame selection.
     */
    private void startFrameSelection()
    {
        if (!clicked && isOverSurface() && animationList.getSelectedObject() != null)
        {
            selectedInitialFrame = UtilMath.fixBetween(getFrameOnMouse(), 1, horizontalFrames * verticalFrames);
            selectedFirstFrame = selectedInitialFrame;
            selectedLastFrame = selectedFirstFrame;
            clicked = true;
        }
    }

    /**
     * Update the frame selection and keep order valid.
     */
    private void updateFrameSelection()
    {
        if (clicked)
        {
            selectedLastFrame = UtilMath.fixBetween(getFrameOnMouse(), 1, horizontalFrames * verticalFrames);
            if (selectedInitialFrame > selectedLastFrame)
            {
                selectedFirstFrame = selectedLastFrame;
                selectedLastFrame = selectedInitialFrame;
            }
            animationProperties.setAnimationRange(selectedFirstFrame, selectedLastFrame);
        }
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
        renderFramesBackground(g);
        renderFramesSelected(g);
        renderOverFrame(g);
        surface.render(g);
    }

    /**
     * Render the background for each frame.
     * 
     * @param g The graphic output.
     */
    private void renderFramesBackground(Graphic g)
    {
        for (int h = 0; h < horizontalFrames; h++)
        {
            for (int v = 0; v < verticalFrames; v++)
            {
                g.setColor(AnimationFrameSelector.COLOR_FRAME);
                g.drawRect(h * frameWidth, v * frameHeight, frameWidth, frameHeight, true);
                g.setColor(ColorRgba.BLACK);
                g.drawRect(h * frameWidth, v * frameHeight, frameWidth, frameHeight, false);
            }
        }
    }

    /**
     * Render the background for each selected frames.
     * 
     * @param g The graphic output.
     */
    private void renderFramesSelected(Graphic g)
    {
        for (int h = 0; h < horizontalFrames; h++)
        {
            for (int v = 0; v < verticalFrames; v++)
            {
                final int frame = h + v * horizontalFrames + 1;
                if (frame >= selectedFirstFrame && frame <= selectedLastFrame)
                {
                    g.setColor(AnimationFrameSelector.COLOR_FRAME_SELECTED);
                    g.drawRect(h * frameWidth, v * frameHeight, frameWidth, frameHeight, true);
                    g.setColor(ColorRgba.BLACK);
                    g.drawRect(h * frameWidth, v * frameHeight, frameWidth, frameHeight, false);
                }
            }
        }
    }

    /**
     * Render the frame where the mouse is over.
     * 
     * @param g The graphic output.
     */
    private void renderOverFrame(Graphic g)
    {
        if (isOverSurface())
        {
            final int rx = UtilMath.getRounded(mouseX, frameWidth);
            final int ry = UtilMath.getRounded(mouseY, frameHeight);

            g.setColor(AnimationFrameSelector.COLOR_FRAME_OVER);
            g.drawRect(rx, ry, frameWidth, frameHeight, true);
            g.setColor(ColorRgba.BLACK);
            g.drawRect(rx, ry, frameWidth, frameHeight, false);
        }
    }

    /**
     * Get the frame designed by the mouse pointer.
     * 
     * @return The frame over the mouse.
     */
    private int getFrameOnMouse()
    {
        final int rx = UtilMath.getRounded(mouseX, frameWidth);
        final int ry = UtilMath.getRounded(mouseY, frameHeight);

        final int fx = rx / frameWidth;
        final int fy = ry / frameHeight;

        return fx + fy * horizontalFrames + 1;
    }

    /**
     * Check if mouse is over animation surface.
     * 
     * @return <code>true</code> if is over, <code>false</code> else.
     */
    private boolean isOverSurface()
    {
        return mouseX >= 0 && mouseX < surface.getWidth() && mouseY >= 0 && mouseY < surface.getHeight();
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

        updateMouse(mx, my);
        startFrameSelection();
        updateFrameSelection();
    }

    @Override
    public void mouseUp(MouseEvent mouseEvent)
    {
        final int mx = mouseEvent.x;
        final int my = mouseEvent.y;

        clicked = false;
        updateMouse(mx, my);
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
        updateFrameSelection();
    }
}
