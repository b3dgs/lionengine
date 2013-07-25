package com.b3dgs.lionengine.example.d_rts.f_warcraft.type;

/**
 * List of entity types.
 */
public enum TypeEntity
{
    /*
     * Human units
     */

    /** Peasant unit. */
    peasant(TypeRace.HUMAN),
    /** Footman unit. */
    footman(TypeRace.HUMAN),
    /** Archer unit. */
    archer(TypeRace.HUMAN),
    /** TownHall building. */
    townhall_human(TypeRace.HUMAN),
    /** Farm building. */
    farm_human(TypeRace.HUMAN),
    /** Barracks building. */
    barracks_human(TypeRace.HUMAN),
    /** Lumber mill building. */
    lumbermill_human(TypeRace.HUMAN),

    /*
     * Orc units
     */

    /** Peon unit. */
    peon(TypeRace.ORC),
    /** Grunt unit. */
    grunt(TypeRace.ORC),
    /** Spearman unit. */
    spearman(TypeRace.ORC),
    /** TownHall building. */
    townhall_orc(TypeRace.ORC),
    /** Farm building. */
    farm_orc(TypeRace.ORC),
    /** Barracks building. */
    barracks_orc(TypeRace.ORC),
    /** Lumber mill building. */
    lumbermill_orc(TypeRace.ORC),

    /*
     * Neutral units
     */

    /** Gold mine building. */
    gold_mine(TypeRace.NEUTRAL);

    /** The race. */
    public final TypeRace race;

    /**
     * Create a new type entity.
     * 
     * @param race The entity race.
     */
    private TypeEntity(TypeRace race)
    {
        this.race = race;
    }
}
