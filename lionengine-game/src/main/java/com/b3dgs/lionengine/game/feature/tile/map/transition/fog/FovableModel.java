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
package com.b3dgs.lionengine.game.feature.tile.map.transition.fog;

import java.util.function.BooleanSupplier;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;

/**
 * Fovable model implementation.
 */
public class FovableModel extends FeatureModel implements Fovable
{
    /** Map tile reference. */
    private final MapTile map = services.get(MapTile.class);
    /** Field of view in tile value. */
    private int fov = FovableConfig.imports(setup);
    /** Checker update. */
    private BooleanSupplier checker = () -> true;

    /** Transformable model. */
    private final Transformable transformable;

    /**
     * Create model.
     * <p>
     * The {@link Services} must provide:
     * </p>
     * <ul>
     * <li>{@link MapTile}</li>
     * </ul>
     * <p>
     * The {@link Featurable} must have:
     * </p>
     * <ul>
     * <li>{@link Transformable}</li>
     * </ul>
     * <p>
     * The {@link Configurer} can provide a valid {@link FovableConfig}.
     * </p>
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @param transformable The transformable feature.
     * @throws LionEngineException If invalid arguments.
     */
    public FovableModel(Services services, Setup setup, Transformable transformable)
    {
        super(services, setup);

        this.transformable = transformable;
    }

    /*
     * Fovable
     */

    @Override
    public void setFov(int fov)
    {
        this.fov = fov;
    }

    @Override
    public void setCanUpdate(BooleanSupplier checker)
    {
        Check.notNull(checker);

        this.checker = checker;
    }

    @Override
    public boolean canUpdate()
    {
        return checker.getAsBoolean();
    }

    @Override
    public int getInTileX()
    {
        return map.getInTileX(transformable);
    }

    @Override
    public int getInTileY()
    {
        return map.getInTileY(transformable);
    }

    @Override
    public int getInTileWidth()
    {
        return transformable.getWidth() / map.getTileWidth();
    }

    @Override
    public int getInTileHeight()
    {
        return transformable.getHeight() / map.getTileHeight();
    }

    @Override
    public int getInTileFov()
    {
        return fov;
    }
}
