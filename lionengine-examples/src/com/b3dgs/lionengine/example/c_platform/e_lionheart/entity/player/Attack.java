package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.player;

import com.b3dgs.lionengine.file.XmlNode;
import com.b3dgs.lionengine.game.CollisionData;

/**
 * Attacks data.
 */
public class Attack
{
    /** The frame index. */
    public final int frame;
    /** The collision data. */
    public final CollisionData collision;

    /**
     * Constructor.
     * 
     * @param attack The attack node.
     */
    public Attack(XmlNode attack)
    {
        frame = attack.readInteger("frame");
        collision = new CollisionData(attack.readInteger("x"), attack.readInteger("y"), attack.readInteger("width"),
                attack.readInteger("height"));
    }
}
