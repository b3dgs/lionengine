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
 * Listener of the fades that reach their end (ADR-0018, ADR-0033, ADR-0071, REQ-AUDIO-012).
 *
 * <p>
 * Set on an {@link AudioBackend} with {@link AudioBackend#setFadeListener(FadeListener)}, by {@link Audio} only, and on
 * {@link Audio} with {@link Audio#setFadeListener(FadeListener)}, by the game. Called on the runtime thread, at the
 * inbox delivery of the completion the backend posted, once per voice whose fade reached its target gain; never for a
 * fade replaced by another one, nor for a voice that ended, was stopped or was stolen during its fade.
 * </p>
 *
 * @since 11.0.0
 */
@FunctionalInterface
public interface FadeListener
{
    /**
     * Notifies that the fade of a voice reached its target gain; the voice plays on at that gain.
     *
     * @param voice The voice whose fade ended.
     */
    void onFadeEnded(int voice);
}
