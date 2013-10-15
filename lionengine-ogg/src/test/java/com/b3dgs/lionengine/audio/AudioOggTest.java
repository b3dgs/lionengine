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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test the audio ogg class.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class AudioOggTest
{
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
}
