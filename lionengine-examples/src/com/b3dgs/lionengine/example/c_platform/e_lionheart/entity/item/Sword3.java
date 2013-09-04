package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.item;

import com.b3dgs.lionengine.example.c_platform.e_lionheart.Context;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.effect.TypeEffect;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.TypeEntity;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.player.Valdyn;

/**
 * Sword3 item. Third level of the sword.
 */
public final class Sword3
        extends EntityItem
{
    /**
     * Constructor.
     * 
     * @param context The context reference.
     */
    public Sword3(Context context)
    {
        super(context, TypeEntity.SWORD3, TypeEffect.TAKEN);
    }

    /*
     * EntityItem
     */

    @Override
    protected void onTaken(Valdyn entity)
    {
        // TODO: Set sword level to 3
    }
}
