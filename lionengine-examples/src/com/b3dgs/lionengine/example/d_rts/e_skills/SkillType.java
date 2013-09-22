package com.b3dgs.lionengine.example.d_rts.e_skills;

import java.util.Locale;

import com.b3dgs.lionengine.example.d_rts.f_warcraft.type.TypeRace;

/**
 * List of skill types.
 */
public enum SkillType
{
    /*
     * Human skills
     */

    /** Move human skill. */
    MOVE_HUMAN(TypeRace.HUMAN),
    /** Build human skill. */
    BUILDING_STANDARD_HUMAN(TypeRace.HUMAN),
    /** Attack melee human skill. */
    ATTACK_SWORD(TypeRace.HUMAN),
    /** Attack bow human skill. */
    ATTACK_BOW(TypeRace.HUMAN),
    /** Cancel human skill. */
    CANCEL_HUMAN(TypeRace.HUMAN),
    /** Stop human skill. */
    STOP_HUMAN(TypeRace.HUMAN),
    /** Build barracks human skill. */
    BUILD_BARRACKS_HUMAN(TypeRace.HUMAN),
    /** Produce peasant skill. */
    PRODUCE_PEASANT(TypeRace.HUMAN),
    /** Produce footman skill. */
    PRODUCE_FOOTMAN(TypeRace.HUMAN),
    /** Produce archer skill. */
    PRODUCE_ARCHER(TypeRace.HUMAN),

    /*
     * Orc skills
     */

    /** Move orc skill. */
    MOVE_ORC(TypeRace.ORC),
    /** Build orc skill. */
    BUILDING_STANDARD_ORC(TypeRace.ORC),
    /** Attack melee orc skill. */
    ATTACK_AXE(TypeRace.ORC),
    /** Attack spear orc skill. */
    ATTACK_SPEAR(TypeRace.ORC),
    /** Stop orc skill. */
    STOP_ORC(TypeRace.ORC),
    /** Cancel orc skill. */
    CANCEL_ORC(TypeRace.ORC),
    /** Build barracks orc skill. */
    BUILD_BARRACKS_ORC(TypeRace.ORC),
    /** Produce peon skill. */
    PRODUCE_PEON(TypeRace.ORC),
    /** Produce grunt skill. */
    PRODUCE_GRUNT(TypeRace.ORC),
    /** Produce spearman skill. */
    PRODUCE_SPEARMAN(TypeRace.ORC);

    /** The race. */
    public final TypeRace race;

    /**
     * Create a new type entity.
     * 
     * @param race The entity race.
     */
    private SkillType(TypeRace race)
    {
        this.race = race;
    }

    @Override
    public String toString()
    {
        return name().toLowerCase(Locale.ENGLISH);
    }
}
