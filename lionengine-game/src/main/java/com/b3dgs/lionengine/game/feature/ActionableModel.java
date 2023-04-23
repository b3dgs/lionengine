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
package com.b3dgs.lionengine.game.feature;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.game.Action;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.game.Cursor;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.geom.Area;
import com.b3dgs.lionengine.geom.Geom;

/**
 * Actionnable model implementation.
 */
public class ActionableModel extends FeatureModel implements Actionable
{
    /** Cursor reference. */
    private final Cursor cursor = services.get(Cursor.class);
    /** Rectangle button area. */
    private final Area button;
    /** Action description. */
    private final String description;
    /** Mouse click number to execute action. */
    private Integer clickAction;
    /** Action used. */
    private Action action;
    /** Enabled flag. */
    private boolean enabled = true;

    /**
     * Create feature.
     * <p>
     * The {@link Services} must provide:
     * </p>
     * <ul>
     * <li>{@link Cursor}</li>
     * </ul>
     * <p>
     * The {@link Configurer} must provide a valid {@link ActionConfig}.
     * </p>
     * <p>
     * If the {@link Featurable} owner is an {@link Action}, it will automatically {@link #setAction(Action)} on it.
     * </p>
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid argument.
     */
    public ActionableModel(Services services, Setup setup)
    {
        super(services, setup);

        final ActionConfig config = ActionConfig.imports(setup);
        button = Geom.createArea(config.getX(), config.getY(), config.getWidth(), config.getHeight());
        description = config.getDescription();
    }

    /*
     * Actionable
     */

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        if (provider instanceof Action)
        {
            setAction((Action) provider);
        }
    }

    @Override
    public void update(double extrp)
    {
        if (enabled && action != null && isOver() && cursor.isPushedOnce(clickAction))
        {
            action.execute();
        }
    }

    @Override
    public void setAction(Action action)
    {
        this.action = action;
    }

    @Override
    public void setClickAction(Integer click)
    {
        clickAction = click;
    }

    @Override
    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    @Override
    public Area getButton()
    {
        return button;
    }

    @Override
    public String getDescription()
    {
        return description;
    }

    @Override
    public boolean isOver()
    {
        return button.contains(cursor.getScreenX(), cursor.getScreenY());
    }

    @Override
    public boolean isEnabled()
    {
        return enabled;
    }
}
