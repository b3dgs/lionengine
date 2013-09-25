package com.b3dgs.lionengine.example.c_platform.d_opponent;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.Loader;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Sequence;

/**
 * Game loop designed to handle our little world.
 */
final class Scene
        extends Sequence
{
    /** World reference. */
    private final World world;

    /**
     * @see Sequence#Sequence(Loader)
     */
    Scene(Loader loader)
    {
        super(loader);
        world = new World(this);
    }

    /*
     * Sequence
     */

    @Override
    protected void load()
    {
        world.loadFromFile(Media.get("level.map"));
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
}
