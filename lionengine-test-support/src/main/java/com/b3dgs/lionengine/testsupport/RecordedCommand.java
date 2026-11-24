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
package com.b3dgs.lionengine.testsupport;

import com.b3dgs.lionengine.render.CommandBuffer;
import com.b3dgs.lionengine.render.Viewport;

/**
 * Copy of one render command presented to the {@link RecordingBackend} (ADR-0045, REQ-SPRITE-010).
 *
 * <p>
 * Holds the values of the entry as written in the {@link CommandBuffer}, its viewport included. Immutable.
 * </p>
 *
 * @since 11.0.0
 */
public final class RecordedCommand
{
    /** Texture handle. */
    private final int texture;
    /** Source rectangle left edge. */
    private final int sourceX;
    /** Source rectangle top edge. */
    private final int sourceY;
    /** Source rectangle width. */
    private final int sourceWidth;
    /** Source rectangle height. */
    private final int sourceHeight;
    /** Horizontal position. */
    private final float x;
    /** Vertical position. */
    private final float y;
    /** Horizontal scale. */
    private final float scaleX;
    /** Vertical scale. */
    private final float scaleY;
    /** Rotation in degrees. */
    private final float rotation;
    /** Horizontal pivot. */
    private final float pivotX;
    /** Vertical pivot. */
    private final float pivotY;
    /** Tint. */
    private final int tint;
    /** Opacity. */
    private final float alpha;
    /** Layer. */
    private final int layer;
    /** Viewport. */
    private final Viewport viewport;

    /**
     * Copies an entry of a buffer.
     *
     * @param commands The buffer.
     * @param entry The entry index.
     */
    RecordedCommand(CommandBuffer commands, int entry)
    {
        texture = commands.getTexture(entry);
        sourceX = commands.getSourceX(entry);
        sourceY = commands.getSourceY(entry);
        sourceWidth = commands.getSourceWidth(entry);
        sourceHeight = commands.getSourceHeight(entry);
        x = commands.getX(entry);
        y = commands.getY(entry);
        scaleX = commands.getScaleX(entry);
        scaleY = commands.getScaleY(entry);
        rotation = commands.getRotation(entry);
        pivotX = commands.getPivotX(entry);
        pivotY = commands.getPivotY(entry);
        tint = commands.getTint(entry);
        alpha = commands.getAlpha(entry);
        layer = commands.getLayer(entry);
        viewport = new Viewport(commands.getViewportX(entry),
                                commands.getViewportY(entry),
                                commands.getViewportWidth(entry),
                                commands.getViewportHeight(entry));
    }

    /**
     * Returns the texture handle.
     *
     * @return The texture handle.
     */
    public int getTexture()
    {
        return texture;
    }

    /**
     * Returns the left edge of the source rectangle.
     *
     * @return The left edge in texture pixels.
     */
    public int getSourceX()
    {
        return sourceX;
    }

    /**
     * Returns the top edge of the source rectangle.
     *
     * @return The top edge in texture pixels.
     */
    public int getSourceY()
    {
        return sourceY;
    }

    /**
     * Returns the width of the source rectangle.
     *
     * @return The width in pixels.
     */
    public int getSourceWidth()
    {
        return sourceWidth;
    }

    /**
     * Returns the height of the source rectangle.
     *
     * @return The height in pixels.
     */
    public int getSourceHeight()
    {
        return sourceHeight;
    }

    /**
     * Returns the horizontal position of the pivot.
     *
     * @return The position in logical-frame pixels, relative to the viewport.
     */
    public float getX()
    {
        return x;
    }

    /**
     * Returns the vertical position of the pivot.
     *
     * @return The position in logical-frame pixels, relative to the viewport.
     */
    public float getY()
    {
        return y;
    }

    /**
     * Returns the horizontal scale.
     *
     * @return The horizontal scale.
     */
    public float getScaleX()
    {
        return scaleX;
    }

    /**
     * Returns the vertical scale.
     *
     * @return The vertical scale.
     */
    public float getScaleY()
    {
        return scaleY;
    }

    /**
     * Returns the rotation.
     *
     * @return The rotation in degrees, counter-clockwise on screen.
     */
    public float getRotation()
    {
        return rotation;
    }

    /**
     * Returns the horizontal pivot.
     *
     * @return The pivot in pixels from the left edge of the source rectangle.
     */
    public float getPivotX()
    {
        return pivotX;
    }

    /**
     * Returns the vertical pivot.
     *
     * @return The pivot in pixels from the top edge of the source rectangle.
     */
    public float getPivotY()
    {
        return pivotY;
    }

    /**
     * Returns the tint.
     *
     * @return The tint, packed {@code 0xAARRGGBB}.
     */
    public int getTint()
    {
        return tint;
    }

    /**
     * Returns the opacity.
     *
     * @return The opacity in {@code [0, 1]}.
     */
    public float getAlpha()
    {
        return alpha;
    }

    /**
     * Returns the layer.
     *
     * @return The layer.
     */
    public int getLayer()
    {
        return layer;
    }

    /**
     * Returns the viewport.
     *
     * @return The viewport, the whole logical frame by default.
     */
    public Viewport getViewport()
    {
        return viewport;
    }
}
