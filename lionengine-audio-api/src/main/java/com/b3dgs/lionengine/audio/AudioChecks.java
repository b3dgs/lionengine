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
package com.b3dgs.lionengine.audio;

/**
 * Argument checks shared by {@link Audio}, the sound handles and {@link MixerGroup} (ADR-0066, ADR-0069, ADR-0071).
 */
final class AudioChecks
{
    /**
     * Checks a gain.
     *
     * @param gain The gain.
     * @throws IllegalArgumentException If it is NaN or outside {@link Audio#MIN_GAIN} to {@link Audio#MAX_GAIN}.
     */
    static void checkGain(double gain)
    {
        if (!(gain >= Audio.MIN_GAIN && gain <= Audio.MAX_GAIN))
        {
            throw new IllegalArgumentException("Gain outside 0 to 1: " + gain);
        }
    }

    /**
     * Checks a fade duration.
     *
     * @param seconds The duration.
     * @throws IllegalArgumentException If it is NaN, infinite or negative.
     */
    static void checkSeconds(double seconds)
    {
        if (!(seconds >= 0.0 && seconds < Double.POSITIVE_INFINITY))
        {
            throw new IllegalArgumentException("Fade duration not finite and not negative: " + seconds);
        }
    }

    /**
     * Checks a priority.
     *
     * @param priority The priority.
     * @throws IllegalArgumentException If it is outside {@link Audio#MIN_PRIORITY} to {@link Audio#MAX_PRIORITY}.
     */
    static void checkPriority(int priority)
    {
        if (priority < Audio.MIN_PRIORITY || priority > Audio.MAX_PRIORITY)
        {
            throw new IllegalArgumentException("Priority outside 0 to 100: " + priority);
        }
    }

    /**
     * Private constructor.
     */
    private AudioChecks()
    {
        throw new UnsupportedOperationException();
    }
}
