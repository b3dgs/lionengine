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
package com.b3dgs.lionengine.audio;

import static com.b3dgs.lionengine.UtilAssert.assertNotNull;
import static com.b3dgs.lionengine.UtilAssert.assertPrivateConstructor;
import static com.b3dgs.lionengine.UtilAssert.assertThrows;

import java.util.Arrays;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.MediaMock;
import com.b3dgs.lionengine.Medias;

/**
 * Test {@link AudioFactory}.
 */
public final class AudioFactoryTest
{
    /**
     * Prepare tests.
     */
    @BeforeAll
    public static void beforeTests()
    {
        Medias.setLoadFromJar(AudioFactoryTest.class);
    }

    /**
     * Clean up tests.
     */
    @AfterAll
    public static void afterTests()
    {
        Medias.setLoadFromJar(null);
    }

    /**
     * Reset formats.
     */
    @AfterEach
    public void clean()
    {
        AudioFactory.clearFormats();
    }

    /**
     * Test constructor.
     */
    @Test
    public void testConstructorPrivate()
    {
        assertPrivateConstructor(AudioFactory.class);
    }

    /**
     * Test add format <code>null</code>.
     */
    @Test
    public void testAddFormatNull()
    {
        assertThrows(() -> AudioFactory.addFormat(null), "Unexpected null argument !");
    }

    /**
     * Test load audio <code>null</code>.
     */
    @Test
    public void testLoadAudioNull()
    {
        assertThrows(() -> AudioFactory.loadAudio(null), "Unexpected null argument !");
    }

    /**
     * Test load audio <code>null</code>.
     */
    @Test
    public void testLoadAudioNullFormat()
    {
        assertThrows(() -> AudioFactory.loadAudio(new MediaMock(), null), "Unexpected null argument !");
    }

    /**
     * Test load audio.
     */
    @Test
    public void testLoadAudio()
    {
        AudioFactory.addFormat(new AudioVoidFormat(Arrays.asList("png")));

        assertNotNull(AudioFactory.loadAudio(Medias.create("image.png")));
        assertNotNull(AudioFactory.loadAudio(Medias.create("image.png"), Audio.class));
        assertNotNull(AudioFactory.loadAudio(Medias.create("image.png"), AudioVoid.class));
    }

    /**
     * Test load audio invalid cast.
     */
    @Test
    public void testLoadAudioInvalidCast()
    {
        AudioFactory.addFormat(new AudioVoidFormat(Arrays.asList("png")));

        assertThrows(() -> AudioFactory.loadAudio(Medias.create("image.png"), MyAudio.class),
                     "[image.png] " + AudioFactory.ERROR_FORMAT);
    }

    /**
     * Test load audio invalid type.
     */
    @Test
    public void testLoadAudioInvalidType()
    {
        assertThrows(() -> AudioFactory.loadAudio(Medias.create("image.wav"), MyAudio.class),
                     "[image.wav] " + AudioFactory.ERROR_FORMAT);
    }

    /**
     * Test add format already exists.
     */
    @Test
    public void testAddFormatExists()
    {
        AudioFactory.addFormat(new AudioVoidFormat(Arrays.asList("png")));

        assertThrows(() -> AudioFactory.addFormat(new AudioVoidFormat(Arrays.asList("png"))),
                     AudioFactory.ERROR_EXISTS + "png");
    }

    /**
     * Test clear formats.
     */
    @Test
    public void testClearFormats()
    {
        AudioFactory.addFormat(new AudioVoidFormat(Arrays.asList("png")));

        assertNotNull(AudioFactory.loadAudio(Medias.create("image.png")));

        AudioFactory.clearFormats();

        assertThrows(() -> AudioFactory.loadAudio(Medias.create("image.png")),
                     "[image.png] " + AudioFactory.ERROR_FORMAT);
    }

    /**
     * Test void format.
     */
    @Test
    public void testVoid()
    {
        assertThrows(() -> AudioFactory.loadAudio(Medias.create("image.png")),
                     "[image.png] " + AudioFactory.ERROR_FORMAT);

        final AudioFormat format = new AudioVoidFormat(Arrays.asList("png"));
        AudioFactory.addFormat(format);

        final Audio audio = AudioFactory.loadAudio(Medias.create("image.png"));
        audio.setVolume(100);
        audio.play();
        audio.stop();

        format.close();
    }

    /**
     * Mock audio
     */
    private static interface MyAudio extends Audio
    {
        // Mock
    }
}
