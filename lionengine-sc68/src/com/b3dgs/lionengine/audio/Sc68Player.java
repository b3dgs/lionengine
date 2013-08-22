package com.b3dgs.lionengine.audio;

import java.io.File;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Media;

/**
 * SC68 player implementation.
 */
class Sc68Player
        implements Sc68
{
    /** Binding reference. */
    private final Sc68Binding binding;

    /**
     * Constructor.
     * 
     * @param binding The binding reference.
     */
    Sc68Player(Sc68Binding binding)
    {
        Check.notNull(binding, "SC68 binding must not be null !");
        this.binding = binding;
    }

    /*
     * Sc68
     */

    @Override
    public void play(Media media)
    {
        Check.notNull(media);
        final File music = Media.getTempFile(media, true, false);
        binding.SC68Play(music.getPath());
    }

    @Override
    public void setVolume(int volume)
    {
        Check.argument(volume >= 0 && volume <= 100, "Wrong volume value !");
        binding.SC68Volume(volume);
    }

    @Override
    public void pause()
    {
        binding.SC68Pause();
    }

    @Override
    public void resume()
    {
        binding.SC68UnPause();
    }

    @Override
    public void stop()
    {
        binding.SC68Stop();
    }

    @Override
    public void free()
    {
        binding.SC68Free();
    }

    @Override
    public int seek()
    {
        return binding.SC68Seek();
    }
}
