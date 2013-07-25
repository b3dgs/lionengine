package com.b3dgs.lionengine.example.d_rts.c_controlpanel;

/**
 * Peon implementation.
 */
final class Peon
        extends Entity
{
    /**
     * Constructor.
     * 
     * @param context The context reference.
     */
    Peon(Context context)
    {
        super(TypeEntity.peon, context);
        life.setMax(60);
        life.fill();
    }
}
