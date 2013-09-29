package com.b3dgs.lionengine.example.e_shmup.c_tyrian.map;

import com.b3dgs.lionengine.example.e_shmup.c_tyrian.HandlerEntity;
import com.b3dgs.lionengine.example.e_shmup.c_tyrian.entity.Entity;
import com.b3dgs.lionengine.example.e_shmup.c_tyrian.entity.EntityStaticType;
import com.b3dgs.lionengine.example.e_shmup.c_tyrian.entity.FactoryEntityStatic;
import com.b3dgs.lionengine.game.map.MapTileGame;

/**
 * Map implementation.
 */
public final class Map
        extends MapTileGame<TileCollision, Tile>
{
    /**
     * Create the entity from the tile number.
     * 
     * @param factoryEntityStatic The entity static factory.
     * @param n The tile number.
     * @return The entity instance, <code>null</code> if none.
     */
    private static Entity create(FactoryEntityStatic factoryEntityStatic, int n)
    {
        switch (n)
        {
            case 6:
                return factoryEntityStatic.createEntity(EntityStaticType.L);
            case 7:
                return factoryEntityStatic.createEntity(EntityStaticType.K);
            case 8:
                return factoryEntityStatic.createEntity(EntityStaticType.Q);
            case 9:
                return factoryEntityStatic.createEntity(EntityStaticType.J);
            case 19:
                return factoryEntityStatic.createEntity(EntityStaticType.I);
            case 23:
                return factoryEntityStatic.createEntity(EntityStaticType.T);
            case 26:
                return factoryEntityStatic.createEntity(EntityStaticType.M);
            case 28:
                return factoryEntityStatic.createEntity(EntityStaticType.F);
            case 30:
                return factoryEntityStatic.createEntity(EntityStaticType.E);
            case 32:
                return factoryEntityStatic.createEntity(EntityStaticType.A);
            case 45:
                return factoryEntityStatic.createEntity(EntityStaticType.H);
            case 46:
                return factoryEntityStatic.createEntity(EntityStaticType.P);
            case 56:
                return factoryEntityStatic.createEntity(EntityStaticType.S);
            case 60:
                return factoryEntityStatic.createEntity(EntityStaticType.D);
            case 62:
                return factoryEntityStatic.createEntity(EntityStaticType.C);
            case 64:
                return factoryEntityStatic.createEntity(EntityStaticType.B);
            case 66:
                return factoryEntityStatic.createEntity(EntityStaticType.R);
            default:
                return null;
        }
    }

    /**
     * Constructor.
     */
    public Map()
    {
        super(24, 28);
    }

    /**
     * Create static entities depending of the tile.
     * 
     * @param factoryEntityStatic The entity static factory.
     * @param handlerEntity The handler entity.
     */
    public void spawnEntityStatic(FactoryEntityStatic factoryEntityStatic, HandlerEntity handlerEntity)
    {
        for (int tx = 0; tx < widthInTile; tx++)
        {
            for (int ty = 0; ty < heightInTile; ty++)
            {
                final Tile tile = getTile(tx, ty);
                if (tile != null)
                {
                    final int n = tile.getNumber();
                    final Entity entity = create(factoryEntityStatic, n);
                    if (entity != null)
                    {
                        entity.teleport(tile.getX(), tile.getY() + 28);
                        handlerEntity.add(entity);
                    }
                }
            }
        }
    }

    /*
     * MapTileShmup
     */

    @Override
    public Tile createTile(int width, int height, Integer pattern, int number, TileCollision collision)
    {
        return new Tile(width, height, pattern, number, collision);
    }

    @Override
    public TileCollision getCollisionFrom(String collision)
    {
        return TileCollision.NONE;
    }
}
