package com.b3dgs.lionengine.example.c_platform.e_lionheart;

import java.io.IOException;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.Sequence;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.background.Swamp;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.background.Water;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.Valdyn;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.map.Map;
import com.b3dgs.lionengine.file.FileReading;
import com.b3dgs.lionengine.file.FileWriting;
import com.b3dgs.lionengine.game.WorldGame;
import com.b3dgs.lionengine.game.platform.CameraPlatform;
import com.b3dgs.lionengine.game.platform.background.Background;

/**
 * World implementation using WorldGame.
 */
class World
        extends WorldGame
{
    /** Map reference. */
    private final Map map;
    /** Entity factory. */
    private final FactoryEntity factory;
    /** Player reference. */
    private final Valdyn valdyn;
    /** Camera reference. */
    private final CameraPlatform camera;
    /** Background reference. */
    private final Background background;
    /** Foreground reference. */
    private final Water water;

    /**
     * Standard constructor.
     * 
     * @param sequence The sequence reference.
     */
    World(Sequence sequence)
    {
        super(sequence);
        map = new Map();
        camera = new CameraPlatform(width, height);
        factory = new FactoryEntity(map, camera, display.getRate());
        valdyn = factory.createValdyn();
        water = new Water(sequence, "Water");
        background = new Swamp(sequence, "Stage1", false, water);
    }

    @Override
    public void update(double extrp)
    {
        valdyn.updateControl(keyboard);
        valdyn.update(extrp);
        background.update(extrp, camera.getMovementHorizontal(), camera.getLocationY());
        camera.follow(valdyn);
        water.update(extrp, camera.getMovementHorizontal(), camera.getLocationY());
    }

    @Override
    public void render(Graphic g)
    {
        background.render(g);
        water.renderBack(g);
        map.render(g, camera);
        valdyn.render(g, camera);
        water.renderFront(g);
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
    }

    @Override
    protected void loaded()
    {
        camera.setLimits(map);
        camera.setIntervals(32, 0);
        valdyn.setLocation(512, 55);
        camera.resetInterval(valdyn);
        background.update(1.0, 1.0, camera.getLocationY());
    }
}
