/*
 * Copyright (C) 2013 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.audio;

import java.io.File;
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
import com.b3dgs.lionengine.core.Media;

/**
 * Default midi implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
final class MidiPlayer
        implements Midi
{
    /**
     * Open the sequence from the media.
     * 
     * @param media The media describing the sequence.
     * @return The opened sequence instance.
     */
    private static Sequence openSequence(Media media)
    {
        Check.notNull(media, "Midi file must exists !");
        final File file = Media.getTempFile(media, true, false);
        try
        {
            return MidiSystem.getSequence(file);
        }
        catch (final IOException exception)
        {
            throw new LionEngineException(exception, media, "Error on reading sequence !");
        }
        catch (final InvalidMidiDataException exception)
        {
            throw new LionEngineException(exception, media, "Invalid midi data !");
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
     * Constructor.
     * 
     * @param media The media midi to play.
     */
    MidiPlayer(Media media)
    {
        try
        {
            sequence = MidiPlayer.openSequence(media);
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
        catch (final InvalidMidiDataException
                     | MidiUnavailableException exception)
        {
            throw new LionEngineException(exception, "No midi output available !");
        }

        ticks = sequence.getTickLength();
        paused = false;
    }

    /*
     * Midi
     */

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
        Check.argument(tick >= 0 && tick <= ticks, "Wrong tick value: ", String.valueOf(tick), " (total = )",
                String.valueOf(ticks));

        sequencer.setTickPosition(tick);
    }

    @Override
    public void setLoop(long first, long last)
    {
        Check.argument(first >= 0 && first <= last, "Wrong first value: ", String.valueOf(first), " (total = )",
                String.valueOf(ticks));
        Check.argument(last <= ticks, "Wrong last value: ", String.valueOf(last), " (total = )", String.valueOf(ticks));

        sequencer.setLoopStartPoint(first);
        sequencer.setLoopEndPoint(last);
    }

    @Override
    public void setVolume(final int volume)
    {
        Check.argument(volume >= Midi.VOLUME_MIN && volume <= Midi.VOLUME_MAX, "Wrong volume value: ",
                String.valueOf(volume), " [" + Midi.VOLUME_MIN + "-" + Midi.VOLUME_MAX + "]");

        final double maxChannelVolume = 127.0;
        final int vol = (int) (volume * maxChannelVolume / Midi.VOLUME_MAX);

        if (synthesizer.getDefaultSoundbank() == null)
        {
            try
            {
                final ShortMessage volumeMessage = new ShortMessage();
                for (int i = 0; i < 16; i++)
                {
                    volumeMessage.setMessage(ShortMessage.CONTROL_CHANGE, i, 7, vol);
                    MidiSystem.getReceiver().send(volumeMessage, -1);
                }
            }
            catch (MidiUnavailableException
                   | InvalidMidiDataException exception)
            {
                return;
            }
        }
        else
        {
            final MidiChannel[] channels = synthesizer.getChannels();
            for (int c = 0; channels != null && c < channels.length; c++)
            {
                channels[c].controlChange(7, vol);
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
