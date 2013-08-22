package com.b3dgs.lionengine.example.d_rts.f_warcraft.skill;

import com.b3dgs.lionengine.example.d_rts.f_warcraft.type.TypeSkill;
import com.b3dgs.lionengine.game.rts.ControlPanelModel;
import com.b3dgs.lionengine.game.rts.CursorRts;

/**
 * Stop skill implementation.
 */
public final class Stop
        extends Skill
{
    /**
     * Constructor.
     * 
     * @param setup The setup skill reference.
     */
    public Stop(SetupSkill setup)
    {
        super(TypeSkill.stop_orc, setup);
    }

    /*
     * Skill
     */

    @Override
    public void action(ControlPanelModel<?> panel, CursorRts cursor)
    {
        owner.stop();
    }
}
