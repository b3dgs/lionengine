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
package com.b3dgs.lionengine.game.feature.tile.map.extractable;

import com.b3dgs.lionengine.game.feature.FeaturableModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;

/**
 * Extractor test.
 */
final class ObjectExtractor extends FeaturableModel implements ExtractorChecker
{
    /** Extract flag. */
    private final boolean extract;
    /** Carry flag. */
    private final boolean carry;

    /**
     * Constructor.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     * @param extract Extract.
     * @param carry Carry.
     */
    public ObjectExtractor(Services services, Setup setup, boolean extract, boolean carry)
    {
        super(services, setup);

        this.extract = extract;
        this.carry = carry;
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
