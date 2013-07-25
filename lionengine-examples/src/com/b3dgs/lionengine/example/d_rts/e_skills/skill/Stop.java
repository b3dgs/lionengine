package com.b3dgs.lionengine.example.d_rts.e_skills.skill;

import com.b3dgs.lionengine.example.d_rts.e_skills.TypeSkill;
import com.b3dgs.lionengine.game.rts.ControlPanelModel;
import com.b3dgs.lionengine.game.rts.CursorRts;

/**
 * Stop skill implementation.
 */
final class Stop
        extends Skill
{
    /**
     * Constructor.
     * 
     * @param setup The setup skill reference.
     */
    Stop(SetupSkill setup)
    {
        super(TypeSkill.stop_orc, setup);
    }

    @Override
    public void action(ControlPanelModel<?> panel, CursorRts cursor)
    {
        owner.stop();
    }
}
