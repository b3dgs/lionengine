package com.b3dgs.lionengine.example.c_platform.a_navmaptile;

import java.awt.Font;
import java.io.IOException;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Sequence;
import com.b3dgs.lionengine.Text;
import com.b3dgs.lionengine.file.FileReading;
import com.b3dgs.lionengine.file.FileWriting;
import com.b3dgs.lionengine.game.CameraGame;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.WorldGame;
import com.b3dgs.lionengine.input.Keyboard;
import com.b3dgs.lionengine.utility.LevelRipConverter;

/**
 * World implementation using WorldGame.
 */
class World
        extends WorldGame
{
    /** Map reference. */
    private final Map map;
    /** Camera reference. */
    private final CameraGame camera;
    /** Text drawer. */
    private final Text text;
    /** Camera force. */
    private final Force force;
    /** Camera movement. */
    private final Force movement;

    /**
     * Default constructor.
     * 
     * @param sequence The sequence reference.
     */
    public World(Sequence sequence)
    {
        super(sequence);
        map = new Map();
        camera = new CameraGame();
        movement = new Force();
        force = new Force();
        text = new Text(Font.SANS_SERIF, 11, Text.NORMAL);

        // Rip a level and store data in the map
        final LevelRipConverter<TileCollision, Tile> rip = new LevelRipConverter<>();
        rip.start(Media.get("level_mario.png"), map, Media.get("tiles", "mario"), false);
    }

    @Override
    public void update(double extrp)
    {
        double speed = 1.0;
        movement.setForce(0.0, 0.0);

        // Mouse control
        if (mouse.getMouseClick() > 0)
        {
            movement.setForce(-mouse.getMoveX() * 4, mouse.getMoveY() * 4);
            speed = 2.0;
        }

        // Keyboard control
        if (keyboard.isPressed(Keyboard.RIGHT))
        {
            movement.addForce(extrp, 4.0, 0.0);
        }
        if (keyboard.isPressed(Keyboard.LEFT))
        {
            movement.addForce(extrp, -4.0, 0.0);
        }
        if (keyboard.isPressed(Keyboard.UP))
        {
            movement.addForce(extrp, 0.0, 4.0);
        }
        if (keyboard.isPressed(Keyboard.DOWN))
        {
            movement.addForce(extrp, 0.0, -4.0);
        }

        // Smooth movement
        force.reachForce(extrp, movement, speed, 0.01);

        // Apply movement to camera
        camera.moveLocation(extrp, force);
    }

    @Override
    public void render(Graphic g)
    {
        // Render map using camera point of view
        map.render(g, camera);
        text.draw(g, 0, 11, camera.getLocationIntX() + " | " + camera.getLocationIntY());
    }

    @Override
    protected void loaded()
    {
        // Nothing here as the world is not loaded
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
