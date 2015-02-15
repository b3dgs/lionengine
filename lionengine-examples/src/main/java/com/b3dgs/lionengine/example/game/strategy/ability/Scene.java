/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.example.game.strategy.ability;

import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.TextStyle;
import com.b3dgs.lionengine.core.Core;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.Loader;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.Sequence;
import com.b3dgs.lionengine.core.Text;
import com.b3dgs.lionengine.core.awt.Engine;
import com.b3dgs.lionengine.core.awt.Keyboard;
import com.b3dgs.lionengine.core.awt.Mouse;
import com.b3dgs.lionengine.example.game.strategy.ability.entity.BarracksOrc;
import com.b3dgs.lionengine.example.game.strategy.ability.entity.BuildingProducer;
import com.b3dgs.lionengine.example.game.strategy.ability.entity.Entity;
import com.b3dgs.lionengine.example.game.strategy.ability.entity.FactoryEntity;
import com.b3dgs.lionengine.example.game.strategy.ability.entity.FactoryProduction;
import com.b3dgs.lionengine.example.game.strategy.ability.entity.FarmOrc;
import com.b3dgs.lionengine.example.game.strategy.ability.entity.GoldMine;
import com.b3dgs.lionengine.example.game.strategy.ability.entity.Grunt;
import com.b3dgs.lionengine.example.game.strategy.ability.entity.HandlerEntity;
import com.b3dgs.lionengine.example.game.strategy.ability.entity.Peon;
import com.b3dgs.lionengine.example.game.strategy.ability.entity.Spearman;
import com.b3dgs.lionengine.example.game.strategy.ability.entity.TownhallOrc;
import com.b3dgs.lionengine.example.game.strategy.ability.entity.UnitAttacker;
import com.b3dgs.lionengine.example.game.strategy.ability.entity.UnitWorker;
import com.b3dgs.lionengine.example.game.strategy.ability.launcher.FactoryLauncher;
import com.b3dgs.lionengine.example.game.strategy.ability.map.Map;
import com.b3dgs.lionengine.example.game.strategy.ability.map.Tile;
import com.b3dgs.lionengine.example.game.strategy.ability.projectile.FactoryProjectile;
import com.b3dgs.lionengine.example.game.strategy.ability.projectile.HandlerProjectile;
import com.b3dgs.lionengine.example.game.strategy.ability.weapon.FactoryWeapon;
import com.b3dgs.lionengine.game.Services;
import com.b3dgs.lionengine.game.TextGame;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.strategy.CameraStrategy;
import com.b3dgs.lionengine.game.strategy.CursorStrategy;
import com.b3dgs.lionengine.game.utility.LevelRipConverter;

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

    /** Keyboard reference. */
    private final Keyboard keyboard;
    /** Mouse reference. */
    private final Mouse mouse;
    /** Text reference. */
    private final TextGame text;
    /** Map reference. */
    private final Map map;
    /** Camera reference. */
    private final CameraStrategy camera;
    /** Cursor reference. */
    private final CursorStrategy cursor;
    /** Control panel reference. */
    private final ControlPanel controlPanel;
    /** Entity factory. */
    private final FactoryEntity factoryEntity;
    /** Production data. */
    private final FactoryProduction factoryProduction;
    /** Production data. */
    private final FactoryLauncher factoryLauncher;
    /** Production data. */
    private final FactoryProjectile factoryProjectile;
    /** Production data. */
    private final FactoryWeapon factoryWeapon;
    /** Entity handler. */
    private final HandlerEntity handlerEntity;
    /** Arrows handler. */
    private final HandlerProjectile handlerProjectile;

    /**
     * Constructor.
     * 
     * @param loader The loader reference.
     */
    Scene(Loader loader)
    {
        super(loader, Scene.NATIVE);
        keyboard = getInputDevice(Keyboard.class);
        mouse = getInputDevice(Mouse.class);
        text = new TextGame(Text.SERIF, 10, TextStyle.NORMAL);
        map = new Map();
        camera = new CameraStrategy(map);
        cursor = new CursorStrategy(mouse, camera, getConfig().getSource(), map, Core.MEDIA.create("cursor.png"));
        controlPanel = new ControlPanel();
        handlerEntity = new HandlerEntity(camera, cursor, controlPanel, map, text);
        handlerProjectile = new HandlerProjectile(camera, handlerEntity);
        factoryProjectile = new FactoryProjectile();
        factoryLauncher = new FactoryLauncher();
        factoryWeapon = new FactoryWeapon();
        factoryEntity = new FactoryEntity();
        factoryProduction = new FactoryProduction();

        controlPanel.addListener(handlerEntity);

        final Services contextEntity = new Services();
        contextEntity.add(map);
        contextEntity.add(factoryEntity);
        contextEntity.add(factoryWeapon);
        contextEntity.add(handlerEntity);
        contextEntity.add(Integer.valueOf(getConfig().getSource().getRate()));
        factoryEntity.setServices(contextEntity);

        final Services contextLauncher = new Services();
        contextLauncher.add(factoryProjectile);
        contextLauncher.add(handlerProjectile);
        factoryLauncher.setServices(contextLauncher);

        final Services contextWeapon = new Services();
        contextWeapon.add(factoryLauncher);
        factoryWeapon.setServices(contextWeapon);

        setSystemCursorVisible(false);
    }

    /**
     * Create an entity from its type.
     * 
     * @param config The entity config.
     * @param tx The horizontal location.
     * @param ty The vertical location.
     * @return The entity instance.
     */
    private Entity createEntity(Media config, int tx, int ty)
    {
        final Entity entity = factoryEntity.create(config);
        entity.setPlayerId(0);
        entity.setLocation(tx, ty);
        handlerEntity.add(entity);
        return entity;
    }

    /*
     * Sequence
     */

    @Override
    public void load()
    {
        final LevelRipConverter<Tile> rip = new LevelRipConverter<>(Core.MEDIA.create("level.png"),
                Core.MEDIA.create("tile"), map);
        rip.start();
        map.loadCollisions(Core.MEDIA.create("tile", MapTile.GROUPS_FILE_NAME));

        keyboard.setHorizontalControlNegative(Keyboard.LEFT);
        keyboard.setHorizontalControlPositive(Keyboard.RIGHT);
        keyboard.setVerticalControlNegative(Keyboard.DOWN);
        keyboard.setVerticalControlPositive(Keyboard.UP);

        camera.setView(0, 0, getWidth(), getHeight());
        camera.setSensibility(30, 30);
        camera.setBorders(map);
        camera.setLocation(map, 15, 13);

        controlPanel.setClickableArea(camera);
        controlPanel.setClickSelection(Mouse.LEFT);
        controlPanel.setSelectionColor(ColorRgba.GREEN);

        handlerEntity.createLayers(map);
        handlerEntity.setClickAssignment(Mouse.RIGHT);

        final GoldMine goldMine = (GoldMine) createEntity(GoldMine.MEDIA, 20, 23);

        UnitWorker peon = (UnitWorker) createEntity(Peon.MEDIA, 25, 20);
        peon.setResource(goldMine);
        peon.startExtraction();

        peon = (UnitWorker) createEntity(Peon.MEDIA, 23, 18);
        peon.addToProductionQueue(factoryProduction.create(BarracksOrc.MEDIA, 17, 15));
        peon.addToProductionQueue(factoryProduction.create(FarmOrc.MEDIA, 31, 19));

        final UnitAttacker grunt = (UnitAttacker) createEntity(Grunt.MEDIA, 33, 25);
        final UnitAttacker spearman = (UnitAttacker) createEntity(Spearman.MEDIA, 27, 22);
        spearman.attack(grunt);
        grunt.attack(spearman);

        final BuildingProducer townHall = (BuildingProducer) createEntity(TownhallOrc.MEDIA, 24, 15);
        townHall.setFrame(2);
        townHall.addToProductionQueue(factoryProduction.create(Peon.MEDIA));
    }

    @Override
    public void update(double extrp)
    {
        if (keyboard.isPressed(Keyboard.ESCAPE))
        {
            end();
        }
        camera.update(keyboard);
        text.update(camera);
        cursor.update(extrp);
        controlPanel.update(extrp, camera, cursor);
        handlerEntity.update(extrp);
        handlerProjectile.update(extrp);
    }

    @Override
    public void render(Graphic g)
    {
        map.render(g, camera);
        handlerEntity.render(g);
        handlerProjectile.render(g);
        controlPanel.renderCursorSelection(g, camera);
        cursor.render(g);
    }

    @Override
    protected void onTerminate(boolean hasNextSequence)
    {
        Engine.terminate();
    }
}
