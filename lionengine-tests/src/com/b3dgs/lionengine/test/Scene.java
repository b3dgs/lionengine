package com.b3dgs.lionengine.test;

import org.junit.Assert;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.Loader;
import com.b3dgs.lionengine.Sequence;

/**
 * Scene base.
 */
class Scene
        extends Sequence
{

    /**
     * Constructor.
     * 
     * @param loader The loader reference.
     */
    public Scene(Loader loader)
    {
        super(loader);
    }

    @Override
    protected void load()
    {
        setMouseVisible(true);
        setMouseVisible(false);
        setExtrapolated(true);
    }

    @Override
    protected void update(double extrp)
    {
        end();
        end(null);
        final int fps = getFps();
        Assert.assertTrue(fps >= 0);
    }

    @Override
    protected void render(Graphic g)
    {
        clearScreen(g);
    }

}
