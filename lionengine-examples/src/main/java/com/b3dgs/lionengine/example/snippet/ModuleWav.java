/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.audio.wav.AudioWav;
import com.b3dgs.lionengine.audio.wav.Wav;
import com.b3dgs.lionengine.core.Medias;

public class ModuleWav
{
    /*
     * Snippet code
     */

    void vav() throws InterruptedException
    {
        final Wav sound = AudioWav.loadWav(Medias.create("sound.wav"));

        sound.play(Align.LEFT, 100);
        Thread.sleep(200);

        sound.play(Align.CENTER, 100);
        Thread.sleep(200);

        sound.play(Align.RIGHT, 100);
        Thread.sleep(200);

        sound.stop();
    }
}
