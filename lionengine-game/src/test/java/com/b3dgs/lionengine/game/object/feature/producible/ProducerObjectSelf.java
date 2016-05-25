/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.object.feature.producible;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.FeaturableModel;

/**
 * Object producer self listener test.
 */
class ProducerObjectSelf extends FeaturableModel implements ProducerChecker, ProducerListener
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
    public boolean checkProduction(Producible producible)
    {
        return check.get();
    }

    @Override
    public void notifyStartProduction(Producible producible, Featurable featurable)
    {
        flag.set(1);
    }

    @Override
    public void notifyProducing(Producible producible, Featurable featurable)
    {
        flag.set(2);
    }

    @Override
    public void notifyProduced(Producible producible, Featurable featurable)
    {
        flag.set(3);
    }

    @Override
    public void notifyCanNotProduce(Producible producible)
    {
        flag.set(4);
    }
}
