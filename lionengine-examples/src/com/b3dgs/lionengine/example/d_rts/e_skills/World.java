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
package com.b3dgs.lionengine.example.d_rts.e_skills;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Sequence;
import com.b3dgs.lionengine.Text;
import com.b3dgs.lionengine.example.d_rts.e_skills.entity.Entity;
import com.b3dgs.lionengine.example.d_rts.e_skills.entity.FactoryEntity;
import com.b3dgs.lionengine.file.FileReading;
import com.b3dgs.lionengine.file.FileWriting;
import com.b3dgs.lionengine.game.TextGame;
import com.b3dgs.lionengine.game.WorldGame;
import com.b3dgs.lionengine.game.rts.CameraRts;
import com.b3dgs.lionengine.input.Mouse;

/**
 * World implementation using WorldRts.
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
    private final Cursor cursor;
    /** Control panel reference. */
    private final ControlPanel controlPanel;
    /** Entity factory. */
    private final FactoryEntity factoryEntity;
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
        text = new TextGame(Font.SERIF, 10, Text.NORMAL);
        map = new Map();
        camera = new CameraRts(map);
        cursor = new Cursor(source, map, Media.get("cursor.png"), Media.get("cursor_over.png"),
                Media.get("cursor_order.png"));
        controlPanel = new ControlPanel(cursor);
        handlerEntity = new HandlerEntity(camera, cursor, controlPanel, map, text);
        handlerProjectile = new HandlerProjectile(camera, handlerEntity);
        context = new Context(map, handlerEntity, handlerProjectile, cursor, output.getRate());
        factoryEntity = context.factoryEntity;
        context.assignContext();
    }

    /*
     * WorldRts
     */

    @Override
    public void update(double extrp)
    {
        camera.update(keyboard);
        text.update(camera);
        cursor.update(extrp, camera, mouse, true);
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
        cursor.renderBox(g);
        controlPanel.renderCursorSelection(g, camera);
        controlPanel.render(g, cursor, camera);
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

        camera.setView(72, 12, 240, 176);
        camera.setSensibility(30, 30);
        camera.setBorders(map);
        camera.setLocation(map, 28, 0);

        controlPanel.setClickableArea(camera);
        controlPanel.setSelectionColor(Color.GREEN);
        controlPanel.setClickSelection(Mouse.LEFT);

        handlerEntity.createLayers(map);
        handlerEntity.setClickAssignment(Mouse.RIGHT);

        final Entity peon = factoryEntity.createEntity(EntityType.PEON);
        peon.setPlayerId(0);
        peon.setLocation(35, 5);
        handlerEntity.add(peon);

        Entity grunt = factoryEntity.createEntity(EntityType.GRUNT);
        grunt.setPlayerId(0);
        grunt.setLocation(39, 7);
        handlerEntity.add(grunt);

        grunt = factoryEntity.createEntity(EntityType.GRUNT);
        grunt.setPlayerId(0);
        grunt.setLocation(41, 8);
        handlerEntity.add(grunt);
    }
}
