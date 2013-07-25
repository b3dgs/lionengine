package com.b3dgs.lionengine.example.d_rts.f_warcraft.skill.orc;

import com.b3dgs.lionengine.example.d_rts.f_warcraft.HandlerEntity;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.skill.Attack;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.skill.SetupSkill;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.type.TypeSkill;

/**
 * Sword attack skill.
 */
final class AttackSpear
        extends Attack
{
    /**
     * Constructor.
     * 
     * @param setup The setup skill reference.
     * @param handler The handler reference.
     */
    AttackSpear(SetupSkill setup, HandlerEntity handler)
    {
        super(TypeSkill.attack_spear, setup, handler);
    }
}
