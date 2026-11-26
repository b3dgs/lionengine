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
 * What {@link Audio} does with a short sound played while every voice is busy (ADR-0027, ADR-0033, ADR-0066,
 * REQ-AUDIO-010).
 *
 * @since 11.0.0
 */
public enum VoicePolicy
{
    /**
     * Default: the oldest voice of the lowest priority is stopped and the new sound takes its place, provided that
     * priority is not above the priority of the new sound; otherwise the new sound is not played and
     * {@link AudioBackend#NO_VOICE} is returned.
     */
    STEAL,
    /** The new sound is not played and {@link AudioBackend#NO_VOICE} is returned; the playing voices go on. */
    DROP_NEW,
    /** The new sound is refused with an {@link IllegalStateException}; the playing voices go on. */
    REFUSE
}
