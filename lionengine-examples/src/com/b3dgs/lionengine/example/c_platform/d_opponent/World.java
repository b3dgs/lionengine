package com.b3dgs.lionengine.example.c_platform.d_opponent;

import java.awt.Color;
import java.io.IOException;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.Sequence;
import com.b3dgs.lionengine.file.FileReading;
import com.b3dgs.lionengine.file.FileWriting;
import com.b3dgs.lionengine.game.WorldGame;
import com.b3dgs.lionengine.game.platform.CameraPlatform;

/**
 * World implementation.
 */
final class World
        extends WorldGame
{
    /** Background color. */
    private static final Color BACKGROUND_COLOR = new Color(107, 136, 255);
    /** Camera reference. */
    private final CameraPlatform camera;
    /** Map reference. */
    private final Map map;
    /** Factory reference. */
    private final FactoryEntity factory;
    /** Mario reference. */
    private final Mario mario;
    /** Handler reference. */
    private final HandlerEntity handler;

    /**
     * Default constructor.
     * 
     * @param sequence The sequence reference.
     */
    World(Sequence sequence)
    {
        super(sequence);
        camera = new CameraPlatform(width, height);
        map = new Map();
        factory = new FactoryEntity(map, display.getRate());
        mario = factory.createMario();
        handler = new HandlerEntity(mario);
    }

    /*
     * WorldGame
     */

    @Override
    public void update(double extrp)
    {
        mario.updateControl(keyboard);
        mario.update(extrp);
        handler.update(extrp);
        camera.follow(mario);
    }

    @Override
    public void render(Graphic g)
    {
        g.setColor(World.BACKGROUND_COLOR);
        g.drawRect(0, 0, width, height, true);
        // Draw the map
        map.render(g, camera);
        // Draw the mario
        mario.render(g, camera);
        handler.render(g, camera);
    }

    @Override
    protected void saving(FileWriting file) throws IOException
    {
        map.save(file);
    }

    @Override
    protected void loading(FileReading file) throws IOException
    {
        map.load(file);
        camera.setLimits(map);
        camera.setIntervals(16, 0);
        map.adjustCollisions();
        // Place entity
        mario.setLocation(80, 32);

        // Create two goombas
        for (int i = 0; i < 2; i++)
        {
            final Goomba goomba = factory.createGoomba();
            goomba.setLocation(532 + i * 24, 32);
            handler.add(goomba);
        }
    }
}
