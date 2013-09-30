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
package com.b3dgs.lionengine.test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.Engine;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.Version;
import com.b3dgs.lionengine.audio.AudioMidi;
import com.b3dgs.lionengine.audio.AudioOgg;
import com.b3dgs.lionengine.audio.AudioSc68;
import com.b3dgs.lionengine.audio.AudioWav;
import com.b3dgs.lionengine.audio.Midi;
import com.b3dgs.lionengine.audio.Ogg;
import com.b3dgs.lionengine.audio.Sc68;
import com.b3dgs.lionengine.audio.Wav;

/**
 * Test file package.
 */
public class TestAudio
{
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
     * Test AudioMidi class.
     * 
     * @throws Exception If error.
     */
    @Test
    public void testAudioMidi() throws Exception
    {
        final Constructor<AudioMidi> constructor = AudioMidi.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        try
        {
            final AudioMidi audioMidi = constructor.newInstance();
            Assert.assertNotNull(audioMidi);
            Assert.fail();
        }
        catch (final InvocationTargetException exception)
        {
            // Success
        }
    }

    /**
     * Test AudioWav class.
     * 
     * @throws Exception If error.
     */
    @Test
    public void testAudioWav() throws Exception
    {
        final Constructor<AudioWav> constructor = AudioWav.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        try
        {
            final AudioWav audioWav = constructor.newInstance();
            Assert.assertNotNull(audioWav);
            Assert.fail();
        }
        catch (final InvocationTargetException exception)
        {
            // Success
        }
    }

    /**
     * Test AudioSc68 class.
     * 
     * @throws Exception If error.
     */
    @Test
    public void testAudioOgg() throws Exception
    {
        final Constructor<AudioOgg> constructor = AudioOgg.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        try
        {
            final AudioOgg audioOgg = constructor.newInstance();
            Assert.assertNotNull(audioOgg);
            Assert.fail();
        }
        catch (final InvocationTargetException exception)
        {
            // Success
        }
    }

    /**
     * Prepare test.
     */
    @Before
    public void setUp()
    {
        Engine.start("UnitTest", Version.create(1, 0, 0), Media.getPath("resources"));
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
            AudioMidi.loadMidi(Media.get(""));
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }

        final Midi midi;
        try
        {
            midi = AudioMidi.loadMidi(Media.get("music.mid"));
            Assert.assertTrue(midi.getTicks() > 0);
        }
        catch (final LionEngineException exception)
        {
            org.junit.Assume.assumeTrue(exception.getMessage(),
                    exception.getMessage().contains("No synthesizer available"));
            Verbose.info("Midi synthesizer not supported on test machine - Test skipped");
            return;
        }

        try
        {
            midi.play(false);
            Thread.sleep(1000);
            midi.pause();
            Thread.sleep(1000);
            midi.resume();
            midi.resume();
            midi.pause();
            midi.pause();
            midi.play(true);
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

        TestAudio.testMidiLoop(midi, -1, 0);
        TestAudio.testMidiLoop(midi, 1, 0);
        TestAudio.testMidiLoop(midi, 0, Integer.MAX_VALUE);
        TestAudio.testMidiLoop(midi, -1, -1);
        TestAudio.testMidiLoop(midi, 0, -1);
        midi.setLoop(1, 5);

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
            midi.setVolume(50);
        }
        catch (final LionEngineException exception)
        {
            Assert.fail();
        }
    }

    /**
     * Test Sc68 functions.
     * 
     * @throws InterruptedException If error.
     */
    @Test
    public void testSc68() throws InterruptedException
    {
        final Sc68 sc68 = AudioSc68.createSc68Player();
        try
        {
            sc68.play(null);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }

        try
        {
            sc68.setVolume(-1);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }
        try
        {
            sc68.setVolume(101);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }
        sc68.setVolume(25);
        sc68.play(Media.get("music.sc68"));
        Thread.sleep(1000);
        sc68.pause();
        Thread.sleep(500);
        sc68.setVolume(75);
        sc68.resume();
        Thread.sleep(1000);
        Assert.assertTrue(sc68.seek() >= 0);
        sc68.stop();
        sc68.free();
    }

    /**
     * Test Wav functions.
     * 
     * @throws InterruptedException If error.
     */
    @Test
    public void testWav() throws InterruptedException
    {
        try
        {
            AudioWav.loadWav(null);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }

        final Wav sound = AudioWav.loadWav(Media.get("sound.wav"));
        try
        {
            sound.setVolume(-1);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }
        try
        {
            sound.setVolume(101);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }
        sound.setVolume(100);
        sound.setAlignment(Align.LEFT);
        sound.play();
        Thread.sleep(200);

        sound.setAlignment(Align.CENTER);
        sound.play();
        Thread.sleep(200);

        sound.setAlignment(Align.RIGHT);
        sound.play();
        Thread.sleep(200);
        sound.stop();
    }

    /**
     * Test Ogg functions.
     * 
     * @throws InterruptedException If error.
     */
    @Test
    public void testOgg() throws InterruptedException
    {
        try
        {
            AudioOgg.loadOgg(null);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }

        final Ogg ogg = AudioOgg.loadOgg(Media.get("music.ogg"));
        try
        {
            ogg.setVolume(-1);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }
        try
        {
            ogg.setVolume(101);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }

        ogg.setVolume(100);
        ogg.play(false);
        Thread.sleep(2000);
        ogg.stop();
    }
}
