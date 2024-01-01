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
package com.b3dgs.lionengine.game.feature.actionable;

import java.util.concurrent.atomic.AtomicBoolean;

import com.b3dgs.lionengine.game.Action;
import com.b3dgs.lionengine.game.feature.FeaturableModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;

/**
 * Object containing action.
 */
final class ObjectAction extends FeaturableModel implements Action
{
    /** Action executed flag. */
    private final AtomicBoolean executed;

    /**
     * Constructor.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     * @param executed The executed flag.
     */
    public ObjectAction(Services services, Setup setup, AtomicBoolean executed)
    {
        super(services, setup);

        this.executed = executed;
    }

    @Override
    public void execute()
    {
        executed.set(true);
    }
}
