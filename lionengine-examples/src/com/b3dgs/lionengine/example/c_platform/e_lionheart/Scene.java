package com.b3dgs.lionengine.example.c_platform.e_lionheart;

import java.awt.Font;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.Loader;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Sequence;
import com.b3dgs.lionengine.Text;
import com.b3dgs.lionengine.input.Keyboard;

/**
 * Scene implementation.
 */
final class Scene
        extends Sequence
{
    /** World reference. */
    private final World world;
    /** Text reference. */
    private final Text text;

    /**
     * Standard constructor.
     * 
     * @param loader The loader reference.
     */
    Scene(Loader loader)
    {
        super(loader);
        world = new World(this);
        text = new Text(Font.SANS_SERIF, 8, Text.NORMAL);
    }

    @Override
    protected void load()
    {
        world.loadFromFile(Media.get("levels", "level1.lrm"));
    }

    @Override
    protected void update(double extrp)
    {
        world.update(extrp);
        if (keyboard.isPressedOnce(Keyboard.ESCAPE))
        {
            end();
        }
    }

    @Override
    protected void render(Graphic g)
    {
        world.render(g);
        text.draw(g, 0, 0, String.valueOf(getFps()));
    }
}
