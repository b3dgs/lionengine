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
import com.b3dgs.lionengine.game.platform.background.ForegroundPlatform;

/**
 * World implementation using WorldGame.
 */
public class World
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
    private final ForegroundPlatform foreground;

    /**
     * Standard constructor.
     * 
     * @param sequence The sequence reference.
     */
    public World(Sequence sequence)
    {
        super(sequence);
        map = new Map();
        factory = new FactoryEntity(display.getRate(), map);
        valdyn = factory.createValdyn();
        camera = new CameraPlatform(width, height);
        background = new Swamp(sequence, "Stage1", false);
        foreground = new Water(sequence, "Water");
    }

    @Override
    public void update(double extrp)
    {
        valdyn.updateControl(keyboard);
        valdyn.update(extrp);
        camera.follow(valdyn);
        background.update(camera.getMovementHorizontal(), camera.getLocationY(), extrp);
        foreground.update(camera.getMovementHorizontal(), camera.getLocationY(), extrp);
    }

    @Override
    public void render(Graphic g)
    {
        background.render(g);
        foreground.primaryRender(g);
        map.render(g, camera);
        valdyn.render(g, camera);
        foreground.secondaryRender(g);
    }

    @Override
    protected void loaded()
    {
        camera.setLimits(map);
        camera.setIntervals(32, 0);
        valdyn.setLocation(512, 128);
    }

    @Override
    public void saving(FileWriting file) throws IOException
    {
        map.save(file);
    }

    @Override
    public void loading(FileReading file) throws IOException
    {
        map.load(file);
    }
}
