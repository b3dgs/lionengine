package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.item;

import com.b3dgs.lionengine.example.c_platform.e_lionheart.Context;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.effect.TypeEffect;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.TypeEntity;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.player.Valdyn;

/**
 * Talisment item.
 */
public final class Talisment
        extends EntityItem
{
    /**
     * Constructor.
     * 
     * @param context The context reference.
     */
    public Talisment(Context context)
    {
        super(context, TypeEntity.TALISMENT, TypeEffect.TAKEN);
    }

    /*
     * EntityItem
     */

    @Override
    protected void onTaken(Valdyn entity)
    {
        // Nothing for the moment
    }
}
