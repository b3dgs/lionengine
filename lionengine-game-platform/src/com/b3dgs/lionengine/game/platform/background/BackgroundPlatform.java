package com.b3dgs.lionengine.game.platform.background;

import java.util.ArrayList;
import java.util.List;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.Sprite;
import com.b3dgs.lionengine.utility.UtilityMath;

/**
 * AbstractBackground skeleton, used for future backgrounds implementation.
 */
public abstract class BackgroundPlatform
        implements Background
{
    /** List of components contained by this background. */
    protected final List<BackgroundComponent> components;
    /** Minimum background value. */
    protected final int maxY;
    /** Maximum background value. */
    protected final int minY;
    /** Wide state. */
    protected final boolean wide;
    /** Total background height. */
    protected int totalHeight;
    /** Number of background components. */
    protected int componentsNumber;
    /** Background theme name. */
    protected final String theme;

    /**
     * Create a new background.
     * 
     * @param theme The background theme.
     * @param min The minimal y value for background.
     * @param max The maximal y value for background.
     * @param wide The wide state.
     */
    public BackgroundPlatform(String theme, int min, int max, boolean wide)
    {
        this.theme = theme;
        this.wide = wide;
        components = new ArrayList<>(1);
        maxY = max;
        minY = min;
    }

    /**
     * Add a component to the background.
     * 
     * @param component The component reference.
     */
    protected void add(BackgroundComponent component)
    {
        components.add(component);
        componentsNumber = components.size();
    }

    /**
     * Create a sprite from its filename.
     * 
     * @param media The sprite media.
     * @param alpha The alpha use flag.
     * @return The sprite instance.
     */
    protected Sprite createSprite(Media media, boolean alpha)
    {
        final Sprite sprite = Drawable.loadSprite(media);
        sprite.load(alpha);
        return sprite;
    }

    /**
     * Create an element from a path and its name, plus its coordinates.
     * 
     * @param path The element path.
     * @param name The element name.
     * @param x The location x.
     * @param y The location y.
     * @param alpha The alpha use flag.
     * @return The created element.
     */
    public BackgroundElement createElement(String path, String name, int x, int y, boolean alpha)
    {
        return new BackgroundElement(x, y, createSprite(Media.get(path, name), alpha));
    }

    /*
     * Background
     */

    @Override
    public final void update(double speed, double y, double extrp)
    {
        final int lowest = totalHeight;
        final double currentY = UtilityMath.fixBetween(y, minY, maxY);
        final int py = (int) (currentY / maxY * lowest) - lowest;

        for (int i = 0; i < componentsNumber; i++)
        {
            components.get(i).update(0, py, speed, extrp);
        }
    }

    @Override
    public final void render(Graphic g)
    {
        for (int i = 0; i < componentsNumber; i++)
        {
            components.get(i).render(g);
        }
    }
}
