package com.b3dgs.lionengine.example.d_rts.e_skills.entity;

import com.b3dgs.lionengine.example.d_rts.e_skills.Context;
import com.b3dgs.lionengine.example.d_rts.e_skills.TypeEntity;

/**
 * Abstract building entity implementation
 */
abstract class Building
        extends Entity
{
    /**
     * Constructor.
     * 
     * @param id The entity type enum.
     * @param context The context reference.
     */
    protected Building(TypeEntity id, Context context)
    {
        super(id, context);
    }

    /*
     * Entity
     */

    @Override
    public void stop()
    {
        // Nothing to do
    }
}
