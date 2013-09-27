package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.item;

import com.b3dgs.lionengine.example.c_platform.e_lionheart.Level;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.Sfx;
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
     * @param level The level reference.
     */
    public PotionLittle(Level level)
    {
        super(level, EntityType.POTION_LITTLE, EffectType.TAKEN);
    }

    /*
     * EntityItem
     */

    @Override
    protected void onTaken(Valdyn entity)
    {
        entity.stats.increaseHeart();
        Sfx.ITEM_POTION_LITTLE.play();
    }
}
