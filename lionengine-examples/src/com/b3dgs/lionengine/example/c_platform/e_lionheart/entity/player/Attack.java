package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.player;

import com.b3dgs.lionengine.file.XmlNode;
import com.b3dgs.lionengine.game.CollisionData;

/**
 * Attacks data.
 */
class Attack
{
    /** The frame index. */
    private final int frame;
    /** The collision data. */
    private final CollisionData collision;

    /**
     * Constructor.
     * 
     * @param attack The attack node.
     */
    Attack(XmlNode attack)
    {
        frame = attack.readInteger("frame");
        collision = new CollisionData(attack.readInteger("x"), attack.readInteger("y"), attack.readInteger("width"),
                attack.readInteger("height"), true);
    }

    /**
     * Get the frame that contain a collision.
     * 
     * @return The frame containing a collision.
     */
    public int getFrame()
    {
        return frame;
    }

    /**
     * Get the collision data.
     * 
     * @return The collision data.
     */
    public CollisionData getCollision()
    {
        return collision;
    }
}
