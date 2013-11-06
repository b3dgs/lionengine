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

import org.junit.Assert;
import org.junit.Test;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.core.Media;

/**
 * Test the ogg player.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class OggTest
{
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

        final Ogg ogg = AudioOgg.loadOgg(Media.create(Media.getPath("src", "test", "resources", "music.ogg")));
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

        ogg.setVolume(60);
        ogg.play(false);
        Thread.sleep(2000);
        ogg.stop();
    }
}
