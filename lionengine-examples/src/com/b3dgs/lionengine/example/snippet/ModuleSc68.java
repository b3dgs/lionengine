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
package com.b3dgs.lionengine.example.snippet;

import org.junit.Assert;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.audio.AudioSc68;
import com.b3dgs.lionengine.audio.Sc68;

@SuppressWarnings("all")
public class ModuleSc68
{
    /*
     * Snippet code
     */

    void sc68() throws InterruptedException
    {
        final Sc68 sc68 = AudioSc68.createSc68Player();
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
}
