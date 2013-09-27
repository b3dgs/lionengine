package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.item;

import com.b3dgs.lionengine.example.c_platform.e_lionheart.Level;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.Sfx;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.effect.EffectType;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.EntityType;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.player.Valdyn;

/**
 * Life item. Give a life to the player when taken.
 */
public final class Life
        extends EntityItem
{
    /**
     * Constructor.
     * 
     * @param level The level reference.
     */
    public Life(Level level)
    {
        super(level, EntityType.LIFE, EffectType.TAKEN);
    }

    /*
     * EntityItem
     */

    @Override
    protected void onTaken(Valdyn entity)
    {
        entity.stats.increaseLife();
        Sfx.ITEM_TAKEN.play();
    }
}
