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
package com.b3dgs.lionengine.example.c_platform.c_entitymap;

import java.io.IOException;

import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.core.Sequence;
import com.b3dgs.lionengine.file.FileReading;
import com.b3dgs.lionengine.file.FileWriting;
import com.b3dgs.lionengine.game.WorldGame;
import com.b3dgs.lionengine.game.platform.CameraPlatform;

/**
 * World implementation.
 */
final class World
        extends WorldGame
{
    /** Background color. */
    private static final ColorRgba BACKGROUND_COLOR = new ColorRgba(107, 136, 255);
    /** Camera reference. */
    private final CameraPlatform camera;
    /** Map reference. */
    private final Map map;
    /** Mario reference. */
    private final Mario hero;

    /**
     * @see WorldGame#WorldGame(Sequence)
     */
    World(Sequence sequence)
    {
        super(sequence);
        camera = new CameraPlatform(width, height);
        map = new Map();
        hero = new Mario(map, source.getRate());
    }

    /*
     * WorldGame
     */

    @Override
    public void update(double extrp)
    {
        hero.updateControl(keyboard);
        hero.update(extrp);
        camera.follow(hero);
    }

    @Override
    public void render(Graphic g)
    {
        g.setColor(World.BACKGROUND_COLOR);
        g.drawRect(0, 0, width, height, true);
        // Draw the map
        map.render(g, camera);
        // Draw the hero
        hero.render(g, camera);
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
        camera.setLimits(map);
        camera.setIntervals(16, 0);
        map.adjustCollisions();
        hero.respawn();
    }
}
