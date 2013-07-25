package com.b3dgs.lionengine.example.c_platform.e_lionheart.background;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.game.platform.background.CloudsPlatform;

/**
 * Clouds implementation.
 */
public final class Clouds
        extends CloudsPlatform
{
    /**
     * Standard constructor.
     * 
     * @param path The resources path.
     * @param wide <code>true</code> for widescreen, <code>false</code> for normal.
     * @param screenWidth The screen width.
     * @param decY The vertical offset.
     */
    public Clouds(Media path, boolean wide, int screenWidth, int decY)
    {
        super(path, 160, 26, wide, screenWidth, decY, false);

        setY(0, 5);
        setY(1, 35);
        setY(2, 59);
        setY(3, 78);
        setY(4, 94);
        setY(5, 105);

        setSpeed(0, -1.35);
        setSpeed(1, -1.2);
        setSpeed(2, -0.9);
        setSpeed(3, -0.7);
        setSpeed(4, -0.5);
        setSpeed(5, -0.4);
    }
}
