package com.b3dgs.lionengine.example.d_rts.c_controlpanel;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Sequence;
import com.b3dgs.lionengine.Text;
import com.b3dgs.lionengine.file.FileReading;
import com.b3dgs.lionengine.file.FileWriting;
import com.b3dgs.lionengine.game.Orientation;
import com.b3dgs.lionengine.game.TextGame;
import com.b3dgs.lionengine.game.rts.CameraRts;
import com.b3dgs.lionengine.game.rts.CursorRts;
import com.b3dgs.lionengine.game.rts.WorldRts;

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
    /** Control panel reference. */
    private final ControlPanel controlPanel;
    /** Entity factory. */
    private final FactoryEntity factoryEntity;
    /** Camera reference. */
    private final CameraRts camera;
    /** Cursor reference. */
    private final CursorRts cursor;
    /** Entity handler. */
    private final HandlerEntity handlerEntity;
    /** Context. */
    private final Context context;

    /**
     * Default constructor.
     * 
     * @param sequence The sequence reference.
     */
    World(Sequence sequence)
    {
        super(sequence);
        text = new TextGame(Font.SERIF, 10, Text.NORMAL);
        map = new Map();
        controlPanel = new ControlPanel();
        factoryEntity = new FactoryEntity();
        camera = new CameraRts(map);
        cursor = new CursorRts(internal, map, Media.get("cursor.png"));
        handlerEntity = new HandlerEntity(controlPanel, map, text);
        context = new Context(map, factoryEntity);
    }

    @Override
    public void update(double extrp)
    {
        camera.update(keyboard);
        text.update(camera);
        cursor.update(extrp, camera, mouse, true);
        controlPanel.update(extrp, camera, cursor, keyboard);
        handlerEntity.update(extrp, camera, cursor);
    }

    @Override
    public void render(Graphic g)
    {
        map.render(g, camera);
        handlerEntity.render(g, camera, cursor);
        controlPanel.renderCursorSelection(g, camera);
        cursor.render(g);
    }

    @Override
    public void loaded()
    {
        camera.setView(0, 0, width, height);
        camera.setSensibility(30, 30);
        camera.setBorders(map);
        camera.setLocation(map, 1, 2);

        controlPanel.setClickableArea(camera);
        controlPanel.setSelectionColor(Color.GREEN);

        handlerEntity.createLayers(map);
        factoryEntity.setContext(context);

        Entity peon = factoryEntity.createEntity(TypeEntity.peon);
        peon.setLocation(8, 10);
        peon.setOrientation(Orientation.EAST);
        handlerEntity.add(peon);

        peon = factoryEntity.createEntity(TypeEntity.peon);
        peon.setLocation(14, 8);
        peon.setOrientation(Orientation.NORTH_WEST);
        handlerEntity.add(peon);
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
    }
}
