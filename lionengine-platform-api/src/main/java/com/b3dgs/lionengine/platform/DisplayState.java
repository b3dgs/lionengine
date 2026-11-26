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
package com.b3dgs.lionengine.platform;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.b3dgs.lionengine.Inbox;
import com.b3dgs.lionengine.render.FrameScaling;
import com.b3dgs.lionengine.render.OutputFilter;
import com.b3dgs.lionengine.render.RenderBackend;

/**
 * Display state shared by the platform implementations: the surface size, the attached render backend, the supported
 * screen modes, the display modes, the display listeners, and the resolution of a request into its effective result
 * and presentation geometry (ADR-0006, ADR-0046, ADR-0060, ADR-0061, REQ-DISPLAY-001, REQ-DISPLAY-002,
 * REQ-DISPLAY-003, REQ-DISPLAY-004, REQ-DISPLAY-007, REQ-DISPLAY-008, REQ-FILTER-002).
 *
 * <p>
 * A platform declares the screen modes it supports with {@link #setSupported(java.util.Set)} and the display modes it
 * offers with {@link #setDisplayModes(List)}, selects the display mode to apply with
 * {@link #selectDisplayMode(DisplayMode, DisplayMode)}, then calls
 * {@link #resolve(DisplayRequest, DisplayMode, String)} with the display mode it applies and the reasons it already
 * has, and applies the effective screen mode of the result. This class adds the screen-mode fallback (ADR-0006,
 * {@link #selectScreenMode(ScreenMode)}), the display-mode reason, the scaling fallback ({@code INTEGER} to
 * {@code FIT}, ADR-0006) and the output filters: the effective filters are those the attached backend supports, in
 * the order requested, without {@code NONE}; the others are skipped and explained. The scaling is computed on the
 * current surface size and never changes the logical frame.
 * </p>
 *
 * <p>
 * The platform posts each applied result to the display listeners with {@link #post(Inbox)}. Runtime thread only.
 * Not thread-safe. Contains no frontend type, so it holds for every platform.
 * </p>
 *
 * @since 11.0.0
 */
public final class DisplayState
{
    /** Separator of the reasons. */
    private static final String SEPARATOR = "; ";
    /** Separator before the first reason: none. */
    private static final String NO_SEPARATOR = "";
    /** Reason of the scaling fallback. */
    private static final String REASON_INTEGER = "INTEGER scaling falls back to FIT: the surface is smaller than the "
                                                 + "logical frame";
    /** Reason of the filters requested without backend. */
    private static final String REASON_NO_BACKEND = "Output filters not applied: no render backend attached";
    /** Reason of a screen mode not supported, between the requested and the effective mode. */
    private static final String REASON_SCREEN_MODE = " not supported, ";
    /** End of the reason of a screen mode not supported. */
    private static final String REASON_USED = " used";
    /** Start of the reason of a display mode not applied. */
    private static final String REASON_DISPLAY_MODE = "Display mode ";
    /** Middle of the reason of a display mode not applied. */
    private static final String REASON_NOT_AVAILABLE = " not available, ";
    /** Fallback order after {@link ScreenMode#FULL_SCREEN} (ADR-0006). */
    private static final ScreenMode[] AFTER_FULL_SCREEN =
    {
        ScreenMode.FULL_WINDOW, ScreenMode.WINDOWED
    };
    /** Fallback order after {@link ScreenMode#WINDOWED} (ADR-0006). */
    private static final ScreenMode[] AFTER_WINDOWED =
    {
        ScreenMode.FULL_WINDOW, ScreenMode.FULL_SCREEN
    };
    /** Fallback order after {@link ScreenMode#FULL_WINDOW} (ADR-0006). */
    private static final ScreenMode[] AFTER_FULL_WINDOW =
    {
        ScreenMode.WINDOWED, ScreenMode.FULL_SCREEN
    };
    /** Reason of a filter the backend does not implement, followed by its identifier. */
    private static final String REASON_SKIPPED = "Output filter not implemented by the render backend, skipped: ";
    /** Start of the reason of a replaced filter, followed by its identifier. */
    private static final String REASON_FILTER = "Output filter ";
    /** Middle of the reason of a replaced filter, followed by the identifier of the filter used. */
    private static final String REASON_REPLACED = " not implemented by the render backend, ";

    /** Logical frame width in pixels. */
    private final int logicalWidth;
    /** Logical frame height in pixels. */
    private final int logicalHeight;
    /** Surface width in device pixels. */
    private int surfaceWidth;
    /** Surface height in device pixels. */
    private int surfaceHeight;
    /** No display listener. */
    private static final DisplayListener[] NO_LISTENER = new DisplayListener[0];

    /** Display listeners, in the order they were added; replaced, never modified, so a delivery keeps its own. */
    private DisplayListener[] listeners = NO_LISTENER;
    /** Screen modes the platform supports, never empty. */
    private Set<ScreenMode> supported = EnumSet.allOf(ScreenMode.class);
    /** Display modes the platform offers, without duplicates, empty until set. */
    private List<DisplayMode> displayModes = List.of();
    /** Attached render backend, {@code null} until attached. */
    private RenderBackend backend;
    /** Last result, {@code null} until the first resolution. */
    private EffectiveResult result;
    /** Last presentation geometry, {@code null} until the first resolution. */
    private PresentationGeometry geometry;
    /**
     * Separator written before the next reason of the current resolution: {@link #NO_SEPARATOR} while no reason is
     * written, {@link #SEPARATOR} after. Tracked instead of testing the builder for emptiness, because
     * {@code CharSequence.isEmpty()} is not available on Android API 21 (ADR-0044).
     */
    private String nextSeparator;

    /**
     * Creates a state whose surface has the logical frame size. Nothing is resolved yet.
     *
     * @param logicalWidth The logical frame width in pixels (positive).
     * @param logicalHeight The logical frame height in pixels (positive).
     * @throws IllegalArgumentException If a size is not positive.
     */
    public DisplayState(int logicalWidth, int logicalHeight)
    {
        if (logicalWidth <= 0 || logicalHeight <= 0)
        {
            throw new IllegalArgumentException("Invalid logical frame: " + logicalWidth + 'x' + logicalHeight);
        }
        this.logicalWidth = logicalWidth;
        this.logicalHeight = logicalHeight;
        surfaceWidth = logicalWidth;
        surfaceHeight = logicalHeight;
    }

    /**
     * Sets the surface size; the platform then resolves its request again.
     *
     * @param width The surface width in device pixels, not negative.
     * @param height The surface height in device pixels, not negative.
     */
    public void setSurface(int width, int height)
    {
        surfaceWidth = width;
        surfaceHeight = height;
    }

    /**
     * Sets the render backend that implements the filters; the platform then resolves its request again.
     *
     * @param backend The backend (never {@code null}).
     */
    public void setBackend(RenderBackend backend)
    {
        this.backend = backend;
    }

    /**
     * Adds a display listener, for {@link Platform#addDisplayListener(DisplayListener)}.
     *
     * @param listener The listener (never {@code null}).
     * @throws IllegalArgumentException If the listener is {@code null}.
     */
    public void addListener(DisplayListener listener)
    {
        if (listener == null)
        {
            throw new IllegalArgumentException("Display listener must not be null");
        }
        final DisplayListener[] added = Arrays.copyOf(listeners, listeners.length + 1);
        added[listeners.length] = listener;
        listeners = added;
    }

    /**
     * Removes a display listener, for {@link Platform#removeDisplayListener(DisplayListener)}; no effect if it was not
     * added.
     *
     * @param listener The listener.
     */
    public void removeListener(DisplayListener listener)
    {
        for (int i = 0; i < listeners.length; i++)
        {
            if (listeners[i] == listener)
            {
                final DisplayListener[] removed = new DisplayListener[listeners.length - 1];
                System.arraycopy(listeners, 0, removed, 0, i);
                System.arraycopy(listeners, i + 1, removed, i, listeners.length - i - 1);
                listeners = removed;
                return;
            }
        }
    }

    /**
     * Posts the current result to the inbox (ADR-0060): at its delivery, on the runtime thread, the listeners added by
     * then are called in order with it, over a snapshot, so a listener added or removed during the delivery takes
     * effect after it. Called by the platform at the frame boundary where it applies a request, and after a resize
     * that changes the result.
     *
     * @param inbox The inbox of the loop (never {@code null}).
     * @return {@code true} if the inbox accepted the event; the result stays readable either way.
     */
    public boolean post(Inbox inbox)
    {
        final EffectiveResult posted = result;
        return inbox.post(() ->
        {
            final DisplayListener[] snapshot = listeners;
            for (final DisplayListener listener : snapshot)
            {
                listener.onDisplayChanged(posted);
            }
        });
    }

    /**
     * Sets the screen modes the platform supports (ADR-0060), all of them by default; the platform then resolves its
     * request again.
     *
     * @param modes The supported modes, at least one; copied (never {@code null}).
     * @throws IllegalArgumentException If the set is {@code null} or empty.
     */
    public void setSupported(Set<ScreenMode> modes)
    {
        if (modes == null || modes.isEmpty())
        {
            throw new IllegalArgumentException("At least one screen mode must be supported");
        }
        supported = EnumSet.copyOf(modes);
    }

    /**
     * Tells whether the platform supports a screen mode.
     *
     * @param mode The screen mode (never {@code null}).
     * @return {@code true} if supported.
     * @throws IllegalArgumentException If the mode is {@code null}.
     */
    public boolean isSupported(ScreenMode mode)
    {
        if (mode == null)
        {
            throw new IllegalArgumentException("Screen mode must not be null");
        }
        return supported.contains(mode);
    }

    /**
     * Returns the screen mode applied for a requested one: the requested mode if supported, otherwise the first
     * supported mode of its fallback order (ADR-0006): {@code FULL_SCREEN} then {@code FULL_WINDOW} then
     * {@code WINDOWED}; {@code WINDOWED} then {@code FULL_WINDOW} then {@code FULL_SCREEN}; {@code FULL_WINDOW} then
     * {@code WINDOWED} then {@code FULL_SCREEN}.
     *
     * @param requested The requested mode (never {@code null}).
     * @return The effective mode, always a supported one.
     */
    public ScreenMode selectScreenMode(ScreenMode requested)
    {
        ScreenMode selected = requested;
        if (!supported.contains(requested))
        {
            final ScreenMode[] order = switch (requested)
            {
                case FULL_SCREEN -> AFTER_FULL_SCREEN;
                case WINDOWED -> AFTER_WINDOWED;
                case FULL_WINDOW -> AFTER_FULL_WINDOW;
            };
            selected = order[supported.contains(order[0]) ? 0 : 1];
        }
        return selected;
    }

    /**
     * Tells whether a display mode matches a requested one (ADR-0061): same width and height, and same refresh rate
     * and bit depth where the requested value is known; an unknown requested value matches any value.
     *
     * @param requested The requested display mode (never {@code null}).
     * @param mode The display mode (never {@code null}).
     * @return {@code true} if the display mode satisfies the request.
     */
    public static boolean matches(DisplayMode requested, DisplayMode mode)
    {
        final boolean size = requested.width() == mode.width() && requested.height() == mode.height();
        final boolean rate = !requested.isRefreshRateKnown() || requested.refreshRate() == mode.refreshRate();
        final boolean depth = !requested.isBitDepthKnown() || requested.bitDepth() == mode.bitDepth();
        return size && rate && depth;
    }

    /**
     * Formats a display mode for a reason: {@code "800x600 60Hz 32bits"}, an unknown value written {@code 0}.
     *
     * @param mode The display mode (never {@code null}).
     * @return The text.
     */
    private static String format(DisplayMode mode)
    {
        return mode.width() + "x" + mode.height() + ' ' + mode.refreshRate() + "Hz " + mode.bitDepth() + "bits";
    }

    /**
     * Distance between two values.
     *
     * @param value The value of a candidate.
     * @param target The target value.
     * @return The absolute difference.
     */
    private static long distance(int value, int target)
    {
        return Math.abs((long) value - target);
    }

    /**
     * Tells whether a display mode is strictly nearer to a request than another one: by size, then refresh rate, then
     * bit depth (ADR-0061).
     *
     * @param mode The candidate.
     * @param best The best mode so far.
     * @param requested The requested display mode.
     * @param rate The target refresh rate.
     * @param depth The target bit depth.
     * @return {@code true} if the candidate is strictly nearer.
     */
    private static boolean isNearer(DisplayMode mode, DisplayMode best, DisplayMode requested, int rate, int depth)
    {
        final long size = distance(mode.width(), requested.width()) + distance(mode.height(), requested.height());
        final long bestSize = distance(best.width(), requested.width()) + distance(best.height(), requested.height());
        final long rateDistance = distance(mode.refreshRate(), rate);
        final long bestRate = distance(best.refreshRate(), rate);
        final int order;
        if (size != bestSize)
        {
            order = Long.compare(size, bestSize);
        }
        else if (rateDistance != bestRate)
        {
            order = Long.compare(rateDistance, bestRate);
        }
        else
        {
            order = Long.compare(distance(mode.bitDepth(), depth), distance(best.bitDepth(), depth));
        }
        return order < 0;
    }

    /**
     * Sets the display modes the platform offers (ADR-0061): copied in the given order, duplicates removed, the first
     * occurrence kept.
     *
     * @param modes The display modes (never {@code null}, not empty, no {@code null} element).
     * @throws IllegalArgumentException If {@code modes} is {@code null}, empty or holds {@code null}.
     */
    public void setDisplayModes(List<DisplayMode> modes)
    {
        if (modes == null || modes.isEmpty())
        {
            throw new IllegalArgumentException("At least one display mode must be offered");
        }
        for (final DisplayMode mode : modes)
        {
            if (mode == null)
            {
                throw new IllegalArgumentException("Display mode must not be null");
            }
        }
        displayModes = Collections.unmodifiableList(new ArrayList<>(new LinkedHashSet<>(modes)));
    }

    /**
     * Returns the display modes the platform offers.
     *
     * @return The display modes, unmodifiable, in the order set, empty until set.
     */
    public List<DisplayMode> getDisplayModes()
    {
        return displayModes;
    }

    /**
     * Selects the offered display mode nearest to a requested one (ADR-0006, ADR-0061): the nearest size (smallest
     * sum of the width and height differences), then the nearest refresh rate, then the nearest bit depth, then the
     * first in the order offered; an offered mode equal to the request is thus selected. An unknown requested refresh
     * rate or bit depth is compared to the current mode's, so the monitor keeps it when it can.
     *
     * @param requested The requested display mode, {@code null} to keep the current one.
     * @param current The current display mode (never {@code null}).
     * @return The selected display mode: {@code current} if {@code requested} is {@code null} or no mode is offered.
     * @throws IllegalArgumentException If {@code current} is {@code null}.
     */
    public DisplayMode selectDisplayMode(DisplayMode requested, DisplayMode current)
    {
        if (current == null)
        {
            throw new IllegalArgumentException("Current display mode must not be null");
        }
        if (requested == null || displayModes.isEmpty())
        {
            return current;
        }
        final int rate = requested.isRefreshRateKnown() ? requested.refreshRate() : current.refreshRate();
        final int depth = requested.isBitDepthKnown() ? requested.bitDepth() : current.bitDepth();
        DisplayMode selected = displayModes.get(0);
        for (final DisplayMode mode : displayModes)
        {
            if (isNearer(mode, selected, requested, rate, depth))
            {
                selected = mode;
            }
        }
        return selected;
    }

    /**
     * Resolves a request into the result and the geometry, read with their getters: the screen mode with its
     * fallback ({@link #selectScreenMode(ScreenMode)}), the display mode given by the platform, the scaling with its
     * fallback, and the filters of the backend. A screen-mode fallback is the first reason; a display mode that does
     * not {@link #matches(DisplayMode, DisplayMode) match} the requested one comes next, as
     * {@code "Display mode 800x600 75Hz 32bits not available, 800x600 60Hz 32bits used"}, then the platform's reasons.
     *
     * @param request The request (never {@code null}).
     * @param displayMode The display mode the platform applies, {@code null} if unknown.
     * @param reasons The reasons already found by the platform, joined by {@code "; "}, empty if none (never
     *            {@code null}).
     */
    public void resolve(DisplayRequest request, DisplayMode displayMode, String reasons)
    {
        final ScreenMode screenMode = selectScreenMode(request.screenMode());
        final StringBuilder reason = new StringBuilder();
        nextSeparator = NO_SEPARATOR;
        if (screenMode != request.screenMode())
        {
            addReason(reason, request.screenMode() + REASON_SCREEN_MODE + screenMode + REASON_USED);
        }
        final DisplayMode requestedMode = request.displayMode();
        if (requestedMode != null && displayMode != null && !matches(requestedMode, displayMode))
        {
            addReason(reason, REASON_DISPLAY_MODE + format(requestedMode) + REASON_NOT_AVAILABLE + format(displayMode)
                              + REASON_USED);
        }
        if (!reasons.isEmpty())
        {
            addReason(reason, reasons);
        }
        final FrameScaling scaling = FrameScaling.of(request.scalingPolicy(),
                                                     logicalWidth,
                                                     logicalHeight,
                                                     surfaceWidth,
                                                     surfaceHeight);
        if (scaling.policy() != request.scalingPolicy())
        {
            addReason(reason, REASON_INTEGER);
        }
        final List<OutputFilter> filters = selectFilters(request.filters(), reason);
        final DisplayRequest effective = new DisplayRequest(screenMode, displayMode, scaling.policy(), filters);
        final String text = reason.toString();
        result = new EffectiveResult(request, effective, !text.isEmpty(), text);
        geometry = new PresentationGeometry(scaling.x(),
                                            scaling.y(),
                                            scaling.width(),
                                            scaling.height(),
                                            scaling.scaleX(),
                                            scaling.scaleY());
    }

    /**
     * Appends a reason after the previous ones of the current resolution.
     *
     * @param reasons The reasons so far.
     * @param reason The reason to add (not empty).
     */
    private void addReason(StringBuilder reasons, String reason)
    {
        reasons.append(nextSeparator).append(reason);
        nextSeparator = SEPARATOR;
    }

    /**
     * Adds the effective filter of a requested one to the chain, and its reason when it differs.
     *
     * @param selected The effective chain, completed.
     * @param filter The requested filter.
     * @param effective The effective filter, {@code null} if skipped.
     * @param reason The reasons, completed.
     */
    private void addFilter(List<OutputFilter> selected,
                           OutputFilter filter,
                           OutputFilter effective,
                           StringBuilder reason)
    {
        if (effective == null)
        {
            addReason(reason, REASON_SKIPPED + filter.id());
        }
        else
        {
            if (!effective.equals(filter))
            {
                addReason(reason, REASON_FILTER + filter.id() + REASON_REPLACED + effective.id() + REASON_USED);
            }
            if (!OutputFilter.NONE.equals(effective))
            {
                selected.add(effective);
            }
        }
    }

    /**
     * Follows the fallbacks of a filter to the first one the attached backend implements.
     *
     * @param filter The requested filter, not {@code NONE}.
     * @return The effective filter, {@code NONE} at worst for a scaling filter, {@code null} for a skipped post filter.
     */
    private OutputFilter selectFilter(OutputFilter filter)
    {
        OutputFilter effective = filter;
        while (effective != null && !OutputFilter.NONE.equals(effective) && !backend.isSupported(effective))
        {
            effective = effective.fallback();
        }
        return effective;
    }

    /**
     * Keeps, for each requested filter, the first one of its fallbacks the backend implements (ADR-0006, ADR-0062).
     *
     * @param requested The requested filters.
     * @param reason The reasons, completed with each replaced or skipped filter.
     * @return The effective filters, without {@code NONE}.
     */
    private List<OutputFilter> selectFilters(List<OutputFilter> requested, StringBuilder reason)
    {
        final List<OutputFilter> selected = new ArrayList<>(requested.size());
        boolean missing = false;
        for (final OutputFilter filter : requested)
        {
            final boolean listed = !OutputFilter.NONE.equals(filter);
            if (listed && backend == null)
            {
                missing = true;
            }
            else if (listed)
            {
                addFilter(selected, filter, selectFilter(filter), reason);
            }
        }
        if (missing)
        {
            addReason(reason, REASON_NO_BACKEND);
        }
        return selected;
    }

    /**
     * Returns the surface width.
     *
     * @return The surface width in device pixels.
     */
    public int getSurfaceWidth()
    {
        return surfaceWidth;
    }

    /**
     * Returns the surface height.
     *
     * @return The surface height in device pixels.
     */
    public int getSurfaceHeight()
    {
        return surfaceHeight;
    }

    /**
     * Returns the result of the last resolution.
     *
     * @return The result, {@code null} before the first resolution.
     */
    public EffectiveResult getResult()
    {
        return result;
    }

    /**
     * Returns the presentation geometry of the last resolution.
     *
     * @return The geometry, {@code null} before the first resolution.
     */
    public PresentationGeometry getGeometry()
    {
        return geometry;
    }
}
