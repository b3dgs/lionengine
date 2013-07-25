package com.b3dgs.lionengine.example.snippet;

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.audio.AudioWav;
import com.b3dgs.lionengine.audio.Wav;

@SuppressWarnings("all")
public class ModuleWav
{
    /*
     * Snippet code
     */

    void vav() throws InterruptedException
    {
        final Wav sound = AudioWav.loadWav(Media.get("sound.wav"));
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
}
