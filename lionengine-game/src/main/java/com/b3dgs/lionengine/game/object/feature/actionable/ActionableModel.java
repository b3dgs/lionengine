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
package com.b3dgs.lionengine.game.object.feature.actionable;

import com.b3dgs.lionengine.game.Cursor;
import com.b3dgs.lionengine.game.handler.FeatureModel;
import com.b3dgs.lionengine.game.handler.Handlable;
import com.b3dgs.lionengine.game.handler.Services;
import com.b3dgs.lionengine.game.object.ObjectGame;
import com.b3dgs.lionengine.game.object.Setup;
import com.b3dgs.lionengine.geom.Geom;
import com.b3dgs.lionengine.geom.Rectangle;

/**
 * Actionnable model implementation.
 */
public class ActionableModel extends FeatureModel implements Actionable
{
    /** Cursor reference. */
    private Cursor cursor;
    /** Rectangle button area. */
    private final Rectangle button;
    /** Action description. */
    private final String description;
    /** Mouse click number to execute action. */
    private int clickAction;
    /** Action used. */
    private Action action;

    /**
     * Create an actionable model.
     * 
     * <p>
     * The {@link ObjectGame} owner must provide a valid {@link com.b3dgs.lionengine.game.Configurer} compatible
     * with {@link ActionConfig}.
     * </p>
     * <p>
     * The {@link Services} must provide the following services:
     * </p>
     * <ul>
     * <li>{@link Cursor}</li>
     * </ul>
     * <p>
     * If the {@link ObjectGame} owner is an {@link Action}, it will automatically {@link #setAction(Action)} on it.
     * </p>
     * 
     * @param setup The setup reference.
     */
    public ActionableModel(Setup setup)
    {
        super();
        final ActionConfig config = ActionConfig.imports(setup.getConfigurer().getRoot());
        button = Geom.createRectangle(config.getX(), config.getY(), config.getWidth(), config.getHeight());
        description = config.getDescription();
    }

    /*
     * Actionable
     */

    @Override
    public void prepare(Handlable owner, Services services)
    {
        super.prepare(owner, services);

        cursor = services.get(Cursor.class);

        if (owner instanceof Action)
        {
            setAction((Action) owner);
        }
    }

    @Override
    public void update(double extrp)
    {
        if (action != null && isOver() && cursor.hasClickedOnce(clickAction))
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
    public void setClickAction(int click)
    {
        clickAction = click;
    }

    @Override
    public Rectangle getButton()
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
}
