package com.b3dgs.lionengine.example.d_rts.f_warcraft;

import com.b3dgs.lionengine.example.d_rts.f_warcraft.type.TypeRace;

/**
 * Game configuration.
 */
public class GameConfig
{
    /** Player race. */
    final TypeRace player;
    /** Opponent race. */
    final TypeRace opponent;
    /** Game map. */
    final String map;
    /** Fog of war state. */
    final boolean hide;
    /** Fog state. */
    final boolean fog;

    /**
     * Constructor.
     * 
     * @param player The player race.
     * @param opponent The opponent race.
     * @param map The game map.
     * @param hide <code>true</code> to enable fog of war map hiding, <code>false</code> else.
     * @param fog <code>true</code> to enable fog map, <code>false</code> else.
     */
    public GameConfig(TypeRace player, TypeRace opponent, String map, boolean hide, boolean fog)
    {
        this.player = player;
        this.opponent = opponent;
        this.map = map;
        this.hide = hide;
        this.fog = fog;
    }
}
