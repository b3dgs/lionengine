/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.example.prototype.gameplay;

import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Mirrorable;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;

/**
 * Zombi implementation.
 */
@FeatureInterface
public final class Zombi extends FeatureModel implements Recyclable
{
    private final Mirrorable mirrorable;

    /**
     * Create zombi.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     * @param mirrorable The mirrorable feature.
     */
    public Zombi(Services services, Setup setup, Mirrorable mirrorable)
    {
        super(services, setup);

        this.mirrorable = mirrorable;
    }

    @Override
    public void recycle()
    {
        mirrorable.mirror(Mirror.HORIZONTAL);
    }
}
