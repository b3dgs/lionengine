package com.b3dgs.lionengine.example.e_shmup.c_tyrian.effect;

import java.util.Locale;

/**
 * List of effect types.
 */
public enum EffectType
{
    /** Smoke effect. */
    SMOKE;

    @Override
    public String toString()
    {
        return name().toLowerCase(Locale.ENGLISH);
    }
}
