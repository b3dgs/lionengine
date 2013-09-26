package com.b3dgs.lionengine.example.d_rts.f_warcraft;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Sequence;
import com.b3dgs.lionengine.Text;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.entity.Entity;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.entity.FactoryEntity;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.type.TypeEntity;
import com.b3dgs.lionengine.file.FileReading;
import com.b3dgs.lionengine.file.FileWriting;
import com.b3dgs.lionengine.game.TextGame;
import com.b3dgs.lionengine.game.TimedMessage;
import com.b3dgs.lionengine.game.WorldGame;
import com.b3dgs.lionengine.input.Mouse;

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
        text = new TextGame(Font.SERIF, 10, Text.NORMAL);
        player = new Player();
        cpu = new Player();
        map = new Map();
        fogOfWar = new FogOfWar(config);
        cursor = new Cursor(internal, map, Media.get("cursor.png"), Media.get("cursor_over.png"),
                Media.get("cursor_order.png"));
        message = new TimedMessage(new Text(Font.DIALOG, 10, Text.NORMAL));
        controlPanel = new ControlPanel(cursor);
        camera = new Camera(map, controlPanel);
        handlerEntity = new HandlerEntity(cursor, controlPanel, map, fogOfWar);
        minimap = new Minimap(map, fogOfWar, controlPanel, handlerEntity, 3, 6);
        handlerProjectile = new HandlerProjectile(handlerEntity);
        context = new Context(map, handlerEntity, handlerProjectile, cursor, message, display.getRate());
        factoryEntity = context.factoryEntity;
        context.assignContext();
        handlerEntity.addListener(minimap);
    }

    /**
     * Create an entity from its type.
     * 
     * @param type The entity type.
     * @param tx The horizontal location.
     * @param ty The vertical location.
     * @return The entity instance.
     */
    private Entity createEntity(TypeEntity type, int tx, int ty)
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
        cursor.update(extrp, camera, mouse, true);
        controlPanel.update(extrp, camera, cursor, keyboard);
        handlerEntity.update(extrp, camera, cursor);
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
        handlerEntity.render(g, camera, cursor);
        handlerProjectile.render(g, camera);
        context.handlerEffect.render(g, camera);
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

        fogOfWar.create(map);
        fogOfWar.setPlayerId(player.id);

        controlPanel.setClickableArea(camera);
        controlPanel.setSelectionColor(Color.GREEN);
        controlPanel.setPlayer(player);
        controlPanel.setClickSelection(Mouse.LEFT);

        handlerEntity.createLayers(map);
        handlerEntity.setPlayer(player);
        handlerEntity.setClickAssignment(Mouse.RIGHT);

        factoryEntity.setContext(context);

        createEntity(TypeEntity.gold_mine, 30, 13);
        createEntity(TypeEntity.gold_mine, 58, 58);

        final Entity peon = createEntity(TypeEntity.peon, 40, 8);
        peon.setPlayer(player);

        Entity grunt = createEntity(TypeEntity.grunt, 38, 5);
        grunt.setPlayer(player);

        grunt = createEntity(TypeEntity.grunt, 39, 5);
        grunt.setPlayer(player);

        final Entity spearman = createEntity(TypeEntity.spearman, 38, 9);
        spearman.setPlayer(player);

        final Entity townHall = createEntity(TypeEntity.townhall_orc, 40, 5);
        townHall.setPlayer(player);

        final Entity peasant = createEntity(TypeEntity.peasant, 40, 10);
        peasant.setPlayer(cpu);

        handlerEntity.update(1.0f, camera, cursor);
        handlerEntity.updatePopulation();
        minimap.entityMoved(peon);
    }
}
