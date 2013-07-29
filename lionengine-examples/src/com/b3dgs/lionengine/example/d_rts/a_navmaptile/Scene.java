package com.b3dgs.lionengine.example.d_rts.a_navmaptile;

import java.awt.Font;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.Loader;
import com.b3dgs.lionengine.Sequence;
import com.b3dgs.lionengine.Text;

/**
 * Game loop designed to handle our little world.
 */
final class Scene
        extends Sequence
{
    /** Text drawer. */
    private final Text text;
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
        text = new Text(Font.SANS_SERIF, 12, Text.NORMAL);
        world = new World(this);
    }

    @Override
    protected void load()
    {
        // Nothing to load for the moment
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
        text.draw(g, 0, 0, "Use arrows keys to move the camera");
    }
}
