package com.b3dgs.lionengine.example.d_rts.f_warcraft.skill;

import com.b3dgs.lionengine.example.d_rts.f_warcraft.FactoryProduction;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.ProducibleEntity;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.entity.BuildingProducer;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.type.TypeEntity;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.type.TypeSkill;
import com.b3dgs.lionengine.game.purview.Configurable;
import com.b3dgs.lionengine.game.rts.ControlPanelModel;
import com.b3dgs.lionengine.game.rts.CursorRts;

/**
 * Skill build implementation.
 */
public class SkillProduceEntity
        extends Skill
{
    /** Production factory. */
    protected final FactoryProduction factoryProduction;
    /** Entity type to produce. */
    private final TypeEntity entity;
    /** The production cost gold. */
    private final int gold;
    /** The production cost wood. */
    private final int wood;

    /**
     * Constructor.
     * 
     * @param id The skill id.
     * @param setup The setup skill reference.
     * @param entity The entity type to produce.
     */
    protected SkillProduceEntity(TypeSkill id, SetupSkill setup, TypeEntity entity)
    {
        super(id, setup);
        this.entity = entity;
        factoryProduction = setup.factoryProduction;
        final Configurable config = factoryProduction.getConfig(TypeEntity.barracks_orc);
        gold = config.getDataInteger("gold", "cost");
        wood = config.getDataInteger("wood", "cost");
        setOrder(false);
    }

    @Override
    public void action(ControlPanelModel<?> panel, CursorRts cursor)
    {
        if (owner instanceof BuildingProducer)
        {
            final ProducibleEntity producible = factoryProduction.createProducible(entity, destX, destY);
            ((BuildingProducer) owner).addToProductionQueue(producible);
        }
    }

    @Override
    public String getDescription()
    {
        final StringBuilder description = new StringBuilder(super.getDescription());
        description.append(":     ").append(gold).append("      ").append(wood);
        return description.toString();
    }
}
