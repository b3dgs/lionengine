package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.item;

import com.b3dgs.lionengine.example.c_platform.e_lionheart.Level;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.Sfx;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.effect.EffectType;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.EntityType;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.player.Valdyn;

/**
 * Sword2 item. Second level of the sword.
 */
public final class Sword2
        extends EntityItem
{
    /**
     * Constructor.
     * 
     * @param level The level reference.
     */
    public Sword2(Level level)
    {
        super(level, EntityType.SWORD2, EffectType.TAKEN);
    }

    /*
     * EntityItem
     */

    @Override
    protected void onTaken(Valdyn entity)
    {
        entity.stats.setSwordLevel(2);
        Sfx.ITEM_TAKEN.play();
    }
}
