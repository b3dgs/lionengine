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

/**
 * Plays an {@link Animation}: the state machine of play, pause, resume and stop, and the frame advance of each loop
 * mode, driven by the simulation update step (ADR-0026, ADR-0058, REQ-ANIM-002, REQ-ANIM-003, REQ-SPR-002).
 *
 * <p>
 * States ({@link AnimatorState}): an animator is created stopped, without animation.
 * </p>
 *
 * <ul>
 * <li>{@link #play(Animation)} is valid in every state: it starts the animation from its first frame, also when it is
 * already playing;</li>
 * <li>{@link #pause()} is valid while playing and keeps the frame and the time spent in it;</li>
 * <li>{@link #resume()} is valid while paused and continues from them;</li>
 * <li>{@link #stop()} is valid in every state and returns to the first frame;</li>
 * <li>a {@link AnimationLoop#ONCE} animation becomes {@link AnimatorState#FINISHED} once its last frame was shown for
 * its full duration, and stays on that frame.</li>
 * </ul>
 *
 * <p>
 * A call that is not valid in the current state throws an {@link IllegalStateException} and changes nothing.
 * </p>
 *
 * <p>
 * Time: {@link #update(double)} is called from the update step with the constant step in seconds, and advances only a
 * playing animator. When the time spent in the current frame reaches its duration, the next frame starts and the time
 * left over carries into it, so several frames may pass in one step and the speed does not depend on the step length.
 * After the last frame, {@link AnimationLoop#LOOP} restarts at the first frame, and {@link AnimationLoop#PING_PONG}
 * turns back, the end frames shown once per turn ({@code 0 1 2 1 0 1 …}). Reading the animator never changes it, so
 * rendering cannot feed time back (REQ-LOOP-006).
 * </p>
 *
 * <p>
 * The animator holds no image: it gives the frame to draw, a cell of the sheet of the animation ({@link #getFrame()}).
 * Nothing allocates after construction. Runtime thread only, not thread-safe (ADR-0018).
 * </p>
 *
 * @since 11.0.0
 */
public final class Animator
{
    /** Error message of a call that is not valid in the current state. */
    private static final String ERROR_STATE = "Animator ";

    /** The animation, {@code null} before the first play. */
    private Animation animation;
    /** The state. */
    private AnimatorState state = AnimatorState.STOPPED;
    /** The index of the current frame in the animation. */
    private int index;
    /** The time spent in the current frame, in seconds. */
    private double time;
    /** {@code true} while a ping-pong animation plays backward. */
    private boolean backward;

    /**
     * Creates a stopped animator, without animation.
     */
    public Animator()
    {
        super();
    }

    /**
     * Plays an animation from its first frame, in every state.
     *
     * @param played The animation (never {@code null}).
     * @throws IllegalArgumentException If the animation is {@code null}.
     */
    public void play(Animation played)
    {
        ImageCache.checkNotNull(played, "Animation");
        animation = played;
        rewind();
        state = AnimatorState.PLAYING;
    }

    /**
     * Pauses a playing animation, keeping the frame and the time spent in it.
     *
     * @throws IllegalStateException If the animator is not playing.
     */
    public void pause()
    {
        check(AnimatorState.PLAYING, "pause");
        state = AnimatorState.PAUSED;
    }

    /**
     * Resumes a paused animation, from the frame and the time kept by the pause.
     *
     * @throws IllegalStateException If the animator is not paused.
     */
    public void resume()
    {
        check(AnimatorState.PAUSED, "resume");
        state = AnimatorState.PLAYING;
    }

    /**
     * Stops the animation and returns to its first frame, in every state.
     */
    public void stop()
    {
        rewind();
        state = AnimatorState.STOPPED;
    }

    /**
     * Advances a playing animation by one update step; changes nothing in another state. Allocates nothing.
     *
     * @param stepSeconds The update step in seconds, zero or positive and finite.
     * @throws IllegalArgumentException If the step is negative, infinite or not a number.
     */
    public void update(double stepSeconds)
    {
        final boolean valid = stepSeconds >= 0.0 && stepSeconds < Double.POSITIVE_INFINITY;
        if (!valid)
        {
            throw new IllegalArgumentException("Step not zero or positive and finite, in seconds: " + stepSeconds);
        }
        if (state == AnimatorState.PLAYING)
        {
            time += stepSeconds;
            double duration = animation.getFrameDuration(index);
            while (state == AnimatorState.PLAYING && time >= duration)
            {
                time -= duration;
                next();
                duration = animation.getFrameDuration(index);
            }
        }
    }

    /**
     * Returns the state.
     *
     * @return The state, {@link AnimatorState#STOPPED} at creation.
     */
    public AnimatorState getState()
    {
        return state;
    }

    /**
     * Returns the animation.
     *
     * @return The animation of the last play, {@code null} before the first play.
     */
    public Animation getAnimation()
    {
        return animation;
    }

    /**
     * Returns the index of the current frame in the animation.
     *
     * @return The index, from {@code 0}; {@code 0} before the first play.
     */
    public int getFrameIndex()
    {
        return index;
    }

    /**
     * Returns the frame to draw: the cell of the current frame.
     *
     * @return The cell index in the sheet of the animation.
     * @throws IllegalStateException If no animation was played yet.
     */
    public int getFrame()
    {
        if (animation == null)
        {
            throw new IllegalStateException(ERROR_STATE + "without animation");
        }
        return animation.getFrame(index);
    }

    /**
     * Returns the time spent in the current frame.
     *
     * @return The time in seconds, zero or positive and below the duration of the frame while playing.
     */
    public double getFrameTime()
    {
        return time;
    }

    /**
     * Checks the state before a call.
     *
     * @param expected The state in which the call is valid.
     * @param call The name of the call, for the message.
     * @throws IllegalStateException If the animator is in another state.
     */
    private void check(AnimatorState expected, String call)
    {
        if (state != expected)
        {
            throw new IllegalStateException(ERROR_STATE + state + ", cannot " + call);
        }
    }

    /**
     * Returns to the first frame, forward, with no time spent.
     */
    private void rewind()
    {
        index = 0;
        time = 0.0;
        backward = false;
    }

    /**
     * Moves to the next frame of the loop mode, or finishes a {@link AnimationLoop#ONCE} animation.
     */
    private void next()
    {
        final int last = animation.getFrameCount() - 1;
        index = switch (animation.getLoop())
        {
            case ONCE -> nextOnce(last);
            case LOOP -> nextLoop(last);
            case PING_PONG -> nextPingPong(last);
        };
    }

    /**
     * Returns the next frame, or finishes on the last one, dropping the time left over.
     *
     * @param last The index of the last frame.
     * @return The index of the next frame, the last one once finished.
     */
    private int nextOnce(int last)
    {
        final int next;
        if (index < last)
        {
            next = index + 1;
        }
        else
        {
            next = last;
            time = 0.0;
            state = AnimatorState.FINISHED;
        }
        return next;
    }

    /**
     * Returns the next frame, the first after the last.
     *
     * @param last The index of the last frame.
     * @return The index of the next frame.
     */
    private int nextLoop(int last)
    {
        final int next;
        if (index < last)
        {
            next = index + 1;
        }
        else
        {
            next = 0;
        }
        return next;
    }

    /**
     * Returns the next frame forward or backward, turning at both ends; a single frame stays.
     *
     * @param last The index of the last frame.
     * @return The index of the next frame.
     */
    private int nextPingPong(int last)
    {
        int next = index;
        if (last > 0)
        {
            if (backward && index == 0 || !backward && index == last)
            {
                backward = !backward;
            }
            if (backward)
            {
                next--;
            }
            else
            {
                next++;
            }
        }
        return next;
    }
}
