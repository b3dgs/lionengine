/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.example.game.strategy.ability.entity;

import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.example.game.strategy.ability.ResourceType;
import com.b3dgs.lionengine.game.Alterable;
import com.b3dgs.lionengine.game.SetupSurfaceGame;
import com.b3dgs.lionengine.game.strategy.ability.extractor.Extractible;

/**
 * Gold mine building implementation. This building allows to extract gold with a worker.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class GoldMine
        extends Building
        implements Extractible
{
    /** Class media. */
    public static final Media MEDIA = Entity.getConfig(GoldMine.class);

    /** Gold amount. */
    private final Alterable gold;
    /** Resource type. */
    private final ResourceType typeResource;

    /**
     * Constructor.
     * 
     * @param setup The setup reference.
     */
    public GoldMine(SetupSurfaceGame setup)
    {
        super(setup);
        typeResource = ResourceType.GOLD;
        gold = new Alterable(50000);
        setFrame(1);
    }

    /*
     * Building
     */

    @Override
    public void stop()
    {
        // Nothing to do
    }

    /*
     * Extractible
     */

    @Override
    public int extractResource(int amount)
    {
        return gold.decrease(amount);
    }

    @Override
    public int getResourceQuantity()
    {
        return gold.getCurrent();
    }

    @Override
    public ResourceType getResourceType()
    {
        return typeResource;
    }
}
