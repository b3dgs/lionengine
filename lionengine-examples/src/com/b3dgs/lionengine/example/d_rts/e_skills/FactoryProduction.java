package com.b3dgs.lionengine.example.d_rts.e_skills;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.example.d_rts.e_skills.entity.FactoryEntity;
import com.b3dgs.lionengine.game.SetupGame;
import com.b3dgs.lionengine.game.purview.Configurable;
import com.b3dgs.lionengine.game.rts.ability.producer.FactoryProductionRts;

/**
 * The production factory.
 */
public final class FactoryProduction
        extends FactoryProductionRts<TypeEntity, ProductionCost, ProducibleEntity>
{
    /**
     * Constructor.
     */
    FactoryProduction()
    {
        super(TypeEntity.class);
        loadAll(TypeEntity.values());
    }

    /*
     * FactoryProductionRts
     */

    @Override
    public ProducibleEntity createProducible(TypeEntity id)
    {
        final Configurable config = getConfig(id);
        final int step = config.getDataInteger("steps", "cost");
        final int gold = config.getDataInteger("gold", "cost");
        final int wood = config.getDataInteger("wood", "cost");
        final int width = config.getDataInteger("widthInTile", "size");
        final int height = config.getDataInteger("heightInTile", "size");

        final ProductionCost cost = new ProductionCost(step, gold, wood);
        final ProducibleEntity producible = new ProducibleEntity(id, cost, height, width);

        return producible;
    }

    @Override
    public ProducibleEntity createProducible(TypeEntity id, int tx, int ty)
    {
        final ProducibleEntity producible = createProducible(id);

        producible.setLocation(tx, ty);

        return producible;
    }

    @Override
    protected SetupGame createSetup(TypeEntity id)
    {
        return new SetupGame(Media.get(FactoryEntity.ENTITY_PATH, id + ".xml"));
    }
}
