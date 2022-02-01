/*
 * Copyright (C) 2013-2022 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

/**
 * Animation data.
 * <p>
 * Defines the <code>first</code> and <code>last</code> animation frame number, the animation <code>speed</code>, a
 * <code>reverse</code> flag (for reversed animation), and a <code>repeat</code> flag (for looped animation).
 * </p>
 * <ul>
 * <li><code>first</code>: first frame of the animation that will be played (included).</li>
 * <li><code>last</code>: last frame of the animation that will be played (included).</li>
 * <li><code>speed</code>: animation speed.</li>
 * <li><code>reverse</code>: reverse flag (1 -&gt; 2 -&gt; 3 -&gt; 2 -&gt; 1).</li>
 * <li><code>repeat</code>: repeat flag (1 -&gt; 2 -&gt; 3 -&gt; 1 -&gt; 2 -&gt; 3...).</li>
 * </ul>
 * <p>
 * Note: <code>reverse</code> and <code>repeat</code> can also be combined to play in loop an animation in reverse:
 * </p>
 * 
 * <pre>
 * <code>1 -&gt; 2 -&gt; 3 -&gt; 2 -&gt; 1 -&gt; 2 -&gt; 3....</code>
 * </pre>
 * <p>
 * This class is Thread-Safe.
 * </p>
 */
public final class Animation extends NameableAbstract
{
    /** Animation default name. */
    public static final String DEFAULT_NAME = "default_anim";
    /** The minimum frame number. */
    public static final int MINIMUM_FRAME = 1;
    /** The minimum animation speed. */
    public static final double MINIMUM_SPEED = 0.0;

    /** First animation frame. */
    private final int firstFrame;
    /** Last animation frame. */
    private final int lastFrame;
    /** Animation speed. */
    private final double speed;
    /** Reverse flag. */
    private final boolean reverse;
    /** Repeat flag. */
    private final boolean repeat;

    /**
     * Create an animation, which can be played by an {@link Animator}.
     * 
     * @param name The animation name (must not be <code>null</code>).
     * @param firstFrame The first frame (included) to play (superior or equal to {@value #MINIMUM_FRAME}).
     * @param lastFrame The last frame (included) to play (superior or equal to firstFrame).
     * @param speed The animation playing speed (superior or equal to #MINIMUM_SPEED).
     * @param reverse <code>true</code> to reverse animation play (play it from first to last, and last to first).
     * @param repeat The repeat state (<code>true</code> will play in loop, <code>false</code> will play once only).
     * @throws LionEngineException If invalid parameters.
     */
    public Animation(String name, int firstFrame, int lastFrame, double speed, boolean reverse, boolean repeat)
    {
        super(name);

        Check.superiorOrEqual(firstFrame, MINIMUM_FRAME);
        Check.superiorOrEqual(lastFrame, firstFrame);
        Check.superiorOrEqual(speed, MINIMUM_SPEED);

        this.firstFrame = firstFrame;
        this.lastFrame = lastFrame;
        this.speed = speed;
        this.reverse = reverse;
        this.repeat = repeat;
    }

    /**
     * Get the first frame.
     * 
     * @return The first frame.
     */
    public int getFirst()
    {
        return firstFrame;
    }

    /**
     * Get the last frame.
     * 
     * @return The last frame.
     */
    public int getLast()
    {
        return lastFrame;
    }

    /**
     * Get frames number.
     * 
     * @return The frames number.
     */
    public int getFrames()
    {
        return lastFrame - firstFrame + 1;
    }

    /**
     * Get the animation speed.
     * 
     * @return The animation speed.
     */
    public double getSpeed()
    {
        return speed;
    }

    /**
     * Get the reverse state.
     * 
     * @return The reverse state.
     */
    public boolean hasReverse()
    {
        return reverse;
    }

    /**
     * Get the repeat state.
     * 
     * @return The repeat state.
     */
    public boolean hasRepeat()
    {
        return repeat;
    }
}
