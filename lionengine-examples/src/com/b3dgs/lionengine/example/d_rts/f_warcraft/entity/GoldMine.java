package com.b3dgs.lionengine.example.d_rts.f_warcraft.entity;

import com.b3dgs.lionengine.example.d_rts.f_warcraft.Context;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.ResourceProgressive;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.type.TypeEntity;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.type.TypeResource;
import com.b3dgs.lionengine.game.rts.ability.extractor.Extractible;

/**
 * Gold mine building implementation. This building allows to extract gold with a worker.
 */
public final class GoldMine
        extends Building
        implements Extractible<TypeResource>
{
    /** Gold amount. */
    private final ResourceProgressive gold;
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
        gold = new ResourceProgressive(100);
        setFrame(1);
    }

    @Override
    public void update(double extrp)
    {
        super.update(extrp);
        gold.update(extrp, 5.0);
    }

    /**
     * Extract the specified amount of gold.
     * 
     * @param amount The gold amount.
     * @return The extracted amount of gold.
     */
    @Override
    public int extractResource(int amount)
    {
        final int decreased = gold.decrease(amount);
        if (gold.isEmpty())
        {
            kill();
        }
        return decreased;
    }

    /**
     * Get the remaining amount of gold.
     * 
     * @return The remaining amount of gold.
     */
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
