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
package com.b3dgs.lionengine.example.warcraft;

import java.io.IOException;

import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.Text;
import com.b3dgs.lionengine.TextStyle;
import com.b3dgs.lionengine.core.Click;
import com.b3dgs.lionengine.core.Key;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.Sequence;
import com.b3dgs.lionengine.core.UtilityImage;
import com.b3dgs.lionengine.example.warcraft.entity.Entity;
import com.b3dgs.lionengine.example.warcraft.entity.EntityType;
import com.b3dgs.lionengine.example.warcraft.entity.FactoryEntity;
import com.b3dgs.lionengine.example.warcraft.entity.HandlerEntity;
import com.b3dgs.lionengine.example.warcraft.map.FogOfWar;
import com.b3dgs.lionengine.example.warcraft.map.Map;
import com.b3dgs.lionengine.example.warcraft.map.Minimap;
import com.b3dgs.lionengine.example.warcraft.projectile.HandlerProjectile;
import com.b3dgs.lionengine.file.FileReading;
import com.b3dgs.lionengine.file.FileWriting;
import com.b3dgs.lionengine.game.TextGame;
import com.b3dgs.lionengine.game.TimedMessage;
import com.b3dgs.lionengine.game.WorldGame;

/**
 * World implementation.
 */
final class World
        extends WorldGame
{
    /** Text reference. */
    private final TextGame text;
    /** Player 1. */
    private final Player player;
    /** Player 2. */
    private final Player cpu;
    /** Map reference. */
    private final Map map;
    /** Camera reference. */
    private final Camera camera;
    /** Fog of war. */
    private final FogOfWar fogOfWar;
    /** Minimap. */
    private final Minimap minimap;
    /** Cursor reference. */
    private final Cursor cursor;
    /** Control panel reference. */
    private final ControlPanel controlPanel;
    /** Entity factory. */
    private final FactoryEntity factoryEntity;
    /** Entity handler. */
    private final HandlerEntity handlerEntity;
    /** Arrows handler. */
    private final HandlerProjectile handlerProjectile;
    /** Timed message. */
    private final TimedMessage message;
    /** Context. */
    private final Context context;

    /**
     * Constructor.
     * 
     * @param sequence The sequence reference.
     * @param config The game configuration.
     */
    World(Sequence sequence, GameConfig config)
    {
        super(sequence);
        text = new TextGame(Text.SERIF, 10, TextStyle.NORMAL);
        player = new Player();
        cpu = new Player();
        map = new Map();
        camera = new Camera(map);
        fogOfWar = new FogOfWar(config);
        cursor = new Cursor(mouse, camera, source, map, Media.get("cursor.png"), Media.get("cursor_over.png"),
                Media.get("cursor_order.png"));
        message = new TimedMessage(UtilityImage.createText(Text.DIALOG, 10, TextStyle.NORMAL));
        controlPanel = new ControlPanel(cursor);
        handlerEntity = new HandlerEntity(camera, cursor, controlPanel, map, fogOfWar);
        minimap = new Minimap(map, fogOfWar, controlPanel, handlerEntity, 3, 6);
        handlerProjectile = new HandlerProjectile(camera, handlerEntity);
        context = new Context(camera, map, handlerEntity, handlerProjectile, cursor, message, source.getRate());
        factoryEntity = context.factoryEntity;
        context.assignContext();
    }

    /**
     * Create an entity from its type.
     * 
     * @param type The entity type.
     * @param tx The horizontal location.
     * @param ty The vertical location.
     * @return The entity instance.
     */
    private Entity createEntity(EntityType type, int tx, int ty)
    {
        final Entity entity = factoryEntity.createEntity(type);
        entity.setLocation(tx, ty);
        handlerEntity.add(entity);
        return entity;
    }

    /*
     * WorldRts
     */

    @Override
    public void update(double extrp)
    {
        camera.update(keyboard);
        text.update(camera);
        cursor.update(extrp);
        controlPanel.update(extrp, camera, cursor, keyboard);
        handlerEntity.update(extrp);
        handlerProjectile.update(extrp);
        minimap.update(cursor, camera, context.handlerEntity, 11, 12);
        context.handlerEffect.update(extrp);
        message.update();
        player.update(extrp);

    }

    @Override
    public void render(Graphic g)
    {
        map.render(g, camera);
        handlerEntity.render(g);
        handlerProjectile.render(g);
        context.handlerEffect.render(g);
        fogOfWar.render(g, camera);
        cursor.renderBox(g);
        controlPanel.renderCursorSelection(g, camera);
        controlPanel.render(g, cursor, camera);
        message.render(g);
        minimap.render(g, camera);
        cursor.render(g);
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
        map.createMiniMap();

        camera.setView(72, 12, 240, 176);
        camera.setSensibility(30, 30);
        camera.setBorders(map);
        camera.setLocation(map, 33, 3);
        camera.setKeys(Key.LEFT, Key.RIGHT, Key.UP, Key.DOWN);

        fogOfWar.create(map);
        fogOfWar.setPlayerId(player.id);

        controlPanel.setClickableArea(camera);
        controlPanel.setSelectionColor(ColorRgba.GREEN);
        controlPanel.setPlayer(player);
        controlPanel.setClickSelection(Click.LEFT);

        camera.setControlPanel(controlPanel);

        handlerEntity.createLayers(map);
        handlerEntity.setPlayer(player);
        handlerEntity.setClickAssignment(Click.RIGHT);

        factoryEntity.setContext(context);

        createEntity(EntityType.GOLD_MINE, 30, 13);
        createEntity(EntityType.GOLD_MINE, 58, 58);

        final Entity peon = createEntity(EntityType.PEON, 40, 8);
        peon.setPlayer(player);

        Entity grunt = createEntity(EntityType.GRUNT, 38, 5);
        grunt.setPlayer(player);

        grunt = createEntity(EntityType.GRUNT, 39, 5);
        grunt.setPlayer(player);

        final Entity spearman = createEntity(EntityType.SPEARMAN, 38, 9);
        spearman.setPlayer(player);

        final Entity townHall = createEntity(EntityType.TOWNHALL_ORC, 40, 5);
        townHall.setPlayer(player);

        final Entity peasant = createEntity(EntityType.PEASANT, 40, 10);
        peasant.setPlayer(cpu);

        handlerEntity.update(1.0);
        handlerEntity.updatePopulation();
    }
}
