package com.b3dgs.lionengine.example.c_platform.c_entitymap;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.Loader;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Sequence;
import com.b3dgs.lionengine.input.Keyboard;

/**
 * Game loop designed to handle our little world.
 */
class Scene
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
        world.loadFromFile(Media.get("level.map"));
    }

    @Override
    protected void update(double extrp)
    {
        world.update(extrp);
        if (keyboard.isPressed(Keyboard.ESCAPE))
        {
            this.end();
        }
    }

    @Override
    protected void render(Graphic g)
    {
        world.render(g);
    }
}
