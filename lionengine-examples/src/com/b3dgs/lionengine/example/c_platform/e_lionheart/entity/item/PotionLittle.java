package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.item;

import com.b3dgs.lionengine.example.c_platform.e_lionheart.Context;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.effect.EffectType;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.EntityType;
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
        super(context, EntityType.POTION_LITTLE, EffectType.TAKEN);
    }

    /*
     * EntityItem
     */

    @Override
    protected void onTaken(Valdyn entity)
    {
        entity.stats.increaseHeart();
    }
}
