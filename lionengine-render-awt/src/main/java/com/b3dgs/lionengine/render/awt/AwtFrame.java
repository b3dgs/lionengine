/*
 * Copyright (C) 2013-2026 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.render.awt;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;

import com.b3dgs.lionengine.render.CommandBuffer;

/**
 * Logical frame of the AWT renderer: an image of the logical frame size on which the render commands are drawn
 * (ADR-0026, REQ-SPRITE-008, REQ-SPRITE-010).
 *
 * <p>
 * Each frame starts cleared to opaque black. Each entry is clipped to its viewport and offset by it, then drawn with
 * nearest-neighbor sampling: its pivot at its position, rotated counter-clockwise on screen, scaled, its colors
 * multiplied by the tint, and its opacity multiplied by the alpha of the tint and by its alpha. A tinted entry first
 * copies its source rectangle on the CPU. A frame allocates only when the opacity of an entry differs from the previous
 * one (a new composite) or when a tinted entry needs a larger scratch image; the command buffer itself never
 * allocates. Runtime thread only.
 * </p>
 */
final class AwtFrame
{
    /** Color of a cleared frame: opaque black. */
    static final int CLEAR = 0xFF000000;

    /** Maximum value of a color channel. */
    private static final double CHANNEL_MAX = 255.0;
    /** Mask of a color channel. */
    private static final int CHANNEL = 0xFF;
    /** Mask of the color channels of a tint. */
    private static final int RGB = 0xFFFFFF;
    /** Shift of the alpha channel. */
    private static final int ALPHA_SHIFT = 24;
    /** Shift of the red channel. */
    private static final int RED_SHIFT = 16;
    /** Shift of the green channel. */
    private static final int GREEN_SHIFT = 8;
    /** Number of channels of a texture: red, green, blue, alpha. */
    private static final int CHANNELS = 4;
    /** Offsets of the tint, none. */
    private static final float[] NO_OFFSET = new float[CHANNELS];

    /** Image of the logical frame. */
    private final BufferedImage image;
    /** Graphics of the image, kept for the life of the frame. */
    private final Graphics2D graphics;
    /** Transform of the entry being drawn. */
    private final AffineTransform transform = new AffineTransform();
    /** Identity transform. */
    private final AffineTransform identity = new AffineTransform();
    /** Logical frame width in pixels. */
    private final int width;
    /** Logical frame height in pixels. */
    private final int height;
    /** Tinted copy of a source rectangle, grown when needed, {@code null} until the first tint. */
    private BufferedImage scratch;
    /** Tint of {@link #tintOperation}. */
    private int tint = CommandBuffer.NO_TINT;
    /** Operation applying {@link #tint}, {@code null} until the first tint. */
    private RescaleOp tintOperation;
    /** Opacity of {@link #composite}. */
    private float opacity = 1.0F;
    /** Composite applying {@link #opacity}. */
    private AlphaComposite composite = AlphaComposite.SrcOver;
    /** Left edge of the current clip. */
    private int clipX;
    /** Top edge of the current clip. */
    private int clipY;
    /** Width of the current clip, {@code -1} when not clipped yet in the frame. */
    private int clipWidth;
    /** Height of the current clip. */
    private int clipHeight;

    /**
     * Creates the logical frame.
     *
     * @param width The logical frame width in pixels.
     * @param height The logical frame height in pixels.
     */
    AwtFrame(int width, int height)
    {
        this.width = width;
        this.height = height;
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        graphics = image.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                                  RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        graphics.setBackground(new Color(CLEAR, true));
    }

    /**
     * Draws the entries of a buffer on the cleared frame, by ascending layer, then submission order. An entry whose
     * texture is not alive draws nothing.
     *
     * @param commands The render commands.
     * @param textures The textures.
     */
    void draw(CommandBuffer commands, AwtTextures textures)
    {
        graphics.setTransform(identity);
        graphics.setClip(null);
        graphics.clearRect(0, 0, width, height);
        clipWidth = -1;
        final int size = commands.size();
        for (int rank = 0; rank < size; rank++)
        {
            final int entry = commands.getEntry(rank);
            final BufferedImage texture = textures.getImage(commands.getTexture(entry));
            if (texture != null)
            {
                drawEntry(commands, entry, texture);
            }
        }
    }

    /**
     * Draws one entry.
     *
     * @param commands The render commands.
     * @param entry The entry index.
     * @param texture The image of its texture.
     */
    private void drawEntry(CommandBuffer commands, int entry, BufferedImage texture)
    {
        final int viewportX = commands.getViewportX(entry);
        final int viewportY = commands.getViewportY(entry);
        graphics.setTransform(identity);
        clip(viewportX, viewportY, commands.getViewportWidth(entry), commands.getViewportHeight(entry));
        transform.setToTranslation(viewportX + (double) commands.getX(entry),
                                   viewportY + (double) commands.getY(entry));
        transform.rotate(-Math.toRadians(commands.getRotation(entry)));
        transform.scale(commands.getScaleX(entry), commands.getScaleY(entry));
        transform.translate(-commands.getPivotX(entry), -commands.getPivotY(entry));
        graphics.setTransform(transform);

        final int argb = commands.getTint(entry);
        graphics.setComposite(composite((float) (commands.getAlpha(entry) * ((argb >>> ALPHA_SHIFT) / CHANNEL_MAX))));
        final int sourceX = commands.getSourceX(entry);
        final int sourceY = commands.getSourceY(entry);
        final int sourceWidth = commands.getSourceWidth(entry);
        final int sourceHeight = commands.getSourceHeight(entry);
        if ((argb & RGB) == RGB)
        {
            graphics.drawImage(texture,
                               0,
                               0,
                               sourceWidth,
                               sourceHeight,
                               sourceX,
                               sourceY,
                               sourceX + sourceWidth,
                               sourceY + sourceHeight,
                               null);
        }
        else
        {
            drawTinted(texture, sourceX, sourceY, sourceWidth, sourceHeight, argb);
        }
    }

    /**
     * Draws the part of a source rectangle inside its texture, with its colors multiplied by a tint.
     *
     * @param texture The texture image.
     * @param x The source left edge.
     * @param y The source top edge.
     * @param sourceWidth The source width.
     * @param sourceHeight The source height.
     * @param argb The tint.
     */
    private void drawTinted(BufferedImage texture, int x, int y, int sourceWidth, int sourceHeight, int argb)
    {
        final int left = Math.max(x, 0);
        final int top = Math.max(y, 0);
        final int insideWidth = Math.min(x + sourceWidth, texture.getWidth()) - left;
        final int insideHeight = Math.min(y + sourceHeight, texture.getHeight()) - top;
        if (insideWidth > 0 && insideHeight > 0)
        {
            tint(texture, left, top, insideWidth, insideHeight, argb);
            final int offsetX = left - x;
            final int offsetY = top - y;
            graphics.drawImage(scratch,
                               offsetX,
                               offsetY,
                               offsetX + insideWidth,
                               offsetY + insideHeight,
                               0,
                               0,
                               insideWidth,
                               insideHeight,
                               null);
        }
    }

    /**
     * Clips the following drawings to a viewport, unless it is the current one.
     *
     * @param x The viewport left edge in logical-frame pixels.
     * @param y The viewport top edge in logical-frame pixels.
     * @param viewportWidth The viewport width in pixels.
     * @param viewportHeight The viewport height in pixels.
     */
    private void clip(int x, int y, int viewportWidth, int viewportHeight)
    {
        if (clipX != x || clipY != y || clipWidth != viewportWidth || clipHeight != viewportHeight)
        {
            graphics.setClip(x, y, viewportWidth, viewportHeight);
            clipX = x;
            clipY = y;
            clipWidth = viewportWidth;
            clipHeight = viewportHeight;
        }
    }

    /**
     * Returns the composite of an opacity, reusing the last one.
     *
     * @param value The opacity in {@code [0, 1]}.
     * @return The composite.
     */
    private AlphaComposite composite(float value)
    {
        if (Float.compare(value, opacity) != 0)
        {
            opacity = value;
            composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, value);
        }
        return composite;
    }

    /**
     * Copies a rectangle of a texture, inside it, into the scratch image with its colors multiplied by a tint.
     *
     * @param texture The texture image.
     * @param x The source left edge.
     * @param y The source top edge.
     * @param sourceWidth The source width.
     * @param sourceHeight The source height.
     * @param argb The tint.
     */
    private void tint(BufferedImage texture, int x, int y, int sourceWidth, int sourceHeight, int argb)
    {
        if (scratch == null || scratch.getWidth() < sourceWidth || scratch.getHeight() < sourceHeight)
        {
            final int scratchWidth = scratch == null ? sourceWidth : Math.max(sourceWidth, scratch.getWidth());
            final int scratchHeight = scratch == null ? sourceHeight : Math.max(sourceHeight, scratch.getHeight());
            scratch = new BufferedImage(scratchWidth, scratchHeight, BufferedImage.TYPE_INT_ARGB);
        }
        if (tintOperation == null || argb != tint)
        {
            tint = argb;
            tintOperation = new RescaleOp(new float[]
            {
                (float) (((argb >> RED_SHIFT) & CHANNEL) / CHANNEL_MAX),
                (float) (((argb >> GREEN_SHIFT) & CHANNEL) / CHANNEL_MAX),
                (float) ((argb & CHANNEL) / CHANNEL_MAX),
                1.0F
            }, NO_OFFSET, null);
        }
        tintOperation.filter(texture.getRaster().createChild(x, y, sourceWidth, sourceHeight, 0, 0, null),
                             scratch.getRaster().createWritableChild(0, 0, sourceWidth, sourceHeight, 0, 0, null));
    }

    /**
     * Returns the image of the logical frame.
     *
     * @return The image, {@link BufferedImage#TYPE_INT_RGB}.
     */
    BufferedImage getImage()
    {
        return image;
    }
}
