package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.item;

import com.b3dgs.lionengine.example.c_platform.e_lionheart.Level;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.Sfx;
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
     * @param level The level reference.
     */
    public Talisment(Level level)
    {
        super(level, EntityType.TALISMENT, EffectType.TAKEN);
    }

    /*
     * EntityItem
     */

    @Override
    protected void onTaken(Valdyn entity)
    {
        entity.stats.increaseTalisment();
        Sfx.ITEM_TAKEN.play();
    }
}
