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
package com.b3dgs.lionengine.example.c_platform.a_navmaptile;

import java.io.IOException;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.Text;
import com.b3dgs.lionengine.TextStyle;
import com.b3dgs.lionengine.core.Key;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.Sequence;
import com.b3dgs.lionengine.core.UtilityImage;
import com.b3dgs.lionengine.file.FileReading;
import com.b3dgs.lionengine.file.FileWriting;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.WorldGame;
import com.b3dgs.lionengine.game.platform.CameraPlatform;
import com.b3dgs.lionengine.utility.LevelRipConverter;

/**
 * World implementation.
 */
final class World
        extends WorldGame
{
    /** Text drawer. */
    private final Text text;
    /** Camera reference. */
    private final CameraPlatform camera;
    /** Map reference. */
    private final Map map;
    /** Camera force. */
    private final Force force;
    /** Camera movement. */
    private final Force movement;

    /**
     * @see WorldGame#WorldGame(Sequence)
     */
    World(Sequence sequence)
    {
        super(sequence);
        text = UtilityImage.createText(Text.SANS_SERIF, 11, TextStyle.NORMAL);
        camera = new CameraPlatform(width, height);
        map = new Map();
        force = new Force();
        movement = new Force();

        // Rip a level and store data in the map
        final LevelRipConverter<Tile> rip = new LevelRipConverter<>();
        rip.start(Media.get("level_mario.png"), map, Media.get("tiles"));
    }

    /*
     * WorldGame
     */

    @Override
    public void update(double extrp)
    {
        double speed = 1.0;
        movement.setForce(0.0, 0.0);

        // Mouse control
        if (mouse.getMouseClick() > 0)
        {
            movement.setForce(-mouse.getMoveX(), mouse.getMoveY());
            speed = 100.0;
        }

        // Keyboard control
        if (keyboard.isPressed(Key.RIGHT))
        {
            movement.addForce(4.0, 0.0);
        }
        if (keyboard.isPressed(Key.LEFT))
        {
            movement.addForce(-4.0, 0.0);
        }
        if (keyboard.isPressed(Key.UP))
        {
            movement.addForce(0.0, 4.0);
        }
        if (keyboard.isPressed(Key.DOWN))
        {
            movement.addForce(0.0, -4.0);
        }

        // Smooth movement
        force.reachForce(extrp, movement, speed, 0.001);

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
