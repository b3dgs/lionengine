package com.b3dgs.lionengine.game.rts;

/**
 * This class represents the player, with its data, using a unique ID. The ID will be assigned to any entity owned by
 * the player. Then it is possible to know which entity is owned by a player.
 */
public abstract class PlayerRts
{
    /** Last player id. */
    private static int lastId = 0;

    /**
     * Get the next ID.
     * 
     * @return The next ID.
     */
    private static int getNextId()
    {
        PlayerRts.lastId++;
        return PlayerRts.lastId;
    }

    /** Player id. */
    public final int id;
    /** Player name. */
    private String name;

    /**
     * Create a new player.
     */
    public PlayerRts()
    {
        name = null;
        id = PlayerRts.getNextId();
    }

    /**
     * Set player name.
     * 
     * @param name The player name.
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Get player name.
     * 
     * @return The player name.
     */
    public String getName()
    {
        return name;
    }
}
