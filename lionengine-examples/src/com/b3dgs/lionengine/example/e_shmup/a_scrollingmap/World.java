package com.b3dgs.lionengine.example.e_shmup.a_scrollingmap;

import java.io.IOException;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Sequence;
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.file.File;
import com.b3dgs.lionengine.file.FileReading;
import com.b3dgs.lionengine.file.FileWriting;
import com.b3dgs.lionengine.game.CameraGame;
import com.b3dgs.lionengine.game.WorldGame;
import com.b3dgs.lionengine.utility.LevelRipConverter;

/**
 * World implementation using WorldGame.
 */
public class World
        extends WorldGame
{
    /** Map reference. */
    private final Map map;
    /** Background reference. */
    private final Background background;
    /** Camera reference. */
    private final CameraGame camera;
    /** Camera offset. */
    private double y;

    /**
     * Default constructor.
     * 
     * @param sequence The sequence reference.
     */
    public World(Sequence sequence)
    {
        super(sequence);
        map = new Map();
        background = new Background();
        camera = new CameraGame();
        camera.setView(0, 0, width, height);
        y = -210;

        // Rip a level and store data in the map
        ripLevel(Media.get("levels", "images", "0.png"), Media.get("tiles", "level1"), Media.get("levels", "0.map"));
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
        final LevelRipConverter<TileCollision, Tile> rip = new LevelRipConverter<>();
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

    @Override
    public void update(double extrp)
    {
        camera.setLocationY(y);
        y += 1.0;
        if (y > 220)
        {
            y = -220;
        }
        background.update(extrp);
    }

    @Override
    public void render(Graphic g)
    {
        background.render(g, camera);
        map.render(g, camera);
    }

    @Override
    protected void loaded()
    {
        // Nothing to do
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
