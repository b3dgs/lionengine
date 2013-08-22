package com.b3dgs.lionengine.audio;

import java.io.File;
import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.Transmitter;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;

/**
 * Default midi implementation.
 */
final class MidiPlayer
        implements Midi
{
    /**
     * Open and return the sequencer instance opened.
     * 
     * @return The opened sequencer.
     */
    private static Sequencer openSequencer()
    {
        try
        {
            final Sequencer sequencer = MidiSystem.getSequencer(false);
            sequencer.open();
            return sequencer;
        }
        catch (final MidiUnavailableException exception)
        {
            throw new LionEngineException(exception, "No sequencer available !");
        }
    }

    /**
     * Open and return the synthesizer instance opened.
     * 
     * @return The opened synthesizer.
     */
    private static Synthesizer openSynthesizer()
    {
        try
        {
            final Synthesizer synthesizer = MidiSystem.getSynthesizer();
            synthesizer.open();
            return synthesizer;
        }
        catch (final MidiUnavailableException exception)
        {
            throw new LionEngineException(exception, "No synthesizer available !");
        }
    }

    /** Current sequencer reference. */
    private final Sequencer sequencer;
    /** Current synthesizer reference. */
    private final Synthesizer synthesizer;
    /** Current sequence reference. */
    private final Sequence sequence;
    /** Receiver. */
    private final Receiver synthReceiver;
    /** Transmitter. */
    private final Transmitter seqTransmitter;
    /** Total ticks. */
    private final long ticks;
    /** Paused flag. */
    private boolean paused;

    /**
     * Create a midi player.
     * 
     * @param media The media midi to play.
     */
    MidiPlayer(Media media)
    {
        // Open sequence
        sequencer = MidiPlayer.openSequencer();
        synthesizer = MidiPlayer.openSynthesizer();
        sequence = openSequence(media);
        ticks = sequence.getTickLength();
        paused = false;

        // Prepare output
        try
        {
            synthReceiver = synthesizer.getReceiver();
            seqTransmitter = sequencer.getTransmitter();
            seqTransmitter.setReceiver(synthReceiver);
        }
        catch (final MidiUnavailableException exception)
        {
            throw new LionEngineException(exception, "No midi output available !");
        }

        // Meta data listener
        final int metaDataEventId = 47;
        sequencer.addMetaEventListener(new MetaEventListener()
        {
            @Override
            public void meta(MetaMessage event)
            {
                if (metaDataEventId == event.getType())
                {
                    close();
                }
            }
        });
    }

    /**
     * Close the sequencer and the synthesizer.
     */
    void close()
    {
        sequencer.close();
        synthesizer.close();
    }

    /**
     * Open the sequence from the media and assign it to the sequencer.
     * 
     * @param media The media describing the sequence.
     * @return The opened sequence instance.
     */
    private Sequence openSequence(Media media)
    {
        Check.notNull(media, "Midi file must exists !");
        final File file = Media.getTempFile(media, true, false);
        try
        {
            final Sequence currentSequence = MidiSystem.getSequence(file);
            sequencer.setSequence(currentSequence);
            return currentSequence;
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
    public void setVolume(int volume)
    {
        Check.argument(volume >= Midi.VOLUME_MIN && volume <= Midi.VOLUME_MAX, "Wrong volume value: ",
                String.valueOf(volume), " [" + Midi.VOLUME_MIN + "-" + Midi.VOLUME_MAX + "]");

        final MidiChannel[] channels = synthesizer.getChannels();
        final double maxChannelVolume = 127.0;
        final int volumeChangeId = 7;
        final int vol = (int) (volume * maxChannelVolume / Midi.VOLUME_MAX);

        for (final MidiChannel channel : channels)
        {
            channel.controlChange(volumeChangeId, vol);
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
        synthReceiver.close();
        seqTransmitter.close();
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
