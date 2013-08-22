package com.b3dgs.lionengine.example.d_rts.f_warcraft.skill;

import com.b3dgs.lionengine.example.d_rts.f_warcraft.HandlerEntity;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.entity.UnitAttacker;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.type.TypeSkill;
import com.b3dgs.lionengine.game.entity.EntityNotFoundException;
import com.b3dgs.lionengine.game.rts.ControlPanelModel;
import com.b3dgs.lionengine.game.rts.CursorRts;
import com.b3dgs.lionengine.game.rts.ability.mover.MoverServices;

/**
 * Attack melee implementation.
 */
public abstract class Attack
        extends Skill
{
    /** Handler reference. */
    private final HandlerEntity handler;

    /**
     * Constructor.
     * 
     * @param id The skill id.
     * @param setup The setup skill reference.
     * @param handler The handler reference.
     */
    protected Attack(TypeSkill id, SetupSkill setup, HandlerEntity handler)
    {
        super(id, setup);
        this.handler = handler;
        setOrder(true);
    }

    /*
     * Skill
     */

    @Override
    public void action(ControlPanelModel<?> panel, CursorRts cursor)
    {
        try
        {
            if (owner instanceof UnitAttacker)
            {
                ((UnitAttacker) owner).attackAny(handler.getEntityAt(destX, destY));
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
