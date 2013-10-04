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
package com.b3dgs.lionengine.example.snippet;

import java.io.IOException;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.core.Loader;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.Sequence;
import com.b3dgs.lionengine.core.Verbose;
import com.b3dgs.lionengine.file.File;
import com.b3dgs.lionengine.file.FileReading;
import com.b3dgs.lionengine.file.FileWriting;
import com.b3dgs.lionengine.game.Alterable;
import com.b3dgs.lionengine.game.Attribute;
import com.b3dgs.lionengine.game.Damages;
import com.b3dgs.lionengine.game.FactoryGame;
import com.b3dgs.lionengine.game.Resource;
import com.b3dgs.lionengine.game.SetupGame;
import com.b3dgs.lionengine.game.SetupSurfaceGame;
import com.b3dgs.lionengine.game.WorldGame;
import com.b3dgs.lionengine.game.entity.EntityGame;
import com.b3dgs.lionengine.game.entity.FactoryEntityGame;
import com.b3dgs.lionengine.game.map.MapTileGame;
import com.b3dgs.lionengine.game.map.TileGame;
import com.b3dgs.lionengine.utility.LevelRipConverter;
import com.b3dgs.lionengine.utility.TileExtractor;

@SuppressWarnings("all")
public class ModuleGame
{
    enum EntityType
    {

    }

    enum TileCollision
    {

    }

    class Tile
            extends TileGame<TileCollision>
    {
        public Tile(int width, int height, Integer pattern, int number, TileCollision collision)
        {
            super(width, height, pattern, number, collision);
        }
    }

    class World
            extends WorldGame
    {
        World(Sequence sequence)
        {
            super(sequence);
        }

        @Override
        public void update(double extrp)
        {
        }

        @Override
        public void render(Graphic g)
        {
        }

        @Override
        protected void saving(FileWriting file) throws IOException
        {
        }

        @Override
        protected void loading(FileReading file) throws IOException
        {
        }
    }

    MapTileGame<TileCollision, Tile> map;

    /*
     * Snippet code
     */

    void alterable()
    {
        final Alterable life = new Alterable(100);
        life.decrease(25); // life = 75
        life.fill(); // life = 100
        life.isFull(); // returns true
        life.getNeeded(150); // returns 50
        life.setMax(150);
        life.fill(); // life = 150
        life.increase(25); // life = 150
        life.set(0); // life = 0
        life.isEmpty(); // returns true
    }

    void attribute()
    {
        final Attribute vitality = new Attribute();
        vitality.set(1);
        vitality.increase(2);
        System.out.println(vitality.get()); // print 3
    }

    void damages()
    {
        final Damages dmg = new Damages(1, 3);
        dmg.getMax(); // returns 3
        dmg.getRandom(); // returns a value between 1 and 3 included
        dmg.getLast(); // returns the last value return by getRandom()
        dmg.setMin(5);
        dmg.setMax(10);
        dmg.getMin(); // returns 5
        dmg.getRandom(); // returns a value between 5 and 10 included
    }

    public class Factory
            extends FactoryGame<EntityType, SetupGame>
    {
        public Factory()
        {
            super(EntityType.class);
            loadAll(EntityType.values());
        }

        @Override
        protected SetupGame createSetup(EntityType id)
        {
            return new SetupGame(Media.get("directory", id + ".xml"));
        }
    }

    public class FactoryEntity
            extends FactoryEntityGame<EntityType, SetupSurfaceGame, EntityGame>
    {
        public FactoryEntity()
        {
            super(EntityType.class);
            loadAll(EntityType.values());
        }

        @Override
        public EntityGame createEntity(EntityType id)
        {
            switch (id)
            {
                default:
                    throw new LionEngineException("Unknown entity: " + id);
            }
        }

        @Override
        protected SetupSurfaceGame createSetup(EntityType id)
        {
            return new SetupSurfaceGame(Media.get("directory", id + ".xml"));
        }
    }

    void resource()
    {
        final Resource gold = new Resource();
        gold.add(100);
        gold.get(); // returns 100
        gold.canSpend(25); // returns true
        gold.spend(25); // returns 75
        gold.canSpend(100); // returns false
    }

    public static class MySequence
            extends Sequence
    {
        private static final Resolution NATIVE = new Resolution(320, 240, 60);

        public MySequence(Loader loader)
        {
            super(loader, MySequence.NATIVE);
            // Initialize variables here
        }

        @Override
        protected void load()
        {
            // Load resources here
        }

        @Override
        protected void update(double extrp)
        {
            // Update routine
        }

        @Override
        protected void render(Graphic g)
        {
            // Render routine
        }
    }

    private void ripLevel(Media levelrip, Media tilesheet, Media output)
    {
        final LevelRipConverter<Tile> rip = new LevelRipConverter<>();
        rip.start(levelrip, map, tilesheet);
        try (FileWriting file = File.createFileWriting(output);)
        {
            map.save(file);
        }
        catch (final IOException exception)
        {
            Verbose.exception(World.class, "constructor", exception, "Error on saving map !");
        }
    }

    private void utilityTileExtractor()
    {
        TileExtractor.start(Media.get("level.png"), Media.get("sheet.png"), 16, 16, 256, 256);
    }
}
