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
package com.b3dgs.lionengine.example.d_rts.d_ability;

import java.io.IOException;

import com.b3dgs.lionengine.core.ColorRgba;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.Sequence;
import com.b3dgs.lionengine.core.Text;
import com.b3dgs.lionengine.core.TextStyle;
import com.b3dgs.lionengine.example.d_rts.d_ability.entity.BuildingProducer;
import com.b3dgs.lionengine.example.d_rts.d_ability.entity.Entity;
import com.b3dgs.lionengine.example.d_rts.d_ability.entity.FactoryEntity;
import com.b3dgs.lionengine.example.d_rts.d_ability.entity.GoldMine;
import com.b3dgs.lionengine.example.d_rts.d_ability.entity.UnitAttacker;
import com.b3dgs.lionengine.example.d_rts.d_ability.entity.UnitWorker;
import com.b3dgs.lionengine.file.FileReading;
import com.b3dgs.lionengine.file.FileWriting;
import com.b3dgs.lionengine.game.TextGame;
import com.b3dgs.lionengine.game.WorldGame;
import com.b3dgs.lionengine.game.rts.CameraRts;
import com.b3dgs.lionengine.game.rts.CursorRts;
import com.b3dgs.lionengine.input.Mouse;

/**
 * World implementation.
 */
final class World
        extends WorldGame
{
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
     * @see WorldGame#WorldGame(Sequence)
     */
    World(Sequence sequence)
    {
        super(sequence);
        text = new TextGame(Text.SERIF, 10, TextStyle.NORMAL);
        map = new Map();
        camera = new CameraRts(map);
        cursor = new CursorRts(mouse, camera, source, map, Media.get("cursor.png"));
        controlPanel = new ControlPanel();
        factoryProduction = new FactoryProduction();
        handlerEntity = new HandlerEntity(camera, cursor, controlPanel, map, text);
        handlerProjectile = new HandlerProjectile(camera, handlerEntity);
        context = new Context(map, handlerEntity, handlerProjectile, output.getRate());
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
        entity.setPlayerId(0);
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
    protected void saving(FileWriting file) throws IOException
    {
        map.save(file);
    }

    @Override
    protected void loading(FileReading file) throws IOException
    {
        map.load(file);

        camera.setView(0, 0, width, height);
        camera.setSensibility(30, 30);
        camera.setBorders(map);
        camera.setLocation(map, 25, 3);

        controlPanel.setClickableArea(camera);
        controlPanel.setClickSelection(Mouse.LEFT);
        controlPanel.setSelectionColor(ColorRgba.GREEN);

        handlerEntity.createLayers(map);
        handlerEntity.setClickAssignment(Mouse.RIGHT);

        final GoldMine goldMine = (GoldMine) createEntity(EntityType.GOLD_MINE, 30, 13);

        UnitWorker peon = (UnitWorker) createEntity(EntityType.PEON, 33, 6);
        peon.setResource(goldMine);
        peon.startExtraction();

        peon = (UnitWorker) createEntity(EntityType.PEON, 35, 12);
        peon.addToProductionQueue(factoryProduction.createProducible(EntityType.BARRACKS_ORC, 30, 4));
        peon.addToProductionQueue(factoryProduction.createProducible(EntityType.FARM_ORC, 40, 10));

        final UnitAttacker grunt = (UnitAttacker) createEntity(EntityType.GRUNT, 42, 9);
        final UnitAttacker spearman = (UnitAttacker) createEntity(EntityType.SPEARMAN, 39, 4);
        spearman.attack(grunt);
        grunt.attack(spearman);

        final BuildingProducer townHall = (BuildingProducer) createEntity(EntityType.TOWNHALL_ORC, 35, 7);
        townHall.setFrame(2);
        townHall.addToProductionQueue(factoryProduction.createProducible(EntityType.PEON));
    }
}
