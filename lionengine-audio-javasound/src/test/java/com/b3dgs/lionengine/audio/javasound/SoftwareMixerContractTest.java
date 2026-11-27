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
package com.b3dgs.lionengine.audio.javasound;

import com.b3dgs.lionengine.InboxSpy;
import com.b3dgs.lionengine.audio.AudioBackend;
import com.b3dgs.lionengine.audio.AudioBackendContract;

/**
 * Binding of the {@link AudioBackendContract} suite on the {@link SoftwareMixer} alone, headless: the test mixes the
 * buffers as the clock (ADR-0045, ADR-0070, REQ-AUDIO-002, REQ-AUDIO-009).
 */
final class SoftwareMixerContractTest extends AudioBackendContract
{
    @Override
    protected AudioBackend createBackend(InboxSpy inbox)
    {
        return new SoftwareMixer(inbox,
                                 JavaSoundBackend.DEFAULT_MAX_VOICES,
                                 JavaSoundBackend.SAMPLE_RATE,
                                 JavaSoundBackend.BUFFER_FRAMES);
    }

    @Override
    protected void advance(AudioBackend backend, int frames)
    {
        final SoftwareMixer mixer = (SoftwareMixer) backend;
        final byte[] bytes = new byte[mixer.getBufferBytes()];
        final int buffers = (frames + JavaSoundBackend.BUFFER_FRAMES - 1) / JavaSoundBackend.BUFFER_FRAMES;
        for (int i = 0; i < buffers; i++)
        {
            mixer.mix(bytes);
        }
    }
}
