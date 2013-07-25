package com.b3dgs.lionengine.example.d_rts.e_skills.skill;

import com.b3dgs.lionengine.example.d_rts.e_skills.HandlerEntity;
import com.b3dgs.lionengine.example.d_rts.e_skills.TypeSkill;
import com.b3dgs.lionengine.example.d_rts.e_skills.entity.UnitAttacker;
import com.b3dgs.lionengine.game.entity.EntityNotFoundException;
import com.b3dgs.lionengine.game.rts.ControlPanelModel;
import com.b3dgs.lionengine.game.rts.CursorRts;
import com.b3dgs.lionengine.game.rts.ability.mover.MoverServices;

/**
 * Attack melee implementation.
 */
final class AttackAxe
        extends Skill
{
    /** Handler reference. */
    private final HandlerEntity handler;

    /**
     * Constructor.
     * 
     * @param setup The setup skill reference.
     * @param handler The handler reference.
     */
    AttackAxe(SetupSkill setup, HandlerEntity handler)
    {
        super(TypeSkill.attack_axe, setup);
        this.handler = handler;
        setOrder(true);
    }

    @Override
    public void action(ControlPanelModel<?> panel, CursorRts cursor)
    {
        try
        {
            if (owner instanceof UnitAttacker)
            {
                ((UnitAttacker) owner).attack(handler.getEntityAt(destX, destY));
            }
        }
        catch (final EntityNotFoundException exception)
        {
            if (owner instanceof MoverServices)
            {
                ((MoverServices) owner).setDestination(destX, destY);
            }
        }
    }
}
