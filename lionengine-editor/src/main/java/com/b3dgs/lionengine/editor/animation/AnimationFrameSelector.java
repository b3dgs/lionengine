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
    /** Frame selected color. */
    private static final ColorRgba COLOR_FRAME_SELECTED = new ColorRgba(192, 240, 192, 192);

    /**
     * Get the offset rounded value to ensure it fit in the grid.
     * 
     * @param value The input value.
     * @param round The round reference.
     * @return The rounded value.
     */
    private static int getOffset(int value, int round)
    {
        final double offset = value / (double) round;
        return (int) ((offset - Math.floor(offset)) * round);
    }

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
    /** Render horizontal start. */
    private final int renderX;
    /** Render vertical start. */
    private final int renderY;
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
        renderX = parent.getSize().x / 2 - surface.getWidth() / 2;
        renderY = parent.getSize().y / 2 - surface.getHeight() / 2;
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
        if (!clicked && isOverSurface() && animationList.getSelectedAnimation() != null)
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
        renderOverFrame(g);
        renderFramesSelected(g);
        surface.render(g, renderX, renderY);
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
                g.drawRect(renderX + h * frameWidth, renderY + v * frameHeight, frameWidth, frameHeight, true);
                g.setColor(ColorRgba.BLACK);
                g.drawRect(renderX + h * frameWidth, renderY + v * frameHeight, frameWidth, frameHeight, false);
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
                    g.drawRect(renderX + h * frameWidth, renderY + v * frameHeight, frameWidth, frameHeight, true);
                    g.setColor(ColorRgba.BLACK);
                    g.drawRect(renderX + h * frameWidth, renderY + v * frameHeight, frameWidth, frameHeight, false);
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
            final int offsetX = AnimationFrameSelector.getOffset(renderX, frameWidth);
            final int offsetY = AnimationFrameSelector.getOffset(renderY, frameHeight);

            final int rx = UtilMath.getRounded(mouseX - offsetX, frameWidth) + offsetX;
            final int ry = UtilMath.getRounded(mouseY - offsetY, frameHeight) + offsetY;

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
        final int offsetX = AnimationFrameSelector.getOffset(renderX, frameWidth);
        final int offsetY = AnimationFrameSelector.getOffset(renderY, frameHeight);

        final int rx = UtilMath.getRounded(mouseX - offsetX, frameWidth) + offsetX;
        final int ry = UtilMath.getRounded(mouseY - offsetY + frameHeight, frameHeight) + offsetY;

        final int fx = (rx - renderX) / frameWidth;
        final int fy = (ry - renderX) / frameHeight;
        final int frame = fx + fy * horizontalFrames + 1;

        return frame;
    }

    /**
     * Check if mouse is over animation surface.
     * 
     * @return <code>true</code> if is over, <code>false</code> else.
     */
    private boolean isOverSurface()
    {
        return mouseX >= renderX && mouseX < renderX + surface.getWidth() && mouseY >= renderY
                && mouseY < renderX + surface.getHeight();
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

        startFrameSelection();
        updateMouse(mx, my);
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
