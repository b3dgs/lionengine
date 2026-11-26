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
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;

import com.b3dgs.lionengine.platform.PresentationGeometry;
import com.b3dgs.lionengine.render.CpuFilter;
import com.b3dgs.lionengine.render.FilterKind;
import com.b3dgs.lionengine.render.OutputFilter;

/**
 * Chain of output filters run on the CPU when the effective chain holds a custom filter (ADR-0064, REQ-FILTER-003).
 *
 * <p>
 * The logical frame is scaled to the size of the presentation geometry, by the custom scaling filter or by the
 * standard one of {@link AwtPresenter}; each post filter then runs on that buffer, the output of one being the input
 * of the next; the result is drawn at the geometry position without scaling. The filters see opaque
 * {@code 0xAARRGGBB} pixels and the alpha they write is ignored. The buffers are created at the first frame of a
 * geometry size and reused while it stays the same, so a frame allocates nothing. Thread: the runtime thread.
 * </p>
 */
final class AwtCpuChain
{
    /** Opaque alpha. */
    private static final int OPAQUE = 0xFF000000;

    /**
     * Makes pixels opaque.
     *
     * @param pixels The pixels.
     * @param count The number of pixels.
     */
    private static void opaque(int[] pixels, int count)
    {
        for (int i = 0; i < count; i++)
        {
            pixels[i] |= OPAQUE;
        }
    }

    /** Presenter of the standard scaling filters. */
    private final AwtPresenter presenter;
    /** Logical frame width in pixels. */
    private final int logicalWidth;
    /** Logical frame height in pixels. */
    private final int logicalHeight;
    /** Pixels of the logical frame, for a custom scaling filter. */
    private final int[] logical;
    /** Geometry of the last frame, {@code null} before the first one. */
    private PresentationGeometry geometry;
    /** The same scaling at the origin, to draw into {@link #scaled}. */
    private PresentationGeometry local;
    /** Scaled frame of the geometry size, then the filtered result. */
    private BufferedImage scaled;
    /** Graphics of {@link #scaled}. */
    private Graphics2D graphics;
    /** First buffer of the geometry size. */
    private int[] first;
    /** Second buffer of the geometry size. */
    private int[] second;

    /**
     * Creates the chain of a logical frame.
     *
     * @param presenter The presenter of the standard scaling filters.
     * @param logicalWidth The logical frame width in pixels.
     * @param logicalHeight The logical frame height in pixels.
     */
    AwtCpuChain(AwtPresenter presenter, int logicalWidth, int logicalHeight)
    {
        this.presenter = presenter;
        this.logicalWidth = logicalWidth;
        this.logicalHeight = logicalHeight;
        logical = new int[logicalWidth * logicalHeight];
    }

    /**
     * Runs the chain and draws the result.
     *
     * @param target The graphics of the surface, in device pixels.
     * @param frame The logical frame, {@link BufferedImage#TYPE_INT_RGB} of the logical size.
     * @param presentation The presentation geometry, of a positive size.
     * @param chain The effective chain, holding a custom filter.
     * @param cpuFilters The CPU implementations of the registered custom filters by registry id.
     */
    void draw(Graphics2D target,
              BufferedImage frame,
              PresentationGeometry presentation,
              List<OutputFilter> chain,
              Map<String, CpuFilter> cpuFilters)
    {
        prepare(presentation);
        final int width = presentation.width();
        final int height = presentation.height();
        final int count = width * height;
        int next = 0;
        final OutputFilter head = chain.get(0);
        if (head.kind() == FilterKind.SCALING && !head.isStandard())
        {
            frame.getRaster().getDataElements(0, 0, logicalWidth, logicalHeight, logical);
            opaque(logical, logical.length);
            cpuFilters.get(head.id()).apply(logical, logicalWidth, logicalHeight, first, width, height);
            next = 1;
        }
        else
        {
            final boolean scales = head.kind() == FilterKind.SCALING;
            presenter.draw(graphics, frame, local, scales ? head : OutputFilter.NONE);
            scaled.getRaster().getDataElements(0, 0, width, height, first);
            opaque(first, count);
            next = scales ? 1 : 0;
        }
        int[] source = first;
        int[] output = second;
        for (int i = next; i < chain.size(); i++)
        {
            final CpuFilter filter = cpuFilters.get(chain.get(i).id());
            filter.apply(source, width, height, output, width, height);
            final int[] swap = source;
            source = output;
            output = swap;
        }
        scaled.getRaster().setDataElements(0, 0, width, height, source);
        target.drawImage(scaled, presentation.x(), presentation.y(), null);
    }

    /**
     * Creates the buffers of a new geometry size; keeps them otherwise.
     *
     * @param presentation The presentation geometry of the frame.
     */
    private void prepare(PresentationGeometry presentation)
    {
        if (presentation.equals(geometry))
        {
            return;
        }
        final boolean resized = geometry == null
                                || geometry.width() != presentation.width()
                                || geometry.height() != presentation.height();
        geometry = presentation;
        local = new PresentationGeometry(0,
                                         0,
                                         presentation.width(),
                                         presentation.height(),
                                         presentation.scaleX(),
                                         presentation.scaleY());
        if (resized)
        {
            if (graphics != null)
            {
                graphics.dispose();
            }
            scaled = new BufferedImage(presentation.width(), presentation.height(), BufferedImage.TYPE_INT_RGB);
            graphics = scaled.createGraphics();
            first = new int[presentation.width() * presentation.height()];
            second = new int[first.length];
        }
    }
}
