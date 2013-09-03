package com.b3dgs.lionengine.example.c_platform.e_lionheart.landscape;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.b3dgs.lionengine.example.c_platform.e_lionheart.TypeWorld;

/**
 * Types of landscapes.
 */
public enum TypeLandscape
{
    /** Swamp dusk. */
    SWAMP_DUSK(0, TypeWorld.SWAMP, "dusk", "raster1.xml", TypeForeground.WATER),
    /** Swamp dawn. */
    SWAMP_DAWN(1, TypeWorld.SWAMP, "dawn", "raster2.xml", TypeForeground.WATER),
    /** Swamp day. */
    SWAMP_DAY(2, TypeWorld.SWAMP, "day", "raster3.xml", TypeForeground.WATER);

    /** Values. */
    private static final TypeLandscape[] VALUES = TypeLandscape.values();

    /**
     * Get the type from its index.
     * 
     * @param index The index.
     * @return The type.
     */
    public static TypeLandscape get(int index)
    {
        return TypeLandscape.VALUES[index];
    }

    /**
     * Get all landscapes related to the world.
     * 
     * @param world The world used as reference.
     * @return The landscapes of this world.
     */
    public static TypeLandscape[] getWorldLandscape(TypeWorld world)
    {
        final List<TypeLandscape> landscapes = new ArrayList<>(3);
        for (final TypeLandscape landscape : TypeLandscape.VALUES)
        {
            if (landscape.getWorld() == world)
            {
                landscapes.add(landscape);
            }
        }
        return landscapes.toArray(new TypeLandscape[landscapes.size()]);
    }

    /** Index. */
    private final int index;
    /** World type. */
    private final TypeWorld world;
    /** Theme name. */
    private final String theme;
    /** Raster name. */
    private final String raster;
    /** The foreground used. */
    private final TypeForeground foreground;

    /**
     * Constructor.
     * 
     * @param index The index number.
     * @param world The world type.
     * @param theme The theme name.
     * @param raster The raster name.
     * @param water The water type.
     */
    private TypeLandscape(int index, TypeWorld world, String theme, String raster, TypeForeground water)
    {
        this.index = index;
        this.world = world;
        this.theme = theme;
        this.raster = raster;
        foreground = water;
    }

    /**
     * Get the index value.
     * 
     * @return The index value.
     */
    public int getIndex()
    {
        return index;
    }

    /**
     * Get the world type.
     * 
     * @return The world type.
     */
    public TypeWorld getWorld()
    {
        return world;
    }

    /**
     * Get the theme name.
     * 
     * @return The theme name.
     */
    public String getTheme()
    {
        return theme;
    }

    /**
     * Get the raster filename.
     * 
     * @return The raster filename.
     */
    public String getRaster()
    {
        return raster;
    }

    /**
     * Get the foreground used.
     * 
     * @return The foreground used.
     */
    public TypeForeground getForeground()
    {
        return foreground;
    }

    @Override
    public String toString()
    {
        final String string = name().toLowerCase(Locale.ENGLISH).replace('_', ' ');
        return Character.toString(string.charAt(0)).toUpperCase(Locale.ENGLISH) + string.substring(1);
    }
}
