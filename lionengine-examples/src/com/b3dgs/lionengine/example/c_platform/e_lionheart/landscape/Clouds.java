package com.b3dgs.lionengine.example.c_platform.e_lionheart.landscape;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.game.platform.background.CloudsPlatform;

/**
 * Clouds implementation.
 */
final class Clouds
        extends CloudsPlatform
{
    /**
     * Constructor.
     * 
     * @param path The resources path.
     * @param screenWidth The screen width.
     * @param decY The vertical offset.
     */
    Clouds(Media path, int screenWidth, int decY)
    {
        super(path, 160, 26, screenWidth, decY, false);

        setY(0, 0);
        setY(1, 30);
        setY(2, 54);
        setY(3, 73);
        setY(4, 89);
        setY(5, 100);

        setSpeed(0, -1.35);
        setSpeed(1, -1.2);
        setSpeed(2, -0.9);
        setSpeed(3, -0.7);
        setSpeed(4, -0.5);
        setSpeed(5, -0.4);
    }
}
