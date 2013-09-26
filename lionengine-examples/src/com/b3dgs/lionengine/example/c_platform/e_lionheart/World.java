package com.b3dgs.lionengine.example.c_platform.e_lionheart;

import java.io.IOException;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.Sequence;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.effect.HandlerEffect;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.FactoryEntity;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.HandlerEntity;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.player.StatsRenderer;
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
    /** Level reference. */
    final Level level;
    /** Camera reference. */
    private final CameraPlatform camera;
    /** Background factory. */
    private final FactoryLandscape factoryLandscape;
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
    /** Stats renderer. */
    private final StatsRenderer statsRenderer;
    /** Landscape reference. */
    private Landscape landscape;
    /** Player reference. */
    private Valdyn player;
    /** Game over. */
    private boolean gameOver;

    /**
     * @see WorldGame#WorldGame(Sequence)
     */
    World(Sequence sequence)
    {
        super(sequence);
        final double scaleH = internal.getWidth() / (double) AppLionheart.ORIGINAL_DISPLAY.getWidth();
        final double scaleV = internal.getHeight() / (double) AppLionheart.ORIGINAL_DISPLAY.getHeight();
        camera = new CameraPlatform(width, height);
        factoryLandscape = new FactoryLandscape(config, scaleH, scaleV, false);
        factoryEntity = new FactoryEntity();
        handlerEntity = new HandlerEntity(camera, factoryEntity);
        level = new Level(factoryEntity, handlerEntity);
        map = level.map;
        context = new Context(level, display.getRate());
        statsRenderer = new StatsRenderer(scaleH);
        handlerEffect = context.handlerEffect;
    }

    /**
     * Get the game over state.
     * 
     * @return <code>true</code> if game is over, <code>false</code> else.
     */
    public boolean isGameOver()
    {
        return gameOver;
    }

    /*
     * WorldGame
     */

    @Override
    public void update(double extrp)
    {
        player.updateControl(keyboard);
        player.update(extrp);
        handlerEntity.update(extrp);
        handlerEffect.update(extrp);
        camera.follow(player);
        landscape.update(extrp, camera);
        if (player.isDestroyed())
        {
            gameOver = true;
        }
    }

    @Override
    public void render(Graphic g)
    {
        landscape.renderBackground(g);
        map.render(g, camera);
        handlerEntity.render(g, camera);
        handlerEffect.render(g, camera);
        player.render(g, camera);
        if (AppLionheart.SHOW_COLLISIONS)
        {
            player.renderCollision(g, camera);
        }
        landscape.renderForeground(g);
        statsRenderer.render(g, player.stats);
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
        player.setLandscape(landscape);
        statsRenderer.load();
        camera.setLimits(map);
        camera.setIntervals(32, 0);
        handlerEntity.prepare();
        player.setCheckpoints(level.worldData.getCheckpoints());
        player.respawn(level.worldData.getStartX(), level.worldData.getStartY());
    }
}
