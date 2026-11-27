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
 * Listener of the end of a long sound started by a {@link Playlist}, on the runtime thread (ADR-0018, ADR-0068).
 */
interface TrackListener
{
    /**
     * Notifies that the sound played to its end, or that its stream failed.
     *
     * @param sound The sound.
     * @param failure The failure of its stream, {@code null} if it played to its end.
     */
    void onTrackEnded(LongSound sound, RuntimeException failure);

    /**
     * Notifies that the sound was stopped, or released while playing.
     *
     * @param sound The sound.
     */
    void onTrackStopped(LongSound sound);
}
