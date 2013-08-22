package com.b3dgs.lionengine.example.d_rts.b_cursor;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Sequence;
import com.b3dgs.lionengine.Text;
import com.b3dgs.lionengine.file.FileReading;
import com.b3dgs.lionengine.file.FileWriting;
import com.b3dgs.lionengine.game.TextGame;
import com.b3dgs.lionengine.game.rts.CameraRts;
import com.b3dgs.lionengine.game.rts.CursorRts;
import com.b3dgs.lionengine.game.rts.WorldRts;

/**
 * World implementation.
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
    private final CursorRts cursor;

    /**
     * Constructor.
     * 
     * @param sequence The sequence reference.
     */
    World(Sequence sequence)
    {
        super(sequence);
        text = new TextGame(Font.SERIF, 10, Text.NORMAL);
        map = new Map();
        camera = new CameraRts(map);
        cursor = new CursorRts(internal, map, Media.get("cursor.png"));
    }

    /**
     * Draw info about the specified tile
     * 
     * @param g The graphics output.
     * @param tx The tile location x.
     * @param ty The tile location y.
     */
    private void renderTileInfo(Graphic g, int tx, int ty)
    {
        final Tile tile = map.getTile(tx, ty);
        if (tile != null)
        {
            final int x = tx * map.getTileWidth();
            final int y = ty * map.getTileHeight();

            text.drawRect(g, Color.GREEN, x, y, map.getTileWidth(), map.getTileHeight());
            text.setColor(Color.YELLOW);
            text.draw(g, x + 18, y + 20, "Coll: " + tile.getCollision());
            text.draw(g, x + 18, y + 11, "Tile number: " + tile.getNumber());
            text.draw(g, x + 18, y + 2, "tx = " + tx + " | ty = " + ty);
        }
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
    }

    @Override
    public void render(Graphic g)
    {
        map.render(g, camera);
        renderTileInfo(g, cursor.getLocationInTileX(), cursor.getLocationInTileY());
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
    }

    @Override
    protected void loaded()
    {
        camera.setView(0, 0, width, height);
        camera.setSensibility(30, 30);
        camera.setBorders(map);
    }
}
