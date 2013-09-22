package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.item;

import com.b3dgs.lionengine.example.c_platform.e_lionheart.Context;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.effect.EffectType;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.EntityType;
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
        super(context, EntityType.TALISMENT, EffectType.TAKEN);
    }

    /*
     * EntityItem
     */

    @Override
    protected void onTaken(Valdyn entity)
    {
        entity.stats.increaseTalisment();
    }
}
