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
 * Desktop OGG Vorbis decoding on JOrbis: {@link com.b3dgs.lionengine.audio.codec.ogg.OggVorbisDecoder} decodes
 * {@code .ogg} files entirely or by streaming, for the short sounds and the long sounds of {@code audio-api}
 * (ADR-0033, ADR-0068, ADR-0073, REQ-AUDIO-002).
 *
 * <p>
 * The only module allowed to use JOrbis ({@code com.jcraft.jogg}, {@code com.jcraft.jorbis}); no JOrbis type appears in
 * its public API. Desktop only, never in Android builds, where the platform decodes OGG Vorbis (ADR-0044). The game
 * registers the decoder explicitly (ADR-0020).
 * </p>
 */
package com.b3dgs.lionengine.audio.codec.ogg;
