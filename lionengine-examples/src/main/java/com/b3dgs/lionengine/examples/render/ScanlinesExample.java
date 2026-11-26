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
package com.b3dgs.lionengine.examples.render;

import java.util.List;

import com.b3dgs.lionengine.platform.DisplayListener;
import com.b3dgs.lionengine.platform.DisplayRequest;
import com.b3dgs.lionengine.platform.Platform;
import com.b3dgs.lionengine.render.CpuFilter;
import com.b3dgs.lionengine.render.CustomFilter;
import com.b3dgs.lionengine.render.FilterKind;
import com.b3dgs.lionengine.render.OutputFilter;
import com.b3dgs.lionengine.render.RenderBackend;
import com.b3dgs.lionengine.resources.ResourceKey;

/**
 * A custom post filter of a game: scanlines that darken every other row of the presented frame (REQ-FILTER-003,
 * ADR-0033, ADR-0064).
 *
 * <p>
 * The game registers its filter on the render backend at startup, then asks for it in the filter chain of a display
 * request, after the scaling filter. This filter has a CPU implementation only, so it runs on the desktop renderer
 * ({@code render-awt}); a GPU backend skips it and reports the skip, until the game adds a fragment shader resource.
 * The CPU implementation allocates nothing and runs once per frame on the scaled frame.
 * </p>
 *
 * @since 11.0.0
 */
public final class ScanlinesExample implements CustomFilter
{
    /** Identifier of the filter, a post filter of the game namespace. */
    public static final OutputFilter SCANLINES = new OutputFilter("game:scanlines", FilterKind.POST);
    /** Mask keeping half of each channel, after a shift by one bit. */
    private static final int HALF = 0x7F7F7F;
    /** Opaque alpha. */
    private static final int OPAQUE = 0xFF000000;

    /**
     * Darkens every other row to half its brightness, the even rows kept.
     *
     * @param source The source pixels, {@code 0xAARRGGBB}.
     * @param width The width in pixels, of both buffers.
     * @param height The height in pixels, of both buffers.
     * @param target The target pixels.
     */
    static void darkenOddRows(int[] source, int width, int height, int[] target)
    {
        for (int y = 0; y < height; y++)
        {
            final boolean odd = (y & 1) == 1;
            for (int x = 0; x < width; x++)
            {
                final int pixel = source[y * width + x];
                target[y * width + x] = odd ? OPAQUE | pixel >> 1 & HALF : pixel;
            }
        }
    }

    /**
     * Registers the filter on the backend, then requests it after the {@code NONE} scaling filter, keeping the other
     * settings, and observes the outcome.
     *
     * @param backend The render backend of the platform.
     * @param platform The started platform, with the backend attached.
     * @param listener The listener told of the effective result once the request is applied.
     * @return The accepted request.
     */
    public static DisplayRequest install(RenderBackend backend, Platform platform, DisplayListener listener)
    {
        backend.register(new ScanlinesExample());
        platform.addDisplayListener(listener);
        final DisplayRequest current = platform.getEffectiveResult().requested();
        return platform.apply(new DisplayRequest(current.screenMode(),
                                                 current.displayMode(),
                                                 current.scalingPolicy(),
                                                 List.of(OutputFilter.NONE, SCANLINES)));
    }

    /**
     * Creates the filter.
     */
    public ScanlinesExample()
    {
        super();
    }

    @Override
    public OutputFilter getFilter()
    {
        return SCANLINES;
    }

    /**
     * Returns no fragment shader: this filter runs on CPU backends only.
     *
     * @return {@code null}.
     */
    @Override
    public ResourceKey getFragmentShader()
    {
        return null;
    }

    @Override
    public CpuFilter getCpuFilter()
    {
        return (source, sourceWidth, sourceHeight, target, targetWidth, targetHeight) -> darkenOddRows(source,
                                                                                                       targetWidth,
                                                                                                       targetHeight,
                                                                                                       target);
    }
}
