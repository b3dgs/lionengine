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

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.List;

import com.b3dgs.lionengine.platform.PresentationGeometry;
import com.b3dgs.lionengine.render.FilterKind;
import com.b3dgs.lionengine.render.OutputFilter;

/**
 * Draws the logical frame into the presentation geometry with the scaling filter of the effective chain (ADR-0006,
 * ADR-0062, ADR-0063, REQ-FILTER-004).
 *
 * <p>
 * {@link OutputFilter#NONE} samples nearest-neighbor and {@link OutputFilter#BILINEAR} interpolates, both with Java2D.
 * {@link OutputFilter#HQ2X} is drawn by {@link Scale2x} when the presentation scale is at least 2 on both axes: the
 * frame is doubled into a buffer kept here, then sampled nearest-neighbor to the geometry; below 2 it is drawn as
 * {@code NONE}. The buffers are created at the first {@code HQ2X} frame and reused, so a frame allocates nothing.
 * Thread: the runtime thread.
 * </p>
 */
final class AwtPresenter
{
    /** Smallest presentation scale where {@code Scale2x} is applied. */
    private static final double SCALE2X_MIN = 2.0;

    /**
     * Returns the scaling filter of an effective chain.
     *
     * @param chain The effective chain, filters the backend implements.
     * @return Its first filter if it scales, {@link OutputFilter#NONE} otherwise.
     */
    static OutputFilter getScalingFilter(List<OutputFilter> chain)
    {
        if (!chain.isEmpty() && chain.get(0).kind() == FilterKind.SCALING)
        {
            return chain.get(0);
        }
        return OutputFilter.NONE;
    }

    /** The scaler standing in for {@code HQ2X}. */
    private final Scale2x scale2x = new Scale2x();
    /** Logical frame width in pixels. */
    private final int width;
    /** Logical frame height in pixels. */
    private final int height;
    /** Pixels of the logical frame, {@code null} until the first {@code HQ2X} frame. */
    private int[] source;
    /** Pixels of the doubled frame, {@code null} until the first {@code HQ2X} frame. */
    private int[] target;
    /** Doubled frame, {@code null} until the first {@code HQ2X} frame. */
    private BufferedImage doubled;

    /**
     * Creates the presenter of a logical frame.
     *
     * @param width The logical frame width in pixels.
     * @param height The logical frame height in pixels.
     */
    AwtPresenter(int width, int height)
    {
        this.width = width;
        this.height = height;
    }

    /**
     * Draws the logical frame into the geometry.
     *
     * @param graphics The graphics of the surface, in device pixels.
     * @param frame The logical frame, {@link BufferedImage#TYPE_INT_RGB} of the logical size.
     * @param geometry The presentation geometry.
     * @param filter The scaling filter: {@link OutputFilter#NONE}, {@link OutputFilter#BILINEAR} or
     *            {@link OutputFilter#HQ2X}.
     */
    void draw(Graphics2D graphics, BufferedImage frame, PresentationGeometry geometry, OutputFilter filter)
    {
        BufferedImage image = frame;
        Object interpolation = RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
        if (OutputFilter.BILINEAR.equals(filter))
        {
            interpolation = RenderingHints.VALUE_INTERPOLATION_BILINEAR;
        }
        else if (OutputFilter.HQ2X.equals(filter)
                 && geometry.scaleX() >= SCALE2X_MIN
                 && geometry.scaleY() >= SCALE2X_MIN)
        {
            image = getDoubled(frame);
        }
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, interpolation);
        graphics.drawImage(image,
                           geometry.x(),
                           geometry.y(),
                           geometry.x() + geometry.width(),
                           geometry.y() + geometry.height(),
                           0,
                           0,
                           image.getWidth(),
                           image.getHeight(),
                           null);
    }

    /**
     * Doubles the logical frame with {@link Scale2x}.
     *
     * @param frame The logical frame.
     * @return The doubled frame, reused.
     */
    private BufferedImage getDoubled(BufferedImage frame)
    {
        if (doubled == null)
        {
            source = new int[width * height];
            target = new int[2 * width * 2 * height];
            doubled = new BufferedImage(2 * width, 2 * height, BufferedImage.TYPE_INT_RGB);
        }
        frame.getRaster().getDataElements(0, 0, width, height, source);
        scale2x.apply(source, width, height, target, 2 * width, 2 * height);
        doubled.getRaster().setDataElements(0, 0, 2 * width, 2 * height, target);
        return doubled;
    }
}
