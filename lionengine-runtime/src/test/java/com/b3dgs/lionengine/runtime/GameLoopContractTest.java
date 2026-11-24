/*
 * Copyright (C) 2013-2026 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.runtime;

import com.b3dgs.lionengine.Log;
import com.b3dgs.lionengine.LoopDriver;
import com.b3dgs.lionengine.LoopDriverContract;

/**
 * Test {@link GameLoop} against the {@link LoopDriver} contract of the {@code core} test JAR (REQ-LOOP-001,
 * REQ-LOOP-002, REQ-LOOP-010).
 */
final class GameLoopContractTest extends LoopDriverContract
{
    @Override
    protected LoopDriver createDriver()
    {
        return new GameLoop(LoopConfig.DEFAULT, new ControllableClock(), new Log(), step ->
        {
            // No simulation.
        }, (alpha, delta) ->
        {
            // No presentation.
        });
    }
}
