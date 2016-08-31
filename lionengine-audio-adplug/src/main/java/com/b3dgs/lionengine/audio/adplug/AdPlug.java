/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.audio.adplug;

import com.b3dgs.lionengine.core.Audio;

/**
 * Allows to play many old music format:
 * <ul>
 * <li>A2M: AdLib Tracker 2 by subz3ro</li>
 * <li>ADL: Westwood ADL File Format</li>
 * <li>AMD: AMUSIC Adlib Tracker by Elyssis</li>
 * <li>BAM: Bob's Adlib Music Format</li>
 * <li>CFF: BoomTracker 4.0 by CUD</li>
 * <li>CMF: Creative Music File Format by Creative Technology</li>
 * <li>D00: EdLib by Vibrants</li>
 * <li>DFM: Digital-FM by R.Verhaag</li>
 * <li>DMO: Twin TrackPlayer by TwinTeam</li>
 * <li>DRO: DOSBox Raw OPL Format</li>
 * <li>DTM: DeFy Adlib Tracker by DeFy</li>
 * <li>HSC: HSC Adlib Composer by Hannes Seifert, HSC-Tracker by Electronic Rats</li>
 * <li>HSP: HSC Packed by Number Six / Aegis Corp.</li>
 * <li>IMF: Apogee IMF File Format</li>
 * <li>KSM: Ken Silverman's Music Format</li>
 * <li>LAA: LucasArts AdLib Audio File Format by LucasArts</li>
 * <li>LDS: LOUDNESS Sound System</li>
 * <li>M: Origin AdLib Music Format</li>
 * <li>MAD: Mlat Adlib Tracker</li>
 * <li>MID: MIDI Audio File Format</li>
 * <li>MKJ: MKJamz by M \ K Productions (preliminary)</li>
 * <li>MSC: AdLib MSCplay</li>
 * <li>MTK: MPU-401 Trakker by SuBZeR0</li>
 * <li>RAD: Reality ADlib Tracker by Reality</li>
 * <li>RAW: RdosPlay RAW file format by RDOS</li>
 * <li>RIX: Softstar RIX OPL Music Format</li>
 * <li>ROL: AdLib Visual Composer by AdLib Inc.</li>
 * <li>S3M: Screamtracker 3 by Future Crew</li>
 * <li>SA2: Surprise! Adlib Tracker 2 by Surprise! Productions</li>
 * <li>SAT: Surprise! Adlib Tracker by Surprise! Productions</li>
 * <li>SCI: Sierra's AdLib Audio File Format</li>
 * <li>SNG: SNGPlay by BUGSY of OBSESSION</li>
 * <li>SNG: Faust Music Creator by FAUST</li>
 * <li>SNG: Adlib Tracker 1.0 by TJ</li>
 * <li>XAD: eXotic ADlib Format by Riven the Mage</li>
 * <li>XMS: XMS-Tracker by MaDoKaN/E.S.G</li>
 * <li>XSM: eXtra Simple Music by Davey W Taylor</li>
 * </ul>
 */
public interface AdPlug extends Audio
{
    /**
     * Pause the audio (can be resumed).
     */
    void pause();

    /**
     * Resume the audio (if paused).
     */
    void resume();
}
