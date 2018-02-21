/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.b3dgs.lionengine;

/**
 * Animation data container for animation routine.
 * <p>
 * It contains the <code>first</code> and <code>last</code> animation frame number, the animation <code>speed</code>, a
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
 * 1 -&gt; 2 -&gt; 3 -&gt; 2 -&gt; 1 -&gt; 2 -&gt; 3....
 * </p>
 * <p>
 * Example:
 * </p>
 * 
 * <pre>
 * final Animation animation = new Animation(4, 6, 0.125, false, true);
 * print(animation.getFirst()); // 4
 * print(animation.getLast()); // 6
 * print(animation.getSpeed()); // 0.125
 * print(animation.getReverse()); // false
 * print(animation.getRepeat()); // true
 * </pre>
 * 
 * @see Animator
 * @see AnimState
 */
public class Animation implements Nameable
{
    /** Animation default name. */
    public static final String DEFAULT_NAME = "default_anim";
    /** The minimum frame number. */
    public static final int MINIMUM_FRAME = 1;

    /** Animation name. */
    private final String name;
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
     * @param name The animation name.
     * @param firstFrame The first frame (included) index to play (superior or equal to {@link Animation#MINIMUM_FRAME}
     *            ).
     * @param lastFrame The last frame (included) index to play (superior or equal to firstFrame).
     * @param speed The animation playing speed (superior or equal to 0.0).
     * @param reverse <code>true</code> to reverse animation play (play it from first to last, and last to first).
     * @param repeat The repeat state (<code>true</code> will play in loop, <code>false</code> will play once only).
     * @throws LionEngineException If invalid animation.
     */
    public Animation(String name, int firstFrame, int lastFrame, double speed, boolean reverse, boolean repeat)
    {
        Check.superiorOrEqual(firstFrame, Animation.MINIMUM_FRAME);
        Check.superiorOrEqual(lastFrame, firstFrame);
        Check.superiorOrEqual(speed, 0.0);

        this.name = name;
        this.firstFrame = firstFrame;
        this.lastFrame = lastFrame;
        this.speed = speed;
        this.reverse = reverse;
        this.repeat = repeat;
    }

    /**
     * Get the first frame of the animation.
     * 
     * @return The first frame.
     */
    public int getFirst()
    {
        return firstFrame;
    }

    /**
     * Get the last frame if the animation.
     * 
     * @return The last frame.
     */
    public int getLast()
    {
        return lastFrame;
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

    /*
     * Nameable
     */

    @Override
    public String getName()
    {
        return name;
    }

    /*
     * Object
     */

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + name.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object object)
    {
        if (this == object)
        {
            return true;
        }
        if (object == null || object.getClass() != getClass())
        {
            return false;
        }
        final Animation other = (Animation) object;
        return getName().equals(other.getName());
    }
}
