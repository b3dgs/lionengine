package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.player;

import com.b3dgs.lionengine.file.XmlNode;

/**
 * Attacks data.
 */
public class Attack
{
    /** The frame index. */
    public final int frame;
    /** The x offset. */
    public final int x;
    /** The y offset. */
    public final int y;
    /** The width offset. */
    public final int width;
    /** The height offset. */
    public final int height;

    /**
     * Constructor.
     * 
     * @param attack The attack node.
     */
    public Attack(XmlNode attack)
    {
        this.frame = attack.readInteger("frame");
        this.x = attack.readInteger("x");
        this.y = attack.readInteger("y");
        this.width = attack.readInteger("width");
        this.height = attack.readInteger("height");
    }
}
