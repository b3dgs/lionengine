/*
 * Copyright (C) 2013 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.player;

import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.Entity;
import com.b3dgs.lionengine.game.Alterable;

/**
 * Player stats.
 */
public final class Stats
{
    /** Entity owner. */
    private final Entity entity;
    /** Life number. */
    private final Alterable life;
    /** Heart number. */
    private final Alterable heart;
    /** Talisment. */
    private final Alterable talisment;
    /** Sword level. */
    private final Alterable sword;
    /** Amulet. */
    private boolean amulet;

    /**
     * Constructor.
     * 
     * @param entity The entity owner.
     */
    public Stats(Entity entity)
    {
        this.entity = entity;
        life = new Alterable(99);
        heart = new Alterable(4);
        talisment = new Alterable(100);
        sword = new Alterable(4);
        life.increase(4);
        heart.fill();
        sword.set(1);
    }

    /**
     * Increase the life number.
     */
    public void increaseLife()
    {
        life.increase(1);
    }

    /**
     * Decrease the life number. Kill the entity if life is equal to 0.
     */
    public void decreaseLife()
    {
        life.decrease(1);
    }

    /**
     * Increase the number of hearts.
     */
    public void increaseHeart()
    {
        heart.increase(1);
    }

    /**
     * Decrease the number of hearts.
     */
    public void decreaseHeart()
    {
        heart.decrease(1);
        if (heart.isEmpty())
        {
            entity.kill();
        }
    }

    /**
     * Replenish hearts.
     */
    public void fillHeart()
    {
        heart.fill();
    }

    /**
     * Increase the talisment number.
     */
    public void increaseTalisment()
    {
        talisment.increase(1);
        if (talisment.isFull())
        {
            if (heart.getMax() < 8)
            {
                heart.setMax(heart.getMax() + 1);
            }
            talisment.reset();
        }
    }

    /**
     * Increase the sword level if needed.
     * 
     * @param toLevel The level destination.
     */
    public void setSwordLevel(int toLevel)
    {
        sword.set(toLevel);
    }

    /**
     * Found the amulet.
     */
    public void foundAmulet()
    {
        amulet = true;
    }

    /**
     * Get the current life number.
     * 
     * @return The life number.
     */
    public int getLife()
    {
        return life.getCurrent();
    }

    /**
     * Get the current heart number.
     * 
     * @return The hurt number.
     */
    public int getHeart()
    {
        return heart.getCurrent();
    }

    /**
     * Get the maximum number of heart.
     * 
     * @return The maximum number of heart.
     */
    public int getHeartMax()
    {
        return heart.getMax();
    }

    /**
     * Get the current number of talisment.
     * 
     * @return The current number of talisment.
     */
    public int getTalisment()
    {
        return talisment.getCurrent();
    }

    /**
     * Get the current sword level.
     * 
     * @return The current sword level.
     */
    public int getSwordLevel()
    {
        return sword.getCurrent();
    }

    /**
     * Get the amulet found state.
     * 
     * @return <code>true</code> if found, <code>false</code> else.
     */
    public boolean getAmulet()
    {
        return amulet;
    }
}
