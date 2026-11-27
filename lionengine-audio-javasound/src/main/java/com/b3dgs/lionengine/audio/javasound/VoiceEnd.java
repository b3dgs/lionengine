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
 * End of a voice, or end of the fade of a voice, posted to the inbox by the mixing thread (ADR-0018, ADR-0070,
 * ADR-0071).
 *
 * <p>
 * Events are pooled by their {@link SoftwareMixer}, so that posting an end allocates nothing: the mixing thread takes
 * one from the pool, and its delivery on the runtime thread gives it back before calling the listener of its kind.
 * </p>
 */
final class VoiceEnd implements InboxEvent
{
    /** The mixer that posted it. */
    private final SoftwareMixer mixer;
    /** The voice that ended, written under the lock of the mixer. */
    private int voice;
    /** {@code true} for the end of a fade, {@code false} for the end of the voice. */
    private boolean fade;

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
     * Sets the voice that ended, or whose fade ended.
     *
     * @param voice The voice.
     * @param fade {@code true} for the end of a fade, {@code false} for the end of the voice.
     */
    void set(int voice, boolean fade)
    {
        this.voice = voice;
        this.fade = fade;
    }

    /**
     * Tells the kind of the event.
     *
     * @return {@code true} for the end of a fade, {@code false} for the end of the voice.
     */
    boolean isFade()
    {
        return fade;
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
