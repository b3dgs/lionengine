package com.b3dgs.lionengine.test;

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
            midi.stop();
        }
        catch (final LionEngineException exception)
        {
            Assert.fail();
        }

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
            midi.setLoop(1, 0);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }

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
