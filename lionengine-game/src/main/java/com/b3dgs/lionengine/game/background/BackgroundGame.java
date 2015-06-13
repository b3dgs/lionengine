/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.b3dgs.lionengine.game.background;

import java.util.ArrayList;
import java.util.List;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.Sprite;

/**
 * AbstractBackground skeleton, used for future backgrounds implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public abstract class BackgroundGame
        implements Background
{
    /**
     * Create an element from a name, plus its coordinates.
     * 
     * @param name The element name.
     * @param x The location x.
     * @param y The location y.
     * @param alpha The alpha use flag.
     * @return The created element.
     * @throws LionEngineException If media is <code>null</code> or image cannot be read.
     */
    public static BackgroundElement createElement(String name, int x, int y, boolean alpha) throws LionEngineException
    {
        return new BackgroundElement(x, y, createSprite(Medias.create(name), alpha));
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
     * @throws LionEngineException If media is <code>null</code> or image cannot be read.
     */
    public static BackgroundElement createElement(String path, String name, int x, int y, boolean alpha)
            throws LionEngineException
    {
        return new BackgroundElement(x, y, createSprite(Medias.create(path, name), alpha));
    }

    /**
     * Create a sprite from its filename.
     * 
     * @param media The sprite media.
     * @param alpha The alpha use flag.
     * @return The sprite instance.
     * @throws LionEngineException If media is <code>null</code> or image cannot be read.
     */
    protected static Sprite createSprite(Media media, boolean alpha) throws LionEngineException
    {
        final Sprite sprite = Drawable.loadSprite(media);
        sprite.load(alpha);
        return sprite;
    }

    /** List of components contained by this background. */
    protected final List<BackgroundComponent> components;
    /** Minimum background value. */
    protected final int maxY;
    /** Maximum background value. */
    protected final int minY;
    /** Background theme name. */
    protected final String theme;
    /** Total background height. */
    protected int totalHeight;
    /** Number of background components. */
    protected int componentsNumber;
    /** Offset y; */
    private int offsetY;

    /**
     * Constructor base.
     * 
     * @param theme The background theme.
     * @param min The minimal y value for background.
     * @param max The maximal y value for background.
     */
    public BackgroundGame(String theme, int min, int max)
    {
        this.theme = theme;
        components = new ArrayList<>(1);
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
            final double currentY = UtilMath.fixBetween(y, minY, maxY);
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
