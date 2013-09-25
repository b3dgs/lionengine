package com.b3dgs.lionengine.example.snippet;

import java.io.IOException;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Loader;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Sequence;
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.file.File;
import com.b3dgs.lionengine.file.FileReading;
import com.b3dgs.lionengine.file.FileWriting;
import com.b3dgs.lionengine.game.Alterable;
import com.b3dgs.lionengine.game.Attribute;
import com.b3dgs.lionengine.game.Damages;
import com.b3dgs.lionengine.game.FactoryGame;
import com.b3dgs.lionengine.game.Resource;
import com.b3dgs.lionengine.game.SetupGame;
import com.b3dgs.lionengine.game.WorldGame;
import com.b3dgs.lionengine.game.entity.EntityGame;
import com.b3dgs.lionengine.game.entity.FactoryEntityGame;
import com.b3dgs.lionengine.game.entity.SetupEntityGame;
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
            extends FactoryEntityGame<EntityType, SetupEntityGame, EntityGame>
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
        protected SetupEntityGame createSetup(EntityType id)
        {
            return new SetupEntityGame(Media.get("directory", id + ".xml"));
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

    public class MySequence
            extends Sequence
    {
        private final World world;

        public MySequence(Loader loader)
        {
            super(loader);
            // Initialize variables here
            world = new World(this);
        }

        @Override
        protected void load()
        {
            // Load resources here
            world.loadFromFile(Media.get("level.lvl"));
        }

        @Override
        protected void update(double extrp)
        {
            // Update routine
            world.update(extrp);
        }

        @Override
        protected void render(Graphic g)
        {
            // Render routine
            world.render(g);
        }

        @Override
        protected void onTerminate(boolean hasNextSequence)
        {
            // Called when sequence is closing, optional
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
