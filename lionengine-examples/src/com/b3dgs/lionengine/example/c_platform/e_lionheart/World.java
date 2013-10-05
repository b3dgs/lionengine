/*
 * Copyright (C) 2013 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.b3dgs.lionengine.example.c_platform.e_lionheart;

import java.io.IOException;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.core.Sequence;
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
        final double scaleH = width / (double) Scene.SCENE_DISPLAY.getWidth();
        final double scaleV = height / (double) Scene.SCENE_DISPLAY.getHeight();
        camera = new CameraPlatform(width, height);
        factoryLandscape = new FactoryLandscape(source, scaleH, scaleV, false);
        factoryEntity = new FactoryEntity();
        handlerEntity = new HandlerEntity(camera, factoryEntity);
        level = new Level(camera, factoryEntity, handlerEntity, source.getRate());
        factoryEntity.setLevel(level);
        map = level.map;
        statsRenderer = new StatsRenderer(width);
        handlerEffect = level.handlerEffect;
    }

    /**
     * Called when the resolution changed.
     * 
     * @param width The new width.
     * @param height The new height.
     */
    public void setScreenSize(int width, int height)
    {
        landscape.setScreenSize(width, height);
        camera.setView(0, 0, width, height);
        camera.setScreenSize(width, height);
        statsRenderer.setScreenWidth(width);
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
        handlerEntity.render(g);
        handlerEffect.render(g);
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
        camera.setIntervals(32, 0);
        handlerEntity.update(1.0);
        handlerEntity.prepare();
        player.setCheckpoints(level.worldData.getCheckpoints());
        player.respawn(level.worldData.getStartX(), level.worldData.getStartY());
    }
}
