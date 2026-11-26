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
package com.b3dgs.lionengine;

import java.util.ArrayList;
import java.util.List;

/**
 * Asynchronous handles followed together, for example by a loading screen (ADR-0019, ADR-0031, ADR-0040,
 * REQ-RT-010).
 *
 * <p>
 * The progress of a group is the mean progress of its members, {@code 1} when it has none. Its state, read from the
 * members in this order: {@link AsyncState#FAILED} as soon as one member failed, with the failure of the first one
 * added ({@link #getFailure()}); otherwise {@link AsyncState#CANCELLED} if one member was cancelled; otherwise
 * {@link AsyncState#READY} when every member is ready, an empty group included; otherwise
 * {@link AsyncState#PENDING}. So a loading screen switches on {@code READY} and reports a failure at once. The group
 * changes only when its members do, at inbox delivery; it holds no value and releases nothing: each loaded value
 * stays owned by the code that requested it.
 * </p>
 *
 * <p>
 * Reading the state, the progress and the failure allocates nothing, so a loading screen may read them at every
 * frame. Threads: runtime thread only, like the handles (ADR-0018).
 * </p>
 *
 * @since 11.0.0
 */
public final class LoadGroup
{
    /** Error message of a {@code null} handle. */
    private static final String ERROR_NULL = "Handle must not be null";
    /** Error message of a handle added twice. */
    private static final String ERROR_DUPLICATE = "Handle already in the group";

    /** The members, in the order they were added. */
    private final List<AsyncHandle<?>> handles = new ArrayList<>();

    /**
     * Creates an empty group.
     */
    public LoadGroup()
    {
        super();
    }

    /**
     * Adds a handle to the group.
     *
     * @param handle The handle, in any state (never {@code null}, not already in the group).
     * @throws IllegalArgumentException If the handle is {@code null} or already in the group.
     */
    public void add(AsyncHandle<?> handle)
    {
        if (handle == null)
        {
            throw new IllegalArgumentException(ERROR_NULL);
        }
        final int count = handles.size();
        for (int i = 0; i < count; i++)
        {
            if (handles.get(i) == handle)
            {
                throw new IllegalArgumentException(ERROR_DUPLICATE);
            }
        }
        handles.add(handle);
    }

    /**
     * Returns the number of handles.
     *
     * @return The number of members.
     */
    public int size()
    {
        return handles.size();
    }

    /**
     * Returns the state of the group.
     *
     * @return The state read from the members, as described by the class (never {@code null}).
     */
    public AsyncState getState()
    {
        boolean failed = false;
        boolean cancelled = false;
        boolean pending = false;
        final int count = handles.size();
        for (int i = 0; i < count; i++)
        {
            final AsyncState member = handles.get(i).getState();
            failed |= member == AsyncState.FAILED;
            cancelled |= member == AsyncState.CANCELLED;
            pending |= member == AsyncState.PENDING;
        }
        final AsyncState state;
        if (failed)
        {
            state = AsyncState.FAILED;
        }
        else if (cancelled)
        {
            state = AsyncState.CANCELLED;
        }
        else if (pending)
        {
            state = AsyncState.PENDING;
        }
        else
        {
            state = AsyncState.READY;
        }
        return state;
    }

    /**
     * Returns the progress of the group.
     *
     * @return The mean progress of the members in {@code [0, 1]}, {@code 1} for an empty group.
     */
    public double getProgress()
    {
        final int count = handles.size();
        double sum = 0.0;
        for (int i = 0; i < count; i++)
        {
            sum += handles.get(i).getProgress();
        }
        final double progress;
        if (count == 0)
        {
            progress = 1.0;
        }
        else
        {
            progress = sum / count;
        }
        return progress;
    }

    /**
     * Returns the failure of the group.
     *
     * @return The failure of the first member added that failed, {@code null} if none failed.
     */
    public RuntimeException getFailure()
    {
        RuntimeException failure = null;
        final int count = handles.size();
        for (int i = 0; i < count && failure == null; i++)
        {
            failure = handles.get(i).getFailure();
        }
        return failure;
    }

    /**
     * Cancels every member still pending, best effort (see {@link AsyncHandle#cancel()}).
     *
     * @return The number of members cancelled by this call.
     */
    public int cancel()
    {
        int cancelled = 0;
        final int count = handles.size();
        for (int i = 0; i < count; i++)
        {
            if (handles.get(i).cancel())
            {
                cancelled++;
            }
        }
        return cancelled;
    }
}
