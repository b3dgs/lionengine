package com.b3dgs.lionengine.example.snippet;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.audio.AudioMidi;
import com.b3dgs.lionengine.audio.Midi;

@SuppressWarnings("all")
public class ModuleMidi
{
    /*
     * Snippet code
     */

    void midi() throws InterruptedException
    {
        final Midi midi = AudioMidi.loadMidi(Media.get("music.mid"));
        midi.play(false);

        Thread.sleep(1000);
        midi.pause();
        Thread.sleep(1000);
        midi.resume();
        midi.pause();
        midi.stop();
    }
}
