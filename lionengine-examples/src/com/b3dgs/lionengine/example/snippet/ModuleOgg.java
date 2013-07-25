package com.b3dgs.lionengine.example.snippet;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.audio.AudioOgg;
import com.b3dgs.lionengine.audio.Ogg;

@SuppressWarnings("all")
public class ModuleOgg
{
    /*
     * Snippet code
     */

    void ogg() throws InterruptedException
    {
        final Ogg ogg = AudioOgg.loadOgg(Media.get("music.ogg"));
        ogg.setVolume(100);
        ogg.play(false);

        Thread.sleep(2000);
        ogg.stop();
    }
}
