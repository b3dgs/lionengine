package com.b3dgs.lionengine.example.c_platform.e_lionheart;

import java.io.IOException;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.Sequence;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.effect.HandlerEffect;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.FactoryEntity;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.HandlerEntity;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.player.Valdyn;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.landscape.FactoryLandscape;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.landscape.Landscape;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.map.Map;
import com.b3dgs.lionengine.file.FileReading;
import com.b3dgs.lionengine.file.FileWriting;
import com.b3dgs.lionengine.game.WorldGame;
import com.b3dgs.lionengine.game.platform.CameraPlatform;

/**
 * Represents the game layer, handling the landscape, the map, and the entities.
 */
final class World
        extends WorldGame
{
    /** Camera reference. */
    private final CameraPlatform camera;
    /** Background factory. */
    private final FactoryLandscape factoryLandscape;
    /** Level reference. */
    private final Level level;
    /** Map reference. */
    private final Map map;
    /** Entity factory. */
    private final FactoryEntity factoryEntity;
    /** Handler entity. */
    private final HandlerEntity handlerEntity;
    /** Handler effect. */
    private final HandlerEffect handlerEffect;
    /** Context. */
    private final Context context;
    /** Background reference. */
    private Landscape landscape;
    /** Player reference. */
    private Valdyn player;

    /**
     * Constructor.
     * 
     * @param sequence The sequence reference.
     */
    World(Sequence sequence)
    {
        super(sequence);
        camera = new CameraPlatform(width, height);
        factoryLandscape = new FactoryLandscape(config, wide, false);
        factoryEntity = new FactoryEntity();
        handlerEntity = new HandlerEntity(camera, factoryEntity);
        level = new Level(factoryEntity, handlerEntity);
        map = level.map;
        context = new Context(level, display.getRate());
        handlerEffect = context.handlerEffect;
    }

    /*
     * WorldGame
     */

    @Override
    public void update(double extrp)
    {
        player.updateControl(keyboard);
        player.update(extrp);
        camera.follow(player);
        handlerEntity.update(extrp);
        handlerEffect.update(extrp);
        landscape.update(extrp, camera);
    }

    @Override
    public void render(Graphic g)
    {
        landscape.renderBackground(g);
        map.render(g, camera);
        handlerEntity.render(g, camera);
        handlerEffect.render(g, camera);
        player.render(g, camera);
        landscape.renderForeground(g);
    }

    @Override
    protected void saving(FileWriting file) throws IOException
    {
        level.save(file);
    }

    @Override
    protected void loading(FileReading file) throws IOException
    {
        level.load(file);
        player = factoryEntity.createValdyn();
        handlerEntity.setPlayer(player);
        landscape = factoryLandscape.createLandscape(level.getLandscape());
    }

    @Override
    protected void loaded()
    {
        camera.setLimits(map);
        camera.setIntervals(32, 0);
        player.respawn();
        // TODO: SonicArranger.play(level.getWorld().getMusic());
    }
}
