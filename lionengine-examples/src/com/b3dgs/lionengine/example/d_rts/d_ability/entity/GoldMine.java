package com.b3dgs.lionengine.example.d_rts.d_ability.entity;

import com.b3dgs.lionengine.example.d_rts.d_ability.Context;
import com.b3dgs.lionengine.example.d_rts.d_ability.TypeEntity;
import com.b3dgs.lionengine.example.d_rts.d_ability.TypeResource;
import com.b3dgs.lionengine.game.Alterable;
import com.b3dgs.lionengine.game.rts.ability.extractor.Extractible;

/**
 * Gold mine building implementation. This building allows to extract gold with a worker.
 */
public final class GoldMine
        extends Building
        implements Extractible<TypeResource>
{
    /** Gold amount. */
    private final Alterable gold;
    /** Resource type. */
    private final TypeResource typeResource;

    /**
     * Constructor.
     * 
     * @param context The context reference.
     */
    GoldMine(Context context)
    {
        super(TypeEntity.gold_mine, context);
        typeResource = TypeResource.GOLD;
        gold = new Alterable(50000);
        setFrame(1);
    }

    /*
     * Building
     */

    @Override
    public void stop()
    {
        // Nothing to do
    }

    /*
     * Extractible
     */

    @Override
    public int extractResource(int amount)
    {
        return gold.decrease(amount);
    }

    @Override
    public int getResourceQuantity()
    {
        return gold.getCurrent();
    }

    @Override
    public TypeResource getResourceType()
    {
        return typeResource;
    }
}
