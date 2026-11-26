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
 * Listener of the voices of an {@link AudioBackend} that end by themselves (ADR-0018, ADR-0065).
 *
 * <p>
 * Called on the runtime thread, at the inbox delivery of the end event the backend posted; never for a stopped
 * voice.
 * </p>
 *
 * @since 11.0.0
 */
@FunctionalInterface
public interface VoiceListener
{
    /**
     * Notifies that a voice played its sound to the end.
     *
     * @param voice The voice, as returned by {@link AudioBackend#play(PcmBuffer)}.
     */
    void onVoiceEnded(int voice);
}
