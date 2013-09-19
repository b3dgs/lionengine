package com.b3dgs.lionengine.example.c_platform.c_entitymap;

import java.awt.Color;
import java.io.IOException;

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
    /** Background color. */
    private static final Color BACKGROUND_COLOR = new Color(107, 136, 255);
    /** Camera reference. */
    private final CameraPlatform camera;
    /** Map reference. */
    private final Map map;
    /** Mario reference. */
    private final Mario hero;

    /**
     * Constructor.
     * 
     * @param sequence The sequence reference.
     */
    World(Sequence sequence)
    {
        super(sequence);
        camera = new CameraPlatform(width, height);
        map = new Map();
        hero = new Mario(map, sequence.config.internal.getRate());
    }

    /*
     * WorldGame
     */

    @Override
    public void update(double extrp)
    {
        hero.updateControl(keyboard);
        hero.update(extrp);
        camera.follow(hero);
    }

    @Override
    public void render(Graphic g)
    {
        g.setColor(World.BACKGROUND_COLOR);
        g.drawRect(0, 0, width, height, true);
        // Draw the map
        map.render(g, camera);
        // Draw the hero
        hero.render(g, camera);
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
        hero.respawn();
    }
}
