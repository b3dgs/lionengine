/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;

import org.junit.After;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.audio.Audio;
import com.b3dgs.lionengine.audio.AudioFactory;
import com.b3dgs.lionengine.core.Medias;

/**
 * Test the midi class.
 */
public class MidiTest
{
    /** Media music. */
    private Media music;
    /** Midi music. */
    private Midi midi;

    /**
     * Prepare the test.
     */
    @Before
    public void prepareTest()
    {
        AudioFactory.addFormat(new MidiFormat());
        Medias.setLoadFromJar(MidiTest.class);
        music = Medias.create("music.mid");
        try
        {
            midi = AudioFactory.loadAudio(music, Midi.class);
            Assert.assertTrue(midi.getTicks() > 0);
        }
        catch (final LionEngineException exception)
        {
            final String message = exception.getMessage();
            Assert.assertTrue(message, message.contains(MidiImpl.ERROR_MIDI));
            Assume.assumeFalse("Midi synthesizer not supported on test machine - Test skipped",
                               message.contains(MidiImpl.ERROR_MIDI));
        }
    }

    /**
     * Clean up tests.
     */
    @After
    public void cleanUp()
    {
        Medias.setLoadFromJar(null);
        AudioFactory.clearFormats();
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
            Assert.assertNotNull(exception);
        }
    }

    /**
     * Test with <code>null</code> argument.
     */
    @Test(expected = LionEngineException.class)
    public void testNullArgument()
    {
        AudioFactory.loadAudio(null);
        Assert.fail();
    }

    /**
     * Test with invalid media.
     */
    @Test(expected = LionEngineException.class)
    public void testInvalidMedia()
    {
        AudioFactory.loadAudio(Medias.create(Constant.EMPTY_STRING));
        Assert.fail();
    }

    /**
     * Test with negative start.
     */
    @Test(expected = LionEngineException.class)
    public void testNegativeStart()
    {
        midi.setStart(-1);
        Assert.fail();
    }

    /**
     * Test with out of range start.
     */
    @Test(expected = LionEngineException.class)
    public void testOutOfRangeStart()
    {
        midi.setStart(Integer.MAX_VALUE);
        Assert.fail();
    }

    /**
     * Test with negative volume.
     */
    @Test(expected = LionEngineException.class)
    public void testNegativeVolume()
    {
        midi.setVolume(-1);
        Assert.fail();
    }

    /**
     * Test with out of range volume.
     */
    @Test(expected = LionEngineException.class)
    public void testOutOfRangeVolume()
    {
        midi.setVolume(101);
        Assert.fail();
    }

    /**
     * Test with invalid music.
     */
    @Test(expected = LionEngineException.class)
    public void testFailMusic()
    {
        final Audio midi2 = AudioFactory.loadAudio(Medias.create("fail.mid"));
        midi2.play();
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
            midi.play();
            Thread.sleep(250);
            midi.pause();
            Thread.sleep(250);
            midi.resume();
            midi.resume();
            midi.pause();
            midi.pause();
            midi.play(true);
            midi.setVolume(10);
            midi.stop();
        }
        catch (final LionEngineException exception)
        {
            Assert.fail(exception.getMessage());
        }

        midi.setStart(0);

        testMidiLoop(midi, -1, 0);
        testMidiLoop(midi, 1, 0);
        testMidiLoop(midi, 0, Integer.MAX_VALUE);
        testMidiLoop(midi, -1, -1);
        testMidiLoop(midi, 0, -1);

        final Midi midi2 = AudioFactory.loadAudio(music, Midi.class);
        midi2.setLoop(6100, 8000);
        midi2.setStart(6100);
        midi2.play(true);
        Thread.sleep(250);
        midi2.setVolume(20);
        Thread.sleep(250);
        midi2.stop();
    }

    /**
     * Test midi with invalid stream.
     */
    @Test(expected = LionEngineException.class)
    public void testMidiInvalidStream()
    {
        Assert.assertNotNull(AudioFactory.loadAudio(new Media()
        {
            @Override
            public String getName()
            {
                return null;
            }

            @Override
            public String getPath()
            {
                return null;
            }

            @Override
            public String getParentPath()
            {
                return null;
            }

            @Override
            public OutputStream getOutputStream()
            {
                return null;
            }

            @Override
            public InputStream getInputStream()
            {
                return new InputStream()
                {
                    @Override
                    public int read() throws IOException
                    {
                        throw new IOException();
                    }
                };
            }

            @Override
            public File getFile()
            {
                return null;
            }

            @Override
            public Collection<Media> getMedias()
            {
                return null;
            }

            @Override
            public boolean exists()
            {
                return true;
            }
        }));
    }
}
