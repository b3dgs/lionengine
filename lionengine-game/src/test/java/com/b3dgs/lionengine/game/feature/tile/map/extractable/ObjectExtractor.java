/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.tile.map.extractable;

import com.b3dgs.lionengine.game.FeaturableModel;
import com.b3dgs.lionengine.game.feature.IdentifiableModel;

/**
 * Extractor test.
 */
class ObjectExtractor extends FeaturableModel implements ExtractorChecker
{
    /** Extract flag. */
    private final boolean extract;
    /** Carry flag. */
    private final boolean carry;

    /**
     * Constructor.
     * 
     * @param extract Extract.
     * @param carry Carry.
     */
    public ObjectExtractor(boolean extract, boolean carry)
    {
        super();
        this.extract = extract;
        this.carry = carry;
        addFeature(new IdentifiableModel());
    }

    @Override
    public boolean canExtract()
    {
        return extract;
    }

    @Override
    public boolean canCarry()
    {
        return carry;
    }
}
