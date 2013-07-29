package com.b3dgs.lionengine.example.d_rts.f_warcraft;

import com.b3dgs.lionengine.example.d_rts.f_warcraft.type.TypeCollision;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.type.TypeResource;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.type.TypeTileColor;
import com.b3dgs.lionengine.game.rts.map.Border20;
import com.b3dgs.lionengine.game.rts.map.TileRts;

/**
 * Tile implementation.
 */
public final class Tile
        extends TileRts<TypeCollision, TypeResource>
{
    /** Border values. */
    private static final Border20[] VALUES = Border20.values();
    /** The collision type color. */
    private TypeTileColor color;
    /** Current tile id (tree). */
    private Border20 tree;
    /** Tile offset number depending of the theme. */
    private int offset;

    /**
     * Constructor.
     * 
     * @param width The tile width.
     * @param height The tile height.
     * @param theme The theme id.
     */
    Tile(int width, int height, int theme)
    {
        super(width, height);
        tree = Border20.NONE;
        offset = 0;
        if (1 == theme)
        {
            offset = 19;
        }
    }

    /**
     * Get the tile color representation on minimap.
     * 
     * @return The color on minimap.
     */
    public TypeTileColor getColor()
    {
        return color;
    }

    /**
     * Set tree id.
     * 
     * @param id The tree id.
     */
    public void setNumber(Border20 id)
    {
        tree = id;
        super.setNumber(id.ordinal() + 125 + offset);
    }

    /**
     * Get the tree id.
     * 
     * @return The tree id.
     */
    public Border20 getId()
    {
        return tree;
    }

    @Override
    public TypeCollision getCollisionFrom(String collision, String type)
    {
        try
        {
            final TypeCollision tileCollision = TypeCollision.valueOf(type);
            if (TypeCollision.TREE == tileCollision)
            {
                if (getNumber() >= 125 + offset && getNumber() <= 144 + offset)
                {
                    tree = Tile.VALUES[getNumber() - (125 + offset)];
                }
            }
            color = TypeTileColor.valueOf(collision);
            return tileCollision;
        }
        catch (IllegalArgumentException
               | NullPointerException exception)
        {
            return TypeCollision.NONE;
        }
    }

    @Override
    public void checkResourceType(TypeCollision collision)
    {
        switch (collision)
        {
            case TREE:
                setResourceType(TypeResource.WOOD);
                break;
            default:
                setResourceType(TypeResource.NONE);
                break;
        }
    }

    @Override
    public boolean checkBlocking(TypeCollision collision)
    {
        return TypeCollision.GROUND != collision;
    }

    @Override
    public void setNumber(int number)
    {
        super.setNumber(number);
        if (number >= 125 + offset && number <= 144 + offset)
        {
            tree = Tile.VALUES[number - (125 + offset)];
        }
        else
        {
            tree = Border20.NONE;
        }
    }

    @Override
    public boolean hasResources()
    {
        return getResourceType() != TypeResource.NONE;
    }
}
