package com.b3dgs.lionengine.example.c_platform.b_entitycontrol;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.Sequence;
import com.b3dgs.lionengine.file.FileReading;
import com.b3dgs.lionengine.file.FileWriting;
import com.b3dgs.lionengine.game.WorldGame;
import com.b3dgs.lionengine.game.platform.CameraPlatform;

/**
 * World implementation using AbstractWorld.
 */
final class World
        extends WorldGame
{
    /** Camera reference. */
    private final CameraPlatform camera;
    /** Mario reference. */
    private final Mario mario;

    /**
     * Constructor.
     * 
     * @param sequence The sequence reference.
     */
    World(Sequence sequence)
    {
        super(sequence);
        camera = new CameraPlatform(width, height);
        mario = new Mario(config.internal.getRate());
    }

    /*
     * WorldGame
     */

    @Override
    public void update(double extrp)
    {
        mario.updateControl(keyboard);
        mario.update(extrp);
    }

    @Override
    public void render(Graphic g)
    {
        // Draw the floor
        g.drawLine(0, 208, width, 208);

        // Draw the mario
        mario.render(g, camera);
    }

    @Override
    protected void saving(FileWriting file)
    {
        // Nothing to do
    }

    @Override
    protected void loading(FileReading file)
    {
        // Nothing to do
    }
}
