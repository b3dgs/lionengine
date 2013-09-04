package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.item;

import com.b3dgs.lionengine.example.c_platform.e_lionheart.Context;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.effect.TypeEffect;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.TypeEntity;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.player.Valdyn;

/**
 * Little Potion item. Give a single heart to the player.
 */
public final class PotionLittle
        extends EntityItem
{
    /**
     * Constructor.
     * 
     * @param context The context reference.
     */
    public PotionLittle(Context context)
    {
        super(context, TypeEntity.POTION_LITTLE, TypeEffect.TAKEN);
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
