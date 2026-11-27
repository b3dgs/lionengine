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

import com.b3dgs.lionengine.InboxEvent;

/**
 * End of a voice posted to the inbox by the mixing thread (ADR-0018, ADR-0070).
 *
 * <p>
 * Events are pooled by their {@link SoftwareMixer}, so that posting an end allocates nothing: the mixing thread takes
 * one from the pool, and its delivery on the runtime thread gives it back before calling the listener.
 * </p>
 */
final class VoiceEnd implements InboxEvent
{
    /** The mixer that posted it. */
    private final SoftwareMixer mixer;
    /** The voice that ended, written under the lock of the mixer. */
    private int voice;

    /**
     * Creates an event of the pool.
     *
     * @param mixer The mixer owning the pool.
     */
    VoiceEnd(SoftwareMixer mixer)
    {
        this.mixer = mixer;
    }

    /**
     * Sets the voice that ended.
     *
     * @param voice The voice.
     */
    void setVoice(int voice)
    {
        this.voice = voice;
    }

    /**
     * Returns the voice that ended.
     *
     * @return The voice.
     */
    int getVoice()
    {
        return voice;
    }

    @Override
    public void deliver()
    {
        mixer.deliver(this);
    }
}
