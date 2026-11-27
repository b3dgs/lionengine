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
package com.b3dgs.lionengine.examples.audio;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import com.b3dgs.lionengine.Log;
import com.b3dgs.lionengine.LogLevel;
import com.b3dgs.lionengine.audio.AudioBackend;
import com.b3dgs.lionengine.audio.PcmBuffer;
import com.b3dgs.lionengine.audio.javasound.JavaSoundBackend;
import com.b3dgs.lionengine.runtime.GameLoop;
import com.b3dgs.lionengine.runtime.LoopConfig;
import com.b3dgs.lionengine.runtime.RenderCallback;
import com.b3dgs.lionengine.runtime.UpdateCallback;

/**
 * Plays a jingle on the speakers through the desktop backend {@link JavaSoundBackend} (REQ-AUDIO-002, ADR-0070).
 *
 * <p>
 * The desktop application creates the backend with the inbox of its loop: the backend mixes its voices on its own
 * thread into one Java Sound line, and the end of each note reaches the {@link Jingle} at the inbox delivery, on the
 * runtime thread, which plays the next note. The notes are mono sounds at 22,050 Hz: the backend plays them on both
 * channels and resamples them to its 44,100 Hz output. The game logic, {@link #start(AudioBackend)} and the jingle,
 * only knows {@link AudioBackend}, so that it runs headless on {@code FakeAudioBackend} in the tests; only
 * {@link #main()} creates the Java Sound backend, and closes it at the end.
 * </p>
 *
 * @since 11.0.0
 */
public final class JavaSoundExample
{
    /** Sample rate of the notes, in Hz: half the output rate, resampled by the backend. */
    public static final int NOTE_RATE = 22_050;
    /** Duration of a note, in seconds. */
    public static final double NOTE_SECONDS = 0.2;
    /** Number of notes of the jingle. */
    public static final int NOTES = 4;
    /** Gain of the first note; each next note is a little softer. */
    public static final double FIRST_GAIN = 0.8;
    /** Gain removed at each note. */
    public static final double GAIN_STEP = 0.1;

    /** Frequency of the first note, in Hz (C5). */
    private static final double FIRST_FREQUENCY = 523.25;
    /** Ratio between two notes: a major third. */
    private static final double THIRD = 1.26;
    /** Pause between two iterations of the loop, in nanoseconds. */
    private static final long ITERATION_NANOS = TimeUnit.MILLISECONDS.toNanos(10L);

    /**
     * Private constructor.
     */
    private JavaSoundExample()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Starts the jingle: generates the notes and plays the first one; each end plays the next one.
     *
     * @param backend The audio backend, created with the inbox of the loop.
     * @return The jingle, its first note playing.
     */
    public static Jingle start(AudioBackend backend)
    {
        final PcmBuffer[] notes = new PcmBuffer[NOTES];
        double frequency = FIRST_FREQUENCY;
        for (int i = 0; i < NOTES; i++)
        {
            notes[i] = BeepExample.beep(frequency, NOTE_SECONDS, NOTE_RATE);
            frequency *= THIRD;
        }
        final Jingle jingle = new Jingle(backend, notes, FIRST_GAIN, GAIN_STEP);
        backend.setVoiceListener(jingle);
        jingle.next();
        return jingle;
    }

    /**
     * Opens the Java Sound backend on the inbox of a game loop, plays the jingle to its end, then closes the backend.
     * A Java 25 launch entry point, without arguments.
     */
    // CHECKSTYLE IGNORE LINE: UncommentedMain
    public static void main()
    {
        final Log log = new Log();
        log.setLevel(LogLevel.INFO);
        final Idle idle = new Idle();
        final GameLoop loop = new GameLoop(LoopConfig.DEFAULT, log, idle, idle);
        final JavaSoundBackend backend = new JavaSoundBackend(loop.getInbox());
        try
        {
            final Jingle jingle = start(backend);
            while (!jingle.isDone())
            {
                loop.iterate();
                LockSupport.parkNanos(ITERATION_NANOS);
            }
            log.log(LogLevel.INFO, "Notes played:", jingle.getPlayed());
        }
        finally
        {
            backend.close();
        }
    }

    /**
     * The game of the example, which does nothing but let the loop deliver the inbox.
     */
    private static final class Idle implements UpdateCallback, RenderCallback
    {
        /**
         * Creates the game.
         */
        Idle()
        {
            super();
        }

        @Override
        public void update(double stepSeconds)
        {
            // Nothing to simulate: the jingle advances at the inbox delivery.
        }

        @Override
        public void render(double alpha, double unscaledDeltaSeconds)
        {
            // Nothing to draw.
        }
    }
}
