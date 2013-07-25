package com.b3dgs.lionengine.example.d_rts.f_warcraft.skill;

import com.b3dgs.lionengine.example.d_rts.f_warcraft.type.TypeSkill;
import com.b3dgs.lionengine.game.rts.ControlPanelModel;
import com.b3dgs.lionengine.game.rts.CursorRts;
import com.b3dgs.lionengine.game.rts.ability.mover.MoverServices;

/**
 * Move skill implementation.
 */
public final class Move
        extends Skill
{
    /**
     * Constructor.
     * 
     * @param setup The setup skill reference.
     */
    public Move(SetupSkill setup)
    {
        super(TypeSkill.move_orc, setup);
        setOrder(true);
    }

    @Override
    public void action(ControlPanelModel<?> panel, CursorRts cursor)
    {
        if (owner instanceof MoverServices)
        {
            ((MoverServices) owner).setDestination(destX, destY);
        }
    }
}
