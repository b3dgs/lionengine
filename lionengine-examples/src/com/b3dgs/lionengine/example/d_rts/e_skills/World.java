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
import com.b3dgs.lionengine.game.rts.CameraRts;
import com.b3dgs.lionengine.game.rts.WorldRts;
import com.b3dgs.lionengine.input.Mouse;

/**
 * World implementation using WorldRts.
 */
final class World
        extends WorldRts
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
     * @see WorldRts#WorldRts(Sequence)
     */
    World(Sequence sequence)
    {
        super(sequence);
        text = new TextGame(Font.SERIF, 10, Text.NORMAL);
        map = new Map();
        camera = new CameraRts(map);
        cursor = new Cursor(internal, map, Media.get("cursor.png"), Media.get("cursor_over.png"),
                Media.get("cursor_order.png"));
        controlPanel = new ControlPanel(cursor);
        handlerEntity = new HandlerEntity(cursor, controlPanel, map, text);
        handlerProjectile = new HandlerProjectile(handlerEntity);
        context = new Context(map, handlerEntity, handlerProjectile, cursor, display.getRate());
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
        handlerEntity.update(extrp, camera, cursor);
        handlerProjectile.update(extrp);
    }

    @Override
    public void render(Graphic g)
    {
        map.render(g, camera);
        handlerEntity.render(g, camera, cursor);
        handlerProjectile.render(g, camera);
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
