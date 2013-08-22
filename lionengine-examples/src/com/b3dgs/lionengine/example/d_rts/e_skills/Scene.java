package com.b3dgs.lionengine.example.d_rts.e_skills;

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
     * Standard constructor.
     * 
     * @param loader The loader reference.
     */
    Scene(Loader loader)
    {
        super(loader);
        world = new World(this);
        setMouseVisible(false);
    }

    /*
     * Sequence
     */

    @Override
    protected void load()
    {
        world.loadFromFile(Media.get("maps", "forest.map"));
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
