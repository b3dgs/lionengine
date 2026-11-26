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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.b3dgs.lionengine.platform.PresentationGeometry;
import com.b3dgs.lionengine.render.CpuFilter;
import com.b3dgs.lionengine.render.CustomFilter;
import com.b3dgs.lionengine.render.OutputFilter;

/**
 * Output filters of the AWT renderer: the standard scaling filters, the registered custom filters, and the
 * presentation of the logical frame with the effective chain (ADR-0062, ADR-0063, ADR-0064, REQ-FILTER-002,
 * REQ-FILTER-003, REQ-FILTER-004).
 *
 * <p>
 * Implemented: {@link OutputFilter#NONE}, {@link OutputFilter#BILINEAR}, {@link OutputFilter#HQ2X} (drawn by Scale2x),
 * and every registered custom filter that has a CPU implementation, read once at its registration; CRT is not. A
 * chain of standard filters is drawn by {@link AwtPresenter}, a chain holding a custom filter by {@link AwtCpuChain}.
 * Thread: the runtime thread.
 * </p>
 */
final class AwtFilters
{
    /**
     * Tells whether a chain holds a custom filter.
     *
     * @param chain The chain.
     * @return {@code true} if one of its filters is not standard.
     */
    private static boolean hasCustom(List<OutputFilter> chain)
    {
        for (int i = 0; i < chain.size(); i++)
        {
            if (!chain.get(i).isStandard())
            {
                return true;
            }
        }
        return false;
    }

    /** Registered custom filters by registry id. */
    private final Map<String, CustomFilter> custom = new HashMap<>();
    /** CPU implementations of the registered custom filters by registry id, read once at registration. */
    private final Map<String, CpuFilter> cpuFilters = new HashMap<>();
    /** Presenter of the standard scaling filters. */
    private final AwtPresenter presenter;
    /** Chain run on the CPU when a custom filter is present. */
    private final AwtCpuChain cpu;

    /**
     * Creates the filters of a logical frame.
     *
     * @param width The logical frame width in pixels.
     * @param height The logical frame height in pixels.
     */
    AwtFilters(int width, int height)
    {
        presenter = new AwtPresenter(width, height);
        cpu = new AwtCpuChain(presenter, width, height);
    }

    /**
     * Registers a custom filter.
     *
     * @param filter The custom filter.
     * @throws IllegalArgumentException If {@code filter} or its identifier is {@code null}, the identifier is a
     *             standard one, or a filter with the same identifier is already registered.
     */
    void register(CustomFilter filter)
    {
        final OutputFilter id = filter == null ? null : filter.getFilter();
        if (id == null || id.isStandard() || custom.containsKey(id.id()))
        {
            throw new IllegalArgumentException("Invalid custom filter: " + id);
        }
        custom.put(id.id(), filter);
        final CpuFilter implementation = filter.getCpuFilter();
        if (implementation != null)
        {
            cpuFilters.put(id.id(), implementation);
        }
    }

    /**
     * Tells whether a filter is implemented.
     *
     * @param filter The filter (not {@code null}).
     * @return {@code true} for the standard scaling filters and the registered custom filters, of the same kind, with
     *         a CPU implementation.
     */
    boolean isSupported(OutputFilter filter)
    {
        if (filter.isStandard())
        {
            return OutputFilter.NONE.equals(filter)
                   || OutputFilter.BILINEAR.equals(filter)
                   || OutputFilter.HQ2X.equals(filter);
        }
        final CustomFilter registered = custom.get(filter.id());
        return registered != null && filter.equals(registered.getFilter()) && cpuFilters.containsKey(filter.id());
    }

    /**
     * Draws the logical frame into the geometry with the effective chain; nothing for an empty geometry.
     *
     * @param graphics The graphics of the surface, in device pixels.
     * @param frame The logical frame.
     * @param geometry The presentation geometry.
     * @param chain The effective chain, filters this renderer implements.
     */
    void draw(Graphics2D graphics, BufferedImage frame, PresentationGeometry geometry, List<OutputFilter> chain)
    {
        if (geometry.width() > 0 && geometry.height() > 0)
        {
            if (hasCustom(chain))
            {
                cpu.draw(graphics, frame, geometry, chain, cpuFilters);
            }
            else
            {
                presenter.draw(graphics, frame, geometry, AwtPresenter.getScalingFilter(chain));
            }
        }
    }
}
