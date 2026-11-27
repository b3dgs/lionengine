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

/**
 * Desktop audio backend on Java Sound: {@link com.b3dgs.lionengine.audio.javasound.JavaSoundBackend} mixes the voices
 * in software into one line on its own thread (ADR-0070, REQ-AUDIO-002).
 *
 * <p>
 * The only module allowed to use {@code javax.sound.sampled}; desktop only, never in Android builds (ADR-0044). Nothing
 * of it appears in {@code audio-api}.
 * </p>
 */
package com.b3dgs.lionengine.audio.javasound;
