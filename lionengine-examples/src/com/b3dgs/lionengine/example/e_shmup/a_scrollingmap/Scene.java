package com.b3dgs.lionengine.example.e_shmup.a_scrollingmap;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.Loader;
import com.b3dgs.lionengine.Sequence;

/**
 * Game loop designed to handle our little world.
 */
public class Scene
        extends Sequence
{
    /** World reference. */
    private final World world;

    /**
     * Standard constructor.
     * 
     * @param loader The loader reference.
     */
    public Scene(final Loader loader)
    {
        super(loader);
        world = new World(this);
    }

    @Override
    protected void load()
    {
        // Nothing to load here
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
    protected void onTerminate()
    {
        // Nothing to do
    }
}
