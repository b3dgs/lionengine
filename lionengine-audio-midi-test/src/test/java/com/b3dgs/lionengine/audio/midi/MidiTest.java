/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import org.junit.Assert;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.core.Media;

/**
 * Test the midi class.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class MidiTest
{
    /** Media music. */
    private static Media MUSIC;
    /** Media music. */
    private static Media FAIL;
    /** Midi music. */
    private static Midi midi;

    /**
     * Prepare the test.
     */
    @BeforeClass
    public static void prepareTest()
    {
        MidiTest.MUSIC = new MediaMock("music.mid");
        MidiTest.FAIL = new MediaMock("fail.mid");

        try
        {
            MidiTest.midi = AudioMidi.loadMidi(MidiTest.MUSIC);
            Assert.assertTrue(MidiTest.midi.getTicks() > 0);
        }
        catch (final LionEngineException exception)
        {
            final String message = exception.getMessage();
            Assert.assertTrue(message, message.contains(Midi.ERROR_MIDI));
            Assume.assumeFalse("Midi synthesizer not supported on test machine - Test skipped",
                               message.contains(Midi.ERROR_MIDI));
        }
    }

    /**
     * Test the midi loop function.
     * 
     * @param midi The midi music.
     * @param start The start tick.
     * @param end The end tick.
     */
    private static void testMidiLoop(Midi midi, int start, int end)
    {
        try
        {
            midi.setLoop(start, end);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }
    }

    /**
     * Test with <code>null</code> argument.
     */
    @Test(expected = LionEngineException.class)
    public void testNullArgument()
    {
        AudioMidi.loadMidi(null);
        Assert.fail();
    }

    /**
     * Test with invalid media.
     */
    @Test(expected = LionEngineException.class)
    public void testInvalidMedia()
    {
        AudioMidi.loadMidi(new MediaMock(Constant.EMPTY_STRING));
        Assert.fail();
    }

    /**
     * Test with negative start.
     */
    @Test(expected = LionEngineException.class)
    public void testNegativeStart()
    {
        MidiTest.midi.setStart(-1);
        Assert.fail();
    }

    /**
     * Test with out of range start.
     */
    @Test(expected = LionEngineException.class)
    public void testOutOfRangeStart()
    {
        MidiTest.midi.setStart(Integer.MAX_VALUE);
        Assert.fail();
    }

    /**
     * Test with negative volume.
     */
    @Test(expected = LionEngineException.class)
    public void testNegativeVolume()
    {
        MidiTest.midi.setVolume(-1);
        Assert.fail();
    }

    /**
     * Test with out of range volume.
     */
    @Test(expected = LionEngineException.class)
    public void testOutOfRangeVolume()
    {
        MidiTest.midi.setVolume(101);
        Assert.fail();
    }

    /**
     * Test with invalid music.
     */
    @Test(expected = LionEngineException.class)
    public void testFailMusic()
    {
        final Midi midi2 = AudioMidi.loadMidi(MidiTest.FAIL);
        midi2.play(false);
        Assert.fail();
    }

    /**
     * Test midi functions.
     * 
     * @throws InterruptedException If error.
     */
    @Test
    public void testMidi() throws InterruptedException
    {
        try
        {
            MidiTest.midi.play(false);
            Thread.sleep(250);
            MidiTest.midi.pause();
            Thread.sleep(250);
            MidiTest.midi.resume();
            MidiTest.midi.resume();
            MidiTest.midi.pause();
            MidiTest.midi.pause();
            MidiTest.midi.play(true);
            MidiTest.midi.setVolume(10);
            MidiTest.midi.stop();
        }
        catch (final LionEngineException exception)
        {
            Assert.fail();
        }

        MidiTest.midi.setStart(0);

        MidiTest.testMidiLoop(MidiTest.midi, -1, 0);
        MidiTest.testMidiLoop(MidiTest.midi, 1, 0);
        MidiTest.testMidiLoop(MidiTest.midi, 0, Integer.MAX_VALUE);
        MidiTest.testMidiLoop(MidiTest.midi, -1, -1);
        MidiTest.testMidiLoop(MidiTest.midi, 0, -1);

        final Midi midi2 = AudioMidi.loadMidi(MidiTest.MUSIC);
        midi2.setLoop(6100, 8000);
        midi2.setStart(6100);
        midi2.play(true);
        Thread.sleep(250);
        midi2.setVolume(20);
        Thread.sleep(250);
        midi2.stop();
    }
}
