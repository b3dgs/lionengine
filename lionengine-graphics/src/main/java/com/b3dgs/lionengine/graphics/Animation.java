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
package com.b3dgs.lionengine.graphics;

import java.util.Arrays;

/**
 * Animation: the cells of a sprite sheet shown one after the other, each for a duration in seconds, and a loop mode
 * (ADR-0003, ADR-0026, ADR-0033, REQ-ANIM-001, REQ-SPR-002).
 *
 * <p>
 * Frames are cell indices of one sheet, numbered as {@link SpriteSheet#getCellX(int)} numbers them; a frame may be
 * given more than once. Each frame has its duration, in seconds of simulation time: built in code with one duration
 * for every frame or one per frame, and read from the {@code animations} of a sheet definition, where
 * {@code frameDurations} overrides {@code frameDuration} ({@link SpriteSheet#getAnimation(String)}). The loop mode
 * says what follows the last frame ({@link AnimationLoop}).
 * </p>
 *
 * <p>
 * An animation describes; it does not play: time advances in the animator, from the update step only (REQ-ANIM-002,
 * REQ-ANIM-003). An animation built in code is not bound to a sheet: its frames are checked against the cells of a
 * sheet when it is played on a sprite; an animation read from a definition was checked against its sheet.
 * </p>
 *
 * <p>
 * Immutable and thread-safe; the arrays given are copied. Equal animations have equal frames, frame
 * durations and loop mode ({@code -0.0} cannot occur: every duration is positive).
 * </p>
 *
 * @since 11.0.0
 */
public final class Animation
{
    /** Name of the frames argument, for the messages. */
    private static final String NAME_FRAMES = "Frames";

    /**
     * Tells whether a value is a valid frame duration.
     *
     * @param duration The value in seconds.
     * @return {@code true} if it is positive and finite; {@code false} for zero, a negative value, an infinity and
     *         not a number.
     */
    static boolean isDuration(double duration)
    {
        return duration > 0.0 && duration < Double.POSITIVE_INFINITY;
    }

    /**
     * Checks a duration.
     *
     * @param duration The duration.
     * @return The duration.
     * @throws IllegalArgumentException If the duration is not positive and finite.
     */
    private static double checkDuration(double duration)
    {
        if (!isDuration(duration))
        {
            throw new IllegalArgumentException("Frame duration not positive and finite, in seconds: " + duration);
        }
        return duration;
    }

    /**
     * Checks and copies the frames.
     *
     * @param frames The frames.
     * @return The copy.
     * @throws IllegalArgumentException If the frames are {@code null}, empty or a frame is negative.
     */
    private static int[] copyFrames(int[] frames)
    {
        ImageCache.checkNotNull(frames, NAME_FRAMES);
        if (frames.length == 0)
        {
            throw new IllegalArgumentException("No frame");
        }
        for (final int frame : frames)
        {
            if (frame < 0)
            {
                throw new IllegalArgumentException("Frame negative: " + frame);
            }
        }
        return frames.clone();
    }

    /**
     * Returns one duration per frame.
     *
     * @param count The number of frames.
     * @param duration The duration of every frame.
     * @return The durations.
     * @throws IllegalArgumentException If the duration is not positive and finite.
     */
    private static double[] fill(int count, double duration)
    {
        final double[] durations = new double[count];
        Arrays.fill(durations, checkDuration(duration));
        return durations;
    }

    /**
     * Checks and copies the frame durations.
     *
     * @param count The number of frames.
     * @param durations The durations.
     * @return The copy.
     * @throws IllegalArgumentException If the durations are {@code null}, not one per frame, or one is not positive
     *             and finite.
     */
    private static double[] copyDurations(int count, double[] durations)
    {
        ImageCache.checkNotNull(durations, "Frame durations");
        if (durations.length != count)
        {
            throw new IllegalArgumentException("One duration per frame expected, frames: "
                                               + count
                                               + ", durations: "
                                               + durations.length);
        }
        for (final double duration : durations)
        {
            checkDuration(duration);
        }
        return durations.clone();
    }

    /**
     * Checks the loop mode.
     *
     * @param mode The loop mode.
     * @return The loop mode.
     * @throws IllegalArgumentException If the loop mode is {@code null}.
     */
    private static AnimationLoop checkLoop(AnimationLoop mode)
    {
        ImageCache.checkNotNull(mode, "Loop");
        return mode;
    }

    /** The frames, cell indices. */
    private final int[] frames;
    /** The duration of each frame in seconds. */
    private final double[] durations;
    /** The loop mode. */
    private final AnimationLoop loop;

    /**
     * Creates an animation whose frames all last the same duration.
     *
     * @param frames The frames, cell indices of a sheet, at least one, each zero or positive; copied (never
     *            {@code null}).
     * @param frameDuration The duration of each frame in seconds, positive and finite.
     * @param loop The loop mode (never {@code null}).
     * @throws IllegalArgumentException If an argument is {@code null}, there is no frame, a frame is negative, or the
     *             duration is not positive and finite.
     */
    public Animation(int[] frames, double frameDuration, AnimationLoop loop)
    {
        super();

        this.frames = copyFrames(frames);
        durations = fill(this.frames.length, frameDuration);
        this.loop = checkLoop(loop);
    }

    /**
     * Creates an animation with a duration per frame.
     *
     * @param frames The frames, cell indices of a sheet, at least one, each zero or positive; copied (never
     *            {@code null}).
     * @param frameDurations The duration of each frame in seconds, one per frame, each positive and finite; copied
     *            (never {@code null}).
     * @param loop The loop mode (never {@code null}).
     * @throws IllegalArgumentException If an argument is {@code null}, there is no frame, a frame is negative, the
     *             durations are not one per frame, or a duration is not positive and finite.
     */
    public Animation(int[] frames, double[] frameDurations, AnimationLoop loop)
    {
        super();

        this.frames = copyFrames(frames);
        durations = copyDurations(this.frames.length, frameDurations);
        this.loop = checkLoop(loop);
    }

    /**
     * Returns the number of frames.
     *
     * @return The number of frames, at least one.
     */
    public int getFrameCount()
    {
        return frames.length;
    }

    /**
     * Returns the cell of a frame.
     *
     * @param index The index of the frame, from {@code 0}.
     * @return The cell index in the sheet.
     * @throws IndexOutOfBoundsException If the index is outside {@code [0, getFrameCount())}.
     */
    public int getFrame(int index)
    {
        checkIndex(index);
        return frames[index];
    }

    /**
     * Returns the duration of a frame.
     *
     * @param index The index of the frame, from {@code 0}.
     * @return The duration in seconds, positive.
     * @throws IndexOutOfBoundsException If the index is outside {@code [0, getFrameCount())}.
     */
    public double getFrameDuration(int index)
    {
        checkIndex(index);
        return durations[index];
    }

    /**
     * Returns the loop mode.
     *
     * @return The loop mode.
     */
    public AnimationLoop getLoop()
    {
        return loop;
    }

    /**
     * Checks a frame index.
     *
     * @param index The index.
     * @throws IndexOutOfBoundsException If the index is outside {@code [0, getFrameCount())}.
     */
    private void checkIndex(int index)
    {
        if (index < 0 || index >= frames.length)
        {
            throw new IndexOutOfBoundsException("Animation frame: " + index + " of " + frames.length);
        }
    }

    @Override
    public boolean equals(Object object)
    {
        return object instanceof final Animation other
               && loop == other.loop
               && Arrays.equals(frames, other.frames)
               && Arrays.equals(durations, other.durations);
    }

    @Override
    public int hashCode()
    {
        return (Arrays.hashCode(frames) * 31 + Arrays.hashCode(durations)) * 31 + loop.hashCode();
    }

    /**
     * Returns a text of the animation, for messages: its frames, their durations and its loop mode, for example
     * {@code Animation[frames=[0, 1], durations=[0.1, 0.2], loop=loop]}.
     *
     * @return The text.
     */
    @Override
    public String toString()
    {
        return "Animation[frames="
               + Arrays.toString(frames)
               + ", durations="
               + Arrays.toString(durations)
               + ", loop="
               + loop.getName()
               + "]";
    }
}
