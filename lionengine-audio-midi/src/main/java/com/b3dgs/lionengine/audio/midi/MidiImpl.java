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
package com.b3dgs.lionengine.audio.midi;

import java.io.BufferedInputStream;
import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Synthesizer;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;

/**
 * Handle midi routine. A midi is a light sound, designed to be played as a background music. Midi are played in a
 * separated thread. It supports the following main controls:
 * <ul>
 * <li>Start index</li>
 * <li>Loop (range setting)</li>
 * <li>Volume</li>
 * <li>Pause and resume</li>
 * </ul>
 * <p>
 * The <code>tick</code> represents the position in the sound data.
 * </p>
 * <p>
 * Example:
 * </p>
 * 
 * <pre>
 * final Midi midi = AudioMidi.loadMidi(Medias.create(&quot;music.mid&quot;));
 * midi.play(false);
 * 
 * Thread.sleep(1000);
 * midi.pause();
 * Thread.sleep(1000);
 * midi.resume();
 * midi.pause();
 * midi.stop();
 * </pre>
 */
public final class MidiImpl implements Midi
{
    /** Error midi. */
    static final String ERROR_MIDI = "No midi output available !";
    /** Error midi data. */
    private static final String ERROR_MIDI_DATA = "Invalid midi data !";
    /** Error midi sequence. */
    private static final String ERROR_MIDI_SEQUENCE = "Error on reading sequence !";

    /**
     * Open the sequence from the media.
     * 
     * @param media The media describing the sequence.
     * @return The opened sequence instance.
     * @throws LionEngineException If media is <code>null</code> or invalid midi.
     */
    private static Sequence openSequence(Media media)
    {
        Check.notNull(media);

        try
        {
            return MidiSystem.getSequence(new BufferedInputStream(media.getInputStream()));
        }
        catch (final IOException exception)
        {
            throw new LionEngineException(exception, media, ERROR_MIDI_SEQUENCE);
        }
        catch (final InvalidMidiDataException exception)
        {
            throw new LionEngineException(exception, media, ERROR_MIDI_DATA);
        }
    }

    /** Current synthesizer. */
    private final Synthesizer synthesizer;
    /** Current sequencer reference. */
    private final Sequencer sequencer;
    /** Current sequence reference. */
    private final Sequence sequence;
    /** Total ticks. */
    private final long ticks;
    /** Paused flag. */
    private boolean paused;

    /**
     * Internal constructor.
     * 
     * @param media The media midi to play.
     * @throws LionEngineException If media is <code>null</code> or invalid midi.
     */
    MidiImpl(Media media)
    {
        try
        {
            sequence = openSequence(media);
            sequencer = MidiSystem.getSequencer(false);
            sequencer.open();
            sequencer.setSequence(sequence);

            synthesizer = MidiSystem.getSynthesizer();
            synthesizer.open();

            if (synthesizer.getDefaultSoundbank() == null)
            {
                sequencer.getTransmitter().setReceiver(MidiSystem.getReceiver());
            }
            else
            {
                sequencer.getTransmitter().setReceiver(synthesizer.getReceiver());
            }
        }
        catch (final InvalidMidiDataException exception)
        {
            throw new LionEngineException(exception, ERROR_MIDI);
        }
        catch (final MidiUnavailableException exception)
        {
            throw new LionEngineException(exception, ERROR_MIDI);
        }

        ticks = sequence.getTickLength();
        paused = false;
    }

    @Override
    public void play()
    {
        play(false);
    }

    @Override
    public void play(boolean loop)
    {
        if (loop)
        {
            sequencer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
        }
        else
        {
            sequencer.setLoopCount(0);
        }
        sequencer.start();
    }

    @Override
    public void setStart(long tick)
    {
        Check.superiorOrEqual(tick, 0);
        Check.inferiorOrEqual(tick, ticks);

        sequencer.setTickPosition(tick);
    }

    @Override
    public void setLoop(long first, long last)
    {
        Check.superiorOrEqual(first, 0);
        Check.inferiorOrEqual(first, last);
        Check.inferiorOrEqual(last, ticks);

        sequencer.setLoopStartPoint(first);
        sequencer.setLoopEndPoint(last);
    }

    @Override
    public void setVolume(int volume)
    {
        Check.superiorOrEqual(volume, VOLUME_MIN);
        Check.inferiorOrEqual(volume, VOLUME_MAX);

        final double maxChannelVolume = 127.0;
        final int channelsNumber = 16;
        final int controlChangeByte = 7;
        final int vol = (int) (volume * maxChannelVolume / VOLUME_MAX);

        if (synthesizer.getDefaultSoundbank() == null)
        {
            try
            {
                final ShortMessage volumeMessage = new ShortMessage();
                for (int i = 0; i < channelsNumber; i++)
                {
                    volumeMessage.setMessage(ShortMessage.CONTROL_CHANGE, i, controlChangeByte, vol);
                    MidiSystem.getReceiver().send(volumeMessage, -1);
                }
            }
            catch (final MidiUnavailableException exception)
            {
                throw new LionEngineException(exception, ERROR_MIDI);
            }
            catch (final InvalidMidiDataException exception)
            {
                throw new LionEngineException(exception, ERROR_MIDI);
            }
        }
        else
        {
            final MidiChannel[] channels = synthesizer.getChannels();
            for (int c = 0; channels != null && c < channels.length; c++)
            {
                channels[c].controlChange(controlChangeByte, vol);
            }
        }
    }

    @Override
    public long getTicks()
    {
        return sequence.getTickLength();
    }

    @Override
    public void stop()
    {
        sequencer.close();
        synthesizer.close();
    }

    @Override
    public void pause()
    {
        if (!paused)
        {
            sequencer.stop();
            paused = true;
        }
    }

    @Override
    public void resume()
    {
        if (paused)
        {
            sequencer.start();
            paused = false;
        }
    }
}
