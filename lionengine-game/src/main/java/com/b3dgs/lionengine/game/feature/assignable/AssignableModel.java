/*
 * Copyright (C) 2013-2022 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.assignable;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.game.Cursor;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;

/**
 * Assignable model implementation.
 */
public class AssignableModel extends FeatureModel implements Assignable
{
    /** Cursor reference. */
    private final Cursor cursor = services.get(Cursor.class);
    /** Viewer reference. */
    private final Viewer viewer = services.get(Viewer.class);
    /** Mouse click number to assign action. */
    private Integer clickAssign;
    /** Assign used. */
    private Assign assign;

    /**
     * Create feature.
     * <p>
     * The {@link Services} must provide the following services:
     * </p>
     * <ul>
     * <li>{@link Cursor}</li>
     * <li>{@link Viewer}</li>
     * </ul>
     * <p>
     * If the {@link Featurable} is an {@link Assign}, it will automatically {@link #setAssign(Assign)} on it.
     * </p>
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public AssignableModel(Services services, Setup setup)
    {
        super(services, setup);
    }

    /*
     * Assignable
     */

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        if (provider instanceof Assign)
        {
            setAssign((Assign) provider);
        }
    }

    @Override
    public void update(double extrp)
    {
        if (assign != null
            && UtilMath.isBetween(cursor.getScreenX(),
                                  viewer.getViewX(),
                                  viewer.getViewX() + (double) viewer.getWidth())
            && UtilMath.isBetween(cursor.getScreenY(),
                                  viewer.getViewY(),
                                  viewer.getViewY() + (double) viewer.getHeight())
            && cursor.isPushedOnce(clickAssign))
        {
            assign.assign();
        }
    }

    @Override
    public void setAssign(Assign assign)
    {
        this.assign = assign;
    }

    @Override
    public void setClickAssign(Integer click)
    {
        clickAssign = click;
    }
}
