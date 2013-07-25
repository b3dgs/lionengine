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
