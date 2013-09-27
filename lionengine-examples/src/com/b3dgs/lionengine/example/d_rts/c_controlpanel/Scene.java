package com.b3dgs.lionengine.example.d_rts.c_controlpanel;

import java.awt.Font;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.Loader;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.Sequence;
import com.b3dgs.lionengine.Text;

/**
 * Game loop designed to handle our little world.
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
        text = new Text(Font.SANS_SERIF, 12, Text.NORMAL);
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
        text.draw(g, 0, 0, "Use arrows keys to move the camera");
        text.draw(g, 0, 12, "Use mouse click to start a selection");
    }
}
