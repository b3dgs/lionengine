package com.b3dgs.lionengine.example.e_shmup.c_tyrian.entity.ship;

import java.util.Locale;

/**
 * List of ship types.
 */
public enum ShipType
{
    /** Gencore phoenix. */
    GENCORE_PHOENIX;

    @Override
    public String toString()
    {
        return name().toLowerCase(Locale.ENGLISH);
    }
}
