package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity;

import java.util.Locale;

/**
 * List of entity collision types.
 */
public enum TypeEntityCollision
{
    /** Default collision. */
    DEFAULT;

    @Override
    public String toString()
    {
        return name().toLowerCase(Locale.ENGLISH);
    }
}
