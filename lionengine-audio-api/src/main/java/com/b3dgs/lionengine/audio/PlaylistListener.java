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
 * Listener of the tracks of a {@link Playlist} that fail (ADR-0018, ADR-0027, ADR-0033, ADR-0068, REQ-AUDIO-008).
 *
 * <p>
 * Called on the runtime thread: during {@link Playlist#play()} for a track that fails to start, or at the inbox
 * delivery of the end of a track whose stream failed. The playlist then skips the track, or stops if it aborts on
 * failure.
 * </p>
 *
 * @since 11.0.0
 */
@FunctionalInterface
public interface PlaylistListener
{
    /**
     * Notifies that a track failed.
     *
     * @param playlist The playlist.
     * @param track The index of the track in the playlist.
     * @param exception The failure: a {@link com.b3dgs.lionengine.resources.ResourceException} for a missing or
     *            unreadable resource, an {@link AudioDecodingException} for an invalid content, an
     *            {@link IllegalStateException} for a released track or no free voice.
     */
    void onTrackFailed(Playlist playlist, int track, RuntimeException exception);
}
