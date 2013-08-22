package com.b3dgs.lionengine.example.d_rts.f_warcraft.type;

/**
 * List of skill types.
 */
public enum TypeSkill
{
    /*
     * Human skills
     */

    /** Move human skill. */
    move_human(TypeRace.HUMAN),
    /** Build human skill. */
    building_standard_human(TypeRace.HUMAN),
    /** Attack melee human skill. */
    attack_sword(TypeRace.HUMAN),
    /** Attack bow human skill. */
    attack_bow(TypeRace.HUMAN),
    /** Cancel human skill. */
    cancel_human(TypeRace.HUMAN),
    /** Stop human skill. */
    stop_human(TypeRace.HUMAN),
    /** Build barracks human skill. */
    build_barracks_human(TypeRace.HUMAN),
    /** Build farm human skill. */
    build_farm_human(TypeRace.HUMAN),
    /** Produce peasant skill. */
    produce_peasant(TypeRace.HUMAN),
    /** Produce footman skill. */
    produce_footman(TypeRace.HUMAN),
    /** Produce archer skill. */
    produce_archer(TypeRace.HUMAN),

    /*
     * Orc skills
     */

    /** Move orc skill. */
    move_orc(TypeRace.ORC),
    /** Build orc skill. */
    building_standard_orc(TypeRace.ORC),
    /** Attack melee orc skill. */
    attack_axe(TypeRace.ORC),
    /** Attack spear orc skill. */
    attack_spear(TypeRace.ORC),
    /** Stop orc skill. */
    stop_orc(TypeRace.ORC),
    /** Cancel orc skill. */
    cancel_orc(TypeRace.ORC),
    /** Build barracks orc skill. */
    build_barracks_orc(TypeRace.ORC),
    /** Build farm orc skill. */
    build_farm_orc(TypeRace.ORC),
    /** Produce peon skill. */
    produce_peon(TypeRace.ORC),
    /** Produce grunt skill. */
    produce_grunt(TypeRace.ORC),
    /** Produce spearman skill. */
    produce_spearman(TypeRace.ORC);

    /** The race. */
    public final TypeRace race;

    /**
     * Create a new type entity.
     * 
     * @param race The entity race.
     */
    private TypeSkill(TypeRace race)
    {
        this.race = race;
    }
}
