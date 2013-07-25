package com.b3dgs.lionengine.example.d_rts.f_warcraft;

import java.awt.Color;
import java.io.IOException;

import com.b3dgs.lionengine.example.d_rts.f_warcraft.type.TypeCollision;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.type.TypeResource;
import com.b3dgs.lionengine.file.FileReading;
import com.b3dgs.lionengine.game.Tiled;
import com.b3dgs.lionengine.game.rts.map.Border20;
import com.b3dgs.lionengine.game.rts.map.Border20Map;
import com.b3dgs.lionengine.game.rts.map.MapTileRts;

/**
 * Map implementation.
 */
public final class Map
        extends MapTileRts<TypeCollision, Tile>
{
    /** Tree map layer. */
    public final Border20Map treeMap;
    /** The map theme id. */
    private int id;
    /** Tree map states. */
    private Border20[][] trees;
    /** Cut tree time number. */
    private int treeCut;

    /**
     * Constructor.
     */
    Map()
    {
        super(16, 16);
        treeMap = new Border20Map(true);
    }

    /**
     * Cut the tree and update trees around.
     * 
     * @param tree The tree to cut.
     */
    public void cutTree(Tiled tree)
    {
        final Tile tile = getTile(tree);
        tile.setNumber(treeCut);
        tile.setResourceType(TypeResource.NONE);
        tile.setCollision(TypeCollision.GROUND);
        updateTree(tile, true);
    }

    /**
     * Get the tree cut tile number.
     * 
     * @return The tree cut tile number.
     */
    public int getTreeCut()
    {
        return treeCut;
    }

    /**
     * Update the tree tile.
     * 
     * @param tile Tree tile.
     * @param check <code>true</code> to check all around, <code>false</code> else.
     */
    public void updateTree(Tile tile, boolean check)
    {
        final int tx = tile.getX() / getTileWidth();
        final int ty = tile.getY() / getTileHeight() + 1;
        if (check)
        {
            treeMap.checkAll(trees, tx, ty, 0, 0, 1);
        }
        else
        {
            treeMap.updateExclude(trees, tx, ty);
        }
        treeMap.finalCheck(trees, tx, ty);
        for (int v = ty - 1; v <= ty + 1; v++)
        {
            for (int h = tx - 1; h <= tx + 1; h++)
            {
                final Tile t = getTile(h, v);
                if (t != null)
                {
                    if (t.getId() != Border20.NONE)
                    {
                        final Border20 a = treeMap.get(trees, h, v);
                        t.setNumber(a);
                    }
                    else
                    {
                        treeMap.set(trees, h, v, Border20.NONE);
                    }
                }
            }
        }
    }

    /**
     * Set the tile tree id.
     * 
     * @param tile The tile reference.
     * @param id The tree id.
     */
    public void setTree(Tile tile, Border20 id)
    {
        treeMap.set(trees, tile.getX() / getTileWidth(), -tile.getY() / getTileHeight(), id);
    }

    @Override
    public void create(int widthInTile, int heightInTile)
    {
        super.create(widthInTile, heightInTile);
        trees = new Border20[heightInTile][widthInTile];
        treeMap.create(this);
        treeCut = 124;
        if (1 == id)
        {
            treeCut = 143;
        }
    }

    @Override
    public Tile createTile(int width, int height)
    {
        return new Tile(width, height, id);
    }

    @Override
    public void load(FileReading file) throws IOException
    {
        super.load(file);
        for (int v = 0; v < heightInTile; v++)
        {
            for (int h = 0; h < widthInTile; h++)
            {
                final Tile tile = getTile(h, v);
                trees[v][h] = tile.getId();
            }
        }
    }

    @Override
    protected Color getTilePixelColor(Tile tile)
    {
        return tile.getColor().getTheme(id);
    }
}
