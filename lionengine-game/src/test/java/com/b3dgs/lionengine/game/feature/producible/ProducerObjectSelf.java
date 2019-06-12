/*
 * Copyright (C) 2013-2019 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.producible;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.FeaturableModel;

/**
 * Object producer self listener test.
 */
final class ProducerObjectSelf extends FeaturableModel implements ProducerChecker, ProducerListener
{
    /** Flag. */
    final AtomicInteger flag = new AtomicInteger();
    /** Checker. */
    private final AtomicBoolean check = new AtomicBoolean(true);

    /**
     * Constructor.
     */
    public ProducerObjectSelf()
    {
        super();
    }

    @Override
    public boolean checkProduction(Featurable featurable)
    {
        return check.get();
    }

    @Override
    public void notifyStartProduction(Featurable featurable)
    {
        flag.set(1);
    }

    @Override
    public void notifyProducing(Featurable featurable)
    {
        flag.set(2);
    }

    @Override
    public void notifyProduced(Featurable featurable)
    {
        flag.set(3);
    }

    @Override
    public void notifyCanNotProduce(Featurable featurable)
    {
        flag.set(4);
    }
}
