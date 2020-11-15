/*
 * Copyright (C) 2013-2020 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.b3dgs.lionengine.game.background;

import java.util.ArrayList;
import java.util.List;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.Sprite;

/**
 * Background skeleton, used for future backgrounds implementation.
 */
public abstract class BackgroundAbstract implements Background
{
    /**
     * Create an element from a name, plus its coordinates.
     * 
     * @param name The element name.
     * @param x The location x.
     * @param y The location y.
     * @return The created element.
     * @throws LionEngineException If media is <code>null</code> or image cannot be read.
     */
    public static BackgroundElement createElement(String name, int x, int y)
    {
        return new BackgroundElement(x, y, createSprite(Medias.create(name)));
    }

    /**
     * Create an element from a path and its name, plus its coordinates.
     * 
     * @param path The element path.
     * @param name The element name.
     * @param x The location x.
     * @param y The location y.
     * @return The created element.
     * @throws LionEngineException If media is <code>null</code> or image cannot be read.
     */
    public static BackgroundElement createElement(String path, String name, int x, int y)
    {
        return new BackgroundElement(x, y, createSprite(Medias.create(path, name)));
    }

    /**
     * Create a sprite from its filename.
     * 
     * @param media The sprite media.
     * @return The sprite instance.
     * @throws LionEngineException If media is <code>null</code> or image cannot be read.
     */
    protected static Sprite createSprite(Media media)
    {
        final Sprite sprite = Drawable.loadSprite(media);
        sprite.load();
        sprite.prepare();
        return sprite;
    }

    /** List of components contained by this background. */
    protected final List<BackgroundComponent> components = new ArrayList<>(1);
    /** Minimum background value. */
    protected final int maxY;
    /** Maximum background value. */
    protected final int minY;
    /** Background theme name. */
    protected final String theme;
    /** Total background height. */
    protected int totalHeight;
    /** Offset y. */
    private int offsetY;

    /**
     * Constructor base.
     * 
     * @param theme The background theme.
     * @param min The minimal y value for background.
     * @param max The maximal y value for background.
     */
    protected BackgroundAbstract(String theme, int min, int max)
    {
        super();

        this.theme = theme;
        maxY = max;
        minY = min;
    }

    /**
     * Get the y offset.
     * 
     * @return The y offset.
     */
    public int getOffsetY()
    {
        return offsetY;
    }

    /**
     * Get the background theme.
     * 
     * @return The background theme.
     */
    public String getTheme()
    {
        return theme;
    }

    /**
     * Get the components number.
     * 
     * @return Components number.
     */
    public int getComponentsNumber()
    {
        return components.size();
    }

    /**
     * Add a component to the background.
     * 
     * @param component The component reference.
     */
    protected void add(BackgroundComponent component)
    {
        components.add(component);
    }

    /**
     * Render the specified component.
     * 
     * @param index The component index.
     * @param g The graphic output.
     */
    protected void renderComponent(int index, Graphic g)
    {
        components.get(index).render(g);
    }

    /**
     * Set the y offset.
     * 
     * @param offsetY The y offset.
     */
    protected void setOffsetY(int offsetY)
    {
        this.offsetY = offsetY;
    }

    /*
     * Background
     */

    @Override
    public final void update(double extrp, double speed, double y)
    {
        final int lowest = totalHeight;
        int py;
        if (maxY == 0)
        {
            py = (int) y;
        }
        else
        {
            final double currentY = UtilMath.clamp(y, minY, maxY);
            py = (int) (currentY / maxY * lowest) - lowest + offsetY;
            if (py > 0)
            {
                py = 0;
            }
        }

        for (final BackgroundComponent component : components)
        {
            component.update(extrp, 0, py, speed);
        }
    }

    @Override
    public final void render(Graphic g)
    {
        for (final BackgroundComponent component : components)
        {
            component.render(g);
        }
    }
}
