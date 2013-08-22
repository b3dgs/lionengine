package com.b3dgs.lionengine.example.d_rts.a_navmaptile;

import java.awt.Font;
import java.io.IOException;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Sequence;
import com.b3dgs.lionengine.Text;
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.file.File;
import com.b3dgs.lionengine.file.FileReading;
import com.b3dgs.lionengine.file.FileWriting;
import com.b3dgs.lionengine.game.rts.CameraRts;
import com.b3dgs.lionengine.game.rts.WorldRts;
import com.b3dgs.lionengine.utility.LevelRipConverter;

/**
 * World implementation.
 */
final class World
        extends WorldRts
{
    /** Text reference. */
    private final Text text;
    /** Map reference. */
    private final Map map;
    /** Camera reference. */
    private final CameraRts camera;

    /**
     * Constructor.
     * 
     * @param sequence The sequence reference.
     */
    World(Sequence sequence)
    {
        super(sequence);
        text = new Text(Font.SANS_SERIF, 10, Text.NORMAL);
        map = new Map();
        camera = new CameraRts(map);

        // Rip a level and store data in the map
        ripLevel(Media.get("maps", "forest.png"), Media.get("tiles", "forest"), Media.get("maps", "forest.map"));

        // Setup camera
        camera.setView(0, 0, width, height);
        camera.setSensibility(30, 30);
        camera.setBorders(map);
    }

    /**
     * Create a level from a level rip.
     * 
     * @param levelrip The level rip image.
     * @param tilesheet The tilesheet image.
     * @param output The output level saved.
     */
    private void ripLevel(Media levelrip, Media tilesheet, Media output)
    {
        final LevelRipConverter<TypeCollision, Tile> rip = new LevelRipConverter<>();
        rip.start(levelrip, map, tilesheet, false);
        try (FileWriting file = File.createFileWriting(output);)
        {
            map.save(file);
        }
        catch (final IOException exception)
        {
            Verbose.exception(World.class, "constructor", exception, "Error on saving map !");
        }
    }

    /*
     * WorldRts
     */

    @Override
    public void update(double extrp)
    {
        camera.update(keyboard);
    }

    @Override
    public void render(Graphic g)
    {
        // Render map using camera point of view
        map.render(g, camera);
        text.draw(g, 0, height - 8, camera.getLocationInTileX(map) + "." + camera.getLocationInTileY(map));
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
        // Nothing here for the moment
    }
}
