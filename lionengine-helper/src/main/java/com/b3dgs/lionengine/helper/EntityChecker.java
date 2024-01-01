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
package com.b3dgs.lionengine.helper;

import java.util.function.BooleanSupplier;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Listenable;
import com.b3dgs.lionengine.ListenableModel;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.game.feature.FeatureAbstract;
import com.b3dgs.lionengine.game.feature.FeatureInterface;

/**
 * Check for entity update and render enabling.
 */
@FeatureInterface
public class EntityChecker extends FeatureAbstract implements Updatable, Listenable<EntityCheckerListener>
{
    /** Listeners. */
    private final ListenableModel<EntityCheckerListener> listeners = new ListenableModel<>();
    /** Checker update. */
    private BooleanSupplier checkerUpdate = () -> true;
    /** Checker render. */
    private BooleanSupplier checkerRender = () -> true;
    /** Checked update flag. */
    private boolean checkedUpdate = true;
    /** Checked render flag. */
    private boolean checkedRender = true;

    /**
     * Create model.
     */
    public EntityChecker()
    {
        super();
    }

    /**
     * Set checker update.
     * 
     * @param checker The checker v.
     */
    public void setCheckerUpdate(BooleanSupplier checker)
    {
        Check.notNull(checker);

        checkerUpdate = checker;
    }

    /**
     * Set checker render.
     * 
     * @param checker The checker render.
     */
    public void setCheckerRender(BooleanSupplier checker)
    {
        Check.notNull(checker);

        checkerRender = checker;
    }

    @Override
    public void update(double extrp)
    {
        if (checkedUpdate != checkerUpdate.getAsBoolean())
        {
            checkedUpdate = !checkedUpdate;

            final int n = listeners.size();
            for (int i = 0; i < n; i++)
            {
                listeners.get(i).notifyCheckedUpdate(checkedUpdate);
            }
        }
        if (checkedRender != checkerRender.getAsBoolean())
        {
            checkedRender = !checkedRender;

            final int n = listeners.size();
            for (int i = 0; i < n; i++)
            {
                listeners.get(i).notifyCheckedRender(checkedRender);
            }
        }
    }

    @Override
    public void addListener(EntityCheckerListener listener)
    {
        listeners.addListener(listener);
    }

    @Override
    public void removeListener(EntityCheckerListener listener)
    {
        listeners.removeListener(listener);
    }
}
