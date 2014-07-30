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
package com.b3dgs.lionengine.example.game.strategy.skills.skill;

import com.b3dgs.lionengine.example.game.strategy.skills.Cursor;
import com.b3dgs.lionengine.example.game.strategy.skills.entity.FactoryProduction;
import com.b3dgs.lionengine.game.ContextGame;

/**
 * Represents the context related to skills.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class ContextSkill
        implements ContextGame
{
    /** Production factory. */
    final FactoryProduction factoryProduction;
    /** Cursor. */
    final Cursor cursor;

    /**
     * Constructor.
     * 
     * @param factoryProduction The production factory.
     * @param cursor The cursor reference.
     */
    public ContextSkill(FactoryProduction factoryProduction, Cursor cursor)
    {
        this.factoryProduction = factoryProduction;
        this.cursor = cursor;
    }
}
