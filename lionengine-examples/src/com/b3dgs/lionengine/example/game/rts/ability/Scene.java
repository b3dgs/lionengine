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
package com.b3dgs.lionengine.example.game.rts.ability;

import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.Text;
import com.b3dgs.lionengine.TextStyle;
import com.b3dgs.lionengine.core.Click;
import com.b3dgs.lionengine.core.Key;
import com.b3dgs.lionengine.core.Loader;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.Sequence;
import com.b3dgs.lionengine.example.game.rts.ability.entity.BuildingProducer;
import com.b3dgs.lionengine.example.game.rts.ability.entity.Entity;
import com.b3dgs.lionengine.example.game.rts.ability.entity.EntityType;
import com.b3dgs.lionengine.example.game.rts.ability.entity.FactoryEntity;
import com.b3dgs.lionengine.example.game.rts.ability.entity.FactoryProduction;
import com.b3dgs.lionengine.example.game.rts.ability.entity.GoldMine;
import com.b3dgs.lionengine.example.game.rts.ability.entity.HandlerEntity;
import com.b3dgs.lionengine.example.game.rts.ability.entity.UnitAttacker;
import com.b3dgs.lionengine.example.game.rts.ability.entity.UnitWorker;
import com.b3dgs.lionengine.example.game.rts.ability.map.Map;
import com.b3dgs.lionengine.example.game.rts.ability.map.Tile;
import com.b3dgs.lionengine.example.game.rts.ability.projectile.HandlerProjectile;
import com.b3dgs.lionengine.game.TextGame;
import com.b3dgs.lionengine.game.rts.CameraRts;
import com.b3dgs.lionengine.game.rts.CursorRts;
import com.b3dgs.lionengine.utility.LevelRipConverter;

/**
 * Game loop designed to handle our little world.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
final class Scene
        extends Sequence
{
    /** Native resolution. */
    private static final Resolution NATIVE = new Resolution(320, 240, 60);

    /** Text reference. */
    private final TextGame text;
    /** Map reference. */
    private final Map map;
    /** Camera reference. */
    private final CameraRts camera;
    /** Cursor reference. */
    private final CursorRts cursor;
    /** Control panel reference. */
    private final ControlPanel controlPanel;
    /** Entity factory. */
    private final FactoryEntity factoryEntity;
    /** Production data. */
    private final FactoryProduction factoryProduction;
    /** Entity handler. */
    private final HandlerEntity handlerEntity;
    /** Arrows handler. */
    private final HandlerProjectile handlerProjectile;
    /** Context. */
    private final Context context;

    /**
     * Constructor.
     * 
     * @param loader The loader reference.
     */
    Scene(Loader loader)
    {
        super(loader, Scene.NATIVE);
        text = new TextGame(Text.SERIF, 10, TextStyle.NORMAL);
        map = new Map();
        camera = new CameraRts(map);
        cursor = new CursorRts(mouse, camera, source, map, Media.get("cursor.png"));
        controlPanel = new ControlPanel();
        handlerEntity = new HandlerEntity(camera, cursor, controlPanel, map, text);
        handlerProjectile = new HandlerProjectile(camera, handlerEntity);
        context = new Context(map, handlerEntity, handlerProjectile, source.getRate());
        factoryEntity = context.factoryEntity;
        factoryProduction = new FactoryProduction(factoryEntity);
        context.assignContext();
        setMouseVisible(false);
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
        final Entity entity = factoryEntity.create(type);
        entity.setPlayerId(0);
        entity.setLocation(tx, ty);
        handlerEntity.add(entity);
        return entity;
    }

    /*
     * Sequence
     */

    @Override
    protected void load()
    {
        final LevelRipConverter<Tile> rip = new LevelRipConverter<>();
        rip.start(Media.get("level.png"), map, Media.get("tiles"));
        map.loadCollisions(Media.get("tiles", "collisions.xml"));

        camera.setView(0, 0, width, height);
        camera.setSensibility(30, 30);
        camera.setBorders(map);
        camera.setLocation(map, 15, 13);
        camera.setKeys(Key.LEFT, Key.RIGHT, Key.UP, Key.DOWN);

        controlPanel.setClickableArea(camera);
        controlPanel.setClickSelection(Click.LEFT);
        controlPanel.setSelectionColor(ColorRgba.GREEN);

        handlerEntity.createLayers(map);
        handlerEntity.setClickAssignment(Click.RIGHT);

        final GoldMine goldMine = (GoldMine) createEntity(EntityType.GOLD_MINE, 20, 23);

        UnitWorker peon = (UnitWorker) createEntity(EntityType.PEON, 25, 20);
        peon.setResource(goldMine);
        peon.startExtraction();

        peon = (UnitWorker) createEntity(EntityType.PEON, 23, 18);
        peon.addToProductionQueue(factoryProduction.create(EntityType.BARRACKS_ORC, 17, 15));
        peon.addToProductionQueue(factoryProduction.create(EntityType.FARM_ORC, 31, 19));

        final UnitAttacker grunt = (UnitAttacker) createEntity(EntityType.GRUNT, 33, 25);
        final UnitAttacker spearman = (UnitAttacker) createEntity(EntityType.SPEARMAN, 27, 22);
        spearman.attack(grunt);
        grunt.attack(spearman);

        final BuildingProducer townHall = (BuildingProducer) createEntity(EntityType.TOWNHALL_ORC, 24, 15);
        townHall.setFrame(2);
        townHall.addToProductionQueue(factoryProduction.create(EntityType.PEON));
    }

    @Override
    protected void update(double extrp)
    {
        if (keyboard.isPressed(Key.ESCAPE))
        {
            end();
        }
        camera.update(keyboard);
        text.update(camera);
        cursor.update(extrp);
        controlPanel.update(extrp, camera, cursor, keyboard);
        handlerEntity.update(extrp);
        handlerProjectile.update(extrp);
    }

    @Override
    protected void render(Graphic g)
    {
        map.render(g, camera);
        handlerEntity.render(g);
        handlerProjectile.render(g);
        controlPanel.renderCursorSelection(g, camera);
        cursor.render(g);
    }
}
