/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.core.Core;
import com.b3dgs.lionengine.core.FactoryMediaProvider;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.Verbose;
import com.b3dgs.lionengine.mock.FactoryMediaMock;

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

    /**
     * Prepare the test.
     */
    @BeforeClass
    public static void prepareTest()
    {
        FactoryMediaProvider.setFactoryMedia(new FactoryMediaMock());
        MidiTest.MUSIC = Core.MEDIA.create("resources", "music.mid");
        MidiTest.FAIL = Core.MEDIA.create("resources", "fail.mid");
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        FactoryMediaProvider.setFactoryMedia(null);
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
     * Test midi functions.
     * 
     * @throws InterruptedException If error.
     */
    @Test
    public void testMidi() throws InterruptedException
    {
        try
        {
            AudioMidi.loadMidi(null);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }

        try
        {
            AudioMidi.loadMidi(Core.MEDIA.create(""));
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }

        final Midi midi;
        try
        {
            midi = AudioMidi.loadMidi(MidiTest.MUSIC);
            Assert.assertTrue(midi.getTicks() > 0);
        }
        catch (final LionEngineException exception)
        {
            Assert.assertTrue(exception.getMessage(), exception.getMessage().contains("No synthesizer available"));
            Verbose.info("Midi synthesizer not supported on test machine - Test skipped");
            return;
        }

        try
        {
            midi.play(false);
            Thread.sleep(500);
            midi.pause();
            Thread.sleep(500);
            midi.resume();
            midi.resume();
            midi.pause();
            midi.pause();
            midi.play(true);
            midi.setVolume(20);
            midi.stop();
        }
        catch (final LionEngineException exception)
        {
            Assert.fail();
        }

        midi.setStart(0);
        try
        {
            midi.setStart(-1);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }

        try
        {
            midi.setStart(Integer.MAX_VALUE);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }

        MidiTest.testMidiLoop(midi, -1, 0);
        MidiTest.testMidiLoop(midi, 1, 0);
        MidiTest.testMidiLoop(midi, 0, Integer.MAX_VALUE);
        MidiTest.testMidiLoop(midi, -1, -1);
        MidiTest.testMidiLoop(midi, 0, -1);

        try
        {
            midi.setVolume(-1);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }

        try
        {
            midi.setVolume(101);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }

        try
        {
            final Midi midi2 = AudioMidi.loadMidi(MidiTest.FAIL);
            midi2.play(false);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }

        final Midi midi2 = AudioMidi.loadMidi(MidiTest.MUSIC);
        midi2.setLoop(6100, 8000);
        midi2.setStart(6100);
        midi2.play(true);
        Thread.sleep(500);
        midi2.setVolume(30);
        Thread.sleep(500);
        midi2.stop();
    }
}
