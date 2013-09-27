package com.b3dgs.lionengine.example.d_rts.f_warcraft;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.Loader;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.Sequence;
import com.b3dgs.lionengine.audio.AudioMidi;
import com.b3dgs.lionengine.audio.Midi;

/**
 * Game loop designed to handle the world.
 */
public final class Scene
        extends Sequence
{
    /** Native resolution. */
    public static final Resolution NATIVE = new Resolution(320, 200, 60);

    /** World reference. */
    private final World world;
    /** Game configuration. */
    private final GameConfig config;
    /** Music. */
    private final Midi music;

    /**
     * Standard constructor.
     * 
     * @param loader The loader reference.
     * @param config The game configuration.
     */
    public Scene(final Loader loader, GameConfig config)
    {
        super(loader, Scene.NATIVE);
        this.config = config;
        world = new World(this, config);
        music = AudioMidi.loadMidi(Media.get(ResourcesLoader.MUSICS_DIR, "orcs.mid"));
        setMouseVisible(false);
    }

    /*
     * Sequence
     */

    @Override
    protected void load()
    {
        world.loadFromFile(Media.get(ResourcesLoader.MAPS_DIR, config.map));
        // music.play(true);
    }

    @Override
    protected void update(double extrp)
    {
        world.update(extrp);
    }

    @Override
    protected void render(Graphic g)
    {
        world.render(g);
    }

    @Override
    protected void onTerminate(boolean hasNextSequence)
    {
        music.stop();
    }
}
