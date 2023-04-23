/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.it.feature.tile.map.collision;

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.Version;
import com.b3dgs.lionengine.awt.graphic.EngineAwt;
import com.b3dgs.lionengine.graphic.engine.Loader;

/**
 * Integration test of map tile collision model class.
 */
final class MapTileCollisionModelIT
{
    /**
     * Test the map tile collision.
     */
    @Test
    void testModel()
    {
        EngineAwt.start(getClass().getSimpleName(), Version.create(1, 0, 0), getClass());
        final Resolution output = new Resolution(640, 480, 60);
        Loader.start(Config.windowed(output), Scene.class).await();
    }
}
