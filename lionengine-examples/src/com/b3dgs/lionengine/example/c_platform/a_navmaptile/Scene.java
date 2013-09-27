package com.b3dgs.lionengine.example.c_platform.a_navmaptile;

import java.awt.Font;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.Loader;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.Sequence;
import com.b3dgs.lionengine.Text;

/**
 * Game loop designed to handle our world.
 */
final class Scene
        extends Sequence
{
    /** Native resolution. */
    private static final Resolution NATIVE = new Resolution(320, 240, 60);

    /** Text drawer. */
    private final Text text;
    /** World reference. */
    private final World world;

    /**
     * Constructor.
     * 
     * @param loader The loader reference.
     */
    Scene(Loader loader)
    {
        super(loader, Scene.NATIVE);
        text = new Text(Font.SANS_SERIF, 11, Text.NORMAL);
        world = new World(this);
    }

    /*
     * Sequence
     */

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
        // Clean screen (as we don't have any background)
        clearScreen(g);

        world.render(g);
        text.draw(g, 0, 0, "Use click + mousemove or arrows keys to move the camera");
    }
}
