package com.b3dgs.lionengine.example.c_platform.e_lionheart.landscape;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.game.platform.CameraPlatform;
import com.b3dgs.lionengine.game.platform.background.BackgroundPlatform;

/**
 * Represents a landscape by containing a background and a foreground.
 */
public class Landscape
{
    /** Landscape type. */
    private final LandscapeType type;
    /** Background element. */
    private final BackgroundPlatform background;
    /** Foreground element. */
    private final Foreground foreground;

    /**
     * Constructor.
     * 
     * @param type The landscape type.
     * @param background The background element.
     * @param foreground The foreground element.
     */
    public Landscape(LandscapeType type, BackgroundPlatform background, Foreground foreground)
    {
        this.type = type;
        this.background = background;
        this.foreground = foreground;
    }

    /**
     * Update the landscape.
     * 
     * @param extrp The extrapolation value.
     * @param camera The camera reference.
     */
    public void update(double extrp, CameraPlatform camera)
    {
        background.update(extrp, camera.getMovementHorizontal(), camera.getLocationY());
        foreground.update(extrp, camera.getMovementHorizontal(), camera.getLocationY());
    }

    /**
     * Render the background.
     * 
     * @param g The graphic output.
     */
    public void renderBackground(Graphic g)
    {
        background.render(g);
        foreground.renderBack(g);
    }

    /**
     * Render the foreground.
     * 
     * @param g The graphic output.
     */
    public void renderForeground(Graphic g)
    {
        foreground.renderFront(g);
    }

    /**
     * Get the current water height.
     * 
     * @return The current water height.
     */
    public double getWaterHeight()
    {
        return foreground.getTop();
    }

    /**
     * Get the landscape type.
     * 
     * @return The landscape type.
     */
    public LandscapeType getType()
    {
        return type;
    }
}
