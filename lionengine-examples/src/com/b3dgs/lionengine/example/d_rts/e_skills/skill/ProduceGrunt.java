package com.b3dgs.lionengine.example.d_rts.e_skills.skill;

import com.b3dgs.lionengine.example.d_rts.e_skills.EntityType;
import com.b3dgs.lionengine.example.d_rts.e_skills.FactoryProduction;
import com.b3dgs.lionengine.example.d_rts.e_skills.ProducibleEntity;
import com.b3dgs.lionengine.example.d_rts.e_skills.SkillType;
import com.b3dgs.lionengine.example.d_rts.e_skills.entity.UnitWorker;
import com.b3dgs.lionengine.game.rts.ControlPanelModel;
import com.b3dgs.lionengine.game.rts.CursorRts;

/**
 * Produce grunt implementation.
 */
final class ProduceGrunt
        extends Skill
{
    /** Production factory. */
    private final FactoryProduction factoryProduction;

    /**
     * Constructor.
     * 
     * @param setup The setup skill reference.
     */
    ProduceGrunt(SetupSkill setup)
    {
        super(SkillType.PRODUCE_GRUNT, setup);
        factoryProduction = setup.factoryProduction;
        setOrder(false);
    }

    /*
     * Skill
     */

    @Override
    public void action(ControlPanelModel<?> panel, CursorRts cursor)
    {
        if (owner instanceof UnitWorker)
        {
            final ProducibleEntity producible = factoryProduction.createProducible(EntityType.GRUNT);
            ((UnitWorker) owner).addToProductionQueue(producible);
        }
    }
}
