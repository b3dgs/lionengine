package com.b3dgs.lionengine.example.d_rts.f_warcraft.skill;

import com.b3dgs.lionengine.example.d_rts.f_warcraft.type.TypeSkill;
import com.b3dgs.lionengine.game.rts.ControlPanelModel;
import com.b3dgs.lionengine.game.rts.CursorRts;

/**
 * Build skill implementation
 */
public abstract class SkillBuild
        extends Skill
{
    /**
     * Constructor.
     * 
     * @param id The skill id.
     * @param setup The setup skill reference.
     */
    protected SkillBuild(TypeSkill id, SetupSkill setup)
    {
        super(id, setup);
    }

    /*
     * Skill
     */

    @Override
    public void action(ControlPanelModel<?> panel, CursorRts cursor)
    {
        owner.setSkillPanel(1);
    }
}
