package com.b3dgs.lionengine.example.d_rts.e_skills.skill;

import com.b3dgs.lionengine.example.d_rts.e_skills.TypeSkill;
import com.b3dgs.lionengine.game.rts.ControlPanelModel;
import com.b3dgs.lionengine.game.rts.CursorRts;

/**
 * Cancel skill implementation.
 */
final class Cancel
        extends Skill
{
    /**
     * Constructor.
     * 
     * @param setup The setup skill reference.
     */
    Cancel(SetupSkill setup)
    {
        super(TypeSkill.cancel_orc, setup);
    }

    /*
     * Skill
     */

    @Override
    public void action(ControlPanelModel<?> panel, CursorRts cursor)
    {
        owner.setSkillPanel(0);
    }
}
